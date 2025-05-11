package ui;

import javax.swing.*;
import java.awt.event.*;

public class HomePage extends JFrame {
    public HomePage() {
        setTitle("ElectroBill - Home");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu customerMenu = new JMenu("Customer");
        JMenuItem manageCustomers = new JMenuItem("Manage Customers");
        customerMenu.add(manageCustomers);
        menuBar.add(customerMenu);

        JMenu billMenu = new JMenu("Bills");
        JMenuItem manageBills = new JMenuItem("Generate Bill");
        billMenu.add(manageBills);
        menuBar.add(billMenu);

        setJMenuBar(menuBar);

        manageCustomers.addActionListener(e -> new CustomerForm());
        manageBills.addActionListener(e -> new BillForm());

        setVisible(true);
    }
}
