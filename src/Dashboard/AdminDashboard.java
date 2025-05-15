package Dashboard;

import auth.AdminLoginForm;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard(String username) {
        setTitle("Admin Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.WHITE);

        Color purple = new Color(128, 0, 128);
        Font titleFont = new Font("Arial", Font.BOLD, 22);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(titleFont);
        title.setForeground(purple);
        title.setBounds(40, 30, 300, 30);
        add(title);

        JLabel welcome = new JLabel("Welcome, " + username);
        welcome.setFont(new Font("Arial", Font.PLAIN, 16));
        welcome.setBounds(40, 70, 300, 20);
        add(welcome);

        JButton viewCustomers = new JButton("View Customers");
        viewCustomers.setBounds(40, 120, 200, 40);
        viewCustomers.setBackground(purple);
        viewCustomers.setForeground(Color.WHITE);
        viewCustomers.setFont(buttonFont);
        viewCustomers.setFocusPainted(false);
        add(viewCustomers);

        JButton viewBills = new JButton("View Bills");
        viewBills.setBounds(40, 180, 200, 40);
        viewBills.setBackground(purple);
        viewBills.setForeground(Color.WHITE);
        viewBills.setFont(buttonFont);
        viewBills.setFocusPainted(false);
        add(viewBills);

        JButton manageUsers = new JButton("Manage Users");
        manageUsers.setBounds(40, 240, 200, 40);
        manageUsers.setBackground(purple);
        manageUsers.setForeground(Color.WHITE);
        manageUsers.setFont(buttonFont);
        manageUsers.setFocusPainted(false);
        add(manageUsers);

        JButton logoutBtn = new JButton("⟲");
        logoutBtn.setBounds(740, 20, 40, 40);
        logoutBtn.setBackground(purple);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 16));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder());
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setOpaque(true);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setMargin(new Insets(0, 0, 0, 0));
        logoutBtn.setBorder(BorderFactory.createLineBorder(purple, 2, true));
        logoutBtn.setToolTipText("Logout");
        logoutBtn.addActionListener(e -> {
            new AdminLoginForm().setVisible(true);
            dispose();
        });
        add(logoutBtn);
    }
}
