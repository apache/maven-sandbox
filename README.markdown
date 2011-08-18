# EMB - Extensible, Modular Builds #

EMB is a build tool that wraps and extends [Apache Maven](http://maven.apache.org/). Its goal is to provide a mechanism for changing the core functionality of Maven using sets of add-on libraries.

## Getting Started ##

The simplest way to use EMB is via the emb-booter. To do this, first add a dependency in your POM to emb-booter:

    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
      xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
      <modelVersion>4.0.0</modelVersion>
      <groupId>org.commonjava.emb.example</groupId>
      <artifactId>example</artifactId>
      <version>1.0-SNAPSHOT</version>
      <properties>
        <mavenVersion>3.0-beta-2</mavenVersion>
        <embVersion>0.3-SNAPSHOT</embVersion>
      </properties>
      <dependencies>
        <dependency>
          <groupId>org.commonjava.emb</groupId>
          <artifactId>emb-booter</artifactId>
          <version>${embVersion}</version>
        </dependency>
      </dependencies>
    </project>

Then, create a new EMBEmbedder instance, and use it to build a project:

    List<String> goals = new ArrayList<String>();
    goals.add( "clean" );
    goals.add( "install" );

    new EMBEmbedderBuilder().build().execute( new EMBExecutionRequest().setGoals( goals ) );
  
## Using Services ##

You can use allowed Maven components via the ServiceManager. For instance, to resolve an artifact:

    EMBEmbeder emb = new EMBEmbedderBuilder().build();
    emb.serviceManager().repositorySystem().resolve( artifact );
  
Or, to build a set of MavenProject instances from POM files:

    EMBEmbeder emb = new EMBEmbedderBuilder().build();
    
    ProjectBuildingRequest req = new DefaultProjectBuildingRequest()
                                      .setSystemProperties( System.getProperties() )
                                      .setValidationLevel( ModelBuildingRequest.VALIDATION_LEVEL_MINIMAL )
                                      .setForceUpdate( true )
                                      .setProcessPlugins( false )
                                      .setRepositoryCache( new InternalRepositoryCache() )
                                      .setLocalRepository( emb.serviceManager()
                                                              .repositorySystem()
                                                              .createLocalRepository( new File( workDir, "local-repository" ) ) );
                             
    List<ProjectBuildingResult> results = emb.serviceManager().projectBuilder().build( pomFiles, useReactor, req );
    
    List<MavenProject> projects = new ArrayList<MavenProject>( pomFiles.size() );
    for ( final ProjectBuildingResult result : results )
    {
        projects.add( result.getProject() );
    }

