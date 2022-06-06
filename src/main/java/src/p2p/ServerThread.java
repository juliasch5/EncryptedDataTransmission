package src.p2p;

import org.json.JSONObject;
import src.ciphering.CBCMode;
import src.ciphering.SessionKey;
import src.user.User;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import java.net.ServerSocket;
import java.security.*;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private Set<ServerThreadThread> serverThreadThreads = new HashSet<ServerThreadThread>();
    private User user;
    private boolean firstMessage;

    public ServerThread(User user) throws IOException {
        this.user = user;
        serverSocket = new ServerSocket(Integer.valueOf(user.getPort()));
        firstMessage = true;
    }

    public void run() {
        try {
            while (true) {
                ServerThreadThread serverThreadThread = new ServerThreadThread(serverSocket.accept(), this);
                serverThreadThreads.add(serverThreadThread);
                serverThreadThread.start();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, String port) throws ShortBufferException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        System.out.println(user.getPeersPublicKeys());
        if (firstMessage == true) {
            exchangePublicKeys(port);
            if (!user.getPeersPublicKeys().isEmpty()) {
                sendSessionKey(port);
            }
            firstMessage = false;
        }
        try {
            serverThreadThreads.forEach(t->t.getPrintWriter().println(message));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            serverThreadThreads.forEach(t->t.getPrintWriter().println(message));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exchangePublicKeys(String port) throws ShortBufferException, IllegalBlockSizeException, IOException, BadPaddingException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        byte[] usersKey = user.getPublicKey().getEncoded();
        String encodedKey = Base64.getEncoder().withoutPadding().encodeToString(new CBCMode().CBCEncrypt(usersKey));
        String jsonString = new JSONObject()
                .put("isKey", true)
                .put("port", port)
                .put("sender", getUser().getPort())
                .put("key", encodedKey)
                .toString();
        byte arr[] = new CBCMode().CBCDecrypt(encodedKey);
        try {
            serverThreadThreads.forEach(t->t.getPrintWriter().println(jsonString));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSessionKey(String port) {
        SessionKey sessionKey = new SessionKey();
        sessionKey.generate();
        user.setSessionKey(sessionKey);
        PublicKey key = getUser().getPeersPublicKey(port);
        String keyString = null;
        try {
            keyString = Base64.getEncoder().withoutPadding().encodeToString(user.getSessionKey().encrypt(key));
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
        String jsonString = new JSONObject()
                    .put("isSessionKey", true)
                    .put("port", port)
                    .put("key", keyString)
                    .toString();

        try {
            String finalJsonString = jsonString;
            serverThreadThreads.forEach(t->t.getPrintWriter().println(finalJsonString));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<ServerThreadThread> getServerThreadThreads() {
        return serverThreadThreads;
    }

    public User getUser() {
        return this.user;
    }
}
