package org.apache.maven.plugin.lifecycle;

import org.apache.maven.lifecycle.model.LifecycleBinding;
import org.apache.maven.lifecycle.model.LifecycleBindings;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import java.util.Iterator;
import java.util.List;

/**
 * Displays the phases of the lifecycle in order of execution.
 *
 * @goal show-lifecycle-phases
 * @author jdcasey
 *
 */
public class ShowLifecyclePhasesMojo
    implements Mojo
{

    /**
     * @parameter default-value="build"
     */
    private String lifecycle;

    private Log log;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        LifecycleBindings bindings = new LifecycleBindings();
        List bindingList = bindings.getBindingList();

        LifecycleBinding referencedBinding = null;
        for ( Iterator it = bindingList.iterator(); it.hasNext(); )
        {
            LifecycleBinding binding = (LifecycleBinding) it.next();
            if ( lifecycle.equals( binding.getId() ) )
            {
                referencedBinding = binding;
                break;
            }
            else if ( "default".equals( lifecycle ) && "build".equals( binding.getId() ) )
            {
                referencedBinding = binding;
                break;
            }
        }

        if ( referencedBinding == null )
        {
            throw new MojoFailureException( "Cannot find lifecycle with name: " + lifecycle, "Try \'build\' (also known as \'default\'), \'clean\', or \'site\'.", lifecycle );
        }
        else
        {
            StringBuffer sb = new StringBuffer( "Phases for lifecycle: " + referencedBinding.getId() + "(in order of execution):\n\n" );

            int idx = 1;
            for ( Iterator it = referencedBinding.getPhaseNamesInOrder().iterator(); it.hasNext(); )
            {
                String name = (String) it.next();
                sb.append( idx ).append( ". " ).append( ( idx < 10 ? " " : "" ) ).append( name ).append( '\n' );
                idx++;
            }

            getLog().info( sb.toString() );
        }
    }

    public Log getLog()
    {
        return log;
    }

    public void setLog( Log log )
    {
        this.log = log;
    }

}
