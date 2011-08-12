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

package org.apache.maven.mae.project;

import static org.apache.maven.mae.project.event.ModelLoaderEventBuilder.newBuiltModelEvent;
import static org.apache.maven.mae.project.event.ModelLoaderEventBuilder.newErrorEvent;
import static org.apache.maven.mae.project.event.ModelLoaderEventBuilder.newResolvedModelEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.mae.MAEException;
import org.apache.maven.mae.project.event.EventDispatcher;
import org.apache.maven.mae.project.event.ModelLoaderEvent;
import org.apache.maven.mae.project.internal.SimpleModelResolver;
import org.apache.maven.mae.project.key.FullProjectKey;
import org.apache.maven.mae.project.session.ProjectToolsSession;
import org.apache.maven.mae.project.session.SessionInjector;
import org.apache.maven.model.Model;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.io.ModelParseException;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.RequestTrace;
import org.sonatype.aether.impl.ArtifactResolver;
import org.sonatype.aether.impl.RemoteRepositoryManager;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.DefaultRequestTrace;

@Component( role = ModelLoader.class )
public class DefaultModelLoader
    implements ModelLoader
{

    @Requirement
    private ArtifactResolver artifactResolver;

    @Requirement
    private RemoteRepositoryManager remoteRepositoryManager;

    @Requirement
    private ModelReader modelReader;

    @Requirement
    private SessionInjector sessionInjector;

    @Override
    public List<Model> loadRawModels( final ProjectToolsSession session, final boolean processModules,
                                      final RequestTrace trace, final File... poms )
        throws ProjectToolsException
    {
        Map<String, Object> options = getStandardOptions();
        Map<File, Model> models = new LinkedHashMap<File, Model>();

        List<File> toProcess = new ArrayList<File>( Arrays.asList( poms ) );
        Map<File, RequestTrace> traces = new HashMap<File, RequestTrace>();
        while ( !toProcess.isEmpty() )
        {
            File file = toProcess.remove( 0 );

            if ( !models.containsKey( file ) )
            {
                RequestTrace modelTrace = trace.newChild( file );
                Model model = loadRaw( new FileModelSource( file ), options, modelTrace, session );
                model.setPomFile( file );

                models.put( file, model );

                if ( processModules )
                {
                    addModulePoms( model, modelTrace, toProcess, traces );
                }
            }
        }

        return new ArrayList<Model>( models.values() );
    }

    @Override
    public List<Model> loadRawModel( final File pom, final boolean processModules, final RequestTrace trace,
                                     final ProjectToolsSession session )
        throws ProjectToolsException
    {
        Map<String, Object> options = getStandardOptions();
        Map<File, Model> models = new LinkedHashMap<File, Model>();

        List<File> toProcess = new ArrayList<File>( Collections.singleton( pom ) );
        Map<File, RequestTrace> traces = new HashMap<File, RequestTrace>();
        while ( !toProcess.isEmpty() )
        {
            File file = toProcess.remove( 0 );

            if ( !models.containsKey( file ) )
            {
                RequestTrace modelTrace = trace.newChild( file );
                Model model = loadRaw( new FileModelSource( file ), options, modelTrace, session );
                model.setPomFile( file );

                models.put( file, model );

                if ( processModules )
                {
                    addModulePoms( model, modelTrace, toProcess, traces );
                }
            }
        }

        return new ArrayList<Model>( models.values() );
    }

    @Override
    public Model loadRawModel( final FullProjectKey key, final RequestTrace trace, final ProjectToolsSession session )
        throws ProjectToolsException
    {
        ModelSource src = resolveModel( key, trace, session );
        Map<String, Object> options = getStandardOptions();
        return loadRaw( src, options, trace, session );
    }

    @Override
    public Model loadRawModel( final String groupId, final String artifactId, final String version,
                               final RequestTrace trace, final ProjectToolsSession session )
        throws ProjectToolsException
    {
        return loadRawModel( new FullProjectKey( groupId, artifactId, version ), trace, session );
    }

    @Override
    public ModelSource resolveModel( final FullProjectKey key, final RequestTrace trace,
                                     final ProjectToolsSession session )
        throws ProjectToolsException
    {
        try
        {
            RepositorySystemSession rss = sessionInjector.getRepositorySystemSession( session );
            List<RemoteRepository> repos = sessionInjector.getRemoteRepositories( session );

            SimpleModelResolver resolver =
                new SimpleModelResolver( rss, repos, new DefaultRequestTrace( key ), artifactResolver,
                                         remoteRepositoryManager );

            ModelSource source = resolver.resolveModel( key.getGroupId(), key.getArtifactId(), key.getVersion() );

            ModelLoaderEvent event = newResolvedModelEvent( trace ).withKey( key ).withModelSource( source ).build();
            getDispatcher( session ).fire( event );

            return source;
        }
        catch ( UnresolvableModelException e )
        {
            ModelLoaderEvent event = newErrorEvent( trace ).withKey( key ).withError( e ).build();
            getDispatcher( session ).fire( event );

            throw new ProjectToolsException( "Failed to resolve model: %s. Reason: %s", e, key, e.getMessage() );
        }
        catch ( MAEException e )
        {
            ModelLoaderEvent event = newErrorEvent( trace ).withKey( key ).withError( e ).build();
            getDispatcher( session ).fire( event );

            throw new ProjectToolsException( "Failed to initialize model-resolving environment: %s. Reason: %s", e,
                                             key, e.getMessage() );
        }
    }

    @Override
    public ModelSource resolveModel( final String groupId, final String artifactId, final String version,
                                     final RequestTrace trace, final ProjectToolsSession session )
        throws ProjectToolsException
    {
        return resolveModel( new FullProjectKey( groupId, artifactId, version ), trace, session );
    }

    private Model loadRaw( final ModelSource source, final Map<String, Object> options, final RequestTrace trace,
                           final ProjectToolsSession session )
        throws ProjectToolsException
    {
        ModelLoaderEvent event = null;

        Model model;
        try
        {
            model = modelReader.read( source.getInputStream(), options );

            event = newBuiltModelEvent( trace ).withModel( model ).withModelSource( source ).build();
            return model;
        }
        catch ( ModelParseException e )
        {
            event = newErrorEvent( trace ).withModelSource( source ).withError( e ).build();
            throw new ProjectToolsException( "Failed to parse model: %s. Reason: %s", e, source, e.getMessage() );
        }
        catch ( IOException e )
        {
            event = newErrorEvent( trace ).withModelSource( source ).withError( e ).build();
            throw new ProjectToolsException( "Failed to read model: %s. Reason: %s", e, source, e.getMessage() );
        }
        finally
        {
            getDispatcher( session ).fire( event );
        }
    }

    private void addModulePoms( final Model model, final RequestTrace trace, final List<File> toProcess,
                                final Map<File, RequestTrace> traces )
    {
        if ( model.getModules() != null )
        {
            File dir = model.getPomFile().getParentFile();
            if ( dir == null )
            {
                dir = new File( System.getProperty( "user.dir" ) );
            }

            for ( String mod : model.getModules() )
            {
                File modFile = new File( dir, mod );
                if ( modFile.exists() )
                {
                    traces.put( modFile, trace.newChild( modFile ) );
                    toProcess.add( modFile );
                }
            }
        }
    }

    private synchronized EventDispatcher<ModelLoaderEvent> getDispatcher( final ProjectToolsSession session )
    {
        EventDispatcher<ModelLoaderEvent> d = session.getEventDispatcher( ModelLoaderEvent.class );
        if ( d == null )
        {
            d = new EventDispatcher<ModelLoaderEvent>();
            session.setEventDispatcher( ModelLoaderEvent.class, d );
        }

        return d;
    }

    private Map<String, Object> getStandardOptions()
    {
        Map<String, Object> options = new HashMap<String, Object>();
        options.put( ModelReader.IS_STRICT, Boolean.FALSE.toString() );

        return options;
    }

}
