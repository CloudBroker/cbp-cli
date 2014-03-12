#!/bin/bash

# Copyright (c) 2012, CloudBroker GmbH. All rights reserved.


##### Command Line Interface for the CloudBroker Platform #####

# This shell script only runs under Linux, Unix and Mac OS X.
# It assumes that the java executable is in the path.

jarfile=`ls -1 CloudbrokerCLI-*.jar | tail -1`
java -jar ${jarfile} "$@"

exit
