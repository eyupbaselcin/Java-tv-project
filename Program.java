import com.javatv.Main;
import java.util.Scanner;

/**
 * Program Sınıfı - Uygulama Başlatıcısı.
 * Kullanıcıya modern Görsel Arayüz (GUI) veya Konsol menüsü arasında seçim
 * yapma imkanı sunar.
 */
public class Program {
    private static final Scanner scanner = new Scanner(System.in);
    private static Tv tv;

    public static void main(String[] args) {
        System.out.println("======================================================");
        System.out.println("         📺 SMART TELEVİZYON PRO SİSTEMİ              ");
        System.out.println("======================================================");
        System.out.println("Lütfen çalıştırma modunu seçiniz:");
        System.out.println("  1- 🌟 Modern Görsel Arayüz (Smart TV GUI)");
        System.out.println("  2- 💻 Gelişmiş Konsol Kumanda Menüsü (CLI)");
        System.out.print("Seçiminiz (1 veya 2, Varsayılan: 1): ");

        String choice = scanner.nextLine().trim();
        if (choice.equals("2")) {
            // Konsol Modunu Başlat
            Main.main(new String[] { "--cli" });
        } else {
            // Varsayılan: Modern GUI'yi Başlat
            System.out.println("\n[+] Smart TV Görsel Arayüzü (GUI) başlatılıyor...");
            Main.main(new String[] {});
        }
    }
}
