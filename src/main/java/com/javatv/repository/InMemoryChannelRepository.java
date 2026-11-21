package com.javatv.repository;

import com.javatv.enums.ChannelCategory;
import com.javatv.model.Channel;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Bellek içi (In-Memory) kanal veri deposu.
 * Varsayılan popüler kanallarla birlikte başlatılır.
 */
public class InMemoryChannelRepository implements ChannelRepository {
    private final Map<Integer, Channel> channelMap = new ConcurrentHashMap<>();

    public InMemoryChannelRepository() {
        resetToDefaultChannels();
    }

    @Override
    public List<Channel> findAll() {
        List<Channel> list = new ArrayList<>(channelMap.values());
        Collections.sort(list);
        return Collections.unmodifiableList(list);
    }

    @Override
    public Optional<Channel> findByNumber(int number) {
        return Optional.ofNullable(channelMap.get(number));
    }

    @Override
    public Optional<Channel> findByName(String name) {
        if (name == null) return Optional.empty();
        return channelMap.values().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name.trim()))
                .findFirst();
    }

    @Override
    public List<Channel> findByCategory(ChannelCategory category) {
        if (category == null) return findAll();
        return channelMap.values().stream()
                .filter(c -> c.getCategory() == category)
                .sorted()
                .toList();
    }

    @Override
    public List<Channel> findFavorites() {
        return channelMap.values().stream()
                .filter(Channel::isFavorite)
                .sorted()
                .toList();
    }

    @Override
    public boolean add(Channel channel) {
        if (channel == null) return false;
        channelMap.put(channel.getNumber(), channel);
        return true;
    }

    @Override
    public boolean removeByNumber(int number) {
        return channelMap.remove(number) != null;
    }

    @Override
    public int count() {
        return channelMap.size();
    }

    @Override
    public void resetToDefaultChannels() {
        channelMap.clear();
        add(new Channel(1, "TRT 1", ChannelCategory.GENEL, true));
        add(new Channel(2, "TRT Haber", ChannelCategory.HABER, true));
        add(new Channel(3, "ATV", ChannelCategory.GENEL, true));
        add(new Channel(4, "Kanal D", ChannelCategory.GENEL, true));
        add(new Channel(5, "NOW TV", ChannelCategory.GENEL, true));
        add(new Channel(6, "Show TV", ChannelCategory.GENEL, true));
        add(new Channel(7, "Star TV", ChannelCategory.GENEL, true));
        add(new Channel(8, "TV8", ChannelCategory.GENEL, true));
        add(new Channel(9, "NTV", ChannelCategory.HABER, true));
        add(new Channel(10, "CNN Türk", ChannelCategory.HABER, true));
        add(new Channel(11, "Habertürk", ChannelCategory.HABER, true));
        add(new Channel(12, "beIN Sports Haber", ChannelCategory.SPOR, true));
        add(new Channel(13, "TRT Spor", ChannelCategory.SPOR, true));
        add(new Channel(14, "A Spor", ChannelCategory.SPOR, true));
        add(new Channel(15, "National Geographic", ChannelCategory.BELGESEL, true));
        add(new Channel(16, "Discovery Channel", ChannelCategory.BELGESEL, true));
        add(new Channel(17, "TLC", ChannelCategory.BELGESEL, true));
        add(new Channel(18, "Sinema TV", ChannelCategory.SINEMA, true));
        add(new Channel(19, "Cartoon Network", ChannelCategory.COCUK, true));
        add(new Channel(20, "PowerTürk TV", ChannelCategory.MUZIK, true));
        add(new Channel(21, "Kral TV", ChannelCategory.MUZIK, true));
    }
}
