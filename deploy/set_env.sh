#!/usr/bin/ksh
#
# run this script to define the Java environment
#
# update the value of JAVA_HOME to point to the JDK on your system
#


JAVA_HOME=/bo/BOE/bobje/jdk
export JAVA_HOME

PATH=$JAVA_HOME/bin:$PATH
export PATH

