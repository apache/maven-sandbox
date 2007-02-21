package org.apache.maven.plugins.license.filetype;

/*
 * Copyright 2001-2006 The Apache Software Foundation.
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

import org.apache.maven.parsers.java.JavaParser;
import org.apache.maven.parsers.java.ParseException;
import org.apache.maven.parsers.java.Token;
import org.apache.maven.plugins.license.ParsingException;
import org.apache.maven.plugins.license.util.SourceLocation;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * JavaSourceParser 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class JavaSourceParser
{
    public SourceLocation identifyCommentInsertionLocation( File javaFile )
        throws ParsingException
    {
        SourceLocation location = new SourceLocation( 0, 0, 0, 0 );

        try
        {
            FileReader reader = new FileReader( javaFile );
            JavaParser parser = new JavaParser( reader );
            
            
            
            parser.enable_tracing();
            
            parser.CompilationUnit();

        }
        catch ( IOException e )
        {
            e.printStackTrace( System.err );
        }
        catch ( ParseException e )
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return location;
    }

}
