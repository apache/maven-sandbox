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

package org.apache.maven.mae.internal.container;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

public class VirtualInstance<T>
    implements Provider<T>
{

    @Inject
    private Injector injector;

    private T instance;

    private final Class<T> virtualClass;

    public VirtualInstance( final Class<T> virtualClass )
    {
        this.virtualClass = virtualClass;
    }

    public void setInstance( final T instance )
    {
        this.instance = instance;
    }

    public Class<T> getVirtualClass()
    {
        return virtualClass;
    }

    public T getRawInstance()
    {
        return instance;
    }

    @Override
    public T get()
    {
        if ( injector != null && instance != null )
        {
            injector.injectMembers( instance );
        }

        return instance;
    }

}
