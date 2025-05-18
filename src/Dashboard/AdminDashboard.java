package Dashboard;

import auth.AdminLoginForm;
import db.DBConnection;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboard extends JFrame {
    private String username;
    private JTable customersTable, billsTable, districtTable;
    private DefaultTableModel customersModel, billsModel, districtModel;
    private List<Customer> allCustomers = new ArrayList<>();
    private List<Bill> allBills = new ArrayList<>();
    private JPanel mainPanel;
    private JPanel resultsPanel;
    private JTabbedPane tabbedPane;

    public AdminDashboard(String username) {
        this.username = username;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Admin Dashboard - Electricity Billing System");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        Color purple = new Color(128, 0, 128);
        Color lightPurple = new Color(230, 204, 255);
        getContentPane().setBackground(lightPurple);


        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(lightPurple);


        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(250, getHeight()));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));


        JLabel welcomeLabel = new JLabel("Welcome, " + username);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(purple);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        leftPanel.add(welcomeLabel);


        String[] buttonLabels = {
                "View All Customers",
                "View All Bills",
                "View By District",
                "Add New Customer",
                "Delete Customer",
                "Edit My Profile",
                "Generate All Report",
                "Generate Customer Report"
        };

        for (String label : buttonLabels) {
            JButton button = createPurpleButton(label);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(220, 40));
            button.setMargin(new Insets(10, 20, 10, 20));

            switch (label) {
                case "View All Customers":
                    button.addActionListener(e -> showCustomersView());
                    break;
                case "View All Bills":
                    button.addActionListener(e -> showBillsView());
                    break;
                case "View By District":
                    button.addActionListener(e -> showDistrictSelection());
                    break;
                case "Add New Customer":
                    button.addActionListener(e -> showAddCustomerDialog());
                    break;
                case "Delete Customer":
                    button.addActionListener(e -> deleteSelectedCustomer());
                    break;
                case "Edit My Profile":
                    button.addActionListener(e -> showEditProfileDialog());
                    break;
                case "Generate All Report":
                    button.addActionListener(e -> generateAllCustomersReport());
                    break;
                case "Generate Customer Report":
                    button.addActionListener(e -> generateCustomerReport());
                    break;
            }

            leftPanel.add(button);
            leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }


        resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(Color.WHITE);
        resultsPanel.setVisible(false);

        tabbedPane = new JTabbedPane();

        customersModel = new DefaultTableModel(new String[]{"ID", "Name", "District", "Sector", "Phone"}, 0);
        customersTable = new JTable(customersModel);
        styleTable(customersTable, purple, lightPurple);
        tabbedPane.addTab("Customers", new JScrollPane(customersTable));

        billsModel = new DefaultTableModel(new String[]{"Bill ID", "Customer", "Meter", "Date", "Units", "Amount", "Status"}, 0);
        billsTable = new JTable(billsModel);
        styleTable(billsTable, purple, lightPurple);
        tabbedPane.addTab("Bills", new JScrollPane(billsTable));

        districtModel = new DefaultTableModel(new String[]{"ID", "Name", "District", "Sector", "Phone", "Meter Number"}, 0);
        districtTable = new JTable(districtModel);
        styleTable(districtTable, purple, lightPurple);
        tabbedPane.addTab("District View", new JScrollPane(districtTable));

        JButton closeButton = createPurpleButton("Close");
        closeButton.addActionListener(e -> showInitialView());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(closeButton);

        resultsPanel.add(tabbedPane, BorderLayout.CENTER);
        resultsPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(resultsPanel, BorderLayout.CENTER);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(purple);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        logoutBtn.addActionListener(e -> {
            new AdminLoginForm().setVisible(true);
            dispose();
        });

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(logoutBtn);

        add(topPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JButton createPurpleButton(String text) {
        Color purple = new Color(128, 0, 128);
        JButton button = new JButton(text);
        button.setBackground(purple);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void styleTable(JTable table, Color headerColor, Color rowColor) {
        table.setBackground(rowColor);
        table.setSelectionBackground(headerColor);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setGridColor(headerColor);
        table.getTableHeader().setBackground(headerColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
    }

    private void showInitialView() {
        resultsPanel.setVisible(false);
        revalidate();
        repaint();
    }

    private void showResultsView() {
        resultsPanel.setVisible(true);
        revalidate();
        repaint();
    }

    private void showCustomersView() {
        loadAllCustomers();
        tabbedPane.setSelectedIndex(0);
        showResultsView();
    }

    private void showBillsView() {
        loadAllBills();
        tabbedPane.setSelectedIndex(1);
        showResultsView();
    }

    private void showDistrictSelection() {
        String[] districts = {"Gasabo", "Kicukiro", "Nyarugenge"};
        String selectedDistrict = (String) JOptionPane.showInputDialog(
                this,
                "Select District:",
                "View Customers by District",
                JOptionPane.PLAIN_MESSAGE,
                null,
                districts,
                districts[0]);

        if (selectedDistrict != null) {
            loadCustomersByDistrict(selectedDistrict);
            tabbedPane.setSelectedIndex(2);
            showResultsView();
        }
    }

    private void loadCustomersByDistrict(String district) {
        districtModel.setRowCount(0);

        String sql = "SELECT c.id, c.first_name, c.last_name, c.district, c.sector, c.phone, m.meter_number " +
                "FROM customers c LEFT JOIN meters m ON c.id = m.customer_id " +
                "WHERE c.district = ?";

        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, district);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String custDistrict = rs.getString("district");
                String sector = rs.getString("sector");
                String phone = rs.getString("phone");
                String meterNumber = rs.getString("meter_number");

                districtModel.addRow(new Object[]{
                        id,
                        firstName + " " + lastName,
                        custDistrict,
                        sector,
                        phone,
                        meterNumber != null ? meterNumber : "No meter"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading district data: " + e.getMessage());
        }
    }

    private void showEditProfileDialog() {
        try (Connection conn = DBConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT username, password FROM admins WHERE username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                JTextField usernameField = new JTextField(rs.getString("username"));
                JPasswordField passwordField = new JPasswordField();
                JPasswordField confirmPasswordField = new JPasswordField();

                JPanel panel = new JPanel(new GridLayout(0, 1));
                panel.add(new JLabel("Username:"));
                panel.add(usernameField);
                panel.add(new JLabel("New Password (leave blank to keep current):"));
                panel.add(passwordField);
                panel.add(new JLabel("Confirm Password:"));
                panel.add(confirmPasswordField);

                int result = JOptionPane.showConfirmDialog(this, panel, "Edit My Profile",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                if (result == JOptionPane.OK_OPTION) {
                     if (!new String(passwordField.getPassword()).isEmpty() &&
                            !new String(passwordField.getPassword()).equals(new String(confirmPasswordField.getPassword()))) {
                        JOptionPane.showMessageDialog(this, "Passwords do not match!");
                        return;
                    }

                     String updateQuery = "UPDATE admins SET username = ?" +
                            (!new String(passwordField.getPassword()).isEmpty() ? ", password = ? " : "") +
                            "WHERE username = ?";

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, usernameField.getText());

                        if (!new String(passwordField.getPassword()).isEmpty()) {
                            updateStmt.setString(2, new String(passwordField.getPassword()));
                            updateStmt.setString(3, username);
                        } else {
                            updateStmt.setString(2, username);
                        }

                        int rows = updateStmt.executeUpdate();
                        if (rows > 0) {
                            JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                             if (!usernameField.getText().equals(username)) {
                                username = usernameField.getText();
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage());
        }
    }

    private void loadAllCustomers() {
        allCustomers.clear();
        customersModel.setRowCount(0);

        String sql = "SELECT id, first_name, last_name, district, sector, phone FROM customers";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String district = rs.getString("district");
                String sector = rs.getString("sector");
                String phone = rs.getString("phone");

                allCustomers.add(new Customer(id, firstName, lastName, district, sector, phone));
                customersModel.addRow(new Object[]{id, firstName + " " + lastName, district, sector, phone});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading customers: " + e.getMessage());
        }
    }

    private void loadAllBills() {
        allBills.clear();
        billsModel.setRowCount(0);

        String sql = "SELECT b.id, c.first_name, c.last_name, m.meter_number, b.bill_date, " +
                "b.unit, b.amount, b.payment_status " +
                "FROM bills b " +
                "JOIN meters m ON b.meter_id = m.id " +
                "JOIN customers c ON m.customer_id = c.id";

        try (Connection conn = DBConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String customerName = rs.getString("first_name") + " " + rs.getString("last_name");
                String meterNumber = rs.getString("meter_number");
                Date billDate = rs.getDate("bill_date");
                double units = rs.getDouble("unit");
                double amount = rs.getDouble("amount");
                String status = rs.getString("payment_status");

                allBills.add(new Bill(id, customerName, meterNumber, billDate, units, amount, status));
                billsModel.addRow(new Object[]{
                        id, customerName, meterNumber, billDate, units, amount, status
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bills: " + e.getMessage());
        }
    }

    private void showAddCustomerDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JComboBox<String> districtField = new JComboBox<>(new String[]{"Gasabo", "Kicukiro", "Nyarugenge"});
        JTextField sectorField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel("District:"));
        panel.add(districtField);
        panel.add(new JLabel("Sector:"));
        panel.add(sectorField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Customer",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DBConnection.connect();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO customers (first_name, last_name, district, sector, phone, password) " +
                                 "VALUES (?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, firstNameField.getText());
                stmt.setString(2, lastNameField.getText());
                stmt.setString(3, (String)districtField.getSelectedItem());
                stmt.setString(4, sectorField.getText());
                stmt.setString(5, phoneField.getText());
                stmt.setString(6, new String(passwordField.getPassword()));

                int rows = stmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(this, "Customer added successfully");
                    loadAllCustomers();
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error adding customer: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedCustomer() {
        if (customersTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first");
            showCustomersView();
            return;
        }

        int row = customersTable.getSelectedRow();
        int customerId = (int) customersModel.getValueAt(row, 0);
        String customerName = (String) customersModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete customer " + customerName + " and all their meters and bills?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DBConnection.connect()) {
                conn.setAutoCommit(false);

                try {

                    PreparedStatement deleteBillsStmt = conn.prepareStatement(
                            "DELETE FROM bills WHERE meter_id IN " +
                                    "(SELECT id FROM meters WHERE customer_id = ?)");
                    deleteBillsStmt.setInt(1, customerId);
                    int billsDeleted = deleteBillsStmt.executeUpdate();


                    PreparedStatement deleteMetersStmt = conn.prepareStatement(
                            "DELETE FROM meters WHERE customer_id = ?");
                    deleteMetersStmt.setInt(1, customerId);
                    int metersDeleted = deleteMetersStmt.executeUpdate();


                    PreparedStatement deleteCustomerStmt = conn.prepareStatement(
                            "DELETE FROM customers WHERE id = ?");
                    deleteCustomerStmt.setInt(1, customerId);
                    int customersDeleted = deleteCustomerStmt.executeUpdate();

                    conn.commit();

                    JOptionPane.showMessageDialog(this,
                            "Deleted " + customersDeleted + " customer, " +
                                    metersDeleted + " meters, and " + billsDeleted + " bills");

                    loadAllCustomers();
                    loadAllBills();
                } catch (SQLException e) {
                    conn.rollback();
                    throw e;
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Error deleting customer: " + e.getMessage());
            }
        }
    }

    private void generateAllCustomersReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Comprehensive Customers Report");
        fileChooser.setSelectedFile(new File("Customers_Report_" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();


                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Comprehensive Customers Report", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.BLACK);
                Paragraph header = new Paragraph("Generated on: " +
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")), headerFont);
                header.setAlignment(Element.ALIGN_RIGHT);
                header.setSpacingAfter(20);
                document.add(header);

                try (Connection conn = DBConnection.connect()) {

                    String customerSql = "SELECT id, first_name, last_name, district, sector, phone FROM customers";
                    PreparedStatement customerStmt = conn.prepareStatement(customerSql);
                    ResultSet customerRs = customerStmt.executeQuery();

                    com.itextpdf.text.Font customerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.DARK_GRAY);
                    com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.BLACK);

                    while (customerRs.next()) {

                        int customerId = customerRs.getInt("id");
                        String customerName = customerRs.getString("first_name") + " " + customerRs.getString("last_name");
                        String district = customerRs.getString("district");
                        String sector = customerRs.getString("sector");
                        String phone = customerRs.getString("phone");

                        Paragraph customerHeader = new Paragraph("Customer: " + customerName, customerFont);
                        customerHeader.setSpacingAfter(5);
                        document.add(customerHeader);


                        Paragraph customerDetails = new Paragraph();
                        customerDetails.add(new Phrase("District: " + district + "\n", dataFont));
                        customerDetails.add(new Phrase("Sector: " + sector + "\n", dataFont));
                        customerDetails.add(new Phrase("Phone: " + phone + "\n\n", dataFont));
                        document.add(customerDetails);


                        String meterSql = "SELECT meter_number, meter_type, location FROM meters WHERE customer_id = ?";
                        PreparedStatement meterStmt = conn.prepareStatement(meterSql);
                        meterStmt.setInt(1, customerId);
                        ResultSet meterRs = meterStmt.executeQuery();

                        boolean hasMeters = false;
                        while (meterRs.next()) {
                            hasMeters = true;
                            String meterNumber = meterRs.getString("meter_number");
                            String meterType = meterRs.getString("meter_type");
                            String location = meterRs.getString("location");

                            Paragraph meterInfo = new Paragraph();
                            meterInfo.add(new Phrase("Meter: " + meterNumber + " (" + meterType + ")\n", dataFont));
                            meterInfo.add(new Phrase("Location: " + location + "\n\n", dataFont));
                            document.add(meterInfo);

                            String billSql = "SELECT bill_date, unit, amount, payment_status " +
                                    "FROM bills WHERE meter_id = " +
                                    "(SELECT id FROM meters WHERE meter_number = ?) " +
                                    "ORDER BY bill_date DESC";
                            PreparedStatement billStmt = conn.prepareStatement(billSql);
                            billStmt.setString(1, meterNumber);
                            ResultSet billRs = billStmt.executeQuery();

                            if (billRs.next()) {
                                 PdfPTable billTable = new PdfPTable(4);
                                billTable.setWidthPercentage(100);
                                billTable.setSpacingBefore(10);
                                billTable.setSpacingAfter(20);


                                addTableHeader(billTable, "Date");
                                addTableHeader(billTable, "Units");
                                addTableHeader(billTable, "Amount");
                                addTableHeader(billTable, "Status");


                                do {
                                    billTable.addCell(new Phrase(billRs.getDate("bill_date").toString(), dataFont));
                                    billTable.addCell(new Phrase(String.format("%.2f", billRs.getDouble("unit")), dataFont));
                                    billTable.addCell(new Phrase(String.format("$%.2f", billRs.getDouble("amount")), dataFont));

                                    String status = billRs.getString("payment_status");
                                    Phrase statusPhrase = new Phrase(status, dataFont);
                                    if ("Paid".equals(status)) {
                                        statusPhrase.getFont().setColor(BaseColor.BLACK);
                                    } else {
                                        statusPhrase.getFont().setColor(BaseColor.RED);
                                    }
                                    billTable.addCell(statusPhrase);
                                } while (billRs.next());

                                document.add(billTable);
                            } else {
                                document.add(new Paragraph("No bills found for this meter\n", dataFont));
                            }
                        }

                        if (!hasMeters) {
                            document.add(new Paragraph("No meters registered for this customer\n", dataFont));
                        }


                        document.add(new Paragraph("\n----------------------------------------\n"));
                    }
                }

                document.close();
                JOptionPane.showMessageDialog(this,
                        "Comprehensive report generated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error generating report: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void addTableHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setBackgroundColor(new BaseColor(128, 0, 128));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        table.addCell(cell);
    }
    private void generateCustomerReport() {
        if (customersTable.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a customer first");
            showCustomersView();
            return;
        }

        int row = customersTable.getSelectedRow();
        int customerId = (int) customersModel.getValueAt(row, 0);
        String customerName = (String) customersModel.getValueAt(row, 1);

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Customer Report");
        fileChooser.setSelectedFile(new File("Customer_Report_" + customerName.replace(" ", "_") +
                "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();


                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
                Paragraph title = new Paragraph("Customer Report: " + customerName, titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);


                com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);
                Customer customer = allCustomers.stream()
                        .filter(c -> c.id == customerId)
                        .findFirst()
                        .orElse(null);

                if (customer != null) {
                    Paragraph info = new Paragraph();
                    info.add(new Phrase("Name: " + customer.firstName + " " + customer.lastName + "\n", infoFont));
                    info.add(new Phrase("Address: " + customer.district + ", " + customer.sector + "\n", infoFont));
                    info.add(new Phrase("Phone: " + customer.phone + "\n\n", infoFont));
                    document.add(info);
                }


                document.add(new Paragraph("Meters:", infoFont));

                PdfPTable metersTable = new PdfPTable(3);
                metersTable.setWidthPercentage(100);

                String[] meterHeaders = {"Meter Number", "Type", "Location"};
                for (String header : meterHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setBackgroundColor(new BaseColor(128, 0, 128));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(5);
                    metersTable.addCell(cell);
                }


                try (Connection conn = DBConnection.connect();
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT meter_number, meter_type, location FROM meters WHERE customer_id = ?")) {

                    stmt.setInt(1, customerId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        metersTable.addCell(rs.getString("meter_number"));
                        metersTable.addCell(rs.getString("meter_type"));
                        metersTable.addCell(rs.getString("location"));
                    }
                }

                document.add(metersTable);
                document.add(Chunk.NEWLINE);


                document.add(new Paragraph("Bills:", infoFont));

                PdfPTable billsTable = new PdfPTable(5);
                billsTable.setWidthPercentage(100);

                String[] billHeaders = {"Date", "Meter", "Units", "Amount", "Status"};
                for (String header : billHeaders) {
                    PdfPCell cell = new PdfPCell(new Phrase(header));
                    cell.setBackgroundColor(new BaseColor(128, 0, 128));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setPadding(5);
                    billsTable.addCell(cell);
                }

                try (Connection conn = DBConnection.connect();
                     PreparedStatement stmt = conn.prepareStatement(
                             "SELECT b.bill_date, m.meter_number, b.unit, b.amount, b.payment_status " +
                                     "FROM bills b JOIN meters m ON b.meter_id = m.id " +
                                     "WHERE m.customer_id = ? ORDER BY b.bill_date DESC")) {

                    stmt.setInt(1, customerId);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        billsTable.addCell(rs.getDate("bill_date").toString());
                        billsTable.addCell(rs.getString("meter_number"));
                        billsTable.addCell(String.format("%.2f", rs.getDouble("unit")));
                        billsTable.addCell(String.format("$%.2f", rs.getDouble("amount")));
                        billsTable.addCell(rs.getString("payment_status"));
                    }
                }

                document.add(billsTable);
                document.close();

                JOptionPane.showMessageDialog(this,
                        "Report generated successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Error generating report: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static class Customer {
        int id;
        String firstName, lastName, district, sector, phone;

        public Customer(int id, String firstName, String lastName, String district, String sector, String phone) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.district = district;
            this.sector = sector;
            this.phone = phone;
        }
    }

    private static class Bill {
        int id;
        String customerName, meterNumber, status;
        Date billDate;
        double units, amount;

        public Bill(int id, String customerName, String meterNumber, Date billDate, double units, double amount, String status) {
            this.id = id;
            this.customerName = customerName;
            this.meterNumber = meterNumber;
            this.billDate = billDate;
            this.units = units;
            this.amount = amount;
            this.status = status;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard("admin").setVisible(true));
    }
}