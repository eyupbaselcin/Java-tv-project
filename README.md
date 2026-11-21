# 📺 Smart TV Simulator Pro (Java 25 Enterprise Edition)

Bu proje, temel bir televizyon simülasyonunun **Clean Code**, **SOLID prensipleri**, **Kurumsal Katmanlı Mimari (MVC)**, **Observer (Gözlemci) Tasarım Deseni** ve modern bir **Grafiksel Kullanıcı Arayüzü (GUI)** ile profesyonel bir seviyeye dönüştürülmüş halidir.

---

## ⚡ HIZLI BAŞLATMA (Nasıl Çalıştırılır?)

Projenizi çalıştırmanın **4 farklı ve kolay yolu** bulunmaktadır. İhtiyacınıza en uygun olanını seçebilirsiniz:

### 1️⃣ Yöntem: Çift Tıklayarak Çalıştırma (En Kolay Yol 🖱️)

Proje klasörünü dosya yöneticisinde (Windows Gezgini) açın:

| Dosya Adı | Açıklama | Ne Yapar? |
| :--- | :--- | :--- |
| **`run-gui.bat`** | 🌟 **Görsel Arayüz (GUI)** | Çift tıkladığınızda kodları derler ve **Smart TV Ekranı & Uzaktan Kumandayı** ekranda açar. |
| **`run-cli.bat`** | 💻 **Konsol (Terminal) Modu** | Çift tıkladığınızda interaktif metin tabanlı kumanda menüsünü başlatır. |
| **`run-tests.bat`** | 🧪 **Birim Testleri** | Çift tıkladığınızda tüm iş kurallarını (18 birim test) otomatik test eder ve sonuçları gösterir. |

---

### 2️⃣ Yöntem: VS Code veya IntelliJ IDEA İçinden Çalıştırma (IDE)

1. Projeyi IDE'nizde açın.
2. `src/main/java/com/javatv/Main.java` dosyasını açın.
3. `public static void main(String[] args)` fonksiyonunun yanındaki yeşil **Run ▶** (Çalıştır) butonuna basın.
   - **Görsel Arayüzü (GUI)** açmak için parametresiz çalıştırmanız yeterlidir.
   - **Konsol Modunu (CLI)** açmak için IDE çalıştırma ayarlarına (Program Arguments) `--cli` yazabilirsiniz.

---

### 3️⃣ Yöntem: Terminal / PowerShell ile Çalıştırma

Proje dizininde bir PowerShell veya Komut İstemi (CMD) penceresi açın:

#### 📺 Görsel Arayüzü (GUI) Başlatmak İçin:
```powershell
# Seçenek A: Hazır PowerShell scripti ile
.\run-gui.ps1

# Seçenek B: Batch scripti ile
.\run-gui.bat
```

#### 💻 Konsol (CLI) Menüsünü Başlatmak İçin:
```powershell
.\run-cli.bat
```

#### 🧪 Testleri Çalıştırmak İçin:
```powershell
.\run-tests.bat
```

---

### 4️⃣ Yöntem: Maven ile Derleme ve Çalıştırma

Projede standart `pom.xml` mevcuttur. Maven yüklü ise:
```bash
# 1. Projeyi derleyin ve paketleyin
mvn clean package

# 2. Üretilen JAR dosyasını çalıştırın (GUI)
java -jar target/smart-tv-simulator-2.0.0.jar

# 3. Konsol modunda çalıştırmak için
java -jar target/smart-tv-simulator-2.0.0.jar --cli
```

---

## 🎮 Smart TV Simülatörü Nasıl Kullanılır? (Kullanım Kılavuzu)

Arayüz açıldığında gerçek bir Smart TV ve yanındaki uzaktan kumanda ile karşılaşırsınız:

