#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()
issue,assignee = im.createIssue2( 'MNGTEST', 'Super doco', 'FIX IT NOW!!!!!!!! (ruby client creation)', '1', 'jdcasey', 'minor' )
puts issue.id,assignee
