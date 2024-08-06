package org.teleight.teleightbots.api.methods;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teleight.teleightbots.api.ApiMethodMultiResponse;
import org.teleight.teleightbots.api.objects.Message;
import org.teleight.teleightbots.api.objects.ReplyKeyboard;

import java.io.Serializable;
import java.util.List;

@Builder(builderClassName = "Builder", toBuilder = true, builderMethodName = "ofBuilder")
@Jacksonized
public record StopMessageLiveLocation(
        @JsonProperty(value = "chat_id")
        @Nullable
        String chatId,

        @JsonProperty(value = "message_id")
        int messageId,

        @JsonProperty(value = "inline_message_id")
        @Nullable
        String inlineMessageId,

        @JsonProperty(value = "reply_markup")
        @Nullable
        ReplyKeyboard replyMarkup
) implements ApiMethodMultiResponse {

    @Override
    public List<Class<? extends Serializable>> getSerializableClasses() {
        return List.of(Message.class, Boolean.class);
    }

    @Override
    public @NotNull String getEndpointURL() {
        return "stopMessageLiveLocation";
    }

}
