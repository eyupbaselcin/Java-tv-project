import com.javatv.enums.ChannelCategory;
import com.javatv.model.Channel;

/**
 * Kanal sınıfı - Geriye dönük uyumluluk ve temiz mimari sarmalayıcısı (Adapter).
 * Yeni projede com.javatv.model.Channel sınıfı temel alınmıştır.
 */
public class Kanal {
    private String kanalAdi;
    private String kanalNo;
    private ChannelCategory kategori;

    public Kanal(String ad, String no) {
        this(ad, no, ChannelCategory.GENEL);
    }

    public Kanal(String ad, String no, ChannelCategory kategori) {
        this.kanalAdi = (ad != null) ? ad.trim() : "Bilinmeyen Kanal";
        this.kanalNo = (no != null) ? no.trim() : "1";
        this.kategori = (kategori != null) ? kategori : ChannelCategory.GENEL;
    }

    public String getKanalAdi() {
        return kanalAdi;
    }

    public void setKanalAdi(String kanalAdi) {
        this.kanalAdi = kanalAdi;
    }

    public String getKanalNo() {
        return kanalNo;
    }

    public void setKanalNo(String kanalNo) {
        this.kanalNo = kanalNo;
    }

    public ChannelCategory getKategori() {
        return kategori;
    }

    public void setKategori(ChannelCategory kategori) {
        this.kategori = kategori;
    }

    /**
     * Yeni mimarideki Channel modeline dönüştürme yardımcısı.
     */
    public Channel toDomainModel() {
        int no = 1;
        try {
            no = Integer.parseInt(kanalNo);
        } catch (NumberFormatException ignored) {}
        return new Channel(no, kanalAdi, kategori, true);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", kanalNo, kanalAdi, kategori.getDisplayName());
    }
}
