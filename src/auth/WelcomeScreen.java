package auth;

import javax.swing.*;
import java.awt.*;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        setTitle("Welcome to ElectroBill");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

         JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

         ImageIcon imageIcon = new ImageIcon("src/assets/logo.png");
        JLabel imageLabel = new JLabel(imageIcon, JLabel.CENTER);
        panel.add(imageLabel, BorderLayout.CENTER);

         JPanel buttonPanel = new JPanel();
        JButton loginBtn = new JButton("Log In");
        JButton signupBtn = new JButton("Sign Up");

        Color purple = new Color(128, 0, 128);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        loginBtn.setBackground(purple);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(buttonFont);
        loginBtn.setPreferredSize(new Dimension(100, 40));

        signupBtn.setBackground(purple);
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.setFont(buttonFont);
        signupBtn.setPreferredSize(new Dimension(100, 40));

        loginBtn.addActionListener(e -> {
            new LoginForm().setVisible(true);
            dispose();
        });

        signupBtn.addActionListener(e -> {
            new SignupForm().setVisible(true);
            dispose();
        });

        buttonPanel.add(signupBtn);
        buttonPanel.add(loginBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreen().setVisible(true);
        });
    }
}
