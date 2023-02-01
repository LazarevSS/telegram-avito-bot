package ru.avito.bot.command;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
public abstract class Command extends BotCommand {
    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public Command(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    protected void send(AbsSender absSender, User user, SendMessage message) {
        try {
            absSender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Failed to send messages to username: " + user.getUserName() + "and userId: " + user.getId());
        }
    }
}
