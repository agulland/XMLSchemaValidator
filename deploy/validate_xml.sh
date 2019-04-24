#!/bin/bash
#---------------------------------------------------------------------#
# validate_xml.sh
#
# This program launches the java application that validates an D37 XML file.
#
# The script is executed,
#
#  ./validate_xml.sh <file> <validation>
#
#  <file>       absolute or relative path to the XML file or folder
#               you want to validate
#  <validation> is either 'true' or 'false' for whether you want to
#               perform full validation (true) or just check that
#               the XML doc is well formed (false)
#
# Examples
#
#  To validate the file 'xml_file.xml' in the current folder
#
#   validate_xml xml_file.xml true
#
# To validate all files in the "C:\My Documents" folder
#
#   validate_xml "C:\My Documents" true
#
#  To validate all files in the subfolder 'xml_files' of the current folder,
#
#     validate_xml xml_files true
#
#  To validate all xml files found in current folder
#
#     validate_xml ./ true
#
#
# ---------------------------------------------------------------------
# Amendment History
#
# Date         Author           Description
# --------     ---------------  -------------------------------------
# 17 Jan 2008  A Gulland        Initial version
# 27 Feb 2008  A Gulland        More instructions, hard code schema
# ---------------------------------------------------------------------

# get parameters
XML_FILE=$1
DO_VALID=$2
SCHEMA=$3

# or hard code set
# SCHEMA="http://www.govtalk.gov.uk/Education/ContactPoint/Load schema/ContactPointLoad_v1-9f.xsd"


# run app
java -jar bin/XMLSchemaValidator.jar "$XML_FILE" $DO_VALID "$SCHEMA"


