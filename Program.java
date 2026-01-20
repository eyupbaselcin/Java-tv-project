import java.util.Scanner;

public class Program {
    static Scanner scanner = new Scanner(System.in);
    static Tv tv;

    public static void main(String[] args) throws Exception {
        try {
        int degistir;
        boolean dongu =true;
        menuGoster();
        while(dongu){
            System.out.println("seçiminizi giriniz:");
            System.out.println("menü için 8 e basınız");
            int secim;
            secim=scanner.nextInt();
            // Eğer TV oluşturulmamışsa, 1 (kurulum), 0 (çıkış) ve 8 (menu) dışında işlem yapma
            if(secim!=0 && secim!=1 && secim!=8 && tv==null){
                System.out.println("Önce TV kurulumu yapınız.");
                continue;
            }
            switch(secim){
                case 0:
                    System.out.println("çıkış işlemi başarılı");
                    dongu=false; break;
                case 1:
                    tvolustur(); break;
                case 2:
                    tv.tvac(); break;
                case 3:
                    tv.tvKapat(); break;
                case 4:
                    tv.sesiAzalt(); break;
                case 5:
                    tv.sesiArir(); break;
                case 6:
                    System.out.println("kanal numarsını giriniz:");
                    degistir=scanner.nextInt();
                    tv.kanaliDegistir(degistir); break;
                case 7:
                    tv.kanalBilgisiniGoster(); break;
                case 8:
                    menuGoster();break;
                default: 
                    break;
                

            }
        }
    } catch (Exception e) {
        System.out.println("Hatalı giriş yaptınız, program sonlandırılıyor.");
        
    }finally {
        System.out.println("Program sonlandı.");
    }
}

    public static void menuGoster() {
        System.out.println("\n******** ANA MENÜ *********");
        System.out.println("0- Çıkış\n" +
                "1- TV kurulumu başlat\n" +
                "2- TV aç\n" +
                "3- TV kapat\n" +
                "4- Sesi azalt\n" +
                "5- Sesi artır\n" +
                "6- Kanalı değiştir\n" +
                "7- Kanal bilgisini göster\n" +
                "8- Ana menüye dön\n");
    }

    public static void tvolustur() {
        if (tv == null) {
            scanner.nextLine();
            System.out.println("TV markasını giriniz: ");
            String marka = scanner.nextLine();
            System.out.println("TV boyutunu giriniz: ");
            String boyut = scanner.nextLine();
            tv = new Tv(marka, boyut);
            System.out.println("TV oluşturuldu: " + tv);
        } else {
            System.out.println("Mevcut bir Tv bulunmaktadır\n\n");
        }
    }
}
