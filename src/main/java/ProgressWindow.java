import javax.swing.*;
import java.awt.*;

public class ProgressWindow {
    private final JFrame progressFrame;
    private JProgressBar progressBar;

    public ProgressWindow() {
        progressFrame = new JFrame();
        progressFrame.setSize(210, 200);
        progressFrame.setLayout(null);
        progressFrame.setTitle("Sending file...");
        progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        progressFrame.getContentPane().setBackground(Color.DARK_GRAY);

        JButton bOK;

        progressBar = new JProgressBar();
        progressBar.setBounds(10, 30, 180, 30);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setVisible(true);
        progressFrame.add(progressBar);

        bOK = new JButton("OK");
        bOK.setBounds(60, 80, 80, 30);
        bOK.addActionListener(e -> {
                if (progressBar.getValue() == 100) {
                    progressFrame.setVisible(false);
                }
        });
        progressFrame.add(bOK);
        progressFrame.setVisible(true);
    }

    public JProgressBar getProgressBar() {
        return this.progressBar;
    }
}
