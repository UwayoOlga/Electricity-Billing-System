package auth;

import db.DBConnection;
import utils.PasswordUtils;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CustomerSignupForm extends JFrame {

    private JTextField firstNameField, lastNameField, sectorField, phoneField;
    private JPasswordField passwordField, confirmPasswordField;
    private JComboBox<String> districtComboBox;

    public CustomerSignupForm() {
        setTitle("Customer Sign Up");
        setSize(550, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        Color purple = new Color(128, 0, 128);
        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 13);

        JButton backButton = new JButton("<");
        backButton.setBounds(10, 10, 35, 35);
        backButton.setBackground(purple);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setContentAreaFilled(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new WelcomeScreen().setVisible(true);
            dispose();
        });

        int xLabel = 100, xField = 220, width = 180, height = 25;

        JLabel firstNameLabel = new JLabel("First Name:");
        firstNameLabel.setFont(labelFont);
        firstNameLabel.setForeground(purple);
        firstNameLabel.setBounds(xLabel, 60, 100, height);
        firstNameField = new JTextField();
        firstNameField.setFont(fieldFont);
        firstNameField.setBounds(xField, 60, width, height);

        JLabel lastNameLabel = new JLabel("Last Name:");
        lastNameLabel.setFont(labelFont);
        lastNameLabel.setForeground(purple);
        lastNameLabel.setBounds(xLabel, 100, 100, height);
        lastNameField = new JTextField();
        lastNameField.setFont(fieldFont);
        lastNameField.setBounds(xField, 100, width, height);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(labelFont);
        passwordLabel.setForeground(purple);
        passwordLabel.setBounds(xLabel, 140, 100, height);
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBounds(xField, 140, width, height);

        JLabel confirmLabel = new JLabel("Confirm Password:");
        confirmLabel.setFont(labelFont);
        confirmLabel.setForeground(purple);
        confirmLabel.setBounds(xLabel, 180, 130, height);
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(fieldFont);
        confirmPasswordField.setBounds(xField, 180, width, height);

        JLabel districtLabel = new JLabel("District:");
        districtLabel.setFont(labelFont);
        districtLabel.setForeground(purple);
        districtLabel.setBounds(xLabel, 220, 100, height);

        // Create a combo box with the allowed districts
        String[] districts = {"gasabo", "kicukiro", "nyarugenge"};
        districtComboBox = new JComboBox<>(districts);
        districtComboBox.setFont(fieldFont);
        districtComboBox.setBounds(xField, 220, width, height);

        JLabel sectorLabel = new JLabel("Sector:");
        sectorLabel.setFont(labelFont);
        sectorLabel.setForeground(purple);
        sectorLabel.setBounds(xLabel, 260, 100, height);
        sectorField = new JTextField();
        sectorField.setFont(fieldFont);
        sectorField.setBounds(xField, 260, width, height);

        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(labelFont);
        phoneLabel.setForeground(purple);
        phoneLabel.setBounds(xLabel, 300, 100, height);
        phoneField = new JTextField();
        phoneField.setFont(fieldFont);
        phoneField.setBounds(xField, 300, width, height);

        JButton signupBtn = new JButton("Sign Up");
        signupBtn.setBounds(200, 370, 120, 35);
        signupBtn.setBackground(purple);
        signupBtn.setForeground(Color.WHITE);
        signupBtn.setFont(labelFont);
        signupBtn.addActionListener(e -> registerUser());

        add(backButton);
        add(firstNameLabel);     add(firstNameField);
        add(lastNameLabel);      add(lastNameField);
        add(passwordLabel);      add(passwordField);
        add(confirmLabel);       add(confirmPasswordField);
        add(districtLabel);      add(districtComboBox);
        add(sectorLabel);        add(sectorField);
        add(phoneLabel);         add(phoneField);
        add(signupBtn);
    }

    private void registerUser() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String password = String.valueOf(passwordField.getPassword());
        String confirmPassword = String.valueOf(confirmPasswordField.getPassword());
        String district = (String) districtComboBox.getSelectedItem();
        String sector = sectorField.getText().trim();
        String phone = phoneField.getText().trim();

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
                || sector.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits.");
            return;
        }

        try (Connection conn = DBConnection.connect()) {
            String hashedPassword = PasswordUtils.encrypt(password);

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO customers (first_name, last_name, password, district, sector, phone) VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, hashedPassword);
            stmt.setString(4, district);
            stmt.setString(5, sector);
            stmt.setString(6, phone);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Customer registered successfully!");
            new CustomerLogin().setVisible(true);
            dispose();
        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "Phone number already exists.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerSignupForm().setVisible(true));
    }
}