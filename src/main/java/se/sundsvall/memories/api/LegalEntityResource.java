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
import se.sundsvall.memories.api.model.LegalEntity;
import se.sundsvall.memories.api.model.LegalEntityParameters;
import se.sundsvall.memories.api.model.PagedLegalEntityResponse;
import se.sundsvall.memories.service.LegalEntityService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/legal-entities")
@Tag(name = "Legal entity", description = "Legal entity (juridisk person) search and retrieval")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class LegalEntityResource {

	private final LegalEntityService legalEntityService;

	LegalEntityResource(final LegalEntityService legalEntityService) {
		this.legalEntityService = legalEntityService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search legal entities", description = "Search legal entities (JURPERS) by name, location, category and activity-period year range. Returns paginated results.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedLegalEntityResponse> searchLegalEntities(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final LegalEntityParameters parameters) {

		return ok(legalEntityService.search(parameters));
	}

	@GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Get legal entity by ID", description = "Get a specific legal entity record by its ID")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	@ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
	ResponseEntity<LegalEntity> getLegalEntity(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@PathVariable final Integer id) {

		return ok(legalEntityService.getById(id));
	}
}
