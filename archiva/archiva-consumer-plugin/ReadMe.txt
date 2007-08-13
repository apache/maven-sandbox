--------------------------------------
How To Add A New Component in Archiva:
--------------------------------------

1. Create a project for your component.

2. Declare your class or in this case, consumer as a component as shown in the example below. This
   should be put at the class level.

   Ex.
   @plexus.component role="org.apache.maven.archiva.consumers.KnownRepositoryContentConsumer"
                     role-hint="discover-new-artifact"
                     instantiation-strategy="per-lookup"


   Legend:
   ** role : the interface that your class implements
   ** role-hint : the lookup name of the component (your class/consumer)
   ** instantiation-strategy : how your class will be instantiated

3. Make sure to add the snipper below in the <build> section of the project's pom. This is needed to
   generate the components.xml.

      <plugin>
        <groupId>org.codehaus.plexus</groupId>
        <artifactId>plexus-maven-plugin</artifactId>
        <version>1.3.5</version>
        <executions>
          <execution>
            <id>generate</id>
            <goals>
              <goal>descriptor</goal>
            </goals>
          </execution>
        </executions>
      </plugin>

4. Package your project by executing 'mvn clean package'

5. Let's say you are using the apache-archiva-1.0-beta-2-SNAPSHOT-bin.tar.gz to run Archiva. Unpack
   the binaries then go to bin/linux-x86-32/ (assuming you are running on Linux), then execute
   './run.sh console'. Then stop or shutdown Archiva after it started. (This is necessary to unpack
   the war file.)

6. Copy the jar file you created in #3 in apache-archiva-1.0-beta-2-SNAPSHOT/apps/archiva/webapp/lib/

7. Add the necessary configurations in archiva.xml (in this case, add 'discover-new-artifact' as a
   <knownContentConsumer>)

8. Start up Archiva again.