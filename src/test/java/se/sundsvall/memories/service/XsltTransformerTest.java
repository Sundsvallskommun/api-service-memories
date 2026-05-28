package se.sundsvall.memories.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.sundsvall.dept44.problem.ThrowableProblem;

import static javax.xml.XMLConstants.ACCESS_EXTERNAL_DTD;
import static javax.xml.XMLConstants.ACCESS_EXTERNAL_STYLESHEET;
import static javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

class XsltTransformerTest {

	private static final String IDENTITY_XSL = """
		<?xml version="1.0"?>
		<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
		  <xsl:output method="html" encoding="UTF-8"/>
		  <xsl:template match="/Document">
		    <html><body><h1><xsl:value-of select="Title"/></h1></body></html>
		  </xsl:template>
		</xsl:stylesheet>
		""";

	private XsltTransformer transformer;

	@BeforeEach
	void setUp() throws Exception {
		final var factory = TransformerFactory.newInstance();
		factory.setFeature(FEATURE_SECURE_PROCESSING, true);
		factory.setAttribute(ACCESS_EXTERNAL_DTD, "");
		factory.setAttribute(ACCESS_EXTERNAL_STYLESHEET, "");
		final var templates = factory.newTemplates(new StreamSource(new ByteArrayInputStream(IDENTITY_XSL.getBytes())));
		transformer = new XsltTransformer(templates);
	}

	@Test
	void transformProducesExpectedHtml() {
		final var xml = "<Document><Title>Hello</Title></Document>".getBytes();
		final var out = new ByteArrayOutputStream();

		transformer.transform(new ByteArrayInputStream(xml), out);

		final var result = out.toString();
		assertThat(result).contains("<h1>Hello</h1>");
	}

	@Test
	void transformRejectsExternalEntities() {
		final var xxe = ("""
			<?xml version="1.0"?>
			<!DOCTYPE Document [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
			<Document><Title>&xxe;</Title></Document>
			""").getBytes();
		final var out = new ByteArrayOutputStream();

		assertThatThrownBy(() -> transformer.transform(new ByteArrayInputStream(xxe), out))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", INTERNAL_SERVER_ERROR);
	}

	@Test
	void transformWrapsMalformedXml() {
		final var bad = "<not-closed".getBytes();
		final var out = new ByteArrayOutputStream();

		assertThatThrownBy(() -> transformer.transform(new ByteArrayInputStream(bad), out))
			.isInstanceOf(ThrowableProblem.class)
			.hasFieldOrPropertyWithValue("status", INTERNAL_SERVER_ERROR);
	}
}
