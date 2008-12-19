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

echo "Generating maven-votes.txt ..."
$CMD maven.vm -DentityExpansionLimit=500000 > $OUTPUTDIR/maven-votes.txt
echo "maven-votes.txt generated."
echo "Generating maven-votes.html ..."
$CMD maven-html.vm -DentityExpansionLimit=500000 > $OUTPUTDIR/maven-votes.html
echo "maven-votes.html generated."
echo "Generating maven-votes.html ..."
$CMD maven-plugins.vm > $OUTPUTDIR/maven-votes.html
echo "maven-votes.html generated."
echo "Generating plugin-votes.html ..."
$CMD maven-plugins-html.vm > $OUTPUTDIR/plugin-votes.html
echo "plugin-votes.html generated."
echo "Generating maven-plugins-fixed.txt ..."
$CMD maven-plugins-fixed.vm > $OUTPUTDIR/maven-plugins-fixed.txt
echo "maven-plugins-fixed.txt generated."
echo "Generating maven-plugins-fixed.html ..."
$CMD maven-plugins-fixed-html.vm > $OUTPUTDIR/maven-plugins-fixed.html
echo "maven-plugins-fixed.html generated."

exit 0