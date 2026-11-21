package com.javatv.ui.gui;

import com.javatv.controller.TvController;
import com.javatv.enums.ChannelCategory;
import com.javatv.enums.PowerState;
import com.javatv.event.TvEventListener;
import com.javatv.model.Channel;
import com.javatv.model.Television;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Profesyonel Smart TV Simülatörü ve Görsel Arayüz (GUI).
 * Gerçekçi OLED ekran, On-Screen Display (OSD), animasyonlu görselleştirici,
 * uzaktan kumanda (Remote Control) ve kanal yönetim paneli içerir.
 */
public class SmartTvGui extends JFrame implements TvEventListener {
    private final TvController controller;

    // Arayüz Bileşenleri
    private TvScreenPanel tvScreenPanel;
    private JLabel ledIndicator;
    private JLabel tvBrandLabel;
    private JLabel liveClockLabel;

    // Kanal Tablosu ve Yönetim
    private JTable channelTable;
    private DefaultTableModel tableModel;
    private JComboBox<ChannelCategory> categoryFilterCombo;

    // OSD Sayaç ve Durum
    private Timer osdTimer;
    private boolean showVolumeOsd = false;
    private boolean showGuideOsd = false;
    private String customNotification = null;
    private Timer notificationTimer;

    // Canlı Saat Sayacı
    private Timer clockTimer;

    public SmartTvGui(TvController controller) {
        this.controller = controller;
        this.controller.addEventListener(this);

        initWindow();
        initComponents();
        setupTimers();
        refreshChannelTable();
        updateDisplayState();
    }

