<?xml version="1.0" encoding="UTF-8"?>
    <!-- written by David Smiley, dsmiley@mitre.org -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:mv2="http://maven.apache.org/POM/4.0.0" exclude-result-prefixes="mv2">
    <xsl:output  indent="yes" method="xml" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/project">
        <project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
            <modelVersion>4.0.0</modelVersion>
            <xsl:apply-templates />
        </project>
    </xsl:template>
    <xsl:template match="/project/id">
        <artifactId><xsl:value-of select="."/></artifactId>
    </xsl:template>
    <xsl:template match="/project/groupId">
        <groupId><xsl:value-of select="/project/package"/></groupId>
    </xsl:template>
    <xsl:template match="currentVersion">
        <version><xsl:value-of select="."/></version>
    </xsl:template>
    <xsl:template match="extend">
        <parent>
            <!-- chop off project.xml -->
            <xsl:variable name="pthX" select="substring(.,0,string-length(.)-10)" />
            <!-- remove ${basedir}/ if present, then append pom.xml -->
            <xsl:variable name="pth">
                <xsl:choose>
                    <xsl:when test="contains($pthX,'${basedir}/')">
                        <xsl:value-of select="substring-after($pthX,'${basedir}/')"/>
                    </xsl:when>
                    <xsl:otherwise><xsl:value-of select="$pthX" /></xsl:otherwise>
                </xsl:choose>
                <xsl:text>pom.xml</xsl:text>
            </xsl:variable>
            <!-- load parent pom and grab what we need -->
            <xsl:variable name="ppom" select="document($pth,/*)/mv2:project"></xsl:variable>
            <artifactId><xsl:value-of select="$ppom/mv2:artifactId" /></artifactId>
            <groupId><xsl:value-of select="$ppom/mv2:groupId" /></groupId>
            <version><xsl:value-of select="$ppom/mv2:version" /></version>
            <xsl:if test="not($pth = '../pom.xml')">
                <relativePath><xsl:value-of select="$pth"/></relativePath>
            </xsl:if>
        </parent>
    </xsl:template>
    <xsl:template match="shortDescription">
        <!-- TODO: if no //description then output as description; otherwise omitt -->
    </xsl:template>
    <xsl:template match="issueTrackingUrl">
        <issueManagement>
            <url><xsl:value-of select="."/></url>
        </issueManagement>
    </xsl:template>
    <xsl:template match="repository">
        <scm><xsl:apply-templates /></scm>
    </xsl:template>
    <xsl:template match="unitTestSourceDirectory">
        <testSourceDirectory><xsl:value-of select="."/></testSourceDirectory>
    </xsl:template>
    <xsl:template match="unitTest">
        <testResources>
            <xsl:apply-templates select="resources/resource" />
            <xsl:apply-templates select="includes|excludes" />
        </testResources>
    </xsl:template>
    <!-- omitt with comment -->
    <xsl:template name="comment">
        <xsl:comment>
            <xsl:call-template name="commentX" />
        </xsl:comment>
    </xsl:template>
    <xsl:template name="commentX">
        <xsl:value-of select="concat(local-name(.),': ',normalize-space(text()),' ')" />
        <xsl:for-each select="*">
            <xsl:call-template name="commentX" />
        </xsl:for-each>
    </xsl:template>
    <xsl:template match="logo|siteAddress|siteDirectory|distributionSite|distributionDirectory">
        <xsl:call-template name="comment" />
    </xsl:template>
    <xsl:template match="gumpRepositoryId|versions|branches|packageGroups|properties">
        <xsl:call-template name="comment" />
    </xsl:template>
    <xsl:template match="nagEmailAddress|aspectSourceDirectory|integrationUnitTestSourceDirectory">
        <xsl:call-template name="comment" />
    </xsl:template>
    <xsl:template match="unitTest/includes|unitTest/excludes">
        <xsl:call-template name="comment" />
    </xsl:template>
    <xsl:template match="dependency/id|dependency/url|dependency/jar|dependency/properties">
        <xsl:call-template name="comment" />
    </xsl:template>
    <!-- omitt silently (handled elsewhere) -->
    <xsl:template match="sourceModifications|defaultGoal|package" />
    
    <!-- a copy template that ensures proper namespace -->
    <xsl:template match="*">
        <xsl:element name="{local-name(.)}"><xsl:apply-templates /></xsl:element>
    </xsl:template>
    <xsl:template match="comment()"><xsl:copy-of select="."/></xsl:template>
</xsl:stylesheet>
