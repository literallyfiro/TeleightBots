package org.teleight.teleightbots.event.keyboard;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teleight.teleightbots.api.methods.AnswerCallbackQuery;
import org.teleight.teleightbots.api.objects.CallbackQuery;
import org.teleight.teleightbots.api.objects.MaybeInaccessibleMessage;
import org.teleight.teleightbots.api.objects.Update;
import org.teleight.teleightbots.api.objects.User;
import org.teleight.teleightbots.bot.Bot;
import org.teleight.teleightbots.event.trait.Event;

import java.util.concurrent.CompletableFuture;

public record ButtonPressEvent(
        @NotNull Bot bot,
        @NotNull Update update
) implements Event {

    public @NotNull CallbackQuery callbackQuery() {
        return update.callbackQuery();
    }

    public @NotNull User from() {
        return callbackQuery().from();
    }

    public @Nullable MaybeInaccessibleMessage message() {
        return callbackQuery().message();
    }

    public @NotNull CompletableFuture<Boolean> completeCallback() {
        return completeCallback(String.valueOf(AnswerCallbackQuery.of(callbackQuery().id())));
    }

    public @NotNull CompletableFuture<Boolean> completeCallback(String text) {
        return completeCallback(AnswerCallbackQuery.of(callbackQuery().id()).text(text).build());
    }

    public @NotNull CompletableFuture<Boolean> completeCallback(AnswerCallbackQuery answerCallbackQuery) {
        return bot.execute(answerCallbackQuery);
    }

}
