/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.plugins.jdee;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * A super-simple Mojo to emit JDEE project files.
 *
 * @goal jdee
 * @description Outputs a JDEE project file.
 * @requiresDependencyResolution test
 */

public class JdeeMojo extends AbstractMojo {
    /**
     * The Maven Project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;

    /**
     * Build directory
     *
     * @parameter expression="${project.build.outputDirectory}"
     * @required
     */
    private File buildDirectory;

    /**
     * The set of dependencies required by the project 
     * @parameter default-value="${project.artifacts}"
     * @required
     * @readonly
     */
    private java.util.Set dependencies;


    private String getVelocityLogFile(String log) {
        return new File(buildDirectory, log).toString();
    }

    private Set getSourceDirs() throws Exception {
        Set results = new HashSet();
        File file = new File(".jdee_sources");
        if (file.exists()) {
            results.addAll(FileUtils.readLines(file));
        } else {
            file.createNewFile();
        }

        results.addAll(emitPaths(project.getCompileSourceRoots()));
        results.addAll(emitPaths(project.getTestCompileSourceRoots()));
        FileUtils.writeLines(file, results);
        return results;
    }

    private Set getGlobalClasspath() throws Exception {
        Set results = new HashSet();
        File file = new File(".jdee_classpath");
        if (file.exists()) {
            results.addAll(FileUtils.readLines(file));            
        } else {
            file.createNewFile();
        }

        results.addAll(emitPaths(project.getCompileClasspathElements()));
        results.addAll(emitPaths(project.getTestClasspathElements()));
        results.addAll(emitPaths(project.getSystemClasspathElements()));
        results.addAll(getDependencies());
        FileUtils.writeLines(file, results);
        return results;
    }

    private Set getDependencies() throws Exception {
        Set results = new HashSet();
        if (dependencies != null && !dependencies.isEmpty()) {
            for (Iterator it = dependencies.iterator(); it.hasNext();) {
                Artifact artifact = (Artifact)it.next();
                results.add(artifact.getFile().toString().replace("\\", "/"));
            }
        }
        return results;
    }

    private Set emitPaths(List paths) throws IOException {
        Set np = new HashSet();
        for (Iterator it = paths.iterator(); it.hasNext();) {
            np.add(it.next().toString().replace("\\", "/"));
        }
        return np;
    }

    private void initVelocity() throws Exception {
        Properties props = new Properties();
        String clzName = "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader";
        props.put("resource.loader", "class");
        props.put("class.resource.loader.class", clzName);
        props.put("runtime.log", getVelocityLogFile("velocity.log"));

        Velocity.init(props);
    }

    private void generatePrj() throws Exception {
        initVelocity();

        String templateFile = "/META-INF/prj.vm";
        Template tmpl = Velocity.getTemplate(templateFile);
        if (tmpl == null) {
            throw new RuntimeException("Can not load template file: " + templateFile);
        }

        File outputFile = new File("prj.el");
        
        VelocityContext ctx = new VelocityContext();
        ctx.put("SOURCE_DIRS", getSourceDirs());
        ctx.put("GLOBAL_CP", getGlobalClasspath());

        Writer outputs = new OutputStreamWriter(new BufferedOutputStream(new FileOutputStream(outputFile)),
                                                "UTF-8");
        VelocityWriter writer = new VelocityWriter(outputs);

        tmpl.merge(ctx, writer);
        writer.close();
    }

    public void execute() throws MojoExecutionException {
        try {
            generatePrj();
        } catch (Exception e) {
            getLog().debug(e);
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
