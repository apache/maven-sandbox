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
import org.apache.maven.model.Model;
import org.apache.maven.project.interpolation.ModelPropertyPolicy;
import org.apache.maven.project.interpolation.ModelProperty;

/**
 * Interpolates the project.properties within the model.
 */
public class PropertyPolicy implements ModelPropertyPolicy {

    public void evaluate(ModelProperty mp, Model model) {
        if(model == null) {
            throw new IllegalArgumentException("model");
        }
        if(mp == null) {
            throw new IllegalArgumentException("mp");
        }

     //   System.out.println("MODEL PROP: VALUE = " + mp.getValue() + ": KEY = " + mp.getKey() + ": EXPR =" + mp.getExpression());
        if (mp.getValue() == null) {            
            mp.setValue(model.getProperties().getProperty(mp.getExpression()));
         //   System.out.println("SET VALUE = " + mp.getValue());
        }
    }
}
