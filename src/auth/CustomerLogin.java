package auth;

import Dashboard.CustomerDashboard;
import db.DBConnection;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CustomerLogin extends JFrame {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JPasswordField passwordField;

    public CustomerLogin() {
        setTitle("Customer Login - Electricity Billing");
        setSize(400, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Customer Login");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(120, 20, 200, 30);
        panel.add(title);

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setBounds(50, 70, 100, 25);
        panel.add(firstNameLabel);

        firstNameField = new JTextField();
        firstNameField.setBounds(150, 70, 180, 25);
        panel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setBounds(50, 110, 100, 25);
        panel.add(lastNameLabel);

        lastNameField = new JTextField();
        lastNameField.setBounds(150, 110, 180, 25);
        panel.add(lastNameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(50, 150, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 150, 180, 25);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 200, 100, 30);
        loginBtn.setBackground(new Color(128, 0, 128));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(this::handleLogin);

        panel.add(loginBtn);
        add(panel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void handleLogin(ActionEvent e) {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            // Step 1: Query user by first name and last name only
            String sql = "SELECT id, password FROM customers WHERE first_name = ? AND last_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password");
                int customerId = rs.getInt("id");

                 if (PasswordUtils.check(password, storedHash)) {
                    dispose();
                    new CustomerDashboard(customerId);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
        }
    }


    public static void main(String[] args) {
        new CustomerLogin();
    }
}
