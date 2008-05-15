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

import java.util.List;

/**
 * Provides services for interpolating the properties of a model.
 */
public interface ModelPropertyInterpolator {

    String ROLE = ModelPropertyInterpolator.class.getName();

    /**
     * Interpolates properties within the specified model, using the list of specified model property policies
     *
     * @param model    the model to interpolate properties of. This value may not be null.
     * @param policies the model property policies used to interpolate the model properties. If this value is null or
     *                 empty, this method returns an unmodified model.
     * @return model with interpolated properties
     * @throws ModelInterpolationException if there is a problem interpolating properties
     */
    Model interpolate(Model model, List policies) throws ModelInterpolationException;
}
