<?xml version="1.0" encoding="ISO-8859-1" ?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <xsl:output method="text" omit-xml-declaration="yes"/>

  <xsl:template match="javadoc">
    <xsl:text>#!/usr/local/bin/dot
#
# Class diagram
#

    digraph G {
        ranksep=1.4;
        edge [fontname="Helvetica", fontsize=9, labelfontname="Helvetica", labelfontsize=9];
        node [fontname="Helvetica", fontsize=9, shape=record];
  </xsl:text>

    <xsl:apply-templates/>

    <xsl:for-each select="package/class">
      <xsl:if test="extends_class and extends_class/classref/@name!='java.lang.Object'">
        <xsl:text>        </xsl:text>
        <xsl:call-template name="fullname">
          <xsl:with-param name="name" select="extends_class/classref/@name"/>
        </xsl:call-template>
        <xsl:text> ->
        </xsl:text>
        <xsl:call-template name="fullname">
          <xsl:with-param name="name" select="../@name"/>
          <xsl:with-param name="parentname" select="@name"/>
        </xsl:call-template>
        <xsl:text> [dir=back, arrowtail=empty];&#10;</xsl:text>
      </xsl:if>
      <xsl:if test="implements">
        <xsl:text>        </xsl:text>
        <xsl:for-each select="implements/interfaceref">
          <xsl:call-template name="fullname">
            <xsl:with-param name="name" select="@name"/>
          </xsl:call-template>
          <xsl:text> ->
          </xsl:text>
          <xsl:call-template name="fullname">
            <xsl:with-param name="name" select="../../../@name"/>
            <xsl:with-param name="parentname" select="../../@name"/>
          </xsl:call-template>
          <xsl:text> [dir=back, arrowtail=empty, style=dashed];&#10;</xsl:text>
        </xsl:for-each>
      </xsl:if>
    </xsl:for-each>

    <xsl:for-each select="package/interface">
      <xsl:if test="extends_class and extends_class/classref/@name!='java.lang.Object'">
        <xsl:value-of select="extends_class/classref/@name"/>
        <xsl:text> ->
        </xsl:text>
        <xsl:value-of select="../@name"/>
        <xsl:text>.</xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text> [dir=back, arrowtail=empty]; </xsl:text>
      </xsl:if>
      <xsl:if test="implements">
        <xsl:for-each select="implements/interfaceref">
          <xsl:value-of select="@name"/>
          <xsl:text> ->
          </xsl:text>
          <xsl:value-of select="../../../@name"/>
          <xsl:text>.</xsl:text>
          <xsl:value-of select="../../@name"/>
          <xsl:text> [dir=back, arrowtail=empty, style=dashed]; </xsl:text>
        </xsl:for-each>
      </xsl:if>
    </xsl:for-each>
    <xsl:text>
    }}</xsl:text>
  </xsl:template>

  <xsl:template match="package">
    <xsl:text>        subgraph cluster</xsl:text>
    <xsl:call-template name="fullname">
      <xsl:with-param name="name" select="@name"/>
    </xsl:call-template>
    <xsl:text> {
            node [style=filled];</xsl:text>
    <xsl:apply-templates/>
    <!-- rank same, min, max, source or sink
             rankdir TB LR (left to right) or TB (top to bottom)
             ranksep .75 separation between ranks, in inches.
             ratio approximate aspect ratio desired, fill or auto
             remincross if true and there are multiple clusters, re-run crossing minimization
           -->
    <!-- rank=same;
             rankdir=TB;
             ranksep=1;
             ratio=fill;
             remincross=true;
           -->
    <xsl:text>
            label = "</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>";
            color="#000000";
            fillcolor="#dddddd";
        }
