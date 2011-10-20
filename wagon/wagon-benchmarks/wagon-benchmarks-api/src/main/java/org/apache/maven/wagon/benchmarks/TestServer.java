package org.apache.maven.wagon.benchmarks;
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

import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Olivier Lamy
 */
public class TestServer
{

    public static final String SERVER_SSL_KEYSTORE_PASSWORD = "wagonhttp";

    protected Logger log = LoggerFactory.getLogger( getClass() );

    public int port;

    public boolean ssl;

    public SecurityHandler securityHandler;

    public Map<String, Class<? extends Servlet>> servletsPerPath = new HashMap<String, Class<? extends Servlet>>();

    public Server server;

    public TestServer()
    {
        // no op
    }

    public void start( int minThreads )
        throws Exception
    {
        server = new Server( 0 );

        QueuedThreadPool threadpool = new QueuedThreadPool();
        threadpool.setMinThreads( minThreads );
        threadpool.setMaxThreads( 2000 );

        server.setThreadPool( threadpool );

        ServletContextHandler context = new ServletContextHandler();

        context.setContextPath( "/" );

        context.setSecurityHandler( securityHandler );

        server.setHandler( context );

        for ( Map.Entry<String, Class<? extends Servlet>> entry : servletsPerPath.entrySet() )
        {
            ServletHolder sh = new ServletHolder( entry.getValue() );
            context.addServlet( sh, entry.getKey() );
        }

        server.setHandler( context );

        Connector connector = null;

        if ( ssl )
        {
            connector = new SslSocketConnector();
            String keystore = System.getProperty( "test.keystore.path" );

            log.info( "TCK Keystore path: " + keystore );
            System.setProperty( "javax.net.ssl.keyStore", keystore );
            System.setProperty( "javax.net.ssl.trustStore", keystore );

            ( (SslSocketConnector) connector ).setKeystore( keystore );
            ( (SslSocketConnector) connector ).setPassword( SERVER_SSL_KEYSTORE_PASSWORD );
            ( (SslSocketConnector) connector ).setKeyPassword( SERVER_SSL_KEYSTORE_PASSWORD );


        }
        else
        {
            connector = new SelectChannelConnector();

        }

        connector.setRequestBufferSize( 12 * 1024 );
        connector.setResponseBufferSize( 12 * 1024 );

        server.addConnector( connector );

        server.start();
        port = server.getConnectors()[0].getLocalPort();

    }

    public void stop()
        throws Exception
    {
        server.stop();
    }
}
