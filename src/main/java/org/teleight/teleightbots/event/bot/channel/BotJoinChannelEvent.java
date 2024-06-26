package org.teleight.teleightbots.event.bot.channel;

import org.jetbrains.annotations.NotNull;
import org.teleight.teleightbots.api.objects.Chat;
import org.teleight.teleightbots.api.objects.Update;
import org.teleight.teleightbots.bot.TelegramBot;
import org.teleight.teleightbots.event.trait.Event;

public record BotJoinChannelEvent(
        @NotNull TelegramBot bot,
        @NotNull Update update
) implements Event {

    public Chat chat(){
        return update().myChatMember().chat();
    }

}
