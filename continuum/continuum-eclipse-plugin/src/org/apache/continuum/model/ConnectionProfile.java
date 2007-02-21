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
package org.apache.continuum.model;

/**
 * Model that captures user preferences to set up a connection profile to a
 * remote Continuum instance.
 * <p>
 * TODO: Override <code>equals()</code> method to allow for checking of
 * duplicates based on profile name.
 * 
 * @author <a href='mailto:rahul.thakur.xdev@gmail.com'>Rahul Thakur</a>
 */
public class ConnectionProfile {

    /**
     * Profile Identifier.
     * <p>
     * TODO: We should make this a surrogate key.
     */
    private String id;

    /**
     * Display label and name of the profile.
     */
    private String label;

    /**
     * Connection string to use to connection remote Continuum instance.
     */
    private String connectionUrl;

    /**
     * Username to user to connect to Continuum instance.
     */
    private String username;

    /**
     * Password to use in conjunction with {@link #username} to connect to
     * Continuum instance.
     */
    private String password;


    /**
     * @return the connectionUrl
     */
    public String getConnectionUrl() {
        return connectionUrl;
    }


    /**
     * @param connectionUrl
     *            the connectionUrl to set
     */
    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }


    /**
     * @param id
     *            the id to set
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }


    /**
     * @param label
     *            the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }


    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }


    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }


    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

}
