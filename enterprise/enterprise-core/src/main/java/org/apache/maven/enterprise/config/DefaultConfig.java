package org.apache.maven.enterprise.config;

import org.apache.maven.enterprise.model.EnterpriseConfig;
import org.apache.maven.enterprise.model.EnterpriseWebdavConfig;
import org.apache.maven.enterprise.model.io.xpp3.EnterpriseXpp3Reader;
import org.apache.maven.enterprise.model.io.xpp3.EnterpriseXpp3Writer;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.Startable;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StartingException;
import org.codehaus.plexus.personality.plexus.lifecycle.phase.StoppingException;
import org.codehaus.plexus.logging.LogEnabled;
import org.codehaus.plexus.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * A wrapper for the Enterprise config for cdc and file monitoring
 *
 * @uthor: Andrew Williams
 * @version: $Id$
 *
 * @plexus.component role="org.apache.maven.enterprise.model.EnterpriseConfig"
 */
public class DefaultConfig
    extends EnterpriseConfig
    implements Startable, LogEnabled
{
    EnterpriseConfig delegate;

    EnterpriseXpp3Reader reader = new EnterpriseXpp3Reader();

    EnterpriseXpp3Writer writer = new EnterpriseXpp3Writer();

    File configFile;

    Logger logger;

    public void start()
        throws StartingException
    {
        String plexusHome = "";
        try
        {
            plexusHome = (String) (new InitialContext()).lookup( "java:comp/env/enterprise/dataDir" );
        }
        catch ( NamingException e )
        {
            /* default to the current directory */
        }

        configFile = new File( plexusHome, "config.xml" );
        logger.debug( "Loading config from " + configFile.getAbsolutePath() );

        try
        {
            delegate = reader.read( new FileReader( configFile ) );
        }
        catch (Exception e)
        {
            logger.warn( "Unable to read config, creating fresh" );
            delegate = new EnterpriseConfig();
            delegate.setWebdav( new EnterpriseWebdavConfig() );
        }
    }

    public void stop()
        throws StoppingException
    {
        try
        {
            writer.write( new FileWriter( configFile ), this );
        }
        catch (Exception e)
        {
            logger.error( "Failed writing config file " + configFile.getAbsolutePath() );
        }
    }

    public void enableLogging( Logger logger )
    {
        this.logger = logger;
    }

    /* wrapped EnterpiseConfig methods */


    public EnterpriseWebdavConfig getWebdav()
    {
        return delegate.getWebdav();
    }

    public void setWebdav( EnterpriseWebdavConfig webdav )
    {
        delegate.setWebdav( webdav );    //To change body of overridden methods use File | Settings | File Templates.
    } 
}
