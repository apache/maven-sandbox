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

/**
 * Provides methods for model properties.
 */
public final class ModelProperty {

    private String value;

    private String key;

    private String expression;

    private boolean isProjectOrPomProperty;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isProjectOrPomProperty() {
        return isProjectOrPomProperty;
    }

    public void setProjectOrPomProperty(boolean projectOrPomProperty) {
        isProjectOrPomProperty = projectOrPomProperty;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelProperty that = (ModelProperty) o;

        if (key != null ? !key.equals(that.key) : that.key != null) return false;

        return true;
    }

    public int hashCode() {
        return (key != null ? key.hashCode() : 0);
    }
}
