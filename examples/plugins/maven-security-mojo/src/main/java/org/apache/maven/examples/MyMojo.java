package org.apache.maven.examples;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

/**
 * Goal which decrypts a password using Maven settings.
 *
 * @goal decrypt
 */
public class MyMojo
    extends AbstractMojo
{
    /**
     * Password to decrypt.
     * @parameter expression="${password}"
     * @required
     */
    private String password;

    /** @component role-hint="mojo" */
    private SecDispatcher securityDispatcher;

    public void execute()
        throws MojoExecutionException
    {
        try {
            // obviously, using it instead of echoing it is recommended :)
            getLog().info( securityDispatcher.decrypt( password ) );
        } catch ( SecDispatcherException e ) {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }
}
