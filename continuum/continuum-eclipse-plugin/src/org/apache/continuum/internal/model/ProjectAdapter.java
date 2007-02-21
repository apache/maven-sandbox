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
package org.apache.continuum.internal.model;

import org.apache.maven.continuum.model.project.Project;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * Adapter implementation that adapts the {@link Project} instance and makes it
 * viewable in Property View.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ProjectAdapter implements IAdaptable {

    /**
     * Wrapped {@link Project} instance.
     */
    private final Project project;

    private ProjectPropertySource propertySource;


    public ProjectAdapter(final Project p) {
        super ();
        this.project = p;
    }


    /**
     * Returns the wrapped instance of the {@link Project}.
     * 
     * @return wrapped instance of the {@link Project}.
     * @deprecated <em>Experimental</em>
     */
    public Project getProject() {
        return this.project;
    }


    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
     */
    public Object getAdapter(Class adapter) {
        // Adapt only if it was an appropriate request
        if (adapter == IPropertySource.class) {
            System.out.println ("Matched adapter class for Project Adapter!");
            if (null == propertySource)
                propertySource = new ProjectPropertySource ();
            return propertySource;
        }
        return null;
    }

    /**
     * Associate Property Source that is returned by this Adapter.
     * 
     * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
     */
    public class ProjectPropertySource implements IPropertySource {

        private IPropertyDescriptor [] propertyDescriptors;


        public boolean isPropertySet(Object id) {
            // TODO Auto-generated method stub
            return false;
        }


        public Object getEditableValue() {
            // TODO Auto-generated method stub
            return null;
        }


        public IPropertyDescriptor [] getPropertyDescriptors() {
            // if (null == this.propertyDescriptors) {
            // // set up descriptors
            // PropertyDescriptor nameDesc = new PropertyDescriptor
            // ("continuum.project.name", "Projects");
            // nameDesc.setCategory ("Projects");
            // PropertyDescriptor buildDesc = new PropertyDescriptor
            // ("continuum.project.builddefinition.name", "Builds");
            // buildDesc.setCategory ("Builds");
            //
            // propertyDescriptors = new IPropertyDescriptor[] { nameDesc,
            // buildDesc};
            //
            // }
            return propertyDescriptors;
        }


        public Object getPropertyValue(Object id) {
            // TODO Auto-generated method stub
            return null;
        }


        public void resetPropertyValue(Object id) {
        // TODO Auto-generated method stub

        }


        public void setPropertyValue(Object id, Object value) {
        // TODO Auto-generated method stub

        }

    }

}
