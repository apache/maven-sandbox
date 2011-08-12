/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.maven.mae.project.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.mae.project.ProjectToolsException;

public class EventDispatcher<E>
{

    private final List<ProjectToolsListener<E>> listeners = new ArrayList<ProjectToolsListener<E>>();

    public EventDispatcher()
    {
    }

    public EventDispatcher( final ProjectToolsListener<E>... listeners )
    {
        for ( ProjectToolsListener<E> listener : listeners )
        {
            addListener( listener );
        }
    }

    public void fire( final E event )
        throws ProjectToolsException
    {
        if ( event == null )
        {
            return;
        }

        for ( ProjectToolsListener<E> listener : new ArrayList<ProjectToolsListener<E>>( listeners ) )
        {
            listener.onEvent( event );
        }
    }

    public synchronized void addListener( final ProjectToolsListener<E> listener )
    {
        if ( listener == null )
        {
            return;
        }

        if ( !listeners.contains( listener ) )
        {
            listeners.add( listener );
        }
    }

    public synchronized void removeListener( final ProjectToolsListener<E> listener )
    {
        if ( listener == null )
        {
            return;
        }

        listeners.remove( listener );
    }

}
