#!/usr/bin/ruby

require 'net/http'
require 'rexml/document'
include Net
include REXML

if ( ARGV.length < 4 )
  puts "\nUsage #{$0} <group-id> <artifact-id> <target-repository-id> <target-repository-url> [<maven-options>]\n\n"
  exit -1
end

group_id = ARGV[0].gsub( '.', '/' )
artifact_id = ARGV[1]
target_repo_id = ARGV[2]
target_repo_url = ARGV[3]

maven_args = ARGV[4, ARGV.length - 4]
debug = maven_args.index( '-X' )

if ( debug )
  system('echo \$PATH = $PATH')
  pwd = `pwd`
  puts "Executing in: #{pwd}"
end

source_user = 'deployment'
source_passwd = 'f00by12'

source_repo_url = "http://repository.sonatype.org:8081/nexus/content/repositories/staged-releases/#{artifact_id}"
source_repo_id = "staged.releases"

#maven_command = '/Users/jdcasey/apps/maven/apache-maven-2.0.8/bin/mvn'
maven_command = '/opt/maven/maven-2.0.7/bin/mvn'

puts "Resolving LATEST version for staged release.\nArtifact-Id: #{artifact_id}\nRepository URL: #{source_repo_url}"

md_path = "#{source_repo_url}/#{group_id}/#{artifact_id}/maven-metadata.xml"

puts "Reading: #{md_path}"
md_response = HTTP.get_response( URI.parse( md_path ) )

puts "Got response:\n\n'#{md_response.body}'\n\n" if debug

if not md_response.kind_of?( HTTPSuccess )
  puts "Bad HTTP response (#{md_response})."
  exit -5000
end

md_doc = Document.new( md_response.body )

md_xpath = '/metadata/versioning/release'

version = md_doc.get_elements( md_xpath )[0].text
puts "Using release version: #{version}"

command = String.new( maven_command )
command << " org.apache.maven.plugins:maven-stage-plugin:1.0-alpha-1:copy"
command << " -DsourceRepositoryId=#{source_repo_id}"
command << " -Dsource=#{source_repo_url}"
command << " -DtargetRepositoryId=#{target_repo_id}"
command << " -Dtarget=#{target_repo_url}"
command << " -Dversion=#{version}"

if ( maven_args != nil )
  maven_args.each {|arg| command << " #{arg}"}
else
  command << " -e"
end

puts "Executing Maven:\n\n'#{command}'\n\n" if debug

if debug
  puts "Sanity check: Maven version at: #{maven_command} is:"
  puts system("#{maven_command} -v")
  puts "\n\n"
end

STDOUT.sync = true

system(command)

exit_code = $?.exitstatus

if ( exit_code != 0 )
  puts "Maven call failed, exiting with code: #{exit_code}"
  exit exit_code
end

puts "Cleaning staging repository: #{source_repo_url}"

maint_url = URI.parse( source_repo_url )

maint_req = HTTP::Delete.new( maint_url.path )
maint_req.basic_auth( source_user, source_passwd )

maint_response = HTTP.start( maint_url.host, maint_url.port ) do |http|
  http.request( maint_req )
end

puts "Got response:\n\n'#{maint_response.body}'\n\n" if debug

if not maint_response.kind_of?( HTTPSuccess )
  puts "Bad HTTP response (#{maint_response})."
  exit -10000
end

