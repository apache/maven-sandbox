
Sandbox :: Maven Quickstart Archetype
=====================================

Problem:  package name interpolation is not possible for non-Java resources

In fixing this problem, we should retain the ability to *not* package
non-Java resources.

Observations:

 * <resource> elements in the archetype descriptor are placed relative to
 src/main/resources, not src/main/resources/[package]

 * In prior versions of the archetype plugin, it was possible to list non-Java
 resources under <sources> and have them "packaged". For example:
 <source>src/main/resources/App.properties</source>

 * Some non-Java files in src/main/java are simply ignored. Javadoc related
 files such as package.html belong with the source code so that (by default)
 they are not included in the jar.

Demonstration:
 
 This module contains the quickstart archetype with the following changes:

 * Added package.html and overview.html for Javadocs.
 * Added App.properties file
 * Modified archetype.xml to include the new files
 
 To try this example:

 $ cd /path/to/maven/archetype
 $ mvn install
 
 $ cd /path/to/maven/sandbox/archetype/maven-archetype-quickstart
 $ mvn install
 
 $ cd /path/to/temp
 $ mvn archetype:create -DgroupId=com.example -DartifactId=myproject
       -DarchetypeVersion=1.1-SNAPSHOT
       -DarchetypeGroupId=org.apache.maven.archetypes
 
 (You may not need archetypeGroupId, I get an error without it.)
 
 Result:

With the following in archetype.xml:

  <sources>
    <source>src/main/java/App.java</source>
    <source>src/main/java/package.html</source>
    <source>src/main/resources/App.properties</source>
  </sources>

I get:

[ERROR] BUILD ERROR
[INFO] ---------------------------------------------------------------------
[INFO] Error creating from archetype

Embedded error: Error merging velocity templates
Unable to find resource 'archetype-resources/src/main/java/package.html'

If I comment out the line for package.html and rebuild the archetype, the error
changes to:

[ERROR] BUILD ERROR
[INFO] ------------------------------------------------------------------------
[INFO] Error creating from archetype

Embedded error: Template 'src/main/resources/App.properties' not in directory 's
rc/main/java'

The only option is to move package.html and App.properties to the <resources> 
section, but then they will not be placed into the proper package structure.

I have also listed the overview.html file in archetype.xml:

  <resources>
    <resource>src/main/java/overview.html</resource>
  </resources>

This works, and places the overview.html file directly in src/main/java.

* Related Links
 
 Allow Non-Java Resources in sources/testSources to be included in an archetype
 http://jira.codehaus.org/browse/ARCHETYPE-62
 
 Archetype: create resources as companions to java source files
 http://www.mail-archive.com/users@maven.apache.org/msg62117.html
 
 Wrong groupId for the archetypes?
 http://www.mail-archive.com/dev@maven.apache.org/msg63136.html

