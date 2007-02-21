#!/usr/bin/ruby

require 'base64'
require 'xmlrpc/client'
require 'yaml'

configuration = YAML::load( File.open( 'patch_server.yaml' ) )
server = XMLRPC::Client.new2( "http://localhost:#{configuration['port']}" )
server.call2( "patch.accept", "foo2.txt", Base64.encode64( "this is the text" ) )
