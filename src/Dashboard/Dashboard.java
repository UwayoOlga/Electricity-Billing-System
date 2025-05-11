package dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Dashboard extends JFrame {

    private String currentUser;

    public Dashboard(String username) {
        this.currentUser = username;

        setTitle("ElectroBill - Dashboard");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Top label
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(welcomeLabel, BorderLayout.CENTER);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();

        // Manage Menu
        JMenu manageMenu = new JMenu("Manage");
        JMenuItem addCustomer = new JMenuItem("Add Customer");
        JMenuItem viewCustomers = new JMenuItem("View Customers");
        manageMenu.add(addCustomer);
        manageMenu.add(viewCustomers);

        // Billing Menu
        JMenu billingMenu = new JMenu("Billing");
        JMenuItem generateBill = new JMenuItem("Generate Bill");
        JMenuItem viewBills = new JMenuItem("View Bills");
        billingMenu.add(generateBill);
        billingMenu.add(viewBills);

        // Settings Menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem logout = new JMenuItem("Logout");
        JMenuItem exit = new JMenuItem("Exit");
        settingsMenu.add(logout);
        settingsMenu.add(exit);

        // Menu actions (sample)
        logout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new auth.LoginForm().setVisible(true);
                dispose();
            }
        });

        exit.addActionListener(e -> System.exit(0));

        // Add Menus to bar
        menuBar.add(manageMenu);
        menuBar.add(billingMenu);
        menuBar.add(settingsMenu);

        setJMenuBar(menuBar);
    }
}
