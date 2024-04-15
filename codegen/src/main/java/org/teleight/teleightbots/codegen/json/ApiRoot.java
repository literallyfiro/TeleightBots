package org.teleight.teleightbots.codegen.json;

import java.util.Map;

public record ApiRoot(
        String version,
        String release_date,
        String changelog,
        Map<String, Method> methods,
        Map<String, TelegramObject> types
) {}
