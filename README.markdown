# MAE - Maven App Engine #

Maven App Engine is a wrapper around Apache Maven, which focuses on making it easy to embed Maven and make use of its components inside a third-party application.

## Before You Start ##

Before you can build MAE, you'll need a special variant of Sonatype's Sisu Plexus Container. You can find it here:

[https://github.com/jdcasey/sisu/tree/2.1.1-selectable](https://github.com/jdcasey/sisu/tree/2.1.1-selectable)

To build:

    $ git clone https://github.com/jdcasey/sisu.git
    $ cd sisu
    $ git fetch origin 2.1.1-selectable
    $ git checkout 2.1.1-selectable
    $ mvn clean install

## Getting Started ##

The simplest way to use MAE is via the mae-booter. To do this, first add a dependency in your POM to mae-booter:

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>org.commonjava.emb.example</groupId>
      <artifactId>example</artifactId>
      <version>1.0-SNAPSHOT</version>
      <properties>
        <mavenVersion>3.0-beta-2</mavenVersion>
      </properties>
      <dependencies>
        <dependency>
          <groupId>org.apache.maven.mae</groupId>
          <artifactId>mae-booter</artifactId>
          <version>1.0-alpha-1-SNAPSHOT</version>
        </dependency>
      </dependencies>
    </project>

Then, create a new MAEEmbedder instance, and use it to build a project:

    List<String> goals = new ArrayList<String>();
    goals.add( "clean" );
    goals.add( "install" );

    new MAEEmbedderBuilder().build().execute( new MAEExecutionRequest().setGoals( goals ) );
  
## Using Services ##

You can use allowed Maven components via the ServiceManager. For instance, to resolve an artifact:

    MAEEmbedder mae = new MAEEmbedderBuilder().build();
    mae.serviceManager().repositorySystem().resolve( artifact );
  
Or, to build a set of MavenProject instances from POM files:

    MAEEmbedder mae = new MAEEmbedderBuilder().build();
    
    ProjectBuildingRequest req = new DefaultProjectBuildingRequest()
                                      .setSystemProperties( System.getProperties() )
                                      .setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL )
                                      .setForceUpdate( true )
                                      .setProcessPlugins( false )
                                      .setRepositoryCache( new InternalRepositoryCache() )
                                      .setLocalRepository( mae.serviceManager()
                                                              .repositorySystem()
                                                              .createLocalRepository( new File( workDir, "local-repository" ) ) );
                             
    List<ProjectBuildingResult> results = mae.serviceManager().projectBuilder().build( pomFiles, useReactor, req );
    
    List<MavenProject> projects = new ArrayList<MavenProject>( pomFiles.size() );
    for ( final ProjectBuildingResult result : results )
    {
        projects.add( result.getProject() );
    }

