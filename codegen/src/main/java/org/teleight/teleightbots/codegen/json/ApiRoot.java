package org.teleight.teleightbots.codegen.json;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public record ApiRoot(
        @SerializedName("version")
        @NotNull
        String version,

        @SerializedName("release_date")
        @NotNull
        String releaseDate,

        @SerializedName("changelog")
        @NotNull
        String changelog,

        @SerializedName("methods")
        @NotNull
        Map<String, TelegramMethod> methods,

        @SerializedName("types")
        @NotNull
        Map<String, TelegramObject> types
) {}
