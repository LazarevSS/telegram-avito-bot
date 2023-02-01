package ru.avito.bot.service;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.avito.bot.client.AvitoClient;
import ru.avito.bot.model.AvitoItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class SubscribeTask extends Thread {
    private final Map<String, AvitoItem> cash = new HashMap<>();
    private final AbsSender absSender;
    private final User user;
    private final Long chatId;
    private final String[] strings;
    private volatile boolean suspended = false;

    public SubscribeTask(AbsSender absSender, User user, Long chatId, String[] strings) {
        this.absSender = absSender;
        this.user = user;
        this.chatId = chatId;
        this.strings = strings;
    }

    @SneakyThrows
    @Override
    public void run() {
        var items = AvitoClient.getInstance().getAvitoItems(strings);
        var newItems = items.stream()
                .filter(avitoItem -> !cash.containsKey(avitoItem.getHref()))
                .collect(Collectors.toList());
        cash.clear();
        items.forEach(avitoItem -> cash.put(avitoItem.getHref(), avitoItem));
        sendMessages(newItems);
    }

    private void sendMessages(List<AvitoItem> newItems) {
        if (!newItems.isEmpty()) {
            Collections.reverse(newItems);
            var messages = new ArrayList<SendMessage>();
            messages.add(headMessage());
            messages.addAll(
                    newItems.stream()
                            .map(this::mapToSendMessage)
                            .collect(Collectors.toList())
            );
            send(messages);
        }
    }

    private SendMessage headMessage() {
        return new SendMessage(chatId.toString(), "Новые обновления по вашей подписке:");
    }

    private SendMessage mapToSendMessage(AvitoItem item) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(item.toString());
        return message;
    }

    private void send(List<SendMessage> messages) {
        messages.forEach(sendMessage -> {
            try {
                absSender.execute(sendMessage);
            } catch (TelegramApiRequestException e) {
                this.interrupt();
            } catch (TelegramApiException e) {
                log.error("Failed to send messages to username: " + user.getUserName() + "and userId: " + user.getId());
            }
        });
    }
}
