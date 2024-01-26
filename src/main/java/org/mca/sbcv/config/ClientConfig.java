package org.mca.sbcv.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty("config-viewer.client.enabled")
@ComponentScan("org.mca.sbcv.client")
@Configuration
public class ClientConfig {
}
