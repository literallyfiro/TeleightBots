package org.teleight.teleightbots.codegen.json;

public record TelegramObject(
        String name,
        String href,
        String[] description,
        TelegramField[] fields,
        String[] subtypes,
        String[] subtype_of
) {}
