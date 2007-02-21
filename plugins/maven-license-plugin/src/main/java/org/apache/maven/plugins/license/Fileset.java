package org.apache.maven.plugins.license;

import org.apache.maven.shared.model.fileset.FileSet;

/**
 * Customizes the string representation of 
 * <code>org.apache.maven.shared.model.fileset.FileSet</code> to return the 
 * included and excluded files from the file-set's directory. Specifically, 
 * <code>"file-set: <I>[directory]</I> (included: <I>[included files]</I>, 
 * excluded: <I>[excluded files]</I>)"</code>   
 *  
 * @see org.apache.maven.shared.model.fileset.FileSet
 */
public class Fileset
    extends FileSet
{

    /**
     * Retrieves the included and excluded files from this file-set's directory.
     * Specifically, <code>"file-set: <I>[directory]</I> (included: 
     * <I>[included files]</I>, excluded: <I>[excluded files]</I>)"</code>   
     * 
     * @return The included and excluded files from this file-set's directory.
     * Specifically, <code>"file-set: <I>[directory]</I> (included: 
     * <I>[included files]</I>, excluded: <I>[excluded files]</I>)"</code>   
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return "file-set: " + getDirectory() + " (included: " + getIncludes() + ", excluded: " + getExcludes() + ")";
    }

}
