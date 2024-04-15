package org.teleight.teleightbots.codegen.json;

public record TelegramField(
        String name,
        String[] types,
        boolean required,
        String description
) {}
