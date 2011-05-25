package org.apache.maven.tck;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * A rule that checks for a resource called {@code /FixPlexusBugs.enforce} and if it exists, will invert the test
 * result of any tests annotated with {@link ReproducesPlexusBug}. Obviously this invertion only occurs in test classes
 * with this rule, e.g. have a public field like {@code @Rule public FixPlexusBugs fixPlexusBugs = new FixPlexusBugs();}
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
            return base;
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
