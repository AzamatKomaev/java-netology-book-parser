package ru.netology.prices.parser;

import java.math.BigDecimal;

public record ParsedProduct(String title, String description, BigDecimal price, String currency, String publishedAt) {
}
