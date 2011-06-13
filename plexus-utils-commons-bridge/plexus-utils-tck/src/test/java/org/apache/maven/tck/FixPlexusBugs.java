package org.apache.maven.tck;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * A rule that checks for a resource called {@code /FixPlexusBugs.enforce} and if it exists, will invert the test
 * result of any tests annotated with {@link ReproducesPlexusBug}. Obviously this invertion only occurs in test classes
 * with this rule, e.g. have a public field like {@code @Rule public FixPlexusBugs fixPlexusBugs = new FixPlexusBugs();}
 * <p/>
 * To turn on the switch globally create a resource in the root of the test classpath called
 * {@code /FixPlexusBugs.enforce}.
 * <p/>
 * To turn on the switch for a specific test class, create a resource with the same name as the test class only
 * substituting {@code .enforce} for {@code .class}.
 */
public class FixPlexusBugs
    implements MethodRule
{
    public Statement apply( final Statement base, FrameworkMethod method, Object target )
    {
        if ( method.getAnnotation( ReproducesPlexusBug.class ) == null )
        {
            return base;
        }

        if ( getClass().getResource( "/" + getClass().getSimpleName() + ".enforce" ) == null )
        {
            String name = "/" + method.getMethod().getDeclaringClass().getName().replace( '.', '/' ) + ".enforce";
            if ( getClass().getResource( name ) == null )
            {
                return base;
            }
        }
        return new Statement()
        {
            @Override
            public void evaluate()
                throws Throwable
            {
                boolean passed;
                try
                {
                    base.evaluate();
                    passed = true;
                }
                catch ( AssertionError t )
                {
                    passed = false;
                }
                if ( passed )
                {
                    throw new AssertionError( "Test verifies a bug in original code that should now be fixed" );
                }
            }
        };
    }
}
