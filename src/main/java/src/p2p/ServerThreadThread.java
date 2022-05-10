package src.p2p;

import java.io.*;
import java.net.Socket;

public class ServerThreadThread extends Thread {
    private ServerThread serverThread;
    private Socket socket;
    private PrintWriter printWriter;
    private OutputStream outputStream;
    private DataOutputStream dataOutputStream;

    public ServerThreadThread(Socket socket, ServerThread serverThread) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    public void run() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            this.printWriter = new PrintWriter(socket.getOutputStream(), true);
            while (true) {
                serverThread.sendMessage(bufferedReader.readLine());
            }
        }
        catch (Exception e) {
            serverThread.getServerThreadThreads().remove(this);
        }
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public DataOutputStream getDataOutputStream() {
        return dataOutputStream;
    }
}
