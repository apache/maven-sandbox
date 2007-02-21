#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()

components = im.getComponents( "MNG" )

components.each do | c |
  if c.name =~ /plugin$/
    puts "Removing #{c.name} with id = #{c.id} ..."
    im.removeComponent( c.id )
  end
end
