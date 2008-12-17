#!/bin/sh

export JAVA_HOME=/opt/java/sdk/current
export JAVA_CMD=$JAVA_HOME/bin/java
export JAVA_OPTS="-ms32m -mx256m"
export JAVA="$JAVA_CMD $JAVA_OPTS"

$JAVA -jar swizzle-jirareport-1.2.3-SNAPSHOT-dep.jar maven-plugins-fixed.vm > $1/maven-plugins-fixed.txt