    private void initWindow() {
        setTitle("Smart TV Pro Simulator - " + controller.getTelevision().getBrand() + " " + controller.getTelevision().getScreenSizeInches() + "\"");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 800);
        setMinimumSize(new Dimension(1080, 720));
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(18, 20, 24));
        setLayout(new BorderLayout(15, 15));
    }

    private void initComponents() {
        // Üst Araç Çubuğu / Başlık
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Orta: TV Çerçevesi ve Ekranı
        JPanel tvArea = createTvEnclosurePanel();
        add(tvArea, BorderLayout.CENTER);

        // Sağ: Uzaktan Kumanda ve Hızlı Kontroller
        JPanel remoteArea = createRemoteControlPanel();
        add(remoteArea, BorderLayout.EAST);

        // Alt: Durum ve Bilgi Çubuğu
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(24, 27, 33));
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel titleLabel = new JLabel("📺 SMART TV ULTRA HD SIMULATOR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(240, 240, 245));

        JPanel rightInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightInfo.setOpaque(false);

        liveClockLabel = new JLabel();
        liveClockLabel.setFont(new Font("Consolas", Font.BOLD, 15));
        liveClockLabel.setForeground(new Color(0, 210, 255));

        JButton btnManageChannels = createStyledButton("📋 Kanal Yöneticisi", new Color(45, 52, 65), Color.WHITE);
        btnManageChannels.addActionListener(e -> openChannelManagerDialog());

        JButton btnResetChannels = createStyledButton("🔄 Kanalları Sıfırla", new Color(45, 52, 65), Color.LIGHT_GRAY);
        btnResetChannels.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Kanal listesi varsayılan ayarlara döndürülsün mü?",
                    "Sıfırlama Onayı",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Kanalları varsayılana sıfırla
                controller.getTelevision().setCurrentChannel(null);
                refreshChannelTable();
                updateDisplayState();
            }
        });

        rightInfo.add(liveClockLabel);
        rightInfo.add(btnManageChannels);
        rightInfo.add(btnResetChannels);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(rightInfo, BorderLayout.EAST);
        return header;
    }

    private JPanel createTvEnclosurePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(5, 20, 5, 10));

        // TV Dış Kasa (Bezel)
        JPanel tvBezel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // TV Kasa Çerçevesi
                g2.setColor(new Color(10, 10, 12));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                // Metalik Dış Çizgi
                g2.setColor(new Color(50, 55, 65));
                g2.setStroke(new BasicStroke(2.5f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 24, 24);
                g2.dispose();
            }
        };
        tvBezel.setBorder(new EmptyBorder(16, 16, 12, 16));

        // Ekran Bileşeni
        tvScreenPanel = new TvScreenPanel();
        tvBezel.add(tvScreenPanel, BorderLayout.CENTER);

        // TV Alt Çıta (Logo & LED)
        JPanel bottomBezel = new JPanel(new BorderLayout());
        bottomBezel.setOpaque(false);
        bottomBezel.setPreferredSize(new Dimension(100, 30));
        bottomBezel.setBorder(new EmptyBorder(6, 10, 0, 10));

        Television tv = controller.getTelevision();
        tvBrandLabel = new JLabel(tv.getBrand().toUpperCase() + " • " + tv.getModel() + " (" + tv.getScreenSizeInches() + "\")", SwingConstants.CENTER);
        tvBrandLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tvBrandLabel.setForeground(new Color(140, 145, 160));

        ledIndicator = new JLabel("●");
        ledIndicator.setFont(new Font("Segoe UI", Font.BOLD, 14));
        ledIndicator.setForeground(Color.RED);
        ledIndicator.setToolTipText("Güç Göstergesi");

        bottomBezel.add(ledIndicator, BorderLayout.WEST);
        bottomBezel.add(tvBrandLabel, BorderLayout.CENTER);

        tvBezel.add(bottomBezel, BorderLayout.SOUTH);
        wrapper.add(tvBezel, BorderLayout.CENTER);

        // TV Ayağı (Stand)
        JPanel standPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                // Ayak boynu
                g2.setColor(new Color(35, 38, 45));
                g2.fillRect(w / 2 - 25, 0, 50, 18);
                // Ayak tabanı
                g2.setColor(new Color(25, 27, 33));
                g2.fillRoundRect(w / 2 - 120, 15, 240, 12, 10, 10);
                g2.setColor(new Color(60, 65, 75));
                g2.drawRoundRect(w / 2 - 120, 15, 240, 12, 10, 10);
                g2.dispose();
            }
        };
        standPanel.setOpaque(false);
        standPanel.setPreferredSize(new Dimension(100, 30));
        wrapper.add(standPanel, BorderLayout.SOUTH);

        return wrapper;
    }

    private JPanel createRemoteControlPanel() {
        JPanel remoteContainer = new JPanel(new BorderLayout());
        remoteContainer.setOpaque(false);
        remoteContainer.setPreferredSize(new Dimension(280, 0));
        remoteContainer.setBorder(new EmptyBorder(5, 5, 15, 20));

        // Kumanda Gövdesi (Ergonomik Tasarım)
        JPanel remoteBody = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 33, 40), getWidth(), getHeight(), new Color(20, 22, 27));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 35, 35);
                g2.setColor(new Color(60, 65, 78));
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 35, 35);
                g2.dispose();
            }
        };
        remoteBody.setLayout(new BoxLayout(remoteBody, BoxLayout.Y_AXIS));
        remoteBody.setBorder(new EmptyBorder(20, 18, 20, 18));

        // Başlık
        JLabel remoteTitle = new JLabel("SMART REMOTE", SwingConstants.CENTER);
        remoteTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        remoteTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        remoteTitle.setForeground(new Color(130, 140, 160));
        remoteBody.add(remoteTitle);
        remoteBody.add(Box.createVerticalStrut(15));

        // Üst Tuşlar: Power ve Mute
        JPanel topRow = new JPanel(new GridLayout(1, 2, 12, 0));
        topRow.setOpaque(false);
        topRow.setMaximumSize(new Dimension(240, 42));

        JButton btnPower = createRoundButton("⏻ GÜÇ", new Color(220, 40, 50), Color.WHITE);
        btnPower.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnPower.addActionListener(e -> controller.togglePower());

        JButton btnMute = createRoundButton("🔇 SESSİZ", new Color(55, 60, 72), Color.WHITE);
        btnMute.addActionListener(e -> controller.toggleMute());

        topRow.add(btnPower);
        topRow.add(btnMute);
        remoteBody.add(topRow);
        remoteBody.add(Box.createVerticalStrut(15));

        // Sayı Tuş Takımı (1-9, 0)
        JPanel numPad = new JPanel(new GridLayout(4, 3, 8, 8));
        numPad.setOpaque(false);
        numPad.setMaximumSize(new Dimension(240, 160));

        for (int i = 1; i <= 9; i++) {
            final int num = i;
            JButton btn = createRoundButton(String.valueOf(num), new Color(42, 46, 56), Color.WHITE);
            btn.addActionListener(e -> {
                if (controller.isPoweredOn()) {
                    controller.changeChannel(num);
                }
            });
            numPad.add(btn);
        }

        JButton btnGuide = createRoundButton("EPG", new Color(48, 55, 68), new Color(0, 210, 255));
        btnGuide.setToolTipText("Kanal Rehberi Aç/Kapat");
        btnGuide.addActionListener(e -> {
            if (controller.isPoweredOn()) {
                showGuideOsd = !showGuideOsd;
                tvScreenPanel.repaint();
            }
        });

        JButton btnZero = createRoundButton("0", new Color(42, 46, 56), Color.WHITE);
        btnZero.addActionListener(e -> {
            if (controller.isPoweredOn()) {
                controller.changeChannel(0);
            }
        });

        JButton btnInfo = createRoundButton("INFO", new Color(48, 55, 68), new Color(255, 195, 0));
        btnInfo.setToolTipText("Kanal Bilgisi");
        btnInfo.addActionListener(e -> {
            if (controller.isPoweredOn()) {
                triggerNotification("Kanal Bilgisi: " + controller.getCurrentChannel().map(Channel::toString).orElse("Yok"));
            }
        });

        numPad.add(btnGuide);
        numPad.add(btnZero);
        numPad.add(btnInfo);
        remoteBody.add(numPad);
        remoteBody.add(Box.createVerticalStrut(15));

        // Ses ve Kanal Rocker Düğmeleri (VOL +/- ve CH +/-)
        JPanel rockerPanel = new JPanel(new GridLayout(1, 2, 14, 0));
        rockerPanel.setOpaque(false);
        rockerPanel.setMaximumSize(new Dimension(240, 110));

        // VOL Bloğu
        JPanel volBlock = new JPanel(new GridLayout(2, 1, 0, 6));
        volBlock.setOpaque(false);
        JButton btnVolUp = createRoundButton("VOL +", new Color(38, 80, 145), Color.WHITE);
        btnVolUp.addActionListener(e -> controller.volumeUp());
        JButton btnVolDown = createRoundButton("VOL -", new Color(38, 80, 145), Color.WHITE);
        btnVolDown.addActionListener(e -> controller.volumeDown());
        volBlock.add(btnVolUp);
        volBlock.add(btnVolDown);

        // CH Bloğu
        JPanel chBlock = new JPanel(new GridLayout(2, 1, 0, 6));
        chBlock.setOpaque(false);
        JButton btnChUp = createRoundButton("CH ▲", new Color(40, 125, 75), Color.WHITE);
        btnChUp.addActionListener(e -> controller.nextChannel());
        JButton btnChDown = createRoundButton("CH ▼", new Color(40, 125, 75), Color.WHITE);
        btnChDown.addActionListener(e -> controller.previousChannel());
        chBlock.add(btnChUp);
        chBlock.add(btnChDown);

        rockerPanel.add(volBlock);
        rockerPanel.add(chBlock);
        remoteBody.add(rockerPanel);
        remoteBody.add(Box.createVerticalStrut(15));

        // Kategori Hızlı Erişim Düğmeleri
        JLabel catLabel = new JLabel("HIZLI KATEGORİLER", SwingConstants.CENTER);
        catLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        catLabel.setForeground(new Color(110, 120, 140));
        remoteBody.add(catLabel);
        remoteBody.add(Box.createVerticalStrut(8));

        JPanel catGrid = new JPanel(new GridLayout(3, 2, 6, 6));
        catGrid.setOpaque(false);
        catGrid.setMaximumSize(new Dimension(240, 90));

        addQuickCategoryButton(catGrid, "📰 Haber", ChannelCategory.HABER);
        addQuickCategoryButton(catGrid, "⚽ Spor", ChannelCategory.SPOR);
        addQuickCategoryButton(catGrid, "🎬 Sinema", ChannelCategory.SINEMA);
        addQuickCategoryButton(catGrid, "🌍 Belgesel", ChannelCategory.BELGESEL);
        addQuickCategoryButton(catGrid, "🎵 Müzik", ChannelCategory.MUZIK);
        addQuickCategoryButton(catGrid, "🧸 Çocuk", ChannelCategory.COCUK);

        remoteBody.add(catGrid);
        remoteContainer.add(remoteBody, BorderLayout.CENTER);
        return remoteContainer;
    }

    private void addQuickCategoryButton(JPanel container, String label, ChannelCategory category) {
        JButton btn = createStyledButton(label, new Color(34, 38, 48), new Color(200, 210, 225));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.addActionListener(e -> {
            if (!controller.isPoweredOn()) return;
            List<Channel> filtered = controller.getChannelsByCategory(category);
            if (!filtered.isEmpty()) {
                controller.changeChannel(filtered.get(0).getNumber());
            } else {
                triggerNotification(category.getDisplayName() + " kategorisinde kanal bulunamadı.");
            }
        });
        container.add(btn);
    }

    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(20, 22, 28));
        footer.setBorder(new EmptyBorder(8, 20, 8, 20));

        JLabel info = new JLabel("İpucu: TV'yi açmak için kırmızı Güç tuşuna basınız. Sayı tuşları veya CH +/- ile kanal değiştirebilirsiniz.");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        info.setForeground(new Color(120, 130, 150));

        JLabel tech = new JLabel("Java 25 • Enterprise MVC • Observer Event System • High-DPI UI");
        tech.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tech.setForeground(new Color(85, 95, 110));

        footer.add(info, BorderLayout.WEST);
        footer.add(tech, BorderLayout.EAST);
        return footer;
    }

    private void setupTimers() {
        // Canlı Saat
        clockTimer = new Timer(1000, e -> {
            liveClockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            if (controller.isPoweredOn()) {
                tvScreenPanel.repaint();
            }
        });
        clockTimer.start();
        liveClockLabel.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));

        // OSD Otomatik Gizleme (2.5 saniye sonra)
        osdTimer = new Timer(2500, e -> {
            showVolumeOsd = false;
            tvScreenPanel.repaint();
        });
        osdTimer.setRepeats(false);

        // Bildirim Otomatik Gizleme
        notificationTimer = new Timer(3500, e -> {
            customNotification = null;
            tvScreenPanel.repaint();
        });
        notificationTimer.setRepeats(false);
    }

    private void triggerNotification(String message) {
        this.customNotification = message;
        tvScreenPanel.repaint();
        notificationTimer.restart();
    }

    private void updateDisplayState() {
        boolean on = controller.isPoweredOn();
        ledIndicator.setForeground(on ? new Color(0, 230, 118) : Color.RED);
        tvScreenPanel.repaint();
    }

    private void refreshChannelTable() {
        if (tableModel == null) return;
        tableModel.setRowCount(0);
        List<Channel> channels = controller.getAllChannels();
        for (Channel c : channels) {
            tableModel.addRow(new Object[]{
                    c.getNumber(),
                    c.getName(),
                    c.getCategory().getDisplayName(),
                    c.isHd() ? "HD" : "SD",
                    c.isFavorite() ? "⭐ Favori" : "—"
            });
        }
    }

    // ==================== TV EKRANI ÇİZİM PANELİ ====================
    private class TvScreenPanel extends JPanel {
        private int waveOffset = 0;
        private final Timer animTimer;

        public TvScreenPanel() {
            setOpaque(true);
            setBackground(Color.BLACK);

            // Ekran Canlı Animasyon Sayacı
            animTimer = new Timer(60, e -> {
                if (controller.isPoweredOn()) {
                    waveOffset = (waveOffset + 3) % 360;
                    repaint();
                }
            });
            animTimer.start();

            // Ekran üzerine tıklama ile rehberi açma/kapatma
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (controller.isPoweredOn()) {
                        showGuideOsd = !showGuideOsd;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            if (!controller.isPoweredOn()) {
                // TV KAPALI EKRANI
                paintPoweredOffScreen(g2, width, height);
            } else {
                // TV AÇIK EKRANI
                paintActiveBroadcast(g2, width, height);
                paintTopStatusBar(g2, width);
                paintChannelInfoBanner(g2, width, height);

                // OSD Katmanları
                if (showVolumeOsd) {
                    paintVolumeOsd(g2, width, height);
                }
                if (showGuideOsd) {
                    paintGuideOverlay(g2, width, height);
                }
                if (customNotification != null) {
                    paintNotificationBanner(g2, width, height);
                }
            }
            g2.dispose();
        }

        private void paintPoweredOffScreen(Graphics2D g2, int w, int h) {
            // Derin siyah parlak ekran
            GradientPaint darkGrad = new GradientPaint(0, 0, new Color(14, 16, 20), w, h, new Color(6, 7, 9));
            g2.setPaint(darkGrad);
            g2.fillRect(0, 0, w, h);

            // Hafif ekran camı yansıması (Gleam)
            g2.setColor(new Color(255, 255, 255, 8));
            Polygon gleam = new Polygon();
            gleam.addPoint(0, 0);
            gleam.addPoint(w / 3, 0);
            gleam.addPoint(0, h / 2);
            g2.fill(gleam);

            // Standby Saati ve Mesaj
            g2.setColor(new Color(75, 82, 95));
            g2.setFont(new Font("Consolas", Font.PLAIN, 28));
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(time, (w - fm.stringWidth(time)) / 2, h / 2 - 10);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            fm = g2.getFontMetrics();
            String msg = "Televizyon Kapalı (Standby)";
            g2.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2 + 30);

            g2.setColor(new Color(55, 62, 75));
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 12));
            fm = g2.getFontMetrics();
            String hint = "Açmak için kumandadaki kırmızı güç butonuna basınız.";
            g2.drawString(hint, (w - fm.stringWidth(hint)) / 2, h / 2 + 55);
        }

        private void paintActiveBroadcast(Graphics2D g2, int w, int h) {
            Channel ch = controller.getCurrentChannel().orElse(null);
            ChannelCategory cat = (ch != null) ? ch.getCategory() : ChannelCategory.GENEL;

            // Kategoriye Göre Tematik Arka Plan
            Color c1, c2;
            switch (cat) {
                case HABER -> { c1 = new Color(20, 35, 75); c2 = new Color(130, 20, 30); }
                case SPOR -> { c1 = new Color(12, 55, 28); c2 = new Color(22, 105, 52); }
                case SINEMA -> { c1 = new Color(30, 15, 50); c2 = new Color(15, 20, 35); }
                case BELGESEL -> { c1 = new Color(18, 52, 68); c2 = new Color(110, 75, 25); }
                case MUZIK -> { c1 = new Color(80, 20, 85); c2 = new Color(25, 45, 115); }
                case COCUK -> { c1 = new Color(25, 95, 125); c2 = new Color(195, 95, 25); }
                default -> { c1 = new Color(25, 30, 42); c2 = new Color(45, 52, 70); }
            }

            GradientPaint bg = new GradientPaint(0, 0, c1, w, h, c2);
            g2.setPaint(bg);
            g2.fillRect(0, 0, w, h);

            // Dinamik Yayın Görseli / Dalgalar
            paintBroadcastVisualizer(g2, w, h, cat);
        }

        private void paintBroadcastVisualizer(Graphics2D g2, int w, int h, ChannelCategory cat) {
            g2.setColor(new Color(255, 255, 255, 18));
            for (int i = 0; i < 5; i++) {
                int yOffset = h / 2 + (int) (Math.sin(Math.toRadians(waveOffset + i * 45)) * 30);
                g2.fillOval(w / 4 + i * 80, yOffset - 40, 140, 80);
            }

            // Kategori Tematik Yayın Ticker / Başlık
            g2.setColor(new Color(0, 0, 0, 120));
            g2.fillRect(0, h - 85, w, 40);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String tickerText = switch (cat) {
                case HABER -> "🔴 CANLI | TÜRKİYE VE DÜNYA GÜNDEMİNDE ÖNE ÇIKAN GELİŞMELER • PİYASALAR • HAVA DURUMU";
                case SPOR -> "⚽ CANLI MAÇ YAYINI | SÜPER LİG ÖZETLERİ • ŞAMPİYONLAR LİGİ HEYECANI DEVAM EDİYOR";
                case SINEMA -> "🎬 YILDIZLAR SİNEMASI | 'DERİN UZAY (DEEP SPACE)' 4K ULTRA HD / DOLBY VISION";
                case BELGESEL -> "🌍 VAHŞİ DOĞA | BÜYÜK OKYANUSUN SIRLARI - 1. BÖLÜM: MERCAN RESİFLERİ";
                case MUZIK -> "🎵 TOP 20 HİT LİSTESİ | EN ÇOK DİNLENEN YERLİ & YABANCI ŞARKILAR KESİNTİSİZ YAYINDA";
                case COCUK -> "🧸 ÇİZGİ DÜNYA | SEVİMLİ KAHRAMANLAR ORMAN MACERASI YENİ BÖLÜM";
                default -> "📺 GENEL YAYIN AKIŞI | YÜKSEK ÇÖZÜNÜRLÜKLÜ DİJİTAL TELEVİZYON YAYINI";
            };
            g2.drawString(tickerText, 25, h - 60);
        }

        private void paintTopStatusBar(Graphics2D g2, int w) {
            // Üst Karartma Bandı
            GradientPaint topFade = new GradientPaint(0, 0, new Color(0, 0, 0, 180), 0, 45, new Color(0, 0, 0, 0));
            g2.setPaint(topFade);
            g2.fillRect(0, 0, w, 45);

            // Sağ Üst Durum İkonları
            g2.setColor(new Color(220, 225, 235));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            String info = "📶 Wi-Fi 5G  |  HDMI 1 (4K 60Hz)  |  DOLBY ATMOS  |  " + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(info, w - fm.stringWidth(info) - 20, 26);
        }

        private void paintChannelInfoBanner(Graphics2D g2, int w, int h) {
            Channel ch = controller.getCurrentChannel().orElse(null);
            if (ch == null) return;

            // Sol Alt Kanal Rozeti (Cam / Glassmorphism efekti)
            int bx = 20;
            int by = 35;
            int bw = 320;
            int bh = 75;

            g2.setColor(new Color(15, 18, 25, 190));
            g2.fillRoundRect(bx, by, bw, bh, 18, 18);
            g2.setColor(new Color(255, 255, 255, 45));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(bx, by, bw, bh, 18, 18);

            // Kanal No (Büyük Sarı/Mavi Vurgulu)
            g2.setColor(new Color(0, 215, 255));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 36));
            String numStr = String.format("%02d", ch.getNumber());
            g2.drawString(numStr, bx + 18, by + 50);

            // Kanal Adı
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            g2.drawString(ch.getName(), bx + 80, by + 35);

            // Kategori ve HD Rozeti
            g2.setColor(new Color(180, 190, 205));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            String sub = ch.getCategory().toString() + (ch.isHd() ? " • [4K HD]" : "") + (ch.isFavorite() ? " • ⭐ Favori" : "");
            g2.drawString(sub, bx + 80, by + 58);
        }

        private void paintVolumeOsd(Graphics2D g2, int w, int h) {
            Television tv = controller.getTelevision();
            int curVol = tv.getVolume();
            boolean muted = tv.isMuted();

            int pw = 280;
            int ph = 55;
            int px = (w - pw) / 2;
            int py = h - 160;

            // Transparan Kapsül Arka Plan
            g2.setColor(new Color(10, 12, 16, 220));
            g2.fillRoundRect(px, py, pw, ph, 25, 25);
            g2.setColor(new Color(0, 200, 255, 90));
            g2.setStroke(new BasicStroke(1.8f));
            g2.drawRoundRect(px, py, pw, ph, 25, 25);

            // İkon
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            g2.setColor(muted ? Color.RED : Color.WHITE);
            g2.drawString(muted ? "🔇" : "🔊", px + 18, py + 36);

            // Bar veya Mute Mesajı
            if (muted) {
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.setColor(Color.RED);
                g2.drawString("SESSIZ (MUTED)", px + 65, py + 34);
            } else {
                int barX = px + 60;
                int barY = py + 22;
                int barW = 150;
                int barH = 10;

                // Boş Bar
                g2.setColor(new Color(50, 55, 70));
                g2.fillRoundRect(barX, barY, barW, barH, 6, 6);

                // Dolu Bar
                float ratio = (float) curVol / Television.MAX_VOLUME;
                int fillW = (int) (barW * ratio);
                g2.setColor(new Color(0, 215, 255));
                g2.fillRoundRect(barX, barY, fillW, barH, 6, 6);

                // Rakam
                g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
                g2.setColor(Color.WHITE);
                g2.drawString(curVol + "/" + Television.MAX_VOLUME, barX + barW + 12, barY + 10);
            }
        }

        private void paintGuideOverlay(Graphics2D g2, int w, int h) {
            int gw = Math.min(520, w - 80);
            int gh = Math.min(360, h - 120);
            int gx = (w - gw) / 2;
            int gy = (h - gh) / 2;

            g2.setColor(new Color(15, 18, 25, 240));
            g2.fillRoundRect(gx, gy, gw, gh, 20, 20);
            g2.setColor(new Color(0, 215, 255, 120));
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(gx, gy, gw, gh, 20, 20);

            g2.setColor(new Color(0, 215, 255));
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            g2.drawString("📑 SMART TV KANAL REHBERİ (EPG)", gx + 25, gy + 35);

            g2.setColor(new Color(170, 180, 200));
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g2.drawString("Kanal değiştirmek için kumandadaki numaraları kullanınız.", gx + 25, gy + 55);

            List<Channel> channels = controller.getAllChannels();
            int yPos = gy + 85;
            int count = Math.min(channels.size(), 7);
            for (int i = 0; i < count; i++) {
                Channel c = channels.get(i);
                boolean isCurrent = controller.getCurrentChannel().map(cur -> cur.getNumber() == c.getNumber()).orElse(false);

                if (isCurrent) {
                    g2.setColor(new Color(0, 215, 255, 50));
                    g2.fillRoundRect(gx + 20, yPos - 18, gw - 40, 28, 8, 8);
                    g2.setColor(new Color(0, 215, 255));
                } else {
                    g2.setColor(Color.WHITE);
                }

                g2.setFont(new Font("Segoe UI", isCurrent ? Font.BOLD : Font.PLAIN, 13));
                String row = String.format("%02d. %-18s  |  %s  %s", c.getNumber(), c.getName(), c.getCategory().getDisplayName(), c.isFavorite() ? "⭐" : "");
                g2.drawString(row, gx + 30, yPos);
                yPos += 34;
            }
        }

        private void paintNotificationBanner(Graphics2D g2, int w, int h) {
            g2.setFont(new Font("Segoe UI", Font.BOLD, 13));
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(customNotification);
            int pw = textW + 50;
            int ph = 40;
            int px = (w - pw) / 2;
            int py = 70;

            g2.setColor(new Color(25, 30, 40, 230));
            g2.fillRoundRect(px, py, pw, ph, 15, 15);
            g2.setColor(new Color(255, 195, 0));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(px, py, pw, ph, 15, 15);

            g2.setColor(Color.WHITE);
            g2.drawString(customNotification, px + 25, py + 25);
        }
    }

    // ==================== KANAL YÖNETİM DİYALOĞU ====================
    private void openChannelManagerDialog() {
        JDialog dialog = new JDialog(this, "📋 Kanal Yöneticisi & TV Ayarları", true);
        dialog.setSize(680, 520);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(24, 27, 33));
        dialog.setLayout(new BorderLayout(10, 10));

        // Tablo Modeli
        String[] columns = {"Kanal No", "Kanal Adı", "Kategori", "Format", "Favori"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        channelTable = new JTable(tableModel);
        channelTable.setBackground(new Color(30, 34, 42));
        channelTable.setForeground(Color.WHITE);
        channelTable.setSelectionBackground(new Color(0, 150, 220));
        channelTable.setSelectionForeground(Color.WHITE);
        channelTable.setRowHeight(28);
        channelTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        channelTable.getTableHeader().setBackground(new Color(40, 46, 58));
        channelTable.getTableHeader().setForeground(Color.WHITE);
        channelTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        channelTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        channelTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        channelTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        refreshChannelTable();

        JScrollPane scrollPane = new JScrollPane(channelTable);
        scrollPane.getViewport().setBackground(new Color(30, 34, 42));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 55, 68)));

        // Üst Filtreleme Çubuğu
        JPanel topFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topFilter.setOpaque(false);
        JLabel filterLabel = new JLabel("Kategoriye Göre Filtrele:");
        filterLabel.setForeground(Color.WHITE);

        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.addItem(null); // Tümü
        for (ChannelCategory c : ChannelCategory.values()) {
            categoryFilterCombo.addItem(c);
        }
        categoryFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(value == null ? "Tüm Kategoriler" : value.toString());
                return this;
            }
        });
        categoryFilterCombo.addActionListener(e -> {
            ChannelCategory selected = (ChannelCategory) categoryFilterCombo.getSelectedItem();
            List<Channel> list = controller.getChannelsByCategory(selected);
            tableModel.setRowCount(0);
            for (Channel c : list) {
                tableModel.addRow(new Object[]{c.getNumber(), c.getName(), c.getCategory().getDisplayName(), c.isHd() ? "HD" : "SD", c.isFavorite() ? "⭐ Favori" : "—"});
            }
        });

        topFilter.add(filterLabel);
        topFilter.add(categoryFilterCombo);

        // Alt Eylem Butonları
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        actions.setOpaque(false);

        JButton btnAdd = createStyledButton("➕ Kanal Ekle", new Color(40, 130, 75), Color.WHITE);
        btnAdd.addActionListener(e -> openAddChannelDialog(dialog));

        JButton btnDelete = createStyledButton("🗑️ Kanal Sil", new Color(180, 50, 50), Color.WHITE);
        btnDelete.addActionListener(e -> {
            int selectedRow = channelTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Lütfen silmek istediğiniz kanalı seçiniz.");
                return;
            }
            int no = (int) tableModel.getValueAt(selectedRow, 0);
            controller.removeChannel(no);
            refreshChannelTable();
        });

        JButton btnFav = createStyledButton("⭐ Favori Aç/Kapat", new Color(210, 150, 20), Color.WHITE);
        btnFav.addActionListener(e -> {
            int selectedRow = channelTable.getSelectedRow();
            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(dialog, "Lütfen işlem yapmak istediğiniz kanalı seçiniz.");
                return;
            }
            int no = (int) tableModel.getValueAt(selectedRow, 0);
            controller.toggleFavorite(no);
            refreshChannelTable();
        });

        JButton btnSelect = createStyledButton("📺 Bu Kanalı Aç", new Color(0, 140, 220), Color.WHITE);
        btnSelect.addActionListener(e -> {
            int selectedRow = channelTable.getSelectedRow();
            if (selectedRow >= 0) {
                int no = (int) tableModel.getValueAt(selectedRow, 0);
                if (!controller.isPoweredOn()) {
                    controller.powerOn();
                }
                controller.changeChannel(no);
                dialog.dispose();
            }
        });

        actions.add(btnAdd);
        actions.add(btnDelete);
        actions.add(btnFav);
        actions.add(btnSelect);

        dialog.add(topFilter, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(actions, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void openAddChannelDialog(JDialog parent) {
        JDialog addDialog = new JDialog(parent, "Yeni Kanal Ekle", true);
        addDialog.setSize(380, 300);
        addDialog.setLocationRelativeTo(parent);
        addDialog.getContentPane().setBackground(new Color(28, 32, 40));
        addDialog.setLayout(new GridLayout(5, 2, 10, 10));

        JLabel lblNo = new JLabel("  Kanal Numarası:");
        lblNo.setForeground(Color.WHITE);
        JTextField txtNo = new JTextField();

        JLabel lblName = new JLabel("  Kanal Adı:");
        lblName.setForeground(Color.WHITE);
        JTextField txtName = new JTextField();

        JLabel lblCat = new JLabel("  Kategori:");
        lblCat.setForeground(Color.WHITE);
        JComboBox<ChannelCategory> comboCat = new JComboBox<>(ChannelCategory.values());

        JLabel lblHd = new JLabel("  Yayın Formatı:");
        lblHd.setForeground(Color.WHITE);
        JCheckBox chkHd = new JCheckBox("HD Yayın", true);
        chkHd.setOpaque(false);
        chkHd.setForeground(Color.WHITE);

        JButton btnSave = createStyledButton("Kaydet", new Color(40, 130, 75), Color.WHITE);
        btnSave.addActionListener(e -> {
            try {
                int no = Integer.parseInt(txtNo.getText().trim());
                String name = txtName.getText().trim();
                ChannelCategory cat = (ChannelCategory) comboCat.getSelectedItem();
                boolean hd = chkHd.isSelected();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, "Kanal adı boş bırakılamaz!");
                    return;
                }
                controller.addChannel(no, name, cat, hd);
                refreshChannelTable();
                addDialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, "Geçersiz kanal numarası girdiniz!");
            }
        });

        JButton btnCancel = createStyledButton("İptal", new Color(70, 75, 88), Color.WHITE);
        btnCancel.addActionListener(e -> addDialog.dispose());

        addDialog.add(lblNo);
        addDialog.add(txtNo);
        addDialog.add(lblName);
        addDialog.add(txtName);
        addDialog.add(lblCat);
        addDialog.add(comboCat);
        addDialog.add(lblHd);
        addDialog.add(chkHd);
        addDialog.add(btnSave);
        addDialog.add(btnCancel);
        addDialog.setVisible(true);
    }

    // ==================== TV EVENT DINLEYICILERI ====================
    @Override
    public void onPowerChanged(Television tv, boolean isOn) {
        SwingUtilities.invokeLater(() -> {
            updateDisplayState();
            triggerNotification(isOn ? "Televizyon Açıldı." : "Televizyon Kapatıldı.");
        });
    }

    @Override
    public void onVolumeChanged(Television tv, int volume, boolean isMuted) {
        SwingUtilities.invokeLater(() -> {
            showVolumeOsd = true;
            tvScreenPanel.repaint();
            osdTimer.restart();
        });
    }

    @Override
    public void onChannelChanged(Television tv, Channel channel) {
        SwingUtilities.invokeLater(() -> {
            tvScreenPanel.repaint();
            triggerNotification(channel.getNumber() + ". " + channel.getName());
        });
    }

    @Override
    public void onMuteToggled(Television tv, boolean isMuted) {
        SwingUtilities.invokeLater(() -> {
            showVolumeOsd = true;
            tvScreenPanel.repaint();
            osdTimer.restart();
        });
    }

    @Override
    public void onChannelListUpdated() {
        SwingUtilities.invokeLater(() -> {
            refreshChannelTable();
            tvScreenPanel.repaint();
        });
    }

    @Override
    public void onError(String errorMessage) {
        SwingUtilities.invokeLater(() -> triggerNotification("⚠️ " + errorMessage));
    }

    // ==================== YARDIMCI GÖRSEL BİLEŞENLER ====================
    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createRoundButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isArmed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
