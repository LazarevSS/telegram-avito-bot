package ru.avito.bot.service;

import lombok.extern.slf4j.Slf4j;
import ru.avito.bot.model.Subscribe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SubscribeService {

    private static SubscribeService instance;

    private SubscribeService() {}

    public static synchronized SubscribeService getInstance() {
        if (instance == null) {
            instance = new SubscribeService();
        }
        return instance;
    }

    private final ScheduledExecutorService executorService =
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
    private final Map<Long, Map<UUID, Subscribe>> userIdAndSubscribeMap = new HashMap<>();

    public void subscribe(SubscribeTask subscribeTask) {
        var uuid = UUID.randomUUID();
        var userId = subscribeTask.getUser().getId();
        var subscribe = Subscribe.builder()
                .uuid(uuid)
                .searchArguments(subscribeTask.getStrings())
                .scheduledFuture(executorService.scheduleAtFixedRate(subscribeTask, 5, 60, TimeUnit.SECONDS))
                .build();
        if (Objects.isNull(userIdAndSubscribeMap.get(userId))) {
            var subscribeMap = new HashMap<UUID, Subscribe>();
            subscribeMap.put(uuid, subscribe);
            userIdAndSubscribeMap.put(userId, subscribeMap);
        } else {
            userIdAndSubscribeMap.get(userId).put(uuid, subscribe);
        }
    }

    public void unsubscribe(Long userId) {
        userIdAndSubscribeMap.get(userId).values().forEach(subscribe -> {
            subscribe.getScheduledFuture().cancel(true);
            userIdAndSubscribeMap.get(userId).clear();
        });
    }

    public Subscribe unsubscribe(Long userId, UUID subscribeId) {
        userIdAndSubscribeMap.get(userId).get(subscribeId).getScheduledFuture().cancel(true);
        return userIdAndSubscribeMap.get(userId).remove(subscribeId);
    }

    public Collection<Subscribe> getUserSubscribed(Long userId) {
        return Objects.isNull(userIdAndSubscribeMap.get(userId))
                ? new ArrayList<>()
                : userIdAndSubscribeMap.get(userId).values();
    }
}
