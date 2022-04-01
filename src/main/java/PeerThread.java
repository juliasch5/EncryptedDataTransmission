import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader;

    public PeerThread(Socket socket) throws IOException {
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        boolean flag = true;
        while (flag) {
            try {
                JSONTokener tokener = new JSONTokener(bufferedReader);
                JSONObject object = new JSONObject(tokener);
                if (object.has("port")) {
                    System.out.println(object.toMap().toString());
                }
             }
            catch (Exception e) {
                flag = false;
                System.out.println("PeerThread exception");
                interrupt();
            }
        }
    }
}
