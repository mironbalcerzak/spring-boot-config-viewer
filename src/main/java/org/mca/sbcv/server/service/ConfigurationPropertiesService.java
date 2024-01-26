package org.mca.sbcv.server.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.mca.sbcv.properties.ConfigViewerProperties;
import org.mca.sbcv.server.dto.ConfigurationPropertiesEntry;
import org.mca.sbcv.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ConfigurationPropertiesService {

    private final List<ConfigurationPropertiesEntry> IN_MEMORY = new ArrayList<>(128);

    private final ConfigViewerProperties configProps;

    private Function<String, Boolean> isIgnored = e -> false;

    @PostConstruct
    public void init() {
        if (!configProps.getServer().isRemoveIgnoredProperties()) {
            return;
        }

        AntPathMatcher matcher = new AntPathMatcher();

        Set<String> ignoredPropertiesNames = configProps.getServer().getIgnoredPropertiesNames();
        Set<String> ignoredPropertiesPatterns = ignoredPropertiesNames.stream()
                .filter(matcher::isPattern)
                .collect(Collectors.toSet());

        ignoredPropertiesNames.removeAll(ignoredPropertiesPatterns);
        isIgnored = (candidate) -> {
            boolean match = ignoredPropertiesNames.contains(candidate);
            if (!match) {
                for (String ignored : ignoredPropertiesPatterns) {
                    match = matcher.match(ignored, candidate);
                    if (match) {
                        break;
                    }
                }
            }
            return match;
        };
    }

    public void update(String domain, String ipAddress, Map<String, String> properties) {
        String applicationName = StringUtils.defaultIfEmpty(properties.get("spring.application.name"), "(unnamed)");
        properties.keySet().removeIf(e -> isIgnored.apply(e));

        ConfigurationPropertiesEntry entry = new ConfigurationPropertiesEntry();
        entry.setProperties(properties);
        entry.setApplicationName(applicationName);
        entry.setIpAddress(ipAddress);
        entry.setDomainAddress(domain);
        entry.setCreated(new Date());

        IN_MEMORY.removeIf(e -> e.getDomainAddress().equalsIgnoreCase(domain)
                && e.getIpAddress().equalsIgnoreCase(ipAddress)
                && e.getApplicationName().equalsIgnoreCase(applicationName));
        IN_MEMORY.add(entry);
    }

    public List<ConfigurationPropertiesEntry> findAllByDomain(String domain) {
        return IN_MEMORY.stream()
                .filter(e -> e.getDomainAddress().equalsIgnoreCase(domain))
                .toList();
    }

    public List<String> findAllDomains() {
        return IN_MEMORY.stream()
                .map(ConfigurationPropertiesEntry::getDomainAddress)
                .distinct()
                .toList();
    }
}
