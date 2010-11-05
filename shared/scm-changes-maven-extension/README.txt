SCM Changes Maven Extension
---------------------------

Build only projects containing files that that you personally have changed (according to SCM), and projects that
depend on those projects (downstream).

To use, add this to your parent POM:

<build>
  <extensions>
    <extension>
      <groupId>org.apache.maven.examples</groupId>
      <artifactId>retro-maven-extension</artifactId>
      <version>1.0-SNAPSHOT</version>
    </extension>
  </extensions>
</build>

Be sure to also specify an SCM connection:

<scm>
  <connection>scm:svn:http://svn.apache.org/repos/asf/maven/plugins/trunk/maven-reactor-plugin/</connection>
  <developerConnection>scm:svn:https://svn.apache.org/repos/asf/maven/plugins/trunk/maven-reactor-plugin/</developerConnection>
  <url>http://svn.apache.org/viewvc/maven/plugins/trunk/maven-reactor-plugin/</url>
</scm>

Then run your build like this:

  mvn install -Dmake.scmChanges

That will build only those projects that you changed, and projects that depend on those projects (downstream).

IF IT DOESN'T APPEAR TO BE WORKING:  Try running mvn with -X to get debug logs.

Note that if you modify the root POM (to add this extension) without checking it in, then EVERYTHING is downstream of
the root POM, so -Dmake.scmChanges will cause a full rebuild; it will appear as if it's not working. You can use
-Dmake.ignoreRootPom to ignore changes in the root POM while testing this extension.