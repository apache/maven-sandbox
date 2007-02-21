Maven Enterprise
================

To build:

mvn install continuum from maven trunk
mvn install archiva from maven trunk
mvn install enterprise (from this directory)

to run:

./enterprise-runtime/target/plexus-app-runtime/bin/plexus.sh

to configure the system for a production install:

mvn install -Pinstall (from this directory)

and copy enterprise-runtime/target/plexus-app-runtime to your preferred
installation location.

