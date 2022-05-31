package src.gui;

import src.App;
import src.p2p.ServerThread;
import src.user.User;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class LoginWindow {
    private final JFrame loginFrame;

    public LoginWindow() {
        loginFrame = new JFrame();
        loginFrame.setSize(400, 400);
        loginFrame.setLayout(null);
        loginFrame.setTitle("Log in");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.getContentPane().setBackground(Color.DARK_GRAY);
    }

    public void loginLayout(BufferedReader bufferedReader) {
        JLabel lPort, lPassword;
        JTextField tfPort;
        JPasswordField pfPassword;
        JButton bLogin;

        lPort = new JLabel("Your port:");
        lPort.setBounds(50, 100, 150, 30);
        lPort.setForeground(Color.WHITE);
        loginFrame.add(lPort);

        tfPort = new JTextField("1234");
        tfPort.setBounds(200, 100, 150, 30);
        tfPort.setBackground(Color.LIGHT_GRAY);
        loginFrame.add(tfPort);

        lPassword = new JLabel("Password:");
        lPassword.setBounds(50, 150, 150, 30);
        lPassword.setForeground(Color.WHITE);
        loginFrame.add(lPassword);

        pfPassword = new JPasswordField("");
        pfPassword.setBounds(200, 150, 150, 30);
        pfPassword.setBackground(Color.LIGHT_GRAY);
        loginFrame.add(pfPassword);

        bLogin = new JButton("Log in");
        bLogin.setBounds(150, 250, 100, 30);
        bLogin.setBackground(Color.LIGHT_GRAY);
        bLogin.addActionListener(e -> {
            String port = tfPort.getText();
            String password = new String(pfPassword.getPassword());

            ServerThread serverThread;
            User user;
            try {
                user = new User(port, password);
                serverThread = new ServerThread(user);
                serverThread.start();
                new App().updateListenToPeers(bufferedReader, user, serverThread);
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            }
            loginFrame.setVisible(false);
        });
        loginFrame.add(bLogin);

        loginFrame.setVisible(true);
    }
}
