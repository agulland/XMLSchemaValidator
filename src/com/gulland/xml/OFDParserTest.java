package com.gulland.xml;

import java.io.File;
import java.io.IOException;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

/**
 * for processing OFD files
 * 
 */
public class OFDParserTest
{
	/** name of namespace used in fields.xsd */
	private static String NAMESPACE = "http://www.gulland.com/software/srdg/1.0/ofd";

	public static void main(String[] argv) {
		OFDParserTest pt = new OFDParserTest();
		pt.parse();
	}

	public void parse() {

		// define file
		String xmlFile = "test/ofd_test.xml";
		String schemaFile = "ofd.xsd";

		String xmlSchemaUrl = NAMESPACE;
		String schemaFolder = System.getProperty("user.dir") + File.separator + "test" + File.separator;
		File f = new File(schemaFolder + schemaFile);
		xmlSchemaUrl += " " + f.toURI();
		System.out.println("Processing xml file: " + xmlSchemaUrl);

		// define parser
		DOMParser parser = new DOMParser();

		try {
			// For info on features see
			// http://xerces.apache.org/xerces-j/features.html

			/*
			 * If this feature is set to true, the document must specify a grammar. If
			 * this feature is set to false, the document may specify a grammar and
			 * that grammar will be parsed but no validation of the document contents
			 * will be performed.
			 */

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
			 * True: Continue after fatal error.
			 * 
			 * False: Stops parse on first fatal error.
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
		} catch (SAXNotRecognizedException e1) {
			e1.printStackTrace();
		} catch (SAXNotSupportedException e1) {
			e1.printStackTrace();
		}

		// ok now parse the doc

		try {
			Handler handler = new Handler();
			parser.setErrorHandler(handler);

			parser.parse(xmlFile);
			Document doc = parser.getDocument();

			if (!handler.validationFail) {

				NodeList nl = doc.getElementsByTagNameNS(NAMESPACE, "file");
				if (nl.getLength() == 0) System.out.println("No file entries found");
				for (int i = 0; i < nl.getLength(); i++) {
					Node fileNode = nl.item(i);

					String path = getChildNodeValueNS(NAMESPACE, fileNode, "path");
					System.out.println("Found file with output path: '" + path + "'");

					String rows = getChildNodeValueNS(NAMESPACE, fileNode, "rows");
					System.out.println("File attribute rows: " + rows);

					String type = getChildNodeValueNS(NAMESPACE, fileNode, "type");
					System.out.println("File attribute type: " + type);

					String delimiter = getChildNodeValueNS(NAMESPACE, fileNode, "delimiter");
					System.out.println("File attribute delimiter: " + delimiter);

					String headers = getChildNodeValueNS(NAMESPACE, fileNode, "headers");
					System.out.println("File attribute headers: " + headers);

					String quoteFields = getChildNodeValueNS(NAMESPACE, fileNode, "quoteFields");
					System.out.println("File attribute quoteFields: " + quoteFields);

					// process columns
					NodeList nlParams = fileNode.getChildNodes();
					for (int j = 0; j < nlParams.getLength(); j++) {
						Node columnNode = nlParams.item(j);
						if ((columnNode != null) && (columnNode.getNodeName().equals("column"))) {
							System.out.println("Column:");
							String colName = getChildNodeValueNS(NAMESPACE, columnNode, "name");
							System.out.println("  name:" + colName);

							String format = getChildNodeValueNS(NAMESPACE, columnNode, "format");
							System.out.println("  format:" + format);

							String width = getChildNodeValueNS(NAMESPACE, columnNode, "width");
							System.out.println("  width:" + width);

							// get field tag
							Node fieldNode = getFieldNode(columnNode);							
							String fieldName = getAttributeValue(fieldNode, "fieldname");
							System.out.println("  Field: " + fieldName);

							// get all parameters
							NodeList nlParameters = fieldNode.getChildNodes();
							for (int l = 0; l < nlParameters.getLength(); l++) {
								Node parameterNode = nlParameters.item(l);
								if ((parameterNode != null) && (parameterNode.getNodeName().equals("parameter"))) {
									String paramName = getAttributeValue(parameterNode, "parameterName");
									String paramValue = getAttributeValue(parameterNode, "value");
									System.out.println("    Parameter (" + paramName + "," + paramValue + ")");
								}
							} // param loop

						}
					} // column node loop
				} // file loop
				
			} // validation fail check

		} catch (IOException e) {
			System.err.println(e);
		} catch (SAXException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Return the value of a specified attribute from a specified node
	 * 
	 * @param node
	 * @param attributeName
	 * @return
	 */
	private String getAttributeValue(Node node, String attributeName) {
		String val = null;

		NamedNodeMap attribs = node.getAttributes();
		Node n = attribs.getNamedItemNS(NAMESPACE, attributeName);
		if (n != null) val = n.getFirstChild().getNodeValue();
		return val;
	}

	/**
	 * Returns the value of a specified child node for a specified node
	 * 
	 * @param node
	 * @param childNodeName
	 * @return
	 */
	private String getChildNodeValueNS(String namespace, Node node, String childNodeName) {
		String val = null;

		String prefix = node.lookupPrefix(namespace);

		NodeList nl = node.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if ((n.getNodeName().equals(prefix + ":" + childNodeName))) val = n.getFirstChild().getNodeValue();
		}

		return val;
	}

	/**
	 * Returns a field node for a specific column node or null if node not found
	 * 
	 * @param columnNode
	 * @return Node a valid field node
	 */
	public Node getFieldNode(Node columnNode) {
		Node fieldNode = null;

		NodeList nlFields = columnNode.getChildNodes();
		for (int k = 0; k < nlFields.getLength(); k++) {
			Node n = nlFields.item(k);
			if ((n != null) && (n.getNodeName().equals("field"))) fieldNode = n;
		}
		return fieldNode;
	}

	class Handler implements ErrorHandler
	{
		public boolean validationFail = false;

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
			validationFail = true;
			System.out.println(type + " error at or near line " + e.getLineNumber() + ": "
					+ e.getMessage());
		}
	}

}