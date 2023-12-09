package org.teleight.teleightbots.demo;

import org.teleight.teleightbots.api.methods.SendMessage;
import org.teleight.teleightbots.api.objects.Message;
import org.teleight.teleightbots.api.objects.chat.Chat;
import org.teleight.teleightbots.bot.Bot;
import org.teleight.teleightbots.conversation.Conversation;
import org.teleight.teleightbots.conversation.RunningConversation;

public class TestConversation implements Conversation.Executor {
    @Override
    public void execute(Bot bot, Chat chat, RunningConversation conversation) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chat.id())
                .text("Send me a messages with the text \"hello\"")
                .build();
        bot.execute(sendMessage);

        var message = conversation.waitFor(Message.class);
        if (message == null) {
            System.out.println("Message is null");
            return;
        }
        System.out.println("Message: " + message.text());


        if (message.text().equals("hello")) {
            SendMessage sendMessage1 = SendMessage.builder()
                    .chatId(chat.id())
                    .text("Good job!")
                    .build();
            bot.execute(sendMessage1);
        } else {
            SendMessage sendMessage1 = SendMessage.builder()
                    .chatId(chat.id())
                    .text("You didn't send \"hello\"!")
                    .build();
            bot.execute(sendMessage1);
        }
    }
}
