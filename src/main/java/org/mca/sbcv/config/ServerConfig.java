package org.mca.sbcv.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty("config-viewer.server.enabled")
@ComponentScan("org.mca.sbcv.server")
@Configuration
public class ServerConfig {
}
