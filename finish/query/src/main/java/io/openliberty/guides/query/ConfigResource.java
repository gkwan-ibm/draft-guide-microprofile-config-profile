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
package io.openliberty.guides.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/config")
public class ConfigResource {

	// tag::config[]
    @Inject
    private Config config;
    // end::config[]

    // tag::configValue[]
    @Inject
    @ConfigProperty(name = "query.contactEmail")
    private ConfigValue contactConfigValue;
    // end::configValue[]
    
    // tag::systemConfig[]
    @Inject
    @ConfigProperties
    private ConfigSystemBean systemConfig;
    // end::systemConfig[]

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Properties> getAllConfig() {
        Map<String, Properties> configMap = new HashMap<String, Properties>();
        configMap.put("ConfigSources", getConfigSources());
        configMap.put("ConfigProperties", getConfigProperties());
        return configMap;
    }

    @GET
    @Path("/contact")
    @Produces(MediaType.APPLICATION_JSON)
    public Properties getContactConfig() {
        Properties configProps = new Properties();
        String sourceName = contactConfigValue.getSourceName();
        int sourceOrdinal = contactConfigValue.getSourceOrdinal();
        String value = contactConfigValue.getValue();
        configProps.put("SourceName", sourceName);
        configProps.put("SourceOrdinal", sourceOrdinal);
        configProps.put("Value", value);
        return configProps;
    }

    // tag::getSystemConfig[]
    @GET
    @Path("/system")
    @Produces(MediaType.APPLICATION_JSON)
    public Properties getSystemConfig() {
        Properties configProps = new Properties();
        configProps.put("system.httpPort", systemConfig.httpPort);
        configProps.put("system.user", systemConfig.user);
        configProps.put("system.password", systemConfig.password);
        configProps.put("system.userPassword", systemConfig.userPassword);
        configProps.put("system.contextRoot", systemConfig.contextRoot);
        configProps.put("system.properties", systemConfig.properties);
        return configProps;
    }
    // end::getSystemConfig[]

    public Properties getConfigSources() {
        Properties configSource = new Properties();
        for (ConfigSource source : config.getConfigSources()) {
            configSource.put(source.getName(), source.getOrdinal());
        }
        return configSource;
    }

    public Properties getConfigProperties() {
        Properties configProperties = new Properties();
        for (String name : config.getPropertyNames()) {
            if (name.startsWith("system.")
                   || name.startsWith("query.")
                || name.equals("role")) {
                configProperties.put(name, config.getValue(name, String.class));
            }
        }
        return configProperties;
    }
}
