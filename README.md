# CloudBroker Platform CLI

The command line interface (CLI) for the CloudBroker Platform (CBP)
allows to perform actions on the platform from the command line.

cbp.properties
This file contains the Java properties that you have to modify first to configure the CBP CLI
for the correct user account and platform installation.

cbp.sh
This is a Bash shell script to call the CBP CLI on Linux, Unix and Mac OS X systems.
You might have to first make it executable with: chmod +x cbp.sh
To run it please use: ./cbp.sh <arguments>

CloudbrokerCLI-*.jar
This is the Java archive containing the CBP CLI.

## About the CloudBroker Platform

The CloudBroker Platform is a web-based application store for the deployment and execution
of compute-intensive scientific and technical software in the cloud. It is provided as a cloud
service that allows for on-demand, scalable and pay-per-use access via the internet.

CloudBroker currently offers the platform as public version under
[CloudBroker](https://platform.cloudbroker.com), as
hosted or in-house setup, as well as as licensed software. This also means that the platform can
be run at different physical places and under different legislation, if desired.

## Requirements

* Java JDK 1.6 or later
* [Apache Ant](http://ant.apache.org/) build system
* [Ivy](http://ant.apache.org/ivy/) dependency manager

## Contributions

CloudBroker welcomes rational contributions as well as bug/issue reports from everyone!

## Authors and Contributors

* Nicola Fantini <<nicola.fantini@cloudbroker.com>>:
  Project management
* Wibke Sudholt <<wibke.sudholt@cloudbroker.com>>:
  Project management
* Andrey Sereda <<andrey.sereda@scaletools.com>>: 
  Lead developer, maintainer
* Maxim Malgin <<svkmax@gmail.com>>:
  Contribution in Tags, Fees and Costs management
* Arthur Karganyan <<artur.karganyan@scaletools.com>>:  
  Contribution in code cleanup and optimization

## Licensing

Copyright 2013 CloudBroker GmbH, Zurich, Switzerland

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

A copy of the license is available in the repository's [LICENSE](https://github.com/CloudBroker/cbp-java-api/blob/master/LICENSE) file.
Please also see the accompanying [NOTICE](https://github.com/CloudBroker/cbp-java-api/blob/master/NOTICE) file.

