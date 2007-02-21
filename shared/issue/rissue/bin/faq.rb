#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()
issue,assignee = im.createIssue2( 'MNGFAQ', ARGV.join(" "), '', '1', 'jdcasey', 'minor' )
im.closeIssue( issue,assignee )
