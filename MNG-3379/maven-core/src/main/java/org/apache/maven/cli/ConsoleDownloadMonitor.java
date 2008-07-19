package org.apache.maven.cli;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.maven.wagon.WagonConstants;
import org.apache.maven.wagon.resource.Resource;
import org.apache.maven.wagon.events.TransferEvent;

import java.util.*;

/**
 * Console download progress meter.  Properly handles multiple downloads simultaneously.
 *
 * @author <a href="mailto:brett@apache.org">Brett Porter</a>
 * @version $Id$
 */
public class ConsoleDownloadMonitor
    extends AbstractConsoleDownloadMonitor
{
    private Map/*<Resource,Integer>*/ downloads;

    public ConsoleDownloadMonitor()
    {
        downloads = new LinkedHashMap();
    }

    public void transferInitiated( TransferEvent transferEvent )
    {
        String message = transferEvent.getRequestType() == TransferEvent.REQUEST_PUT ? "Uploading" : "Downloading";

        String url = transferEvent.getWagon().getRepository().getUrl();

        // TODO: can't use getLogger() because this isn't currently instantiated as a component
        out.println( message + ": " + url + "/" + transferEvent.getResource().getName() );

    }

    public void transferStarted( TransferEvent transferEvent )
    {
        // This space left intentionally blank
    }

    public synchronized void transferProgress( TransferEvent transferEvent, byte[] buffer, int length )
    {
        Resource resource = transferEvent.getResource();
        if (!downloads.containsKey(resource))
        {
            downloads.put(resource, new Long(length));
        } else
        {
            Long complete = (Long) downloads.get(resource);
            complete = new Long(complete.longValue() + length);
            downloads.put(resource, complete);
        }

        for (Iterator i = downloads.entrySet().iterator(); i.hasNext(); )
        {
            Map.Entry entry = (Map.Entry) i.next();
            Long complete = (Long)entry.getValue();
            String status = getDownloadStatusForResource(complete.longValue(), ((Resource)entry.getKey()).getContentLength());
            out.print(status);
            if (i.hasNext())
            {
                out.print(" ");
            }
        }
        out.print("\r");
    }

    String getDownloadStatusForResource(long progress, long total)
    {
        if ( total >= 1024 )
        {
            return (progress / 1024 ) + "/" + ( total == WagonConstants.UNKNOWN_LENGTH ? "?" : ( total / 1024 ) + "K");
        }
        else
        {
            return progress + "/" + ( total == WagonConstants.UNKNOWN_LENGTH ? "?" : total + "b" );
        }
    }

    public synchronized void transferCompleted( TransferEvent transferEvent )
    {
        super.transferCompleted(transferEvent);
        downloads.remove(transferEvent.getResource());
    }
}
