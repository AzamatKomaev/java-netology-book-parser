package ru.netology.prices.parser;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ru.netology.prices.config.AppProperties;
import tools.jackson.databind.JsonNode;

@Component
public class DummyJsonSource implements ProductSource {

    private final WebClient client;
    private final AppProperties props;

    public DummyJsonSource(WebClient client, AppProperties props) {
        this.client = client;
        this.props = props;
    }

    @Override
    public String name() {
        return "dummyjson";
    }

    @Override
    public boolean supports(URI uri) {
        return "dummyjson.com".equals(uri.getHost());
    }

    @Override
    public ParsedProduct fetch(String url) {
        JsonNode json = client.get().uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block(props.fetch().timeout());
        if (json == null || !json.hasNonNull("price")) {
            throw new IllegalStateException("Unexpected JSON from " + url);
        }
        String published = json.path("meta").path("createdAt").asString(null);
        return new ParsedProduct(json.path("title").asString(""), json.path("description").asString(""),
                new BigDecimal(json.get("price").asString()), "USD", published);
    }
}
