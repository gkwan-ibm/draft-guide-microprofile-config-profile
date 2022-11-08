// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package it.io.openliberty.guides.query;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Base64;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.json.JsonObject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

public class QueryEndpointIT {

    private static String configProfile;
    private static String queryUrl;
    private static String sysContextRoot;
    private static String systemUrl;
    private static String authHeader;

    private static Client client;

    @BeforeAll
    public static void setup() {

        configProfile = System.getProperty("liberty.var.mp.config.profile", "testing");

        String queryPort = System.getProperty("http.port");
        queryUrl = "http://localhost:" + queryPort + "/query/";

        String systemPort = System.getProperty(configProfile + ".system.port");
        sysContextRoot = (configProfile.equals("development")) ? 
                             System.getProperty(configProfile + ".system.context.root") : "";
        systemUrl = "http://localhost:" + systemPort + "/system" + sysContextRoot + "/property/";
        
        String userPassword = System.getProperty(configProfile + ".system.username") + ":"
        		              + System.getProperty(configProfile + ".system.password");
		authHeader = "Basic "
                + Base64.getEncoder().encodeToString(userPassword .getBytes());

        client = ClientBuilder.newClient();
    }

    @Test
    public void testGetSystemProperties() {
        Response queryResponse = this.getResponse(queryUrl + "systems/localhost", null);
        this.assertResponse(queryUrl + "systems/localhost", queryResponse);

        JsonObject jsonFromQuery = queryResponse.readEntity(JsonObject.class);
        String osNameFromQuery = jsonFromQuery.getString("os.name");
        String osArchFromQuery = jsonFromQuery.getString("os.arch");
        String javaVersionFromQuery = jsonFromQuery.getString("java.version");
        String javaVendorFromQuery = jsonFromQuery.getString("java.vendor");

        Response osNameSysResponse = this.getResponse(systemUrl + "os.name", authHeader);
        Response osArchSysResponse = this.getResponse(systemUrl + "os.arch", authHeader);
        Response javaVersionSysResponse = this.getResponse(systemUrl + "java.version", authHeader);
        Response javaVendorSysResponse = this.getResponse(systemUrl + "java.vendor", authHeader);

        String osNameFromSystem = osNameSysResponse.readEntity(String.class);
        String osArchFromSystem = osArchSysResponse.readEntity(String.class);
        String javaVersionFromSystem = javaVersionSysResponse.readEntity(String.class);
        String javaVendorFromSystem = javaVendorSysResponse.readEntity(String.class);

        this.assertProperty("os.name", osNameFromQuery, osNameFromSystem);
        this.assertProperty("os.arch", osArchFromQuery, osArchFromSystem);
        this.assertProperty("java.version", javaVersionFromQuery, javaVersionFromSystem);
        this.assertProperty("java.vendor", javaVendorFromQuery, javaVendorFromSystem);

        queryResponse.close();
        osNameSysResponse.close();
        osArchSysResponse.close();
        javaVersionSysResponse.close();
        javaVendorSysResponse.close();
    }

    @Test
    public void testGetAllConfig() {
        Response response = this.getResponse(queryUrl + "config", null);
        this.assertResponse(queryUrl + "config", response);

        response.close();
    }

    @Test
    public void testGetContactConfig() {
        Response response = this.getResponse(queryUrl + "config/contact", null);
        this.assertResponse(queryUrl + "config/contact", response);

        JsonObject jsonObject = response.readEntity(JsonObject.class);
        String expectedValue = (configProfile.equals("development")) ? "alice" : "admin";
        String actualValue = jsonObject.getString("Value");
        assertEquals(expectedValue + "@ol.guides.com", actualValue, 
                     "Values of property query.contactEmail does not match");

        response.close();
    }

    @Test
    public void testGetSystemConfig() {
        Response response = this.getResponse(queryUrl + "config/system", null);
        this.assertResponse(queryUrl + "config/system", response);

        JsonObject jsonObject = response.readEntity(JsonObject.class);
        String userPassword = jsonObject.getString("system.userPassword");
        String contextRoot = jsonObject.getString("system.contextRoot");
        int port = jsonObject.getInt("system.httpPort");

        assertEquals(System.getProperty(configProfile + ".system.username") + ":" 
                     + System.getProperty(configProfile + ".system.password"), userPassword, 
                     "Values of property system.userPassword does not match");
        assertEquals("system" + sysContextRoot, contextRoot, 
                     "Values of property system.contextRoot does not match");
        assertEquals(Integer.valueOf(System.getProperty(configProfile + ".system.port")), port, 
                     "Values of property system.httpPort does not match");

        response.close();
    }

    // tag::javadoc[]
    /**
     * <p>
     * Returns response information from the specified URL.
     * </p>
     *
     * @param url - target URL.
     * @param authHeader - authorization request header, if needed.
     * @return Response object with the response from the specified URL.
     */
    // end::javadoc[]
    private Response getResponse(String url, String authHeader) {
        if (authHeader != null) {
            return client.target(url).request().header("Authorization", authHeader).get();
        }
        return client.target(url).request().get();
    }

    // tag::javadoc[]
    /**
     * <p>
     * Asserts that the given URL has the correct response code of 200.
     * </p>
     *
     * @param url      - target URL.
     * @param response - response received from the target URL.
     */
    // end::javadoc[]
    private void assertResponse(String url, Response response) {
        assertEquals(200, response.getStatus(), "Incorrect response code from " + url);
    }

    // tag::javadoc[]
    /**
     * Asserts that the specified JVM system property is equivalent in both the
     * system and inventory services.
     *
     * @param propertyName - name of the system property to check.
     * @param hostname     - name of JVM's host.
     * @param expected     - expected name.
     * @param actual       - actual name.
     */
    // end::javadoc[]
    private void assertProperty(String propertyName, String expected, String actual) {
        assertEquals(expected, actual, "JVM system property [" + propertyName + "] "
                        + "in the system service does not match the one stored in "
                        + "the query service for localhost");
    }

}
