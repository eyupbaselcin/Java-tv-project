package com.javatv.event;

import com.javatv.model.Channel;
import com.javatv.model.Television;

/**
 * TV durum değişikliklerini dinleyen Observer (Gözlemci) arayüzü.
 * GUI ve diğer bileşenlerin TV modelinden bağımsız olarak güncellenmesini sağlar.
 */
public interface TvEventListener {
    default void onPowerChanged(Television tv, boolean isOn) {}
    default void onVolumeChanged(Television tv, int volume, boolean isMuted) {}
    default void onChannelChanged(Television tv, Channel channel) {}
    default void onMuteToggled(Television tv, boolean isMuted) {}
    default void onChannelListUpdated() {}
    default void onError(String errorMessage) {}
}
