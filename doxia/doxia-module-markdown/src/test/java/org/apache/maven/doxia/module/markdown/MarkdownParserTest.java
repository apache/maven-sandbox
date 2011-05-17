package org.apache.maven.doxia.module.markdown;

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

import org.apache.maven.doxia.parser.AbstractParserTest;
import org.apache.maven.doxia.parser.Parser;

/**
 * Tests for {@link MarkdownParser}.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 1.3
 */
public class MarkdownParserTest extends AbstractParserTest
{

    /**
     * The {@link MarkdownParser} used for the tests.
     */
    protected MarkdownParser parser;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        parser = (MarkdownParser) lookup(Parser.ROLE, MarkdownParser.ROLE_HINT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Parser createParser()
    {
        return parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String outputExtension()
    {
        return MarkdownSiteModule.FILE_EXTENSION;
    }
}
