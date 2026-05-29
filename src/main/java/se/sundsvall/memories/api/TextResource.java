package se.sundsvall.memories.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.OneOf;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.memories.api.model.PagedTextResponse;
import se.sundsvall.memories.api.model.Text;
import se.sundsvall.memories.api.model.TextParameters;
import se.sundsvall.memories.service.TextService;
import se.sundsvall.memories.service.TextService.FileVariant;
import se.sundsvall.memories.service.TextService.MediaFileVariant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/texts")
@Tag(name = "Texts", description = "Text search and file operations")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class TextResource {

	private final TextService textService;

	TextResource(final TextService textService) {
		this.textService = textService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search texts", description = "Fulltext search across title, comment and OCR text; only published texts are returned. XMLTEXT and mediaFiles are excluded from the list response.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedTextResponse> searchTexts(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final TextParameters parameters) {

		return ok(textService.search(parameters));
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get text by ID", description = "Get a specific text by its ID, including the full XMLTEXT content and any extra media files (TEXT_MULTI)")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<Text> getText(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id) {

		return ok(textService.getById(id));
	}

	@GetMapping(path = "/{id}/file")
	@Operation(summary = "Get text file",
		description = "Get a file associated with a text by specifying the variant (thumbnail, large, text). Returned inline so browsers render it directly; Content-Type is derived from the filename extension for thumbnail/large, and is text/html for the text variant when the source is XML.")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "*/*"))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	void getTextFile(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id,
		@RequestParam @OneOf({
			"thumbnail", "large", "text"
		}) final String variant,
		final HttpServletResponse response) {

		textService.streamFile(id, FileVariant.valueOf(variant.toUpperCase()), response);
	}

	@GetMapping(path = "/{id}/media/{mediaId}/file")
	@Operation(summary = "Get text media file",
		description = "Get an extra media file (TEXT_MULTI) attached to a text by specifying the media file id and the variant (thumbnail, large, original). Returned inline so browsers render it directly; Content-Type is derived from the filename extension.")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "*/*"))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	void getTextMediaFile(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id,
		@PathVariable final Integer mediaId,
		@RequestParam @OneOf({
			"thumbnail", "large", "original"
		}) final String variant,
		final HttpServletResponse response) {

		textService.streamMediaFile(id, mediaId, MediaFileVariant.valueOf(variant.toUpperCase()), response);
	}
}
