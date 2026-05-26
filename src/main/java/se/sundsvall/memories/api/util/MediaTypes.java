package se.sundsvall.memories.api.util;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

public final class MediaTypes {

	private MediaTypes() {}

	public static MediaType resolve(final String filename) {
		return MediaTypeFactory.getMediaType(filename).orElse(MediaType.APPLICATION_OCTET_STREAM);
	}
}
