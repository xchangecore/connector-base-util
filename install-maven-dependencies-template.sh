#!/bin/sh

mvn install:install-file -DgroupId=com.saic.uicds.core.em -DartifactId=com.saic.uicds.core.em.xmlbeans-em -Dversion=1.1.4 -Dpackaging=jar -Dfile=./jarfiles/com.saic.uicds.core.em.xmlbeans-em-1.1.4.jar

mvn install:install-file -DgroupId=com.saic.uicds.core.infrastructure -DartifactId=com.saic.uicds.core.infrastructure.xmlbeans-infrastructure -Dversion=1.2.0 -Dpackaging=jar -Dfile=./jarfiles/com.saic.uicds.core.infrastructure.xmlbeans-infrastructure-1.2.0.jar

mvn install:install-file -DgroupId=com.saic.uicds.core.em -DartifactId=com.saic.uicds.core.em.xmlbeans-edxl_rm -Dversion=1.0.0 -Dpackaging=jar -Dfile=./jarfiles/com.saic.uicds.core.em.xmlbeans-edxl_rm-1.0.0.jar

mvn install:install-file -DgroupId=org.rometools -DartifactId=rome-modules -Dversion=1.5-SNAPSHOT -Dpackaging=jar -Dfile=./jarfiles/rome-modules-1.5-SNAPSHOT.jar

mvn install:install-file -DgroupId=org.rometools -DartifactId=rome -Dversion=1.1-SNAPSHOT -Dpackaging=jar -Dfile=./jarfiles/rome-1.1-SNAPSHOT.jar

mvn install:install-file -DgroupId=org.rometools -DartifactId=rome-fetcher -Dversion=1.2 -Dpackaging=jar -Dfile=./jarfiles/rome-fetcher-1.2.jar
