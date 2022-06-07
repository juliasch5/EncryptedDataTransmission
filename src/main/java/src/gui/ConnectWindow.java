package src.gui;

import src.App;
import src.p2p.PeerThread;
import src.p2p.ServerThread;
import src.user.User;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ConnectWindow {
    private final JFrame connectFrame;
    private ChatWindow chat;

    public ConnectWindow() {
        connectFrame = new JFrame();
        connectFrame.setSize(400, 400);
        connectFrame.setLayout(null);
        connectFrame.setTitle("Connect with...");
        connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectFrame.getContentPane().setBackground(Color.DARK_GRAY);

        chat = new ChatWindow();
    }

    public void connectLayout(BufferedReader bufferedReader, User user, ServerThread serverThread, App app) {
        JLabel lHostname, lPort, lMode;
        JTextField tfHostname, tfPort;
        JComboBox cbMode;
        JButton bConnect;

        lHostname = new JLabel("Hostname:");
        lHostname.setBounds(50, 75, 150, 30);
        lHostname.setForeground(Color.WHITE);
        connectFrame.add(lHostname);

        tfHostname = new JTextField("localhost");
        tfHostname.setBounds(200, 75, 150, 30);
        tfHostname.setBackground(Color.LIGHT_GRAY);
        connectFrame.add(tfHostname);

        lPort = new JLabel("Port:");
        lPort.setBounds(50, 125, 150, 30);
        lPort.setForeground(Color.WHITE);
        connectFrame.add(lPort);

        tfPort = new JTextField("1234");
        tfPort.setBounds(200, 125, 150, 30);
        tfPort.setBackground(Color.LIGHT_GRAY);
        connectFrame.add(tfPort);

        lMode = new JLabel("Encryption mode:");
        lMode.setBounds(50, 175, 150, 30);
        lMode.setForeground(Color.WHITE);
        connectFrame.add(lMode);

        String[] modes = {"ECB", "CBC"};
        cbMode = new JComboBox(modes);
        cbMode.setBounds(200, 175, 150, 30);
        cbMode.setBackground(Color.LIGHT_GRAY);
        connectFrame.add(cbMode);

        bConnect = new JButton("Connect");
        bConnect.setBounds(150, 275, 100, 30);
        bConnect.setBackground(Color.LIGHT_GRAY);
        bConnect.addActionListener(e -> {
            String hostname = tfHostname.getText();
            String port1 = tfPort.getText();
            String mode = cbMode.getItemAt(cbMode.getSelectedIndex()).toString();

            user.setEncryptionMode(mode);

            Socket socket = null;
            try {
                socket = new Socket(hostname, Integer.parseInt(port1));
                new PeerThread(socket, chat, user).start();
            }
            catch (Exception exception) {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                else System.out.println("invalid input");
            }
            app.communicate(bufferedReader, port1, serverThread, chat);

            connectFrame.setVisible(false);
        });
        connectFrame.add(bConnect);

        connectFrame.setTitle("Connect");
        connectFrame.setVisible(true);
    }
}
