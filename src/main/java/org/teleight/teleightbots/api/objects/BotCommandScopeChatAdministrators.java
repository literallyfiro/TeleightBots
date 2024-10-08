package org.teleight.teleightbots.api.objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public record BotCommandScopeChatAdministrators(
        @JsonProperty(value = "chat_id", required = true)
        @NotNull
        String chatId
) implements BotCommandScope {

    @Override
    public @NotNull String type() {
        return "chat_administrators";
    }

}
