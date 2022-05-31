package src;

import src.user.User;

import src.p2p.ServerThread;
import src.gui.ChatWindow;
import src.gui.ConnectWindow;
import src.gui.LoginWindow;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException {
        LoginWindow login = new LoginWindow();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        login.loginLayout(bufferedReader);
    }

    public void updateListenToPeers(BufferedReader bufferedReader, User user, ServerThread serverThread) throws IOException {
        ConnectWindow connect = new ConnectWindow();
        connect.connectLayout(bufferedReader, user, serverThread, this);
    }

    public void communicate(BufferedReader bufferedReader, String port, ServerThread serverThread, ChatWindow chat) {
        chat.chatLayout(port, serverThread);
    }
}