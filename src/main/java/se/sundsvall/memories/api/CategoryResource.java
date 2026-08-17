package se.sundsvall.memories.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.common.validators.annotation.ValidMunicipalityId;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.dept44.problem.violations.ConstraintViolationProblem;
import se.sundsvall.memories.api.model.Category;
import se.sundsvall.memories.service.CategoryLookup;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@Validated
@RequestMapping("/{municipalityId}/categories")
@Tag(name = "Category", description = "Category (verksamhetskategori) lookup for search form dropdowns")
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "Bad request", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(oneOf = {
		Problem.class, ConstraintViolationProblem.class
	}))),
	@ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = Problem.class)))
})
class CategoryResource {

	private final CategoryLookup categoryLookup;

	CategoryResource(final CategoryLookup categoryLookup) {
		this.categoryLookup = categoryLookup;
	}

	@GetMapping(produces = APPLICATION_JSON_VALUE)
	@Operation(summary = "List categories", description = "List all verksamhetskategorier (sorted by name) for search form dropdowns.")
	@ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Category.class))))
	ResponseEntity<List<Category>> getCategories(
		@PathVariable @ValidMunicipalityId final String municipalityId) {

		return ok(categoryLookup.getAllCategories());
	}
}
