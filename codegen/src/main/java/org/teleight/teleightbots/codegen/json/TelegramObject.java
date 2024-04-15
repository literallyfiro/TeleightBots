package org.teleight.teleightbots.codegen.json;

public record TelegramObject(
        String name,
        String href,
        String[] description,
        TelegramField[] fields
) {}
