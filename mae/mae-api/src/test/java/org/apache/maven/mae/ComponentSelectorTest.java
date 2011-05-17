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

package org.apache.maven.mae;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.maven.mae.internal.container.ComponentKey;
import org.apache.maven.mae.internal.container.ComponentSelector;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

public class ComponentSelectorTest
{

    @BeforeClass
    public static void setupLogging()
    {
        final Configurator log4jConfigurator = new Configurator()
        {
            @SuppressWarnings( "unchecked" )
            public void doConfigure( final URL notUsed, final LoggerRepository repo )
            {
                repo.getRootLogger().addAppender( new ConsoleAppender( new SimpleLayout() ) );

                final Enumeration<Logger> loggers = repo.getCurrentLoggers();
                while ( loggers.hasMoreElements() )
                {
                    final Logger logger = loggers.nextElement();
                    logger.setLevel( Level.DEBUG );
                }
            }
        };

        log4jConfigurator.doConfigure( null, LogManager.getLoggerRepository() );
    }

    @SuppressWarnings( "rawtypes" )
    @Test
    public void componentSubstitutionWhenTargetHasRoleHint()
    {
        final Properties selectors = new Properties();
        selectors.setProperty( "role#hint", "other-hint" );

        final ComponentSelector selector = new ComponentSelector().setSelection( String.class, "hint", "other-hint" );

        final Set<ComponentKey<?>> overridden = selector.getKeysOverriddenBy( String.class, "other-hint" );
        assertEquals( 1, overridden.size() );

        final ComponentKey ok = overridden.iterator().next();
        assertEquals( "hint", ok.getHint() );
        assertEquals( String.class, ok.getRoleClass() );
        assertFalse( selector.hasOverride( String.class, "other-hint" ) );
        assertTrue( selector.hasOverride( String.class, "hint" ) );
    }

    @SuppressWarnings( "rawtypes" )
    @Test
    public void componentSubstitutionWhenTargetRoleHintIsMissing()
    {
        final Properties selectors = new Properties();
        selectors.setProperty( "role", "other-hint" );

        final ComponentSelector selector = new ComponentSelector().setSelection( String.class, "other-hint" );

        final Set<ComponentKey<?>> overridden = selector.getKeysOverriddenBy( String.class, "other-hint" );
        assertEquals( 1, overridden.size() );

        final ComponentKey ok = overridden.iterator().next();

        assertEquals( ComponentKey.DEFAULT_HINT, ok.getHint() );

        assertEquals( String.class, ok.getRoleClass() );
        assertFalse( selector.hasOverride( String.class, "other-hint" ) );
        assertTrue( selector.hasOverride( String.class ) );
    }

}
