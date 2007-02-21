package org.apache.maven.issue.jira;

import org.apache.maven.issue.jira.authentication.AuthenticationSource;
import org.apache.maven.issue.jira.project.ProjectRecordSource;
import org.apache.maven.issue.jira.project.ProjectRecordRetrievalException;
import org.apache.maven.issue.jira.project.ProjectRecord;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Iterator;

public class JiraSoapClient
{
    private URL endpoint;

    private String token;

    private JiraSoapService service;

    private AuthenticationSource authenticationSource;

    // ----------------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------------

    public void setEndpoint( URL endpoint )
    {
        this.endpoint = endpoint;
    }

    public void setAuthenticationSource( AuthenticationSource authenticationSource )
    {
        this.authenticationSource = authenticationSource;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public void initialize()
        throws Exception
    {
        JiraSoapServiceService jiraSoapServiceLocator = new JiraSoapServiceServiceLocator();

        service = jiraSoapServiceLocator.getJirasoapserviceV2( endpoint );

        authenticationSource.initialize();

        token = service.login( authenticationSource.getLogin(), authenticationSource.getPassword() );
    }

    // ----------------------------------------------------------------------
    // Projects
    // ----------------------------------------------------------------------

    /**
     * Create a series of projects from a ProjectRecordSource
     *
     * @param source
     * @throws RemoteException
     * @throws ProjectRecordRetrievalException
     *
     */
    public void createProjects( ProjectRecordSource source )
        throws RemoteException, ProjectRecordRetrievalException
    {
        for ( Iterator i = source.getRecords(); i.hasNext(); )
        {
            ProjectRecord record = (ProjectRecord) i.next();

            createProject( record.getKey(),
                           record.getName(),
                           record.getDescription(),
                           record.getLeadId(),
                           record.getPermissionSchemeId(),
                           record.getNotificationSchemeId() );
        }
    }

    public String createProject( String projectKey,
                                 String projectName,
                                 String projectDescription,
                                 String leadId,
                                 String permissionSchemeId,
                                 String notificationSchemeId )
        throws RemoteException
    {
        RemoteProject project = new RemoteProject();

        project.setKey( projectKey );

        project.setLead( leadId );

        project.setName( projectName );

        project.setDescription( projectDescription );

        // ----------------------------------------------------------------------
        // Permission Scheme
        // ----------------------------------------------------------------------

        RemotePermissionScheme permissionScheme = new RemotePermissionScheme();

        System.out.println( "permissionSchemeId = " + permissionSchemeId );

        permissionScheme.setId( Long.getLong( permissionSchemeId ) );

        permissionScheme.setId( new Long( 0 ) );

        project.setPermissionScheme( permissionScheme );

        // ----------------------------------------------------------------------
        // Notification Scheme
        // ----------------------------------------------------------------------

        RemoteScheme notificationScheme = new RemoteScheme();

        System.out.println( "notificationSchemeId = " + notificationSchemeId );

        notificationScheme.setId( Long.getLong( notificationSchemeId ) );

        project.setNotificationScheme( notificationScheme );

        // ----------------------------------------------------------------------
        //
        // ----------------------------------------------------------------------

        RemoteProject returnedProject = service.createProjectFromObject( token, project );

        projectKey = returnedProject.getKey();

        return projectKey;
    }

    public void addVersion( String projectKey, String version )
        throws RemoteException
    {
        RemoteVersion remoteVersion = new RemoteVersion();

        remoteVersion.setName( version );

        remoteVersion.setSequence( new Long( 6 ) );

        service.addVersion( token, projectKey, remoteVersion );
    }

    // ----------------------------------------------------------------------
    // Issues
    // ----------------------------------------------------------------------

    public RemoteIssue createIssue( String projectKey,
                                    String summary,
                                    String description,
                                    String issueTypeId,
                                    String priorityId,
                                    String componentId,
                                    String versionId,
                                    String assigneeId )
        throws RemoteException
    {
        return createIssue( projectKey, summary, description, issueTypeId, priorityId, componentId, versionId, assigneeId, null );
    }

    // Attachments are currently busted in the JIRA soap interface
    public RemoteIssue createIssue( String projectKey,
                                    String summary,
                                    String description,
                                    String issueTypeId,
                                    String priorityId,
                                    String componentId,
                                    String versionId,
                                    String assigneeId,
                                    File attachment )
        throws RemoteException
    {
        RemoteIssue issue = new RemoteIssue();

        issue.setProject( projectKey );

        issue.setSummary( summary );

        issue.setType( issueTypeId );

        issue.setPriority( priorityId );

        issue.setDescription( description );

        if ( attachment != null )
        {
            issue.setAttachmentNames( new String[]{attachment.getAbsolutePath()} );
        }

        issue.setAssignee( assigneeId );

        RemoteComponent component = new RemoteComponent();

        component.setId( componentId );

        issue.setComponents( new RemoteComponent[]{component} );

        // Make up some remote versions
        RemoteVersion version = new RemoteVersion();

        version.setId( versionId );

        RemoteVersion[] remoteVersions = new RemoteVersion[]{version};

        issue.setFixVersions( remoteVersions );

        RemoteIssue returnedIssue = service.createIssue( token, issue );

        final String issueKey = returnedIssue.getKey();

        printIssueDetails( returnedIssue );

        return returnedIssue;
    }

    public void testGetIssues( String filterId )
        throws RemoteException
    {
        RemoteIssue[] issues = service.getIssuesFromFilter( token, filterId );

        for ( int i = 0; i < issues.length; i++ )
        {
            RemoteIssue issue = issues[i];
            System.out.println( "issue.getSummary(): " + issue.getSummary() );
        }
    }

    public void findIssuesWithTerm( String term )
        throws RemoteException
    {
        long startTime = System.currentTimeMillis();

        RemoteIssue[] issuesFromTextSearch = service.getIssuesFromTextSearch( token, term );

        System.out.println( issuesFromTextSearch.length + " issues with term \"" + term + "\"" );

        for ( int i = 0; i < issuesFromTextSearch.length; i++ )
        {
            RemoteIssue remoteIssue = issuesFromTextSearch[i];
            System.out.println( "\t" + remoteIssue.getKey() + " " + remoteIssue.getSummary() );
        }
        System.out.println( "Time taken for search: " + ( System.currentTimeMillis() - startTime ) + "ms" );
    }

    /*
    public void updateIssue( String issueKey )
        throws RemoteException
    {
        getFieldsForEdit( issueKey );

        // Update the issue
        RemoteFieldValue[] actionParams = new RemoteFieldValue[]{
            new RemoteFieldValue( "summary", new String[]{NEW_SUMMARY} ),
            new RemoteFieldValue( CUSTOM_FIELD_KEY_1, new String[]{CUSTOM_FIELD_VALUE_1} ),
            new RemoteFieldValue( CUSTOM_FIELD_KEY_2, new String[]{CUSTOM_FIELD_VALUE_2} )};

        service.updateIssue( token, issueKey, actionParams );
    }
    */

    public void getFieldsForEdit( String issueKey )
        throws RemoteException
    {
        // Editing the issue & getting the fields available on edit
        System.out.println( "The issue " + issueKey + " has the following editable fields:" );
        final RemoteField[] fieldsForEdit = service.getFieldsForEdit( token, issueKey );
        for ( int i = 0; i < fieldsForEdit.length; i++ )
        {
            RemoteField remoteField = fieldsForEdit[i];
            System.out.println( "\tremoteField: " + remoteField.getId() );
        }
    }

    public void addComment( String issueKey, String comment )
        throws RemoteException
    {
        final RemoteComment remoteComment = new RemoteComment();

        remoteComment.setBody( comment );

        service.addComment( token, issueKey, remoteComment );
    }

    public void addAttachemnt( String issueKey, File attachment )
        throws RemoteException
    {
        RemoteIssue issue = service.getIssue( token, issueKey );

        addAttachment( issue, attachment );
    }

    public void addAttachment( RemoteIssue issue, File attachment )
        throws RemoteException
    {
        boolean added = service.addAttachmentToIssue( token, new String[]{attachment.getAbsolutePath()}, issue );
    }

    // ----------------------------------------------------------------------
    // Groups
    // ----------------------------------------------------------------------

    public void testUpdateGroup()
        throws RemoteException
    {
        System.out.println( "Testing group update.." );
        RemoteGroup group = service.getGroup( token, "jira-developers" );
        System.out.println( "Updating group: " + group.getName() );
        System.out.println( "group.getUsers(): " + ( group.getUsers().length ) );


        RemoteUser[] remoteUsers = new RemoteUser[group.getUsers().length + 1];
        final RemoteUser[] oldUsers = group.getUsers();
        for ( int i = 0; i < oldUsers.length; i++ )
        {
            remoteUsers[i] = oldUsers[i];
        }
        remoteUsers[remoteUsers.length - 1] = new RemoteUser( null, null, "fred" );
        group.setUsers( remoteUsers );
        service.updateGroup( token, group );

        group = service.getGroup( token, "jira-developers" );
        System.out.println( "group: " + group );
        System.out.println( "group.getUsers(): " + ( group.getUsers().length ) );
    }

    // ----------------------------------------------------------------------
    // Permissions
    // ----------------------------------------------------------------------

    public void testGetAllPermissions()
        throws RemoteException
    {
        RemotePermission[] allPermissions = service.getAllPermissions( token );
        for ( int i = 0; i < allPermissions.length; i++ )
        {
            RemotePermission allPermission = allPermissions[i];
            System.out.println( "allPermission.getName(): " + allPermission.getName() );
        }
    }

    // ----------------------------------------------------------------------
    // Custom Fields
    // ----------------------------------------------------------------------

    public void testGetCustomFields()
        throws RemoteException
    {
        final RemoteField[] customFields = service.getCustomFields( token );
        for ( int i = 0; i < customFields.length; i++ )
        {
            RemoteField customField = customFields[i];
            System.out.println( "customField.getName(): " + customField.getName() );
        }
    }

    // ----------------------------------------------------------------------
    // Filters
    // ----------------------------------------------------------------------

    public void testGetFilters()
        throws RemoteException
    {
        System.out.println( "All saved filters:" );
        RemoteFilter[] savedFilters = service.getSavedFilters( token );
        for ( int i = 0; i < savedFilters.length; i++ )
        {
            RemoteFilter filter = savedFilters[i];
            String description = filter.getDescription() != null ? ( ": " + filter.getDescription() ) : "";
            System.out.println( "\t" + filter.getName() + description );
        }
    }


    public void printIssueDetails( RemoteIssue issue )
    {
        System.out.println( "Issue Details" );
        Method[] declaredMethods = issue.getClass().getDeclaredMethods();
        for ( int i = 0; i < declaredMethods.length; i++ )
        {
            Method declaredMethod = declaredMethods[i];
            if ( declaredMethod.getName().startsWith( "get" ) && declaredMethod.getParameterTypes().length == 0 )
            {
                System.out.print( "Issue." + declaredMethod.getName() + "() -> " );
                try
                {
                    Object o = declaredMethod.invoke( issue, new Object[]{} );
                    if ( o instanceof Object[] )
                        System.out.println( printArray( (Object[]) o ) );
                    else
                        System.out.println( o );
                }
                catch ( IllegalAccessException e )
                {
                    e.printStackTrace();
                }
                catch ( InvocationTargetException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String printArray
        ( Object[] o )
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < o.length; i++ )
        {
            sb.append( o[i] + " " );
        }
        return sb.toString();
    }
}
