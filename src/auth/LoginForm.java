package auth;

import db.DBConnection;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginForm extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm() {
        setTitle("Log In");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color backgroundColor = new Color(70, 0, 90);
        Color labelColor = Color.WHITE;
        Color buttonColor = new Color(128, 0, 128);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(backgroundColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setForeground(labelColor);
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(userLabel, gbc);

        usernameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.ipadx = 200;
        mainPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(labelColor);
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.ipadx = 0;
        mainPanel.add(passLabel, gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.ipadx = 200;
        mainPanel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Log In");
        loginBtn.setBackground(buttonColor);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.ipadx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> loginUser());

        add(mainPanel);
    }

    private void loginUser() {
        String user = usernameField.getText();
        String pass = String.valueOf(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
            stmt.setString(1, user);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedInput = PasswordUtils.encrypt(pass);
                if (hashedInput.equals(rs.getString("password"))) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    new dashboard.Dashboard(user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect password.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
