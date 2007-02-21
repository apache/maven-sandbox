#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()
issue = im.getIssue( "MNG-500" )
puts issue.id
puts issue.assignee