```
+---------------------------------------------------------------+  +---------------------+
| [🔴/🟢 LED]  SAMSUNG NEO QLED 65"         Wi-Fi • HDMI 1 • Saat |  |    SMART REMOTE     |
+---------------------------------------------------------------+  +---------------------+
|                                                               |  | [⏻ GÜÇ]   [🔇 SESSİZ]|
|   [02] TRT Haber • 📰 Haber • [4K HD]                         |  | [ 1 ]  [ 2 ]  [ 3 ] |
|                                                               |  | [ 4 ]  [ 5 ]  [ 6 ] |
|   🔴 CANLI | TÜRKİYE VE DÜNYA GÜNDEMİ...                     |  | [ 7 ]  [ 8 ]  [ 9 ] |
|                                                               |  | [EPG]  [ 0 ]  [INFO]|
|          ( 🔊 [██████████░░░░░] 12/30 OSD Barı )              |  | [VOL+]       [CH ▲] |
|                                                               |  | [VOL-]       [CH ▼] |
+---------------------------------------------------------------+  | [Haber]     [Spor]  |
|                         [ TV AYAĞI ]                          |  | [Sinema]   [Belgesel|
+---------------------------------------------------------------+  +---------------------+
```

1. **Açma / Kapatma:**
   - TV başlangıçta bekleme (Standby) modundadır. Kumandadaki **kırmızı ⏻ GÜÇ** tuşuna basarak televizyonu açın. LED ışığı kırmızıdan **yeşile** dönecektir.
2. **Kanal Değiştirme:**
   - **CH ▲ / CH ▼** tuşları veya **0-9** sayı tuşları ile kanalları gezebilirsiniz.
   - Kanal değiştikçe TV ekranındaki **canlı yayın arka planı ve alt bant akışı (Ticker)** kanal türüne göre dinamik olarak adapte olur:
     - **Haber Kanalları:** Kırmızı-mavi stüdyo teması ve son dakika haber bandı.
     - **Spor Kanalları:** Stadyum yeşili tema ve canlı maç skorları.
     - **Sinema Kanalları:** Mor sinematik tema ve film detayları.
     - **Belgesel & Müzik:** Okyanus/doğa teması veya dinamik ses dalgası animasyonu.
3. **Ses Ayarı ve OSD (On-Screen Display):**
   - **VOL +** ve **VOL -** tuşlarına bastığınızda ekranın alt kısmında modern bir ses seviye çubuğu belirir ve 2.5 saniye sonra kendiliğinden kaybolur.
   - **🔇 SESSİZ (Mute)** tuşuna basarak sesi anında kesebilirsiniz. Sesi artırdığınızda TV otomatik olarak sessiz moddan çıkar.
4. **Kanal Rehberi (EPG) ve Yönetim:**
   - Kumandadan **EPG** tuşuna basarak ekran üzeri yarı şeffaf kanal rehberini açıp kapatabilirsiniz.
   - Üst bardaki **"📋 Kanal Yöneticisi"** butonuna tıklayarak tablo üzerinden kanalları listeleyebilir, **yeni kanal ekleyebilir**, silebilir veya **favori (⭐)** yapabilirsiniz.

---

## 🌟 Yapılan Profesyonel İyileştirmeler (Neler Değişti?)

| Kriter | Eski Kod Yapısı | Yeni Profesyonel Mimari |
| :--- | :--- | :--- |
| **Mimari** | Tek dosyada spagetti mantık, statik değişkenler | **Katmanlı Mimari (MVC - Model View Controller)** |
| **Arayüz (UI)** | Sadece Scanner ile ilkel terminal döngüsü | **Smart TV OLED Simülatörü (Swing GUI)** + **Gelişmiş CLI** |
| **Tasarım Deseni** | Yok | **Observer (Event-Driven)**, **Repository**, **Adapter** |
| **Kanal Modeli** | String listesi (CNN, TRT vb.) | **Channel Nesnesi** (Numara, İsim, Kategori Enum, Format, Favori) |
| **Hata Yönetimi** | `catch (Exception e)` ile kör yakalama | **Özel İstisnalar (TvPoweredOffException, ChannelNotFoundException)** |
| **Test Edilebilirlik** | Test edilemez, sıkı bağlı (tightly coupled) | **Birim Test Paketi (18/18 Unit Test Tam Başarılı)** |
| **Derleme & Taşınabilirlik**| Elle tekil derleme | **Maven (`pom.xml`)** + Bağımsız `.bat` ve `.ps1` Başlatıcılar |

---

## 🏗️ Proje Dizin Yapısı

