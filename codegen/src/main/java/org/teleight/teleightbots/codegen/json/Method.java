package org.teleight.teleightbots.codegen.json;

public record Method(
        String name,
        String href,
        String[] description,
        String[] returns,
        TelegramField[] fields,
        String[] subtype_of,
        String[] subtypes
) {}
