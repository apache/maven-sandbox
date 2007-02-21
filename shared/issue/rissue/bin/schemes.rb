#!/usr/bin/ruby

require 'rubygems'
require_gem 'jiraruby'

im = IssueManager.new()

s = im.getPermissionSchemes()

s.each do | scheme |
  puts scheme
end
