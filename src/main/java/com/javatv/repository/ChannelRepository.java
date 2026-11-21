package com.javatv.repository;

import com.javatv.enums.ChannelCategory;
import com.javatv.model.Channel;
import java.util.List;
import java.util.Optional;

/**
 * Kanal veri erişim katmanı arayüzü (Repository Pattern).
 */
public interface ChannelRepository {
    List<Channel> findAll();
    Optional<Channel> findByNumber(int number);
    Optional<Channel> findByName(String name);
    List<Channel> findByCategory(ChannelCategory category);
    List<Channel> findFavorites();
    boolean add(Channel channel);
    boolean removeByNumber(int number);
    int count();
    void resetToDefaultChannels();
}
