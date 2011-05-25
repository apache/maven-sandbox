package org.apache.maven.tck;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks test cases where we are testing buggy behaviour we will want to fix but need to reproduce
 * to establish compatibility.
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface ReproducesPlexusBug
{
    String value();
}
