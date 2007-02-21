#!/usr/bin/ruby

require 'defaultDriver'
#require 'JiraSoapServiceServiceClient'
require 'yaml'
require 'yaml/store'
require 'rexml/document'
require 'net/http'
require 'uri'
require 'cgi'

include REXML

class IssueManager
  def initialize( debug=false )
    @debug = debug
    @jira = YAML::load( File.open( File.join( File.dirname( __FILE__ ), 'mappings.yaml' ) ) )
    @server = getServer()
  end

  def getServer
    if @server == nil
      @config = YAML::load( File.open( File.join( ENV['HOME'], 'jira.yaml' ) ) )
      user = @config['user']
      password = @config['password']
      @url = @config['server-soap']
      @server = JiraSoapService.new( @url )
      @token = @server.login( user, password )
    end
    return @server
  end

  #----------------------------------------------------------------
  # SOAP service delegator
  #----------------------------------------------------------------

  # Delegate calls to the underlying server where possible
  def method_missing(method_name, *args)
    return @server.send( method_name, *([@token] + args) )
  end    

  #----------------------------------------------------------------
  # Projects
  #----------------------------------------------------------------

  # The issue is a struct with all the issue information
  # 
  # NOTE: default assignee type is unassigned
  def createProject( key, name, description, url, lead, permissionSchemeId, notificationSchemeId, assigneeType = "3" )
    url = CGI.escape( url )
    description = CGI.escape( description )
    name = CGI.escape( name )
    query = "#{@config['server']}/secure/admin/AddProject.jspa?name=#{name}&key=#{key}&url=#{url}&lead=#{lead}&assigneeType=#{assigneeType}&description=#{description}&notificationScheme=#{notificationSchemeId}&permissionScheme=#{permissionSchemeId}&issueSecurityScheme=-1&Create=Create&os_username=#{@config['user']}&os_password=#{@config['password']}"
    request = URI.parse(query)
    res = Net::HTTP.get(request)
    if @debug
      puts res
    end    
  end  
  
  def updateProject( pid, name, url, lead, description, assigneeType )
    # Assignee type
    # 2 = project lead
    # 3 = unassigned
    name = CGI.escape( name )
    url = CGI.escape( url )
    description = CGI.escape( description )    
    query = "#{@config['server']}/secure/project/EditProject.jspa?name=#{name}&url=#{url}&lead=#{lead}&assigneeType=#{assigneeType}&description=#{description}&pid=#{pid}&Update=Update&os_username=#{@config['user']}&os_password=#{@config['password']}"   
    request = URI.parse(query)
    res = Net::HTTP.get(request)
    if @debug
      puts query
      puts res
    end
  end    

  def assignWorkflowScheme( pid, workflowSchemeId )
    # Assign workflow: this will only work if it's a simple transition that requires no human intervention.
    query = "#{@config['server']}/secure/project/SelectProjectWorkflowSchemeStep2.jspa?schemeId=#{workflowSchemeId}&projectId=#{pid}&Associate=Associate&os_username=#{@config['user']}&os_password=#{@config['password']}"
    request = URI.parse(query)
    res = Net::HTTP.get(request)
    if @debug
      puts query
      puts res
    end    
  end    

  #----------------------------------------------------------------
  # Issues
  #----------------------------------------------------------------

  # The issue is a struct with all the issue information
  def closeIssue( issue, assignee, fixVersion=nil )
    id = issue.id
    if fixVersion == nil
      fixVersionParameter = ""
    else
      fixVersionParameter="fixVersions=#{fixVersion}&"
    end
    query = "#{@config['server']}/secure/CommentAssignIssue.jspa?#{fixVersionParameter}resolution=1&action=2&id=#{id}&assignee=#{assignee}&Close%20Issue=Close%20Issue&os_username=#{@config['user']}&os_password=#{@config['password']}"
    url = URI.parse(query)
    res = Net::HTTP.get(url)  
    if @debug
      puts query
      puts res
    end          
  end
  
  # The issue is a struct with all the issue information
  def reopenIssue( issue, assignee )
    id = issue.id
    query = "#{@config['server']}/secure/CommentAssignIssue.jspa?action=3&id=#{id}&assignee=#{assignee}&Reopen%20Issue=Reopen%20Issue&os_username=#{@config['user']}&os_password=#{@config['password']}"
    url = URI.parse(query)
    res = Net::HTTP.get(url)
    if @debug
      puts query
      puts res
    end        
  end  
  
  def issueUrl( issue )
    return @config['server'] + '/browse/' + issue.key
  end

  def updateIssue( issueId, fields )
    return @server.updateIssue( issueId, fields )
  end

  def createIssue2( project, summary, description, type, assignee, priorityKey )
    return createIssue( project, summary, description, type, assignee, priorityKey, nil )
  end

  def createIssue( project, summary, description, type, assignee, priorityKey, component )

    issue = RemoteIssue.new()
    issue.project = project
    issue.summary = summary
    issue.description = description 
    issue.type = type
    issue.assignee = assignee

    priority = @jira['priority'][priorityKey]
    issue.priority = priority
    
    if @debug
      puts "project: #{project}"
      puts "summary #{summary}"
      puts "description: #{description}"
      puts "type #{type}"
      puts "assignee: #{assignee}"
      puts "priorityKey: #{priorityKey} "
      puts "priority: #{priority}"
    end

    if component != nil
      issue.components = [ component ]
    end
 
    issue = @server.createIssue( @token, issue )
    return issue,assignee
  end

  #----------------------------------------------------------------
  # Permission Schemes
  #----------------------------------------------------------------

  # The issue is a struct with all the issue information
  def assignPermissionScheme( project, notificationSchemeId )
    projectId = project['id']
    query = "#{@config['server']}/secure/project/SelectProjectPermissionScheme.jspa?schemeIds=#{notificationSchemeId}&projectId=#{projectId}&Associate=Associate&os_username=#{@config['user']}&os_password=#{@config['password']}"
    url = URI.parse(query)
    res = Net::HTTP.get(url)
  end  
  
  #----------------------------------------------------------------
  # Notification Schemes
  #----------------------------------------------------------------  
  
  # The issue is a struct with all the issue information
  def assignNotificationScheme( project, notificationSchemeId )
    projectId = project['id']
    query = "#{@config['server']}/secure/project/SelectProjectScheme.jspa?schemeIds=#{notificationSchemeId}&projectId=#{projectId}&Associate=Associate&os_username=#{@config['user']}&os_password=#{@config['password']}"
    url = URI.parse(query)
    res = Net::HTTP.get(url)
  end  

  def generateXdoc( filterName )
    document = REXML::Element.new( "document" )
    properties = REXML::Element.new( "properties" )
    properties.add_element( "title" ).add_text( "JIRA Issues" )
    properties.add_element( "author", { "email" => "jason@maven.org" } )
    document.add_element( properties )
    body = REXML::Element.new( "body" )
    document.add_element( body )
    section = REXML::Element.new( "section" )
    section.attributes['name'] = "JIRA Issues"
    body.add_element( section )
    table = REXML::Element.new( "table" )        
    section.add_element( table )
    issues = issuesByFilter( 'patches' )
    
    for issue in issues
      tr = REXML::Element.new( "tr" )
      tr.add_element( "td" ).add_text( issue.summary )
      a = REXML::Element.new( "a" ).add_text( issue.key )
      a.attributes['href'] = issueUrl( issue )
      tr.add_element( "td" ).add_element( a )
      table.add_element( tr )
    end

    return document

  end

  #----------------------------------------------------------------
  # Components
  #----------------------------------------------------------------
  
  def removeComponent( id )
    url = CGI.escape( url )
    description = CGI.escape( description )
    name = CGI.escape( name )
    query = "#{@config['server']}/secure/project/DeleteComponent.jspa?action=remove&cid=#{cid}&confirm=true&Delete=Delete&os_username=#{@config['user']}&os_password=#{@config['password']}"
    request = URI.parse(query)
    res = Net::HTTP.get(request)
    puts res
  end
  
  # Create component mappings
  # Create filter mappings
  def setup( projectKey )
    components = getServer().getComponents( projectKey )
    puts components
    y = YAML::Store.new( "components2.yaml", :Indent => 2 )
    y.transaction do
      y['components'] = components
    end                
  end
end
