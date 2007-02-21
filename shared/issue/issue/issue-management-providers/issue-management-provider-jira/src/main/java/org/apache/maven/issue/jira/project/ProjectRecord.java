package org.apache.maven.issue.jira.project;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id$
 */
public class ProjectRecord
{
    private String key;
    
    private String name;

    private String description;

    private String leadId;

    private String permissionSchemeId;

    private String notificationSchemeId;

    public ProjectRecord( String key, String name, String description, String leadId, String permissionSchemeId, String notificationSchemeId )
    {
        this.key = key;
        this.name = name;
        this.description = description;
        this.leadId = leadId;
        this.permissionSchemeId = permissionSchemeId;
        this.notificationSchemeId = notificationSchemeId;
    }

    public String getKey()
    {
        return key;
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public String getLeadId()
    {
        return leadId;
    }

    public String getPermissionSchemeId()
    {
        return permissionSchemeId;
    }

    public String getNotificationSchemeId()
    {
        return notificationSchemeId;
    }
}
