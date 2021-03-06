/*
* Copyright 2006 The Apache Software Foundation.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.myfaces.trinidadbuild.plugin.jdeveloper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.IOUtil;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.codehaus.plexus.util.xml.Xpp3DomWriter;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;


/**
 * @version $Id$
 * @goal jdev
 * @execute phase=process-resources
 * @requiresDependencyResolution test
 * @description Goal which generates the JDeveloper Workspace
 */
public class JDeveloperMojo extends AbstractMojo
{
  /**
   * @parameter
   */
  private String[] libraries;

  /**
   * @parameter
   */
  private File[] sourceRoots;

  /**
   * @parameter
   */
  private File[] testSourceRoots;

  /**
   * @parameter
   */
  private File[] resourceRoots;

  /**
   * @parameter
   */
  private File[] testResourceRoots;

  /**
   * @parameter expression="${force}"
   */
  private boolean force;

  /**
   * @parameter expression="10.1.3.0.4"
   * @required
   * @readonly
   */
  private String release;

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * @parameter expression="${reactorProjects}"
   * @required
   * @readonly
   */
  private List reactorProjects;

  /**
   * Execute the Mojo.
   */
  public void execute() throws MojoExecutionException
  {
    try
    {
      generateWorkspace();
      generateProject();
      generateTestProject();
    }
    catch (IOException e)
    {
      throw new MojoExecutionException(e.getMessage());
    }
  }

  private void generateWorkspace() throws MojoExecutionException, IOException
  {
    if (!project.getCollectedProjects().isEmpty())
    {
      getLog().info("Generating JDeveloper " + release +
                    " workspace: " + project.getArtifactId());

      File workspaceFile = getJWorkspaceFile(project);

      try
      {
        Xpp3Dom workspaceDOM = readWorkspaceDOM(workspaceFile);
        replaceProjects(workspaceFile.getParentFile(), workspaceDOM);
        writeDOM(workspaceFile, workspaceDOM);
      }
      catch (XmlPullParserException e)
      {
        throw new MojoExecutionException("Error generating project", e);
      }
    }
  }

  private void generateProject() throws IOException, MojoExecutionException
  {
    if (!"pom".equals(project.getPackaging()))
    {
      File projectFile = getJProjectFile(project);
      // TODO: read configuration for war:war goal
      File webappDir = new File(project.getBasedir(), "src/main/webapp");
      // TODO: read configuration for compiler:complie goal
      File outputDir = new File(project.getBuild().getDirectory(), "classes");

      MavenProject executionProject = project.getExecutionProject();
      List compileSourceRoots = executionProject.getCompileSourceRoots();
      if (sourceRoots != null)
      {
        for (int i=0; i < sourceRoots.length; i++)
        {
          compileSourceRoots.add(sourceRoots[i].getAbsolutePath());
        }
      }

      List compileResourceRoots = executionProject.getResources();
      if (resourceRoots != null)
      {
        for (int i=0; i < resourceRoots.length; i++)
        {
          Resource resource = new Resource();
          resource.setDirectory(resourceRoots[i].getAbsolutePath());
          compileResourceRoots.add(resource);
        }
      }

      getLog().info("Generating JDeveloper " + release +
                    " Project " + project.getArtifactId());

      // Note: include "compile", "provided", "system" and "runtime" scopes
      Set compileArtifacts = new LinkedHashSet();
      compileArtifacts.addAll(project.getCompileArtifacts());
      compileArtifacts.addAll(project.getRuntimeArtifacts());

      // Note: separate "runtime" vs. "compile" dependencies in JDeveloper?
      generateProject(projectFile,
                      project.getArtifactId(),
                      project.getPackaging(),
                      project.getDependencies(),
                      new ArrayList(compileArtifacts),
                      compileSourceRoots,
                      compileResourceRoots,
                      Collections.singletonList(webappDir.getPath()),
                      outputDir);
    }
  }

