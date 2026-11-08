package ru.netology.prices.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import ru.netology.prices.config.AppProperties;

@Component
public class HtmlFetcher {

    private final WebClient client;
    private final AppProperties props;

    public HtmlFetcher(WebClient client, AppProperties props) {
        this.client = client;
        this.props = props;
    }

    public Document load(String url) {
        String html = client.get().uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block(props.fetch().timeout());
        if (html == null || html.isBlank()) {
            throw new IllegalStateException("Empty response from " + url);
        }
        return Jsoup.parse(html, url);
    }
}