</xsl:text>
  </xsl:template>

  <xsl:template match="class">
    <xsl:text>&#10;        </xsl:text>
    <xsl:call-template name="fullname">
      <xsl:with-param name="name" select="../@name"/>
      <xsl:with-param name="parentname" select="@name"/>
    </xsl:call-template>
    <xsl:text> [ </xsl:text>
    <xsl:choose>
      <xsl:when test="@extensiblity='abstract'">
        <xsl:text>
            color="#848684",
            fillcolor="#ced7ce",
            fontname="Helvetica-Italic"</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>
            color="#9c0031",
            fillcolor="#ffffce",</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
    <!-- need to replace newline chars with \n -->
    <!--comment="<xsl:value-of select="doc" />" ,-->
    <xsl:text>
            URL="</xsl:text>
    <xsl:call-template name="filepath">
      <xsl:with-param name="name" select="../@name"/>
      <xsl:with-param name="parentname" select="@name"/>
    </xsl:call-template>
    <xsl:text>",
            style=filled,
            label="{</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>\n|</xsl:text>
    <xsl:if test="show">
      <xsl:for-each select="field">
        <xsl:choose>
          <xsl:when test="@access='public'">
            <xsl:text>+</xsl:text>
          </xsl:when>
          <xsl:when test="@access='private'">
            <xsl:text>-</xsl:text>
          </xsl:when>
          <xsl:when test="@access='protected'">
            <xsl:text>#</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>/</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/primitive/@type"/>
          <xsl:with-param name="marker" select="'.'"/>
        </xsl:call-template>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/classref/@name"/>
          <xsl:with-param name="marker" select="'.'"/>
        </xsl:call-template>
        <xsl:text>\n</xsl:text>
      </xsl:for-each>
    </xsl:if>
    <xsl:text>|</xsl:text>
    <xsl:if test="show">
      <!-- constructor -->
      <xsl:for-each select="method">
        <xsl:choose>
          <xsl:when test="@access='public'">
            <xsl:text>+</xsl:text>
          </xsl:when>
          <xsl:when test="@access='private'">
            <xsl:text>-</xsl:text>
          </xsl:when>
          <xsl:when test="@access='protected'">
            <xsl:text>#</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>/</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/primitive/@type"/>
          <xsl:with-param name="marker" select="'.'"/>
        </xsl:call-template>
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="returns/classref/@name"/>
          <xsl:with-param name="marker" select="'.'"/>
        </xsl:call-template>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
        <xsl:text>( </xsl:text>
        <xsl:for-each select="parameter">
          <xsl:value-of select="primitive/@type"/>
          <xsl:call-template name="substring-after-last">
            <xsl:with-param name="input" select="classref/@name"/>
            <xsl:with-param name="marker" select="'.'"/>
          </xsl:call-template>
          <xsl:text>,</xsl:text>
        </xsl:for-each>
        <xsl:text>)\n</xsl:text>
      </xsl:for-each>
    </xsl:if>
    <xsl:text>}"
            ];</xsl:text>
  </xsl:template>

  <xsl:template match="interface">
    <xsl:text>
        </xsl:text>
    <xsl:call-template name="fullname">
      <xsl:with-param name="name" select="../@name"/>
      <xsl:with-param name="parentname" select="@name"/>
    </xsl:call-template>
    <xsl:text> [
            color="#9c0031",
            fillcolor="#deffff",
            label="{«interface»\n</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>\n}"
            ];</xsl:text>
  </xsl:template>

  <xsl:template match="doc"/>

  <xsl:template match="extends"/>

  <xsl:template match="field"/>

  <xsl:template match="constructor"/>

  <xsl:template match="method"/>

  <xsl:template name="fullname">
    <xsl:param name="name"/>
    <xsl:param name="parentname"/>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$name"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'_'"/>
    </xsl:call-template>
    <xsl:if test="$parentname!=''">_</xsl:if>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$parentname"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'_'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="filepath">
    <xsl:param name="name"/>
    <xsl:param name="parentname"/>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$name"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'/'"/>
    </xsl:call-template>
    <xsl:if test="$parentname!=''">/</xsl:if>
    <xsl:call-template name="replace-string">
      <xsl:with-param name="text" select="$parentname"/>
      <xsl:with-param name="replace" select="'.'"/>
      <xsl:with-param name="with" select="'/'"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template name="replace-string">
    <xsl:param name="text"/>
    <xsl:param name="replace"/>
    <xsl:param name="with"/>
    <xsl:choose>
      <xsl:when test="contains($text,$replace)">
        <xsl:value-of select="substring-before($text,$replace)"/>
        <xsl:value-of select="$with"/>
        <xsl:call-template name="replace-string">
          <xsl:with-param name="text" select="substring-after($text,$replace)"/>
          <xsl:with-param name="replace" select="$replace"/>
          <xsl:with-param name="with" select="$with"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$text"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="substring-after-last">
    <xsl:param name="input"/>
    <xsl:param name="marker"/>
    <xsl:choose>
      <xsl:when test="contains($input,$marker)">
        <xsl:call-template name="substring-after-last">
          <xsl:with-param name="input" select="substring-after($input,$marker)"/>
          <xsl:with-param name="marker" select="$marker"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="$input"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
</xsl:stylesheet>
