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
import se.sundsvall.memories.api.model.NodeParameters;
import se.sundsvall.memories.api.model.PagedNodeResponse;
import se.sundsvall.memories.service.NodeService;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/nodes")
@Tag(name = "Node", description = "Archive and collection (arkiv och samlingar) node search")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class NodeResource {

	private final NodeService nodeService;

	NodeResource(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "Search archive and collection nodes", description = "Search the archive tree (arkiv, serier, volymer, samlingar) by free text, node type and period. Returns paginated results.")
	@ApiResponse(responseCode = "200", description = "Successful operation")
	ResponseEntity<PagedNodeResponse> searchNodes(
		@PathVariable @ValidMunicipalityId final String municipalityId,
		@Valid final NodeParameters parameters) {

		return ok(nodeService.search(parameters));
	}
}
