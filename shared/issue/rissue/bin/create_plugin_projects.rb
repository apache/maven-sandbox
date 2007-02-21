#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()

File.open("projects.txt") do | file |
  file.each_line do | line |
    key,name,description,url,lead,permissionScheme,notificationScheme = line.chomp.split(",")
    puts "Creating #{name} ..."
    im.createProject( key, name, description, url, lead, permissionScheme, notificationScheme )
  end
end
