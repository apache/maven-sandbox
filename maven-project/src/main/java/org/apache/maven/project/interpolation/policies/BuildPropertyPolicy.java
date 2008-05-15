package org.apache.maven.project.interpolation.policies;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.project.interpolation.ModelPropertyPolicy;
import org.apache.maven.project.interpolation.ModelProperty;
import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.project.path.PathTranslator;
import org.apache.maven.project.path.DefaultPathTranslator;
import org.apache.maven.model.Model;
import org.apache.maven.model.Build;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.net.MalformedURLException;

/**
 * Interpolates the build properties: build.directory, build.outputDirectory, build.testOutputDirectory,
 * build.sourceDirectory, build.testSourceDirectory, basedir.
 */
public class BuildPropertyPolicy implements ModelPropertyPolicy {

    /**
     * Project directory for the build.
     */
    private File projectDir;

    /**
     * Expression that matches pom, project, env properties
     */
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(pom\\.|project\\.|env\\.)?([^}]+)\\}");

    /**
     * Constructor
     *
     * @param projectDir the project directory for the build. May not be null.
     */
    public BuildPropertyPolicy(File projectDir) {
        if (projectDir == null) {
            throw new IllegalArgumentException("projectDir");
        }
        this.projectDir = projectDir;
    }

    /**
     * Interpolates the build properties and resolves absolute path.
     *
     * @param mp    model property
     * @param model model. May not be null.
     * @throws ModelInterpolationException
     */
    public void evaluate(ModelProperty mp, Model model) throws ModelInterpolationException {
        if (model == null) {
            throw new IllegalArgumentException("model");
        }
        if (mp == null) {
            throw new IllegalArgumentException("mp");
        }

        String expression = mp.getExpression();
        if (expression == null) {
            throw new IllegalArgumentException("mp.expression");
        }

        Build build = model.getBuild();
        if (mp.getValue() == null) {
            //  System.out.println("Aligning path: Expression = " + expression);
            if (expression.equals("basedir")) {
                mp.setValue(projectDir.getAbsolutePath());
            } else if (expression.equals("build.directory")) {
                setPath(mp, build.getDirectory());
            } else if (expression.equals("build.outputDirectory")) {
                setPath(mp, build.getOutputDirectory());
            } else if (expression.equals("build.testOutputDirectory")) {
                setPath(mp, build.getTestOutputDirectory());
            } else if (expression.equals("build.sourceDirectory")) {
                setPath(mp, build.getSourceDirectory());
            } else if (expression.equals("build.testSourceDirectory")) {
                setPath(mp, build.getTestSourceDirectory());
            } else if (expression.equals("build.scriptSourceDirectory")) {
                setPath(mp, build.getScriptSourceDirectory());
            } else if (expression.equals("reporting.outputDirectory")) {
                setPath(mp, model.getReporting().getOutputDirectory());
            }

        } else {
            System.out.println("Matched value: Expression = " + expression);
            List modelProperties = new ArrayList();
            Matcher matcher = EXPRESSION_PATTERN.matcher(mp.getValue());
            while (matcher.find()) {
                ModelProperty modelProperty = new ModelProperty();
                modelProperty.setKey(matcher.group(0));
                modelProperty.setExpression(matcher.group(2));
                //mp.getExpression().equals(mp.getExpression()) && 
                if (!modelProperties.contains(modelProperty)) {
                    modelProperties.add(modelProperty);
                }
            }

            for (Iterator j = modelProperties.iterator(); j.hasNext();) {
                ModelProperty modelProperty = (ModelProperty) j.next();
                //     System.out.println("Found property. KEY = " + modelProperty.getKey() + ": VALUE = " + modelProperty.getValue());
                if (expression.equals("basedir")) {
                    modelProperty.setValue(projectDir.getPath());
                } else if (expression.equals("build.directory")) {
                    modelProperty.setValue(build.getDirectory());
                } else if (expression.equals("build.outputDirectory")) {
                    modelProperty.setValue(build.getOutputDirectory());
                } else if (expression.equals("build.testOutputDirectory")) {
                    modelProperty.setValue(build.getTestOutputDirectory());
                } else if (expression.equals("build.sourceDirectory")) {
                    modelProperty.setValue(build.getSourceDirectory());
                } else if (expression.equals("build.testSourceDirectory")) {
                    modelProperty.setValue(build.getTestSourceDirectory());
                } else if (expression.equals("build.scriptSourceDirectory")) {
                    modelProperty.setValue(build.getScriptSourceDirectory());
                } else if (expression.equals("reporting.outputDirectory")) {
                    modelProperty.setValue(model.getReporting().getOutputDirectory());
                }
                if (mp.getValue() != null && modelProperty.getValue() != null) {
                  //  System.out.println("Replace values: OLD VALUE = " + mp.getValue() + ": KEY = "
                  //          + modelProperty.getKey() + ": NEW VALUE =" + modelProperty.getValue());
                    mp.setValue(StringUtils.replace(mp.getValue(), modelProperty.getKey(), modelProperty.getValue()));
                    break;
                }
                System.out.println(mp.getValue());
            }
        }
    }

    /**
     * Aligns the value of the model property to the project directory.
     *
     * @param mp        model property
     * @param directory directory to align to the project directory
     */
    private void setPath(ModelProperty mp, String directory) throws ModelInterpolationException {
        if (directory == null) {
            throw new IllegalArgumentException("directory");
        }
        PathTranslator pathTranslator = new DefaultPathTranslator();
        if (!directory.startsWith("${")) {
            /*
                            File filePath = new File(pathTranslator.alignToBaseDirectory(directory, projectDir));
                String path = (!directory.contains("${")) ? filePath.toURI().toURL().toExternalForm() : filePath.getAbsolutePath();
                */
            mp.setValue(replaceFileSeparators(pathTranslator.alignToBaseDirectory(directory, projectDir)));

        } else {
            mp.setValue(replaceFileSeparators(directory));
        }
    }

    private static String replaceFileSeparators(String b) {
        return b.replace("/", File.separator).replace("\\", File.separator);
    }
}
