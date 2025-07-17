import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

public class MeditationTimerApp extends JFrame {
    private JSlider timeSlider;
    private JLabel timeLabel;
    private JButton startButton;
    private JButton musicButton;
    private Timer countdownTimer;
    private int remainingSeconds;
    private Clip clip;
    private boolean isPlaying = false;

    public MeditationTimerApp() {
        setTitle("🌿 Mindful Moments");
        setSize(420, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        Font mainFont = new Font("Georgia", Font.PLAIN, 18);
        Color softBlue = new Color(204, 229, 255);
        Color softPink = new Color(255, 204, 229);
        Color buttonColor = new Color(137, 207, 240);

        // Gradient panel
        JPanel gradientPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                GradientPaint gp = new GradientPaint(0, 0, softBlue, 0, getHeight(), softPink);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        gradientPanel.setLayout(new BoxLayout(gradientPanel, BoxLayout.Y_AXIS));
        gradientPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(gradientPanel);

        // Time label
        timeLabel = new JLabel("Meditation Time: 10 mins 0 secs", SwingConstants.CENTER);
        timeLabel.setFont(mainFont);
        timeLabel.setForeground(new Color(50, 50, 50));
        timeLabel.setOpaque(false);
        timeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gradientPanel.add(timeLabel);

        // Slider
        timeSlider = new JSlider(1, 60, 10);
        timeSlider.setMajorTickSpacing(10);
        timeSlider.setPaintTicks(true);
        timeSlider.setPaintLabels(true);
        timeSlider.setOpaque(false);
        gradientPanel.add(timeSlider);

        // Start Button
        startButton = new JButton("🧘 Start Meditation");
        startButton.setFont(mainFont);
        startButton.setBackground(buttonColor);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setOpaque(true);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gradientPanel.add(Box.createVerticalStrut(10));
        gradientPanel.add(startButton);

        // Music Button
        musicButton = new JButton("▶ Music");
        musicButton.setFont(new Font("Arial", Font.BOLD, 16));
        musicButton.setBackground(new Color(180, 100, 200));
        musicButton.setForeground(Color.WHITE);
        musicButton.setFocusPainted(false);
        musicButton.setOpaque(true);
        musicButton.setPreferredSize(new Dimension(100, 40));
        musicButton.setMaximumSize(new Dimension(120, 50));
        musicButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        musicButton.setContentAreaFilled(false);

        // Make it oval-shaped
        musicButton.setBorder(BorderFactory.createEmptyBorder());
        musicButton.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                AbstractButton b = (AbstractButton) c;
                ButtonModel model = b.getModel();

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (model.isPressed()) {
                    g2.setColor(new Color(150, 50, 180));
                } else {
                    g2.setColor(new Color(180, 100, 200));
                }

                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 40, 40);

                g2.setColor(Color.WHITE);
                FontMetrics fm = g2.getFontMetrics();
                String text = b.getText();
                int x = (c.getWidth() - fm.stringWidth(text)) / 2;
                int y = (c.getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(text, x, y);

                g2.dispose();
            }
        });

        gradientPanel.add(Box.createVerticalStrut(10));
        gradientPanel.add(musicButton);

        // Quote
        JLabel quoteLabel = new JLabel("<html><center><i>\"Breathe in calm, breathe out stress.\"</i></center></html>");
        quoteLabel.setFont(new Font("Serif", Font.ITALIC, 14));
        quoteLabel.setForeground(new Color(80, 80, 80));
        quoteLabel.setOpaque(false);
        quoteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        gradientPanel.add(Box.createVerticalStrut(15));
        gradientPanel.add(quoteLabel);

        // Slider logic
        timeSlider.addChangeListener(e -> {
            int val = timeSlider.getValue();
            timeLabel.setText("Meditation Time: " + val + " mins 0 secs");
        });

        // Timer logic
        startButton.addActionListener(e -> {
            remainingSeconds = timeSlider.getValue() * 60;
            startButton.setEnabled(false);
            countdownTimer = new Timer(1000, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (remainingSeconds > 0) {
                        int mins = remainingSeconds / 60;
                        int secs = remainingSeconds % 60;
                        timeLabel.setText(String.format("Remaining: %02d:%02d", mins, secs));
                        remainingSeconds--;
                    } else {
                        countdownTimer.stop();
                        timeLabel.setText("✨ Meditation Complete!");
                        startButton.setEnabled(true);
                    }
                }
            });
            countdownTimer.start();
        });

        // Music Button Logic
        musicButton.addActionListener(e -> {
            if (!isPlaying) {
                playMusic();
                musicButton.setText("⏸ Music");
                isPlaying = true;
            } else {
                stopMusic();
                
                musicButton.setText("▶ Music");
                isPlaying = false;
            }
        });
    }

    private void playMusic() {
        try {
            File audioFile = new File("C:\\Java\\Meditation.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error playing music: " + ex.getMessage());
        }
    }

    private void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            MeditationTimerApp app = new MeditationTimerApp();
            app.setVisible(true);
        });
    }
}




