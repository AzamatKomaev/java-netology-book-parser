package ru.netology.prices.web;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ParseRequest(@NotEmpty List<@NotBlank String> urls) {
}
