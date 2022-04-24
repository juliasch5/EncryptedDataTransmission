import javax.swing.*;

public class ProgressWindow {
    private final JFrame progressFrame;
    private JProgressBar progressBar;
    private int numberOfMessages;

    public ProgressWindow(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
        progressFrame = new JFrame();
        progressFrame.setSize(210, 200);
        progressFrame.setLayout(null);
        progressFrame.setTitle("Sending file...");
        progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       // progressFrame.getContentPane().setBackground(Color.DARK_GRAY);

        JButton bOK;

        progressBar = new JProgressBar(0, numberOfMessages);
        progressBar.setBounds(10, 30, 180, 30);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(true);
        progressFrame.add(progressBar);

        bOK = new JButton("OK");
        bOK.setBounds(60, 80, 80, 30);
        bOK.addActionListener(e -> {
                if (progressBar.getValue() == numberOfMessages) {
                    progressFrame.setVisible(false);
                }
        });
        progressFrame.add(bOK);
        progressFrame.setVisible(true);
    }

    public void updateBar(int value) {
        progressBar.setValue(value);
    }
}
