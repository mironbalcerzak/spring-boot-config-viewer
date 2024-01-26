package org.mca.sbcv.server.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ConfigurationPropertiesEntry {

    private Date created;

    private String applicationName;
    private String domainAddress;
    private String ipAddress;

    private Map<String, String> properties;

}
