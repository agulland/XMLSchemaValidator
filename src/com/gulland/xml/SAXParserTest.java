package com.gulland.xml;

import java.io.File;
import java.io.IOException;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Demo reading an XML file using DOM parser
 * 
 * @author agulland
 * 
 */
public class SAXParserTest extends DefaultHandler
{
	/** Default parser name. */
	protected static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";

	private static String NAMESPACE = "http://www.gulland.com";

	private String thisElement;
	private String thisValue;
	private String paramName;
	private String datatype;
	

	/**
	 * event for start of Element
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		thisElement = localName;
		if(localName.equals("parameter")) {
			paramName = attributes.getValue(uri,"parameterName");
		  datatype = attributes.getValue(uri,"datatype");
		}
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		System.out.println("Element '" + thisElement + "' " + thisValue);
		if(thisElement.equals("parameter")) {
			System.out.println(paramName + ", " + datatype);
		}
	}

	public void characters(char ch[], int start, int length) throws SAXException {
		thisValue = new String(ch, start, length);
	}

	@Override
	public void warning(SAXParseException e) {
		doError(e, "[Warning]");
	}

	@Override
	public void error(SAXParseException e) {
		doError(e, "[Error]");
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		doError(e, "[Fatal]");
	}

	private void doError(SAXParseException e, String type) {
		System.out.println(type + " error at or near line " + e.getLineNumber() + ": " + e.getMessage());
	}

	/**
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {

		SAXParserTest spt = new SAXParserTest();

		String xmlFile = "test/fields.xml";
		String xmlSchemaUrl = NAMESPACE;

		String schemaFolder = System.getProperty("user.dir") + File.separator + "test" + File.separator;
		String schemaFile = "fields.xsd";
		File f = new File(schemaFolder + schemaFile);
		xmlSchemaUrl += " " + f.toURI();

		/** this has to be relative to xml file */
		System.out.println(xmlSchemaUrl);

		XMLReader parser = null;

		// create parser
		try {
			parser = XMLReaderFactory.createXMLReader(DEFAULT_PARSER_NAME);
		} catch (Exception e) {
			System.err.println("error: Unable to instantiate parser (" + DEFAULT_PARSER_NAME + ")");
		}

		// For info on features see
		// http://xerces.apache.org/xerces-j/features.html

		/*
		 * If this feature is set to true, the document must specify a grammar. If
		 * this feature is set to false, the document may specify a grammar and that
		 * grammar will be parsed but no validation of the document contents will be
		 * performed.
		 */
		try {
			parser.setFeature("http://xml.org/sax/features/validation", true);
			/* Turn on XML Schema support */
			parser.setFeature("http://apache.org/xml/features/validation/schema", true);

			/*
			 * Enable full schema constraint checking, including checking which may be
			 * time-consuming or memory intensive. Currently, particle unique
			 * attribution constraint checking and particle derivation restriction
			 * checking are controlled by this option.
			 */
			parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

			/*
			 * True: Continue after fatal error. False: Stops parse on first fatal
			 * error.
			 */
			parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", true);

			/*
			 * Perform namespace processing: prefixes will be stripped off element and
			 * attribute names and replaced with the corresponding namespace URIs. By
			 * default, the two will simply be concatenated, but the namespace-sep
			 * core property allows the application to specify a delimiter string for
			 * separating the URI part and the local part.
			 * 
			 * NEEDS TO BE TRUE
			 */
			parser.setFeature("http://xml.org/sax/features/namespaces", true);

			/*
			 * This property allows the user to specify a list of schemas to use. If
			 * the targetNamespace of a schema (specified using this property) matches
			 * the targetNamespace of a schema occurring in the instance document in
			 * schemaLocation attribute, the schema specified by the user using this
			 * property will be used (i.e., the instance document's schemaLocation
			 * attribute will be effectively ignored).
			 * 
			 * Note: The syntax is the same as for schemaLocation attributes in
			 * instance documents: e.g, "http://www.example.com file_name.xsd". The
			 * user can specify more than one XML Schema in the list.
			 */
			parser.setProperty("http://apache.org/xml/properties/schema/external-schemaLocation",
					xmlSchemaUrl);

			// parse file
			parser.setContentHandler(spt);
			parser.setErrorHandler(spt);

			parser.parse(xmlFile);

		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}

}
