package src.gui;

import org.json.JSONObject;
import src.ciphering.CBCMode;
import src.ciphering.ECBMode;
import src.p2p.ServerThread;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.ShortBufferException;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64;
import java.util.List;

public class ChatWindow {
    private final JFrame chatFrame;
    private JTextArea chatArea, fileTextArea;
    private JLabel lSendFile, lSendMessage;
    private JTextArea taText;
    private JButton bSendMessage, bSendFile, bFile;
    private JPanel panel;
    private JScrollPane scroll;
    private JScrollPane fileTextAreaScroll;
    private JScrollPane taScroll;
    private JButton exchangeButton;
    private ServerThread server;
    private String fileText;
    GroupLayout layout;

    public ChatWindow() {
        chatFrame = new JFrame();
        chatFrame.setSize(500, 600);
        chatFrame.setTitle("Security of Computer Systems");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.getContentPane().setBackground(Color.DARK_GRAY);
    }

    public void chatLayout(String port, ServerThread serverThread) {
        server = serverThread;
        exchangeButton = new JButton("Exchange keys");
        exchangeButton.setBounds(110, 490, 100, 30);
        exchangeButton.setBackground(Color.LIGHT_GRAY);
        exchangeButton.addActionListener(e -> {
            chatArea.append("Me:\n" + "Public key has been sent" + "\n");

            String jsonString = new JSONObject()
                    .put("isFile", false)
                    .put("port", port)
                    .put("sender", serverThread.getUser().getPort())
                    .put("message", "Public key received")
                    .toString();

            try {
                serverThread.sendMessage(jsonString, port);
            } catch (ShortBufferException shortBufferException) {
                shortBufferException.printStackTrace();
            } catch (IllegalBlockSizeException illegalBlockSizeException) {
                illegalBlockSizeException.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (BadPaddingException badPaddingException) {
                badPaddingException.printStackTrace();
            } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
                invalidAlgorithmParameterException.printStackTrace();
            } catch (NoSuchPaddingException noSuchPaddingException) {
                noSuchPaddingException.printStackTrace();
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            } catch (NoSuchProviderException noSuchProviderException) {
                noSuchProviderException.printStackTrace();
            } catch (InvalidKeyException invalidKeyException) {
                invalidKeyException.printStackTrace();
            }
        });
        panel = new JPanel();
        panel.setBackground(Color.DARK_GRAY);
        panel.setSize(500,600);
        layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        chatArea = new JTextArea("",20, 50);
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setBackground(Color.GRAY);
        chatArea.setForeground(Color.WHITE);

        scroll = new JScrollPane(chatArea);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        lSendFile = new JLabel("Send file: ");
        lSendFile.setBounds(10, 490, 100, 30);
        lSendFile.setForeground(Color.WHITE);

        fileTextArea = new JTextArea(2, 10);
        fileTextArea.setBounds(220, 490, 180, 30);
        fileTextArea.setEditable(false);
        fileTextArea.setLineWrap(true);
        fileTextArea.setBackground(Color.LIGHT_GRAY);

        fileTextAreaScroll = new JScrollPane(fileTextAreaScroll);
        fileTextAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        bFile = new JButton("Choose file");
        bFile.setBounds(110, 490, 100, 30);
        bFile.setBackground(Color.LIGHT_GRAY);
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
                String mode = serverThread.getUser().getEncryptionMode();
                String encryptedFileStream = null;
                try {
                    encryptedFileStream = encrypt(byteArray, mode);
                } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
                    invalidAlgorithmParameterException.printStackTrace();
                } catch (ShortBufferException shortBufferException) {
                    shortBufferException.printStackTrace();
                } catch (IllegalBlockSizeException illegalBlockSizeException) {
                    illegalBlockSizeException.printStackTrace();
                } catch (NoSuchPaddingException noSuchPaddingException) {
                    noSuchPaddingException.printStackTrace();
                } catch (IOException exception) {
                    exception.printStackTrace();
                } catch (BadPaddingException badPaddingException) {
                    badPaddingException.printStackTrace();
                } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                    noSuchAlgorithmException.printStackTrace();
                } catch (NoSuchProviderException noSuchProviderException) {
                    noSuchProviderException.printStackTrace();
                } catch (InvalidKeyException invalidKeyException) {
                    invalidKeyException.printStackTrace();
                }

                String jsonString = new JSONObject()
                        .put("isFile", true)
                        .put("port", port)
                        .put("mode", mode)
                        .put("fileName", file.getName())
                        .put("fileStream", encryptedFileStream)
                        .put("fileSize", fileSize)
                        .put("index", 0)
                        .put("numberOfMessages", 1)
                        .toString();

                //fileTextArea.append(jsonString);
                fileText = jsonString;
                System.out.println("Ready to send.");
            }
        });

        bSendFile = new JButton("Send");
        bSendFile.setBounds(410, 490, 70, 30);
        bSendFile.setBackground(Color.LIGHT_GRAY);
        bSendFile.addActionListener(e -> {
            //String jsonString = fileTextArea.getText();
            String jsonString = fileText;
            fileTextArea.setText("");

            JSONObject object = new JSONObject(jsonString);
            if(object.has("isFile")) {
                Long fileSize = Long.parseLong(object.get("fileSize").toString());
                String fileName = object.get("fileName").toString();
                if(fileSize < 50 * 1024) { // less than 50KB
                    try {
                        serverThread.sendMessage(jsonString, port);
                    } catch (ShortBufferException shortBufferException) {
                        shortBufferException.printStackTrace();
                    } catch (IllegalBlockSizeException illegalBlockSizeException) {
                        illegalBlockSizeException.printStackTrace();
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    } catch (BadPaddingException badPaddingException) {
                        badPaddingException.printStackTrace();
                    } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
                        invalidAlgorithmParameterException.printStackTrace();
                    } catch (NoSuchPaddingException noSuchPaddingException) {
                        noSuchPaddingException.printStackTrace();
                    } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                        noSuchAlgorithmException.printStackTrace();
                    } catch (NoSuchProviderException noSuchProviderException) {
                        noSuchProviderException.printStackTrace();
                    } catch (InvalidKeyException invalidKeyException) {
                        invalidKeyException.printStackTrace();
                    }
                }
                else {
                    // split fileStream
                    String port1 = object.get("port").toString();
                    String fileStream = object.get("fileStream").toString();
                    String mode = object.get("mode").toString();
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
                                        .put("mode", mode)
                                        .put("fileName", fileName)
                                        .put("fileStream", stream[i])
                                        .put("fileSize", fileSize)
                                        .put("index", i)
                                        .put("numberOfMessages", numberOfMessages)
                                        .toString();
                                serverThread.sendMessage(newJsonString, port);

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

        lSendMessage = new JLabel("Send message: ");
        lSendMessage.setBounds(10, 530, 100, 30);
        lSendMessage.setForeground(Color.WHITE);

        taText = new JTextArea(2, 10);
        taText.setBounds(110, 530, 290, 30);
        taText.setBackground(Color.LIGHT_GRAY);
        taText.setLineWrap(true);

        taScroll = new JScrollPane(taText);
        taScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        bSendMessage = new JButton("Send");
        bSendMessage.setBounds(410, 530, 70, 30);
        bSendMessage.setBackground(Color.LIGHT_GRAY);
        bSendMessage.addActionListener(e -> {
            String message = taText.getText();

            taText.setText("");
            chatArea.append("Me:\n" + message + "\n");

            String mode = serverThread.getUser().getEncryptionMode();
            byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

            try {
                String encryptedMessage = encrypt(messageBytes, mode);

                String jsonString = new JSONObject()
                        .put("isFile", false)
                        .put("port", port)
                        .put("mode", mode)
                        .put("message", encryptedMessage)
                        .toString();

                serverThread.sendMessage(jsonString, port);
            } catch (ShortBufferException shortBufferException) {
                shortBufferException.printStackTrace();
            } catch (IllegalBlockSizeException illegalBlockSizeException) {
                illegalBlockSizeException.printStackTrace();
            } catch (IOException exception) {
                exception.printStackTrace();
            } catch (BadPaddingException badPaddingException) {
                badPaddingException.printStackTrace();
            } catch (InvalidAlgorithmParameterException invalidAlgorithmParameterException) {
                invalidAlgorithmParameterException.printStackTrace();
            } catch (NoSuchPaddingException noSuchPaddingException) {
                noSuchPaddingException.printStackTrace();
            } catch (NoSuchAlgorithmException noSuchAlgorithmException) {
                noSuchAlgorithmException.printStackTrace();
            } catch (NoSuchProviderException noSuchProviderException) {
                noSuchProviderException.printStackTrace();
            } catch (InvalidKeyException invalidKeyException) {
                invalidKeyException.printStackTrace();
            }
        });

        groupComponents();
        chatFrame.setLayout(new BoxLayout(chatFrame.getContentPane(), BoxLayout.Y_AXIS));
        chatFrame.add(scroll);
        chatFrame.add(panel);

        chatFrame.pack();
        chatFrame.setVisible(true);
    }

    public void groupComponents() {
        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(lSendFile)
                                .addComponent(lSendMessage))
                        .addGroup(layout.createParallelGroup()
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(bFile))
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(fileTextAreaScroll)))
                                .addComponent(taScroll))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(bSendFile)
                                .addComponent(bSendMessage)
                                .addComponent(exchangeButton))
        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(lSendFile)
                                .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                .addComponent(bFile)
                                                .addComponent(fileTextAreaScroll))
                                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)))
                                .addComponent(bSendFile))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(lSendMessage)
                                .addComponent(taScroll)
                                .addComponent(bSendMessage))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(exchangeButton))
        );
    }

    public String encrypt(byte[] message, String mode) throws InvalidAlgorithmParameterException, ShortBufferException, IllegalBlockSizeException, NoSuchPaddingException, IOException, BadPaddingException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        if (mode.equals("CBC")) {
            return Base64
                    .getEncoder()
                    .withoutPadding()
                    .encodeToString(new CBCMode(server.getUser().getSessionKey().getByteArray()).CBCEncrypt(message));
        }
        else if (mode.equals("ECB")) {
            return Base64.getEncoder()
                    .withoutPadding()
                    .encodeToString(new ECBMode().encrypt(message, server.getUser().getSessionKey().getByteArray()));
        }
        else {
            return null;
        }
    }

    public JTextArea getChatArea() {
        return this.chatArea;
    }

    public ServerThread getServer() {
        return this.server;
    }
}
