package com.javatv.model;

import com.javatv.enums.ChannelCategory;
import java.util.Objects;

/**
 * Televizyon kanal modelini temsil eden profesyonel veri sınıfı.
 * İmmutability (değiştirilemezlik) ve nesne eşitliği (equals/hashCode) kurallarına uygundur.
 */
public class Channel implements Comparable<Channel> {
    private final int number;
    private final String name;
    private final ChannelCategory category;
    private final boolean hd;
    private boolean favorite;

    public Channel(int number, String name, ChannelCategory category, boolean hd) {
        if (number <= 0) {
            throw new IllegalArgumentException("Kanal numarası 1 veya daha büyük olmalıdır: " + number);
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Kanal adı boş bırakılamaz.");
        }
        this.number = number;
        this.name = name.trim();
        this.category = (category != null) ? category : ChannelCategory.GENEL;
        this.hd = hd;
        this.favorite = false;
    }

    public Channel(int number, String name) {
        this(number, name, ChannelCategory.GENEL, true);
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public ChannelCategory getCategory() {
        return category;
    }

    public boolean isHd() {
        return hd;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    @Override
    public int compareTo(Channel other) {
        return Integer.compare(this.number, other.number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Channel other)) return false;
        return number == other.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return String.format("[%02d] %-15s (%s) %s %s",
                number,
                name,
                category.getDisplayName(),
                hd ? "HD" : "SD",
                favorite ? "⭐" : "");
    }
}
