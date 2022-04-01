import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class App {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\tenter your port and password");
        String[] setup = bufferedReader.readLine().split(" ");
        ServerThread serverThread = new ServerThread(setup[0]);
        serverThread.start();
        new App().updateListenToPeers(bufferedReader, setup[0], serverThread);
    }

    public void updateListenToPeers(BufferedReader bufferedReader, String port, ServerThread serverThread) throws IOException {
        System.out.println("\tenter hostname and port to receive message from");
        String input = bufferedReader.readLine();
        String[] values = input.split(":");
        Socket socket = null;
        try {
            socket = new Socket(values[0], Integer.valueOf(values[1]));
            new PeerThread(socket).start();
        }
        catch (Exception e) {
            if (socket != null) socket.close();
            else System.out.println("invalid input");
        }
        communicate(bufferedReader, port, serverThread);
    }

    public void communicate(BufferedReader bufferedReader, String port, ServerThread serverThread) {
        try {
            System.out.println("\tenter text you want to send");
            boolean flag = true;
            while (flag) {
                String message = bufferedReader.readLine();
                if (message.equals("e")) {
                    flag = false;
                    break;
                }
                else if (message.equals("c")) {
                    updateListenToPeers(bufferedReader, port, serverThread);
                }
                else {
                    String jsonString = new JSONObject()
                            .put("port", port)
                            .put("message", message)
                            .toString();
                    serverThread.sendMessage(jsonString);
                }
            }
            System.exit(0);
        }
        catch (Exception e) {

        }
    }
}
