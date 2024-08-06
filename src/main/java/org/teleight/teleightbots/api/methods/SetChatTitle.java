package org.teleight.teleightbots.api.methods;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;
import org.teleight.teleightbots.api.ApiMethodBoolean;

@Builder(builderClassName = "Builder", toBuilder = true, builderMethodName = "ofBuilder")
@Jacksonized
public record SetChatTitle(
        @JsonProperty(value = "chat_id", required = true)
        @NotNull
        String chatId,

        @JsonProperty(value = "title", required = true)
        @NotNull
        String title
) implements ApiMethodBoolean {

    public static @NotNull Builder ofBuilder(String chatId, String title) {
        return new SetChatTitle.Builder().chatId(chatId).title(title);
    }

    @Override
    public @NotNull String getEndpointURL() {
        return "setChatTitle";
    }

}
