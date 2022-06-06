package src.user;

import org.bouncycastle.crypto.util.PublicKeyFactory;
import src.ciphering.KeyStore;
import src.ciphering.SHA256;
import src.ciphering.SessionKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final String port;
    private final String password;
    private final byte[] localKey;
    private final KeyPair keyPair;
    private Map<String, byte[]> peersPublicKeys;
    private SessionKey sessionKey;

    public User(String port, String password) throws NoSuchAlgorithmException {
        this.port = port;
        this.password = password;
        SHA256 sha = new SHA256(password);
        this.localKey = sha.getSHA();
        this.keyPair = generateRSAKeyPair();
        KeyStore store = new KeyStore(this.port, this.keyPair, this.localKey);
        store.saveKeysToFiles();
        peersPublicKeys = new HashMap<>();
        sessionKey = new SessionKey();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        return pair;
    }

    public void addPeersPublicKey(String port, byte[] key) {
        peersPublicKeys.put(port, key);
    }

    public String getPort() {
        return this.port;
    }

    public byte[] getLocalKey() {
        return this.localKey;
    }

    private PrivateKey getPrivateKey() {
        return this.keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }

    public Map<String, byte[]> getPeersPublicKeys() {
        return peersPublicKeys;
    }

    public PublicKey getPeersPublicKey(String port){
        byte[] key = this.peersPublicKeys.get(port);
        try{
            byte[] byteKey = Base64.getEncoder().encode(key);
            KeySpec X509publicKey = new X509EncodedKeySpec(key);
            KeyFactory kf = KeyFactory.getInstance("RSA");

            return kf.generatePublic(X509publicKey);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public SessionKey getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        byte decrypted[] = null;
        try {
            decrypted = this.sessionKey.decrypt(getPrivateKey(), sessionKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        this.sessionKey = new SessionKey(decrypted);
    }

    public void setSessionKey(SessionKey sessionKey) {
        this.sessionKey = sessionKey;
    }
}
