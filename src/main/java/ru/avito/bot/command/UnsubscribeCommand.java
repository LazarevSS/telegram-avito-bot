package ru.avito.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.avito.bot.service.SubscribeService;

import java.util.Objects;
import java.util.UUID;
public class UnsubscribeCommand extends Command {

    private final SubscribeService subscribeService = SubscribeService.getInstance();

    public UnsubscribeCommand(String commandIdentifier) {
        super(commandIdentifier, description());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        if (Objects.isNull(subscribeService.unsubscribe(user.getId(), UUID.fromString(strings[0])))) {
            send(absSender, user, new SendMessage(chat.getId().toString(), "Не удалось отписаться от подписки, попробуйте снова"));
        } else {
            send(absSender, user, new SendMessage(chat.getId().toString(), "Вы успешно отписались от подписки"));
        }
    }

    private static String description() {
        return "Команда отписки на ваш запрос. Используйте команду, " +
                "если вы не хотите больше получать уведомления по вашему запросу. " +
                "Для выполнения введите команду /unsubscribe [идентификатор подписки]. " +
                "Например /unsubscribe 52be5e22-a6f4-4dbb-8dd3-826c1a3fe11c";
    }
}
