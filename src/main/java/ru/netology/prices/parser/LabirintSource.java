package ru.netology.prices.parser;

import java.math.BigDecimal;
import java.net.URI;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

@Component
public class LabirintSource implements ProductSource {

    private final HtmlFetcher fetcher;

    public LabirintSource(HtmlFetcher fetcher) {
        this.fetcher = fetcher;
    }

    @Override
    public String name() {
        return "labirint";
    }

    @Override
    public boolean supports(URI uri) {
        return uri.getHost() != null && uri.getHost().endsWith("labirint.ru");
    }

    @Override
    public ParsedProduct fetch(String url) {
        Document doc = fetcher.load(url);

        Element h1 = doc.selectFirst("h1[itemprop=name]");
        String title = h1 == null || h1.textNodes().isEmpty() ? "" : h1.textNodes().get(0).text().trim();
        if (title.isEmpty()) {
            title = doc.title();
        }

        String priceRaw = attr(doc.selectFirst("meta[itemprop=price]"), "content");
        if (priceRaw.isEmpty()) {
            throw new IllegalStateException("Price not found on " + url);
        }
        String currency = attr(doc.selectFirst("meta[itemprop=priceCurrency]"), "content");

        return new ParsedProduct(title, description(doc), new BigDecimal(priceRaw.replace(',', '.')),
                currency.isEmpty() ? "RUB" : currency, year(doc));
    }

    private String description(Document doc) {
        for (Element el : doc.select("div[class*=_text_] > div")) {
            String t = el.text().trim();
            if (t.length() > 40) {
                return cut(t, 1000);
            }
        }
        return "";
    }

    private String year(Document doc) {
        for (Element name : doc.select("div[class*=_name_]")) {
            if (name.text().trim().equals("Издательство") && name.nextElementSibling() != null) {
                var m = java.util.regex.Pattern.compile("(19|20)\\d{2}").matcher(name.nextElementSibling().text());
                if (m.find()) {
                    return m.group();
                }
            }
        }
        return null;
    }

    private static String text(Element e) {
        return e == null ? "" : e.text().trim();
    }

    private static String attr(Element e, String name) {
        return e == null ? "" : e.attr(name).trim();
    }

    private static String cut(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max);
    }
}
