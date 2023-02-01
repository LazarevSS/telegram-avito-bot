package ru.avito.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.avito.bot.client.AvitoClient;

import java.util.Collections;

public class SearchCommand extends Command {
    public SearchCommand(String commandIdentifier) {
        super(commandIdentifier, description());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        if (strings.length == 0) {
            message.setText("Не введен запрос. Повторите еще раз");
            send(absSender, user, message);
            return;
        }
        var avitoClient = AvitoClient.getInstance();
        var avitoItems = avitoClient.getAvitoItems(strings);
        Collections.reverse(avitoItems);
        avitoItems.forEach(avitoItem -> {

            message.setText(avitoItem.toString());
            send(absSender, user, message);
        });
    }

    private static String description() {
        return "Команда получения последних обновление по вашему запросу. " +
                "Для выполнения введите команду /search [ваш запрос]. " +
                "Например /search playstation 5";
    }
}
