import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.Base64;
import java.util.List;

public class GUI {
    private final JFrame loginFrame, connectFrame, chatFrame;
    private JTextArea chatArea, fileTextArea;

    public GUI() {
        loginFrame = new JFrame();
        loginFrame.setSize(400, 400);
        loginFrame.setLayout(null);
        loginFrame.setTitle("Log in");
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.getContentPane().setBackground(Color.DARK_GRAY);

        connectFrame = new JFrame();
        connectFrame.setSize(400, 400);
        connectFrame.setLayout(null);
        connectFrame.setTitle("Connect with...");
        connectFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        connectFrame.getContentPane().setBackground(Color.DARK_GRAY);

        chatFrame = new JFrame();
        chatFrame.setSize(500, 600);
        chatFrame.setLayout(null);
        chatFrame.setTitle("Security of Computer Systems");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.getContentPane().setBackground(Color.DARK_GRAY);
    }

    public void loginLayout(BufferedReader bufferedReader) {
        JLabel lPort, lPassword;
        JTextField tfPort;
        JPasswordField pfPassword;
        JButton bLogin;
        GUI gui = this;

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

        pfPassword = new JPasswordField();
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
            try {
                serverThread = new ServerThread(port);
                serverThread.start();
                new App().updateListenToPeers(bufferedReader, port, serverThread, gui);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
            loginFrame.setVisible(false);
        });
        loginFrame.add(bLogin);

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
        lHostname.setForeground(Color.WHITE);
        connectFrame.add(lHostname);

        tfHostname = new JTextField("localhost");
        tfHostname.setBounds(200, 75, 150, 30);
        connectFrame.add(tfHostname);

        lPort = new JLabel("Port:");
        lPort.setBounds(50, 125, 150, 30);
        lPort.setForeground(Color.WHITE);
        connectFrame.add(lPort);

        tfPort = new JTextField("1234");
        tfPort.setBounds(200, 125, 150, 30);
        connectFrame.add(tfPort);

        lMode = new JLabel("Encryption mode:");
        lMode.setBounds(50, 175, 150, 30);
        lMode.setForeground(Color.WHITE);
        connectFrame.add(lMode);

        String[] modes = {"ECB", "CBC"};
        cbMode = new JComboBox(modes);
        cbMode.setBounds(200, 175, 150, 30);
        connectFrame.add(cbMode);

        bConnect = new JButton("Connect");
        bConnect.setBounds(150, 275, 100, 30);
        bConnect.addActionListener(e -> {
            String hostname = tfHostname.getText();
            String port1 = tfPort.getText();
            String mode = cbMode.getItemAt(cbMode.getSelectedIndex()).toString();

            Socket socket = null;
            try {
                socket = new Socket(hostname, Integer.parseInt(port1));
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
            app.communicate(bufferedReader, port1, serverThread, gui);

            connectFrame.setVisible(false);
        });
        connectFrame.add(bConnect);

        connectFrame.setTitle("Connect");
        connectFrame.setVisible(true);
    }

    public void chatLayout(String port, ServerThread serverThread) {
        JLabel lSendFile, lSendMessage;
        JTextArea taText;
        JButton bSendMessage, bSendFile, bFile;

        chatArea = new JTextArea();
        chatArea.setBounds(10, 10, 470, 470);
        chatArea.setEditable(false);
        chatFrame.add(chatArea);

        lSendFile = new JLabel("Send file: ");
        lSendFile.setBounds(10, 490, 100, 30);
        chatFrame.add(lSendFile);

        fileTextArea = new JTextArea();
        fileTextArea.setBounds(220, 490, 180, 30);
        fileTextArea.setEditable(false);
        chatFrame.add(fileTextArea);

        bFile = new JButton("Choose file");
        bFile.setBounds(110, 490, 100, 30);
        bFile.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int i = fc.showOpenDialog(chatFrame);
            if (i == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                Long fileSize = file.length(); // size in bytes

                byte[] byteArray = new byte[(int)file.length()];
                FileInputStream fis;
                BufferedInputStream bis;
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
                        .put("fileSize", fileSize)
                        .put("index", 0)
                        .put("numberOfMessages", 1)
                        .toString();

                fileTextArea.append(jsonString);
            }
        });
        chatFrame.add(bFile);

        bSendFile = new JButton("Send");
        bSendFile.setBounds(410, 490, 70, 30);
        bSendFile.addActionListener(e -> {
            String jsonString = fileTextArea.getText();
            fileTextArea.setText("");

            JSONObject object = new JSONObject(jsonString);
            if(object.has("isFile")) {
                Long fileSize = Long.parseLong(object.get("fileSize").toString());
                String fileName = object.get("fileName").toString();
                if(fileSize < 50 * 1024) { // less than 50KB
                    serverThread.sendMessage(jsonString);
                }
                else {
                    // split fileStream
                    String port1 = object.get("port").toString();
                    String fileStream = object.get("fileStream").toString();
                    int numberOfMessages = fileStream.length() / (50 * 1024) + 1;

                    String[] stream = fileStream.split("(?<=\\G.{" + 50 * 1024 + "})");

                    ProgressWindow progressWindow = new ProgressWindow();

                    SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
                        @Override
                        protected Void doInBackground() throws Exception {
                            Integer progress = 0;
                            Double tmp = 0.0;
                            setProgress(0);

                            progressWindow.getProgressBar().setMaximum(100);

                            for (int i = 0; i < numberOfMessages; i++) {
                                String newJsonString = new JSONObject()
                                        .put("isFile", true)
                                        .put("port", port1)
                                        .put("fileName", fileName)
                                        .put("fileStream", stream[i])
                                        .put("fileSize", fileSize)
                                        .put("index", i)
                                        .put("numberOfMessages", numberOfMessages)
                                        .toString();
                                serverThread.sendMessage(newJsonString);

                                tmp = Double.valueOf(i + 1) / Double.valueOf(numberOfMessages) * 100.0;

                                progress = tmp.intValue();
                                setProgress(progress);
                                publish(progress);
                            }
                            return null;
                        }

                        @Override
                        protected void process(List<Integer> chunks) {
                            for (Integer p : chunks) {
                                progressWindow.getProgressBar().setValue(p);
                            }
                        }
                    };
                    worker.execute();
                }
                chatArea.append("Me:\n" + fileName + "\n");
            }
        });
        chatFrame.add(bSendFile);

        lSendMessage = new JLabel("Send message: ");
        lSendMessage.setBounds(10, 530, 100, 30);
        chatFrame.add(lSendMessage);

        taText = new JTextArea();
        taText.setBounds(110, 530, 290, 30);
        chatFrame.add(taText);

        bSendMessage = new JButton("Send");
        bSendMessage.setBounds(410, 530, 70, 30);
        bSendMessage.addActionListener(e -> {
            String message = taText.getText();

            taText.setText("");
            chatArea.append("Me:\n" + message + "\n");

            String jsonString = new JSONObject()
                    .put("isFile", false)
                    .put("port", port)
                    .put("message", message)
                    .toString();

            serverThread.sendMessage(jsonString);
        });
        chatFrame.add(bSendMessage);

        chatFrame.setVisible(true);
    }

    public JTextArea getChatArea() {
        return this.chatArea;
    }
}
