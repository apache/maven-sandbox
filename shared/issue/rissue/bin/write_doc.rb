#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()
document = im.generateXdoc( "patches" )
document.write( $stdout, 2 )
