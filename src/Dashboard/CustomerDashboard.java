package Dashboard;

import auth.CustomerLogin;
import db.DBConnection;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.itextpdf.text.FontFactory.HELVETICA;
import static com.itextpdf.text.FontFactory.HELVETICA_BOLD;
import static java.awt.Color.white;

public class CustomerDashboard extends JFrame {
    private final int customerId;
    private List<Bill> customerBills = new ArrayList<>();
    private double totalAmountDue = 0.0;
    private LocalDate dueDate;
    private String paymentStatus = "Unpaid";
    private JComboBox<String> meterCombo;
    private static final double UNIT_RATE = 100.0;

    private JTextField txtFirstName, txtLastName, txtDistrict, txtSector, txtPhone;
    private JButton btnEdit, btnSave;
    private JTable metersTable, billsTable, usageTable, alertsTable;
    private DefaultTableModel metersModel, billsModel, usageModel, alertsModel;
    private JLabel lblCurrentBillAmount, lblDueDate, lblPaymentStatus;
    private JTextField searchMetersField, searchBillsField;
    private TableRowSorter<DefaultTableModel> metersSorter, billsSorter;

    private class Bill {
        int meterId;
        String meterNumber;
        Date billDate;
        double units;
        double amount;
        String status;

        public Bill(int meterId, String meterNumber, Date billDate, double units, double amount, String status) {
            this.meterId = meterId;
            this.meterNumber = meterNumber;
            this.billDate = billDate;
            this.units = units;
            this.amount = amount;
            this.status = status;
        }
    }

    public CustomerDashboard(int customerId) {
        this.customerId = customerId;
        setTitle("Customer Dashboard - Electricity Billing");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color purple = new Color(128, 0, 128);
        Color lightPurple = new Color(230, 204, 255);
        Color white = Color.WHITE;
        getContentPane().setBackground(white);

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel accountPanel = createAccountSummaryPanel(purple, white);
        tabbedPane.addTab("Account Summary", accountPanel);

        JPanel metersPanel = createMetersPanel(purple, white, lightPurple);
        tabbedPane.addTab("Meters", metersPanel);
        JPanel billingPanel = createBillingPanel(purple, white, lightPurple);
        tabbedPane.addTab("Billing Overview", billingPanel);
        JPanel paymentsPanel = createPaymentsPanel(purple, white, lightPurple);
        tabbedPane.addTab("Payments", paymentsPanel);
        JPanel usagePanel = createUsagePanel(white, lightPurple);
        tabbedPane.addTab("Usage History", usagePanel);
        JPanel alertsPanel = createAlertsPanel(white, lightPurple);
        tabbedPane.addTab("Notifications", alertsPanel);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(white);
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(purple);
        logoutButton.setForeground(white);
        logoutButton.addActionListener(e -> {
            this.dispose();
            new CustomerLogin();
        });

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanel.setBackground(white);
        logoutPanel.add(logoutButton);
        headerPanel.add(logoutPanel, BorderLayout.EAST);
        headerPanel.add(tabbedPane, BorderLayout.CENTER);
        add(headerPanel);

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            JLabel tabLabel = new JLabel(tabbedPane.getTitleAt(i));
            tabLabel.setOpaque(true);
            tabLabel.setBackground(purple);
            tabLabel.setForeground(white);
            tabLabel.setHorizontalAlignment(SwingConstants.CENTER);
            tabLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            tabbedPane.setTabComponentAt(i, tabLabel);
        }

