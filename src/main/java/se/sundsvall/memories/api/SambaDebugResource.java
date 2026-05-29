package se.sundsvall.memories.api;

import io.swagger.v3.oas.annotations.Hidden;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se.sundsvall.dept44.problem.Problem;
import se.sundsvall.memories.integration.samba.SambaIntegration;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.ok;

/**
 * TEMPORARY diagnostic endpoint for browsing the SMB share while investigating missing files (e.g. confirming whether
 * the TEXT image folders are populated). Read-only directory listing.
 * <p>
 * Disabled unless {@code debug.samba.enabled=true} (env {@code DEBUG_SAMBA_ENABLED}); because the bean is then absent,
 * it does not appear in the generated OpenAPI specification. Enable it on a non-public deployment, inspect, then turn
 * it
 * back off. {@link Hidden} keeps it out of the API docs even when enabled.
 */
@RestController
@Hidden
@ConditionalOnProperty(prefix = "debug.samba", name = "enabled", havingValue = "true")
class SambaDebugResource {

	private final SambaIntegration sambaIntegration;

	SambaDebugResource(final SambaIntegration sambaIntegration) {
		this.sambaIntegration = sambaIntegration;
	}

	@GetMapping(path = "/admin/samba", produces = APPLICATION_JSON_VALUE)
	ResponseEntity<List<String>> list(@RequestParam final String path) {
		if (path.contains("..")) {
			throw Problem.valueOf(BAD_REQUEST, "Path must not contain '..'");
		}
		return ok(sambaIntegration.list(path));
	}
}
