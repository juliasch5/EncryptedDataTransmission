package src.user;

import src.ciphering.KeyStore;
import src.ciphering.SHA256;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class User {
    private final String port;
    private final String password;
    private final byte[] localKey;
    private final KeyPair keyPair;
    private Map<String, String> peersPublicKeys;

    public User(String port, String password) throws NoSuchAlgorithmException {
        this.port = port;
        this.password = password;
        SHA256 sha = new SHA256(password);
        this.localKey = sha.getSHA();
        this.keyPair = generateRSAKeyPair();
        KeyStore store = new KeyStore(this.port, this.keyPair, this.localKey);
        store.saveKeysToFiles();
        peersPublicKeys = new HashMap<>();
    }

    private KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();
        return pair;
    }

    public void addPeersPublicKey(String port, String key) {
        peersPublicKeys.put(port, key);
    }

    public String getPort() {
        return this.port;
    }

    public byte[] getLocalKey() {
        return this.localKey;
    }

    public PublicKey getPublicKey() {
        return this.keyPair.getPublic();
    }
}