        loadAccountSummary();
        loadMeters();
        loadBills();
        loadUsage();
        loadAlerts();
        setVisible(true);
    }

    private JPanel createAccountSummaryPanel(Color purple, Color bg) {
        JPanel panel = new JPanel(new GridBagLayout());
        Color lightPurple = new Color(230, 204, 255);

        panel.setBackground(lightPurple);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        JLabel heading = new JLabel("Account Details");
        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(purple);
        gbc.gridwidth = 2;
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(heading, gbc);
        gbc.gridwidth = 1;

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        String[] labels = {"First Name:", "Last Name:", "District:", "Sector:", "Phone:"};
        JTextField[] fields = new JTextField[5];
        fields[0] = txtFirstName = new JTextField(20);
        fields[1] = txtLastName = new JTextField(20);
        fields[2] = txtDistrict = new JTextField(20);
        fields[3] = txtSector = new JTextField(20);
        fields[4] = txtPhone = new JTextField(20);

        for (JTextField txt : fields) txt.setEditable(false);

        int row = 1;
        for (int i = 0; i < labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont);
            lbl.setForeground(purple);
            gbc.gridx = 0; gbc.gridy = row;
            panel.add(lbl, gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
            row++;
        }

        btnEdit = new JButton("Edit");
        btnEdit.setBackground(purple); btnEdit.setForeground(white);
        btnSave = new JButton("Save");
        btnSave.setBackground(purple); btnSave.setForeground(white);
        btnSave.setEnabled(false);

        btnEdit.addActionListener(e -> enableEditMode(true));
        btnSave.addActionListener(e -> { saveAccountChanges(); enableEditMode(false); });

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(btnEdit, gbc);
        gbc.gridx = 1;
        panel.add(btnSave, gbc);

        return panel;
    }

    private JPanel createMetersPanel(Color purple, Color bg, Color tableBg) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(bg);
        searchPanel.add(new JLabel("Search:"));
        searchMetersField = new JTextField(20);
        searchPanel.add(searchMetersField);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(purple);
        searchButton.setForeground(white);
        searchButton.addActionListener(e -> searchMeters());
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.NORTH);

        metersModel = new DefaultTableModel(new String[]{"Meter Number", "Type", "Location", "Status"}, 0);
        metersTable = new JTable(metersModel);

        metersTable.setBackground(tableBg);
        metersTable.setSelectionBackground(purple);
        metersTable.setSelectionForeground(white);
        metersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        metersTable.setRowHeight(25);
        metersTable.setGridColor(purple);
        metersTable.getTableHeader().setBackground(purple);
        metersTable.getTableHeader().setForeground(white);
        metersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        metersSorter = new TableRowSorter<>(metersModel);
        metersTable.setRowSorter(metersSorter);
        panel.add(new JScrollPane(metersTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(); btnPanel.setBackground(bg);
        JButton btnAdd = new JButton("Add Meter"); btnAdd.setBackground(purple); btnAdd.setForeground(white);
        JButton btnDel = new JButton("Delete Selected Meter"); btnDel.setBackground(purple); btnDel.setForeground(white);
        JButton btnReport = new JButton("Generate Report"); btnReport.setBackground(purple); btnReport.setForeground(white);
        JButton btnPref = new JButton("Calculate Bill"); btnPref.setBackground(purple); btnPref.setForeground(white);
        btnAdd.addActionListener(e -> showAddMeterDialog());
        btnDel.addActionListener(e -> deleteSelectedMeter());
        btnReport.addActionListener(e -> generateMonthlyReport());
        btnPref.addActionListener(e -> markAsPreferred());
        btnPanel.add(btnAdd); btnPanel.add(btnDel); btnPanel.add(btnReport); btnPanel.add(btnPref);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createBillingPanel(Color purple, Color bg, Color tableBg) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);

        JPanel top = new JPanel(new GridLayout(1, 3, 15, 15)); top.setBackground(bg);
        Font f = new Font("Arial", Font.BOLD, 16);
        lblCurrentBillAmount = new JLabel("Current Bill: RWF0.00"); lblCurrentBillAmount.setFont(f);
        lblDueDate = new JLabel("Due Date: N/A"); lblDueDate.setFont(f);
        lblPaymentStatus = new JLabel("Payment Status: N/A"); lblPaymentStatus.setFont(f);
        top.add(lblCurrentBillAmount); top.add(lblDueDate); top.add(lblPaymentStatus);
        panel.add(top, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(bg);
        searchPanel.add(new JLabel("Search:"));
        searchBillsField = new JTextField(20);
        searchPanel.add(searchBillsField);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(purple);
        searchButton.setForeground(white);
        searchButton.addActionListener(e -> searchBills());
        searchPanel.add(searchButton);
        panel.add(searchPanel, BorderLayout.CENTER);

        billsModel = new DefaultTableModel(new String[]{"Meter", "Bill Date", "Units", "Amount", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        billsTable = new JTable(billsModel);

        billsTable.setBackground(tableBg);
        billsTable.setSelectionBackground(purple);
        billsTable.setSelectionForeground(white);
        billsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        billsTable.setRowHeight(25);
        billsTable.setGridColor(purple);
        billsTable.getTableHeader().setBackground(purple);
        billsTable.getTableHeader().setForeground(white);
        billsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        billsSorter = new TableRowSorter<>(billsModel);
        billsTable.setRowSorter(billsSorter);
        panel.add(new JScrollPane(billsTable), BorderLayout.CENTER);

        JButton btnUnits = new JButton("Enter Units");
        btnUnits.setBackground(purple); btnUnits.setForeground(white);
        btnUnits.addActionListener(e -> showUnitsDialog());
        JPanel south = new JPanel(); south.setBackground(bg);
        south.add(btnUnits);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createPaymentsPanel(Color purple, Color bg, Color lightPurple) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(bg);

        JLabel titleLabel = new JLabel("Make a Payment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(purple);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(bg);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel meterLabel = new JLabel("Select Meter:");
        meterLabel.setFont(new Font("Arial", Font.BOLD, 14));
        meterLabel.setForeground(purple);
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(meterLabel, gbc);

        meterCombo = new JComboBox<>();
        meterCombo.setPreferredSize(new Dimension(200, 30));
        meterCombo.setBackground(lightPurple);
        meterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        updatePaymentMeterDropdown();
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(meterCombo, gbc);

        JLabel amountLabel = new JLabel("Amount Due:");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        amountLabel.setForeground(purple);
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(amountLabel, gbc);

        JTextField amountField = new JTextField();
        amountField.setEditable(false);
        amountField.setPreferredSize(new Dimension(200, 30));
        amountField.setFont(new Font("Arial", Font.PLAIN, 14));
        amountField.setBackground(lightPurple);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(amountField, gbc);

        JLabel methodLabel = new JLabel("Payment Method:");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        methodLabel.setForeground(purple);
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(methodLabel, gbc);

        JComboBox<String> methodCombo = new JComboBox<>(new String[]{"Credit Card", "Mobile Money", "Bank Transfer"});
        methodCombo.setPreferredSize(new Dimension(200, 30));
        methodCombo.setBackground(lightPurple);
        methodCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(methodCombo, gbc);

        JLabel totalLabel = new JLabel("Total to Pay:");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(purple);
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(totalLabel, gbc);

        JTextField totalField = new JTextField();
        totalField.setEditable(false);
        totalField.setPreferredSize(new Dimension(200, 30));
        totalField.setFont(new Font("Arial", Font.BOLD, 14));
        totalField.setBackground(lightPurple);
        gbc.gridx = 1; gbc.gridy = 3;
        formPanel.add(totalField, gbc);

        contentPanel.add(formPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(bg);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton payBtn = new JButton("Make Payment");
        payBtn.setFont(new Font("Arial", Font.BOLD, 16));
        payBtn.setBackground(purple);
        payBtn.setForeground(white);
        payBtn.setPreferredSize(new Dimension(200, 40));
        payBtn.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

        meterCombo.addActionListener(e -> {
            String selectedMeter = (String) meterCombo.getSelectedItem();
            if (selectedMeter != null && !selectedMeter.equals("No unpaid meters found")) {
                Bill latestBill = getLatestBillForMeter(selectedMeter);
                if (latestBill != null) {
                    amountField.setText(String.format("RWF%.2f", latestBill.amount));
                    totalField.setText(String.format("RWF%.2f", latestBill.amount));
                } else {
                    amountField.setText("No bill found");
                    totalField.setText("RWF0.00");
                }
            }
        });

        payBtn.addActionListener(e -> {
            String selectedMeter = (String) meterCombo.getSelectedItem();
            if (selectedMeter != null && !selectedMeter.equals("No unpaid meters found")) {
                Bill latestBill = getLatestBillForMeter(selectedMeter);
                if (latestBill != null) {
                    String method = (String) methodCombo.getSelectedItem();
                    boolean paymentSuccessful = showPaymentDetailsDialog(method, latestBill.amount);

                    if (paymentSuccessful) {
                        processPayment(latestBill, method);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No bill found for selected meter");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a meter");
            }
        });

        buttonPanel.add(payBtn);
        contentPanel.add(buttonPanel);
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createUsagePanel(Color bg, Color tableBg) {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(bg);
        usageModel = new DefaultTableModel(new String[]{"Date", "Units Used"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        usageTable = new JTable(usageModel);

        usageTable.setBackground(tableBg);
        usageTable.setSelectionBackground(new Color(128, 0, 128));
        usageTable.setSelectionForeground(white);
        usageTable.setFont(new Font("Arial", Font.PLAIN, 14));
        usageTable.setRowHeight(25);
        usageTable.setGridColor(new Color(128, 0, 128));
        usageTable.getTableHeader().setBackground(new Color(128, 0, 128));
        usageTable.getTableHeader().setForeground(white);
        usageTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(new JScrollPane(usageTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAlertsPanel(Color bg, Color tableBg) {
        JPanel panel = new JPanel(new BorderLayout()); panel.setBackground(bg);
        alertsModel = new DefaultTableModel(new String[]{"Alert Type", "Message", "Date"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };        alertsTable = new JTable(alertsModel);

        alertsTable.setBackground(tableBg);
        alertsTable.setSelectionBackground(new Color(128, 0, 128));
        alertsTable.setSelectionForeground(white);
        alertsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        alertsTable.setRowHeight(25);
        alertsTable.setGridColor(new Color(128, 0, 128));
        alertsTable.getTableHeader().setBackground(new Color(128, 0, 128));
        alertsTable.getTableHeader().setForeground(white);
        alertsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        panel.add(new JScrollPane(alertsTable), BorderLayout.CENTER);
        return panel;
    }

    private void searchMeters() {
        String text = searchMetersField.getText();
        if (text.trim().length() == 0) {
            metersSorter.setRowFilter(null);
        } else {
            metersSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void searchBills() {
        String text = searchBillsField.getText();
        if (text.trim().length() == 0) {
            billsSorter.setRowFilter(null);
        } else {
            billsSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void generateMonthlyReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Monthly Report");
        fileChooser.setSelectedFile(new File("Electricity_Usage_Report_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM")) + ".pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                com.itextpdf.text.Font titleFont = FontFactory.getFont(HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Monthly Electricity Usage Report", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                com.itextpdf.text.Font infoFont = FontFactory.getFont(HELVETICA, 12, BaseColor.BLACK);
                Paragraph customerInfo = new Paragraph();
                customerInfo.add(new Phrase("Customer Name: " + txtFirstName.getText() + " " +
                        txtLastName.getText() + "\n", infoFont));
                customerInfo.add(new Phrase("Address: " + txtDistrict.getText() + ", " +
                        txtSector.getText() + "\n", infoFont));
                customerInfo.add(new Phrase("Phone: " + txtPhone.getText() + "\n", infoFont));
                customerInfo.add(new Phrase("Report Date: " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) +
                        "\n\n", infoFont));
                document.add(customerInfo);

                PdfPTable metersPdfTable = new PdfPTable(metersModel.getColumnCount());
                metersPdfTable.setWidthPercentage(100);

                for (int i = 0; i < metersModel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(metersModel.getColumnName(i)));
                    cell.setBackgroundColor(new BaseColor(128, 0, 128));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(5);
                    metersPdfTable.addCell(cell);
                }

                com.itextpdf.text.Font tableFont = FontFactory.getFont(HELVETICA, 12, BaseColor.BLACK);
                for (int i = 0; i < metersModel.getRowCount(); i++) {
                    for (int j = 0; j < metersModel.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(
                                metersModel.getValueAt(i, j).toString(), tableFont));
                        cell.setPadding(5);
                        metersPdfTable.addCell(cell);
                    }
                }

                document.add(new Paragraph("Meter Information:", infoFont));
                document.add(metersPdfTable);
                document.add(Chunk.NEWLINE);

                PdfPTable billsPdfTable = new PdfPTable(billsModel.getColumnCount());
                billsPdfTable.setWidthPercentage(100);

                for (int i = 0; i < billsModel.getColumnCount(); i++) {
                    PdfPCell cell = new PdfPCell(new Phrase(billsModel.getColumnName(i)));
                    cell.setBackgroundColor(new BaseColor(128, 0, 128));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(5);
                    billsPdfTable.addCell(cell);
                }

                for (int i = 0; i < billsModel.getRowCount(); i++) {
                    for (int j = 0; j < billsModel.getColumnCount(); j++) {
                        PdfPCell cell = new PdfPCell(new Phrase(
                                billsModel.getValueAt(i, j).toString(), tableFont));
                        cell.setPadding(5);
                        billsPdfTable.addCell(cell);
                    }
                }

                document.add(new Paragraph("Billing Information:", infoFont));
                document.add(billsPdfTable);
                document.add(Chunk.NEWLINE);

                Paragraph summary = new Paragraph();
                summary.add(new Phrase("Total Amount Due: " +
                        String.format("RWF%.2f", totalAmountDue) + "\n", infoFont));
                summary.add(new Phrase("Due Date: " +
                        dueDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")) + "\n", infoFont));
                summary.add(new Phrase("Payment Status: " + paymentStatus + "\n", infoFont));
                document.add(summary);

                document.close();

                JOptionPane.showMessageDialog(this,
                        "Monthly report generated successfully!",
                        "Report Generated",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error generating report: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadAccountSummary() {
        String sql = "SELECT first_name, last_name, district, sector, phone FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                txtFirstName.setText(rs.getString("first_name"));
                txtLastName.setText(rs.getString("last_name"));
                txtDistrict.setText(rs.getString("district"));
                txtSector.setText(rs.getString("sector"));
                txtPhone.setText(rs.getString("phone"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading account: " + e.getMessage());
        }
    }

    private void enableEditMode(boolean editable) {
        txtFirstName.setEditable(editable);
        txtLastName.setEditable(editable);
        txtDistrict.setEditable(editable);
        txtSector.setEditable(editable);
        txtPhone.setEditable(editable);
        btnSave.setEnabled(editable);
        btnEdit.setEnabled(!editable);
    }

    private void saveAccountChanges() {
        String sql = "UPDATE customers SET first_name=?, last_name=?, district=?, sector=?, phone=? WHERE id=?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, txtFirstName.getText());
            stmt.setString(2, txtLastName.getText());
            stmt.setString(3, txtDistrict.getText());
            stmt.setString(4, txtSector.getText());
            stmt.setString(5, txtPhone.getText());
            stmt.setInt(6, customerId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Account updated successfully.");
            loadAccountSummary();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error saving account: " + e.getMessage());
        }
    }

    private void loadMeters() {
        metersModel.setRowCount(0);
        String sql = "SELECT meter_number, meter_type, location, status, is_preferred FROM meters WHERE customer_id = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                boolean isPreferred = rs.getBoolean("is_preferred");
                String prefStatus = isPreferred ? "★ Preferred" : "";

                metersModel.addRow(new Object[]{
                        rs.getString("meter_number"),
                        rs.getString("meter_type"),
                        rs.getString("location"),
                        rs.getString("status") + " " + prefStatus
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading meters: " + e.getMessage());
        }
    }

    private void markAsPreferred() {
        int row = metersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a meter first.");
            return;
        }

        String meterNo = (String) metersModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark meter " + meterNo + " as preferred?\n(Only preferred meters will be billed)",
                "Calculate bill",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE meters SET is_preferred = NOT is_preferred WHERE meter_number = ?")) {

                stmt.setString(1, meterNo);
                stmt.executeUpdate();
                loadMeters();
                JOptionPane.showMessageDialog(this,
                        "Meter preference updated successfully.");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error updating meter preference: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddMeterDialog() {
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Commercial", "Residence"});
        JComboBox<String> locationBox = new JComboBox<>(new String[]{"Gasabo", "Kicukiro", "Nyarugenge"});
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Active", "Inactive"});
        JTextField meterNumberField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Meter Type:")); panel.add(typeBox);
        panel.add(new JLabel("Location:")); panel.add(locationBox);
        panel.add(new JLabel("Status:")); panel.add(statusBox);
        panel.add(new JLabel("Meter Number:")); panel.add(meterNumberField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Meter", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO meters (customer_id, meter_type, location, status, meter_number) VALUES (?, ?, ?, ?, ?)")) {
                stmt.setInt(1, customerId);
                stmt.setString(2, (String) typeBox.getSelectedItem());
                stmt.setString(3, (String) locationBox.getSelectedItem());
                stmt.setString(4, (String) statusBox.getSelectedItem());
                stmt.setString(5, meterNumberField.getText());
                stmt.executeUpdate();
                loadMeters();
                if (meterCombo != null) {
                    updateMeterComboBox(meterCombo);
                }
                JOptionPane.showMessageDialog(this, "Meter added successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding meter: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedMeter() {
        int row = metersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a meter to delete.");
            return;
        }

        String meterNo = (String) metersModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete meter " + meterNo + " and all its bills?",
                "Confirm", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.connect()) {
                conn.setAutoCommit(false);
                try {
                    PreparedStatement deleteBillsStmt = conn.prepareStatement(
                            "DELETE FROM bills WHERE meter_id = (SELECT id FROM meters WHERE meter_number = ?)");
                    deleteBillsStmt.setString(1, meterNo);
                    int billsDeleted = deleteBillsStmt.executeUpdate();

                    PreparedStatement deleteMeterStmt = conn.prepareStatement(
                            "DELETE FROM meters WHERE meter_number = ? AND customer_id = ?");
                    deleteMeterStmt.setString(1, meterNo);
                    deleteMeterStmt.setInt(2, customerId);
                    int metersDeleted = deleteMeterStmt.executeUpdate();

                    conn.commit();
                    JOptionPane.showMessageDialog(this,
                            "Deleted " + metersDeleted + " meter and " + billsDeleted + " associated bills");
                    loadMeters();
                    loadBills();
                } catch (SQLException e) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this,
                            "Error deleting meter: " + e.getMessage());
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Database error: " + e.getMessage());
            }
        }
    }
    private void generatePaymentReceipt(Bill bill, String paymentMethod) {
        try {
             String fileName = "Payment_Receipt_" + bill.meterNumber + "_" +
                    LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf";

             JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Payment Receipt");
            fileChooser.setSelectedFile(new File(fileName));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();

                 Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                 com.itextpdf.text.Font titleFont = FontFactory.getFont(HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("PAYMENT RECEIPT", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                 com.itextpdf.text.Font infoFont = FontFactory.getFont(HELVETICA, 12, BaseColor.BLACK);
                Paragraph customerInfo = new Paragraph();
                customerInfo.add(new Phrase("Customer: " + txtFirstName.getText() + " " + txtLastName.getText() + "\n", infoFont));
                customerInfo.add(new Phrase("Address: " + txtDistrict.getText() + ", " + txtSector.getText() + "\n", infoFont));
                customerInfo.add(new Phrase("Phone: " + txtPhone.getText() + "\n\n", infoFont));
                document.add(customerInfo);

                 PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(100);
                table.setSpacingBefore(10);
                table.setSpacingAfter(10);

                addTableHeader(table, "Payment Details", "Value");
                addTableRow(table, "Meter Number", bill.meterNumber);
                addTableRow(table, "Payment Date", LocalDate.now().toString());
                addTableRow(table, "Payment Method", paymentMethod);
                addTableRow(table, "Amount Paid", String.format("RWF%.2f", bill.amount));
                addTableRow(table, "Bill Period", bill.billDate.toString());
                addTableRow(table, "Units Consumed", String.format("%.2f", bill.units));

                document.add(table);

                 Paragraph thanks = new Paragraph("\nThank you for your payment!", infoFont);
                thanks.setAlignment(Element.ALIGN_CENTER);
                document.add(thanks);

                document.close();

                JOptionPane.showMessageDialog(this,
                        "Payment receipt saved as:\n" + file.getAbsolutePath(),
                        "Receipt Generated",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating receipt: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addTableHeader(PdfPTable table, String header1, String header2) {
        com.itextpdf.text.Font headerFont = FontFactory.getFont(HELVETICA_BOLD, 12, BaseColor.WHITE);
        PdfPCell cell;

        cell = new PdfPCell(new Phrase(header1, headerFont));
        cell.setBackgroundColor(new BaseColor(128, 0, 128));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(header2, headerFont));
        cell.setBackgroundColor(new BaseColor(128, 0, 128));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableRow(PdfPTable table, String label, String value) {
        com.itextpdf.text.Font font = FontFactory.getFont(HELVETICA, 12, BaseColor.BLACK);

        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        labelCell.setBackgroundColor(new BaseColor(230, 204, 255));
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        table.addCell(valueCell);
    }
    private void loadBills() {
        billsModel.setRowCount(0);
        customerBills.clear();
        totalAmountDue = 0.0;

        String sql = "SELECT m.id as meter_id, m.meter_number, b.bill_date, b.unit, b.amount, " +
                "CASE WHEN b.payment_status IS NULL OR b.payment_status = 'Unpaid' THEN 'Unpaid' ELSE 'Paid' END as payment_status " +
                "FROM meters m LEFT JOIN bills b ON m.id=b.meter_id " +
                "WHERE m.customer_id=? AND (b.payment_status IS NULL OR b.payment_status = 'Unpaid') " +
                "ORDER BY m.meter_number";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int meterId = rs.getInt("meter_id");
                String meterNumber = rs.getString("meter_number");
                Date billDate = rs.getDate("bill_date");
                double units = rs.getDouble("unit");
                double amount = rs.getDouble("amount");
                String status = rs.getString("payment_status");

                if (billDate != null) {
                    Bill bill = new Bill(meterId, meterNumber, billDate, units, amount, status);
                    customerBills.add(bill);
                    if ("Unpaid".equals(status)) {
                        totalAmountDue += amount;
                    }

                    billsModel.addRow(new Object[]{
                            meterNumber,
                            billDate,
                            units,
                            String.format("%,.0f RWF", amount),
                            status
                    });
                }
            }

            dueDate = LocalDate.now().plusDays(15);
            updateBillingSummary();
            if (meterCombo != null) {
                updatePaymentMeterDropdown();
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + e.getMessage());
        }
    }

    private void updatePaymentMeterDropdown() {
        meterCombo.removeAllItems();
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT m.meter_number " +
                             "FROM meters m JOIN bills b ON m.id = b.meter_id " +
                             "WHERE m.customer_id = ? AND m.status = 'Active' " +
                             "AND (b.payment_status IS NULL OR b.payment_status = 'Unpaid')")) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            boolean hasUnpaidMeters = false;
            while (rs.next()) {
                meterCombo.addItem(rs.getString("meter_number"));
                hasUnpaidMeters = true;
            }

            if (!hasUnpaidMeters) {
                meterCombo.addItem("No unpaid meters found");
                meterCombo.setEnabled(false);
            } else {
                meterCombo.setEnabled(true);
            }
        } catch (SQLException e) {
            meterCombo.addItem("Error loading meters");
            meterCombo.setEnabled(false);
        }
    }

    private void updateBillingSummary() {
        lblCurrentBillAmount.setText(String.format("Current Bill: %,.0f RWF", totalAmountDue));
        lblDueDate.setText("Due Date: " + dueDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        paymentStatus = (totalAmountDue > 0) ? "Unpaid" : "Paid";
        lblPaymentStatus.setText("Payment Status: " + paymentStatus);
    }

    private void showUnitsDialog() {
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, meter_number FROM meters WHERE customer_id=? AND status='Active' AND is_preferred=TRUE")) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            List<Integer> meterIds = new ArrayList<>();
            List<JTextField> unitFields = new ArrayList<>();
            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("Meter Number"));
            panel.add(new JLabel("Units Used"));

            boolean hasPreferredMeters = false;
            while (rs.next()) {
                hasPreferredMeters = true;
                int mId = rs.getInt("id");
                meterIds.add(mId);
                panel.add(new JLabel(rs.getString("meter_number")));
                JTextField unitsField = new JTextField();
                unitFields.add(unitsField);
                panel.add(unitsField);
            }

            if (!hasPreferredMeters) {
                JOptionPane.showMessageDialog(this,
                        "No preferred meters found. Please select meters first.",
                        "No Meters Selected",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int result = JOptionPane.showConfirmDialog(
                    this, panel, "Enter Units for Billing (Selected Meters Only)",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String insertSql = "INSERT INTO bills (meter_id, bill_date, unit, amount, payment_status) " +
                        "VALUES (?, ?, ?, ?, 'Unpaid')";

                try (PreparedStatement insStmt = conn.prepareStatement(insertSql)) {
                    for (int i = 0; i < meterIds.size(); i++) {
                        int mId = meterIds.get(i);
                        double units = Double.parseDouble(unitFields.get(i).getText());
                        double amt = units * UNIT_RATE;

                        insStmt.setInt(1, mId);
                        insStmt.setDate(2, Date.valueOf(LocalDate.now()));
                        insStmt.setDouble(3, units);
                        insStmt.setDouble(4, amt);
                        insStmt.addBatch();
                    }
                    insStmt.executeBatch();

                    JOptionPane.showMessageDialog(this, "Bills generated successfully for preferred meters.");
                    loadBills();
                    loadUsage();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for units",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error entering units: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Bill getLatestBillForMeter(String meterNumber) {
        for (Bill bill : customerBills) {
            if (bill.meterNumber.equals(meterNumber) && bill.status.equals("Unpaid")) {
                return bill;
            }
        }
        return null;
    }

    private boolean showPaymentDetailsDialog(String paymentMethod, double amount) {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        JTextField detailsField = new JTextField();

        switch (paymentMethod) {
            case "Mobile Money":
                panel.add(new JLabel("Enter Mobile Money Number:"));
                break;
            case "Credit Card":
                panel.add(new JLabel("Enter Credit Card Number:"));
                break;
            case "Bank Transfer":
                panel.add(new JLabel("Enter Bank Account Number:"));
                break;
        }

        panel.add(detailsField);
        panel.add(new JLabel("Amount to Pay: RWF" + String.format("%.2f", amount)));

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                paymentMethod + " Payment Details",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String paymentDetails = detailsField.getText().trim();
            if (paymentDetails.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter payment details");
                return false;
            }

            if (paymentMethod.equals("Mobile Money") && !paymentDetails.matches("\\d{10}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 10-digit mobile number");
                return false;
            }

            if (paymentMethod.equals("Credit Card") && !paymentDetails.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid 16-digit card number");
                return false;
            }

            if (paymentMethod.equals("Bank Transfer") && !paymentDetails.matches("\\d{10,20}")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid bank account number (10-20 digits)");
                return false;
            }

            return true;
        }

        return false;
    }
    private void processPayment(Bill bill, String method) {
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE bills SET payment_status='Paid' WHERE meter_id=? AND bill_date=?")) {

            stmt.setInt(1, bill.meterId);
            stmt.setDate(2, bill.billDate);
            stmt.executeUpdate();

            paymentStatus = "Paid";
            updateBillingSummary();
            loadBills();
            String paymentMessage = String.format("Payment of RWF%.2f for meter %s via %s",
                    bill.amount, bill.meterNumber, method);
            addAlert("Payment", paymentMessage);

             generatePaymentReceipt(bill, method);

            JOptionPane.showMessageDialog(this,
                    "Payment of " + String.format("RWF%.2f", bill.amount) +
                            " processed successfully via " + method);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error processing payment: " + ex.getMessage());
        }
    }

    private void addAlert(String type, String message) {
        alertsModel.addRow(new Object[]{
                type,
                message,
                new Timestamp(System.currentTimeMillis())
        });
        alertsTable.scrollRectToVisible(alertsTable.getCellRect(alertsModel.getRowCount()-1, 0, true));
    }

    private void updateMeterComboBox(JComboBox<String> combo) {
        combo.removeAllItems();
        combo.setEnabled(false);
        combo.addItem("Loading meters...");

        new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                List<String> activeMeters = new ArrayList<>();
                String sql = "SELECT meter_number FROM meters " +
                        "WHERE customer_id = ? AND status = 'Active' " +
                        "ORDER BY meter_number ASC";

                try (Connection conn = DBConnection.connect();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, customerId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        activeMeters.add(rs.getString("meter_number"));
                    }
                } catch (SQLException e) {
                    throw new Exception("Database error: " + e.getMessage());
                }
                return activeMeters;
            }

            @Override
            protected void done() {
                try {
                    combo.removeAllItems();
                    List<String> meters = get();
                    if (meters.isEmpty()) {
                        combo.addItem("No active meters available");
                        combo.setEnabled(false);
                    } else {
                        for (String meter : meters) {
                            combo.addItem(meter);
                        }
                        combo.setEnabled(true);
                        if (combo.getSelectedIndex() == -1 && combo.getItemCount() > 0) {
                            combo.setSelectedIndex(0);
                        }
                    }
                } catch (Exception e) {
                    combo.removeAllItems();
                    combo.addItem("Error loading meters");
                    combo.setToolTipText(e.getMessage());
                    combo.setEnabled(false);
                    JOptionPane.showMessageDialog(CustomerDashboard.this,
                            "Failed to load meters: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void loadUsage() {
        usageModel.setRowCount(0);
        String sql = "SELECT bill_date as usage_date, unit FROM bills WHERE meter_id IN " +
                "(SELECT id FROM meters WHERE customer_id=?) ORDER BY bill_date DESC LIMIT 10";
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                usageModel.addRow(new Object[]{
                        rs.getDate("usage_date"),
                        rs.getDouble("unit")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading usage data: " + e.getMessage());
        }
    }

    private void loadAlerts() {
        alertsModel.setRowCount(0);
        alertsModel.addRow(new Object[]{"LogIn", "Welcome to your dashboard", new Timestamp(System.currentTimeMillis())});
        alertsModel.addRow(new Object[]{"Reminder", "Your bill is due soon", new Timestamp(System.currentTimeMillis())});
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CustomerDashboard(1));
    }
}