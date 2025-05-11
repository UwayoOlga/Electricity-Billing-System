package ui;

import db.DBConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class CustomerForm extends JFrame {
    JTextField nameField, meterField;
    JTable table;
    DefaultTableModel model;

    public CustomerForm() {
        setTitle("Manage Customers");
        setSize(500, 400);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        nameField = new JTextField();
        meterField = new JTextField();
        JButton addButton = new JButton("Add Customer");

        formPanel.add(new JLabel("Name:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Meter Number:"));
        formPanel.add(meterField);
        formPanel.add(addButton);

        add(formPanel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ID", "Name", "Meter"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        addButton.addActionListener(e -> addCustomer());

        loadCustomers();
        setVisible(true);
    }

    void loadCustomers() {
        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM customers")) {
            model.setRowCount(0);
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("meter_number")});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    void addCustomer() {
        String name = nameField.getText();
        String meter = meterField.getText();
        if (name.isEmpty() || meter.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields");
            return;
        }
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers(name, meter_number) VALUES(?, ?)");) {
            stmt.setString(1, name);
            stmt.setString(2, meter);
            stmt.executeUpdate();
            loadCustomers();
            nameField.setText("");
            meterField.setText("");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