  private void generateTestProject() throws IOException, MojoExecutionException
  {
    if (!"pom".equals(project.getPackaging()))
    {
      File projectFile = getJProjectTestFile(project);
      File webappDir = new File(project.getBasedir(), "src/test/webapp");
      // TODO: read configuration for compiler:testCompile goal
      File outputDir = new File(project.getBuild().getDirectory(), "test-classes");

      // self dependency needed for test project
      List testDependencies = new ArrayList(project.getTestDependencies());
      Dependency selfDependency = new Dependency();
      selfDependency.setArtifactId(project.getArtifactId());
      selfDependency.setGroupId(project.getGroupId());
      selfDependency.setType(project.getPackaging());
      testDependencies.add(selfDependency);

      MavenProject executionProject = project.getExecutionProject();
      List compileSourceRoots = executionProject.getTestCompileSourceRoots();
      if (testSourceRoots != null)
      {
        for (int i=0; i < testSourceRoots.length; i++)
        {
          compileSourceRoots.add(testSourceRoots[i].getAbsolutePath());
        }
      }

      List compileResourceRoots = executionProject.getTestResources();
      if (testResourceRoots != null)
      {
        for (int i=0; i < testResourceRoots.length; i++)
        {
          Resource resource = new Resource();
          resource.setDirectory(testSourceRoots[i].getAbsolutePath());
          compileResourceRoots.add(resource);
        }
      }

      getLog().info("Generating JDeveloper " + release +
                    " Project " + project.getArtifactId() + "-test");

      // Note: all artifacts implicitly included in "test" scope
      generateProject(projectFile,
                      project.getArtifactId() + "-test",
                      project.getPackaging(),
                      testDependencies,
                      project.getTestArtifacts(),
                      compileSourceRoots,
                      compileResourceRoots,
                      Collections.singletonList(webappDir.getPath()),
                      outputDir);
    }
  }

  private void generateProject(
    File   projectFile,
    String projectName,
    String packaging,
    List   dependencies,
    List   artifacts,
    List   sourceRoots,
    List   resourceRoots,
    List   webSourceRoots,
    File   outputDir) throws IOException, MojoExecutionException
  {
    try
    {
      File projectDir = projectFile.getParentFile();
      Xpp3Dom projectDOM = readProjectDOM(projectFile);
      replaceWebappInfo(projectName, projectDOM);
      replaceSourcePaths(projectDir, sourceRoots, resourceRoots, projectDOM);
      //replaceResourcePaths(projectDir, resourceRoots, projectDOM);
      replaceWebSourcePaths(projectDir, webSourceRoots, projectDOM);
      replaceDependencies(projectDir, dependencies, projectDOM);
      replaceLibraries(projectDir, artifacts, projectDOM);
      replaceOutputDirectory(projectDir, outputDir, projectDOM);
      writeDOM(projectFile, projectDOM);

      if ("war".equals(packaging))
        copyTagLibraries(projectDir, dependencies, artifacts);
    }
    catch (XmlPullParserException e)
    {
      throw new MojoExecutionException("Error generating project", e);
    }
  }

  private void replaceProjects(
    File    workspaceDir,
    Xpp3Dom workspaceDOM) throws XmlPullParserException
  {
    // /jws:workspace
    //   /list[@n="listOfChildren"]
    Xpp3Dom sourceDOM = workspaceDOM.getChild("list");

    // <hash>
    //   <value n="nodeClass" v="oracle.jdeveloper.model.JProject"/>
    //   <url n="URL" path="[workspace-relative-path-to-project.jpr]"/>
    // </hash>
    Xpp3Dom targetDOM = new Xpp3Dom("list");

    for (Iterator i = project.getCollectedProjects().iterator(); i.hasNext();)
    {
      MavenProject collectedProject = (MavenProject)i.next();

      File projectFile = getJProjectFile(collectedProject);
      targetDOM.addChild(createProjectReferenceDOM(workspaceDir, projectFile));

      File testProjectFile = getJProjectTestFile(collectedProject);
      targetDOM.addChild(createProjectReferenceDOM(workspaceDir, testProjectFile));
    }

    // TODO: use a better merge algorithm
    removeChildren(sourceDOM);

    // make sure to pass Boolean.FALSE to allow
    // multiple child elements with the same name
    Xpp3Dom.mergeXpp3Dom(sourceDOM, targetDOM, Boolean.FALSE);
  }

