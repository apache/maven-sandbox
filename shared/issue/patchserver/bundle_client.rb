#!/usr/bin/ruby

require 'base64'
require 'xmlrpc/client'
require 'yaml'

if ARGV[0] == nil
  puts "", "You must specify a bundle to transfer!", ""
  exit
end

bundle = ARGV[0]
configuration = YAML::load( File.open( 'submission_server.yaml' ) )
server = XMLRPC::Client.new2( configuration['url'] )
server.call2( "bundle.accept", bundle, Base64.encode64( File.new( bundle, "r" ).read ) )
