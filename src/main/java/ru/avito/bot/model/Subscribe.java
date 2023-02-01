package ru.avito.bot.model;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
import java.util.concurrent.ScheduledFuture;

@Builder
@Getter
public class Subscribe {
    private UUID uuid;
    private String[] searchArguments;
    private ScheduledFuture scheduledFuture;

    @Override
    public String toString() {
        return  "идентификатор подписки: " + uuid + "\n"+
                "запрос для поиска: " + String.join(" ", searchArguments) + "\n";
    }
}
