package se.sundsvall.memories.service.model;

import org.springframework.core.io.Resource;

/**
 * Bundle of everything a controller needs to serve an SMB-backed file for inline playback: the
 * {@link Resource} (length + fresh-stream-per-call), the resolved MIME type, and the filename used for
 * {@code Content-Disposition}.
 */
public record StreamPayload(Resource resource, String mimeType, String filename) {
}
