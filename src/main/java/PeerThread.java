import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader;
    private GUI gui;

    public PeerThread(Socket socket, GUI gui) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.gui = gui;
    }

    public void run() {
        boolean flag = true;
        while (flag) {
            try {
                JSONTokener tokener = new JSONTokener(bufferedReader);
                JSONObject object = new JSONObject(tokener);
                if (object.has("isFile")) {
                    if (object.get("isFile").equals(true)) {
                        if (object.has("port")) {
                            String port = object.get("port").toString();
                            String fileName = object.get("fileName").toString();
                            String fileStream = object.get("fileStream").toString();

                            gui.getChatArea().append("from port " + port + ":\nfile: " + fileName + "\n");

                            byte[] byteArray = Base64.getDecoder().decode(fileStream);
                            for (byte b : byteArray) {
                                System.out.println((char) b);
                            }
                            saveFile(fileName, byteArray);
                        }
                    }
                    else {
                        if (object.has("port")) {
                            System.out.println(object.toMap().toString());
                            gui.getChatArea().append("from port " + object.get("port").toString() + ":\n" +
                                    object.get("message").toString() + "\n");
                        }
                    }
                }
             }
            catch (Exception e) {
                flag = false;
                System.out.println("PeerThread exception");
                interrupt();
            }
        }
    }

    public void saveFile(String fileName, byte[] byteArray) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\julia\\Downloads\\" + fileName);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(byteArray, 0, byteArray.length);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }
}
