package org.apache.maven.doxia.editor.action;

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

import org.apache.maven.doxia.editor.Application;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public abstract class AbstractDoxiaAction
    extends AbstractAction
{
    private Application application;

    public Application getApplication()
    {
        return application;
    }

    public void setApplication( Application application )
    {
        this.application = application;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected abstract void doAction( ActionEvent e )
        throws Exception;

    public final void actionPerformed( ActionEvent event )
    {
        try
        {
            doAction( event );
        }
        catch ( Exception e )
        {
            e.printStackTrace( System.out );
        }
    }
}
