require 'default.rb'

require 'soap/rpc/driver'

class JiraSoapService < ::SOAP::RPC::Driver
  DefaultEndpointUrl = "http://jira.codehaus.org/rpc/soap/jirasoapservice-v2"
  MappingRegistry = ::SOAP::Mapping::Registry.new

  MappingRegistry.set(
    ArrayOf_tns1_RemoteProject,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteProject") }
  )
  MappingRegistry.set(
    ArrayOf_xsd_string,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://www.w3.org/2001/XMLSchema", "string") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteIssue,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue") }
  )
  MappingRegistry.set(
    RemoteVersion,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteVersion") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteField,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteField") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteFieldValue,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteFieldValue") }
  )
  MappingRegistry.set(
    RemoteIssue,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteVersion,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteVersion") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteComponent,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteComponent") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteCustomFieldValue,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteCustomFieldValue") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteNamedObject,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteNamedObject") }
  )
  MappingRegistry.set(
    RemoteComment,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteComment") }
  )
  MappingRegistry.set(
    RemotePermissionScheme,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemotePermissionMapping,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionMapping") }
  )
  MappingRegistry.set(
    RemotePermission,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePermission") }
  )
  MappingRegistry.set(
    RemoteEntity,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteEntity") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemotePermission,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePermission") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteScheme,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteScheme") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemotePermissionScheme,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme") }
  )
  MappingRegistry.set(
    RemoteProject,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteProject") }
  )
  MappingRegistry.set(
    RemoteScheme,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteScheme") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteComment,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteComment") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteFilter,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteFilter") }
  )
  MappingRegistry.set(
    RemoteGroup,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteUser,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteUser") }
  )
  MappingRegistry.set(
    RemoteUser,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteUser") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteResolution,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteResolution") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteStatus,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteStatus") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemotePriority,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePriority") }
  )
  MappingRegistry.set(
    ArrayOf_tns1_RemoteIssueType,
    ::SOAP::SOAPArray,
    ::SOAP::Mapping::Registry::TypedArrayFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteIssueType") }
  )
  MappingRegistry.set(
    RemoteServerInfo,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteServerInfo") }
  )
  MappingRegistry.set(
    RemoteField,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteField") }
  )
  MappingRegistry.set(
    RemoteFieldValue,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteFieldValue") }
  )
  MappingRegistry.set(
    RemoteComponent,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteComponent") }
  )
  MappingRegistry.set(
    RemoteCustomFieldValue,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteCustomFieldValue") }
  )
  MappingRegistry.set(
    RemoteNamedObject,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteNamedObject") }
  )
  MappingRegistry.set(
    RemotePermissionMapping,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionMapping") }
  )
  MappingRegistry.set(
    RemoteFilter,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteFilter") }
  )
  MappingRegistry.set(
    RemoteResolution,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteResolution") }
  )
  MappingRegistry.set(
    RemoteStatus,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteStatus") }
  )
  MappingRegistry.set(
    RemotePriority,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemotePriority") }
  )
  MappingRegistry.set(
    RemoteIssueType,
    ::SOAP::SOAPStruct,
    ::SOAP::Mapping::Registry::TypedStructFactory,
    { :type => XSD::QName.new("http://beans.soap.rpc.jira.atlassian.com", "RemoteIssueType") }
  )

  Methods = [
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getProjectsNoSchemes"),
      "",
      "getProjectsNoSchemes",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getProjectsNoSchemesReturn", ["RemoteProject[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getIssuesFromTextSearchWithProject"),
      "",
      "getIssuesFromTextSearchWithProject",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["String[]", "http://www.w3.org/2001/XMLSchema", "string"]],
        ["in", "in2", ["::SOAP::SOAPString"]],
        ["in", "in3", ["::SOAP::SOAPInt"]],
        ["retval", "getIssuesFromTextSearchWithProjectReturn", ["RemoteIssue[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getIssuesFromTextSearch"),
      "",
      "getIssuesFromTextSearch",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getIssuesFromTextSearchReturn", ["RemoteIssue[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getIssuesFromFilter"),
      "",
      "getIssuesFromFilter",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getIssuesFromFilterReturn", ["RemoteIssue[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "refreshCustomFields"),
      "",
      "refreshCustomFields",
      [ ["in", "in0", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "addVersion"),
      "",
      "addVersion",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["RemoteVersion", "http://beans.soap.rpc.jira.atlassian.com", "RemoteVersion"]],
        ["retval", "addVersionReturn", ["RemoteVersion", "http://beans.soap.rpc.jira.atlassian.com", "RemoteVersion"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getCustomFields"),
      "",
      "getCustomFields",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getCustomFieldsReturn", ["RemoteField[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteField"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "progressWorkflowAction"),
      "",
      "progressWorkflowAction",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["::SOAP::SOAPString"]],
        ["in", "in3", ["RemoteFieldValue[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteFieldValue"]],
        ["retval", "progressWorkflowActionReturn", ["RemoteIssue", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getFieldsForAction"),
      "",
      "getFieldsForAction",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["::SOAP::SOAPString"]],
        ["retval", "getFieldsForActionReturn", ["RemoteField[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteField"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getAvailableActions"),
      "",
      "getAvailableActions",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getAvailableActionsReturn", ["RemoteNamedObject[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteNamedObject"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getFieldsForEdit"),
      "",
      "getFieldsForEdit",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getFieldsForEditReturn", ["RemoteField[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteField"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "updateIssue"),
      "",
      "updateIssue",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["RemoteFieldValue[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteFieldValue"]],
        ["retval", "updateIssueReturn", ["RemoteIssue", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "addComment"),
      "",
      "addComment",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["RemoteComment", "http://beans.soap.rpc.jira.atlassian.com", "RemoteComment"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "deletePermissionScheme"),
      "",
      "deletePermissionScheme",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "deletePermissionFrom"),
      "",
      "deletePermissionFrom",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemotePermissionScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]],
        ["in", "in2", ["RemotePermission", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermission"]],
        ["in", "in3", ["RemoteEntity", "http://beans.soap.rpc.jira.atlassian.com", "RemoteEntity"]],
        ["retval", "deletePermissionFromReturn", ["RemotePermissionScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "addPermissionTo"),
      "",
      "addPermissionTo",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemotePermissionScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]],
        ["in", "in2", ["RemotePermission", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermission"]],
        ["in", "in3", ["RemoteEntity", "http://beans.soap.rpc.jira.atlassian.com", "RemoteEntity"]],
        ["retval", "addPermissionToReturn", ["RemotePermissionScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "createPermissionScheme"),
      "",
      "createPermissionScheme",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["::SOAP::SOAPString"]],
        ["retval", "createPermissionSchemeReturn", ["RemotePermissionScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getAllPermissions"),
      "",
      "getAllPermissions",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getAllPermissionsReturn", ["RemotePermission[]", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermission"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getSecuritySchemes"),
      "",
      "getSecuritySchemes",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getSecuritySchemesReturn", ["RemoteScheme[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteScheme"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getPermissionSchemes"),
      "",
      "getPermissionSchemes",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getPermissionSchemesReturn", ["RemotePermissionScheme[]", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getNotificationSchemes"),
      "",
      "getNotificationSchemes",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getNotificationSchemesReturn", ["RemoteScheme[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteScheme"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "deleteProject"),
      "",
      "deleteProject",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "updateProject"),
      "",
      "updateProject",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemoteProject", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]],
        ["retval", "updateProjectReturn", ["RemoteProject", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "createProjectFromObject"),
      "",
      "createProjectFromObject",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemoteProject", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]],
        ["retval", "createProjectFromObjectReturn", ["RemoteProject", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "createProject"),
      "",
      "createProject",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["::SOAP::SOAPString"]],
        ["in", "in3", ["::SOAP::SOAPString"]],
        ["in", "in4", ["::SOAP::SOAPString"]],
        ["in", "in5", ["::SOAP::SOAPString"]],
        ["in", "in6", ["RemotePermissionScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemotePermissionScheme"]],
        ["in", "in7", ["RemoteScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemoteScheme"]],
        ["in", "in8", ["RemoteScheme", "http://beans.soap.rpc.jira.atlassian.com", "RemoteScheme"]],
        ["retval", "createProjectReturn", ["RemoteProject", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "deleteIssue"),
      "",
      "deleteIssue",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "addAttachmentToIssue"),
      "",
      "addAttachmentToIssue",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["String[]", "http://www.w3.org/2001/XMLSchema", "string"]],
        ["in", "in2", ["RemoteIssue", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]],
        ["retval", "addAttachmentToIssueReturn", ["::SOAP::SOAPBoolean"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "createIssue"),
      "",
      "createIssue",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemoteIssue", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]],
        ["retval", "createIssueReturn", ["RemoteIssue", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getComments"),
      "",
      "getComments",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getCommentsReturn", ["RemoteComment[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteComment"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getIssue"),
      "",
      "getIssue",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getIssueReturn", ["RemoteIssue", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssue"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getSavedFilters"),
      "",
      "getSavedFilters",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getSavedFiltersReturn", ["RemoteFilter[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteFilter"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "deleteGroup"),
      "",
      "deleteGroup",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "updateGroup"),
      "",
      "updateGroup",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemoteGroup", "http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup"]],
        ["retval", "updateGroupReturn", ["RemoteGroup", "http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "removeUserFromGroup"),
      "",
      "removeUserFromGroup",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemoteGroup", "http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup"]],
        ["in", "in2", ["RemoteUser", "http://beans.soap.rpc.jira.atlassian.com", "RemoteUser"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "addUserToGroup"),
      "",
      "addUserToGroup",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["RemoteGroup", "http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup"]],
        ["in", "in2", ["RemoteUser", "http://beans.soap.rpc.jira.atlassian.com", "RemoteUser"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "createGroup"),
      "",
      "createGroup",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["RemoteUser", "http://beans.soap.rpc.jira.atlassian.com", "RemoteUser"]],
        ["retval", "createGroupReturn", ["RemoteGroup", "http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getGroup"),
      "",
      "getGroup",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getGroupReturn", ["RemoteGroup", "http://beans.soap.rpc.jira.atlassian.com", "RemoteGroup"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "deleteUser"),
      "",
      "deleteUser",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "createUser"),
      "",
      "createUser",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["in", "in2", ["::SOAP::SOAPString"]],
        ["in", "in3", ["::SOAP::SOAPString"]],
        ["in", "in4", ["::SOAP::SOAPString"]],
        ["retval", "createUserReturn", ["RemoteUser", "http://beans.soap.rpc.jira.atlassian.com", "RemoteUser"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getUser"),
      "",
      "getUser",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getUserReturn", ["RemoteUser", "http://beans.soap.rpc.jira.atlassian.com", "RemoteUser"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getResolutions"),
      "",
      "getResolutions",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getResolutionsReturn", ["RemoteResolution[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteResolution"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getStatuses"),
      "",
      "getStatuses",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getStatusesReturn", ["RemoteStatus[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteStatus"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getPriorities"),
      "",
      "getPriorities",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getPrioritiesReturn", ["RemotePriority[]", "http://beans.soap.rpc.jira.atlassian.com", "RemotePriority"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getSubTaskIssueTypes"),
      "",
      "getSubTaskIssueTypes",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getSubTaskIssueTypesReturn", ["RemoteIssueType[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssueType"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getIssueTypes"),
      "",
      "getIssueTypes",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getIssueTypesReturn", ["RemoteIssueType[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteIssueType"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getComponents"),
      "",
      "getComponents",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getComponentsReturn", ["RemoteComponent[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteComponent"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getVersions"),
      "",
      "getVersions",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "getVersionsReturn", ["RemoteVersion[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteVersion"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getProjects"),
      "",
      "getProjects",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getProjectsReturn", ["RemoteProject[]", "http://beans.soap.rpc.jira.atlassian.com", "RemoteProject"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "getServerInfo"),
      "",
      "getServerInfo",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "getServerInfoReturn", ["RemoteServerInfo", "http://beans.soap.rpc.jira.atlassian.com", "RemoteServerInfo"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "logout"),
      "",
      "logout",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["retval", "logoutReturn", ["::SOAP::SOAPBoolean"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ],
    [ XSD::QName.new("http://soap.rpc.jira.atlassian.com", "login"),
      "",
      "login",
      [ ["in", "in0", ["::SOAP::SOAPString"]],
        ["in", "in1", ["::SOAP::SOAPString"]],
        ["retval", "loginReturn", ["::SOAP::SOAPString"]] ],
      { :request_style =>  :rpc, :request_use =>  :encoded,
        :response_style => :rpc, :response_use => :encoded }
    ]
  ]

  def initialize(endpoint_url = nil)
    endpoint_url ||= DefaultEndpointUrl
    super(endpoint_url, nil)
    self.mapping_registry = MappingRegistry
    init_methods
  end

private

  def init_methods
    Methods.each do |definitions|
      opt = definitions.last
      if opt[:request_style] == :document
        add_document_operation(*definitions)
      else
        add_rpc_operation(*definitions)
        qname = definitions[0]
        name = definitions[2]
        if qname.name != name and qname.name.capitalize == name.capitalize
          ::SOAP::Mapping.define_singleton_method(self, qname.name) do |*arg|
            __send__(name, *arg)
          end
        end
      end
    end
  end
end

