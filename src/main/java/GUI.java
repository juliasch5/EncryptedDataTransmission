import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class GUI {
    private final JFrame loginFrame;
    private final JFrame connectFrame;
    private final JFrame chatFrame;
    private JTextArea chatArea;

    public GUI() {
        loginFrame = new JFrame();
        loginFrame.setSize(400, 400);
        loginFrame.setLayout(null);

        connectFrame = new JFrame();
        connectFrame.setSize(400, 400);
        connectFrame.setLayout(null);

        chatFrame = new JFrame();
        chatFrame.setSize(500, 600);
        chatFrame.setLayout(null);
    }

    public void loginLayout(BufferedReader bufferedReader) {
        JLabel lPort, lPassword;
        JTextField tfPort;
        JPasswordField pfPassword;
        JButton bLogin;
        GUI gui = this;

        lPort = new JLabel("Your port:");
        lPort.setBounds(50, 100, 150, 30);
        loginFrame.add(lPort);

        tfPort = new JTextField("");
        tfPort.setBounds(200, 100, 150, 30);
        loginFrame.add(tfPort);

        lPassword = new JLabel("Password:");
        lPassword.setBounds(50, 150, 150, 30);
        loginFrame.add(lPassword);

        pfPassword = new JPasswordField();
        pfPassword.setBounds(200, 150, 150, 30);
        loginFrame.add(pfPassword);

        bLogin = new JButton("Log in");
        bLogin.setBounds(150, 250, 100, 30);
        bLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String port = tfPort.getText();
                String password = new String(pfPassword.getPassword());

                ServerThread serverThread = null;
                try {
                    serverThread = new ServerThread(port);
                    serverThread.start();
                    new App().updateListenToPeers(bufferedReader, port, serverThread, gui);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                loginFrame.setVisible(false);
            }
        });
        loginFrame.add(bLogin);

        loginFrame.setTitle("Log in");
        loginFrame.setVisible(true);
    }

    public void connectLayout(BufferedReader bufferedReader, String port, ServerThread serverThread, App app) {
        JLabel lHostname, lPort, lMode;
        JTextField tfHostname, tfPort;
        JComboBox cbMode;
        JButton bConnect;
        GUI gui = this;

        lHostname = new JLabel("Hostname:");
        lHostname.setBounds(50, 75, 150, 30);
        connectFrame.add(lHostname);

        tfHostname = new JTextField();
        tfHostname.setBounds(200, 75, 150, 30);
        connectFrame.add(tfHostname);

        lPort = new JLabel("Port:");
        lPort.setBounds(50, 125, 150, 30);
        connectFrame.add(lPort);

        tfPort = new JTextField("");
        tfPort.setBounds(200, 125, 150, 30);
        connectFrame.add(tfPort);

        lMode = new JLabel("Encryption mode:");
        lMode.setBounds(50, 175, 150, 30);
        connectFrame.add(lMode);

        String modes[] = {"ECB", "CBC"};
        cbMode = new JComboBox(modes);
        cbMode.setBounds(200, 175, 150, 30);
        connectFrame.add(cbMode);

        bConnect = new JButton("Connect");
        bConnect.setBounds(150, 275, 100, 30);
        bConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String hostname = tfHostname.getText();
                String port = tfPort.getText();
                String mode = cbMode.getItemAt(cbMode.getSelectedIndex()).toString();

                Socket socket = null;
                try {
                    socket = new Socket(hostname, Integer.valueOf(port));
                    new PeerThread(socket, gui).start();
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
                app.communicate(bufferedReader, port, serverThread, gui);

                connectFrame.setVisible(false);
            }
        });
        connectFrame.add(bConnect);

        connectFrame.setTitle("Connect");
        connectFrame.setVisible(true);
    }

    public void chatLayout(String port, ServerThread serverThread) {
        JTextArea taText;
        JButton bSend, bFile;

        chatArea = new JTextArea();
        chatArea.setBounds(10, 10, 470, 510);
        chatFrame.add(chatArea);

        bFile = new JButton("File");
        bFile.setBounds(10, 530, 30, 30);
        bFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                int i = fc.showOpenDialog(chatFrame);
                if (i == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    chatArea.append("Me:\n" + file.getName() + "\n");

                    byte[] byteArray = new byte[(int)file.length()];
                    FileInputStream fis = null;
                    BufferedInputStream bis = null;
                    try {
                        fis = new FileInputStream(file);
                        bis = new BufferedInputStream(fis);
                        bis.read(byteArray, 0, byteArray.length);
                    } catch (IOException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    }

                    String jsonString = new JSONObject()
                            .put("isFile", true)
                            .put("port", port)
                            .put("fileName", file.getName())
                            .put("fileStream", Base64.getEncoder().encodeToString(byteArray))
                            .toString();

                    serverThread.sendMessage(jsonString);
                }
            }
        });
        chatFrame.add(bFile);

        taText = new JTextArea();
        taText.setBounds(50, 530, 350, 30);
        chatFrame.add(taText);

        bSend = new JButton("Send");
        bSend.setBounds(410, 530, 70, 30);
        bSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = taText.getText();

                taText.setText("");
                chatArea.append("Me:\n" + message + "\n");

                String jsonString = new JSONObject()
                        .put("isFile", false)
                        .put("port", port)
                        .put("message", message)
                        .toString();

                serverThread.sendMessage(jsonString);
            }
        });
        chatFrame.add(bSend);

        chatFrame.setVisible(true);
    }

    public JTextArea getChatArea() {
        return this.chatArea;
    }
}
