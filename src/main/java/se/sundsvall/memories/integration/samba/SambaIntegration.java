package se.sundsvall.memories.integration.samba;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import java.io.IOException;
import java.io.OutputStream;
import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthenticator;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Component
@CircuitBreaker(name = "sambaIntegration")
@EnableConfigurationProperties(SambaIntegrationProperties.class)
public class SambaIntegration {

	private static final Logger LOGGER = LoggerFactory.getLogger(SambaIntegration.class);

	private final CIFSContext context;
	private final String shareUrl;

	public SambaIntegration(final SambaIntegrationProperties properties) {
		context = SingletonContext.getInstance()
			.withCredentials(new NtlmPasswordAuthenticator(properties.domain(), properties.username(), properties.password()));

		shareUrl = "smb://%s:%d%s".formatted(properties.host(), properties.port(), properties.share());
	}

	public void streamFile(final String filePath, final OutputStream outputStream) {
		final var fullPath = shareUrl + filePath;
		LOGGER.info("Streaming file from SMB share: {}", fullPath);

		try (final var file = new SmbFile(fullPath, context);
			final var inputStream = new SmbFileInputStream(file)) {

			inputStream.transferTo(outputStream);
		} catch (final IOException e) {
			if (e.getMessage() != null && e.getMessage().contains("The system cannot find the file specified")) {
				LOGGER.error("File not found at path: {}", fullPath);
				throw Problem.valueOf(NOT_FOUND, "File not found at path '%s'".formatted(filePath));
			}
			LOGGER.error("Failed to read file from SMB share at path: {}", fullPath, e);
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "Failed to read file from SMB share");
		}
	}
}
