package org.mca.sbcv.config;

import org.mca.sbcv.properties.ConfigViewerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        ConfigViewerProperties.class
})
public class ConfigurationPropertiesConfig {
}
