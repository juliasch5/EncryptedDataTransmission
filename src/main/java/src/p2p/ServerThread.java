package src.p2p;

import org.json.JSONObject;
import src.user.User;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

public class ServerThread extends Thread {
    private ServerSocket serverSocket;
    private Set<ServerThreadThread> serverThreadThreads = new HashSet<ServerThreadThread>();
    private User user;

    public ServerThread(User user) throws IOException {
        this.user = user;
        serverSocket = new ServerSocket(Integer.valueOf(user.getPort()));
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

    public void sendMessage(String message) {
        try {
            serverThreadThreads.forEach(t->t.getPrintWriter().println(message));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void exchangePublicKeys(String port) {
        String jsonString = new JSONObject()
                .put("isKey", true)
                .put("port", port)
                .put("key", new String(user.getPublicKey().getEncoded()))
                .toString();
        try {
            serverThreadThreads.forEach(t->t.getPrintWriter().println(jsonString));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<ServerThreadThread> getServerThreadThreads() {
        return serverThreadThreads;
    }
}
