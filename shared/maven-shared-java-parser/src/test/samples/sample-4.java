package foo;

/**
 * A Class Comment.
 */
public class Foo
{
    public static void main(String args[])
    {
        (new Foo()).go();
    }

    /**
     * A method javadoc with the word import in it to confuse the parser.
     * import this.
     * import that;
     * this java source should not have a normal import line.
     */
    public void go()
    {
        System.out.println("Foo.");
    }
}
