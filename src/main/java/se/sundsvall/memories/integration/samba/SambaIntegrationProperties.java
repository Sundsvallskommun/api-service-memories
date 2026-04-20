package se.sundsvall.memories.integration.samba;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("integration.samba")
public record SambaIntegrationProperties(

	@NotBlank String host,
	int port,
	@NotBlank String domain,
	@NotBlank String username,
	@NotBlank String password,
	@NotBlank String share,
	@NotBlank String filmFolder,
	@NotBlank String publFolder,
	@NotBlank String fotoFolder) {
}
