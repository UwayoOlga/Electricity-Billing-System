package ui;

import auth.WelcomeScreen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginScreen extends JFrame {

    private JButton welcomeButton;

    public LoginScreen() {
        setTitle("Electricity Billing System - Login");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create main panel with background image
        ImagePanel panel = new ImagePanel("src/assets/start.png");
        panel.setLayout(null);

        // Add application title
        JLabel titleLabel = new JLabel("Smart Billing. Bright Future.", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(70, 380, 800, 40);
        panel.add(titleLabel);

        // Welcome button
        welcomeButton = new JButton("GET STARTED");
        welcomeButton.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeButton.setForeground(Color.WHITE);
        welcomeButton.setBackground(new Color(128, 0, 128));
        welcomeButton.setBounds(350, 450, 200, 50);
        welcomeButton.setFocusPainted(false);
        welcomeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        welcomeButton.addActionListener(this::goToWelcomeScreen);
        panel.add(welcomeButton);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void goToWelcomeScreen(ActionEvent e) {
        new WelcomeScreen().setVisible(true);
        dispose();
    }

    // Custom panel for background image
    class ImagePanel extends JPanel {
        private Image backgroundImage;

        public ImagePanel(String imagePath) {
            try {
                backgroundImage = ImageIO.read(new File(imagePath));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading background image", "Error", JOptionPane.ERROR_MESSAGE);
                backgroundImage = null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}