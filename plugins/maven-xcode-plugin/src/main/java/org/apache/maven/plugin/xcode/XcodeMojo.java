package org.apache.maven.plugin.xcode;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.artifact.manager.WagonManager;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.model.Resource;
import org.apache.maven.wagon.TransferFailedException;
import org.apache.maven.wagon.ResourceDoesNotExistException;
import org.dom4j.Element;
import org.codehaus.plexus.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.io.FilenameFilter;

/**
 * Goal for generating an Xcode project from a POM.
 * This plug-in provides the ability to generate projects for Xcode from Apple, Inc.
 *
 * @goal xcode
 * @execute phase="generate-sources"
 */
public class XcodeMojo
        extends AbstractXcodeMojo
{
    /**
     * The reactor projects in a multi-module build.
     *
     * @parameter expression="${reactorProjects}"
     * @required
     * @readonly
     */
    private List reactorProjects;

    /**
     * @component
     */
    private WagonManager wagonManager;

    /**
     * Whether to link the reactor projects as dependency modules or as libraries.
     *
     * @parameter expression="${linkModules}" default-value="true"
     */
    private boolean linkModules;

    /**
     * Specify the location of the deployment descriptor file, if one is provided.
     *
     * @parameter expression="${deploymentDescriptorFile}"
     */
    private String deploymentDescriptorFile;

    /**
     * Whether to use full artifact names when referencing libraries.
     *
     * @parameter expression="${useFullNames}" default-value="false"
     */
    private boolean useFullNames;

    /**
     * Enables/disables the downloading of source attachments.
     *
     * @parameter expression="${downloadSources}" default-value="false"
     */
    private boolean downloadSources;

    /**
     * Enables/disables the downloading of javadoc attachments.
     *
     * @parameter expression="${downloadJavadocs}" default-value="false"
     */
    private boolean downloadJavadocs;

    /**
     * Sets the classifier string attached to an artifact source archive name.
     *
     * @parameter expression="${sourceClassifier}" default-value="sources"
     */
    private String sourceClassifier;

    /**
     * Sets the classifier string attached to an artifact javadoc archive name.
     *
     * @parameter expression="${javadocClassifier}" default-value="javadoc"
     */
    private String javadocClassifier;

    /**
     * Specify the name of the registered IDEA JDK to use
     * for the project.
     *
     * @parameter expression="${jdkName}"
     */
    private String jdkName;

    /**
     * Specify the version of the JDK to use for the project for the purpose of
     * enabled assertions and Java 5.0 language features.
     * The default value is the specification version of the executing JVM.
     *
     * @parameter expression="${jdkLevel}"
     * @todo would be good to use the compilation source if possible
     */
    private String jdkLevel;

    /**
     * An optional set of Library objects that allow you to specify a comma separated list of source dirs, class dirs,
     * or to indicate that the library should be excluded from the module. For example:
     * <p/>
     * <pre>
     * &lt;libraries&gt;
     *  &lt;library&gt;
     *      &lt;name&gt;webwork&lt;/name&gt;
     *      &lt;sources&gt;file://$webwork$/src/java&lt;/sources&gt;
     *      &lt;!--
     *      &lt;classes&gt;...&lt;/classes&gt;
     *      &lt;exclude&gt;true&lt;/exclude&gt;
     *      --&gt;
     *  &lt;/library&gt;
     * &lt;/libraries&gt;
     * </pre>
     *
     * @parameter
     */
    private Library[] libraries;

    /**
     * A comma-separated list of directories that should be excluded. These directories are in addition to those
     * already excluded, such as target/classes. A common use of this is to exclude the entire target directory.
     *
     * @parameter
     */
    private String exclude;

    /**
     * Specify the resource pattern in wildcard format, for example "?*.xml;?*.properties".
     * Currently supports 4.x and 5.x.
     * Because IDEA doesn't distinguish between source and resources directories, this is needed.
     * The default value corresponds to any file without a java extension.
     * Please note that the default value includes package.html files as it's not possible to exclude those.
     *
     * @parameter expression="${wildcardResourcePatterns}" default-value="!?*.java"
     */
    private String wildcardResourcePatterns;


    /**
     * Causes the module libraries to use a short name for all dependencies. This is very convenient but has been
     * reported to cause problems with IDEA.
     *
     * @parameter default-value="false"
     */
    private boolean dependenciesAsLibraries;



    public void execute()
        throws MojoExecutionException
    {
        try
        {
            doDependencyResolution( executedProject, localRepo );
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "Unable to build project dependencies.", e );
        }

        File projectDir = new File( executedProject.getBasedir(),
                executedProject.getArtifactId() + ".xcodeproj" );
        if (projectDir.exists()) {
            if (!projectDir.isDirectory()) {
                throw new MojoExecutionException( "Target " +
                        projectDir.getPath() +
                        " exists, but is not a directory." );
            }
        } else {
            if(!projectDir.mkdirs()) {
                throw new MojoExecutionException( "Unable to create " +
                        projectDir.getPath() +
                        " directory." );
            }
        }

        Map propertyList = new HashMap();
        propertyList.put("archiveVersion", "1");
        propertyList.put("classes", Collections.EMPTY_LIST);
        propertyList.put("objectVersion", "42");

        Map objects = new HashMap();
        propertyList.put("objects", objects);

        final String sourceTree = "<source>";

        PBXObjectRef buildConfigurationList = null;
        List buildPhases = new ArrayList();
        List dependencies = Collections.EMPTY_LIST;
        PBXObjectRef productReference = null;

        Map toolDebugSettings = new HashMap();
        Map toolReleaseSettings = new HashMap();

        toolDebugSettings.put("COPY_PHASE_STRIP", "NO");
        toolDebugSettings.put("GCC_DYNAMIC_NO_PIC", "NO");
        toolDebugSettings.put("GCC_ENABLE_FIX_AND_CONTINUE", "YES");
        toolDebugSettings.put("GCC_GENERATE_DEBUGGING_SYMBOLS", "YES");
        toolDebugSettings.put("GCC_OPTIMIZATION_LEVEL", "0");
        toolDebugSettings.put("JAVA_ARCHIVE_CLASSES", "YES");
        toolDebugSettings.put("JAVA_ARCHIVE_COMPRESSION", "NO");
        toolDebugSettings.put("JAVA_ARCHIVE_TYPE", "JAR");
        toolDebugSettings.put("JAVA_COMPILER", "/usr/bin/javac");
        toolDebugSettings.put("JAVA_COMPILER_SOURCE_VERSION", "1.3");
        toolDebugSettings.put("JAVA_COMPILER_TARGET_VM_VERSION", "1.3");
        //toolDebugSettings.put("JAVA_MANIFEST_FILE", "Manifest");
        toolDebugSettings.put("PRODUCT_NAME", executedProject.getArtifactId());
        toolDebugSettings.put("PURE_JAVA", "YES");
        toolDebugSettings.put("REZ_EXECUTABLE", "YES");
        toolDebugSettings.put("ZERO_LINK", "YES");

        toolReleaseSettings.put("COPY_PHASE_STRIP", "YES");
        toolReleaseSettings.put("GCC_ENABLE_FIX_AND_CONTINUE", "NO");
        toolReleaseSettings.put("JAVA_ARCHIVE_CLASSES", "YES");
        toolReleaseSettings.put("JAVA_ARCHIVE_COMPRESSION", "NO");
        toolReleaseSettings.put("JAVA_ARCHIVE_TYPE", "JAR");
        toolReleaseSettings.put("JAVA_COMPILER", "/usr/bin/javac");
        //toolReleaseSettings.put("JAVA_MANIFEST_FILE", "Manifest");
        toolReleaseSettings.put("PRODUCT_NAME", executedProject.getArtifactId());
        toolReleaseSettings.put("PURE_JAVA", "YES");
        toolReleaseSettings.put("REZ_EXECUTABLE", "YES");
        toolReleaseSettings.put("ZERO_LINK", "NO");

        PBXObjectRef toolConfigurations =
                addProjectConfigurationList(objects,
                        toolDebugSettings,
                        toolReleaseSettings);

        PBXObjectRef toolTarget = createPBXToolTarget(
                toolConfigurations, buildPhases, dependencies,
                executedProject.getArtifactId(),
                "/usr/local/install",
                executedProject.getArtifactId(), productReference);
        objects.put(toolTarget.getID(), toolTarget.getProperties());
        
/*
        if ( "war".equals( executedProject.getPackaging() ) )
        {
            toolTarget = addWebModule( objects );
        }
        else if ( "ejb".equals( executedProject.getPackaging() ) )
        {
            toolTarget = addEjbModule( objects );
        }
        else if ( "ear".equals( executedProject.getPackaging() ) )
        {
            toolTarget = addEarModule( objects );
        } else {
        }
*/

        String outputDir = executedProject.getBuild().getOutputDirectory();

        String outputTestDir = executedProject.getBuild().getTestOutputDirectory();

        Map groups = new HashMap();

        List rootGroupChildren = new ArrayList();
        PBXObjectRef rootGroup = createPBXGroup(executedProject.getArtifactId(),
                sourceTree, rootGroupChildren);
        objects.put(rootGroup.getID(), rootGroup.getProperties());

        PBXObjectRef products = createPBXGroup("Products", sourceTree, new ArrayList());
        objects.put(products.getID(), products.getProperties());
        rootGroupChildren.add(products.getID());


        //
        //   add project configurations
        //
        PBXObjectRef projectConfigurations =
                addProjectConfigurationList(objects,
                        Collections.EMPTY_MAP,
                        Collections.EMPTY_MAP);

        PBXObjectRef project = createPBXProject(projectConfigurations,
                rootGroup, "", Collections.EMPTY_LIST);
        objects.put(project.getID(), project.getProperties());
        propertyList.put("rootObject", project.getID());

        int buildActionMask = 2147483647;

        
        List buildFiles = new ArrayList();
        PBXObjectRef sourcesBuildPhase = createPBXSourcesBuildPhase(buildActionMask,
                buildFiles, false);
        objects.put(sourcesBuildPhase.getID(), sourcesBuildPhase.getProperties());
        buildPhases.add(sourcesBuildPhase);

        PBXObjectRef javaArchiveBuildPhase = createPBXJavaArchiveBuildPhase(
                buildActionMask, buildFiles, false);
        objects.put(javaArchiveBuildPhase.getID(),
                javaArchiveBuildPhase.getProperties());

        buildPhases.add(javaArchiveBuildPhase);
        buildPhases.add(sourcesBuildPhase);

        PBXObjectRef copyFilesBuildPhase = createPBXCopyFilesBuildPhase(
                8, "/usr/share/man/man1", 0, Collections.EMPTY_LIST, true);
        buildPhases.add(copyFilesBuildPhase);
        objects.put(copyFilesBuildPhase.getID(), copyFilesBuildPhase.getProperties());

        PBXObjectRef frameworksBuildPhase = createPBXFrameworksBuildPhase(
                buildActionMask, Collections.EMPTY_LIST, false);
        buildPhases.add(frameworksBuildPhase);
        objects.put(frameworksBuildPhase.getID(), frameworksBuildPhase.getProperties());
        
        

        for ( Iterator i = executedProject.getCompileSourceRoots().iterator(); i.hasNext(); )
        {
            String directory = (String) i.next();
            toolDebugSettings.put("JAVA_SOURCE_SUBDIR", directory);
            toolReleaseSettings.put("JAVA_SOURCE_SUBDIR", directory);
            addSourceFolder( objects, rootGroup, groups, sourceTree,
                    buildFiles, directory, false );
        }
        for ( Iterator i = executedProject.getTestCompileSourceRoots().iterator(); i.hasNext(); )
        {
            String directory = (String) i.next();
//            addSourceFolder( objects, rootGroup, groups, sourceTree, directory, true );
        }

        for ( Iterator i = executedProject.getBuild().getResources().iterator(); i.hasNext(); )
        {
            Resource resource = (Resource) i.next();
            String directory = resource.getDirectory();
            if ( resource.getTargetPath() == null && !resource.isFiltering() )
            {
//                addSourceFolder( objects, rootGroup, groups, sourceTree, directory, false );
            }
            else
            {
                getLog().info(
                    "Not adding resource directory as it has an incompatible target path or filtering: "
                        + directory );
            }
        }

        for ( Iterator i = executedProject.getBuild().getTestResources().iterator(); i.hasNext(); )
        {
            Resource resource = (Resource) i.next();
            String directory = resource.getDirectory();
            if ( resource.getTargetPath() == null && !resource.isFiltering() )
            {
//                addSourceFolder( objects, rootGroup, groups, sourceTree, directory, true );
            }
            else
            {
                getLog().info(
                    "Not adding test resource directory as it has an incompatible target path or filtering: "
                        + directory );
            }
        }

        removeOldElements( objects, "excludeFolder" );

        //For excludeFolder
        File target = new File( executedProject.getBuild().getDirectory() );
        File classes = new File( executedProject.getBuild().getOutputDirectory() );
        File testClasses = new File( executedProject.getBuild().getTestOutputDirectory() );

        List sourceFolders = new ArrayList(); //content.elements( "sourceFolder" );

        List filteredExcludes = new ArrayList();
        filteredExcludes.addAll( getExcludedDirectories( target, filteredExcludes, sourceFolders ) );
        filteredExcludes.addAll( getExcludedDirectories( classes, filteredExcludes, sourceFolders ) );
        filteredExcludes.addAll( getExcludedDirectories( testClasses, filteredExcludes, sourceFolders ) );

        if ( exclude != null )
        {
            String[] dirs = exclude.split( "[,\\s]+" );
            for ( int i = 0; i < dirs.length; i++ )
            {
                File excludedDir = new File( executedProject.getBasedir(), dirs[i] );
                filteredExcludes.addAll( getExcludedDirectories( excludedDir, filteredExcludes, sourceFolders ) );
            }
        }

        // even though we just ran all the directories in the filteredExcludes List through the intelligent
        // getExcludedDirectories method, we never actually were guaranteed the order that they were added was
        // in the order required to make the most optimized exclude list. In addition, the smart logic from
        // that method is entirely skipped if the directory doesn't currently exist. A simple string matching
        // will do pretty much the same thing and make the list more concise.
        ArrayList actuallyExcluded = new ArrayList();
        Collections.sort( filteredExcludes );
        for ( Iterator i = filteredExcludes.iterator(); i.hasNext(); )
        {
            String dirToExclude = i.next().toString();
            String dirToExcludeTemp = dirToExclude.replace( '\\', '/' );
            boolean addExclude = true;
            for ( Iterator iterator = actuallyExcluded.iterator(); iterator.hasNext(); )
            {
                String dir = iterator.next().toString();
                String dirTemp = dir.replace( '\\', '/' );
                if ( dirToExcludeTemp.startsWith( dirTemp + "/" ) )
                {
                    addExclude = false;
                    break;
                }
                else if ( dir.startsWith( dirToExcludeTemp + "/" ) )
                {
                    actuallyExcluded.remove( dir );
                }
            }

            if ( addExclude )
            {
                actuallyExcluded.add( dirToExclude );
                addExcludeFolder( objects, dirToExclude );
            }
        }

        //Remove default exclusion for output dirs if there are sources in it
        String outputModuleUrl = getModuleFileUrl( executedProject.getBuild().getOutputDirectory() );
        String testOutputModuleUrl = getModuleFileUrl( executedProject.getBuild().getTestOutputDirectory() );
/*
        for ( Iterator i = content.elements( "sourceFolder" ).iterator(); i.hasNext(); )
        {
            Element sourceFolder = (Element) i.next();
            String sourceUrl = sourceFolder.attributeValue( "url" ).replace( '\\', '/' );
            if ( sourceUrl.startsWith( outputModuleUrl + "/" ) || sourceUrl.startsWith( testOutputModuleUrl ) )
            {
                component.remove( component.element( "exclude-output" ) );
                break;
            }
        }
*/
//        rewriteDependencies( objects );



        File projectFile = new File(projectDir, "project.pbxproj");
        try {
            PropertyListSerialization.serialize(propertyList, projectFile);
        } catch(Exception ex) {
            throw new MojoExecutionException("Unable to create " +
                    projectFile.getPath(), ex);
        }
        

    }


    private static PBXObjectRef createPBXToolTarget(
            final PBXObjectRef buildConfigurationList,
                                                    final List buildPhases,
                                                    final List dependencies,
                                                    final String name,
                                                    final String productInstallPath,
                                                    final String productName,
                                                    final PBXObjectRef productReference) {

        Map map = new HashMap();
        map.put("isa", "PBXToolTarget");
        map.put("buildConfigurationList", buildConfigurationList.getID());
        map.put("buildPhases", buildPhases);
        map.put("dependencies", dependencies);
        map.put("name", name);
        if (productInstallPath != null) {
            map.put("productInstallPath", productInstallPath);
        }
        if (productName != null) {
            map.put("productName", productName);
        }
        if (productReference != null) {
            map.put("productReference", productReference);
        }
        return new PBXObjectRef(map);
    }

    /**
     * Create PBXSourcesBuildPhase.
     * @param buildActionMask build action mask.
     * @param files source files.
     * @param runOnly if true, phase should only be run on deployment.
     * @return PBXSourcesBuildPhase.
     */
    private static PBXObjectRef createPBXSourcesBuildPhase(int buildActionMask,
                                                 List files,
                                                 boolean runOnly) {
        Map map = new HashMap();
        map.put("buildActionMask",
                String.valueOf(buildActionMask));
        map.put("files", files);
        map.put("isa", "PBXSourcesBuildPhase");
        map.put("runOnlyForDeploymentPostprocessing", runOnly ? "1" : "0");
        return new PBXObjectRef(map);
    }

    /**
     * Create PBXCopyFilesBuildPhase.
     * @param buildActionMask build action mask.
     * @param files source files.
     * @param runOnly if true, phase should only be run on deployment.
     * @return PBXSourcesBuildPhase.
     */
    private static PBXObjectRef createPBXCopyFilesBuildPhase(int buildActionMask,
                                                 final String dstPath,
                                                 final int dstSubfolderSpec,
                                                 List files,
                                                 boolean runOnly) {
        Map map = new HashMap();
        map.put("buildActionMask",
                String.valueOf(buildActionMask));
        map.put("files", files);
        map.put("dstPath", dstPath);
        map.put("dstSubfolderSpec", String.valueOf(dstSubfolderSpec));
        map.put("isa", "PBXCopyFilesBuildPhase");
        map.put("runOnlyForDeploymentPostprocessing", runOnly ? "1" : "0");
        return new PBXObjectRef(map);
    }

    /**
     * Create PBXFrameworksBuildPhase.
     * @param buildActionMask build action mask.
     * @param files source files.
     * @param runOnly if true, phase should only be run on deployment.
     * @return PBXSourcesBuildPhase.
     */
    private static PBXObjectRef createPBXFrameworksBuildPhase(int buildActionMask,
                                                 List files,
                                                 boolean runOnly) {
        Map map = new HashMap();
        map.put("buildActionMask",
                String.valueOf(buildActionMask));
        map.put("files", files);
        map.put("isa", "PBXFrameworksBuildPhase");
        map.put("runOnlyForDeploymentPostprocessing", runOnly ? "1" : "0");
        return new PBXObjectRef(map);
    }

    /**
     * Create PBXJavaArchiveBuildPhase.
     * @param buildActionMask build action mask.
     * @param files source files.
     * @param runOnly if true, phase should only be run on deployment.
     * @return PBXSourcesBuildPhase.
     */
    private static PBXObjectRef createPBXJavaArchiveBuildPhase(int buildActionMask,
                                                 List files,
                                                 boolean runOnly) {
        Map map = new HashMap();
        map.put("buildActionMask",
                String.valueOf(buildActionMask));
        map.put("files", files);
        map.put("isa", "PBXJavaArchiveBuildPhase");
        map.put("runOnlyForDeploymentPostprocessing", runOnly ? "1" : "0");
        return new PBXObjectRef(map);
    }

    /**
     * Create XCBuildConfiguration.
     * @param name name.
     * @param buildSettings build settings.
     * @return build configuration.
     */
    private static PBXObjectRef createXCBuildConfiguration(final String name,
                                                 final Map buildSettings) {
        Map map = new HashMap();
        map.put("isa", "XCBuildConfiguration");
        map.put("buildSettings", buildSettings);
        map.put("name", name);
        return new PBXObjectRef(map);
    }

    /**
     * Create XCConfigurationList.
     * @param buildConfigurations build configurations.
     * @return configuration list.
     */
    private static PBXObjectRef createXCConfigurationList(final List buildConfigurations) {
        Map map = new HashMap();
        map.put("isa", "XCConfigurationList");
        map.put("buildConfigurations", buildConfigurations);
        return new PBXObjectRef(map);
    }


    public PBXObjectRef addProjectConfigurationList(final Map objects,
                                                    final Map debugSettings,
                                                    final Map releaseSettings) {
        //
        //   Create a configuration list with
        //     two stock configurations: Debug and Release
        //
        List configurations = new ArrayList();
        PBXObjectRef debugConfig = createXCBuildConfiguration("Debug", debugSettings);
        objects.put(debugConfig.getID(), debugConfig.getProperties());
        configurations.add(debugConfig);

        PBXObjectRef releaseConfig =
                createXCBuildConfiguration("Release", releaseSettings);
        objects.put(releaseConfig.getID(), releaseConfig.getProperties());
        configurations.add(releaseConfig);
        PBXObjectRef configurationList = createXCConfigurationList(configurations);
        Map projectConfigurationListProperties = configurationList.getProperties();
        projectConfigurationListProperties.put("defaultConfigurationIsVisible", "0");
        projectConfigurationListProperties.put("defaultConfigurationName", "Debug");
        objects.put(configurationList.getID(), configurationList.getProperties());

        return configurationList;
        
    }

    /**
     * @TODO
     * @param propertyList
     */
    private static final void addWebModule(final Map propertyList) {

    }

    /**
     * @TODO
     * @param propertyList
     */
    private static final void addEarModule(final Map propertyList) {

    }

    /**
     * @TODO
     * @param propertyList
     */
    private static final void addEjbModule(final Map propertyList) {

    }

    /**
     * Create PBXProject.
     * @param buildConfigurationList build configuration list.
     * @param mainGroup main group.
     * @param projectDirPath project directory path.
     * @param targets targets.
     * @return project.
     */
    private static PBXObjectRef createPBXProject(final PBXObjectRef buildConfigurationList,
                                       final PBXObjectRef mainGroup,
                                       final String projectDirPath,
                                       final List targets) {
        Map map = new HashMap();
        map.put("isa", "PBXProject");
        map.put("buildConfigurationList", buildConfigurationList.getID());
        map.put("hasScannedForEncodings", "0");
        map.put("mainGroup", mainGroup.getID());
        map.put("projectDirPath", projectDirPath);
        map.put("targets", targets);
        return new PBXObjectRef(map);
    }


    /**
     * Create PBXGroup.
     * @param name group name.
     * @param sourceTree source tree.
     * @param children list of PBXFileReferences.
     * @return group.
     */
    private static PBXObjectRef createPBXGroup(final String name,
                                     final String sourceTree,
                                     final List children) {
        Map map = new HashMap();
        map.put("isa", "PBXGroup");
        map.put("name", name);
        map.put("sourceTree", sourceTree);
        map.put("children", children);
        return new PBXObjectRef(map);
    }


    private PBXObjectRef addPBXGroup(final Map objects,
                          final PBXObjectRef rootGroup,
                          final Map groups,
                          final String sourceTree,
                          final File directory) {
        PBXObjectRef group = (PBXObjectRef) groups.get(directory.getPath());
        if (group == null) {
            File parentDir = directory.getParentFile();
            PBXObjectRef parentGroup = rootGroup;
            if (parentDir != null) {
                parentGroup = addPBXGroup(objects, rootGroup, groups, sourceTree, parentDir);
            }
            group =  createPBXGroup(directory.getName(), sourceTree, new ArrayList());
            List children = (List) parentGroup.getProperties().get("children");
            children.add(group.getID());
            groups.put(directory.getPath(), group);
            objects.put(group.getID(), group.getProperties());
        }
        return group;
    }

    /**
     * Create PBXFileReference.
     * @param sourceTree source tree.
     * @param file file.
     * @return PBXFileReference object.
     */
    private static PBXObjectRef createPBXFileReference(final String sourceTree,
                                             final File file) {
        Map map = new HashMap();
        map.put("isa", "PBXFileReference");
        map.put("path", file.getPath());
        map.put("sourceTree", sourceTree);
        return new PBXObjectRef(map);
    }


    /**
     * Adds a sourceFolder element to Xcode project file.
     * @TODO
     *
     * @param objects   Xcode objects
     * @param directory Directory to set as url.
     * @param isTest    True if directory isTestSource.
     */
    private void addSourceFolder( final Map objects,
                                  final PBXObjectRef rootGroup,
                                  final Map groups,
                                  final String sourceTree,
                                  final List sourceBuildPhaseFiles,
                                  final String directory,
                                  final boolean isTest )
    {
        File sourceDir = new File(directory);
        if ( !StringUtils.isEmpty( directory ) && sourceDir.isDirectory() )
        {
              File baseDir = executedProject.getBasedir();
              File relDir = new File(toRelative(baseDir, directory));

              PBXObjectRef group = addPBXGroup(objects, rootGroup,
                      groups, sourceTree, relDir);
              List children = (List) group.getProperties().get("children");
              File[] files = sourceDir.listFiles();
              for (int i = 0; i < files.length; i++) {
                  File file = files[i];
                  if (!file.getName().startsWith(".")) {
                      if (file.isDirectory()) {
                          addSourceFolder(objects, rootGroup, groups,
                                  sourceTree, sourceBuildPhaseFiles,
                                  new File(relDir, file.getName()).getPath(), isTest);
                      } else {
                          String fileName = file.getName();
                          PBXObjectRef fileRef =
                                  createPBXFileReference(sourceTree,
                                         new File(relDir, fileName));
                          objects.put(fileRef.getID(), fileRef.getProperties());
                          children.add(fileRef.getID());
                          if (fileName.length() > 5 &&
                              fileName.lastIndexOf(".java") == fileName.length() - 5) {
                              PBXObjectRef buildFile = createPBXBuildFile(
                                      fileRef, Collections.EMPTY_MAP);
                              objects.put(buildFile.getID(), buildFile.getProperties());
                              sourceBuildPhaseFiles.add(buildFile.getID());
                              fileRef.getProperties().put("lastKnownFileType", "sourcecode.java");
                          }
                      }
                  }
              }
        }
    }

    /**
     * Create PBXBuildFile.
     * @param fileRef source file.
     * @param settings build settings.
     * @return PBXBuildFile.
     */
    private static PBXObjectRef createPBXBuildFile(PBXObjectRef fileRef,
                                         Map settings) {
        Map map = new HashMap();
        map.put("fileRef", fileRef);
        map.put("isa", "PBXBuildFile");
        if (settings != null) {
            map.put("settings", settings);
        }
        return new PBXObjectRef(map);
    }


    /**
     * @TODO
     * @param objects
     * @param name
     */
    private static void removeOldElements(final Map objects, final String name) {

    }

    private List getExcludedDirectories( File target, List excludeList, List sourceFolders )
    {
        List foundFolders = new ArrayList();

        int totalDirs = 0, excludedDirs = 0;

        if ( target.exists() && !excludeList.contains( target.getAbsolutePath() ) )
        {
            File[] files = target.listFiles();

            for ( int i = 0; i < files.length; i++ )
            {
                File file = files[i];
                if ( file.isDirectory() && !excludeList.contains( file.getAbsolutePath() ) )
                {
                    totalDirs++;

                    String absolutePath = file.getAbsolutePath();
                    String url = getModuleFileUrl( absolutePath );

                    boolean addToExclude = true;
                    for ( Iterator sources = sourceFolders.iterator(); sources.hasNext(); )
                    {
                        String source = ( (Element) sources.next() ).attributeValue( "url" );
                        if ( source.equals( url ) )
                        {
                            addToExclude = false;
                            break;
                        }
                        else if ( source.indexOf( url ) == 0 )
                        {
                            foundFolders.addAll(
                                getExcludedDirectories( new File( absolutePath ), excludeList, sourceFolders ) );
                            addToExclude = false;
                            break;
                        }
                    }
                    if ( addToExclude )
                    {
                        excludedDirs++;
                        foundFolders.add( absolutePath );
                    }
                }
            }

            //if all directories are excluded, then just exclude the parent directory
            if ( totalDirs > 0 && totalDirs == excludedDirs )
            {
                foundFolders.clear();

                foundFolders.add( target.getAbsolutePath() );
            }
        }
        else if ( !target.exists() )
        {
            //might as well exclude a non-existent dir so that it won't show when it suddenly appears
            foundFolders.add( target.getAbsolutePath() );
        }

        return foundFolders;
    }

    private void addExcludeFolder(Map content, String directory )
    {
//        Element excludeFolder = createElement( content, "excludeFolder" );
//        excludeFolder.addAttribute( "url", getModuleFileUrl( directory ) );
    }

    private void rewriteDependencies( Map component )
    {
        Map modulesByName = new HashMap();
        Map modulesByUrl = new HashMap();
        Set unusedModules = new HashSet();
//        for ( Iterator children = component.elementIterator( "orderEntry" ); children.hasNext(); )
        {
            Element orderEntry = null;//= (Element) children.next();

            String type = "" ;//orderEntry.attributeValue( "type" );
            if ( "module".equals( type ) )
            {
                modulesByName.put( orderEntry.attributeValue( "module-name" ), orderEntry );
            }
            else if ( "module-library".equals( type ) )
            {
                // keep track for later so we know what is left
                unusedModules.add( orderEntry );

                Element lib = orderEntry.element( "library" );
                String name = lib.attributeValue( "name" );
                if ( name != null )
                {
                    modulesByName.put( name, orderEntry );
                }
                else
                {
                    Element classesChild = lib.element( "CLASSES" );
                    if ( classesChild != null )
                    {
                        Element rootChild = classesChild.element( "root" );
                        if ( rootChild != null )
                        {
                            String url = rootChild.attributeValue( "url" );
                            if ( url != null )
                            {
                                // Need to ignore case because of Windows drive letters
                                modulesByUrl.put( url.toLowerCase(), orderEntry );
                            }
                        }
                    }
                }
            }
        }

        List testClasspathElements = executedProject.getTestArtifacts();
        for ( Iterator i = testClasspathElements.iterator(); i.hasNext(); )
        {
            Artifact a = (Artifact) i.next();

            Library library = findLibrary( a );
            if ( library != null && library.isExclude() )
            {
                continue;
            }

            String moduleName;
            if ( useFullNames )
            {
                moduleName = a.getGroupId() + ':' + a.getArtifactId() + ':' + a.getType() + ':' + a.getVersion();
            }
            else
            {
                moduleName = a.getArtifactId();
            }

            Element dep = (Element) modulesByName.get( moduleName );

            if ( dep == null )
            {
                // Need to ignore case because of Windows drive letters
                dep = (Element) modulesByUrl.get( getLibraryUrl( a ).toLowerCase() );
            }

            if ( dep != null )
            {
                unusedModules.remove( dep );
            }
            else
            {
//                dep = createElement( component, "orderEntry" );
            }

            boolean isIdeaModule = false;
            if ( linkModules )
            {
                isIdeaModule = isReactorProject( a.getGroupId(), a.getArtifactId() );

                if ( isIdeaModule )
                {
                    dep.addAttribute( "type", "module" );
                    dep.addAttribute( "module-name", moduleName );
                }
            }

            if ( a.getFile() != null && !isIdeaModule )
            {
                dep.addAttribute( "type", "module-library" );

                Element lib = dep.element( "library" );

                if ( lib == null )
                {
                    lib = createElement( dep, "library" );
                }

                if ( dependenciesAsLibraries )
                {
                    lib.addAttribute( "name", moduleName );
                }

                // replace classes
                removeOldElements( lib, "CLASSES" );
                Element classes = createElement( lib, "CLASSES" );
                if ( library != null && library.getSplitClasses().length > 0 )
                {
                    lib.addAttribute( "name", moduleName );
                    String[] libraryClasses = library.getSplitClasses();
                    for ( int k = 0; k < libraryClasses.length; k++ )
                    {
                        String classpath = libraryClasses[k];
                        extractMacro( classpath );
                        Element classEl = createElement( classes, "root" );
                        classEl.addAttribute( "url", classpath );
                    }
                }
                else
                {
                    createElement( classes, "root" ).addAttribute( "url", getLibraryUrl( a ) );
                }

                if ( library != null && library.getSplitSources().length > 0 )
                {
                    removeOldElements( lib, "SOURCES" );
                    Element sourcesElement = createElement( lib, "SOURCES" );
                    String[] sources = library.getSplitSources();
                    for ( int k = 0; k < sources.length; k++ )
                    {
                        String source = sources[k];
                        extractMacro( source );
                        Element sourceEl = createElement( sourcesElement, "root" );
                        sourceEl.addAttribute( "url", source );
                    }
                }
                else if ( downloadSources )
                {
//                    resolveClassifier( createOrGetElement( lib, "SOURCES" ), a, sourceClassifier );
                }

                if ( library != null && library.getSplitJavadocs().length > 0 )
                {
                    removeOldElements( lib, "JAVADOC" );
                    Element javadocsElement = createElement( lib, "JAVADOC" );
                    String[] javadocs = library.getSplitJavadocs();
                    for ( int k = 0; k < javadocs.length; k++ )
                    {
                        String javadoc = javadocs[k];
                        extractMacro( javadoc );
                        Element sourceEl = createElement( javadocsElement, "root" );
                        sourceEl.addAttribute( "url", javadoc );
                    }
                }
                else if ( downloadJavadocs )
                {
//                    resolveClassifier( createOrGetElement( lib, "JAVADOC" ), a, javadocClassifier );
                }
            }
        }

        for ( Iterator i = unusedModules.iterator(); i.hasNext(); )
        {
            Element orderEntry = (Element) i.next();

            component.remove( orderEntry );
        }
    }

    private String getLibraryUrl( Artifact artifact )
    {
        return "jar://" + artifact.getFile().getAbsolutePath().replace( '\\', '/' ) + "!/";
    }

    private Library findLibrary( Artifact a )
    {
        if ( libraries != null )
        {
            for ( int j = 0; j < libraries.length; j++ )
            {
                Library library = libraries[j];
                if ( a.getArtifactId().equals( library.getName() ) )
                {
                    return library;
                }
            }
        }

        return null;
    }



    private void extractMacro( String path )
    {
/*
        if ( macros != null )
        {
            Pattern p = Pattern.compile( ".*\\$([^\\$]+)\\$.*" );
            Matcher matcher = p.matcher( path );
            while ( matcher.find() )
            {
                String macro = matcher.group( 1 );
                macros.add( macro );
            }
        }
*/
    }



    private void resolveClassifier( Map element, Artifact a, String classifier )
    {
        String id = a.getId() + '-' + classifier;

        String path = null;
/*
        if ( attemptedDownloads.containsKey( id ) )
        {
            getLog().debug( id + " was already downloaded." );
            path = (String) attemptedDownloads.get( id );
        }
        else
        {
            getLog().debug( id + " was not attempted to be downloaded yet: trying..." );
            path = resolveClassifiedArtifact( a, classifier );
            attemptedDownloads.put( id, path );
        }
*/

        if ( path != null )
        {
            String jarPath = "jar://" + path + "!/";
            getLog().debug( "Setting " + classifier + " for " + id + " to " + jarPath );
            removeOldElements( element, "root" );
//            createElement( element, "root" ).addAttribute( "url", jarPath );
        }
    }

    private String resolveClassifiedArtifact( Artifact artifact, String classifier )
    {
        String basePath = artifact.getFile().getAbsolutePath().replace( '\\', '/' );
        int delIndex = basePath.indexOf( ".jar" );
        if ( delIndex < 0 )
        {
            return null;
        }

        List remoteRepos = executedProject.getRemoteArtifactRepositories();
        try
        {
            Artifact classifiedArtifact = artifactFactory.createArtifactWithClassifier( artifact.getGroupId(),
                                                                                        artifact.getArtifactId(),
                                                                                        artifact.getVersion(),
                                                                                        artifact.getType(),
                                                                                        classifier );
            String dstFilename = basePath.substring( 0, delIndex ) + '-' + classifier + ".jar";
            File dstFile = new File( dstFilename );
            classifiedArtifact.setFile( dstFile );
            //this check is here because wagonManager does not seem to check if the remote file is newer
            //    or such feature is not working
            if ( !dstFile.exists() )
            {
                wagonManager.getArtifact( classifiedArtifact, remoteRepos );
            }
            return dstFile.getAbsolutePath().replace( '\\', '/' );
        }
        catch ( TransferFailedException e )
        {
            getLog().debug( e );
            return null;
        }
        catch ( ResourceDoesNotExistException e )
        {
            getLog().debug( e );
            return null;
        }
    }



    /**
     * Translate the relative path of the file into module path
     *
     * @param basedir File to use as basedir
     * @param path    Absolute path string to translate to ModuleFileUrl
     * @return moduleFileUrl Translated Module File URL
     */
    private String getModuleFileUrl( File basedir, String path )
    {
        return "file://$MODULE_DIR$/" + toRelative( basedir, path );
    }

    private String getModuleFileUrl( String file )
    {
        return getModuleFileUrl( executedProject.getBasedir(), file );
    }

    private boolean isReactorProject( String groupId, String artifactId )
    {
        if ( reactorProjects != null )
        {
            for ( Iterator j = reactorProjects.iterator(); j.hasNext(); )
            {
                MavenProject p = (MavenProject) j.next();
                if ( p.getGroupId().equals( groupId ) && p.getArtifactId().equals( artifactId ) )
                {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Represents a property map with an 96 bit identity.
     * When placed in a property list, this object will
     * output the string representation of the identity
     * which XCode uses to find the corresponding property
     * bag in the "objects" property of the top-level property list.
     */
    private static final class PBXObjectRef {
        /**
         * Identifier.
         */
        private final String id;
        /**
         * Properties.
         */
        private final Map properties;
        /**
         * Next available identifier.
         */
        private static int nextID = 0;

        /**
         * Create reference.
         * @param props properties.
         */
        public PBXObjectRef(final Map props) {
            if (props == null) {
                throw new NullPointerException("props");
            }
            StringBuffer buf = new StringBuffer("000000000000000000000000");
            String idStr = Integer.toHexString(nextID++);
            buf.replace(buf.length() - idStr.length(), buf.length(), idStr);
            id = buf.toString();
            properties = props;
        }

        /**
         * Get object identifier.
         * @return identifier.
         */
        public String toString() {
            return id;
        }

        /**
         * Get object identifier.
         * @return object identifier.
         */
        public String getID() {
            return id;
        }

        /**
         * Get properties.
         * @return properties.
         */
        public Map getProperties() {
            return properties;
        }
    }



    public void setProject( MavenProject project )
    {
        this.executedProject = project;
    }
}
