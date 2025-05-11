package auth;

import db.DBConnection;
import utils.PasswordUtils;
import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class SignupForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField, confirmPasswordField;

    public SignupForm() {
        setTitle("Sign Up");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color purple = new Color(128, 0, 128);
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JPanel panel = new JPanel(new GridLayout(5, 2, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        panel.setBackground(Color.WHITE);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(purple);
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(purple);
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(labelFont);
        confirmLabel.setForeground(purple);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(fieldFont);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBackground(purple);
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(new Font("Arial", Font.BOLD, 16));
        signupBtn.setFocusPainted(false);
        signupBtn.setPreferredSize(new Dimension(120, 40));

        signupBtn.addActionListener(e -> registerUser());

        panel.add(userLabel);
        panel.add(usernameField);
        panel.add(passLabel);
        panel.add(passwordField);
        panel.add(confirmLabel);
        panel.add(confirmPasswordField);
        panel.add(new JLabel());
        panel.add(signupBtn);

        add(panel);
    }

    private void registerUser() {
        String user = usernameField.getText();
        String pass = String.valueOf(passwordField.getPassword());
        String confirm = String.valueOf(confirmPasswordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields can't be empty.");
            return;
        }

        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords don't match!");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            String hashed = PasswordUtils.encrypt(pass);
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            stmt.setString(1, user);
            stmt.setString(2, hashed);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Registration successful!");
            new LoginForm().setVisible(true);
            dispose();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Username already exists.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
