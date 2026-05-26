package se.sundsvall.memories.service;

import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerException;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import se.sundsvall.dept44.problem.Problem;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Component
public class XsltTransformer {

	private final Templates templates;

	public XsltTransformer(final Templates minnenHtmlTemplates) {
		this.templates = minnenHtmlTemplates;
	}

	public void transform(final InputStream xml, final OutputStream out) {
		try {
			final var transformer = templates.newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.METHOD, "html");
			transformer.transform(new SAXSource(hardenedXmlReader(), new InputSource(xml)), new StreamResult(out));
		} catch (final TransformerException | SAXException | ParserConfigurationException e) {
			throw Problem.valueOf(INTERNAL_SERVER_ERROR, "Failed to transform XML: %s".formatted(e.getMessage()));
		}
	}

	private static XMLReader hardenedXmlReader() throws SAXException, ParserConfigurationException {
		final var parserFactory = SAXParserFactory.newInstance();
		parserFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		parserFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		parserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		parserFactory.setNamespaceAware(true);
		return parserFactory.newSAXParser().getXMLReader();
	}
}
