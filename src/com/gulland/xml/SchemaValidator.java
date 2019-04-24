package com.gulland.xml;

import org.apache.xerces.parsers.SAXParser;
import java.io.File;
import java.io.FileFilter;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Validates XML files against a given schema.
 * 
 * TODO use log4j to capture errors in file
 * 
 * @author AGULLAND
 * 
 */
public class SchemaValidator {

	
	/**
	 * Main execution class. Expecting 2 or 3 arguments
	 * 
	 * FileURL - full path to a folder containing XML files or to an individual
	 * XML file Validation - do full validation (true) or well formed checks
	 * (false) SchemaURL - the location (absolute or relative to FileURL) of
	 * where the schema resides that the XML file is to be validated against.
	 * Only required if Validation = true.
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		// for(int i=0;i<argv.length;i++) System.out.println("arg: <" + argv[i]
		// + ">");

		// validate params
		if (argv.length < 2) {
			System.out
					.println("Missing arguments. Must provide full path to an XML document or a folder containing a set of XML documents and 'true' or 'false' for whether to include schema validation.");
			System.exit(1);
		}

		// get params
		String fileURL = argv[0];
		String validationParam = argv[1];
		String schemaURL = "";
		if (argv.length > 2)
			schemaURL = argv[2];

		// set full validation on or off
		boolean schemaValidationOn = (validationParam.equalsIgnoreCase("true"));

		// if we are validating then check we have a 3rd param
		if ((schemaValidationOn) && (schemaURL == "")) {
			System.out
					.println("If validating (2nd param is 'true') then must provide schema location as 3rd parameter.");
			System.exit(1);
		}

		SchemaValidator validator = new SchemaValidator();

		// check if doc is a file or a directory
		if (new File(fileURL).isDirectory()) {
			validator.parseDirectory(fileURL, schemaURL, schemaValidationOn);
		} else {
			// check single file
			if (schemaValidationOn)
				System.out.println("Validating file '" + fileURL
						+ "'\nAgainst schema '" + schemaURL + ".");
			else
				System.out.println("Checking file '" + fileURL);

			validator.validateSchema(fileURL, schemaURL, schemaValidationOn);
		}
	}

	
	
	/**
	 * Will parse all files in a directory
	 * 
	 * @param XmlDocumentUrl
	 *            Full path to the directory containing XML files
	 * @param SchemaURL
	 *            Full path to schema file
	 * @param schemaValidationOn
	 *            boolean false if we just wish to perform a well formed check
	 */
	private void parseDirectory(String folderUrl, String schemaURL,
			boolean schemaValidationOn) {
		// get only XML files in directory
		File root = new File(folderUrl);
		File files[] = root.listFiles(new XMLFilter());

		// process if we found files
		if (files.length > 0) {
			// output summary info
			System.out.println("Found the following" + files.length
					+ " xml files in folder " + folderUrl);
			for (int i = 0; i < files.length; i++)
				System.out.println("  " + files[i].getName());
			System.out.println();

			if (schemaValidationOn)
				System.out.println("Validation against schema '" + schemaURL
						+ "'");
			else
				System.out.println("Performing well formded checks.");

			// iterate over files
			for (int i = 0; i < files.length; ++i) {
				// add some whitespace
				System.out.println();
				System.out.println("Processing file '" + files[i].getName()
						+ "'");
				validateSchema(files[i].getPath(), schemaURL,
						schemaValidationOn);
			}
		} else {
			System.out.println("No XML Files found");
		}
	}

	/**
	 * This will validate an XML file against a schema. Optionally you can just
	 * check for doc being well formed
	 * 
	 * @param XmlDocumentUrl
	 *            Full path to the XML file
	 * @param SchemaURL
	 *            Full path to schema file
	 * @param schemaValidationOn
	 *            boolean false if we just wish to perform a well formed check
	 */
	public void validateSchema(String documentUrl, String schemaURL,
			boolean schemaValidationOn) {
		SAXParser parser = new SAXParser();

		try {
			parser.setFeature("http://xml.org/sax/features/validation",
					schemaValidationOn);
			parser.setFeature(
					"http://apache.org/xml/features/validation/schema",
					schemaValidationOn);
			parser
					.setFeature(
							"http://apache.org/xml/features/validation/schema-full-checking",
							schemaValidationOn);
			if (schemaValidationOn)
				parser
						.setProperty(
								"http://apache.org/xml/properties/schema/external-schemaLocation",
								schemaURL);

			Validator handler = new Validator();
			parser.setErrorHandler(handler);
			parser.parse(documentUrl);

			if (!handler.validationError) {
				if (schemaValidationOn)
					System.out.println("Your document is valid!");
				else
					System.out.println("Your document is well formed!");
			}
		} catch (java.io.IOException ioe) {
			System.out.println("Error message: " + ioe.getMessage());
		} catch (SAXException e) {
			// System.out.println("Error message: " + e.getMessage() );
		}
	}

	private class Validator extends DefaultHandler {
		public boolean validationError = false;
		private int errorCounter = 0;

		public void error(SAXParseException exception) throws SAXException {
			validationError = true;
			errorCounter++;
			System.out
					.println("Error " + errorCounter + " at or near line "
							+ exception.getLineNumber() + ": "
							+ exception.getMessage());
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			validationError = true;
			errorCounter++;
			System.out.println("Fatal error " + errorCounter
					+ " at or near line " + exception.getLineNumber() + ": "
					+ exception.getMessage());
		}

		public void warning(SAXParseException exception) throws SAXException {
		}

		public void resetErrors() {
			errorCounter = 0;
		}
	}


	/**
	 * This class is used to provide XML filtering
	 * 
	 * @author AGULLAND
	 */
	private class XMLFilter implements FileFilter {
		public boolean accept(File checkThisFile) {
			String filename = checkThisFile.getName().toLowerCase();
			return filename.endsWith("xml");
		}
	}

}
