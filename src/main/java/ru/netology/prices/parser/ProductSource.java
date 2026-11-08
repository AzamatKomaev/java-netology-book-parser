package ru.netology.prices.parser;

import java.net.URI;

public interface ProductSource {

    String name();

    boolean supports(URI uri);

    ParsedProduct fetch(String url);
}
