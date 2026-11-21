package com.javatv.service;

import com.javatv.enums.ChannelCategory;
import com.javatv.event.TvEventListener;
import com.javatv.model.Channel;
import com.javatv.model.Television;

import java.util.List;
import java.util.Optional;

/**
 * Televizyon servis katmanı arayüzü.
 * Tüm iş kurallarını (business logic) yönetir.
 */
public interface TvService {
    Television getTelevision();
    boolean isPoweredOn();
    void powerOn();
    void powerOff();
    boolean togglePower();

    int volumeUp();
    int volumeDown();
    int setVolume(int volume);
    boolean toggleMute();

    Channel changeChannel(int channelNumber);
    Channel changeChannel(String channelName);
    Channel nextChannel();
    Channel previousChannel();

    Optional<Channel> getCurrentChannel();
    List<Channel> getAllChannels();
    List<Channel> getChannelsByCategory(ChannelCategory category);
    List<Channel> getFavoriteChannels();
    boolean addChannel(Channel channel);
    boolean removeChannel(int channelNumber);
    boolean toggleFavorite(int channelNumber);

    void addEventListener(TvEventListener listener);
    void removeEventListener(TvEventListener listener);
}
