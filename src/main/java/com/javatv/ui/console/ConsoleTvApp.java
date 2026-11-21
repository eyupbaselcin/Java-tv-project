package com.javatv.ui.console;

import com.javatv.controller.TvController;
import com.javatv.enums.ChannelCategory;
import com.javatv.model.Channel;
import com.javatv.model.Television;

import java.util.List;
import java.util.Scanner;

/**
 * Konsol (CLI) tabanlı kullanıcı arayüzü.
 * Orijinal konsol uygulamasının nesne yönelimli ve profesyonel sürümüdür.
 */
public class ConsoleTvApp {
    private final TvController controller;
    private final Scanner scanner;

    public ConsoleTvApp(TvController controller) {
        this.controller = controller;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        printWelcomeBanner();

        while (running) {
            printMenu();
            System.out.print("➤ Seçiminiz (0-9): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "0" -> {
                    System.out.println("\n[+] TV simülasyonundan çıkış yapıldı. İyi günler!");
                    running = false;
                }
                case "1" -> handlePowerToggle();
                case "2" -> handleVolumeUp();
                case "3" -> handleVolumeDown();
                case "4" -> handleToggleMute();
                case "5" -> handleNextChannel();
                case "6" -> handlePreviousChannel();
                case "7" -> handleChangeChannelByNumber();
                case "8" -> handleShowAllChannels();
                case "9" -> handleTvStatus();
                default -> System.out.println("[-] Geçersiz seçim! Lütfen menüdeki numaralardan birini giriniz.");
            }
        }
    }

    private void printWelcomeBanner() {
        Television tv = controller.getTelevision();
        System.out.println("============================================================");
        System.out.println("         📺 SMART TELEVISION CONSOLE CONTROLLER             ");
        System.out.println("       Model: " + tv.getBrand() + " " + tv.getModel() + " (" + tv.getScreenSizeInches() + "\")");
        System.out.println("============================================================");
    }

    private void printMenu() {
        Television tv = controller.getTelevision();
        System.out.println("\n----------------- [ ANA KUMANDA MENÜSÜ ] ------------------");
        System.out.println("  1- TV Aç / Kapat         (Mevcut: " + (tv.isOn() ? "AÇIK 🟢" : "KAPALI 🔴") + ")");
        System.out.println("  2- Sesi Artır (+)        (Ses: " + tv.getVolume() + (tv.isMuted() ? " [SESSİZ]" : "") + ")");
        System.out.println("  3- Sesi Azalt (-)");
        System.out.println("  4- Sessize Al / Aç (Mute)");
        System.out.println("  5- Sonraki Kanal (CH +)");
        System.out.println("  6- Önceki Kanal (CH -)");
        System.out.println("  7- Numarayla Kanal Değiştir");
        System.out.println("  8- Kanal Listesini Görüntüle");
        System.out.println("  9- Detaylı TV Durumunu Göster");
        System.out.println("  0- Çıkış Yap");
        System.out.println("-----------------------------------------------------------");
    }

    private void handlePowerToggle() {
        boolean newState = controller.togglePower();
        System.out.println(newState ? "[+] TV Açıldı. Ekran yayına hazır." : "[-] TV Kapatıldı (Bekleme Modu).");
    }

    private void handleVolumeUp() {
        if (!ensureTvOn()) return;
        int vol = controller.volumeUp();
        if (vol >= 0) {
            printVolumeBar(vol);
        }
    }

    private void handleVolumeDown() {
        if (!ensureTvOn()) return;
        int vol = controller.volumeDown();
        if (vol >= 0) {
            printVolumeBar(vol);
        }
    }

    private void handleToggleMute() {
        if (!ensureTvOn()) return;
        boolean muted = controller.toggleMute();
        System.out.println(muted ? "[!] TV Sessize Alındı (Mute Aktif)." : "[+] TV Sesi Açıldı.");
    }

    private void handleNextChannel() {
        if (!ensureTvOn()) return;
        Channel ch = controller.nextChannel();
        if (ch != null) {
            System.out.println("[+] Kanal Değiştirildi: " + ch);
        }
    }

    private void handlePreviousChannel() {
        if (!ensureTvOn()) return;
        Channel ch = controller.previousChannel();
        if (ch != null) {
            System.out.println("[+] Kanal Değiştirildi: " + ch);
        }
    }

    private void handleChangeChannelByNumber() {
        if (!ensureTvOn()) return;
        System.out.print("Gitmek istediğiniz kanal numarasını giriniz: ");
        try {
            int no = Integer.parseInt(scanner.nextLine().trim());
            Channel ch = controller.changeChannel(no);
            if (ch != null) {
                System.out.println("[+] Kanal Değiştirildi: " + ch);
            }
        } catch (NumberFormatException e) {
            System.out.println("[-] Hata: Geçerli bir kanal numarası girmelisiniz.");
        }
    }

    private void handleShowAllChannels() {
        List<Channel> channels = controller.getAllChannels();
        System.out.println("\n============ MEVCUT KANAL LİSTESİ (" + channels.size() + ") ============");
        for (Channel c : channels) {
            boolean isCur = controller.getCurrentChannel().map(cur -> cur.getNumber() == c.getNumber()).orElse(false);
            System.out.println((isCur ? " 👉 " : "    ") + c);
        }
        System.out.println("========================================================");
    }

    private void handleTvStatus() {
        Television tv = controller.getTelevision();
        System.out.println("\n============ TELEVİZYON SİSTEM BİLGİSİ ============");
        System.out.println("  Marka/Model  : " + tv.getBrand() + " " + tv.getModel());
        System.out.println("  Ekran Boyutu : " + tv.getScreenSizeInches() + " inç");
        System.out.println("  Güç Durumu   : " + tv.getPowerState().getDescription());
        System.out.println("  Ses Seviyesi : " + tv.getVolume() + "/" + Television.MAX_VOLUME + (tv.isMuted() ? " [SESSİZ]" : ""));
        System.out.println("  Aktif Kanal  : " + controller.getCurrentChannel().map(Channel::toString).orElse("Seçilmedi (TV Kapalı)"));
        System.out.println("====================================================");
    }

    private boolean ensureTvOn() {
        if (!controller.isPoweredOn()) {
            System.out.println("[-] İşlem yapılamaz: TV şu anda KAPALI. Önce '1' seçeneği ile TV'yi açınız.");
            return false;
        }
        return true;
    }

    private void printVolumeBar(int volume) {
        int max = Television.MAX_VOLUME;
        int filled = (volume * 20) / max;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < 20; i++) {
            if (i < filled) bar.append("█");
            else bar.append("░");
        }
        bar.append("] ").append(volume).append("/").append(max);
        System.out.println("[Ses] " + bar);
    }
}
