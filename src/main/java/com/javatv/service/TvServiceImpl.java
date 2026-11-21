package com.javatv.service;

import com.javatv.enums.ChannelCategory;
import com.javatv.enums.PowerState;
import com.javatv.event.TvEventListener;
import com.javatv.exception.ChannelNotFoundException;
import com.javatv.exception.TvPoweredOffException;
import com.javatv.exception.VolumeOutOfBoundsException;
import com.javatv.model.Channel;
import com.javatv.model.Television;
import com.javatv.repository.ChannelRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Televizyon servis uygulayıcısı (Service Layer).
 * İş kurallarını yönetir, hataları kontrol eder ve event dinleyicilerini bilgilendirir.
 */
public class TvServiceImpl implements TvService {
    private final Television television;
    private final ChannelRepository channelRepository;
    private final List<TvEventListener> listeners = new CopyOnWriteArrayList<>();

    public TvServiceImpl(Television television, ChannelRepository channelRepository) {
        this.television = (television != null) ? television : new Television("Samsung", "Neo QLED 4K", 65);
        this.channelRepository = channelRepository;

        // Varsayılan ilk kanal
        List<Channel> channels = channelRepository.findAll();
        if (!channels.isEmpty()) {
            this.television.setCurrentChannel(channels.get(0));
        }
    }

    @Override
    public Television getTelevision() {
        return television;
    }

    @Override
    public boolean isPoweredOn() {
        return television.isOn();
    }

    @Override
    public synchronized void powerOn() {
        if (!television.isOn()) {
            television.setPowerState(PowerState.ON);
            if (television.getCurrentChannel() == null) {
                List<Channel> channels = channelRepository.findAll();
                if (!channels.isEmpty()) {
                    television.setCurrentChannel(channels.get(0));
                }
            }
            notifyPowerChanged(true);
        }
    }

    @Override
    public synchronized void powerOff() {
        if (television.isOn()) {
            television.setPowerState(PowerState.OFF);
            notifyPowerChanged(false);
        }
    }

    @Override
    public synchronized boolean togglePower() {
        if (television.isOn()) {
            powerOff();
            return false;
        } else {
            powerOn();
            return true;
        }
    }

    @Override
    public synchronized int volumeUp() {
        ensurePoweredOn("Sesi artırma");
        if (television.isMuted()) {
            television.setMuted(false);
            notifyMuteToggled(false);
        }
        int current = television.getVolume();
        if (current >= Television.MAX_VOLUME) {
            notifyError("Maksimum ses seviyesine ulaşıldı (" + Television.MAX_VOLUME + ").");
            return current;
        }
        television.setVolume(current + 1);
        notifyVolumeChanged(television.getVolume(), false);
        return television.getVolume();
    }

    @Override
    public synchronized int volumeDown() {
        ensurePoweredOn("Sesi azaltma");
        if (television.isMuted()) {
            television.setMuted(false);
            notifyMuteToggled(false);
        }
        int current = television.getVolume();
        if (current <= Television.MIN_VOLUME) {
            notifyError("Minimum ses seviyesine ulaşıldı (" + Television.MIN_VOLUME + ").");
            return current;
        }
        television.setVolume(current - 1);
        notifyVolumeChanged(television.getVolume(), false);
        return television.getVolume();
    }

    @Override
    public synchronized int setVolume(int volume) {
        ensurePoweredOn("Ses seviyesi ayarlama");
        if (volume < Television.MIN_VOLUME || volume > Television.MAX_VOLUME) {
            throw new VolumeOutOfBoundsException("Ses seviyesi " + Television.MIN_VOLUME + " ile " + Television.MAX_VOLUME + " arasında olmalıdır: " + volume);
        }
        television.setVolume(volume);
        if (television.isMuted()) {
            television.setMuted(false);
        }
        notifyVolumeChanged(television.getVolume(), television.isMuted());
        return television.getVolume();
    }

    @Override
    public synchronized boolean toggleMute() {
        ensurePoweredOn("Sessize alma");
        boolean newMuteState = !television.isMuted();
        television.setMuted(newMuteState);
        notifyMuteToggled(newMuteState);
        return newMuteState;
    }

    @Override
    public synchronized Channel changeChannel(int channelNumber) {
        ensurePoweredOn("Kanal değiştirme");
        Optional<Channel> found = channelRepository.findByNumber(channelNumber);
        if (found.isEmpty()) {
            String errorMsg = channelNumber + " numaralı kanal bulunamadı!";
            notifyError(errorMsg);
            throw new ChannelNotFoundException(channelNumber);
        }
        Channel channel = found.get();
        television.setCurrentChannel(channel);
        notifyChannelChanged(channel);
        return channel;
    }

