package src.ciphering;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.*;

public class KeyStore {
    private final PublicKey publicKey;
    private final PrivateKey privateKey;
    private final byte[] hashedPassword;
    private final String port;

    public KeyStore(String port, KeyPair keys, byte[] hashedPassword) {
        this.port = port;
        this.publicKey = keys.getPublic();
        this.privateKey = keys.getPrivate();
        this.hashedPassword = hashedPassword;
    }
    public boolean saveKeysToFiles() {
        try (FileOutputStream fos = new FileOutputStream("public.key")){
            fos.write(publicKey.getEncoded());
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
        try (FileOutputStream fos = new FileOutputStream("private.key")){
            fos.write(new CBCMode(hashedPassword).CBCEncrypt(privateKey.toString()));
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return false;
        } catch (ShortBufferException e) {
            e.printStackTrace();
            return false;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return false;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return false;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
