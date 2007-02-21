package org.apache.maven.shared.parser;

import antlr.CommonAST;
import antlr.Token;
import antlr.collections.AST;

/**
 * LocationAST - Extension to AST to provide for tracking of line number and column
 * of AST elements. 
 *
 * @author <a href="mailto:joakim@erdfelt.com">Joakim Erdfelt</a>
 * @version $Id$
 */
public class LocationAST
    extends CommonAST
{
    private int line = -1;

    private int column = -1;

    public LocationAST()
    {
        super();
    }

    public LocationAST( Token tok )
    {
        super( tok );
        initialize( tok );
    }

    public void initialize( AST t )
    {
        super.initialize( t );
        if ( t instanceof LocationAST )
        {
            setLine( ( (LocationAST) t ).getLine() );
            setColumn( ( (LocationAST) t ).getColumn() );
        }
    }

    public void initialize( Token tok )
    {
        super.initialize( tok );
        setLine( tok.getLine() );
        setColumn( tok.getColumn() );
    }

    public int getColumn()
    {
        return column;
    }

    public void setColumn( int column )
    {
        this.column = column;
    }

    public int getLine()
    {
        return line;
    }

    public void setLine( int line )
    {
        this.line = line;
    }
}
