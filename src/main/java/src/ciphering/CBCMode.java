package src.ciphering;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CBCMode {

    //Default block size
    public static int blockSize = 16;

    Cipher encryptCipher = null;
    Cipher decryptCipher = null;

    // Buffer used to transport the bytes from one stream to another
    byte[] buf = new byte[blockSize];       //input buffer
    byte[] obuf = new byte[512];            //output buffer

    // The key
    byte[] key = null;
    // The initialization vector needed by the CBC mode
    byte[] IV = null;

    public CBCMode() {
        //for a 192 key you must install the unrestricted policy files
        //  from the JCE/JDK downloads page
        key = "SECRET_1SECRET_2".getBytes();
        //default IV value initialized with 0
        IV = new byte[blockSize];
    }

    public CBCMode(String pass, byte[] iv){
        //get the key and the IV
        key = pass.getBytes();
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }

    public CBCMode(byte[] pass) {
        //get the key and the IV
        key = new byte[pass.length];
        System.arraycopy(pass, 0 , key, 0, pass.length);
        IV = new byte[blockSize];
    }

    public CBCMode(byte[] pass, byte[]iv){
        //get the key and the IV
        key = new byte[pass.length];
        System.arraycopy(pass, 0 , key, 0, pass.length);
        IV = new byte[blockSize];
        System.arraycopy(iv, 0 , IV, 0, iv.length);
    }

    public void InitCiphers() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException,
            InvalidKeyException, InvalidAlgorithmParameterException {
        //1. create the cipher
        encryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //2. create the key
        SecretKey keyValue = new SecretKeySpec(key,"AES");
        //3. create the IV
        AlgorithmParameterSpec IVspec = new IvParameterSpec(IV);
        //4. init the cipher
        encryptCipher.init(Cipher.ENCRYPT_MODE, keyValue, IVspec);

        //1 create the cipher
        decryptCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //2. the key is already created
        //3. the IV is already created
        //4. init the cipher
        decryptCipher.init(Cipher.DECRYPT_MODE, keyValue, IVspec);
    }

    public void ResetCiphers(){
        encryptCipher=null;
        decryptCipher=null;
    }

    public byte[] CBCEncrypt(String value) throws IOException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        InitCiphers();
        //optionally put the IV at the beggining of the cipher file
        //fos.write(IV, 0, IV.length);

//        byte[] buffer = new byte[blockSize];
//        int noBytes = 0;
//        byte[] cipherBlock = new byte[encryptCipher.getOutputSize(buffer.length)];
//        int cipherBytes;
//        while((noBytes = fis.read(buffer)) != -1) {
//            cipherBytes = encryptCipher.update(buffer, 0, noBytes, cipherBlock);
//            fos.write(cipherBlock, 0, cipherBytes);
//        }
        //always call doFinal
        //byte byteValue[] = Base64.getDecoder().decode(value.getBytes());
        byte[] cipherBytes = encryptCipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        return cipherBytes;
//        fos.write(cipherBlock,0,cipherBytes);
//
//        //close the files
//        fos.close();
//        fis.close();
    }

    public byte[] CBCEncrypt(byte[] value) throws IOException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        InitCiphers();
        byte[] cipherBytes = encryptCipher.doFinal(value);
        return cipherBytes;
    }
    public byte[] CBCDecrypt(String value) throws IOException, ShortBufferException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        InitCiphers();
        // get the IV from the file
        // DO NOT FORGET TO reinit the cipher with the IV
        //fis.read(IV,0,IV.length);
        //this.InitCiphers();

//        byte[] buffer = new byte[blockSize];
//        int noBytes = 0;
//        byte[] cipherBlock =
//            new byte[decryptCipher.getOutputSize(buffer.length)];
//        int cipherBytes;
//        while((noBytes = fis.read(buffer))!=-1)
//        {
//        cipherBytes =
//                decryptCipher.update(buffer, 0, noBytes, cipherBlock);
//        fos.write(cipherBlock, 0, cipherBytes);
//        }
        //allways call doFinal
        byte decodedValue[] = decryptCipher.doFinal(Base64.getDecoder().decode(value));
        return decodedValue;
//        fos.write(cipherBlock,0,cipherBytes);
//
//        //close the files
//        fos.close();
//        fis.close();
    }
}