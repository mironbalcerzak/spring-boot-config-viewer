package org.mca.sbcv.server.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mca.sbcv.server.dto.ConfigurationPropertiesEntry;
import org.mca.sbcv.server.service.ConfigurationPropertiesService;
import org.mca.sbcv.utils.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ServerRestController {

    private final HttpServletRequest request;
    private final ConfigurationPropertiesService configurationPropertiesService;

    @PostMapping(value = "/publish")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateProperties(@RequestBody Map<String, String> requestBody) {
        String ipAddress = StringUtils.defaultIfEmpty(request.getRemoteAddr(), "(unresolved)");
        String domain = StringUtils.defaultIfEmpty(request.getServerName(), "(unresolved)");

        configurationPropertiesService.update(domain, ipAddress, requestBody);
    }

    @GetMapping(value = "/domains")
    public List<String> findAllDomains() {
        return configurationPropertiesService.findAllDomains();
    }

    @GetMapping(value = "/domains/{domain}/properties")
    public Map<String, ConfigurationPropertiesEntry> findAllByDomain(@PathVariable("domain") String domain) {
        return configurationPropertiesService.findAllByDomain(domain).stream()
                .collect(Collectors.toMap(ConfigurationPropertiesEntry::getApplicationName, e -> e));
    }

}
