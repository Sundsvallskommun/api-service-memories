package se.sundsvall.memories.integration.samba;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@CircuitBreaker(name = "sambaIntegration")
@EnableConfigurationProperties(SambaIntegrationProperties.class)
public class SambaIntegration {

	private static final Logger LOGGER = LoggerFactory.getLogger(SambaIntegration.class);

	private static final Duration LENGTH_CACHE_TTL = Duration.ofSeconds(60);

	private final CIFSContext context;
	private final String shareUrl;
	private final ConcurrentMap<String, CachedLength> lengthCache = new ConcurrentHashMap<>();

	public SambaIntegration(final SambaIntegrationProperties properties) {
		context = SingletonContext.getInstance()
			.withCredentials(new NtlmPasswordAuthenticator(properties.domain(), properties.username(), properties.password()));

		shareUrl = "smb://%s:%d%s".formatted(properties.host(), properties.port(), properties.share());
	}

	public void streamFile(final String filePath, final OutputStream outputStream) {
		final var fullPath = fullPath(filePath);
		LOGGER.info("Streaming file from SMB share: {}", fullPath);

		try (final var file = new SmbFile(fullPath, context);
			final var inputStream = new SmbFileInputStream(file)) {

			inputStream.transferTo(outputStream);
		} catch (final IOException e) {
			throw mapSmbError(e, filePath, fullPath);
		}
	}

	/**
	 * Opens a {@link Resource} view of an SMB-backed file, suitable for use with Spring's {@code ResourceRegion} /
	 * {@code ResourceHttpMessageConverter} for Range-aware streaming. Content length is cached briefly
	 * ({@value #LENGTH_CACHE_TTL}) so repeated Range requests from a single playback session don't each incur a metadata
	 * round-trip.
	 *
	 * @param  filePath the path relative to the SMB share (e.g. {@code "/ljud/interview.mp3"})
	 * @return          a {@link Resource} whose {@code getInputStream()} opens a fresh {@code SmbFileInputStream} per
	 *                  call
	 */
	public Resource openResource(final String filePath) {
		return new SmbResource(filePath, this::openInputStream, this::resolveLength);
	}

	InputStream openInputStream(final String filePath) {
		final var fullPath = fullPath(filePath);
		LOGGER.info("Streaming file from SMB share: {}", fullPath);
		try {
			final var file = new SmbFile(fullPath, context);
			return new SmbFileInputStream(file);
		} catch (final IOException e) {
			throw mapSmbError(e, filePath, fullPath);
		}
	}

	/**
	 * Lists the entries of a directory on the share. Diagnostic helper for the (disabled-by-default) samba debug
	 * endpoint. Directory entries come back with a trailing {@code /} (jcifs convention); a trailing {@code /} is added
	 * to {@code dirPath} if missing so jcifs treats it as a directory. Sorted for stable output.
	 *
	 * @param  dirPath the directory path relative to the share (e.g. {@code "/MINNEN/MEDIA/TEXT/fil_stor/"})
	 * @return         sorted entry names, never null
	 */
	public List<String> list(final String dirPath) {
		final var normalized = dirPath.endsWith("/") ? dirPath : dirPath + "/";
		final var fullPath = fullPath(normalized);
		LOGGER.info("Listing SMB directory: {}", fullPath);
		try (final var dir = new SmbFile(fullPath, context)) {
			final var names = dir.list();
			return names == null ? List.of() : Arrays.stream(names).sorted().toList();
		} catch (final IOException e) {
			throw mapSmbError(e, normalized, fullPath);
		}
	}

	long resolveLength(final String filePath) {
		final var cached = lengthCache.get(filePath);
		if (cached != null && cached.expiresAtNanos > System.nanoTime()) {
			return cached.length;
		}
		final var fullPath = fullPath(filePath);
		try (final var file = new SmbFile(fullPath, context)) {
			final var length = file.length();
			lengthCache.put(filePath, new CachedLength(length, System.nanoTime() + LENGTH_CACHE_TTL.toNanos()));
			return length;
		} catch (final IOException e) {
			throw mapSmbError(e, filePath, fullPath);
		}
	}

	private String fullPath(final String filePath) {
		return shareUrl + filePath;
	}

	private RuntimeException mapSmbError(final IOException e, final String filePath, final String fullPath) {
		if (e.getMessage() != null && e.getMessage().contains("The system cannot find the file specified")) {
			LOGGER.error("File not found at path: {}", fullPath);
			return Problem.valueOf(NOT_FOUND, "File not found at path '%s'".formatted(filePath));
		}
		LOGGER.error("Failed to read file from SMB share at path: {}", fullPath, e);
		return Problem.valueOf(INTERNAL_SERVER_ERROR, "Failed to read file from SMB share");
	}

	private record CachedLength(long length, long expiresAtNanos) {}
}
