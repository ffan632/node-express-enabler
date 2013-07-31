[fabrician.org](http://fabrician.org/)
==========================================================================
Node.js Express Enabler Guide
==========================================================================

Introduction
--------------------------------------
This is a TIBCO Silver Fabric Enabler for node.js Express applications. With this Enabler, Silver Fabric Server can automatically provisions, orchestrates, controls and manages Node.js Express applications. 
Since Express applications don't use configuration file, the Enabler installs 'container_env.js' with the application. The application needs to import this module to pickup configuration information. The 
changes are minimum, please reference the sample app in the Configuration section.   

Supported Platforms
--------------------------------------
* Silver Fabric 5
* Linux

Installation
--------------------------------------
The Enabler consists of an Enabler Runtime Grid Library and a Distribution 
Grid Library. The Enabler Runtime contains information specific to a Silver Fabric version that is used to integrate the Enabler, and the Distribution contains the application server or program used for the 
Enabler. Installation of the Nodejs Express Enabler involves copying these Grid 
Libraries to the SF_HOME/webapps/livecluster/deploy/resources/gridlib directory on the Silver Fabric Broker. 


Distribution Grid Library
--------------------------------------
* Build nodejs-distribution
** Download node.js binary for your platform from http://nodejs.org/download/
** Unpack the node.js archive, rename the top level directory (for example, node-v0.10.15-linux-x64 ) to "node"
** Create a distribution Grid Library with the "node" directory and "grid-library.xml" such as the following:  
```
    <grid-library os="linux64">
        <grid-library-name>nodejs-distribution</grid-library-name>
        <grid-library-version>{nodejs-distro-version}</grid-library-version>
    </grid-library>
```
* Build express-distribution
** With node.js installed, install certain version of express in the current directory with npm  "npm install express@<version>"
** Create a distribution Grid Library with the "node_modules" directory and "grid-library.xml" such as the following:  
```
    <!-- os can be either linux or linux64 -->
    <grid-library os="linux64">
        <grid-library-name>express-distribution</grid-library-name>
        <grid-library-version>{express-distro-version}</grid-library-version>
    </grid-library>
```

**Notes** 
* **Make sure the 'os' attribute is appropriate for you platform (linux or linux64)**
* **{nodejs-distro-version} must match property nodejs-distro-version in the Enabler project's pom.xml**
* **{express-distro-version} must match property express-distro-version in the Enabler project's pom.xml**

Runtime Grid Library
--------------------------------------
The Enabler Runtime Grid Library is created by building the maven project.  The build depends on the
SilverFabricSDK jar file that is distributed with TIBCO Silver Fabric.  The SilverFabricSDK.jar file needs to
be referenced in the maven pom.xml or it can be placed in the project root directory.

* Edit the pom.xml file, and update the version of the distribution to the one you just built.
* Build the Enabler Runtime Grid Library with maven:

```bash
mvn package
```

Deploy Application
--------------------------------------
* Create a new "Nodejs Express" Component On the Silver Fabric Broker.
* Package your application into .zip or .tar.gz archive, upload the archive by choosing "Edit Component" -> "Add/remove archive files"
** You need to set the main script file using the "MAIN_SCRIPT" Runtime Context Variable.
* Optionally you can package additional Nodejs modules by packaging ./node_modules into an archive and upload them side by side with the application

  
Configuration 
--------------------------------------
* Configure HTTP
** "Edit Component" -> "Add/edit Application Component features" -> Edit "Http Support" Feature, check on "Http Enabled".
** Choose HTTP port by setting the "LISTEN_PORT" Runtime_Context_Variable
* Configure HTTPS
** "Edit Component" -> "Add/edit Application Component features" -> Edit "Http Support" Feature, check on "Https Enabled".
** Choose HTTPS port by setting the "LISTEN_PORT_SSL" Runtime_Context_Variable; Optionally set up server credential with "SSL_KEY_FILE", "SSL_KEY_PASSWORD" and "SSL_TRUSTED_CA_FILE" variables

The container collects these information and installs "container_env.js" by the side of the application's top director, the application can pickup these information by importing this module. 
Here is a code snippet extracted from sample/app.js installed by the container
```
var express = require('express');
var app = express();
// import container configuration
var cntr_env = require('../container_env.js');

// enable STATS
if ( cntr_env.SERVER_STATS_ENABLED ){
  app.use(cntr_env.stats);
}

// simple respond
app.use(function(req, res, next){
  res.send('Hello World');
});

// enable HTTP port
if ( cntr_env.HTTP_ENABLED ){
  var http = require ('http');
  http.createServer(app).listen(cntr_env.HTTP_PORT);
}

// enable HTTPS port
if ( cntr_env.HTTPS_ENABLED ){
  var https = require ('https');
  var fs = require ('fs');
  var options = {
    key: fs.readFileSync(cntr_env.SSL_KEY_FILE),
    cert: fs.readFileSync(cntr_env.SSL_TRUSTED_CA_FILE),
    passphrase: cntr_env.SSL_KEY_PASSWORD 
  };
  https.createServer(options, app).listen(cntr_env.HTTPS_PORT);
}

``` 

Statistics
--------------------------------------
So far we only support very few server statistics. 
* **Total Accesses** - Total requests processed since last started.
To enable statistics in the application, you need to call app.use() to "connect" container provided statistics function (as in the previous example). You also need to set following Runtime_Context_Variables:
* SERVER_STATS_ENABLED
* SERVER_STATS_PATH

Runtime Context Variables
--------------------------------------
* **LISTEN_PORT** - Web Server listening port.  
    * Type: String/Inc
    * Default value: 8080 
* **LISTEN_PORT_SSL** - Web Server SSL listening port.  Note: your distribution must be setup for SSL, othewise ignore this.
    * Type: String/Inc
    * Default value: 443
* **SSL_KEY_FILE** - path to Server KEY file (relative to the current module), if SSL is supported.
    * Type: String
    * Default value: ""
* **SSL_KEY_PASSWORD** - Password to SSL Key file.
    * Type: String
    * Default value: ""
* **SSL_TRUSTED_CA_FILE** - SSL Trusted CA file (relative to the current module), if SSL is supported.
    * Type: String
    * Default value: ""
* **SERVER_STATS_ENABLED** - Indicating whether Server Statistics will be enabled. 
    * Type: String
    * Default value: "true"
* **SERVER_STATS_PATH** - URL path to retrieve server statistics.  
    * Type: String
    * Default value: "/server-status"
* **MAIN_SCRIPT** -- The main app script the Enabler starts
    * Type: String
    * Default value: "sample/app.js"


