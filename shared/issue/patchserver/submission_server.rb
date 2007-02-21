#!/usr/bin/ruby

require 'fileutils'
require "xmlrpc/server"
require 'yaml'
require 'base64'

class PatchServer
  def initialize
    configuration = YAML::load( File.open( 'patch_server.yaml' ) )    

    patch_directory = configuration['patch_directory']    
    bundle_directory = configuration['bundle_directory']

    if !FileTest.directory?( patch_directory )
      FileUtils.mkdir( patch_directory )
    end

    if !FileTest.directory?( bundle_directory )
      FileUtils.mkdir( bundle_directory )
    end

    server = XMLRPC::Server.new( configuration['port'] )
    server.add_handler( "patch", SubmissionHandler.new( patch_directory ) )
    server.add_handler( "bundle", SubmissionHandler.new( bundle_directory ) )
    trap("INT") { server.shutdown() }
    server.serve                
  end

end

class SubmissionHandler
  
  def initialize( dir )
    @dir = dir
  end

  def echo( text )
    return text
  end

  def accept( file_name, content )
    file_name = "#{@dir}/#{file_name}"
    file = File.new( file_name, "w" )
    file.sync = true
    file.print( Base64.decode64( content ) )
    file.close    
    return "ok"  
  end

end

server = PatchServer.new
