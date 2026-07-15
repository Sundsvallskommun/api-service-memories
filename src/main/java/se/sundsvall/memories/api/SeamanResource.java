package se.sundsvall.memories.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import se.sundsvall.memories.api.model.PagedSeamanResponse;
import se.sundsvall.memories.api.model.Seaman;
import se.sundsvall.memories.api.model.SeamanParameters;
import se.sundsvall.memories.service.SeamanService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/seamen")
@Tag(name = "Seaman", description = "Seaman (sjömanshus register) search and retrieval")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class SeamanResource {

	private final SeamanService seamanService;

	SeamanResource(final SeamanService seamanService) {
		this.seamanService = seamanService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search seamen", description = "Search the seamen's register (SJOMAN) by name, birth parish and birth year range. Returns paginated results.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedSeamanResponse> searchSeamen(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final SeamanParameters parameters) {

		return ok(seamanService.search(parameters));
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get seaman by ID", description = "Get a specific seaman record by its ID")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<Seaman> getSeaman(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id) {

		return ok(seamanService.getById(id));
	}
}
