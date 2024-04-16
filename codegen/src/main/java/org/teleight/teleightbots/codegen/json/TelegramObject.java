package org.teleight.teleightbots.codegen.json;

import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TelegramObject(
        @SerializedName("name")
        @NotNull
        String name,

        @SerializedName("href")
        @NotNull
        String href,

        @SerializedName("description")
        @NotNull
        String[] description,

        @SerializedName("fields")
        @Nullable
        TelegramField[] fields,

        @SerializedName("subtypes")
        @Nullable
        TypeName[] subtypes,

        @SerializedName("subtype_of")
        @Nullable
        TypeName[] subtypeOf,

        @SerializedName("requires_builder")
        boolean requiresBuilder
) {}
