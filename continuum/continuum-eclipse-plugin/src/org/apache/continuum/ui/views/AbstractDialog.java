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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.codehaus.plexus.util.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Common base class for specific dialog extensions.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public abstract class AbstractDialog extends Dialog {

    /**
     * @param parentShell
     */
    public AbstractDialog(IShellProvider parentShell) {
        super (parentShell);
        // TODO Auto-generated constructor stub
    }


    /**
     * @param parentShell
     */
    public AbstractDialog(Shell parentShell) {
        super (parentShell);
        // TODO Auto-generated constructor stub
    }


    /**
     * @see org.eclipse.jface.dialogs.Dialog#createDialogArea(org.eclipse.swt.widgets.Composite)
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        // TODO Auto-generated method stub
        return super.createDialogArea (parent);
    }


    // ------------------------------------------------------------------------
    // Helper methods to add and layout controls on the dialog
    // ------------------------------------------------------------------------

    protected Group createGroup(Composite parent, String text, int numColumns) {
        Group composite = new Group (parent, SWT.NONE | SWT.V_SCROLL);
        composite.setText (text);
        GridLayout layout = new GridLayout ();
        layout.numColumns = numColumns;
        layout.makeColumnsEqualWidth = false;
        composite.setLayout (layout);
        GridData data = new GridData (GridData.FILL_BOTH);
        composite.setLayoutData (data);
        return composite;
    }


    protected Label createLabel(Composite parent, String text) {
        Label label = new Label (parent, SWT.NONE);
        label.setText (text);
        GridData data = new GridData ();
        data.horizontalAlignment = GridData.FILL;
        label.setLayoutData (data);
        return label;
    }


    protected Text createTextField(Composite parent) {
        Text text = new Text (parent, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData ();
        data.horizontalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = true;
        data.verticalAlignment = GridData.CENTER;
        data.grabExcessVerticalSpace = false;
        data.widthHint = 250;
        text.setLayoutData (data);
        return text;
    }


    /**
     * @deprecated Use {@link #createButton(Composite,String,int)} instead
     */
    protected Button createCheckBox(Composite group, String label) {
        return createButton (group, label, SWT.PUSH | SWT.LEFT);
    }


    /**
     * Creates a Button with specified label and display style.
     * 
     * @param group
     * @param label
     * @param style
     * @return
     */
    protected Button createButton(Composite group, String label, int style) {
        Button button = new Button (group, style);
        button.setText (label);
        GridData data = new GridData ();
        button.setLayoutData (data);
        return button;
    }


    protected Combo createCombo(Composite parent, String [] items) {
        Combo combo = new Combo (parent, SWT.DROP_DOWN | SWT.READ_ONLY);
        combo.setLayoutData (new GridData (GridData.FILL_HORIZONTAL));
        combo.setFont (parent.getFont ());
        combo.setItems (items);
        combo.select (0);
        return combo;
    }


    // ------------------------------------------------------------------------
    // Service methods to take model object instances and introspect methods
    // and properties from them.
    // ------------------------------------------------------------------------

    protected ArrayList getDefaultValues(Object obj) {
        ArrayList ret = new ArrayList ();
        Class childClassImpl = obj.getClass ();
        Method [] methods = childClassImpl.getMethods ();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName ().startsWith ("set")) {
                Class setType = methods[i].getParameterTypes ()[0];
                if (!setType.equals (Class.class)) {
                    ret.add (new DefaultValue (methods[i]));
                }
            }
        }
        return ret;
    }


    protected void setDefaultValues(ArrayList defaultValues, Object obj) {
        for (Iterator i = defaultValues.iterator (); i.hasNext ();) {
            DefaultValue defaultValue = (DefaultValue) i.next ();
            try {
                defaultValue.method.invoke (obj, new Object[] { defaultValue.value});
            } catch (Exception e) {
                e.printStackTrace ();
            }
        }
    }

    /**
     * Simple POJO that holds the labels, values and widget hints to display on
     * the Dialog.
     * 
     * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
     */
    protected class DefaultValue {

        protected Method method;

        protected String label;

        protected Object value;

        protected Object widget;


        protected DefaultValue(Method method) {
            this.method = method;
            label = method.getName ().substring (3) + ":";//$NON-NLS-1$
            String deHumped = StringUtils.addAndDeHump (label);
            StringTokenizer tk = new StringTokenizer (deHumped, "-");
            StringBuffer sb = new StringBuffer ();
            while (tk.hasMoreTokens ()) {
                if (sb.length () > 0)
                    sb.append (' ');
                sb.append (StringUtils.capitalise (tk.nextToken ()));
            }
            // label = StringUtils.capitalise (deHumped.replace ("-", " "));
            label = sb.toString ();
        }


        public String toString() {
            return method.toString ();
        }
    }

}