  private void replaceWebSourcePaths(
    File    projectDir,
    List    sourceRoots,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.jdeveloper.model.J2eeSettings"]
    //     /hash[@n="webContentSet"]
    //       /list[@n="url-path"]
    Xpp3Dom pathsDOM =
      findNamedChild(projectDOM, "hash", "oracle.jdeveloper.model.J2eeSettings");
    Xpp3Dom contentSetDOM =
      findNamedChild(pathsDOM, "hash", "webContentSet");
    Xpp3Dom sourceDOM =
      findNamedChild(contentSetDOM, "list", "url-path");

    //
    // <url path="[relative-path-to-source-root]" />
    //
    Xpp3Dom targetDOM = new Xpp3Dom("list");
    for (Iterator i = sourceRoots.iterator(); i.hasNext();)
    {
      File sourceRoot = new File((String)i.next());
      String relativeRoot = getRelativeDir(projectDir, sourceRoot);
      Xpp3Dom urlDOM = new Xpp3Dom("url");
      urlDOM.setAttribute("path", relativeRoot);
      targetDOM.addChild(urlDOM);
    }

    // TODO: use a better merge algorithm
    removeChildren(sourceDOM);

    // make sure to pass Boolean.FALSE to allow
    // multiple child elements with the same name
    Xpp3Dom.mergeXpp3Dom(sourceDOM, targetDOM, Boolean.FALSE);
  }

  private void replaceWebappInfo(
    String  projectName,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.jdeveloper.model.J2eeSettings"]
    //     /value[@n="j2eeWebAppName" v="maven-generated-webapp"]
    //     /value[@n="j2eeWebContextRoot" v="maven-generated-context-root"]
     Xpp3Dom settingsDOM =
       findNamedChild(projectDOM, "hash", "oracle.jdeveloper.model.J2eeSettings");
    Xpp3Dom webappNameDOM =
      findNamedChild(settingsDOM, "value", "j2eeWebAppName");
    Xpp3Dom webappContextDOM =
      findNamedChild(settingsDOM, "value", "j2eeWebContextRoot");

    // update the webapp name
    webappNameDOM.setAttribute("v", projectName + "-webapp");

    // update the webapp context root
    webappContextDOM.setAttribute("v", projectName + "-context-root");
  }

  private void replaceSourcePaths(
    File    projectDir,
    List    sourceRoots,
    List    resourceRoots,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.jdeveloper.model.PathsConfiguration"]
    //     /hash[@n="javaContentSet"]
    //       /list[@n="url-path"]
    Xpp3Dom pathsDOM =
      findNamedChild(projectDOM, "hash", "oracle.jdeveloper.model.PathsConfiguration");
    Xpp3Dom contentSetDOM =
      findNamedChild(pathsDOM, "hash", "javaContentSet");
    Xpp3Dom sourceDOM =
      findNamedChild(contentSetDOM, "list", "url-path");

    //
    // <url path="[relative-path-to-source-root]" />
    //
    Xpp3Dom targetDOM = new Xpp3Dom("list");
    Collections.sort(sourceRoots);
    for (Iterator i = sourceRoots.iterator(); i.hasNext();)
    {
      File sourceRoot = new File((String)i.next());
      String relativeRoot = getRelativeDir(projectDir, sourceRoot);
      Xpp3Dom urlDOM = new Xpp3Dom("url");
      urlDOM.setAttribute("path", relativeRoot);
      targetDOM.addChild(urlDOM);
    }

    // TODO: get bug fixed in 10.1.3 for copying resources
    Collections.sort(resourceRoots, new Comparator()
    {
      public int compare(Object a, Object b)
      {
        Resource ra = (Resource)a;
        Resource rb = (Resource)b;
        return ra.getDirectory().compareTo(rb.getDirectory());
      }

    });
    for (Iterator i = resourceRoots.iterator(); i.hasNext();)
    {
      Resource resource = (Resource)i.next();
      File resourceRoot = new File(resource.getDirectory());
      String relativeRoot = getRelativeDir(projectDir, resourceRoot);
      Xpp3Dom urlDOM = new Xpp3Dom("url");
      urlDOM.setAttribute("path", relativeRoot);
      targetDOM.addChild(urlDOM);
    }

    // TODO: use a better merge algorithm
    removeChildren(sourceDOM);

    // make sure to pass Boolean.FALSE to allow
    // multiple child elements with the same name
    Xpp3Dom.mergeXpp3Dom(sourceDOM, targetDOM, Boolean.FALSE);
  }

