package org.teleight.teleightbots.codegen.json;

import com.google.gson.annotations.SerializedName;
import com.squareup.javapoet.TypeName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record TelegramField(
        @SerializedName("name")
        @NotNull
        String name,

        @SerializedName("types")
        @Nullable
        TypeName[] types,

        @SerializedName("required")
        boolean required,

        @SerializedName("description")
        @NotNull
        String description
) {}
