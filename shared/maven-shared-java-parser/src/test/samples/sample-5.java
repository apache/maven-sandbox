package foo;

/* Look!  And old comment that exists before the import statements.
 */

import org.bar.Bar;

/* a normal multiline that exists before the class definition. */

/**
 * Javadoc for class Foo.
 */
public class Foo
{
    /**
     * Javadoc for Class Field MESSAGE.
     */
    private static final String MESSAGE = "Foo.";

    public static void main( String args[] )
    {
        ( new Foo() ).go();
    }
    
    /**
     * Javadoc for constructor.
     */
    public Foo()
    {
        // Ignore
    }

    /**
     * Javadoc for go()
     */
    public void go()
    {
        System.out.println( "Hello " + MESSAGE + "." );
    }
}
