package auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        String[] loginOptions = {"Select Role", "Admin", "Client"};
        JComboBox<String> loginDropdown = new JComboBox<>(loginOptions);
        loginDropdown.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setBackground(new Color(128, 0, 128));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 14));
        loginBtn.setPreferredSize(new Dimension(100, 40));

        loginBtn.addActionListener(e -> {
            String selectedRole = (String) loginDropdown.getSelectedItem();
            if (selectedRole.equals("Admin")) {
                new AdminLoginForm().setVisible(true);
            } else if (selectedRole.equals("Client")) {
                new CustomerLogin().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid role.");
                return;
            }
            dispose();
        });

        JButton signupBtn = new JButton("Sign Up");
        String[] signupOptions = {"Select Role", "Admin", "Client"};
        JComboBox<String> signupDropdown = new JComboBox<>(signupOptions);
        signupDropdown.setFont(new Font("Arial", Font.BOLD, 14));
        signupBtn.setBackground(new Color(128, 0, 128));
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFocusPainted(false);
        signupBtn.setFont(new Font("Arial", Font.BOLD, 14));
        signupBtn.setPreferredSize(new Dimension(100, 40));

        signupBtn.addActionListener(e -> {
            String selectedRole = (String) signupDropdown.getSelectedItem();
            if (selectedRole.equals("Admin")) {
                new AdminSignupForm().setVisible(true);
            } else if (selectedRole.equals("Client")) {
                new CustomerSignupForm().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid role.");
                return;
            }
            dispose();
        });

        buttonPanel.add(loginDropdown);
        buttonPanel.add(loginBtn);
        buttonPanel.add(signupDropdown);
        buttonPanel.add(signupBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

         add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomeScreen().setVisible(true);
        });
    }
}
