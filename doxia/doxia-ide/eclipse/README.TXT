Eclipse plugin for Apache Maven Doxia

Build with Maven:
Tycho (http://tycho.sonatype.org) won't resolve through the reactor for the bundle, so you need to build firstly org.apache.maven.doxia.eclipse.dependencies project:

mvn clean install -f doxia-osgi/org.apache.maven.doxia.eclipse.dependencies/pom.xml
|_ Make an OSGi bundle of doxia.
mvn clean install
|_ Create a feature and a P2 repository
|_ Eclipse plugins

Using Eclipse IDE:
File/import/Existing projects in workspace/${basedir}

Other comments:
1162393 is the rev which demos m-e-p editing MANIFEST.MF (!)
  
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

Improvements:
* review POM groupId
* fixed icons and label
* add more actions like add list, comment/uncomment in Apt...

References:
* DOXIA-233: Create an Eclipse plugin to edit Doxia files
* DOXIA-228: Create a new tool to handle module conversion