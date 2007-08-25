package org.apache.maven.doxia.linkcheck;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.commons.lang.StringEscapeUtils;

/**
 * An class containing the results of a single check of a link.
 * 
 * @author <a href="mailto:bwalding@apache.org">Ben Walding</a>
 * @author <a href="mailto:aheritier@apache.org">Arnaud Heritier</a>
 * @version $Id$
 */
public final class LinkCheckResult
{
    /** status. */
    private String status;

    /** target. */
    private String target;

    /** errorMessage. */
    private String errorMessage;

    /**
     * Returns the status.
     *
     * @return String
     */
    public String getStatus()
    {
        return this.status;
    }

    /**
     * Sets the status.
     *
     * @param stat The status to set
     */
    public void setStatus( String stat )
    {
        this.status = stat;
    }

    /**
     * Returns the target.
     *
     * @return String
     */
    public String getTarget()
    {
        return this.target;
    }

    /**
     * Sets the target.
     *
     * @param targ The target to set
     */
    public void setTarget( String targ )
    {
        this.target = targ;
    }

    /**
     * Returns the errorMessage.
     *
     * @return the errorMessage.
     */
    public String getErrorMessage()
    {
        return this.errorMessage;
    }

    /**
    
     * @param message The errorMessage to set.
     */
    public void setErrorMessage( String message )
    {
        this.errorMessage = message;
    }

    /**
     * Creates an XML representation of this link check result
     *
     * @return xml fragment representation of this result
     */
    public String toXML()
    {
        StringBuffer buf = new StringBuffer();

        buf.append( "    <result>" + LinkCheck.EOL );

        buf.append( "      <target>" + StringEscapeUtils.escapeXml( getTarget() ) + "</target>" + LinkCheck.EOL );

        buf.append( "      <status>" + getStatus() + "</status>" + LinkCheck.EOL );

        buf.append( "      <errorMessage>" + StringEscapeUtils.escapeXml( getErrorMessage() ) + "</errorMessage>" + LinkCheck.EOL );

        buf.append( "    </result>" + LinkCheck.EOL );

        return buf.toString();
    }

}
