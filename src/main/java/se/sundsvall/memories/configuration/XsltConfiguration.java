package se.sundsvall.memories.configuration;

import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class XsltConfiguration {

	private static final String XSL_LOCATION = "xsl/minnen_HTML.xsl";

	@Bean
	public Templates minnenHtmlTemplates() throws TransformerConfigurationException, IOException {
		final var factory = TransformerFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

		final var xsl = new ClassPathResource(XSL_LOCATION);
		try (final var input = xsl.getInputStream()) {
			return factory.newTemplates(new StreamSource(input, xsl.getURL().toString()));
		}
	}
}
