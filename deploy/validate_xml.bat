echo off
REM ---------------------------------------------------------------------
REM validate_xml.bat
REM
REM This program launches the java application that validates an D37 XML file.
REM
REM The script is executed,
REM
REM  validate_xml.bat <file> <validation>
REM
REM where
REM
REM  <file>       absolute or relative path to the XML file or folder
REM               you want to validate
REM  <validation> is either 'true' or 'false' for whether you want to
REM               perform full validation (true) or just check that
REM               the XML doc is well formed (false)
REM
REM Examples
REM
REM  To validate the file 'xml_file.xml' in the current folder
REM
REM   validate_xml xml_file.xml true
REM
REM To validate all files in the "C:\My Documents" folder
REM
REM   validate_xml "C:\My Documents" true
REM
REM  To validate all files in the subfolder 'xml_files' of the current folder,
REM
REM     validate_xml xml_files true
REM
REM  To validate all xml files found in current folder
REM
REM     validate_xml ./ true
REM
REM
REM ---------------------------------------------------------------------
REM Amendment History
REM
REM Date         Author           Description
REM --------     ---------------  -------------------------------------
REM 17 Jan 2008  A Gulland        Initial version
REM 27 Feb 2008  A Gulland        More instructions, hard code schema
REM ---------------------------------------------------------------------

REM check first param
set xml_file=%1

REM namespace and schema location
set schemaURL=%3


REM run app
echo %xml_file%
java -jar bin\XMLSchemaValidator.jar %xml_file% %2 %schemaURL%
