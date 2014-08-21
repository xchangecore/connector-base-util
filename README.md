connector-base-util
===================

The basics of using the Spring web service framework to send requests to XchangeCore.
This code should be compiled first before any other code in the repository.

Prerequisites:
--------------
1. Java JDK 1.7.0-xx with JAVA_HOME set
2. maven 3.0.3 or later


To Build:
----------------------
1. Edit the install-maven-dependencies.bat batch file to change
   the paths that reference in the -Dfile switch. The jar files
   are in the jarfiles folder. 
2. Run the install-maven-dependencies.bat to install the XchangeCore
   XmlBeans jar files and the rome jar files in the local maven repository.
3. Set an environment variable called MAVEN_OPTS to -Xss4m.
4. Run "mvn clean install" in the java directory of the client code to
   build com.saic.uicds.client.util.
5. Run "mvn clean install -Dmaven.test.skip=true" to skip the tests which need a running XchangeCore to pass.

