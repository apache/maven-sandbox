#!/usr/bin/ruby

require 'rubygems'
require 'jiraruby'

im = IssueManager.new()
im.closeIssue( "30737" )
