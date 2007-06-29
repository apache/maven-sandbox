package org.apache.maven.shared.jar.identification;

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

import java.util.Collections;
import java.util.List;

/**
 * Empty Repository Hash Search.  Always returns an empty list.
 * Used for local only implementation of a RepositoryHashSearch.
 * It is expected for the users of this library to provide an implementation
 * of a RepositoryHashSearch against a real repository.
 *
 * @plexus.component role="org.apache.maven.shared.jar.identification.RepositoryHashSearch"
 */
public class EmptyRepositoryHashSearch
    implements RepositoryHashSearch
{
    public boolean isValid()
    {
        return false;
    }

    public List searchBytecodeHash( String hash )
    {
        return Collections.EMPTY_LIST;
    }

    public List searchFileHash( String hash )
    {
        return Collections.EMPTY_LIST;
    }
}