#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()
projects = im.getProjectsNoSchemes()
projects.each do | p |
  if p.name =~ /Maven 2.x/
    puts "#{p.id},#{p.name},#{p.url},#{p.lead},#{p.description},3"
  end
end
