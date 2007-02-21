To use the example module here which is already set up to use the enterprise
features you will need to install the dav provider for wagon.

Do this by grabbing the files:

wagon-webdav-1.0-beta-2.jar
slide-webdavlib-2.1.jar
commons-logging-1.0.4.jar
commons-httpclient-2.0.2.jar

and putting them in M2_HOME/lib/

You will also need to create an account on enterprise (go to
http://localhost:8080/archiva/) and enter the credentials into
~/.m2/settings.xml, e.g.:

<settings>
  <servers>
    <server>
      <id>enterprise-repository</id>
      <username>admin</username>
      <password>password</password>
    </server>
    <server>
      <id>enterprise-snapshot</id>
      <username>admin</username>
      <password>password</password>
    </server>
    <server>
      <id>enterprise-site</id>
      <username>admin</username>
      <password>password</password>
    </server>
  </servers>
</settings>

Then you can "mvn deploy" or "mvn site site:deploy" to install the artifacts into the
enterprise repositories (making sure enterprise is booted first).
