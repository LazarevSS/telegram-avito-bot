package ru.avito.bot;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.avito.bot.command.BlockCommand;
import ru.avito.bot.command.SearchCommand;
import ru.avito.bot.command.SubscribeCommand;
import ru.avito.bot.command.SubscriptionsListCommand;
import ru.avito.bot.command.UnsubscribeCommand;

@Slf4j
public class Bot extends TelegramLongPollingCommandBot {
    private final String botName;
    private final String botToken;

    public Bot(String botName, String botToken) {
        super();
        this.botName = botName;
        this.botToken = botToken;
        register(new SearchCommand("/search"));
        register(new SubscribeCommand("/subscribe"));
        register(new UnsubscribeCommand("/unsubscribe"));
        register(new SubscriptionsListCommand("/subscriptions"));
        register(new BlockCommand("/block"));
        register(new HelpCommand());
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        if ("kicked".equalsIgnoreCase(update.getMyChatMember().getNewChatMember().getStatus())) {
            BlockCommand blockCommand = new BlockCommand("/block");
            blockCommand.block(update.getMyChatMember().getFrom().getId());
        }
        Message msg = update.getMessage();
        Long chatId = msg.getChatId();
        String userName = getUserName(msg);

        setAnswer(chatId, userName, "Я еще не научился отвечать на такое сообщение. Попробуй еще раз :)");
    }

    private String getUserName(Message msg) {

        User user = msg.getFrom();
        String userName = user.getUserName();
        return (userName != null) ? userName : String.format("%s %s", user.getLastName(), user.getFirstName());
    }

    private void setAnswer(Long chatId, String userName, String text) {
        SendMessage answer = new SendMessage();
        answer.setText(text);
        answer.setChatId(chatId.toString());
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            log.error(userName + " has a problem: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
