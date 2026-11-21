package com.javatv.model;

import com.javatv.enums.PowerState;
import java.util.Objects;

/**
 * Televizyon donanım ve durum modelini temsil eder.
 */
public class Television {
    public static final int MIN_VOLUME = 0;
    public static final int MAX_VOLUME = 30;
    public static final int DEFAULT_VOLUME = 12;

    private final String brand;
    private final String model;
    private final int screenSizeInches;

    private PowerState powerState;
    private int volume;
    private boolean muted;
    private Channel currentChannel;
    private int previousVolume;

    public Television(String brand, String model, int screenSizeInches) {
        this.brand = (brand != null && !brand.trim().isEmpty()) ? brand.trim() : "SmartTV";
        this.model = (model != null && !model.trim().isEmpty()) ? model.trim() : "4K Ultra HD";
        this.screenSizeInches = (screenSizeInches > 0) ? screenSizeInches : 55;
        this.powerState = PowerState.OFF;
        this.volume = DEFAULT_VOLUME;
        this.previousVolume = DEFAULT_VOLUME;
        this.muted = false;
        this.currentChannel = null;
    }

    public Television(String brand, int screenSizeInches) {
        this(brand, "Crystal Series", screenSizeInches);
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public int getScreenSizeInches() {
        return screenSizeInches;
    }

    public PowerState getPowerState() {
        return powerState;
    }

    public void setPowerState(PowerState powerState) {
        this.powerState = powerState;
    }

    public boolean isOn() {
        return this.powerState == PowerState.ON;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = Math.clamp(volume, MIN_VOLUME, MAX_VOLUME);
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public int getPreviousVolume() {
        return previousVolume;
    }

    public void setPreviousVolume(int previousVolume) {
        this.previousVolume = previousVolume;
    }

    public Channel getCurrentChannel() {
        return currentChannel;
    }

    public void setCurrentChannel(Channel currentChannel) {
        this.currentChannel = currentChannel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Television that)) return false;
        return screenSizeInches == that.screenSizeInches &&
                Objects.equals(brand, that.brand) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(brand, model, screenSizeInches);
    }

    @Override
    public String toString() {
        return String.format("%s %s (%d\" Smart TV) - Durum: %s | Ses: %d%s | Kanal: %s",
                brand,
                model,
                screenSizeInches,
                powerState.getDescription(),
                volume,
                muted ? " [MUTED]" : "",
                currentChannel != null ? currentChannel.getName() : "Seçilmedi");
    }
}
