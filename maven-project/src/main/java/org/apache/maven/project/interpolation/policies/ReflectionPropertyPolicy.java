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

import org.apache.maven.project.interpolation.ModelProperty;
import org.apache.maven.project.interpolation.ModelPropertyPolicy;
import org.apache.maven.project.interpolation.ModelInterpolationException;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.introspection.ReflectionValueExtractor;

/**
 * Substitutes values from model into their respective property. For example,
 * build.sourceDirectory = model.getBuild().getSourceDirectory().
 */
public class ReflectionPropertyPolicy implements ModelPropertyPolicy {

    public void evaluate(ModelProperty mp, Model model) throws ModelInterpolationException {
        if(model == null) {
            throw new IllegalArgumentException("model");
        }
        if(mp == null) {
            throw new IllegalArgumentException("mp");
        }        
        if(mp.getValue() == null) {
            try {
                mp.setValue((String) ReflectionValueExtractor.evaluate(mp.getExpression(), model, false));
            } catch (Exception e) {
                throw new ModelInterpolationException("", e);
            }
        }
    }
}
