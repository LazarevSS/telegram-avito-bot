package ru.avito.bot.command;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.avito.bot.service.SubscribeService;

public class BlockCommand extends Command {

    private final SubscribeService subscribeService = SubscribeService.getInstance();

    public BlockCommand(String commandIdentifier) {
        super(commandIdentifier, description());
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        block(user.getId());
    }

    public void block(Long userId) {
        subscribeService.unsubscribe(userId);
    }

    private static String description() {
        return "Команда блокировки бота. Если вы не хотите больше получать уведомления по вашему" +
                " запросу и отписаться от всех подписок. " +
                "Для выполнения введите команду /block.";
    }
}
