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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class DialogTestSupport {

    protected void run(String title) {

        Display display = new Display ();
        final Shell shell = new Shell (display);
        shell.setText (title);
        shell.setToolTipText (title);

        // get the dialog contents and prepare for display.
        final Dialog dlg = createDialog (shell);        
        Button b1 = new Button (shell, SWT.PUSH);
        b1.setText ("Show Dialog");
        b1.setToolTipText (title);
        b1.addSelectionListener (new SelectionAdapter () {

            public void widgetSelected(SelectionEvent e) {
                dlg.setBlockOnOpen (true);
                dlg.open ();
            }

        });

        // layout our test window.
        final int insetX = 4, insetY = 4;
        FormLayout formLayout = new FormLayout ();
        formLayout.marginWidth = insetX;
        formLayout.marginHeight = insetY;
        FormData data = new FormData ();
        data.left = new FormAttachment (0, 10);
        data.top = new FormAttachment (0, 10);
        b1.setLayoutData (data);

        shell.setLayout (formLayout);

        shell.pack ();
        shell.open ();
        while (!shell.isDisposed ()) {
            if (!display.readAndDispatch ())
                display.sleep ();
        }
        display.dispose ();

    }


    protected abstract Dialog createDialog(final Shell shell);
}
