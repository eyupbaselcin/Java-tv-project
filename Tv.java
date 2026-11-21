import com.javatv.enums.ChannelCategory;
import com.javatv.model.Channel;
import com.javatv.model.Television;
import com.javatv.repository.ChannelRepository;
import com.javatv.repository.InMemoryChannelRepository;
import com.javatv.service.TvService;
import com.javatv.service.TvServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Televizyon Sınıfı - Profesyonel Servis Katmanı ile Entegre Edilmiş Sürüm.
 * Orijinal metod isimlerini (tvac, tvKapat, sesiAzalt, sesiArir) korurken
 * arkasında SOLID ve Observer mimarisi barındırır.
 */
public class Tv {
    private final TvService tvService;
    private final Television television;

    public Tv(String marka, String boyut) {
        int size = 55;
        try {
            // Boyut ifadesinden sayısal inç değerini çıkar
            String cleanSize = boyut.replaceAll("[^0-9]", "");
            if (!cleanSize.isEmpty()) {
                size = Integer.parseInt(cleanSize);
            }
        } catch (Exception ignored) {}

        this.television = new Television(marka, "Smart Crystal Series", size);
        ChannelRepository repository = new InMemoryChannelRepository();
        this.tvService = new TvServiceImpl(this.television, repository);
    }

    public String getMarka() {
        return television.getBrand();
    }

    public String getBoyut() {
        return television.getScreenSizeInches() + " inç";
    }

    public boolean isAcik() {
        return tvService.isPoweredOn();
    }

    // 2. Seçenek: TV Aç
    public void tvac() {
        if (!tvService.isPoweredOn()) {
            tvService.powerOn();
            System.out.println("[+] TV açıldı. (Model: " + television.getBrand() + " " + television.getScreenSizeInches() + "\")");
        } else {
            System.out.println("[!] TV zaten açık.");
        }
    }

    // 3. Seçenek: TV Kapat
    public void tvKapat() {
        if (tvService.isPoweredOn()) {
            tvService.powerOff();
            System.out.println("[-] TV kapatıldı (Bekleme Modu).");
        } else {
            System.out.println("[!] TV zaten kapalı.");
        }
    }

    // 4. Seçenek: Sesi Azalt
    public void sesiAzalt() {
        if (!tvService.isPoweredOn()) {
            System.out.println("[-] İşlem başarısız: TV kapalı. Lütfen önce TV'yi açınız.");
            return;
        }
        int cur = television.getVolume();
        if (cur <= Television.MIN_VOLUME) {
            System.out.println("[!] Ses seviyesi minimum zaten (" + Television.MIN_VOLUME + ").");
            return;
        }
        int newVol = tvService.volumeDown();
        System.out.println("[🔊] Ses Seviyesi Azaltıldı: " + newVol + "/" + Television.MAX_VOLUME);
    }

    // 5. Seçenek: Sesi Artır (Orijinal imza sesiArir ve düzeltilmiş sesiArtir)
    public void sesiArir() {
        sesiArtir();
    }

    public void sesiArtir() {
        if (!tvService.isPoweredOn()) {
            System.out.println("[-] İşlem başarısız: TV kapalı. Lütfen önce TV'yi açınız.");
            return;
        }
        int cur = television.getVolume();
        if (cur >= Television.MAX_VOLUME) {
            System.out.println("[!] Ses seviyesi maksimum zaten (" + Television.MAX_VOLUME + ").");
            return;
        }
        int newVol = tvService.volumeUp();
        System.out.println("[🔊] Ses Seviyesi Artırıldı: " + newVol + "/" + Television.MAX_VOLUME);
    }

    // 6. Seçenek: Kanal Değiştir
    public void kanaliDegistir(int kanalNo) {
        if (!tvService.isPoweredOn()) {
            System.out.println("[-] İşlem başarısız: TV kapalı. Lütfen önce TV'yi açınız.");
            return;
        }
        try {
            Channel ch = tvService.changeChannel(kanalNo);
            System.out.println("[📺] Kanal Değiştirildi: " + ch.getNumber() + " - " + ch.getName() + " (" + ch.getCategory().getDisplayName() + ")");
        } catch (Exception e) {
            System.out.println("[-] Hata: " + e.getMessage());
        }
    }

    // 7. Seçenek: Kanal Bilgisini Göster
    public void kanalBilgisiniGoster() {
        if (!tvService.isPoweredOn()) {
            System.out.println("[-] TV kapalı.");
            return;
        }
        tvService.getCurrentChannel().ifPresentOrElse(
                ch -> System.out.println("[ℹ️] Aktif Kanal: " + ch),
                () -> System.out.println("[!] Henüz bir kanal seçilmedi.")
        );
    }

    // Tüm Kanalları Listele
    public List<Kanal> getKanalListesi() {
        List<Kanal> list = new ArrayList<>();
        for (Channel c : tvService.getAllChannels()) {
            list.add(new Kanal(c.getName(), String.valueOf(c.getNumber()), c.getCategory()));
        }
        return list;
    }

    public TvService getTvService() {
        return tvService;
    }

    public Television getTelevision() {
        return television;
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Durum: %s | Ses: %d",
                television.getBrand(),
                getBoyut(),
                tvService.isPoweredOn() ? "AÇIK" : "KAPALI",
                television.getVolume());
    }
}
