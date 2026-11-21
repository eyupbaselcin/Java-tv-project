package com.javatv;

import com.javatv.controller.TvController;
import com.javatv.model.Television;
import com.javatv.repository.ChannelRepository;
import com.javatv.repository.InMemoryChannelRepository;
import com.javatv.service.TvService;
import com.javatv.service.TvServiceImpl;
import com.javatv.ui.console.ConsoleTvApp;
import com.javatv.ui.gui.SmartTvGui;

import javax.swing.*;

/**
 * Ana Uygulama Giriş Noktası (Application Entrypoint).
 * Bağımlılık Enjeksiyonunu (Dependency Injection) koordine eder ve
 * isteğe göre Görsel GUI veya Konsol CLI arayüzünü başlatır.
 */
public class Main {
    public static void main(String[] args) {
        // 1. Model ve Veri Katmanı
        Television tv = new Television("Samsung", "Neo QLED 4K Pro", 65);
        ChannelRepository repository = new InMemoryChannelRepository();

        // 2. Servis Katmanı
        TvService service = new TvServiceImpl(tv, repository);

        // 3. Kontrolör Katmanı (MVC)
        TvController controller = new TvController(service);

        // 4. Arayüz Seçimi: Argüman olarak --cli verilirse konsol, aksi takdirde GUI başlatılır.
        boolean cliMode = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                cliMode = true;
                break;
            }
        }

        if (cliMode) {
            ConsoleTvApp consoleApp = new ConsoleTvApp(controller);
            consoleApp.start();
        } else {
            // Modern Swing Görsel Arayüzünü Başlat
            SwingUtilities.invokeLater(() -> {
                try {
                    // Sistem yerel tema desteği
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                SmartTvGui gui = new SmartTvGui(controller);
                gui.setVisible(true);
            });
        }
    }
}
