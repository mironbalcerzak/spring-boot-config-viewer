package org.mca.sbcv.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mca.sbcv.properties.ConfigViewerProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApplicationPropertiesPublisher {

    private final ConfigurableEnvironment environment;
    private final ConfigViewerProperties config;

    private final ObjectMapper objectMapper;
    private Function<String, Boolean> isIgnored;

    @EventListener
    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        Map<String, String> properties = readProperties(environment);
        send(properties);
        log.info("sent application context properties info");
    }

    private void send(Map<String, String> props) {
        String url = config.getClient().getUrl();

        HttpClient client = HttpClient.newHttpClient();

        try {
            String payload = objectMapper.writeValueAsString(props);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != HttpServletResponse.SC_ACCEPTED) {
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, String> readProperties(ConfigurableEnvironment springEnv) {
        MutablePropertySources propSrcs = springEnv.getPropertySources();
        return StreamSupport.stream(propSrcs.spliterator(), false)
                .filter(ps -> ps instanceof EnumerablePropertySource<?>)
                .map(ps -> ((EnumerablePropertySource) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .collect(Collectors.toMap(Function.identity(), springEnv::getProperty, (e1, e2) -> e2, TreeMap::new));
    }

}
