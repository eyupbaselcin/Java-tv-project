package com.javatv.enums;

/**
 * Televizyon kanallarının kategorilerini temsil eden numaralandırma.
 */
public enum ChannelCategory {
    HABER("Haber", "📰"),
    SPOR("Spor", "⚽"),
    SINEMA("Sinema & Dizi", "🎬"),
    BELGESEL("Belgesel", "🌍"),
    MUZIK("Müzik", "🎵"),
    COCUK("Çocuk & Animasyon", "🧸"),
    GENEL("Genel Yayın", "📺");

    private final String displayName;
    private final String icon;

    ChannelCategory(String displayName, String icon) {
        this.displayName = displayName;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return icon + " " + displayName;
    }
}