  private void replaceResourcePaths(
    File    projectDir,
    List    resourceRoots,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.ide.model.ResourcePaths"]
    //     /hash[@n="resourcesContentSet"]
    //       /list[@n="url-path"]

    Xpp3Dom pathsDOM =
      findNamedChild(projectDOM, "hash", "oracle.ide.model.ResourcePaths");
    Xpp3Dom contentSetDOM =
      findNamedChild(pathsDOM, "hash", "resourcesContentSet");
    Xpp3Dom sourceDOM =
      findNamedChild(contentSetDOM, "list", "url-path");

    //
    // <url path="[relative-path-to-source-root]" />
    //
    Xpp3Dom targetDOM = new Xpp3Dom("list");
    for (Iterator i = resourceRoots.iterator(); i.hasNext();)
    {
      Resource resource = (Resource)i.next();
      File resourceRoot = new File(resource.getDirectory());
      String relativeRoot = getRelativeDir(projectDir, resourceRoot);
      Xpp3Dom urlDOM = new Xpp3Dom("url");
      urlDOM.setAttribute("path", relativeRoot);
      targetDOM.addChild(urlDOM);
    }

    // TODO: use a better merge algorithm
    removeChildren(sourceDOM);

    // make sure to pass Boolean.FALSE to allow
    // multiple child elements with the same name
    Xpp3Dom.mergeXpp3Dom(sourceDOM, targetDOM, Boolean.FALSE);
  }

  private void replaceDependencies(
    File    projectDir,
    List    dependencies,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.ide.model.DependencyConfiguration"]
    //     /list[@n="dependencyList"]
    Xpp3Dom configDOM =
      findNamedChild(projectDOM, "hash", "oracle.ide.model.DependencyConfiguration");
    Xpp3Dom sourceDOM =
      findNamedChild(configDOM, "list", "dependencyList");

    Xpp3Dom targetDOM = new Xpp3Dom("list");
    for (Iterator i = dependencies.iterator(); i.hasNext();)
    {
      Dependency dependency = (Dependency)i.next();
      MavenProject dependentProject = findDependentProject(dependency.getManagementKey());
      if (dependentProject != null)
      {
        File dependentProjectFile = getJProjectFile(dependentProject);
        String relativePath = getRelativeFile(projectDir, dependentProjectFile);

        Xpp3Dom hashDOM = new Xpp3Dom("hash");
        Xpp3Dom valueDOM = new Xpp3Dom("value");
        valueDOM.setAttribute("n", "class");
        valueDOM.setAttribute("v", "oracle.jdeveloper.library.ProjectLibrary");
        Xpp3Dom urlDOM = new Xpp3Dom("url");
        urlDOM.setAttribute("n", "sourceURL");
        urlDOM.setAttribute("path", relativePath);
        hashDOM.addChild(valueDOM);
        hashDOM.addChild(urlDOM);
        targetDOM.addChild(hashDOM);
      }
    }

    // TODO: use a better merge algorithm
    removeChildren(sourceDOM);

    // make sure to pass Boolean.FALSE to allow
    // multiple child elements with the same name
    Xpp3Dom.mergeXpp3Dom(sourceDOM, targetDOM, Boolean.FALSE);
  }

