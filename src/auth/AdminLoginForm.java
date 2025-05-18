package auth;

import db.DBConnection;
import utils.PasswordUtils;
import Dashboard.AdminDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class AdminLoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLoginForm() {
        setTitle("Admin Login - Electricity Billing");
        setSize(600, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        ImagePanel panel = new ImagePanel("src/assets/login (2).png");
        panel.setLayout(null);

        JLabel title = new JLabel("Welcome Back!");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setBounds(120, 20, 200, 30);
        panel.add(title);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(50, 70, 100, 25);
        panel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 70, 180, 25);
        usernameField.setOpaque(false);
        panel.add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 110, 100, 25);
        panel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 180, 25);
        passwordField.setOpaque(false);
        panel.add(passwordField);

        JButton loginBtn = new JButton("Login");
        loginBtn.setBounds(150, 160, 100, 30);
        loginBtn.setBackground(new Color(128, 0, 128));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(this::loginAdmin);
        panel.add(loginBtn);


        JButton backBtn = new JButton("Back");
        backBtn.setBounds(20, 20, 80, 25);
        backBtn.setBackground(new Color(128, 0, 128));
        backBtn.setForeground(Color.WHITE);
        backBtn.addActionListener(e -> {
            new WelcomeScreen().setVisible(true);
            dispose();
        });
        panel.add(backBtn);

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    class ImagePanel extends JPanel {
        private Image image;

        public ImagePanel(String imagePath) {
            this.image = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private void loginAdmin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

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

    public static void main(String[] args) {
        new AdminLoginForm();
    }
}