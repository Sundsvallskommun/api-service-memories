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
import se.sundsvall.memories.api.model.PagedPublicationResponse;
import se.sundsvall.memories.api.model.Publication;
import se.sundsvall.memories.api.model.PublicationParameters;
import se.sundsvall.memories.service.PublicationService;
import se.sundsvall.memories.service.PublicationService.FileVariant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/publications")
@Tag(name = "Publications", description = "Publication search and file operations")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class PublicationResource {

	private final PublicationService publicationService;

	PublicationResource(final PublicationService publicationService) {
		this.publicationService = publicationService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search publications", description = "Fulltext search across title, comment and OCR text; only published publications are returned. XMLTEXT is excluded from the list response.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedPublicationResponse> searchPublications(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final PublicationParameters parameters) {

		return ok(publicationService.search(parameters));
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get publication by ID", description = "Get a specific publication by its ID, including the full XMLTEXT OCR content")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<Publication> getPublication(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id) {

		return ok(publicationService.getById(id));
	}

	@GetMapping(path = "/{id}/file")
	@Operation(summary = "Get publication file", description = "Download a file associated with a publication by specifying the variant (liten, stor, original, txt, xtra)")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/octet-stream"))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	void getPublicationFile(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id,
		@RequestParam @OneOf({
			"liten", "stor", "original", "txt", "xtra"
		}) final String variant,
		final HttpServletResponse response) {

		publicationService.streamFile(id, FileVariant.valueOf(variant.toUpperCase()), response);
	}
}