  private void replaceLibraries(
    File    projectDir,
    List    artifacts,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.jdevimpl.config.JProjectLibraries"]
    //     /hash[@n="internalDefinitions"]
    //       /list[@n="libraryDefinitions"]
    Xpp3Dom projectLibsDOM =
      findNamedChild(projectDOM, "hash", "oracle.jdevimpl.config.JProjectLibraries");
    Xpp3Dom internalDefsDOM =
      findNamedChild(projectLibsDOM, "hash", "internalDefinitions");
    Xpp3Dom sourceDefsDOM =
      findNamedChild(internalDefsDOM, "list", "libraryDefinitions");

    // /jpr:project
    //   /hash[@n="oracle.jdevimpl.config.JProjectLibraries"]
    //     /list[@n="libraryReferences"]
    Xpp3Dom sourceRefsDOM =
      findNamedChild(projectLibsDOM, "list", "libraryReferences");

    Xpp3Dom targetDefsDOM = new Xpp3Dom("list");
    Xpp3Dom targetRefsDOM = new Xpp3Dom("list");

    //
    // libraryDefinitions
    //
    // <hash>
    //   <list n="classPath">
    //     <url path="[path-to-artifact]" jar-entry="" />
    //   </list>
    //   <value n="deployedByDefault" v="true"/>
    //   <value n="description" v="[artifact.id]"/>
    //   <value n="id" v="[artifact.id]"/>
    // </hash>
    //

     // sort the artifacts
     Collections.sort(artifacts, new Comparator() {
       public int compare(Object a, Object b)
       {
         Artifact arta = (Artifact)a;
         Artifact artb = (Artifact)b;
         return arta.getId().compareTo(artb.getId());
       }

     });

    List libraryRefs = new LinkedList();
    for (Iterator i = artifacts.iterator(); i.hasNext();)
    {
      Artifact artifact = (Artifact)i.next();
      if (!isDependentProject(artifact.getDependencyConflictId()))
      {
        String id = artifact.getId();
        String path = getRelativeFile(projectDir, artifact.getFile());

        // libraryReferences entry
        Xpp3Dom hashDOM = new Xpp3Dom("hash");
        Xpp3Dom listDOM = new Xpp3Dom("list");
        listDOM.setAttribute("n", "classPath");

        Xpp3Dom urlDOM = new Xpp3Dom("url");
        urlDOM.setAttribute("path", path);
        urlDOM.setAttribute("jar-entry", "");
        listDOM.addChild(urlDOM);
        hashDOM.addChild(listDOM);

        Xpp3Dom valueDOM = new Xpp3Dom("value");
        valueDOM.setAttribute("n", "deployedByDefault");
        valueDOM.setAttribute("v", "true");
        hashDOM.addChild(valueDOM);

        valueDOM = new Xpp3Dom("value");
        valueDOM.setAttribute("n", "description");
        valueDOM.setAttribute("v", id);
        hashDOM.addChild(valueDOM);

        valueDOM = new Xpp3Dom("value");
        valueDOM.setAttribute("n", "id");
        valueDOM.setAttribute("v", id);
        hashDOM.addChild(valueDOM);
        targetDefsDOM.addChild(hashDOM);

        libraryRefs.add(id);
      }
    }

    // add manually defined libraries
    if (libraries != null)
    {
      for (int i=0; i < libraries.length; i++)
      {
        libraryRefs.add(0, libraries[i]);
      }
    }

    //
    // libraryReferences
    //
    // <hash>
    //   <value n="id" v="[artifact.id]"/>
    //   <value n="isJDK" v="false"/>
    // </hash>
    //
    Collections.sort(libraryRefs);
    for (Iterator i = libraryRefs.iterator(); i.hasNext();)
    {
      String id = (String)i.next();

      // libraryDefinitions entry
      Xpp3Dom hashDOM = new Xpp3Dom("hash");

      Xpp3Dom valueDOM = new Xpp3Dom("value");
      valueDOM.setAttribute("n", "id");
      valueDOM.setAttribute("v", id);
      hashDOM.addChild(valueDOM);

      valueDOM = new Xpp3Dom("value");
      valueDOM.setAttribute("n", "isJDK");
      valueDOM.setAttribute("v", "false");
      hashDOM.addChild(valueDOM);
      targetRefsDOM.addChild(hashDOM);
    }

    // First, add JSP Runtime dependency if src/main/webapp exists
    // TODO: use a better merge algorithm
    removeChildren(sourceDefsDOM);
    removeChildren(sourceRefsDOM);

    // make sure to pass Boolean.FALSE to allow
    // multiple child elements with the same name
    Xpp3Dom.mergeXpp3Dom(sourceDefsDOM, targetDefsDOM, Boolean.FALSE);
    Xpp3Dom.mergeXpp3Dom(sourceRefsDOM, targetRefsDOM, Boolean.FALSE);
  }

