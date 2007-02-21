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

import java.util.ArrayList;
import java.util.List;

/**
 * Displays the section with Project details for a selected project.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class BuildDefinitionInfoSection extends AbstractTablePropertySection {

    private static final String LABEL_BUILD_DEFINITION = "Build Definition";

    /**
     * Array of Table headers for the Category Document view.
     */
    private static ColumnHeaders [] headers = ColumnHeaders.values ();


    /**
     * 
     */
    protected List getColumnLabelText() {
        List<String> list = new ArrayList<String> ();
        // add the column names here
        for (int i = 0; i < headers.length; i++)
            list.add (headers[i].label);
        return list;
    }


    /**
     * 
     */
    protected String getKeyForRow(Object object) {
        // TODO do something.
        return null;
    }


    /**
     * 
     */
    protected List getValuesForRow(Object object) {
        List list = new ArrayList ();
        // TODO do something
        return list;
    }


    /**
     * 
     */
    protected String getButtonLabelText() {
        return LABEL_BUILD_DEFINITION;
    }


    /**
     * 
     */
    protected List getOwnedRows() {
        List list = new ArrayList ();
        // TODO: do something
        return list;
    }


    /**
     * 
     */
    protected Object getNewChild() {
        // TODO: do something
        return null;
    }

    /**
     * ColumnHeader ENUM for rendering column headers.
     */
    public enum ColumnHeaders {
        /**
         * Enum instances to be used to render column headers for the Document
         * list view.
         */
        ID("Id", 4, true), NAME("Name", 4, true), IS_DEFAULT("Default?", 4, true), GOALS("Goals", 4, true), ARGUMENTS("Arguments", 4, true), PROFILE("Profile", 4, true), SCHEDULE("Schedule", 4, true), PROJECT("Project Name", 4, true), LAST_BUILD("Last Run", 4, true);

        /**
         * Label to display.
         */
        private String label;

        /**
         * Determines if the column is resizable.
         */
        private boolean isResizable;

        /**
         * Weight for the column relative to other columns.
         */
        private int weight;


        /**
         * Creates an instance of {@link ColumnHeaders} enum with specified
         * properties.
         * 
         * @param label
         *            String label to be used to display the Column name.
         * @param weight
         *            relative weight to be used for layout/sizing purposes.
         * @param isResizable
         *            true, if this column is re-sizeable, else false.
         */
        ColumnHeaders(String label, int weight, boolean isResizable) {
            this.label = label;
            this.isResizable = isResizable;
            this.weight = weight;
        }


        /**
         * Returns <code>true</code> if the column represented by this enum
         * instance is resizeable.
         * 
         * @return the isResizable
         */
        public boolean isResizable() {
            return isResizable;
        }


        /**
         * Returns the label as <code>String</code> for the column represented
         * by this enum.
         * 
         * @return the label
         */
        public String getLabel() {
            return label;
        }


        /**
         * Returns the relative weight for the column represented by this enum.
         * <p>
         * Weight is used for setting up relative column width.
         * 
         * @return the weight as int.
         */
        public int getWeight() {
            return weight;
        }

    }
}
