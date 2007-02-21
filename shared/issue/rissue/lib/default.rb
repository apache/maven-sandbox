require 'xsd/qname'

# {http://beans.soap.rpc.jira.atlassian.com}AbstractRemoteEntity
class AbstractRemoteEntity
  @@schema_type = "AbstractRemoteEntity"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]]
  ]

  attr_accessor :id

  def initialize(id = nil)
    @id = id
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}AbstractNamedRemoteEntity
class AbstractNamedRemoteEntity
  @@schema_type = "AbstractNamedRemoteEntity"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]]
  ]

  attr_accessor :id
  attr_accessor :name

  def initialize(id = nil, name = nil)
    @id = id
    @name = name
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteScheme
class RemoteScheme
  @@schema_type = "RemoteScheme"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["id", ["SOAP::SOAPLong", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["type", ["SOAP::SOAPString", XSD::QName.new(nil, "type")]]
  ]

  attr_accessor :description
  attr_accessor :id
  attr_accessor :name
  attr_accessor :type

  def initialize(description = nil, id = nil, name = nil, type = nil)
    @description = description
    @id = id
    @name = name
    @type = type
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemotePermission
class RemotePermission
  @@schema_type = "RemotePermission"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["permission", ["SOAP::SOAPLong", XSD::QName.new(nil, "permission")]]
  ]

  attr_accessor :name
  attr_accessor :permission

  def initialize(name = nil, permission = nil)
    @name = name
    @permission = permission
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteEntity
class RemoteEntity
  @@schema_type = "RemoteEntity"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = []

  def initialize
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionMapping
class RemotePermissionMapping
  @@schema_type = "RemotePermissionMapping"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["permission", ["RemotePermission", XSD::QName.new(nil, "permission")]],
    ["remoteEntities", ["ArrayOf_tns1_RemoteEntity", XSD::QName.new(nil, "remoteEntities")]]
  ]

  attr_accessor :permission
  attr_accessor :remoteEntities

  def initialize(permission = nil, remoteEntities = nil)
    @permission = permission
    @remoteEntities = remoteEntities
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemotePermissionScheme
class RemotePermissionScheme
  @@schema_type = "RemotePermissionScheme"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["id", ["SOAP::SOAPLong", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["type", ["SOAP::SOAPString", XSD::QName.new(nil, "type")]],
    ["permissionMappings", ["ArrayOf_tns1_RemotePermissionMapping", XSD::QName.new(nil, "permissionMappings")]]
  ]

  attr_accessor :description
  attr_accessor :id
  attr_accessor :name
  attr_accessor :type
  attr_accessor :permissionMappings

  def initialize(description = nil, id = nil, name = nil, type = nil, permissionMappings = nil)
    @description = description
    @id = id
    @name = name
    @type = type
    @permissionMappings = permissionMappings
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteProject
class RemoteProject
  @@schema_type = "RemoteProject"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["issueSecurityScheme", ["RemoteScheme", XSD::QName.new(nil, "issueSecurityScheme")]],
    ["key", ["SOAP::SOAPString", XSD::QName.new(nil, "key")]],
    ["lead", ["SOAP::SOAPString", XSD::QName.new(nil, "lead")]],
    ["notificationScheme", ["RemoteScheme", XSD::QName.new(nil, "notificationScheme")]],
    ["permissionScheme", ["RemotePermissionScheme", XSD::QName.new(nil, "permissionScheme")]],
    ["projectUrl", ["SOAP::SOAPString", XSD::QName.new(nil, "projectUrl")]],
    ["url", ["SOAP::SOAPString", XSD::QName.new(nil, "url")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :description
  attr_accessor :issueSecurityScheme
  attr_accessor :key
  attr_accessor :lead
  attr_accessor :notificationScheme
  attr_accessor :permissionScheme
  attr_accessor :projectUrl
  attr_accessor :url

  def initialize(id = nil, name = nil, description = nil, issueSecurityScheme = nil, key = nil, lead = nil, notificationScheme = nil, permissionScheme = nil, projectUrl = nil, url = nil)
    @id = id
    @name = name
    @description = description
    @issueSecurityScheme = issueSecurityScheme
    @key = key
    @lead = lead
    @notificationScheme = notificationScheme
    @permissionScheme = permissionScheme
    @projectUrl = projectUrl
    @url = url
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteVersion
class RemoteVersion
  @@schema_type = "RemoteVersion"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["archived", ["SOAP::SOAPBoolean", XSD::QName.new(nil, "archived")]],
    ["releaseDate", ["SOAP::SOAPDateTime", XSD::QName.new(nil, "releaseDate")]],
    ["released", ["SOAP::SOAPBoolean", XSD::QName.new(nil, "released")]],
    ["sequence", ["SOAP::SOAPLong", XSD::QName.new(nil, "sequence")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :archived
  attr_accessor :releaseDate
  attr_accessor :released
  attr_accessor :sequence

  def initialize(id = nil, name = nil, archived = nil, releaseDate = nil, released = nil, sequence = nil)
    @id = id
    @name = name
    @archived = archived
    @releaseDate = releaseDate
    @released = released
    @sequence = sequence
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteComponent
class RemoteComponent
  @@schema_type = "RemoteComponent"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]]
  ]

  attr_accessor :id
  attr_accessor :name

  def initialize(id = nil, name = nil)
    @id = id
    @name = name
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteCustomFieldValue
class RemoteCustomFieldValue
  @@schema_type = "RemoteCustomFieldValue"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["customfieldId", ["SOAP::SOAPString", XSD::QName.new(nil, "customfieldId")]],
    ["key", ["SOAP::SOAPString", XSD::QName.new(nil, "key")]],
    ["values", ["ArrayOf_xsd_string", XSD::QName.new(nil, "values")]]
  ]

  attr_accessor :customfieldId
  attr_accessor :key
  attr_accessor :values

  def initialize(customfieldId = nil, key = nil, values = nil)
    @customfieldId = customfieldId
    @key = key
    @values = values
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteIssue
class RemoteIssue
  @@schema_type = "RemoteIssue"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["affectsVersions", ["ArrayOf_tns1_RemoteVersion", XSD::QName.new(nil, "affectsVersions")]],
    ["assignee", ["SOAP::SOAPString", XSD::QName.new(nil, "assignee")]],
    ["attachmentNames", ["ArrayOf_xsd_string", XSD::QName.new(nil, "attachmentNames")]],
    ["components", ["ArrayOf_tns1_RemoteComponent", XSD::QName.new(nil, "components")]],
    ["created", ["SOAP::SOAPDateTime", XSD::QName.new(nil, "created")]],
    ["customFieldValues", ["ArrayOf_tns1_RemoteCustomFieldValue", XSD::QName.new(nil, "customFieldValues")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["duedate", ["SOAP::SOAPDateTime", XSD::QName.new(nil, "duedate")]],
    ["environment", ["SOAP::SOAPString", XSD::QName.new(nil, "environment")]],
    ["fixVersions", ["ArrayOf_tns1_RemoteVersion", XSD::QName.new(nil, "fixVersions")]],
    ["key", ["SOAP::SOAPString", XSD::QName.new(nil, "key")]],
    ["priority", ["SOAP::SOAPString", XSD::QName.new(nil, "priority")]],
    ["project", ["SOAP::SOAPString", XSD::QName.new(nil, "project")]],
    ["reporter", ["SOAP::SOAPString", XSD::QName.new(nil, "reporter")]],
    ["resolution", ["SOAP::SOAPString", XSD::QName.new(nil, "resolution")]],
    ["status", ["SOAP::SOAPString", XSD::QName.new(nil, "status")]],
    ["summary", ["SOAP::SOAPString", XSD::QName.new(nil, "summary")]],
    ["type", ["SOAP::SOAPString", XSD::QName.new(nil, "type")]],
    ["updated", ["SOAP::SOAPDateTime", XSD::QName.new(nil, "updated")]],
    ["votes", ["SOAP::SOAPLong", XSD::QName.new(nil, "votes")]]
  ]

  attr_accessor :id
  attr_accessor :affectsVersions
  attr_accessor :assignee
  attr_accessor :attachmentNames
  attr_accessor :components
  attr_accessor :created
  attr_accessor :customFieldValues
  attr_accessor :description
  attr_accessor :duedate
  attr_accessor :environment
  attr_accessor :fixVersions
  attr_accessor :key
  attr_accessor :priority
  attr_accessor :project
  attr_accessor :reporter
  attr_accessor :resolution
  attr_accessor :status
  attr_accessor :summary
  attr_accessor :type
  attr_accessor :updated
  attr_accessor :votes

  def initialize(id = nil, affectsVersions = nil, assignee = nil, attachmentNames = nil, components = nil, created = nil, customFieldValues = nil, description = nil, duedate = nil, environment = nil, fixVersions = nil, key = nil, priority = nil, project = nil, reporter = nil, resolution = nil, status = nil, summary = nil, type = nil, updated = nil, votes = nil)
    @id = id
    @affectsVersions = affectsVersions
    @assignee = assignee
    @attachmentNames = attachmentNames
    @components = components
    @created = created
    @customFieldValues = customFieldValues
    @description = description
    @duedate = duedate
    @environment = environment
    @fixVersions = fixVersions
    @key = key
    @priority = priority
    @project = project
    @reporter = reporter
    @resolution = resolution
    @status = status
    @summary = summary
    @type = type
    @updated = updated
    @votes = votes
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteField
class RemoteField
  @@schema_type = "RemoteField"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]]
  ]

  attr_accessor :id
  attr_accessor :name

  def initialize(id = nil, name = nil)
    @id = id
    @name = name
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteFieldValue
class RemoteFieldValue
  @@schema_type = "RemoteFieldValue"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["values", ["ArrayOf_xsd_string", XSD::QName.new(nil, "values")]]
  ]

  attr_accessor :id
  attr_accessor :values

  def initialize(id = nil, values = nil)
    @id = id
    @values = values
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteNamedObject
class RemoteNamedObject
  @@schema_type = "RemoteNamedObject"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]]
  ]

  attr_accessor :id
  attr_accessor :name

  def initialize(id = nil, name = nil)
    @id = id
    @name = name
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteComment
class RemoteComment
  @@schema_type = "RemoteComment"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["body", ["SOAP::SOAPString", XSD::QName.new(nil, "body")]],
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["level", ["SOAP::SOAPString", XSD::QName.new(nil, "level")]],
    ["timePerformed", ["SOAP::SOAPDateTime", XSD::QName.new(nil, "timePerformed")]],
    ["username", ["SOAP::SOAPString", XSD::QName.new(nil, "username")]]
  ]

  attr_accessor :body
  attr_accessor :id
  attr_accessor :level
  attr_accessor :timePerformed
  attr_accessor :username

  def initialize(body = nil, id = nil, level = nil, timePerformed = nil, username = nil)
    @body = body
    @id = id
    @level = level
    @timePerformed = timePerformed
    @username = username
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteUser
class RemoteUser
  @@schema_type = "RemoteUser"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["email", ["SOAP::SOAPString", XSD::QName.new(nil, "email")]],
    ["fullname", ["SOAP::SOAPString", XSD::QName.new(nil, "fullname")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]]
  ]

  attr_accessor :email
  attr_accessor :fullname
  attr_accessor :name

  def initialize(email = nil, fullname = nil, name = nil)
    @email = email
    @fullname = fullname
    @name = name
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteGroup
class RemoteGroup
  @@schema_type = "RemoteGroup"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["users", ["ArrayOf_tns1_RemoteUser", XSD::QName.new(nil, "users")]]
  ]

  attr_accessor :name
  attr_accessor :users

  def initialize(name = nil, users = nil)
    @name = name
    @users = users
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteFilter
class RemoteFilter
  @@schema_type = "RemoteFilter"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["author", ["SOAP::SOAPString", XSD::QName.new(nil, "author")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["project", ["SOAP::SOAPString", XSD::QName.new(nil, "project")]],
    ["xml", ["SOAP::SOAPString", XSD::QName.new(nil, "xml")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :author
  attr_accessor :description
  attr_accessor :project
  attr_accessor :xml

  def initialize(id = nil, name = nil, author = nil, description = nil, project = nil, xml = nil)
    @id = id
    @name = name
    @author = author
    @description = description
    @project = project
    @xml = xml
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}AbstractRemoteConstant
class AbstractRemoteConstant
  @@schema_type = "AbstractRemoteConstant"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["icon", ["SOAP::SOAPString", XSD::QName.new(nil, "icon")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :description
  attr_accessor :icon

  def initialize(id = nil, name = nil, description = nil, icon = nil)
    @id = id
    @name = name
    @description = description
    @icon = icon
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteResolution
class RemoteResolution
  @@schema_type = "RemoteResolution"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["icon", ["SOAP::SOAPString", XSD::QName.new(nil, "icon")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :description
  attr_accessor :icon

  def initialize(id = nil, name = nil, description = nil, icon = nil)
    @id = id
    @name = name
    @description = description
    @icon = icon
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteStatus
class RemoteStatus
  @@schema_type = "RemoteStatus"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["icon", ["SOAP::SOAPString", XSD::QName.new(nil, "icon")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :description
  attr_accessor :icon

  def initialize(id = nil, name = nil, description = nil, icon = nil)
    @id = id
    @name = name
    @description = description
    @icon = icon
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemotePriority
class RemotePriority
  @@schema_type = "RemotePriority"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["icon", ["SOAP::SOAPString", XSD::QName.new(nil, "icon")]],
    ["color", ["SOAP::SOAPString", XSD::QName.new(nil, "color")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :description
  attr_accessor :icon
  attr_accessor :color

  def initialize(id = nil, name = nil, description = nil, icon = nil, color = nil)
    @id = id
    @name = name
    @description = description
    @icon = icon
    @color = color
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteIssueType
class RemoteIssueType
  @@schema_type = "RemoteIssueType"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["id", ["SOAP::SOAPString", XSD::QName.new(nil, "id")]],
    ["name", ["SOAP::SOAPString", XSD::QName.new(nil, "name")]],
    ["description", ["SOAP::SOAPString", XSD::QName.new(nil, "description")]],
    ["icon", ["SOAP::SOAPString", XSD::QName.new(nil, "icon")]]
  ]

  attr_accessor :id
  attr_accessor :name
  attr_accessor :description
  attr_accessor :icon

  def initialize(id = nil, name = nil, description = nil, icon = nil)
    @id = id
    @name = name
    @description = description
    @icon = icon
  end
end

# {http://beans.soap.rpc.jira.atlassian.com}RemoteServerInfo
class RemoteServerInfo
  @@schema_type = "RemoteServerInfo"
  @@schema_ns = "http://beans.soap.rpc.jira.atlassian.com"
  @@schema_element = [
    ["baseUrl", ["SOAP::SOAPString", XSD::QName.new(nil, "baseUrl")]],
    ["buildDate", ["SOAP::SOAPDateTime", XSD::QName.new(nil, "buildDate")]],
    ["buildNumber", ["SOAP::SOAPString", XSD::QName.new(nil, "buildNumber")]],
    ["edition", ["SOAP::SOAPString", XSD::QName.new(nil, "edition")]],
    ["version", ["SOAP::SOAPString", XSD::QName.new(nil, "version")]]
  ]

  attr_accessor :baseUrl
  attr_accessor :buildDate
  attr_accessor :buildNumber
  attr_accessor :edition
  attr_accessor :version

  def initialize(baseUrl = nil, buildDate = nil, buildNumber = nil, edition = nil, version = nil)
    @baseUrl = baseUrl
    @buildDate = buildDate
    @buildNumber = buildNumber
    @edition = edition
    @version = version
  end
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteEntity
class ArrayOf_tns1_RemoteEntity < ::Array
  @@schema_element = [
    ["item", ["RemoteEntity", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePermissionMapping
class ArrayOf_tns1_RemotePermissionMapping < ::Array
  @@schema_element = [
    ["item", ["RemotePermissionMapping", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteProject
class ArrayOf_tns1_RemoteProject < ::Array
  @@schema_element = [
    ["item", ["RemoteProject", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_xsd_string
class ArrayOf_xsd_string < ::Array
  @@schema_element = [
    ["item", ["String", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteVersion
class ArrayOf_tns1_RemoteVersion < ::Array
  @@schema_element = [
    ["item", ["RemoteVersion", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteComponent
class ArrayOf_tns1_RemoteComponent < ::Array
  @@schema_element = [
    ["item", ["RemoteComponent", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteCustomFieldValue
class ArrayOf_tns1_RemoteCustomFieldValue < ::Array
  @@schema_element = [
    ["item", ["RemoteCustomFieldValue", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssue
class ArrayOf_tns1_RemoteIssue < ::Array
  @@schema_element = [
    ["item", ["RemoteIssue", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteField
class ArrayOf_tns1_RemoteField < ::Array
  @@schema_element = [
    ["item", ["RemoteField", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteFieldValue
class ArrayOf_tns1_RemoteFieldValue < ::Array
  @@schema_element = [
    ["item", ["RemoteFieldValue", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteNamedObject
class ArrayOf_tns1_RemoteNamedObject < ::Array
  @@schema_element = [
    ["item", ["RemoteNamedObject", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteUser
class ArrayOf_tns1_RemoteUser < ::Array
  @@schema_element = [
    ["item", ["RemoteUser", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePermission
class ArrayOf_tns1_RemotePermission < ::Array
  @@schema_element = [
    ["item", ["RemotePermission", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteScheme
class ArrayOf_tns1_RemoteScheme < ::Array
  @@schema_element = [
    ["item", ["RemoteScheme", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePermissionScheme
class ArrayOf_tns1_RemotePermissionScheme < ::Array
  @@schema_element = [
    ["item", ["RemotePermissionScheme", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteComment
class ArrayOf_tns1_RemoteComment < ::Array
  @@schema_element = [
    ["item", ["RemoteComment", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteFilter
class ArrayOf_tns1_RemoteFilter < ::Array
  @@schema_element = [
    ["item", ["RemoteFilter", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteResolution
class ArrayOf_tns1_RemoteResolution < ::Array
  @@schema_element = [
    ["item", ["RemoteResolution", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteStatus
class ArrayOf_tns1_RemoteStatus < ::Array
  @@schema_element = [
    ["item", ["RemoteStatus", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemotePriority
class ArrayOf_tns1_RemotePriority < ::Array
  @@schema_element = [
    ["item", ["RemotePriority", XSD::QName.new(nil, "item")]]
  ]
end

# {http://jira.codehaus.org/rpc/soap/jirasoapservice-v2}ArrayOf_tns1_RemoteIssueType
class ArrayOf_tns1_RemoteIssueType < ::Array
  @@schema_element = [
    ["item", ["RemoteIssueType", XSD::QName.new(nil, "item")]]
  ]
end

# {http://exception.rpc.jira.atlassian.com}RemoteException
class RemoteException < ::StandardError
  @@schema_type = "RemoteException"
  @@schema_ns = "http://exception.rpc.jira.atlassian.com"
  @@schema_element = []

  def initialize
  end
end

# {http://exception.rpc.jira.atlassian.com}RemotePermissionException
class RemotePermissionException < ::StandardError
  @@schema_type = "RemotePermissionException"
  @@schema_ns = "http://exception.rpc.jira.atlassian.com"
  @@schema_element = []

  def initialize
  end
end

# {http://exception.rpc.jira.atlassian.com}RemoteAuthenticationException
class RemoteAuthenticationException < ::StandardError
  @@schema_type = "RemoteAuthenticationException"
  @@schema_ns = "http://exception.rpc.jira.atlassian.com"
  @@schema_element = []

  def initialize
  end
end

# {http://exception.rpc.jira.atlassian.com}RemoteValidationException
class RemoteValidationException < ::StandardError
  @@schema_type = "RemoteValidationException"
  @@schema_ns = "http://exception.rpc.jira.atlassian.com"
  @@schema_element = []

  def initialize
  end
end
