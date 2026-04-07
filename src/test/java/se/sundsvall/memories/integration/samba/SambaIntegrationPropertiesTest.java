package se.sundsvall.memories.integration.samba;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.memories.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles("junit")
class SambaIntegrationPropertiesTest {

	@Autowired
	private SambaIntegrationProperties properties;

	@Test
	void testProperties() {
		assertThat(properties.host()).isEqualTo("localhost");
		assertThat(properties.port()).isEqualTo(445);
		assertThat(properties.domain()).isEqualTo("WORKGROUP");
		assertThat(properties.username()).isEqualTo("user");
		assertThat(properties.password()).isEqualTo("password");
		assertThat(properties.share()).isEqualTo("/share/");
	}
}
