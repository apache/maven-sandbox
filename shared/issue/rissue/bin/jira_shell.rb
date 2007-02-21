#!/usr/bin/ruby -w

require 'readline'

# create commands and then allow the commands to be used by
# - command line
# - interactive shell
# - UI

line = 0
loop do
  eval Readline.readline( '%.3d> ' % line, true)
  line += 1
end
