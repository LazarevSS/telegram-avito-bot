package ru.avito.bot.command;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.avito.bot.model.Subscribe;
import ru.avito.bot.service.SubscribeService;

import java.util.stream.Collectors;

public class SubscriptionsListCommand extends Command {

    private final SubscribeService subscribeService = SubscribeService.getInstance();

    public SubscriptionsListCommand(String commandIdentifier) {
        super(commandIdentifier, description());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        var subscriptionsText = subscribeService.getUserSubscribed(user.getId()).stream()
                .map(Subscribe::toString)
                .collect(Collectors.joining("\n"));
        send(absSender, user, new SendMessage(chat.getId().toString(), messageText(subscriptionsText)));
    }

    private String messageText(String subscriptionsText) {
        return subscriptionsText.isEmpty()
                ? "Подписки отсутствуют"
                : "Ваши подписки: \n" + subscriptionsText;
    }

    private static String description() {
        return "Команда получения информации о всех имеющихся у вас подписках. " +
                "Для выполнения введите команду /subscriptions";
    }
}