```
Java-tv-project-main/
├── src/main/java/com/javatv/
│   ├── controller/
│   │   └── TvController.java             # UI ile Servis katmanını koordine eden MVC Kontrolörü
│   ├── enums/
│   │   ├── ChannelCategory.java          # Haber, Spor, Sinema, Belgesel, Müzik, Çocuk
│   │   └── PowerState.java               # ON, OFF, STANDBY
│   ├── event/
│   │   └── TvEventListener.java          # Observer Deseni (GUI'nin TV durumunu dinlemesi)
│   ├── exception/
│   │   ├── TvException.java              # Temel TV istisnası
│   │   ├── TvPoweredOffException.java    # TV kapalıyken işlem koruması
│   │   ├── ChannelNotFoundException.java # Kanal bulunamadı hatası
│   │   └── VolumeOutOfBoundsException.java # Ses sınırı taşma koruması
│   ├── model/
│   │   ├── Channel.java                  # Kanal veri modeli (Comparable, equals/hashCode)
│   │   └── Television.java               # Televizyon donanım modeli
│   ├── repository/
│   │   ├── ChannelRepository.java        # Veri erişim katmanı arayüzü
│   │   └── InMemoryChannelRepository.java# 21 adet hazır Türk & dünya kanalı veri deposu
│   ├── service/
│   │   ├── TvService.java                # İş kuralları arayüzü (Business Logic)
│   │   └── TvServiceImpl.java            # Ses sınırları, kanal döngüsü, güç yönetimi
│   ├── ui/
│   │   ├── gui/
│   │   │   └── SmartTvGui.java           # OLED ekran, kumanda, OSD ve kanal yönetim GUI'si
│   │   └── console/
│   │       └── ConsoleTvApp.java         # Yenilenmiş interaktif ASCII konsol kumandası
│   └── Main.java                         # Dependency Injection ve Başlatıcı
│
├── src/test/java/com/javatv/
│   └── TvServiceTest.java                # 18 adet kapsamlı Birim Test (Unit Test)
│
├── run-gui.bat                           # 🌟 Çift tıkla Görsel GUI'yi başlat
├── run-cli.bat                           # 💻 Çift tıkla Konsol Kumandasını başlat
├── run-tests.bat                         # 🧪 Çift tıkla Birim Testleri çalıştır
├── run-gui.ps1                           # PowerShell GUI başlatıcı
├── pom.xml                               # Maven Proje Yapılandırması
├── Kanal.java, Tv.java, Program.java     # Eski çağrılar için geriye dönük uyumluluk adaptörleri
└── README.md                             # Bu dokümantasyon dosyası
```

---

## 🧪 Birim Testler (Unit Tests)

Tüm iş kuralları bağımsız test sınıfı ile test edilmiştir:
- TV kapalıyken ses/kanal değiştirilemez (`TvPoweredOffException` doğrulaması).
- Ses minimum 0, maksimum 30 sınırını aşamaz (Clamping kontrolü).
- Kanal ileri/geri geçişlerinde liste sonundan başına döngü testi.
- Sessize alma (Mute) ve ses tuşuna basıldığında otomatik sessizden çıkma kontrolü.
- Kanal ekleme ve silme işlemlerinin veri tabanına yansıması.

```
==================================================
  RUNNING SMART TV APPLICATION UNIT TESTS        
==================================================
 [PASS] TV initially should be OFF
 [PASS] TV should be ON after powerOn()
 [PASS] TV should be OFF after powerOff()
 [PASS] Properly throws TvPoweredOffException when altering volume while OFF
 [PASS] Volume set to maximum
 [PASS] Volume should clamp at max boundary
 [PASS] Volume set to minimum
 [PASS] Volume should clamp at min boundary
 [PASS] Channel number should switch to 2
 [PASS] Channel name should match channel 2
 [PASS] Next channel from 2 should be 3
 [PASS] Previous channel from 3 should return to 2
 [PASS] TV initially should not be muted
 [PASS] TV should be muted after toggleMute()
 [PASS] Volume up should automatically unmute TV
 [PASS] Repository count should increment on add
 [PASS] Should find newly added channel
 [PASS] Repository count should decrement on remove
==================================================
TEST RESULTS: 18 PASSED, 0 FAILED.
==================================================
```

---

## 📋 Sistem Gereksinimleri
- **Java:** JDK 17, 21 veya 25 (Java Desktop / Swing çekirdeğe dahil olduğundan harici hiçbir kütüphaneye veya internet bağlantısına ihtiyaç duymaz).
- **İşletim Sistemi:** Windows, macOS veya Linux.
