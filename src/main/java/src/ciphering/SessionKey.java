package src.ciphering;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class SessionKey {
    private SecretKeySpec sessionKey;
    private static final String CIPHER = "AES";

    public SessionKey() {

    }

    public SessionKey(SecretKeySpec sessionKey) {
        this.sessionKey = sessionKey;
    }

    public SessionKey(byte sessionKey[]) {
        this.sessionKey = new SecretKeySpec(sessionKey, CIPHER);
    }

    public SecretKeySpec generate() {
        byte[] key = new byte[32];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);
        sessionKey = new SecretKeySpec(key, CIPHER);
        return sessionKey;
    }

    public SecretKeySpec getSessionKey() {
        return sessionKey;
    }

    public String getString() {
        return new String(sessionKey.getEncoded());
    }

    public byte[] getByteArray() {
        return this.sessionKey.getEncoded();
    }

    public byte[] encrypt(PublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte ciphered[] =  cipher.doFinal(this.getByteArray());
        return ciphered;
    }

    public byte[] decrypt(PrivateKey key, String encrypted) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte decryped[] = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return decryped;
    }
}
