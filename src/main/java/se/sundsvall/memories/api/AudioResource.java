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
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.memories.api.model.Audio;
import se.sundsvall.memories.api.model.AudioParameters;
import se.sundsvall.memories.api.model.PagedAudioResponse;
import se.sundsvall.memories.service.AudioService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/audios")
@Tag(name = "Audio", description = "Audio search and file operations")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class AudioResource {

	private final AudioService audioService;

	AudioResource(final AudioService audioService) {
		this.audioService = audioService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search audio", description = "Search audio by free text query across title and comment. Returns paginated results.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedAudioResponse> searchAudios(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final AudioParameters parameters) {

		return ok(audioService.search(parameters));
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get audio by ID", description = "Get a specific audio record by its ID")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<Audio> getAudio(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id) {

		return ok(audioService.getById(id));
	}

	@GetMapping(path = "/{id}/file")
	@Operation(summary = "Get audio file", description = "Download the file associated with an audio record")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/octet-stream"))
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	void getAudioFile(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id,
		final HttpServletResponse response) {

		audioService.streamFile(id, response);
	}
}
