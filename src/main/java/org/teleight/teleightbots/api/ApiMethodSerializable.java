package org.teleight.teleightbots.api;

import org.jetbrains.annotations.NotNull;
import org.teleight.teleightbots.exception.exceptions.TelegramRequestException;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface ApiMethodSerializable extends ApiMethod<Serializable> {

    List<Class<? extends Serializable>> getSerializableClasses();

    @Override
    default @NotNull Serializable deserializeResponse(@NotNull String answer) throws TelegramRequestException {
        return deserializeResponseFromPossibilities(answer, getSerializableClasses());
    }

    default Serializable deserializeResponseFromPossibilities(String answer, List<Class<? extends Serializable>> possibleValues) throws TelegramRequestException {
        Throwable lastException = null;
        for (Class<? extends Serializable> possibleValue : possibleValues) {
            try {
                return deserializeResponseSerializable(answer, possibleValue);
            } catch (TelegramRequestException e) {
                if (e.getCause() instanceof IOException) {
                    lastException = e.getCause();
                } else {
                    throw e;
                }
            }
        }
        throw new TelegramRequestException("Unable to deserialize response", lastException);
    }

}
