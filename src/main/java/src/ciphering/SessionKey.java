package src.ciphering;

import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class SessionKey {
    private SecretKeySpec sessionKey;
    private static final String CIPHER = "AES";

    public SessionKey() {

    }

    public SessionKey(SecretKeySpec sessionKey) {
        this.sessionKey = sessionKey;
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
}
