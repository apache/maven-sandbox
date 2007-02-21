#!/usr/bin/ruby

# Check for jira.yaml so that a user can create a project
# Make sure that all the required fields are present
#  Name
#  Key
#  Description
#  URL
#  Lead
#  Notification Scheme
#  Permission Scheme

require 'rubygems'
require 'cmdparse'
require_gem 'jiraruby'

def validate( parameter, value )
  if value == nil
    puts "The parameter #{parameter} cannot be null."
    exit
  end
end

cmd = CmdParse::CommandParser.new( true, true )
cmd.program_name = "create_project"
cmd.program_version = [0, 1, 1]
cmd.options = CmdParse::OptionParserWrapper.new do |opt|
  opt.separator "Global options:"
  opt.on("--verbose", "Be verbose when outputting info") {|t| $verbose = true }
end

# Add standard help/version commands
cmd.add_command( CmdParse::HelpCommand.new )
cmd.add_command( CmdParse::VersionCommand.new )

# Creating new JIRA projects
create = CmdParse::Command.new( 'create', false, true )
create.short_desc = "Create JIRA Project"
create.options = CmdParse::OptionParserWrapper.new do |opt|
  opt.on( '-n', '--name=val', String, 'Project Name' ) { |$name| }
  opt.on( '-k', '--key=val', String, 'Project Key' ) { |$key| }
  opt.on( '-d', '--description=val', String, 'Project Description' ) { |$description| }  
  opt.on( '-u', '--url=val', String, 'Project URL' ) { |$url| }  
  opt.on( '-l', '--lead=val', String, 'Project Lead' ) { |$lead| }
  opt.on( '-s', '--notification-scheme=val', String, 'Project Notification Scheme' ) { |$notification_scheme| }
  opt.on( '-p', '--permission-scheme=val', String, 'Project Permission Scheme' ) { |$permission_scheme| }
  opt.on( '-w', '--workflow-scheme=val', String, 'Project Workflow Scheme' ) { |$workflow_scheme| }  
end
create.set_execution_block do |args|
  # Can't figure out how to get cmdparse to do this for me yet. So doing it manually for now.
  validate( "name", $name )
  validate( "key", $key )
  validate( "description", $description )
  validate( "url", $url )
  validate( "lead", $lead )
  validate( "notification-scheme", $notification_scheme )
  validate( "permission-scheme", $permission_scheme )
  # Assigning the workflow is optional
  #validate( "workflow-scheme", $workflow_scheme )  
 
  puts "Using the following information to create a JIRA project:"
  puts "               Name: #{$name}"
  puts "                Key: #{$key}"
  puts "        Description: #{$description}"
  puts "                URL: #{$url}"
  puts "               Lead: #{$lead}"
  puts "Notification Scheme: #{$notification_scheme}"
  puts "  Permission Scheme: #{$permission_scheme}"
  puts "    Workflow Scheme: #{$workflow_scheme}"  
 
  # Now verify that the user wants to continue
  puts "Do you want to continue? (Y/N) [Y] "

  $response = $stdin.gets.chomp

  puts "'#{$response}'"

  if $response.upcase! == "Y"
    puts "Creating project ..."
    im = IssueManager.new()
    # Create the project
    projectId = im.createProject( $key, $name, $description, $url, $lead, $permission_scheme, $notification_scheme )
    puts "Project has been created (projectId=#{projectId})"
    # Update the workflow if requested
    if $workflow_scheme != nil
      im.assignWorkflowScheme( projectId, $workflow_scheme )
    end
  end
end

cmd.add_command( create )

cmd.parse

