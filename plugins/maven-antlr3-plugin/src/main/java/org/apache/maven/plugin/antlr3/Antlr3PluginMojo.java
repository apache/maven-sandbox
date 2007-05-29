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

package org.apache.maven.plugin.antlr3;

import org.antlr.Tool;
import org.antlr.analysis.DFA;
import org.antlr.codegen.CodeGenerator;
import org.antlr.tool.BuildDependencyGenerator;
import org.antlr.tool.Grammar;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.compiler.util.scan.InclusionScanException;
import org.codehaus.plexus.compiler.util.scan.SimpleSourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.SourceInclusionScanner;
import org.codehaus.plexus.compiler.util.scan.mapping.SourceMapping;
import org.codehaus.plexus.compiler.util.scan.mapping.SuffixMapping;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Generate source code from ANTLRv3 grammar specifications.
 *
 * @goal antlr
 * @phase generate-sources
 */
public class Antlr3PluginMojo extends AbstractMojo
{
    /**
     * Specifies the Antlr directory containing grammar files.
     * 
     * @parameter expression="${basedir}/src/main/antlr"
     * @required
     */
    protected File sourceDirectory;

    /**
     * A set of patterns matching files from the sourceDirectory that
     * should be processed as grammers.
     * 
     * @parameter
     */
    Set includes = new HashSet();

    /**
     * Set of exclude patterns
     * 
     * @parameter
     */
    Set excludes = new HashSet();

    /**
     * Enables ANTLR-specific network debugging. Requires a tool able to
     * talk this protocol e.g. ANTLRWorks
     * 
     * @parameter
     */
    protected boolean debug = false;

    /**
     * Generate a parser that logs rule entry/exit messages.
     * 
     * @parameter
     */
    protected boolean trace = false;

    /**
     * Generate a parser that computes profiling information.
     * 
     * @parameter
     */
    protected boolean profile = false;

    private Tool tool;

    /**
     * Location for generated Java files.
     * 
     * @parameter expression="${project.build.directory}/generated-sources/antlr"
     * @required
     */
    private File outputDirectory;

    /**
     * The number of milliseconds ANTLR will wait for analysis of each
     * alternative in the grammar to complete before giving up.
     * 
     * @parameter
     */
    private int conversionTimeout;

    /**
     * @parameter expression="${project}"
     * @required
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException
    {
        File f = outputDirectory;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        tool = new Tool();
        DFA.MAX_TIME_PER_DFA_CREATION = conversionTimeout;

        try
        {
            processGrammarFiles();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( "", e );
        }
        if ( project != null )
        {
            project.addCompileSourceRoot( outputDirectory.getPath() );
        }
    }

    private void processGrammarFiles()
        throws TokenStreamException, RecognitionException, IOException, InclusionScanException
    {
        SourceMapping mapping = new SuffixMapping( "g", Collections.EMPTY_SET );
        Set includes = getIncludesPatterns();
        SourceInclusionScanner scan = new SimpleSourceInclusionScanner( includes, excludes );
        scan.addSourceMapping( mapping );
        Set grammarFiles = scan.getIncludedSources( sourceDirectory, null );
        if ( grammarFiles.isEmpty() )
        {
            if ( getLog().isInfoEnabled() )
            {
                getLog().info( "No grammars to process" );
            }
        }
        else
        {
            boolean built = false;
            for ( Iterator i = grammarFiles.iterator(); i.hasNext(); )
            {
                built |= processGrammarFile( ( (File) i.next() ).getPath() );
            }
            if ( !built && getLog().isInfoEnabled() )
            {
                getLog().info( "No grammars processed; generated files are up to date" );
            }
        }
    }

    public Set getIncludesPatterns()
    {
        if ( includes == null || includes.isEmpty() )
        {
            return Collections.singleton( "**/*.g" );
        }
        return includes;
    }

    private boolean processGrammarFile( String grammarFileName )
        throws TokenStreamException, RecognitionException, IOException
    {
        // Hack the output dir such that the output hierarchy will match the
        // source hierarchy.  This way, grammar authors can arrange their
        // grammars in a structure that matches the package (assuming Java
        // output), and the generated files will not produce warnings/errors
        // from javac due to the path-prefix not matching the package-prefix.
        // (ANTLR sort-of does this itself, but only when grammar file names
        // are specified relative to $PWD)
        String sourceSubdir = findSourceSubdir( grammarFileName );
        File outputSubdir = new File( outputDirectory, sourceSubdir );
        tool.setOutputDirectory( outputSubdir.getPath() );

        BuildDependencyGenerator dep = new BuildDependencyGenerator( tool, grammarFileName );
        List outputFiles = dep.getGeneratedFileList();
        // TODO: processing order for multiple grammars based on interdependencies?
        List dependents = dep.getDependenciesFileList();
        if ( AntlrHelper.buildRequired( grammarFileName, outputFiles ) )
        {
            generate( grammarFileName );
            return true;
        }
        return false;
    }

    private String findSourceSubdir( String grammarFileName )
    {
        String srcPath = sourceDirectory.getPath();
        if ( !grammarFileName.startsWith( srcPath ) )
        {
            throw new IllegalArgumentException( "expected " + grammarFileName
                                               + " to be prefixed with "
                                               + sourceDirectory );
        }
        File unprefixedGrammarFileName = new File( grammarFileName.substring( srcPath.length() ) );
        return unprefixedGrammarFileName.getParent();
    }

    private void generate( String grammarFileName ) throws TokenStreamException, RecognitionException, IOException
    {
        if ( getLog().isInfoEnabled() )
        {
            getLog().info( "Processing grammar " + grammarFileName );
        }
        Grammar grammar = tool.getGrammar( grammarFileName );
        processGrammar( grammar );

        // now handle the lexer if one was created for a merged spec
        String lexerGrammarStr = grammar.getLexerGrammar();
        if ( grammar.type == Grammar.COMBINED && lexerGrammarStr != null )
        {
            String lexerGrammarFileName = grammar.getImplicitlyGeneratedLexerFileName();
            Writer w = tool.getOutputFile( grammar, lexerGrammarFileName );
            w.write( lexerGrammarStr );
            w.close();
            StringReader sr = new StringReader( lexerGrammarStr );
            Grammar lexerGrammar = new Grammar();
            lexerGrammar.setTool( tool );
            File lexerGrammarFullFile = new File( tool.getFileDirectory( lexerGrammarFileName ), lexerGrammarFileName );
            lexerGrammar.setFileName( lexerGrammarFullFile.toString() );
            lexerGrammar.importTokenVocabulary( grammar );
            lexerGrammar.setGrammarContent( sr );
            sr.close();
            processGrammar( lexerGrammar );
        }
    }

    private void processGrammar( Grammar grammar )
    {
        String language = (String) grammar.getOption( "language" );
        if ( language != null )
        {
            CodeGenerator generator = new CodeGenerator( tool, grammar, language );
            grammar.setCodeGenerator( generator );
            generator.setDebug( debug );
            generator.setProfile( profile );
            generator.setTrace( trace );
            generator.genRecognizer();
        }
    }
}