package src.p2p;

import src.gui.ChatWindow;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class PeerThread extends Thread {
    private BufferedReader bufferedReader;
    private ChatWindow chatWindow;
    private JSONObject[] largeFileMessages;

    public PeerThread(Socket socket, ChatWindow chatWindow) throws IOException {
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.chatWindow = chatWindow;
        this.largeFileMessages = new JSONObject[1024 * 100];
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
                            // small files
                            int numberOfMessages = Integer.parseInt(object.get("numberOfMessages").toString());
                            if (numberOfMessages == 1) {
                                System.out.println(object.toMap().toString());
                                String port = object.get("port").toString();
                                String fileName = object.get("fileName").toString();
                                String fileStream = object.get("fileStream").toString();

                                byte[] byteArray = Base64.getDecoder().decode(fileStream);
                                for (byte b : byteArray) {
                                    // System.out.println((char) b);
                                }
                                saveFile(port, fileName, byteArray);
                            }
                            // large files
                            else {
                                System.out.println(object.toMap().toString());
                                int index = Integer.parseInt(object.get("index").toString());
                                largeFileMessages[index] = object;

                                if (numberOfMessages == index + 1) {
                                    String port = object.get("port").toString();
                                    String fileName = object.get("fileName").toString();

                                    String fileStream = "";

                                    for (int i = 0; i < numberOfMessages; i++) {
                                        JSONObject tmpObject = largeFileMessages[i];
                                        fileStream += tmpObject.get("fileStream").toString();
                                    }

                                    byte[] byteArray = Base64.getDecoder().decode(fileStream);
                                    for (byte b : byteArray) {
                                        // System.out.println((char) b);
                                    }

                                    saveFile(port, fileName, byteArray);
                                }
                            }
                        }
                    }
                    // text message
                    else {
                        if (object.has("port")) {
                            System.out.println(object.toMap().toString());
                            chatWindow.getChatArea().append("from port " + object.get("port").toString() + ":\n" +
                                    object.get("message").toString() + "\n");
                        }
                    }
                }
             }
            catch (Exception e) {
                flag = false;
                System.out.println("src.p2p.PeerThread exception");
                e.printStackTrace();
                interrupt();
            }
        }
    }

    public void saveFile(String port, String fileName, byte[] byteArray) throws IOException {
        String home = System.getProperty("user.home");
        FileOutputStream fileOutputStream = new FileOutputStream(home + "\\Downloads\\" + fileName);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        bufferedOutputStream.write(byteArray, 0, byteArray.length);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();

        chatWindow.getChatArea().append("from port " + port + ":\nfile " + fileName + " downloaded to "
                + home + "\\Downloads\\" + fileName + "\n");
    }

}
