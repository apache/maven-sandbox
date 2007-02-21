/**
 *    Copyright 2006  <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.continuum.ui.views;

import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;

/**
 * Common base class to aid creation of <em>new</em> model entities.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class AbstractNewDialog extends AbstractDialog {

    public AbstractNewDialog(IShellProvider parentShell) {
        super (parentShell);
        // TODO Auto-generated constructor stub
    }


    /**
     * @param parentShell
     */
    public AbstractNewDialog(Shell parentShell) {
        super (parentShell);
        // TODO Auto-generated constructor stub
    }

}
