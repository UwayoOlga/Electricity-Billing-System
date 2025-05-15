package auth;

import Dashboard.CustomerDashboard;
import db.DBConnection;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerLoginForm extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField passwordField;

    public CustomerLoginForm() {
        setTitle("Customer Login");
        setSize(700, 500);
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

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(labelFont);
        firstNameLabel.setForeground(purple);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(firstNameLabel, gbc);

        firstNameField = new JTextField(20);
        firstNameField.setFont(fieldFont);
        gbc.gridx = 1;
        formPanel.add(firstNameField, gbc);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(labelFont);
        lastNameLabel.setForeground(purple);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lastNameLabel, gbc);

        lastNameField = new JTextField(20);
        lastNameField.setFont(fieldFont);
        gbc.gridx = 1;
        formPanel.add(lastNameField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(labelFont);
        passLabel.setForeground(purple);
        gbc.gridx = 0;
        gbc.gridy = 2;
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
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(loginBtn, gbc);

        loginBtn.addActionListener(e -> loginUser());

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));

        add(mainPanel);
    }

    private void loginUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String pass = String.valueOf(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Database connection failed.");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM customers WHERE first_name = ? AND last_name = ?");
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");

                if (PasswordUtils.check(pass, storedHash)) {
                    JOptionPane.showMessageDialog(this, "Login successful!");
                    new CustomerDashboard().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect password.");
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerLoginForm().setVisible(true));
    }
}
