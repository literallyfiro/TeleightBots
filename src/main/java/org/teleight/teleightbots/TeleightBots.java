package org.teleight.teleightbots;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.teleight.teleightbots.bot.manager.BotManager;
import org.teleight.teleightbots.exception.ExceptionManager;
import org.teleight.teleightbots.scheduler.Scheduler;

import java.io.IOException;

public final class TeleightBots {

    @ApiStatus.Internal
    private static final TeleightBotsProcess teleightBotsProcess;

    // Initializes the TeleightBots API
    static {
        teleightBotsProcess = new TeleightBotsProcessImpl();
    }

    /**
     * Stops the TeleightBots API.
     * <p>
     * This method should be called when the TeleightBots API is no longer needed.
     * It stops the TeleightBots API and releases all resources.
     * </p>
     */
    public static void stopCleanly() {
        try {
            teleightBotsProcess.close();
        } catch (IOException e) {
            getExceptionManager().handleException(e);
        }
    }

    /**
     * Returns the Scheduler associated with the TeleightBots process.
     * <p>
     * The Scheduler is responsible for scheduling tasks to be executed at a later time or at regular intervals.
     * </p>
     *
     * @return The Scheduler associated with the TeleightBots process.
     */
    public static @NotNull Scheduler getScheduler() {
        return teleightBotsProcess.scheduler();
    }

    /**
     * Returns the BotManager associated with the TeleightBots process.
     * <p>
     * The BotManager is responsible for managing bots, including registering new bots and getting existing bots.
     * </p>
     *
     * @return The BotManager associated with the TeleightBots process.
     */
    public static @NotNull BotManager getBotManager() {
        return teleightBotsProcess.botManager();
    }

    /**
     * Returns the exception manager used by the TeleightBots API.
     * <p>
     * The exception manager is responsible for handling exceptions thrown by the TeleightBots API.
     * It is also responsible for logging exceptions and notifying the user about them.
     * </p>
     *
     * @return the exception manager
     */
    public static @NotNull ExceptionManager getExceptionManager() {
        return teleightBotsProcess.exceptionManager();
    }

}
