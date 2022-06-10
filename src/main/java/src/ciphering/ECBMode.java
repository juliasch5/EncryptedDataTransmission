package src.ciphering;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class ECBMode {

    public byte[] encrypt(byte[] input, byte[] pass) {
        byte[] crypted = null;
        try {
            byte[] key = new byte[pass.length];
            System.arraycopy(pass, 0 , key, 0, pass.length);

            SecretKeySpec skey = new SecretKeySpec(key, "AES");

            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            crypted = cipher.doFinal(input);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return crypted;
    }

    public byte[] decrypt(String input, byte[] pass) {
        byte[] output = null;
        try {
            byte[] key = new byte[pass.length];
            System.arraycopy(pass, 0 , key, 0, pass.length);
            SecretKeySpec skey = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skey);
            output = cipher.doFinal(Base64.getDecoder().decode(input));
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return output;
    }

}