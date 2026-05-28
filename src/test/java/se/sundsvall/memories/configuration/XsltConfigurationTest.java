package se.sundsvall.memories.configuration;

import javax.xml.transform.Templates;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import se.sundsvall.memories.Application;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("junit")
class XsltConfigurationTest {

	@Autowired
	private Templates minnenHtmlTemplates;

	@Test
	void templatesBeanIsLoadedFromClasspath() throws Exception {
		assertThat(minnenHtmlTemplates).isNotNull();
		assertThat(minnenHtmlTemplates.newTransformer()).isNotNull();
	}
}
