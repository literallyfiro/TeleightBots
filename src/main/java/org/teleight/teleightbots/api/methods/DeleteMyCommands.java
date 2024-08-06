package org.teleight.teleightbots.api.methods;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teleight.teleightbots.api.ApiMethodBoolean;
import org.teleight.teleightbots.api.objects.BotCommandScope;

@Builder(builderClassName = "Builder", toBuilder = true, builderMethodName = "ofBuilder")
@Jacksonized
public record DeleteMyCommands(
        @JsonProperty(value = "scope")
        @Nullable
        BotCommandScope scope,

        @JsonProperty(value = "language_code")
        @Nullable
        String languageCode
) implements ApiMethodBoolean {

    @Override
    public @NotNull String getEndpointURL() {
        return "deleteMyCommands";
    }

}
