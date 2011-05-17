/*
 * Copyright 2010 Red Hat, Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.maven.mae.conf;

import org.apache.log4j.Logger;
import org.apache.maven.mae.conf.ext.ExtensionConfiguration;
import org.apache.maven.mae.conf.ext.ExtensionConfigurationException;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.apache.maven.mae.internal.container.InstanceRegistry;

import java.util.Map;
import java.util.Set;

public interface MAELibrary
{

    Logger getLogger();

    ExtensionConfiguration getConfiguration();

    ComponentSelector getComponentSelector();

    Set<ComponentKey<?>> getExportedComponents();

    Set<ComponentKey<?>> getManagementComponents( Class<?> managementType );

    Map<Class<?>, Set<ComponentKey<?>>> getManagementComponents();

    String getLabel();

    String getId();

    String getLogHandle();

    String getName();

    String getVersion();

    void loadConfiguration( final MAEConfiguration embConfig )
        throws ExtensionConfigurationException;

    InstanceRegistry getInstanceRegistry();

}
