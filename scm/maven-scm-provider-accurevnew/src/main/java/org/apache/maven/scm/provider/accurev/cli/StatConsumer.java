package org.apache.maven.scm.provider.accurev.cli;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.List;
import java.util.Map;

import org.apache.maven.scm.log.ScmLogger;

public class StatConsumer
    extends XppStreamConsumer
{

    public StatConsumer( ScmLogger logger )
    {
        super( logger );

    }

    private static final String ELEMENT_TAG = "element";

    private String status = null;

    public String getStatus()
    {
        return status;
    }

    @Override
    protected void startTag( List<String> tagPath, Map<String, String> attributes )
    {
        int lastIndex = tagPath.size() - 1;
        if ( ELEMENT_TAG.equalsIgnoreCase( ( tagPath.get( lastIndex ) ) ) )
        {
            status = attributes.get( "status" );
        }
    }
}
