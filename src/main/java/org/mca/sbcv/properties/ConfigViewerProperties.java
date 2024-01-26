package org.mca.sbcv.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Set;
import java.util.TreeSet;

@ConfigurationProperties(prefix = "config-viewer")
@Data
public class ConfigViewerProperties {

    private final ConfigViewerServerProps server = new ConfigViewerServerProps();
    private final ConfigViewerClientProps client = new ConfigViewerClientProps();

    @Data
    public static class ConfigViewerServerProps {

        private final ConfigViewerSecurityProps security = new ConfigViewerSecurityProps();
        private final Set<String> ignoredPropertiesNames = new TreeSet<>();
        private boolean removeIgnoredProperties;
        private boolean enabled;
    }

    @Data
    public static class ConfigViewerClientProps {

        private final ConfigViewerSecurityProps security = new ConfigViewerSecurityProps();
        private boolean enabled;
        private String url;

    }

    public static class ConfigViewerSecurityProps {
        private String username;
        private String password;
    }
}
