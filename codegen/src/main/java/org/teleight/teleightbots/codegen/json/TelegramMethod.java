package org.teleight.teleightbots.codegen.json;

import com.squareup.javapoet.TypeName;

public record TelegramMethod(
        String name,
        String href,
        String[] description,
        TypeName[] returns,
        TelegramField[] fields,
        TypeName[] subtype_of,
        TypeName[] subtypes
) {}
