package org.codehaus.plexus.util;


import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IOUtilTest
{

    @Test
    public void isUtilityClass()
        throws Exception
    {
        for ( Constructor c : IOUtil.class.getConstructors() )
        {
            assertThat( Modifier.isPrivate( c.getModifiers() ), is( true ) );
        }
    }
}