    @Override
    public synchronized Channel changeChannel(String channelName) {
        ensurePoweredOn("Kanal değiştirme");
        Optional<Channel> found = channelRepository.findByName(channelName);
        if (found.isEmpty()) {
            String errorMsg = "'" + channelName + "' adlı kanal bulunamadı!";
            notifyError(errorMsg);
            throw new ChannelNotFoundException(channelName);
        }
        Channel channel = found.get();
        television.setCurrentChannel(channel);
        notifyChannelChanged(channel);
        return channel;
    }

    @Override
    public synchronized Channel nextChannel() {
        ensurePoweredOn("Sonraki kanala geçme");
        List<Channel> channels = channelRepository.findAll();
        if (channels.isEmpty()) {
            notifyError("Kayıtlı kanal bulunmuyor.");
            return null;
        }
        Channel current = television.getCurrentChannel();
        int nextIndex = 0;
        if (current != null) {
            int currentIndex = channels.indexOf(current);
            if (currentIndex >= 0) {
                nextIndex = (currentIndex + 1) % channels.size();
            }
        }
        Channel next = channels.get(nextIndex);
        television.setCurrentChannel(next);
        notifyChannelChanged(next);
        return next;
    }

    @Override
    public synchronized Channel previousChannel() {
        ensurePoweredOn("Önceki kanala geçme");
        List<Channel> channels = channelRepository.findAll();
        if (channels.isEmpty()) {
            notifyError("Kayıtlı kanal bulunmuyor.");
            return null;
        }
        Channel current = television.getCurrentChannel();
        int prevIndex = channels.size() - 1;
        if (current != null) {
            int currentIndex = channels.indexOf(current);
            if (currentIndex > 0) {
                prevIndex = currentIndex - 1;
            } else if (currentIndex == 0) {
                prevIndex = channels.size() - 1;
            }
        }
        Channel prev = channels.get(prevIndex);
        television.setCurrentChannel(prev);
        notifyChannelChanged(prev);
        return prev;
    }

    @Override
    public Optional<Channel> getCurrentChannel() {
        return Optional.ofNullable(television.getCurrentChannel());
    }

    @Override
    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> getChannelsByCategory(ChannelCategory category) {
        return channelRepository.findByCategory(category);
    }

    @Override
    public List<Channel> getFavoriteChannels() {
        return channelRepository.findFavorites();
    }

    @Override
    public synchronized boolean addChannel(Channel channel) {
        boolean added = channelRepository.add(channel);
        if (added) {
            notifyChannelListUpdated();
        }
        return added;
    }

    @Override
    public synchronized boolean removeChannel(int channelNumber) {
        boolean removed = channelRepository.removeByNumber(channelNumber);
        if (removed) {
            if (television.getCurrentChannel() != null && television.getCurrentChannel().getNumber() == channelNumber) {
                List<Channel> all = channelRepository.findAll();
                television.setCurrentChannel(all.isEmpty() ? null : all.get(0));
                if (television.isOn()) {
                    notifyChannelChanged(television.getCurrentChannel());
                }
            }
            notifyChannelListUpdated();
        }
        return removed;
    }

    @Override
    public synchronized boolean toggleFavorite(int channelNumber) {
        Optional<Channel> opt = channelRepository.findByNumber(channelNumber);
        if (opt.isPresent()) {
            Channel ch = opt.get();
            ch.setFavorite(!ch.isFavorite());
            notifyChannelListUpdated();
            return ch.isFavorite();
        }
        return false;
    }

    @Override
    public void addEventListener(TvEventListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeEventListener(TvEventListener listener) {
        listeners.remove(listener);
    }

    private void ensurePoweredOn(String action) {
        if (!television.isOn()) {
            TvPoweredOffException ex = new TvPoweredOffException(action);
            notifyError(ex.getMessage());
            throw ex;
        }
    }

    private void notifyPowerChanged(boolean isOn) {
        for (TvEventListener l : listeners) {
            try { l.onPowerChanged(television, isOn); } catch (Exception ignored) {}
        }
    }

    private void notifyVolumeChanged(int volume, boolean isMuted) {
        for (TvEventListener l : listeners) {
            try { l.onVolumeChanged(television, volume, isMuted); } catch (Exception ignored) {}
        }
    }

    private void notifyChannelChanged(Channel channel) {
        for (TvEventListener l : listeners) {
            try { l.onChannelChanged(television, channel); } catch (Exception ignored) {}
        }
    }

    private void notifyMuteToggled(boolean isMuted) {
        for (TvEventListener l : listeners) {
            try { l.onMuteToggled(television, isMuted); } catch (Exception ignored) {}
        }
    }

    private void notifyChannelListUpdated() {
        for (TvEventListener l : listeners) {
            try { l.onChannelListUpdated(); } catch (Exception ignored) {}
        }
    }

    private void notifyError(String msg) {
        for (TvEventListener l : listeners) {
            try { l.onError(msg); } catch (Exception ignored) {}
        }
    }
}
