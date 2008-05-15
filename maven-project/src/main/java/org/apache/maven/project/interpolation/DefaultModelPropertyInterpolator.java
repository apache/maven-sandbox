package org.apache.maven.project.interpolation;

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

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.StringWriter;
import java.io.IOException;
import java.io.StringReader;

/**
 * Default implementation of the model property interpolator.
 */
public class DefaultModelPropertyInterpolator implements ModelPropertyInterpolator, LogEnabled {

    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("\\$\\{(pom\\.|project\\.|env\\.)?([^}]+)\\}");

    private Logger logger;

    public void enableLogging(Logger logger) {
        if(logger == null) {
            throw new IllegalArgumentException("logger");
        }
        this.logger = logger;
    }
    /**
     * @see DefaultModelPropertyInterpolator#interpolate(org.apache.maven.model.Model, java.util.List) 
     */
    public Model interpolate(Model model, List policies) throws ModelInterpolationException {
        if(model == null) {
            throw new IllegalArgumentException("model");
        }

        if(policies == null || policies.size() == 0) {
            return model;
        }

        StringWriter pomStringWriter = new StringWriter();
        MavenXpp3Writer writer = new MavenXpp3Writer();
        try {
            writer.write(pomStringWriter, model);
        }
        catch (IOException e) {
            throw new ModelInterpolationException("Cannot serialize project model for interpolation.", e);
        }

        String result = interpolateString(model, policies, pomStringWriter.toString());
      // System.out.println(result);
        
        MavenXpp3Reader modelReader = new MavenXpp3Reader();
        try {
            return modelReader.read(new StringReader(result));
        }
        catch (IOException e) {
            throw new ModelInterpolationException(
                    "Cannot read project model from interpolating filter of serialized version.", e);
        }
        catch (XmlPullParserException e) {
            throw new ModelInterpolationException(
                    "Cannot read project model from interpolating filter of serialized version.", e);
        }
    }

    private String interpolateString(Model model, List policies, String value) throws ModelInterpolationException {
        List modelProperties = new ArrayList();
        Matcher matcher = EXPRESSION_PATTERN.matcher(value);
        while (matcher.find()) {
            ModelProperty modelProperty = new ModelProperty();
            modelProperty.setKey(matcher.group(0));
            modelProperty.setExpression(matcher.group(2));
            if (!modelProperties.contains(modelProperty)) {
                modelProperties.add(modelProperty);
            }
        }

        for (Iterator j = modelProperties.iterator(); j.hasNext();) {
            ModelProperty modelProperty = (ModelProperty) j.next();
            for (Iterator i = policies.iterator(); i.hasNext();) {
                ModelPropertyPolicy policy = (ModelPropertyPolicy) i.next();
                policy.evaluate(modelProperty, model);
                if (modelProperty.getValue() != null) {
              //  System.out.println("REPLACE: Key = " + modelProperty.getKey() + ": Value = " + modelProperty.getValue());
                    String result = StringUtils.replace(value, modelProperty.getKey(), modelProperty.getValue());
                    if (!result.equals(value)) {     //&& EXPRESSION_PATTERN.matcher(modelProperty.getValue()).matches()
                        String s = interpolateString(model, policies, modelProperty.getValue());
                     //   System.out.println("REPLACE: Key-1 = " + modelProperty.getKey() + ": Value = " + s);
                        value = StringUtils.replace(value, modelProperty.getKey(), s);
                    }
                }
            }
        }
        return value;
    }
}
