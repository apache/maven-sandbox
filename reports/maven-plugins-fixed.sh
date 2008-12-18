#!/bin/sh

if [ $JAVA_HOME ]
then
    echo "The script will use \$JAVA_HOME = $JAVA_HOME"
else
    echo "\$JAVA_HOME must be defined to launch the script."
    exit 1
fi

if [ $# -ne 1 ]
then
    echo "Usage - $0 output-directory"
    exit 1
fi

if [ -d $1 ]
then
    export OUTPUTDIR=$1
else
    echo "Sorry, $1 directory does not exist"
fi

export JAVA_CMD=$JAVA_HOME/bin/java
export JAVA_OPTS="-ms32m -mx256m"
export CMD="$JAVA_CMD $JAVA_OPTS -jar swizzle-jirareport-1.2.3-SNAPSHOT-dep.jar"

$CMD maven-plugins-fixed.vm > $OUTPUTDIR/maven-plugins-fixed.txt
$CMD maven-plugins-fixed-html.vm > $OUTPUTDIR/maven-plugins-fixed.html

exit 0