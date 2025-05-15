package Dashboard;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.Vector;

public class CustomerDashboard extends JFrame {

    private final int customerId;  // Passed after login
    private final Color purple = new Color(128, 0, 128);
    private JTable meterTable, billTable;
    private JLabel welcomeLabel;

    public CustomerDashboard(int customerId, String firstName, String lastName) {
        this.customerId = customerId;
        setTitle("Customer Dashboard");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        welcomeLabel = new JLabel("Welcome, " + firstName + " " + lastName);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(purple);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        mainPanel.add(welcomeLabel, BorderLayout.NORTH);

        // Tabs for meters and bills
        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("My Meters", createMeterPanel());
        tabbedPane.addTab("My Bills", createBillPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Logout + Report buttons
        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(purple);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.addActionListener(e -> logout());

        JButton reportBtn = new JButton("Generate Report");
        reportBtn.setBackground(purple);
        reportBtn.setForeground(Color.WHITE);
        reportBtn.addActionListener(this::generateReport);

        bottomPanel.add(reportBtn);
        bottomPanel.add(logoutBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        add(mainPanel);

        setVisible(true);
        loadMeters();
    }

    private JPanel createMeterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        meterTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(meterTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBillPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        billTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(billTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void loadMeters() {
        try (Connection conn = DBConnection.connect()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id AS Meter_ID, meter_number AS Meter_Number FROM meters WHERE customer_id = ?");
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            meterTable.setModel(buildTableModel(rs));

            rs.beforeFirst();
            Vector<Integer> meterIds = new Vector<>();
            while (rs.next()) {
                meterIds.add(rs.getInt("Meter_ID"));
            }
            loadBills(conn, meterIds);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading meters: " + e.getMessage());
        }
    }

    private void loadBills(Connection conn, Vector<Integer> meterIds) throws SQLException {
        if (meterIds.isEmpty()) return;

        StringBuilder query = new StringBuilder("SELECT b.id AS Bill_ID, m.meter_number AS Meter, b.month, b.amount_due, b.status, b.due_date FROM bills b JOIN meters m ON b.meter_id = m.id WHERE m.id IN (");
        for (int i = 0; i < meterIds.size(); i++) {
            query.append("?");
            if (i < meterIds.size() - 1) query.append(",");
        }
        query.append(")");

        PreparedStatement stmt = conn.prepareStatement(query.toString());
        for (int i = 0; i < meterIds.size(); i++) {
            stmt.setInt(i + 1, meterIds.get(i));
        }

        ResultSet rs = stmt.executeQuery();
        billTable.setModel(buildTableModel(rs));
    }

    private void generateReport(ActionEvent e) {
        JOptionPane.showMessageDialog(this, "Report generation coming soon!");
        // Optional: Export meter + bill data to CSV/PDF
    }

    private void logout() {
        dispose();
        new auth.CustomerLoginForm().setVisible(true);
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int colCount = meta.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= colCount; i++) {
            columnNames.add(meta.getColumnName(i));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= colCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }
        return new DefaultTableModel(data, columnNames);
    }

    public static void main(String[] args) {
        // For demo purposes
        SwingUtilities.invokeLater(() -> new CustomerDashboard(1, "John", "Doe"));
    }
}
