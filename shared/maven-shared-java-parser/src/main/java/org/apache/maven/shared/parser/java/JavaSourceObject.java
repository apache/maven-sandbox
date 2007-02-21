package org.apache.maven.shared.parser.java;

public class JavaSourceObject
{
    private int startLine;

    private int endLine;

    private int startColumn;

    private int endColumn;

    public int getEndColumn()
    {
        return endColumn;
    }

    public void setEndColumn( int endColumn )
    {
        this.endColumn = endColumn;
    }

    public int getEndLine()
    {
        return endLine;
    }

    public void setEndLine( int endLine )
    {
        this.endLine = endLine;
    }

    public int getStartColumn()
    {
        return startColumn;
    }

    public void setStartColumn( int startColumn )
    {
        this.startColumn = startColumn;
    }

    public int getStartLine()
    {
        return startLine;
    }

    public void setStartLine( int startLine )
    {
        this.startLine = startLine;
    }
}
