package org.apache.maven.doxia.editor.action.manager;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.codehaus.plexus.personality.plexus.lifecycle.phase.InitializationException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Initializable;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.apache.maven.doxia.editor.action.manager.ActionManager;
import org.apache.maven.doxia.editor.action.AbstractDoxiaAction;
import org.apache.maven.doxia.editor.Application;

import javax.swing.*;
import java.util.Map;
import java.util.HashMap;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class DefaultActionManager
    extends AbstractLogEnabled
    implements ActionManager, Initializable
{
    private Application application;

    private Map actions = new HashMap();

    // ----------------------------------------------------------------------
    // ActionManager Implementation
    // ----------------------------------------------------------------------

    public Action getAction( String name )
    {
        Action action = (Action) actions.get( name );

        if ( action == null )
        {
            throw new RuntimeException( "No such action '" + name + "'." );
        }

        if ( action instanceof AbstractDoxiaAction )
        {
            ((AbstractDoxiaAction) action).setApplication( application );
        }

        return action;
    }

    // ----------------------------------------------------------------------
    // Component Lifecycle
    // ----------------------------------------------------------------------

    public void initialize()
        throws InitializationException
    {
    }
}
