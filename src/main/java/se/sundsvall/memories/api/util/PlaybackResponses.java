package se.sundsvall.memories.api.util;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.util.StreamUtils;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.service.model.StreamPayload;

import static org.springframework.http.HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE;

/**
 * Shared helper for the {@code /stream} endpoints on Audio and Film. Writes the response directly to the
 * {@link HttpServletResponse}, producing a {@code 200 OK} full-body response for plain requests and a
 * {@code 206 Partial Content} response for a single-range request. Malformed or unsatisfiable Range headers yield a
 * 416 Problem.
 *
 * Multi-range requests fall back to a {@code 200 OK} full-body response (acceptable per RFC 7233 § 4.1 — clients that
 * actually need multipart responses will fall back to sequential single-range requests).
 *
 * We write directly to the servlet response rather than returning {@code ResponseEntity<ResourceRegion>} because
 * {@code ResourceRegionHttpMessageConverter}'s {@code canWrite(Type, ...)} check fails when the declared response
 * entity type is a wildcard / {@code Object}, which produces a silent 500 on the range path.
 */
public final class PlaybackResponses {

	private PlaybackResponses() {}

	public static void write(final StreamPayload payload, final String rangeHeader, final HttpServletResponse response) throws IOException {
		final var length = payload.resource().contentLength();

		if (rangeHeader == null || rangeHeader.isBlank()) {
			writeFull(payload, response, length);
			return;
		}

		final var ranges = parseRanges(rangeHeader);
		if (ranges.size() != 1) {
			writeFull(payload, response, length);
			return;
		}

		if (isUnsatisfiable(rangeHeader, length)) {
			response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes */" + length);
			throw Problem.valueOf(REQUESTED_RANGE_NOT_SATISFIABLE, "Range not satisfiable: " + rangeHeader);
		}

		final var range = ranges.getFirst();
		final var start = range.getRangeStart(length);
		final var end = range.getRangeEnd(length);

		setCommonHeaders(response, payload);
		response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
		response.setHeader(HttpHeaders.CONTENT_RANGE, "bytes " + start + "-" + end + "/" + length);
		response.setContentLengthLong(end - start + 1);

		try (final InputStream in = payload.resource().getInputStream()) {
			StreamUtils.copyRange(in, response.getOutputStream(), start, end);
		}
	}

	private static void writeFull(final StreamPayload payload, final HttpServletResponse response, final long length) throws IOException {
		setCommonHeaders(response, payload);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentLengthLong(length);
		try (final InputStream in = payload.resource().getInputStream()) {
			in.transferTo(response.getOutputStream());
		}
	}

	private static void setCommonHeaders(final HttpServletResponse response, final StreamPayload payload) {
		response.setHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
		response.setContentType(payload.mimeType());
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
			ContentDisposition.inline().filename(payload.filename()).build().toString());
	}

	private static List<HttpRange> parseRanges(final String rangeHeader) {
		try {
			return HttpRange.parseRanges(rangeHeader);
		} catch (final IllegalArgumentException _) {
			throw Problem.valueOf(REQUESTED_RANGE_NOT_SATISFIABLE, "Invalid Range header: " + rangeHeader);
		}
	}

	/**
	 * Spring's {@link HttpRange#getRangeStart(long)} clamps first-byte-pos to {@code length - 1} rather than reporting
	 * unsatisfiable, so {@code bytes=999-9999} on a 10-byte file silently becomes byte 9. RFC 7233 says a range whose
	 * first-byte-pos exceeds the selected representation's length is unsatisfiable and must get 416; we check the raw
	 * header here to preserve that semantic. Called only for single-range requests.
	 */
	private static boolean isUnsatisfiable(final String rangeHeader, final long length) {
		final var eq = rangeHeader.indexOf('=');
		if (eq < 0) {
			return true;
		}
		final var spec = rangeHeader.substring(eq + 1).trim();
		final var dash = spec.indexOf('-');
		if (dash <= 0) {
			// suffix form "bytes=-N" — unsatisfiable only if N is 0 or not a number
			try {
				return Long.parseLong(spec.substring(1)) == 0;
			} catch (final NumberFormatException _) {
				return true;
			}
		}
		try {
			return Long.parseLong(spec.substring(0, dash)) >= length;
		} catch (final NumberFormatException _) {
			return true;
		}
	}
}
