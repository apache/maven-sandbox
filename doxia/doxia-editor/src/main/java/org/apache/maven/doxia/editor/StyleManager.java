package org.apache.maven.doxia.editor;

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

import javax.swing.text.SimpleAttributeSet;

/**
 * @author <a href="mailto:trygvis@inamo.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public interface StyleManager
{
    String ROLE = StyleManager.class.getName();

    SimpleAttributeSet getTitleStyle();

    SimpleAttributeSet getAuthorStyle();

    SimpleAttributeSet getDateStyle();

    SimpleAttributeSet getSection1Style();

    SimpleAttributeSet getSection2Style();

    SimpleAttributeSet getSection3Style();

    SimpleAttributeSet getSection4Style();

    SimpleAttributeSet getSection5Style();

    SimpleAttributeSet getTextStyle();

    SimpleAttributeSet getParagraphSeparatorStyle();
}
