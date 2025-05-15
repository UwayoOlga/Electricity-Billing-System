package auth;

import db.DBConnection;
import utils.PasswordUtils;
import Dashboard.AdminDashboard;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminLoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLoginForm() {
        setTitle("Admin Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color purple = new Color(128, 0, 128);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 13);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JButton backBtn = new JButton("←");
        backBtn.setBackground(purple);
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setFont(new Font("Arial", Font.BOLD, 18));
        backBtn.setPreferredSize(new Dimension(45, 45));
        backBtn.setBorder(BorderFactory.createEmptyBorder());
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setOpaque(true);
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(true);
        backBtn.setToolTipText("Back to Welcome");

        backBtn.addActionListener(e -> {
            new WelcomeScreen().setVisible(true);
            dispose();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(backBtn);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Admin Username:");
        userLabel.setFont(labelFont);
        userLabel.setForeground(purple);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(fieldFont);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(purple);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JButton loginBtn = new JButton("Log In");
        loginBtn.setBackground(purple);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Arial", Font.BOLD, 16));
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(120, 40));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> loginAdmin());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        add(mainPanel);
    }

    private void loginAdmin() {
        String username = usernameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());

        try (Connection conn = DBConnection.connect()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM admins WHERE username = ?");
            stmt.setString(1, username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next() && PasswordUtils.check(password, rs.getString("password"))) {
                JOptionPane.showMessageDialog(this, "Admin login successful!");
                new AdminDashboard(username).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin credentials.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

}
