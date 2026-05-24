package jasypt;


// Jasypt'in şifreleme ve deşifre etme işlemlerini yürüten ana motor sınıfı import edilir
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.iv.RandomIvGenerator;

public class TestJasypt { // Jasypt Daha Çok  password-based symmetric encryption

    public static void main(String[] args) {
        // 1. Şifreleme motorundan yeni bir nesne (instance) oluşturuluyor
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        // buna secret key denir. Şifreleme ve deşifre işlemlerinde aynı secret key kullanılmalıdır, aksi takdirde deşifre işlemi başarısız olur.
        encryptor.setPassword("mySecretKey");
        encryptor.setAlgorithm("PBEWithHMACSHA512AndAES_256");
        // HMAC-SHA512 => Hash-based Message Authentication Code (HMAC) algoritması, SHA-512 hash fonksiyonunu kullanarak mesaj doğrulama kodu oluşturur.
        // AES-256 => Advanced Encryption Standard (AES) algoritması,  verinin şifrelenmesi için kullanılan bir blok şifreleme algoritmasıdır. AES-256, 256 bit anahtar uzunluğuna sahip bir şifreleme yöntemidir.
        encryptor.setIvGenerator(new RandomIvGenerator());
        // IV (Initialization Vector) => Şifreleme algoritmalarında kullanılan bir başlangıç vektörüdür. IV, şifreleme işlemi sırasında kullanılan rastgele bir değerdir ve her şifreleme işlemi için farklı olmalıdır. IV, şifreleme algoritmasının güvenliğini artırmak için kullanılır.

         String originalText = "TOKEN";
            // Şifreleme işlemi gerçekleştirilir
        String encryptedText = encryptor.encrypt(originalText);
        System.out.println("Encrypted Text: " + encryptedText);
        System.out.println("Çözülmüş Orijinal Metin: " + encryptor.decrypt(encryptedText));
    }
}
