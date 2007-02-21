#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new(true)

File.open("projects.txt") do | file |
  file.each_line do | line |
    pid = line.chomp.split(",")
    im.assignWorkflowScheme( pid, "10011" )
  end
end