  private void copyTagLibraries(
    File projectDir,
    List dependencies,
    List artifacts) throws IOException
  {
    File targetDir = new File(projectDir, "src/main/webapp/WEB-INF");

    for (Iterator i = dependencies.iterator(); i.hasNext();)
    {
      Dependency dependency = (Dependency)i.next();
      MavenProject dependentProject = findDependentProject(dependency.getManagementKey());
      if (dependentProject != null)
      {
        List resourceRoots = dependentProject.getResources();
        for (Iterator j = resourceRoots.iterator(); j.hasNext();)
        {
          Resource resource = (Resource)j.next();
          String resourceRoot = resource.getDirectory();
          File resourceDirectory = new File(resourceRoot);
          if (resourceDirectory.exists())
          {
            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(resourceRoot);
            scanner.addDefaultExcludes();
            scanner.setIncludes(new String[] { "META-INF/*.tld" });
            scanner.scan();

            String[] tldFiles = scanner.getIncludedFiles();
            for (int k=0; k < tldFiles.length; k++)
            {
              File sourceFile = new File(resourceDirectory, tldFiles[k]);
              File targetFile = new File(targetDir, sourceFile.getName());

              if (targetFile.exists())
                targetFile.delete();
              FileUtils.copyFile(sourceFile, targetFile);
            }
          }
        }
      }
    }

    for (Iterator i = artifacts.iterator(); i.hasNext();)
    {
      Artifact artifact = (Artifact)i.next();
      if (!isDependentProject(artifact.getDependencyConflictId()) &&
          "jar".equals(artifact.getType()))
      {
        File file = artifact.getFile();
        JarFile jarFile = new JarFile(file);
        Enumeration jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements())
        {
          JarEntry jarEntry = (JarEntry)jarEntries.nextElement();
          String name = jarEntry.getName();
          if (name.startsWith("META-INF/") && name.endsWith(".tld"))
          {
            File sourceFile = new File(name);
            File targetFile = new File(targetDir, sourceFile.getName());
            URL jarURL = file.toURL();
            URL sourceURL = new URL("jar:" + jarURL.toExternalForm() + "!/" + name);
            if (targetFile.exists())
              targetFile.delete();
            FileUtils.copyURLToFile(sourceURL, targetFile);
            targetFile.setReadOnly();
          }
        }
      }
    }
  }

  private void replaceOutputDirectory(
    File    projectDir,
    File    outputDir,
    Xpp3Dom projectDOM) throws XmlPullParserException
  {
    // /jpr:project
    //   /hash[@n="oracle.jdevimpl.config.JProjectPaths"]
    //       /url[@n="outputDirectory"]

    Xpp3Dom projectPathsDOM =
      findNamedChild(projectDOM, "hash", "oracle.jdevimpl.config.JProjectPaths");
    Xpp3Dom sourceDOM =
      findNamedChild(projectPathsDOM, "url", "outputDirectory");

    //
    // <url path="[relative-path-to-output-dir]" />
    //
    sourceDOM.setAttribute("path", getRelativeDir(projectDir, outputDir));
  }

  /**
   * Returns the JDeveloper project file for a Maven POM.
   *
   * @param project  the Maven POM
   *
   * @return  the JDeveloper project file
   */
  private File getJProjectFile(
    MavenProject project)
  {
    String jprName = project.getArtifactId() + ".jpr";
    return new File(project.getBasedir(), jprName);
  }

  /**
   * Returns the JDeveloper test project file for a Maven POM.
   *
   * @param project  the Maven POM
   *
   * @return  the JDeveloper test project file
   */
  private File getJProjectTestFile(
    MavenProject project)
  {
    String jprName = project.getArtifactId() + "-test.jpr";
    return new File(project.getBasedir(), jprName);
  }

  /**
   * Returns the JDeveloper workspace file for a Maven POM.
   *
   * @param project  the Maven POM
   *
   * @return  the JDeveloper workspace file
   */
  private File getJWorkspaceFile(
    MavenProject project)
  {
    String jwsName = project.getArtifactId() + ".jws";
    return new File(project.getBasedir(), jwsName);
  }

  /**
   * Reads a JDeveloper workspace file into DOM.
   *
   * @param workspaceFile  the JDeveloper workspace file
   *
   * @return the parsed DOM
   */
  private Xpp3Dom readWorkspaceDOM(
    File workspaceFile) throws IOException, XmlPullParserException
  {
    return readDOM(workspaceFile, release + "/workspace.xml");
  }

  /**
   * Reads a JDeveloper project file into DOM.
   *
   * @param projectFile  the JDeveloper project file
   *
   * @return the parsed DOM
   */
  private Xpp3Dom readProjectDOM(
    File projectFile) throws IOException, XmlPullParserException
  {
    return readDOM(projectFile, release + "/project.xml");
  }

  /**
   * Reads a source file into DOM, defaulting to a packaged resource if
   * the source file does not already exist.
   *
   * @param sourceFile    the source file to be parsed
   * @param resourcePath  the default packaged resource
   *
   * @return the parsed DOM
   */
  private Xpp3Dom readDOM(
    File   sourceFile,
    String resourcePath) throws IOException, XmlPullParserException
  {
    Reader reader = null;
    try
    {
      if (!force && sourceFile.exists())
      {
        // parse the existing source file
        reader = new FileReader(sourceFile);
      }
      else
      {
        // parse the default resource file
        URL resource = getClass().getResource(resourcePath);

        // ensure that the resourcePath can be found
        if (resource == null)
          throw new IOException("Unable to read resource: " + resourcePath);

        reader = new InputStreamReader(resource.openStream());
      }
      return Xpp3DomBuilder.build(reader);
    }
    finally
    {
      IOUtil.close(reader);
    }
  }

  /**
   * Writes a XML DOM to the target file.
   *
   * @param targetFile  the target file
   * @param sourceDOM   the source DOM
   */
  private void writeDOM(
    File    targetFile,
    Xpp3Dom sourceDOM) throws IOException, XmlPullParserException
  {
    FileWriter writer = null;
    try
    {
      writer = new FileWriter(targetFile);
      Xpp3DomWriter.write(writer, sourceDOM);
    }
    finally
    {
      IOUtil.close(writer);
    }
  }

  private String getRelativeDir(
    File source,
    File target)
  {
    return getRelativePath(source, target, true);
  }

  private String getRelativeFile(
    File source,
    File target)
  {
    return getRelativePath(source, target, false);
  }

  /**
   * Returns the relative path between two files.
   *
   * @param source  the source file
   * @param target  the target file
   *
   * @return  the relative path between two files
   */
  private String getRelativePath(
    File    source,
    File    target,
    boolean isDirectory)
  {
    String sourcePath = source.getAbsolutePath();
    String targetPath = target.getAbsolutePath();

    if (targetPath.startsWith(sourcePath + File.separatorChar))
    {
      String relativePath = targetPath.substring(sourcePath.length() + 1);
      relativePath = relativePath.replace(File.separatorChar, '/');
      if (isDirectory)
        relativePath += "/";
      return relativePath;
    }
    else
    {
      String[] sourcePaths = sourcePath.split("\\" + File.separator);
      String[] targetPaths = targetPath.split("\\" + File.separator);

      // On Windows, the first element in the absolute path is a drive letter
      if(System.getProperty("os.name").startsWith("Windows"))
      {
        // uppercase the drive letter because Cygwin sometimes delivers
        // a lowercase drive letter
        sourcePaths[0] = sourcePaths[0].toUpperCase();
        targetPaths[0] = targetPaths[0].toUpperCase();
      }

      int sourcePathCount = sourcePaths.length;
      int targetPathCount = targetPaths.length;
      int commonPathCount = 0;

      int minPathCount = Math.min(sourcePathCount, targetPathCount);
      for (int i=0; i < minPathCount; i++)
      {
        if (sourcePaths[i].equals(targetPaths[i]))
          commonPathCount++;
      }

      if (commonPathCount > 0)
      {
        int sourceRelativePathCount = sourcePathCount - commonPathCount;
        int targetRelativePathCount = targetPathCount - commonPathCount;

        int relativePathCount = sourceRelativePathCount + targetRelativePathCount;
        String[] relativePaths = new String[relativePathCount];

        for (int i=0; i < sourceRelativePathCount; i++)
        {
          relativePaths[i] = "..";
        }

        for (int i=0; i < targetRelativePathCount; i++)
        {
          relativePaths[sourceRelativePathCount + i] = targetPaths[commonPathCount + i];
        }

        // join
        StringBuffer relativePath = new StringBuffer();
        for (int i=0; i < relativePathCount; i++)
        {
          if (i > 0)
            relativePath.append("/");
          relativePath.append(relativePaths[i]);
        }
        return relativePath.toString();
      }
      else
      {
        return targetPath;
      }
    }
  }

  private Xpp3Dom findNamedChild(
    Xpp3Dom parent,
    String  childName,
    String  attrValue)
  {
    Xpp3Dom[] hash = parent.getChildren(childName);
    for (int i=0; i < hash.length; i++)
    {
      if (attrValue.equals(hash[i].getAttribute("n")))
        return hash[i];
    }

    Xpp3Dom child = new Xpp3Dom(childName);
    child.setAttribute("n", attrValue);
    parent.addChild(child);

    return child;
  }

  private MavenProject findDependentProject(
    String dependencyManagementKey)
  {
    for (Iterator i=reactorProjects.iterator(); i.hasNext(); )
    {
      MavenProject reactorProject = (MavenProject)i.next();
      String ident = reactorProject.getArtifact().getDependencyConflictId();
      if (ident.equals(dependencyManagementKey))
        return reactorProject.getExecutionProject();
    }

    return null;
  }

  private boolean isDependentProject(
    String dependencyManagementKey)
  {
    return (findDependentProject(dependencyManagementKey) != null);
  }

  private void removeChildren(
    Xpp3Dom parent)
  {
    while (parent.getChildCount() != 0)
      parent.removeChild(0);
  }

  private Xpp3Dom createProjectReferenceDOM(
    File workspaceDir,
    File projectFile)
  {
    Xpp3Dom hashDOM = new Xpp3Dom("hash");
    Xpp3Dom valueDOM = new Xpp3Dom("value");
    valueDOM.setAttribute("n", "nodeClass");
    valueDOM.setAttribute("v", "oracle.jdeveloper.model.JProject");
    Xpp3Dom urlDOM = new Xpp3Dom("url");
    urlDOM.setAttribute("n", "URL");
    urlDOM.setAttribute("path", getRelativeFile(workspaceDir, projectFile));
    hashDOM.addChild(valueDOM);
    hashDOM.addChild(urlDOM);
    return hashDOM;
  }

}
