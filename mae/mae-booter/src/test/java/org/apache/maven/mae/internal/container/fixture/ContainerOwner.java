package org.apache.maven.mae.internal.container.fixture;

import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.annotations.Component;
import org.codehaus.plexus.component.annotations.Requirement;

@Component( role = ContainerOwner.class )
public class ContainerOwner
{

    @Requirement
    public PlexusContainer container;

}
