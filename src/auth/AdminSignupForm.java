package auth;

import db.DBConnection;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminSignupForm extends JFrame {

    private JTextField usernameField, adminIDField;
    private JPasswordField passwordField, confirmPasswordField;

    public AdminSignupForm() {
        setTitle("Admin Sign Up");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        Color purple = new Color(128, 0, 128);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 12);

        // Panel for fields
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        panel.setBackground(Color.WHITE);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(purple);
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);

        // Admin ID
        JLabel adminIDLabel = new JLabel("Admin ID:");
        adminIDLabel.setFont(labelFont);
        adminIDLabel.setForeground(purple);
        adminIDField = new JTextField();
        adminIDField.setFont(fieldFont);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(purple);
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);

        // Confirm Password
        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(labelFont);
        confirmLabel.setForeground(purple);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(fieldFont);

        // Sign Up Button
        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBackground(purple);
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(labelFont);
        signupBtn.setFocusPainted(false);
        signupBtn.addActionListener(e -> registerAdmin());

        // Add fields to panel
        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(adminIDLabel);
        panel.add(adminIDField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(confirmLabel);
        panel.add(confirmPasswordField);
        panel.add(new JLabel());
        panel.add(signupBtn);

        // Back Button (top-left corner)
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(purple);
        backButton.setBorder(BorderFactory.createLineBorder(purple));
        backButton.setFocusPainted(false);
        backButton.setMargin(new Insets(5, 10, 5, 10));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.addActionListener(e -> {
            new WelcomeScreen().setVisible(true);
            dispose();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(backButton);

        add(topPanel, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }

    private void registerAdmin() {
        String user = usernameField.getText().trim();
        String adminID = adminIDField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword());
        String confirm = String.valueOf(confirmPasswordField.getPassword());

        if (user.isEmpty() || pass.isEmpty() || adminID.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields can't be empty.");
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords don't match!");
            return;
        }

        try (Connection conn = DBConnection.connect()) {

            // Check if Admin ID already exists
            PreparedStatement checkID = conn.prepareStatement("SELECT id FROM admins WHERE id = ?");
            checkID.setString(1, adminID);
            ResultSet rs = checkID.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "This Admin ID is already registered.");
                return;
            }

             PreparedStatement checkUsername = conn.prepareStatement("SELECT username FROM admins WHERE username = ?");
            checkUsername.setString(1, user);
            ResultSet rs2 = checkUsername.executeQuery();
            if (rs2.next()) {
                JOptionPane.showMessageDialog(this, "Username already taken.");
                return;
            }

             String hashed = PasswordUtils.encrypt(pass);
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO admins (id, username, password) VALUES (?, ?, ?)");
            stmt.setString(1, adminID);
            stmt.setString(2, user);
            stmt.setString(3, hashed);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Admin registration successful!");
            new WelcomeScreen().setVisible(true);
            dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

}
