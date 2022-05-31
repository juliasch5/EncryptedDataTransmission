package src.ciphering;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
    private final String input;

    public SHA256(String input) {
        this.input = input;
    }

    public byte[] getSHA() throws NoSuchAlgorithmException
    {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(this.input.getBytes());
    }
}
