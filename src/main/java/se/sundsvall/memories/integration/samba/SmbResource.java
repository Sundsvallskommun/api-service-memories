package se.sundsvall.memories.integration.samba;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;

/**
 * A Spring {@link org.springframework.core.io.Resource} backed by a file on an SMB share. Each call to
 * {@link #getInputStream()} opens a fresh stream, and {@link #contentLength()} goes through the supplied lookup (which
 * the calling {@link SambaIntegration} caches). All SMB error → HTTP Problem mapping lives in {@code SambaIntegration}.
 */
class SmbResource extends AbstractResource {

	private final String filePath;
	private final StreamOpener opener;
	private final LengthSupplier lengthSupplier;

	SmbResource(final String filePath, final StreamOpener opener, final LengthSupplier lengthSupplier) {
		this.filePath = filePath;
		this.opener = opener;
		this.lengthSupplier = lengthSupplier;
	}

	@Override
	public long contentLength() {
		return lengthSupplier.length(filePath);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return opener.open(filePath);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public boolean isReadable() {
		return true;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public String getFilename() {
		final var normalized = filePath.replace('\\', '/');
		final var slash = normalized.lastIndexOf('/');
		return slash >= 0 ? normalized.substring(slash + 1) : normalized;
	}

	@Override
	public String getDescription() {
		return "SMB resource [" + filePath + "]";
	}

	@FunctionalInterface
	interface LengthSupplier {
		long length(String filePath);
	}

	@FunctionalInterface
	interface StreamOpener {
		InputStream open(String filePath) throws IOException;
	}
}
