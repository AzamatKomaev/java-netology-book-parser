package ru.netology.prices.parser;

import java.math.BigDecimal;
import java.net.URI;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class BooksToScrapeSource implements ProductSource {

    private final HtmlFetcher fetcher;

    public BooksToScrapeSource(HtmlFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public String name() {
        return "books.toscrape";
    }

    @Override
    public boolean supports(URI uri) {
        return uri.getHost() != null && uri.getHost().endsWith("books.toscrape.com");
    }

    @Override
    public ParsedProduct fetch(String url) {
        Document doc = fetcher.load(url);
        Element title = doc.selectFirst("div.product_main h1");
        Element price = doc.selectFirst("p.price_color");
        if (title == null || price == null) {
            throw new IllegalStateException("Product markup not found on " + url);
        }
        Element desc = doc.selectFirst("#product_description + p");
        String amount = price.text().replaceAll("[^0-9.]", "");
        String currency = price.text().contains("£") ? "GBP" : "";
        return new ParsedProduct(title.text(), desc == null ? "" : desc.text(), new BigDecimal(amount), currency, null);
    }
}
