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

import org.apache.maven.doxia.parser.ParseException;

/**
 * Encapsulate a Markdown document parse error.
 *
 * @author Julien Nicoulaud <julien.nicoulaud@gmail.com>
 * @since 1.3
 */
public class MarkdownParseException extends ParseException
{

    /**
     * Build a new instance of {@link MarkdownParseException} with the specified detail message and cause.
     * <br/>
     * <b>Note</b>: no line or column number will be used.
     *
     * @param message The detailed message.
     *                This can later be retrieved by the <code>Throwable.getMessage()</code> method.
     * @param e       the cause. This can be retrieved later by the <code>Throwable.getCause()</code> method.
     *                (A null value is permitted, and indicates that the cause is nonexistent or unknown.)
     */
    public MarkdownParseException(String message, Exception e)
    {
        super(message, e);
    }
}
