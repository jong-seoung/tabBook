package tabbook.server.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Component
public class EncryptUtil {

    @Value("${hash-secret}")
    private String secret;

    public String hashEmail(String email) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(email.getBytes());
            return Base64.getUrlEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("암호화 실패", e);
        }
    }

    public String decryptEmail(String encryptedEmail) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getUrlDecoder().decode(encryptedEmail);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted);
        } catch (Exception e) {
            throw new RuntimeException("복호화 실패", e);
        }
}
}
