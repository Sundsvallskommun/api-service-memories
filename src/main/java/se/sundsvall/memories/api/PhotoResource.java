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
import se.sundsvall.memories.api.model.PagedPhotoResponse;
import se.sundsvall.memories.api.model.Photo;
import se.sundsvall.memories.api.model.PhotoParameters;
import se.sundsvall.memories.service.PhotoService;
import se.sundsvall.memories.service.PhotoService.FileVariant;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/photos")
@Tag(name = "Photos", description = "Photo search and file operations")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class PhotoResource {

	private final PhotoService photoService;

	PhotoResource(final PhotoService photoService) {
		this.photoService = photoService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search photos", description = "Fulltext search across title and comment; only published photos are returned.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedPhotoResponse> searchPhotos(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final PhotoParameters parameters) {

		return ok(photoService.search(parameters));
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get photo by ID", description = "Get a specific photo by its ID")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<Photo> getPhoto(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id) {

		return ok(photoService.getById(id));
	}

	@GetMapping(path = "/{id}/file")
	@Operation(summary = "Get photo file",
		description = "Get a file associated with a photo by specifying the variant (thumbnail = thumbnail preview, large = large image). Returned inline so browsers render it directly; Content-Type is derived from the filename extension.")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "*/*"))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	void getPhotoFile(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id,
		@RequestParam @OneOf({
			"thumbnail", "large"
		}) final String variant,
		final HttpServletResponse response) {

		photoService.streamFile(id, FileVariant.valueOf(variant.toUpperCase()), response);
	}
}
