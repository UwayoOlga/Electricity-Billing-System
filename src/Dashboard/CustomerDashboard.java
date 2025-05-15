package Dashboard;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.time.LocalDate;

public class CustomerDashboard extends JFrame {

    private int customerId;
    private JTable meterTable;
    private DefaultTableModel meterTableModel;

    private JTextField meterNumberField;
    private JComboBox<String> meterTypeCombo;
    private JButton addMeterBtn;

    private JTable billsTable;
    private DefaultTableModel billsTableModel;

    public CustomerDashboard(int customerId) {
        this.customerId = customerId;

        setTitle("Customer Dashboard - Electricity Billing");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Purple theme panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(128, 0, 128)); // purple background
        add(mainPanel, BorderLayout.CENTER);

        // Title
        JLabel titleLabel = new JLabel("Welcome to Your Dashboard");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Meters Panel
        JPanel metersPanel = new JPanel(new BorderLayout());
        metersPanel.setOpaque(false);

        meterTableModel = new DefaultTableModel(new String[]{"Meter ID", "Meter Number", "Type", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        meterTable = new JTable(meterTableModel);
        JScrollPane meterScroll = new JScrollPane(meterTable);
        metersPanel.add(meterScroll, BorderLayout.CENTER);

        // Add meter input form
        JPanel addMeterPanel = new JPanel();
        addMeterPanel.setOpaque(false);
        addMeterPanel.setLayout(new FlowLayout());

        meterNumberField = new JTextField(10);
        meterTypeCombo = new JComboBox<>(new String[]{"Residential", "Commercial", "Industrial"});
        addMeterBtn = new JButton("Add Meter");
        addMeterBtn.setBackground(new Color(128, 0, 128));
        addMeterBtn.setForeground(Color.WHITE);
        addMeterBtn.addActionListener(e -> addMeter());

        addMeterPanel.add(new JLabel("Meter Number:"));
        addMeterPanel.add(meterNumberField);
        addMeterPanel.add(new JLabel("Type:"));
        addMeterPanel.add(meterTypeCombo);
        addMeterPanel.add(addMeterBtn);

        metersPanel.add(addMeterPanel, BorderLayout.SOUTH);

        mainPanel.add(metersPanel, BorderLayout.WEST);
        metersPanel.setPreferredSize(new Dimension(450, 0));

        // Bills panel
        JPanel billsPanel = new JPanel(new BorderLayout());
        billsPanel.setOpaque(false);

        billsTableModel = new DefaultTableModel(new String[]{"Bill ID", "Meter Number", "Usage (kWh)", "Amount ($)", "Date"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        billsTable = new JTable(billsTableModel);
        JScrollPane billsScroll = new JScrollPane(billsTable);
        billsPanel.add(billsScroll, BorderLayout.CENTER);

        mainPanel.add(billsPanel, BorderLayout.CENTER);

        // Load data from DB
        loadMeters();
        loadBills();

        setVisible(true);
    }

    private void loadMeters() {
        meterTableModel.setRowCount(0);
        String sql = "SELECT id, meter_number, meter_type, status FROM meters WHERE customer_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                meterTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("meter_number"),
                        rs.getString("meter_type"),
                        rs.getString("status")
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading meters: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void loadBills() {
        billsTableModel.setRowCount(0);
        String sql = "SELECT b.id, m.meter_number, b.unit, b.amount, b.bill_date " +
                "FROM bills b JOIN meters m ON b.meter_id = m.id WHERE m.customer_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                billsTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("meter_number"),
                        rs.getDouble("unit"),
                        rs.getDouble("amount"),
                        rs.getDate("bill_date").toLocalDate()
                });
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void addMeter() {
        String meterNumber = meterNumberField.getText().trim();
        String meterType = (String) meterTypeCombo.getSelectedItem();

        if (meterNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a meter number.");
            return;
        }

        String sql = "INSERT INTO meters (customer_id, meter_number, meter_type, status) VALUES (?, ?, ?, 'Active')";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            stmt.setString(2, meterNumber);
            stmt.setString(3, meterType);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Meter added successfully!");
                meterNumberField.setText("");
                loadMeters();
                loadBills();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add meter.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding meter: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    private void calculateAndAddBill(int meterId, double usage) {
        double ratePerKwh = 0.15; // Example rate
        double amount = usage * ratePerKwh;

        String sql = "INSERT INTO bills (meter_id, units, amount, billing_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, meterId);
            stmt.setDouble(2, usage);
            stmt.setDouble(3, amount);
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            stmt.executeUpdate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding bill: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}
