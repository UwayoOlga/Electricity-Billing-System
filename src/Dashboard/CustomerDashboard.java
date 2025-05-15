package Dashboard;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.format.DateTimeFormatter;

public class CustomerDashboard extends JFrame {

    private final int customerId;

    private JLabel lblName, lblAccountNumber, lblEmail, lblPhone;
    private JTable metersTable, billsTable, usageTable, alertsTable;
    private DefaultTableModel metersModel, billsModel, usageModel, alertsModel;
    private JLabel lblCurrentBillAmount, lblDueDate, lblPaymentStatus;
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone;
    private JButton btnEdit, btnSave;
    public CustomerDashboard(int customerId) {
        this.customerId = customerId;

        setTitle("Customer Dashboard - Electricity Billing");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Purple theme
        Color purple = new Color(128, 0, 128);
        getContentPane().setBackground(Color.WHITE);

        // Main layout with tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Account Summary Tab
        JPanel accountPanel = createAccountSummaryPanel(purple);
        tabbedPane.addTab("Account Summary", accountPanel);

        // Meters Tab
        JPanel metersPanel = createMetersPanel();
        tabbedPane.addTab("Meters", metersPanel);

        // Billing Tab
        JPanel billingPanel = createBillingPanel();
        tabbedPane.addTab("Billing Overview", billingPanel);

        // Payments Tab
        JPanel paymentsPanel = createPaymentsPanel();
        tabbedPane.addTab("Payments", paymentsPanel);

        // Usage Tab
        JPanel usagePanel = createUsagePanel();
        tabbedPane.addTab("Usage History", usagePanel);

        // Alerts Tab
        JPanel alertsPanel = createAlertsPanel();
        tabbedPane.addTab("Alerts", alertsPanel);

        add(tabbedPane);

        loadAccountSummary();
        loadMeters();
        loadBills();
        loadUsage();
        loadAlerts();

        setVisible(true);
        Color white = Color.WHITE;

// Loop through tabs and apply custom tab component
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JLabel tabLabel = new JLabel(tabbedPane.getTitleAt(i));
            tabLabel.setOpaque(true);
            tabLabel.setBackground(purple);
            tabLabel.setForeground(white);
            tabLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tabLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            tabbedPane.setTabComponentAt(i, tabLabel);
        }
    }
    private JPanel createAccountSummaryPanel(Color purple) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Labels
        JLabel lblNameTitle = new JLabel("First Name:");
        JLabel lblLastNameTitle = new JLabel("Last Name:");
        JLabel lblAccountNumberTitle = new JLabel("Account Number:");
        JLabel lblEmailTitle = new JLabel("Email:");
        JLabel lblPhoneTitle = new JLabel("Phone:");

        Font font = new Font("Arial", Font.BOLD, 16);
        lblNameTitle.setFont(font);
        lblLastNameTitle.setFont(font);
        lblAccountNumberTitle.setFont(font);
        lblEmailTitle.setFont(font);
        lblPhoneTitle.setFont(font);

        lblNameTitle.setForeground(purple);
        lblLastNameTitle.setForeground(purple);
        lblAccountNumberTitle.setForeground(purple);
        lblEmailTitle.setForeground(purple);
        lblPhoneTitle.setForeground(purple);

        // Inputs
        txtFirstName = new JTextField(20);
        txtLastName = new JTextField(20);
        lblAccountNumber = new JLabel();
        txtEmail = new JTextField(20);
        txtPhone = new JTextField(20);

        txtFirstName.setEditable(false);
        txtLastName.setEditable(false);
        txtEmail.setEditable(false);
        txtPhone.setEditable(false);

        // Layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(lblNameTitle, gbc);
        gbc.gridx = 1;
        panel.add(txtFirstName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblLastNameTitle, gbc);
        gbc.gridx = 1;
        panel.add(txtLastName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblAccountNumberTitle, gbc);
        gbc.gridx = 1;
        panel.add(lblAccountNumber, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblEmailTitle, gbc);
        gbc.gridx = 1;
        panel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(lblPhoneTitle, gbc);
        gbc.gridx = 1;
        panel.add(txtPhone, gbc);

        // Edit and Save buttons
        btnEdit = new JButton("Edit");
        btnSave = new JButton("Save");
        btnSave.setEnabled(false);

        btnEdit.addActionListener(e -> enableEditMode(true));
        btnSave.addActionListener(e -> saveAccountChanges());

        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(btnEdit, gbc);
        gbc.gridx = 1;
        panel.add(btnSave, gbc);

        return panel;
    }


    private JPanel createMetersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        metersModel = new DefaultTableModel(new String[]{"Meter Number", "Type", "Location"}, 0);
        metersTable = new JTable(metersModel);

        JScrollPane scrollPane = new JScrollPane(metersTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton btnAddMeter = new JButton("Add Meter");
        btnAddMeter.addActionListener(e -> showAddMeterDialog());
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnAddMeter);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBillingPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Top panel for current bill summary
        JPanel topPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        topPanel.setBackground(Color.WHITE);

        lblCurrentBillAmount = new JLabel("Current Bill: $0.00");
        lblDueDate = new JLabel("Due Date: N/A");
        lblPaymentStatus = new JLabel("Payment Status: N/A");

        Font font = new Font("Arial", Font.BOLD, 16);
        lblCurrentBillAmount.setFont(font);
        lblDueDate.setFont(font);
        lblPaymentStatus.setFont(font);

        topPanel.add(lblCurrentBillAmount);
        topPanel.add(lblDueDate);
        topPanel.add(lblPaymentStatus);

        panel.add(topPanel, BorderLayout.NORTH);

        // Table for recent bills
        billsModel = new DefaultTableModel(
                new String[]{"Billing Period", "Units Used", "Amount", "Due Date", "Status"}, 0);
        billsTable = new JTable(billsModel);
        panel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentsPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Make a Payment");
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setForeground(new Color(128, 0, 128));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Select bill to pay
        panel.add(new JLabel("Select Bill:"));
        JComboBox<String> billCombo = new JComboBox<>();
        panel.add(billCombo);

        // Payment amount
        panel.add(new JLabel("Payment Amount:"));
        JTextField amountField = new JTextField();
        panel.add(amountField);

        // Payment method
        panel.add(new JLabel("Payment Method:"));
        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Credit Card", "Mobile Money", "Bank Transfer"});
        panel.add(methodCombo);

        // Pay button
        JButton payBtn = new JButton("Pay");
        payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(payBtn);

        // TODO: load unpaid bills into billCombo and handle payment logic

        return panel;
    }

    private JPanel createUsagePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        usageModel = new DefaultTableModel(new String[]{"Date", "Units Used"}, 0);
        usageTable = new JTable(usageModel);

        panel.add(new JScrollPane(usageTable), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlertsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        alertsModel = new DefaultTableModel(new String[]{"Alert Type", "Message", "Date"}, 0);
        alertsTable = new JTable(alertsModel);

        panel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadAccountSummary() {
        String sql = "SELECT first_name, last_name, account_number, email, phone FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtFirstName.setText(rs.getString("first_name"));
                txtLastName.setText(rs.getString("last_name"));
                lblAccountNumber.setText(rs.getString("account_number"));
                txtEmail.setText(rs.getString("email"));
                txtPhone.setText(rs.getString("phone"));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading account summary: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void enableEditMode(boolean editable) {
        txtFirstName.setEditable(editable);
        txtLastName.setEditable(editable);
        txtEmail.setEditable(editable);
        txtPhone.setEditable(editable);
        btnSave.setEnabled(editable);
        btnEdit.setEnabled(!editable);
    }
    private void saveAccountChanges() {
        String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, txtFirstName.getText().trim());
            stmt.setString(2, txtLastName.getText().trim());
            stmt.setString(3, txtEmail.getText().trim());
            stmt.setString(4, txtPhone.getText().trim());
            stmt.setInt(5, customerId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Account updated successfully.");
                enableEditMode(false);
            } else {
                JOptionPane.showMessageDialog(this, "Update failed. Try again.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error updating account: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void loadMeters() {
        metersModel.setRowCount(0);
        String sql = "SELECT meter_number, meter_type, location FROM meters WHERE customer_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                metersModel.addRow(new Object[]{
                        rs.getString("meter_number"),
                        rs.getString("meter_type"),
                        rs.getString("location")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading meters: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBills() {
        billsModel.setRowCount(0);
        String sql = "SELECT b.billing_period_start, b.billing_period_end, b.units, b.amount, b.due_date, b.payment_status " +
                "FROM bills b JOIN meters m ON b.meter_id = m.id WHERE m.customer_id = ? ORDER BY b.due_date DESC LIMIT 5";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            // For current bill summary
            boolean currentBillSet = false;
            while (rs.next()) {
                String period = rs.getDate("billing_period_start") + " to " + rs.getDate("billing_period_end");
                int units = rs.getInt("units");
                double amount = rs.getDouble("amount");
                Date dueDate = rs.getDate("due_date");
                String status = rs.getString("payment_status");

                billsModel.addRow(new Object[]{period, units, String.format("$%.2f", amount), dueDate, status});

                if (!currentBillSet) {
                    lblCurrentBillAmount.setText(String.format("Current Bill: $%.2f", amount));
                    lblDueDate.setText("Due Date: " + dueDate.toString());
                    lblPaymentStatus.setText("Payment Status: " + status);
                    currentBillSet = true;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadUsage() {
        usageModel.setRowCount(0);
        String sql = "SELECT usage_date, units_used FROM usage WHERE meter_id IN (SELECT id FROM meters WHERE customer_id = ?) ORDER BY usage_date DESC LIMIT 10";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usageModel.addRow(new Object[]{
                        rs.getDate("usage_date"),
                        rs.getInt("units_used")
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading usage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAlerts() {
        alertsModel.setRowCount(0);
        String sql = "SELECT alert_type, message, created_at FROM alerts WHERE customer_id = ? ORDER BY created_at DESC LIMIT 10";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                alertsModel.addRow(new Object[]{
                        rs.getString("alert_type"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at").toString()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading alerts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAddMeterDialog() {
        JDialog dialog = new JDialog(this, "Add New Meter", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));

        dialog.add(new JLabel("Meter Number:"));
        JTextField meterNumberField = new JTextField();
        dialog.add(meterNumberField);

        dialog.add(new JLabel("Meter Type:"));
        JTextField meterTypeField = new JTextField();
        dialog.add(meterTypeField);

        dialog.add(new JLabel("Location:"));
        JTextField locationField = new JTextField();
        dialog.add(locationField);

        JButton btnAdd = new JButton("Add Meter");
        btnAdd.addActionListener(e -> {
            String meterNum = meterNumberField.getText().trim();
            String meterType = meterTypeField.getText().trim();
            String location = locationField.getText().trim();

            if (meterNum.isEmpty() || meterType.isEmpty() || location.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill all fields.");
                return;
            }

            try (Connection conn = DBConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO meters (customer_id, meter_number, meter_type, location) VALUES (?, ?, ?, ?)")) {
                stmt.setInt(1, customerId);
                stmt.setString(2, meterNum);
                stmt.setString(3, meterType);
                stmt.setString(4, location);

                int inserted = stmt.executeUpdate();
                if (inserted > 0) {
                    JOptionPane.showMessageDialog(dialog, "Meter added successfully!");
                    dialog.dispose();
                    loadMeters();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error adding meter: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        dialog.add(btnAdd);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancel);

        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        // For testing only
        SwingUtilities.invokeLater(() -> new CustomerDashboard(1));
    }
}
