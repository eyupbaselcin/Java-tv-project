import java.util.ArrayList;

public class Tv {
    private String marka;
    private String boyut;
    private boolean acik = false;
    int kanalNumarasi;
    String kanalAdi;
    private int ses = 10;

    private ArrayList<Kanal> kanallar = new ArrayList<>(); // kanal listem için

    public String getBoyut() {
        return boyut;
    }

    public void setBoyut(String boyut) {
        this.boyut = boyut;
    }

    public String getMarka() {
        return marka;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public Tv(String marka, String boyut) {
        this.boyut = boyut;
        this.marka = marka;
        kanallariOlustur("cnn", "1");
        kanallariOlustur("trt haber", "2");
        kanallariOlustur("atv", "3");
        kanallariOlustur("kanal d", "4");
        kanallariOlustur("fox", "5");
        kanallariOlustur("ntv", "6");
        kanallariOlustur("HaberTÜRK","7");
    }

    private void kanallariOlustur(String ad, String no) {
        Kanal k = new Kanal(ad, no);
        kanallar.add(k);
    }

    // 2. seçenek hakkı için
    public void tvac() {
        if (!acik) {
            acik = true;
            System.out.println("TV açıldı.");
        } else {
            System.out.println("TV zaten açık.");
        }
    }

    // 3. seçenek hakkı için
    public void tvKapat() {
        if (acik) {
            acik = false;
            System.out.println("TV kapatıldı.");
        } else {
            System.out.println("TV zaten kapalı.");
        }
    }

    // 4. seçenek hakkı
    public void sesiAzalt() {
        if (acik && ses > 0) {
            ses--;
            System.out.println("Ses: " + ses);
        } else {
            if(!acik) {
                System.out.println("TV kapalı.");
            }
            System.out.println("TV kapalı veya ses seviyesi minimum zaten.");
        }
    }
    // 5. seçenek hakkı
    public void sesiArir() {
        if (acik && ses < 20) {
            ses++;
            System.out.println("Ses: " + ses);
        } else {
            if(!acik) {
                System.out.println("TV kapalı.");
            }
            System.out.println("TV kapalı veya ses seviyesi maksimum zaten.");
        }
    }

    @Override
    public String toString() {
        return "marka: " + marka + " \t boyut: " + boyut;

    }
    // 6. seçim hakkı için
    public void kanaliDegistir(int degistir) {
        if (!acik) {
            System.out.println("TV kapalı.");
            return;
        }
        if (degistir > 0 && degistir <= kanallar.size()) {
            kanalNumarasi = degistir;
            kanalAdi = kanallar.get(degistir - 1).getKanalAdi();
            System.out.println("Kanal değiştirildi: " + kanalNumarasi + " - " + kanalAdi);
        } else {
            System.out.println("Geçersiz kanal numarası.");
        }
    }
    // 7. seçim hakkı için
    public void kanalBilgisiniGoster() {
        if (!acik) {
            System.out.println("TV kapalı.");
            return;
        }
        if (kanalAdi == null || kanalNumarasi == 0) {
            System.out.println("Henüz kanal seçilmedi.");
        } else {
            System.out.println(kanalNumarasi + "." + kanalAdi);
        }
    }
}
