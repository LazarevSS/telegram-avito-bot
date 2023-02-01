package ru.avito.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.avito.bot.service.SubscribeService;
import ru.avito.bot.service.SubscribeTask;

public class SubscribeCommand extends Command {

    private final SubscribeService subscribeService = SubscribeService.getInstance();

    public SubscribeCommand(String commandIdentifier) {
        super(commandIdentifier, description());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (strings.length == 0) {
            send(absSender, user, new SendMessage(chat.getId().toString(), failedSubscribeText()));
            return;
        }
        subscribeService.subscribe(new SubscribeTask(absSender, user, chat.getId(), strings));
        send(absSender, user, new SendMessage(chat.getId().toString(), successSubscribeText(strings)));
    }

    private String successSubscribeText(String[] strings) {
        return "Вы успешно подписались на обновления по запросу: " + String.join(" ", strings);
    }

    private String failedSubscribeText() {
        return "Не введен запрос. Повторите еще раз";
    }

    private static String description() {
        return "Команда подписки на ваш запрос, вам регулярно будут поступать обновления по вашему запросу. " +
                "Для выполнения введите команду /subscribe [ваш запрос]. " +
                "Например /subscribe playstation 5";
    }

}
