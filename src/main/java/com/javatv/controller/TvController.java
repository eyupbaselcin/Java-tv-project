package com.javatv.controller;

import com.javatv.enums.ChannelCategory;
import com.javatv.event.TvEventListener;
import com.javatv.exception.TvException;
import com.javatv.model.Channel;
import com.javatv.model.Television;
import com.javatv.service.TvService;

import java.util.List;
import java.util.Optional;

/**
 * TV Uygulama Kontrolörü (Controller / MVC).
 * Kullanıcı arayüzü (UI) eylemlerini servis katmanına iletir ve sonuçları koordine eder.
 */
public class TvController {
    private final TvService tvService;

    public TvController(TvService tvService) {
        this.tvService = tvService;
    }

    public Television getTelevision() {
        return tvService.getTelevision();
    }

    public boolean isPoweredOn() {
        return tvService.isPoweredOn();
    }

    public boolean togglePower() {
        return tvService.togglePower();
    }

    public void powerOn() {
        tvService.powerOn();
    }

    public void powerOff() {
        tvService.powerOff();
    }

    public int volumeUp() {
        try {
            return tvService.volumeUp();
        } catch (TvException e) {
            return -1;
        }
    }

    public int volumeDown() {
        try {
            return tvService.volumeDown();
        } catch (TvException e) {
            return -1;
        }
    }

    public int setVolume(int volume) {
        try {
            return tvService.setVolume(volume);
        } catch (TvException e) {
            return -1;
        }
    }

    public boolean toggleMute() {
        try {
            return tvService.toggleMute();
        } catch (TvException e) {
            return false;
        }
    }

    public Channel changeChannel(int number) {
        try {
            return tvService.changeChannel(number);
        } catch (TvException e) {
            return null;
        }
    }

    public Channel nextChannel() {
        try {
            return tvService.nextChannel();
        } catch (TvException e) {
            return null;
        }
    }

    public Channel previousChannel() {
        try {
            return tvService.previousChannel();
        } catch (TvException e) {
            return null;
        }
    }

    public Optional<Channel> getCurrentChannel() {
        return tvService.getCurrentChannel();
    }

    public List<Channel> getAllChannels() {
        return tvService.getAllChannels();
    }

    public List<Channel> getChannelsByCategory(ChannelCategory category) {
        return tvService.getChannelsByCategory(category);
    }

    public boolean addChannel(int number, String name, ChannelCategory category, boolean hd) {
        return tvService.addChannel(new Channel(number, name, category, hd));
    }

    public boolean removeChannel(int number) {
        return tvService.removeChannel(number);
    }

    public boolean toggleFavorite(int number) {
        return tvService.toggleFavorite(number);
    }

    public void addEventListener(TvEventListener listener) {
        tvService.addEventListener(listener);
    }

    public void removeEventListener(TvEventListener listener) {
        tvService.removeEventListener(listener);
    }
}
