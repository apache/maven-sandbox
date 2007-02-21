require 'rubygems'
require_gem 'jiraruby'

# faq counter: totals, by each contributor. a report would be nice.

class JiraPlugin < Plugin

  def jira(m,params)
    unless(params)
      m.reply "incorrect usage. " + help(m.plugin)
    end
    
    # Using the jira library directly seems to crash rbot ...
    # so i'm just exec'ing the command line tool used to query
    # JIRA from the command line.

    issue = getServer().getIssue( params[:issue].to_s.upcase )
    url = getServer().issueUrl(issue)
    
    m.reply( " " )
    string = ""
    string << "     URL: #{url}\n"
    string << "  Status: #{issue.status}\n"
    string << " Summary: #{issue.summary}\n" 
    string << "Reporter: #{issue.reporter}"
    m.reply( string )
    m.reply( " " )
  end

  # Need to know which person submitted the entry 
  def faqa(m,params)        
    unless(m.params)
      m.reply "incorrect usage. " + help(m.plugin)
    end
    server = getServer()
    who = params[:who]
    summary = params[:phrase].join( " " )
    issue,assignee = server.createIssue2( 'MNGFAQ', summary, 'none', '1', who, 'minor' )
    server.closeIssue( issue,assignee )
  end
  
  # Need to know which person submitted the entry 
  def faqq(m,params)        
    unless(m.params)
      m.reply "incorrect usage. " + help(m.plugin)
    end
    server = getServer()
    who = params[:who]
    summary = params[:phrase].join( " " )
    puts summary,who
    server.createIssue2( 'MNGFAQ', summary, 'none', '1', who, 'minor' )
  end  
  
  def faqc(m,params)
    unless(m.params)
      m.reply "incorrect usage. " + help(m.plugin)
    end
    server = getServer()    
    issueId = params[:issue].upcase!
    who = params[:who]
    issue = server.getIssue( issueId )
    server.closeIssue( issue, who )
  end
  
  def getServer
    return IssueManager.new()
  end
end

plugin = JiraPlugin.new
plugin.map 'jira :issue'
plugin.map 'faqa :who *phrase'
plugin.map 'faqq :who *phrase'
plugin.map 'faqc :who :issue'
plugin.map 'issue :project :summary :description :assignee'
