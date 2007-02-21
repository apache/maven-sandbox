require 'default.rb'

class JiraSoapService
  # SYNOPSIS
  #   getProjectsNoSchemes(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getProjectsNoSchemesReturn ArrayOf_tns1_RemoteProject - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteProject
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getProjectsNoSchemes(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getIssuesFromTextSearchWithProject(in0, in1, in2, in3)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             ArrayOf_xsd_string - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_xsd_string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #   in3             Int - {http://www.w3.org/2001/XMLSchema}int
  #
  # RETURNS
  #   getIssuesFromTextSearchWithProjectReturn ArrayOf_tns1_RemoteIssue - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getIssuesFromTextSearchWithProject(in0, in1, in2, in3)
    p [in0, in1, in2, in3]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getIssuesFromTextSearch(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getIssuesFromTextSearchReturn ArrayOf_tns1_RemoteIssue - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getIssuesFromTextSearch(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getIssuesFromFilter(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getIssuesFromFilterReturn ArrayOf_tns1_RemoteIssue - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getIssuesFromFilter(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   refreshCustomFields(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def refreshCustomFields(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   addVersion(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             RemoteVersion - {http://beans.soap.rpc.jira.atlassian.com}RemoteVersion
  #
  # RETURNS
  #   addVersionReturn RemoteVersion - {http://beans.soap.rpc.jira.atlassian.com}RemoteVersion
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def addVersion(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getCustomFields(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getCustomFieldsReturn ArrayOf_tns1_RemoteField - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteField
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getCustomFields(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   progressWorkflowAction(in0, in1, in2, in3)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #   in3             ArrayOf_tns1_RemoteFieldValue - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteFieldValue
  #
  # RETURNS
  #   progressWorkflowActionReturn RemoteIssue - {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def progressWorkflowAction(in0, in1, in2, in3)
    p [in0, in1, in2, in3]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getFieldsForAction(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getFieldsForActionReturn ArrayOf_tns1_RemoteField - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteField
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getFieldsForAction(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getAvailableActions(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getAvailableActionsReturn ArrayOf_tns1_RemoteNamedObject - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteNamedObject
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getAvailableActions(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getFieldsForEdit(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getFieldsForEditReturn ArrayOf_tns1_RemoteField - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteField
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def getFieldsForEdit(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   updateIssue(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             ArrayOf_tns1_RemoteFieldValue - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteFieldValue
  #
  # RETURNS
  #   updateIssueReturn RemoteIssue - {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException
  #
  def updateIssue(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   addComment(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             RemoteComment - {http://beans.soap.rpc.jira.atlassian.com}RemoteComment
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def addComment(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   deletePermissionScheme(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def deletePermissionScheme(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   deletePermissionFrom(in0, in1, in2, in3)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemotePermissionScheme - {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
  #   in2             RemotePermission - {http://beans.soap.rpc.jira.atlassian.com}RemotePermission
  #   in3             RemoteEntity - {http://beans.soap.rpc.jira.atlassian.com}RemoteEntity
  #
  # RETURNS
  #   deletePermissionFromReturn RemotePermissionScheme - {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def deletePermissionFrom(in0, in1, in2, in3)
    p [in0, in1, in2, in3]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   addPermissionTo(in0, in1, in2, in3)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemotePermissionScheme - {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
  #   in2             RemotePermission - {http://beans.soap.rpc.jira.atlassian.com}RemotePermission
  #   in3             RemoteEntity - {http://beans.soap.rpc.jira.atlassian.com}RemoteEntity
  #
  # RETURNS
  #   addPermissionToReturn RemotePermissionScheme - {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def addPermissionTo(in0, in1, in2, in3)
    p [in0, in1, in2, in3]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   createPermissionScheme(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   createPermissionSchemeReturn RemotePermissionScheme - {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def createPermissionScheme(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getAllPermissions(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getAllPermissionsReturn ArrayOf_tns1_RemotePermission - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePermission
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getAllPermissions(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getSecuritySchemes(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getSecuritySchemesReturn ArrayOf_tns1_RemoteScheme - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteScheme
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getSecuritySchemes(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getPermissionSchemes(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getPermissionSchemesReturn ArrayOf_tns1_RemotePermissionScheme - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePermissionScheme
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getPermissionSchemes(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getNotificationSchemes(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getNotificationSchemesReturn ArrayOf_tns1_RemoteScheme - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteScheme
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getNotificationSchemes(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   deleteProject(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def deleteProject(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   updateProject(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemoteProject - {http://beans.soap.rpc.jira.atlassian.com}RemoteProject
  #
  # RETURNS
  #   updateProjectReturn RemoteProject - {http://beans.soap.rpc.jira.atlassian.com}RemoteProject
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def updateProject(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   createProjectFromObject(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemoteProject - {http://beans.soap.rpc.jira.atlassian.com}RemoteProject
  #
  # RETURNS
  #   createProjectFromObjectReturn RemoteProject - {http://beans.soap.rpc.jira.atlassian.com}RemoteProject
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def createProjectFromObject(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   createProject(in0, in1, in2, in3, in4, in5, in6, in7, in8)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #   in3             String - {http://www.w3.org/2001/XMLSchema}string
  #   in4             String - {http://www.w3.org/2001/XMLSchema}string
  #   in5             String - {http://www.w3.org/2001/XMLSchema}string
  #   in6             RemotePermissionScheme - {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
  #   in7             RemoteScheme - {http://beans.soap.rpc.jira.atlassian.com}RemoteScheme
  #   in8             RemoteScheme - {http://beans.soap.rpc.jira.atlassian.com}RemoteScheme
  #
  # RETURNS
  #   createProjectReturn RemoteProject - {http://beans.soap.rpc.jira.atlassian.com}RemoteProject
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def createProject(in0, in1, in2, in3, in4, in5, in6, in7, in8)
    p [in0, in1, in2, in3, in4, in5, in6, in7, in8]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   deleteIssue(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def deleteIssue(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   addAttachmentToIssue(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             ArrayOf_xsd_string - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_xsd_string
  #   in2             RemoteIssue - {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
  #
  # RETURNS
  #   addAttachmentToIssueReturn Boolean - {http://www.w3.org/2001/XMLSchema}boolean
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def addAttachmentToIssue(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   createIssue(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemoteIssue - {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
  #
  # RETURNS
  #   createIssueReturn RemoteIssue - {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def createIssue(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getComments(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getCommentsReturn ArrayOf_tns1_RemoteComment - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteComment
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getComments(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getIssue(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getIssueReturn  RemoteIssue - {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getIssue(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getSavedFilters(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getSavedFiltersReturn ArrayOf_tns1_RemoteFilter - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteFilter
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getSavedFilters(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   deleteGroup(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def deleteGroup(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   updateGroup(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemoteGroup - {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
  #
  # RETURNS
  #   updateGroupReturn RemoteGroup - {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def updateGroup(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   removeUserFromGroup(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemoteGroup - {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
  #   in2             RemoteUser - {http://beans.soap.rpc.jira.atlassian.com}RemoteUser
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def removeUserFromGroup(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   addUserToGroup(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             RemoteGroup - {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
  #   in2             RemoteUser - {http://beans.soap.rpc.jira.atlassian.com}RemoteUser
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def addUserToGroup(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   createGroup(in0, in1, in2)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             RemoteUser - {http://beans.soap.rpc.jira.atlassian.com}RemoteUser
  #
  # RETURNS
  #   createGroupReturn RemoteGroup - {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def createGroup(in0, in1, in2)
    p [in0, in1, in2]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getGroup(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getGroupReturn  RemoteGroup - {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getGroup(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   deleteUser(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   N/A
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def deleteUser(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   createUser(in0, in1, in2, in3, in4)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #   in2             String - {http://www.w3.org/2001/XMLSchema}string
  #   in3             String - {http://www.w3.org/2001/XMLSchema}string
  #   in4             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   createUserReturn RemoteUser - {http://beans.soap.rpc.jira.atlassian.com}RemoteUser
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteValidationException - {http://exception.rpc.jira.atlassian.com}RemoteValidationException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def createUser(in0, in1, in2, in3, in4)
    p [in0, in1, in2, in3, in4]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getUser(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getUserReturn   RemoteUser - {http://beans.soap.rpc.jira.atlassian.com}RemoteUser
  #
  # RAISES
  #   #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getUser(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getResolutions(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getResolutionsReturn ArrayOf_tns1_RemoteResolution - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteResolution
  #
  # RAISES
  #   #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getResolutions(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getStatuses(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getStatusesReturn ArrayOf_tns1_RemoteStatus - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteStatus
  #
  # RAISES
  #   #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getStatuses(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getPriorities(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getPrioritiesReturn ArrayOf_tns1_RemotePriority - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePriority
  #
  # RAISES
  #   #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getPriorities(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getSubTaskIssueTypes(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getSubTaskIssueTypesReturn ArrayOf_tns1_RemoteIssueType - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssueType
  #
  # RAISES
  #   #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getSubTaskIssueTypes(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getIssueTypes(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getIssueTypesReturn ArrayOf_tns1_RemoteIssueType - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssueType
  #
  # RAISES
  #   #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getIssueTypes(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getComponents(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getComponentsReturn ArrayOf_tns1_RemoteComponent - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteComponent
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getComponents(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getVersions(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getVersionsReturn ArrayOf_tns1_RemoteVersion - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteVersion
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getVersions(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getProjects(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getProjectsReturn ArrayOf_tns1_RemoteProject - {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteProject
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemotePermissionException - {http://exception.rpc.jira.atlassian.com}RemotePermissionException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def getProjects(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   getServerInfo(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   getServerInfoReturn RemoteServerInfo - {http://beans.soap.rpc.jira.atlassian.com}RemoteServerInfo
  #
  def getServerInfo(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   logout(in0)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   logoutReturn    Boolean - {http://www.w3.org/2001/XMLSchema}boolean
  #
  def logout(in0)
    p [in0]
    raise NotImplementedError.new
  end

  # SYNOPSIS
  #   login(in0, in1)
  #
  # ARGS
  #   in0             String - {http://www.w3.org/2001/XMLSchema}string
  #   in1             String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RETURNS
  #   loginReturn     String - {http://www.w3.org/2001/XMLSchema}string
  #
  # RAISES
  #   #   fault           RemoteException - {http://exception.rpc.jira.atlassian.com}RemoteException, #   fault           RemoteAuthenticationException - {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
  #
  def login(in0, in1)
    p [in0, in1]
    raise NotImplementedError.new
  end
end

