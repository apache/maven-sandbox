#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

filterId = "11464"

im = IssueManager.new( true )
issues = im.getIssuesFromFilter( filterId )

issues.each do | i |
  im.closeIssue( i, 'jason', "12228" )
end
