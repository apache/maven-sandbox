Eclipse plugin for Apache Maven Doxia

Requirements:
* Maven 3 (!)
* Eclipse Helios

Building (with Maven 3):
Tycho (http://tycho.sonatype.org) won't resolve through the reactor for the bundle, so you need to build org.apache.maven.doxia.eclipse.dependencies project first:

mvn clean install -f doxia-osgi/org.apache.maven.doxia.eclipse.dependencies/pom.xml
|_ Makes an OSGi bundle of doxia.

mvn clean install
|_ Creates a feature and a P2 repository
|_ Builds Eclipse plugins

TODO: how to make Eclipse install from the previous build?

Developing this plugin with Eclipse:
File > Import > Existing Projects in Workspace
To test modifications, open plugin.xml and launch

TODO: what modules to import?

Other comments:
1162393 is the rev which demos m-e-p editing MANIFEST.MF (!)

Improvements:
* review POM groupId
* fixed icons and label
* add more actions like add list, comment/uncomment in Apt...
* add Maven documentation for the plugin and how to build an update site.
* test it with other Eclipse distributions

References:
* DOXIATOOLS-8: Create an Eclipse plugin to edit Doxia files


TO BE REVIEWED:

Required Prerequisites:
* Eclipse SDK Platform (http://download.eclipse.org/eclipse/downloads/)
* Web Tools Platform (http://download.eclipse.org/webtools/downloads/)
  - EMF
  - EMF XSD
  - GEF
  - DTP
  - WTP Tool Developers

Known Limitations:
* Needs to bump to WSTP M6 due to:
  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=212330
  - https://bugs.eclipse.org/bugs/show_bug.cgi?id=224040
