package ui;

import db.DBConnection;
import javax.swing.*;
import java.sql.*;

public class BillForm extends JFrame {
    JTextField customerIdField, unitsField;
    JLabel amountLabel;

    public BillForm() {
        setTitle("Generate Bill");
        setSize(300, 200);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        customerIdField = new JTextField();
        unitsField = new JTextField();
        amountLabel = new JLabel("Amount: $0");
        JButton generateButton = new JButton("Generate");

        add(new JLabel("Customer ID:"));
        add(customerIdField);
        add(new JLabel("Units Used:"));
        add(unitsField);
        add(generateButton);
        add(amountLabel);

        generateButton.addActionListener(e -> generateBill());

        setVisible(true);
    }

    void generateBill() {
        try {
            int customerId = Integer.parseInt(customerIdField.getText());
            int units = Integer.parseInt(unitsField.getText());
            double rate = 0.2;
            double amount = units * rate;

            try (Connection conn = DBConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO bills(customer_id, units, amount, billing_date) VALUES (?, ?, ?, NOW())")) {
                stmt.setInt(1, customerId);
                stmt.setInt(2, units);
                stmt.setDouble(3, amount);
                stmt.executeUpdate();
                amountLabel.setText("Amount: $" + amount);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
