package src.gui;

import org.json.JSONObject;
import src.p2p.ServerThread;

import javax.swing.*;
import java.awt.*;
import java.io.*;
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
    GroupLayout layout;

    public ChatWindow() {
        chatFrame = new JFrame();
        chatFrame.setSize(500, 600);
        chatFrame.setTitle("Security of Computer Systems");
        chatFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        chatFrame.getContentPane().setBackground(Color.DARK_GRAY);
    }

    public void chatLayout(String port, ServerThread serverThread) {
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

        bSendFile = new JButton("Send");
        bSendFile.setBounds(410, 490, 70, 30);
        bSendFile.setBackground(Color.LIGHT_GRAY);
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

            String jsonString = new JSONObject()
                    .put("isFile", false)
                    .put("port", port)
                    .put("message", message)
                    .toString();

            serverThread.sendMessage(jsonString);
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
                                .addComponent(bSendMessage))
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
        );
    }

    public JTextArea getChatArea() {
        return this.chatArea;
    }
}
