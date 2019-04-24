Schema Validator 1.2.0
----------------------



Instructions
------------

This schema validator will validate an XML file against a given schema (xsd 
file). It can also check an XML for being 'well formed' - that it contains
valid XML.

You can execute it against a single file or against all XML files in a 
folder.


How to Use
----------

To execute from the command line enter,

  java -jar bin\XMLSchemaValidator.jar %xml_file %validation %schema

where the paramters are,

  xml_file     the relative or absolute path to an XML file to validate or to a 
               folder containing XML files you wish to validate.
  validation   set to 'true' if you wish to perform full validation against a 
               schema or false if you wish to perform a well formed check.
  schema       optional, if performing a full validation then must provide 
               either namespace and path to the schema being used to validate 
               the XML file if using namespaces or just path if not


Examples
--------

In some of the examples below the command has been split over 2 or 3 lines to
fit in the page however the commands should always be entered in a single line

If a path contains spaces then use double quotes around the paramter:

  java -jar bin\XMLSchemaValidator.jar "C:\My Documents\My XML File.xml" false


When entering a folder do not include the trailing backslash, for example this
will work,

  java -jar bin\XMLSchemaValidator.jar "C:\My Documents\My XML Folder" false
  
while this won't,

  java -jar bin\XMLSchemaValidator.jar "C:\My Documents\My XML Folder\" false


An example of validating against a schema using namespaces:

  java -jar bin\XMLSchemaValidator.jar "C:\My Documents\My XML File.xml" true 
  "http://www.mywebsite.co.uk/namspace schema/myschema.xsd"

where 'http://www.mywebsite.co.uk/namspace' is the namespace declaration being 
used by the 'myschema.xsd' schema. This schema file is in a folder schema and 
the path is relative to the XML file being validated so its full path is 
"C:\My Documents\schema\myschema.xsd"



 
