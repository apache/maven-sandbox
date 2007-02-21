package org.apache.maven.pomo;

import org.apache.maven.model.Model;

import java.util.Map;
import java.io.Writer;

/**
 * @author Jason van Zyl
 */
public interface Pomo
{
    String ROLE = Pomo.class.getName();

    Model generateModel( PomInfoSource source,
                         Map jarAnalysisResults )
        throws ModelGenerationException;


    void writeModel( Writer writer,
                     Model model )
        throws ModelWritingException;

}
