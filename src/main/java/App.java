import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException {
        GUI gui = new GUI();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        gui.loginLayout(bufferedReader);
    }

    public void updateListenToPeers(BufferedReader bufferedReader, String port, ServerThread serverThread, GUI gui) throws IOException {
        gui.connectLayout(bufferedReader, port, serverThread, this);
    }

    public void communicate(BufferedReader bufferedReader, String port, ServerThread serverThread, GUI gui) {
        gui.chatLayout(port, serverThread);
    }
}
