/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package copl_finals;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import javax.swing.ButtonGroup;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import javax.swing.text.PlainDocument;


/**
 *
 * @author Admin
 */

public class Dashboard extends javax.swing.JFrame {
    
    private JButton lastClickedButton = null;
    private Timer timer;
    
    
    public Dashboard() {
        initComponents();
        refreshData();
        applyNumberFilter(txtAmountWD);
        applyNumberFilter(txtAmountWW);
        applyNumberFilter(txtAmountWT);
        applyNumberFilter(txtStartingMoney);
        applyNumberFilter(txtBalanceGoal);
        startAutoRefresh();
        setLocationRelativeTo(null);
        txtFirstName.setFocusable(false); // Prevent focus
        txtLastName.setFocusable(false); // Prevent focus
        txtTitleMS.setFocusable(false); // Prevent focus
        txtSavingsDetails2.setFocusable(false); // Prevent focus
        txtFirstName.setBackground(Color.LIGHT_GRAY);
        txtLastName.setBackground(Color.LIGHT_GRAY);
        txtTitleMS.setBackground(Color.LIGHT_GRAY);
        txtSavingsDetails2.setBackground(Color.LIGHT_GRAY);
        
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData();
        refreshData2();
        String Fname = dbAccess.getFirstName().substring(0, 1).toUpperCase() + dbAccess.getFirstName().substring(1).toLowerCase();
        String Lname = dbAccess.getLastName().substring(0, 1).toUpperCase() + dbAccess.getLastName().substring(1).toLowerCase();
        txtFirstName.setText(String.format(Fname));
        txtLastName.setText(String.format(Lname));
        
        cmbxMS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedTitle = (String) cmbxMS.getSelectedItem();
                DatabaseAccess.Savings selectedSavings = dbAccess.getSavingsByTitle(selectedTitle);

                if (selectedSavings != null) {
                    lblprogress.setText(selectedSavings.getProgress());
                    txtTitleMS.setText(selectedSavings.getTitle());
                    txtSavingsDetails2.setText(selectedSavings.getSavingsDetails());
                    lblDateCreated.setText(selectedSavings.getDateCreated());
                    lblDateModifiedlMS.setText(selectedSavings.getLastModified());
                    lblCurrentBalanceMS.setText(Double.toString(selectedSavings.getSavingsQty()));
                    lblBalanceGoalMS.setText(Double.toString(selectedSavings.getSavingsGoal()));
                }
                
                    }
           
        });
        
        ButtonGroup group = new ButtonGroup();
        group.add(rbtnDepositMS);
        group.add(rbtnWithdrawMS);

        int initialTabIndex = 4; // Index of the tab you want to show first (0-based index)
        jTabbedPane2.setSelectedIndex(initialTabIndex);
        btnHome.setForeground(Color.WHITE);
        lastClickedButton = btnHome;
        btnHome.setFocusPainted(false);
        btnWallet.setFocusPainted(false);
        btnManageSavings.setFocusPainted(false);
        btnHistory.setFocusPainted(false);
        btnProfile.setFocusPainted(false);
        // Initially hide all indicators
        lblPicIndicatorWallet.setVisible(false);
        lblPicIndicatorManageSavings.setVisible(false);
        lblPicIndicatorHistory.setVisible(false);
        lblPicIndicatorProfile.setVisible(false);
        
        // Define the custom color
        Color customColor = new Color(0, 178, 39);
        Color hoverColor = Color.GRAY;
        Color clickedColor = Color.WHITE;
        Color unclickedColor = Color.BLACK;

        // Apply settings to the buttons
        configureButton(btnHome, customColor, hoverColor, clickedColor, unclickedColor, lblPicIndicatorHome);
        configureButton(btnWallet, customColor, hoverColor, clickedColor, unclickedColor, lblPicIndicatorWallet);
        configureButton(btnManageSavings, customColor, hoverColor, clickedColor, unclickedColor, lblPicIndicatorManageSavings);
        configureButton(btnHistory, customColor, hoverColor, clickedColor, unclickedColor, lblPicIndicatorHistory);
        configureButton(btnProfile, customColor, hoverColor, clickedColor, unclickedColor, lblPicIndicatorProfile);
        configureButton(btnLogout, customColor, hoverColor, clickedColor, unclickedColor, lblPicIndicatorHome);
                // Assuming your search text box is named txtSearch
        txtSearchH.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filterTable();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filterTable();
            }
        });

    }   
    
        private static void applyNumberFilter(JTextField textField) {
        PlainDocument doc = (PlainDocument) textField.getDocument();
        doc.setDocumentFilter(new NumberDocumentFilter());
    }

    private static class NumberDocumentFilter extends DocumentFilter {
        private static final String DECIMAL_REGEX = "\\d*\\.?\\d{0,2}";

        @Override
        public void insertString(FilterBypass fb, int offs, String str, AttributeSet attr) throws BadLocationException {
            String newText = getNewText(fb, offs, str);
            if (newText.matches(DECIMAL_REGEX) && isValidInput(newText)) {
                super.insertString(fb, offs, str, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offs, int length, String str, AttributeSet attr) throws BadLocationException {
            String newText = getNewText(fb, offs, str);
            if (newText.matches(DECIMAL_REGEX) && isValidInput(newText)) {
                super.replace(fb, offs, length, str, attr);
            }
        }

        @Override
        public void remove(FilterBypass fb, int offs, int length) throws BadLocationException {
            super.remove(fb, offs, length);
        }

        private String getNewText(FilterBypass fb, int offs, String str) throws BadLocationException {
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            StringBuilder sb = new StringBuilder(currentText);
            sb.replace(offs, offs + str.length(), str);
            return sb.toString();
        }

        private boolean isValidInput(String text) {
            // Check if the first character is zero
            return text.length() == 0 || text.charAt(0) != '0';
        }
    }
    
    private void updateSavingsQty() {
    String selectedTitle = (String) cmbxMS.getSelectedItem();
    double amount = Double.parseDouble(txtTransactionMS.getText());
    boolean isDeposit = rbtnDepositMS.isSelected();
    
    DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
    dbAccess.retrieveUserData();
    DatabaseAccess.Savings selectedSavings = dbAccess.getSavingsByTitle(selectedTitle);
    
    
    if (isDeposit) {
            if (dbAccess.getBalance() < amount) {
                JOptionPane.showMessageDialog(this, "Insufficient balance to deposit the specified amount.", "Insufficient Balance", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (selectedSavings.getSavingsQty()+amount > selectedSavings.getSavingsGoal()) {
                JOptionPane.showMessageDialog(this, "Exceeding the limit of Goal, Try again.", "Insufficient Savings", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
        } else {
            if (selectedSavings.getSavingsQty() < amount) {
                JOptionPane.showMessageDialog(this, "Insufficient savings to withdraw the specified amount.", "Insufficient Savings", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    
    
    try {
        dbAccess.updateSavingsQty(dbAccess.getAccountNumber(), selectedTitle, amount, isDeposit);
        JOptionPane.showMessageDialog(this, "Savings updated successfully.");
        txtTransactionMS.setText("");
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error updating savings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void filterTable() {
    String searchTerm = txtSearchH.getText().toLowerCase();

    // Create a new table model for filtered data
    DefaultTableModel model = (DefaultTableModel) tblHistory.getModel();
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    tblHistory.setRowSorter(sorter);

    // Define the filter for the table
    RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
        @Override
        public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
            String date = entry.getStringValue(1).toLowerCase(); // Date column
            String transacType = entry.getStringValue(2).toLowerCase(); // Transaction column
            String transacAccount = entry.getStringValue(3).toLowerCase(); // Account column
            String amount = entry.getStringValue(4).toLowerCase(); // Amount column

            return date.contains(searchTerm) || transacType.contains(searchTerm)
                    || transacAccount.contains(searchTerm) || amount.contains(searchTerm);
        }
    };
    sorter.setRowFilter(rf);
}
    
    private void refreshData() {
        if (Login.USERNAME != null && !Login.USERNAME.isEmpty()) {
            // Create DatabaseAccess instance with the username
            DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
            dbAccess.retrieveUserData();
            lblBalanceD.setText(String.format("%.2f", dbAccess.getBalance()));
            lblBalanceW.setText(String.format("%.2f", dbAccess.getBalance()));

            lblEmailP.setText(String.format(dbAccess.getEmail()));
            lblPhoneNumP.setText(String.format(dbAccess.getPhoneNumber()));
            lblAccountNumber.setText(String.format(dbAccess.getAccountNumber()));
            updateTransactionTable();
            updateWithdrawalTable();
            updateDepositTable();
            updateHistoryTable();
        } else {
            lblBalanceD.setText("Username not set.");
        }
    }
    
    private void refreshData2(){
        if (Login.USERNAME != null && !Login.USERNAME.isEmpty()) {
            // Create DatabaseAccess instance with the username
            DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
            dbAccess.retrieveUserData();
            

            // Clear all items from the JComboBox
            cmbxMS.removeAllItems();

            for (DatabaseAccess.Savings savings : dbAccess.getSavingsList()) {
            cmbxMS.addItem(savings.getTitle());
            }
            }
    }
    
    private void startAutoRefresh() {
        // Create a Timer that triggers every 2000 milliseconds (2 seconds)
        timer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
        timer.start(); // Start the timer
    }
    
    private void updateTransactionTable() {
            DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
            dbAccess.retrieveUserData(); // Retrieve user data including transactions

            List<DatabaseAccess.Transaction> transactions = dbAccess.getTransactions();
            transactions.sort((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate())); // Sort in descending order

            // Limit to the latest 5 transactions
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setColumnIdentifiers(new Object[] {
            "Transaction", "Account", "Amount", "Date"
             });
            model.setRowCount(0); // Clear existing rows

            for (int i = 0; i < Math.min(5, transactions.size()); i++) {
                DatabaseAccess.Transaction txn = transactions.get(i);
                model.addRow(new Object[] {
                    txn.getTransacType(),
                    txn.getTransacAccount(),
                    txn.getAmount(),
                    txn.getTransferDate()
                });
            }
            Map<String, Double> totals = calculateTransactionTotals(transactions);
            displayTransactionTotals(totals);
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    }
    
    private void updateWithdrawalTable() {
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData(); // Retrieve user data including transactions

        List<DatabaseAccess.Transaction> transactions = dbAccess.getTransactions();
        
        // Filter for withdrawals
        List<DatabaseAccess.Transaction> withdrawals = new ArrayList<>();
        for (DatabaseAccess.Transaction txn : transactions) {
            if ("Withdraw".equals(txn.getTransacType())) {
                withdrawals.add(txn);
            }
        }

        // Sort withdrawals by transfer date in descending order
        withdrawals.sort((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate()));

        // Limit to the latest 5 withdrawals
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setColumnIdentifiers(new Object[] {
            "Account", "Amount", "Date"
        });
        model.setRowCount(0); // Clear existing rows

        for (int i = 0; i < Math.min(5, withdrawals.size()); i++) {
            DatabaseAccess.Transaction txn = withdrawals.get(i);
            model.addRow(new Object[] {
                txn.getTransacAccount(),
                txn.getAmount(),
                txn.getTransferDate()
            });
        }
        // Adjust column widths to fit content
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
}
    
    private void updateDepositTable() {
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData(); // Retrieve user data including transactions

        List<DatabaseAccess.Transaction> transactions = dbAccess.getTransactions();
        
        // Filter for withdrawals
        List<DatabaseAccess.Transaction> withdrawals = new ArrayList<>();
        for (DatabaseAccess.Transaction txn : transactions) {
            if ("Deposit".equals(txn.getTransacType())) {
                withdrawals.add(txn);
            }
        }

        // Sort withdrawals by transfer date in descending order
        withdrawals.sort((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate()));

        // Limit to the latest 5 withdrawals
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setColumnIdentifiers(new Object[] {
            "Account", "Amount", "Date"
        });
        model.setRowCount(0); // Clear existing rows

        for (int i = 0; i < Math.min(5, withdrawals.size()); i++) {
            DatabaseAccess.Transaction txn = withdrawals.get(i);
            model.addRow(new Object[] {
                txn.getTransacAccount(),
                txn.getAmount(),
                txn.getTransferDate()
            });
        }
        // Adjust column widths to fit content
        jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
}
    private void updateHistoryTable() {
            DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
            dbAccess.retrieveUserData(); // Retrieve user data including transactions

            List<DatabaseAccess.Transaction> transactions = dbAccess.getTransactions();
            transactions.sort((t1, t2) -> t2.getTransferDate().compareTo(t1.getTransferDate())); // Sort in descending order

            // Limit to the latest 5 transactions
            DefaultTableModel model = (DefaultTableModel) tblHistory.getModel();
            model.setColumnIdentifiers(new Object[] {
             "No.", "Date", "Transaction", "Account", "Amount"
             });
            model.setRowCount(0); // Clear existing rows

            for (int i = 0; i < Math.min(75, transactions.size()); i++) {
                DatabaseAccess.Transaction txn = transactions.get(i);
                model.addRow(new Object[] {
                    i + 1,
                    txn.getTransferDate(),
                    txn.getTransacType(),
                    txn.getTransacAccount(),
                    txn.getAmount()
                });
            }
            Map<String, Double> totals = calculateTransactionTotals(transactions);
            displayTransactionTotals(totals);
            jTable1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            filterTable();
    }

    
    private Map<String, Double> calculateTransactionTotals(List<DatabaseAccess.Transaction> transactions) {
        Map<String, Double> totals = new HashMap<>();
        for (DatabaseAccess.Transaction txn : transactions) {
            String type = txn.getTransacType();
            Double amount = Double.parseDouble(txn.getAmount());
            if (totals.containsKey(type)) {
                totals.put(type, totals.get(type) + amount);
            } else {
                totals.put(type, amount);
            }
        }
        return totals;
    }
    
    private void displayTransactionTotals(Map<String, Double> totals) {
    // Set default values if totals are not available
    double totalWithdrawal = totals.getOrDefault("Withdraw", 0.0);
    double totalDeposit = totals.getOrDefault("Deposit", 0.0);
    double totalSend = totals.getOrDefault("Send", 0.0);
    double totalReceive = totals.getOrDefault("Receive", 0.0);

    // Calculate total money spent and received
    double totalMoneySpent = totalSend;
    double totalMoneyReceived = totalReceive;

    // Update labels with the totals
    lblTotalWithdrawal.setText(String.format("%.2f", totalWithdrawal));
    lblTotalDeposit.setText(String.format("%.2f", totalDeposit));
    lblTotalSpent.setText(String.format("%.2f", totalMoneySpent));
    lblTotalReceived.setText(String.format("%.2f", totalMoneyReceived));
}



     private void configureButton(JButton button, Color customColor, Color hoverColor, Color clickedColor, Color unclickedColor, JLabel indicator) {
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(true);
            
        button.setUI(new BasicButtonUI());

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtonColors(button); // Reset colors of other buttons
                button.setForeground(clickedColor); // Set clicked button's text color to white
                lastClickedButton = button; // Update the last clicked button
                showIndicator(indicator);
            }
        });

        Color originalForegroundColor = button.getForeground();

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button == lastClickedButton) {
                    button.setForeground(clickedColor); // Change text color to white if it’s the clicked button
                } else {
                    button.setForeground(hoverColor); // Change text color to gray for other buttons
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button == lastClickedButton) {
                    button.setForeground(clickedColor); // Keep the clicked button’s text color white
                } else {
                    button.setForeground(unclickedColor); // Reset text color to black for other buttons
                }
            }
        });
    }
     
    private void resetButtonColors(JButton clickedButton) {
        JButton[] buttons = {btnHome, btnWallet, btnManageSavings, btnHistory, btnProfile};
        for (JButton button : buttons) {
            if (button != clickedButton) {
                button.setForeground(Color.BLACK); // Set other buttons' text color to black
            }
        }
    }
    
    private void showIndicator(JLabel selectedIndicator) {
        // Hide all indicators
        lblPicIndicatorHome.setVisible(false);
        lblPicIndicatorWallet.setVisible(false);
        lblPicIndicatorManageSavings.setVisible(false);
        lblPicIndicatorHistory.setVisible(false);
        lblPicIndicatorProfile.setVisible(false);

        // Show the selected indicator
        selectedIndicator.setVisible(true);
    }

     
    @SuppressWarnings("unchecked")
    // Add these methods to your Dashboard class

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel9 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        lblBalanceW = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        lblAccountNumber = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        pnlDepositW = new javax.swing.JPanel();
        cmbxDepositFrom = new javax.swing.JComboBox<>();
        jLabel40 = new javax.swing.JLabel();
        rbPmoneyD = new javax.swing.JRadioButton();
        rbEmoneyD = new javax.swing.JRadioButton();
        jLabel41 = new javax.swing.JLabel();
        btnConfirmWD = new javax.swing.JButton();
        txtAmountWD = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jPanel15 = new javax.swing.JPanel();
        pnlWithdrawW = new javax.swing.JPanel();
        cmbxWithdrawTo = new javax.swing.JComboBox<>();
        jLabel47 = new javax.swing.JLabel();
        rbPmoneyW = new javax.swing.JRadioButton();
        rbEmoneyW = new javax.swing.JRadioButton();
        jLabel48 = new javax.swing.JLabel();
        btnConfirmWW = new javax.swing.JButton();
        txtAmountWW = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jPanel17 = new javax.swing.JPanel();
        pnlTransferW = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        btnConfirmWT = new javax.swing.JButton();
        txtAmountWT = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        txtTransferTo = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        jPanel23 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        btnAddSavingsMS = new javax.swing.JButton();
        cmbxMS = new javax.swing.JComboBox<>();
        jPanel14 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        txtTitleMS = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        lblDateModifiedlMS = new javax.swing.JLabel();
        lblCurrentBalanceMS = new javax.swing.JLabel();
        lblBalanceGoalMS = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        txtTransactionMS = new javax.swing.JTextField();
        rbtnDepositMS = new javax.swing.JRadioButton();
        rbtnWithdrawMS = new javax.swing.JRadioButton();
        btnUpdateMS = new javax.swing.JButton();
        btnEnterTransMS = new javax.swing.JButton();
        jLabel45 = new javax.swing.JLabel();
        lblDateCreated = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        lblprogress = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtSavingsDetails2 = new javax.swing.JTextArea();
        jPanel11 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblHistory = new javax.swing.JTable();
        jLabel46 = new javax.swing.JLabel();
        txtSearchH = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        lblPicture = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        txtFirstName = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtLastName = new javax.swing.JTextField();
        btnEditProfile = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        lblEmailP = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        lblPhoneNumP = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jCheckBox1 = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        jPanel32 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        lblTotalWithdrawal = new javax.swing.JLabel();
        jPanel25 = new javax.swing.JPanel();
        lblTotalSpent = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jPanel40 = new javax.swing.JPanel();
        lblTotalDeposit = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        lblTotalReceived = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        jLabel79 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jPanel19 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        lblBalanceD = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jPanel24 = new javax.swing.JPanel();
        lblDate1 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblSavingsAug = new javax.swing.JLabel();
        jPanel20 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        lblSpendingsAug = new javax.swing.JLabel();
        jPanel21 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        lblSavingsSept = new javax.swing.JLabel();
        lblDate2 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        lblSpendingsSept = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtTitleCS = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtSavingsDetails = new javax.swing.JTextArea();
        txtSavingsPass = new javax.swing.JTextField();
        txtStartingMoney = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        btnCreateSavings = new javax.swing.JButton();
        btnCreateSavings1 = new javax.swing.JButton();
        jLabel29 = new javax.swing.JLabel();
        txtBalanceGoal = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        btnHome = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnWallet = new javax.swing.JButton();
        btnManageSavings = new javax.swing.JButton();
        btnHistory = new javax.swing.JButton();
        btnProfile = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        lblPicIndicatorHome = new javax.swing.JLabel();
        lblPicIndicatorWallet = new javax.swing.JLabel();
        lblPicIndicatorManageSavings = new javax.swing.JLabel();
        lblPicIndicatorHistory = new javax.swing.JLabel();
        lblPicIndicatorProfile = new javax.swing.JLabel();

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setSize(new java.awt.Dimension(1075, 586));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel4.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel4.setText("Wallet");

        jPanel7.setBackground(new java.awt.Color(200, 252, 180));
        jPanel7.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 252, 180), 1, true));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel5.setText("Account Number:");

        lblBalanceW.setFont(new java.awt.Font("Segoe UI Black", 1, 40)); // NOI18N
        lblBalanceW.setText("jLabel16");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel8.setText("Your Balance:");

        lblAccountNumber.setFont(new java.awt.Font("Segoe UI Black", 1, 40)); // NOI18N
        lblAccountNumber.setText("jLabel16");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBalanceW, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAccountNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBalanceW)
                    .addComponent(lblAccountNumber))
                .addContainerGap(36, Short.MAX_VALUE))
        );

        jTabbedPane3.setBackground(new java.awt.Color(200, 252, 180));

        pnlDepositW.setBackground(new java.awt.Color(200, 252, 180));
        pnlDepositW.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbxDepositFrom.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Mode of Transaction...", "GCASH", "7-Eleven Physical Store", "Paymaya", "From Other Bank" }));
        pnlDepositW.add(cmbxDepositFrom, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, 812, 47));

        jLabel40.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel40.setText("Deposit to:");
        pnlDepositW.add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, -1, -1));

        rbPmoneyD.setBackground(new java.awt.Color(200, 252, 180));
        rbPmoneyD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbPmoneyD.setText("Physical Money");
        rbPmoneyD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPmoneyDActionPerformed(evt);
            }
        });
        pnlDepositW.add(rbPmoneyD, new org.netbeans.lib.awtextra.AbsoluteConstraints(152, 107, -1, -1));

        rbEmoneyD.setBackground(new java.awt.Color(200, 252, 180));
        rbEmoneyD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbEmoneyD.setText("E-Money");
        pnlDepositW.add(rbEmoneyD, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 107, -1, -1));

        jLabel41.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel41.setText("Amount:");
        pnlDepositW.add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 144, -1, -1));

        btnConfirmWD.setBackground(new java.awt.Color(8, 124, 28));
        btnConfirmWD.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnConfirmWD.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmWD.setText("Confirm");
        btnConfirmWD.setBorder(null);
        btnConfirmWD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmWDActionPerformed(evt);
            }
        });
        pnlDepositW.add(btnConfirmWD, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 235, 224, 48));
        pnlDepositW.add(txtAmountWD, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 170, 777, 47));

        jLabel19.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel19.setText("PHP");
        pnlDepositW.add(jLabel19, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, 47));

        jTabbedPane3.addTab("Deposit", pnlDepositW);

        jPanel15.setBackground(new java.awt.Color(200, 252, 180));

        pnlWithdrawW.setBackground(new java.awt.Color(200, 252, 180));
        pnlWithdrawW.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cmbxWithdrawTo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Select Mode of Transaction...", "GCASH", "Paymaya", "ATM" }));
        pnlWithdrawW.add(cmbxWithdrawTo, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, 812, 47));

        jLabel47.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel47.setText("Withdraw from:");
        pnlWithdrawW.add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 16, -1, -1));

        rbPmoneyW.setBackground(new java.awt.Color(200, 252, 180));
        rbPmoneyW.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbPmoneyW.setText("Physical Money");
        rbPmoneyW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbPmoneyWActionPerformed(evt);
            }
        });
        pnlWithdrawW.add(rbPmoneyW, new org.netbeans.lib.awtextra.AbsoluteConstraints(152, 107, -1, -1));

        rbEmoneyW.setBackground(new java.awt.Color(200, 252, 180));
        rbEmoneyW.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        rbEmoneyW.setText("E-Money");
        pnlWithdrawW.add(rbEmoneyW, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 107, -1, -1));

        jLabel48.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel48.setText("Amount:");
        pnlWithdrawW.add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 144, -1, -1));

        btnConfirmWW.setBackground(new java.awt.Color(8, 124, 28));
        btnConfirmWW.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnConfirmWW.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmWW.setText("Confirm");
        btnConfirmWW.setBorder(null);
        btnConfirmWW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmWWActionPerformed(evt);
            }
        });
        pnlWithdrawW.add(btnConfirmWW, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 235, 224, 48));
        pnlWithdrawW.add(txtAmountWW, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 170, 777, 47));

        jLabel20.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel20.setText("PHP");
        pnlWithdrawW.add(jLabel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, 47));

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 838, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlWithdrawW, javax.swing.GroupLayout.DEFAULT_SIZE, 838, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(pnlWithdrawW, javax.swing.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
        );

        jTabbedPane3.addTab("Withdraw", jPanel15);

        jPanel17.setBackground(new java.awt.Color(200, 252, 180));

        pnlTransferW.setBackground(new java.awt.Color(200, 252, 180));

        jLabel42.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel42.setText("Transfer to:");

        jLabel43.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel43.setText("Amount:");

        btnConfirmWT.setBackground(new java.awt.Color(8, 124, 28));
        btnConfirmWT.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnConfirmWT.setForeground(new java.awt.Color(255, 255, 255));
        btnConfirmWT.setText("Confirm");
        btnConfirmWT.setBorder(null);
        btnConfirmWT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmWTActionPerformed(evt);
            }
        });

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel34.setText("PHP");

        javax.swing.GroupLayout pnlTransferWLayout = new javax.swing.GroupLayout(pnlTransferW);
        pnlTransferW.setLayout(pnlTransferWLayout);
        pnlTransferWLayout.setHorizontalGroup(
            pnlTransferWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransferWLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlTransferWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel42)
                    .addComponent(jLabel43)
                    .addComponent(btnConfirmWT, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(pnlTransferWLayout.createSequentialGroup()
                        .addComponent(jLabel34)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAmountWT, javax.swing.GroupLayout.PREFERRED_SIZE, 777, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtTransferTo))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        pnlTransferWLayout.setVerticalGroup(
            pnlTransferWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTransferWLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel42)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTransferTo, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                .addGap(26, 26, 26)
                .addComponent(jLabel43)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlTransferWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txtAmountWT)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(btnConfirmWT, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(51, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 867, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel17Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pnlTransferW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 305, Short.MAX_VALUE)
            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel17Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(pnlTransferW, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane3.addTab("Transfer", jPanel17);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(1327, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 2171, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 628, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("tab2", jPanel9);

        jPanel23.setBackground(new java.awt.Color(255, 255, 255));

        jLabel21.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel21.setText("Manage Savings");

        btnAddSavingsMS.setBackground(new java.awt.Color(0, 204, 102));
        btnAddSavingsMS.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        btnAddSavingsMS.setForeground(new java.awt.Color(255, 255, 255));
        btnAddSavingsMS.setText("Add Savings");
        btnAddSavingsMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddSavingsMSActionPerformed(evt);
            }
        });

        cmbxMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbxMSActionPerformed(evt);
            }
        });

        jPanel14.setBackground(new java.awt.Color(200, 252, 180));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel33.setText("Savings Title:");

        txtTitleMS.setText("Please Select a Saving.");

        jLabel36.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel36.setText("Balance Goal:");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel37.setText("Savings Details:");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel38.setText("Balance:");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel39.setText("Balance Goal");

        jLabel44.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel44.setText("Date Modified:");

        lblDateModifiedlMS.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDateModifiedlMS.setText("Date");

        lblCurrentBalanceMS.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblCurrentBalanceMS.setText("Balance");

        lblBalanceGoalMS.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblBalanceGoalMS.setText("Goal");

        jLabel53.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel53.setText("Transaction:");

        txtTransactionMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTransactionMSActionPerformed(evt);
            }
        });

        rbtnDepositMS.setText("Deposit");

        rbtnWithdrawMS.setText("Withdraw");
        rbtnWithdrawMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtnWithdrawMSActionPerformed(evt);
            }
        });

        btnUpdateMS.setBackground(new java.awt.Color(8, 124, 28));
        btnUpdateMS.setForeground(new java.awt.Color(255, 255, 255));
        btnUpdateMS.setText("Update");
        btnUpdateMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateMSActionPerformed(evt);
            }
        });

        btnEnterTransMS.setBackground(new java.awt.Color(8, 124, 28));
        btnEnterTransMS.setForeground(new java.awt.Color(255, 255, 255));
        btnEnterTransMS.setText("Enter");
        btnEnterTransMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnterTransMSActionPerformed(evt);
            }
        });

        jLabel45.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel45.setText("Date Created:");

        lblDateCreated.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblDateCreated.setText("Date");

        jLabel12.setText("Status: ");

        lblprogress.setText("Unknown");

        txtSavingsDetails2.setColumns(20);
        txtSavingsDetails2.setRows(5);
        txtSavingsDetails2.setText("Please Select a Saving");
        jScrollPane5.setViewportView(txtSavingsDetails2);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblprogress))
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel14Layout.createSequentialGroup()
                                    .addComponent(txtTransactionMS, javax.swing.GroupLayout.PREFERRED_SIZE, 464, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnEnterTransMS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(rbtnWithdrawMS)
                                                    .addComponent(rbtnDepositMS))
                                                .addComponent(jLabel39))
                                            .addGap(159, 159, 159))))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                                    .addComponent(lblCurrentBalanceMS, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel14Layout.createSequentialGroup()
                                            .addComponent(jLabel45)
                                            .addGap(18, 18, 18)
                                            .addComponent(lblDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel14Layout.createSequentialGroup()
                                            .addComponent(jLabel44)
                                            .addGap(10, 10, 10)
                                            .addComponent(lblDateModifiedlMS, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(27, 27, 27))))
                        .addGroup(jPanel14Layout.createSequentialGroup()
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel37)
                                .addComponent(jLabel33)
                                .addComponent(jLabel36))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtTitleMS)
                                .addGroup(jPanel14Layout.createSequentialGroup()
                                    .addComponent(lblBalanceGoalMS, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addComponent(jScrollPane5))
                            .addGap(11, 11, 11)
                            .addComponent(btnUpdateMS, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(1325, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(lblprogress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel33)
                    .addComponent(txtTitleMS, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel37, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(110, 110, 110))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane5)
                            .addGroup(jPanel14Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnUpdateMS, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27)))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(lblBalanceGoalMS, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDateCreated, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDateModifiedlMS, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel44, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel38, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCurrentBalanceMS, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel53, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTransactionMS, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnEnterTransMS, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(rbtnDepositMS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(rbtnWithdrawMS)
                        .addGap(414, 414, 414)
                        .addComponent(jLabel39, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel23Layout.createSequentialGroup()
                        .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel21, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnAddSavingsMS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1632, Short.MAX_VALUE)
                        .addComponent(cmbxMS, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel21)
                .addGap(18, 18, 18)
                .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cmbxMS)
                    .addComponent(btnAddSavingsMS, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(40, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab3", jPanel10);

        jPanel18.setBackground(new java.awt.Color(255, 255, 255));

        jLabel18.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel18.setText("History");

        tblHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(tblHistory);

        jLabel46.setForeground(new java.awt.Color(102, 102, 102));
        jLabel46.setText("All Transaction History");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 16)); // NOI18N
        jLabel9.setText("Search:");

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 2159, Short.MAX_VALUE)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel18)
                            .addComponent(jLabel46, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtSearchH)))
                .addContainerGap())
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel46)
                .addGap(25, 25, 25)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSearchH, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 441, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jTabbedPane2.addTab("tab4", jPanel11);

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));

        lblPicture.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel13.setText("First Name");

        txtFirstName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtFirstName.setText("jTextField1");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setText("Last Name");

        txtLastName.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txtLastName.setText("jTextField1");

        btnEditProfile.setBackground(new java.awt.Color(0, 204, 102));
        btnEditProfile.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnEditProfile.setForeground(new java.awt.Color(255, 255, 255));
        btnEditProfile.setText("Edit Profile");
        btnEditProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditProfileActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel22.setText("Email Address");

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel23.setText("Your email address is");

        lblEmailP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblEmailP.setText("jLabel24");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel31.setText("Phone Number");

        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel32.setText("Your phone number is");

        lblPhoneNumP.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblPhoneNumP.setText("jLabel24");

        jTextField5.setText("__________________________________________________________________________________________________________________________________________________________________");
        jTextField5.setBorder(null);
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jTextField7.setText("__________________________________________________________________________________________________________________________________________________________________");
        jTextField7.setBorder(null);
        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });

        jTextField8.setText("__________________________________________________________________________________________________________________________________________________________________");
        jTextField8.setBorder(null);
        jTextField8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField8ActionPerformed(evt);
            }
        });

        jCheckBox1.setBackground(new java.awt.Color(255, 255, 255));
        jCheckBox1.setText("Edit Name");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel22)
                                    .addComponent(jLabel31)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(jLabel32)
                                        .addGap(6, 6, 6)
                                        .addComponent(lblPhoneNumP))
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addComponent(jLabel23)
                                        .addGap(6, 6, 6)
                                        .addComponent(lblEmailP))))
                            .addGroup(jPanel12Layout.createSequentialGroup()
                                .addComponent(lblPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel12Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addGroup(jPanel12Layout.createSequentialGroup()
                                                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel13)
                                                    .addComponent(jLabel14))
                                                .addGap(494, 494, 494))
                                            .addComponent(txtLastName, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                                            .addComponent(txtFirstName)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jCheckBox1))))
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 811, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnEditProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 811, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(1354, Short.MAX_VALUE))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 811, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFirstName, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(jLabel14)
                        .addGap(6, 6, 6)
                        .addComponent(txtLastName, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox1)
                        .addGap(30, 30, 30))
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addComponent(lblPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel22)
                .addGap(6, 6, 6)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel23)
                    .addComponent(lblEmailP))
                .addGap(6, 6, 6)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel31)
                .addGap(6, 6, 6)
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(lblPhoneNumP))
                .addGap(12, 12, 12)
                .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(113, 113, 113)
                .addComponent(btnEditProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23))
        );

        jTabbedPane2.addTab("tab5", jPanel12);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel1.setText("Dashboard");

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        jLabel7.setFont(new java.awt.Font("Segoe UI Black", 1, 18)); // NOI18N
        jLabel7.setText("Recent Transactions");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable1.getTableHeader().setResizingAllowed(false);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable1);

        jTabbedPane1.addTab("All", jScrollPane1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jTable2.setRowSelectionAllowed(false);
        jTable2.getTableHeader().setResizingAllowed(false);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(jTable2);

        jTabbedPane1.addTab("Withdraw", jScrollPane2);

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jTabbedPane1.addTab("Deposit", jScrollPane3);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel32.setBackground(new java.awt.Color(200, 252, 180));
        jPanel32.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 252, 180), 1, true));

        jLabel58.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel58.setText("Total Withdrawal");

        lblTotalWithdrawal.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        lblTotalWithdrawal.setText("jLabel30");

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel58)
                    .addComponent(lblTotalWithdrawal, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalWithdrawal)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel25.setBackground(new java.awt.Color(200, 252, 180));
        jPanel25.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 252, 180), 1, true));

        lblTotalSpent.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        lblTotalSpent.setText("jLabel30");

        jLabel60.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel60.setText("Total Money Sent");

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel60)
                    .addComponent(lblTotalSpent, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel60)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalSpent)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        jPanel40.setBackground(new java.awt.Color(200, 252, 180));
        jPanel40.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 252, 180), 1, true));

        lblTotalDeposit.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        lblTotalDeposit.setText("jLabel30");

        jLabel61.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel61.setText("Total Deposit");

        javax.swing.GroupLayout jPanel40Layout = new javax.swing.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel61)
                    .addComponent(lblTotalDeposit, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );
        jPanel40Layout.setVerticalGroup(
            jPanel40Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel40Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel61)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalDeposit)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel42.setBackground(new java.awt.Color(200, 252, 180));
        jPanel42.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 252, 180), 1, true));

        lblTotalReceived.setFont(new java.awt.Font("Segoe UI Black", 1, 30)); // NOI18N
        lblTotalReceived.setText("jLabel30");

        jLabel62.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel62.setText("Total Money Received");

        javax.swing.GroupLayout jPanel42Layout = new javax.swing.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel42Layout.createSequentialGroup()
                        .addComponent(jLabel62)
                        .addGap(0, 46, Short.MAX_VALUE))
                    .addComponent(lblTotalReceived, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel42Layout.setVerticalGroup(
            jPanel42Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel42Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel62)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblTotalReceived)
                .addContainerGap(41, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(53, 53, 53)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel42, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel40, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel40, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel25, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel42, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );

        jLabel79.setFont(new java.awt.Font("Segoe UI", 1, 28)); // NOI18N
        jLabel79.setText("Monthly");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 292, Short.MAX_VALUE)
        );

        jPanel19.setBackground(new java.awt.Color(200, 252, 180));
        jPanel19.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 252, 180), 1, true));

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel16.setText("Your Balance:");

        lblBalanceD.setFont(new java.awt.Font("Segoe UI Black", 1, 40)); // NOI18N
        lblBalanceD.setText("jLabel16");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel16)
                    .addComponent(lblBalanceD, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addGap(27, 27, 27)
                .addComponent(lblBalanceD)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jTextField4.setText("_______________________________________________________________________________________________________");
        jTextField4.setBorder(null);
        jTextField4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField4ActionPerformed(evt);
            }
        });

        jPanel24.setBackground(new java.awt.Color(255, 255, 255));
        jPanel24.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        jPanel24.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        lblDate1.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        lblDate1.setText("September");
        jPanel24.add(lblDate1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 180, 140, -1));

        jPanel5.setBackground(new java.awt.Color(0, 204, 51));

        jLabel2.setBackground(new java.awt.Color(0, 204, 51));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setText("Savings:");

        lblSavingsAug.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSavingsAug.setText("jLabel6");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSavingsAug, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addComponent(lblSavingsAug, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel24.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 280, 40));

        jPanel20.setBackground(new java.awt.Color(255, 51, 0));

        jLabel6.setBackground(new java.awt.Color(0, 204, 51));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setText("Spendings:");

        lblSpendingsAug.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSpendingsAug.setText("jLabel6");

        javax.swing.GroupLayout jPanel20Layout = new javax.swing.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel20Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSpendingsAug, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel20Layout.setVerticalGroup(
            jPanel20Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(lblSpendingsAug, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel24.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, 280, 40));

        jPanel21.setBackground(new java.awt.Color(0, 204, 51));

        jLabel26.setBackground(new java.awt.Color(0, 204, 51));
        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setText("Savings:");

        lblSavingsSept.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSavingsSept.setText("jLabel6");

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSavingsSept, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addComponent(lblSavingsSept, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel24.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 280, 40));

        lblDate2.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        lblDate2.setText("August");
        jPanel24.add(lblDate2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jPanel22.setBackground(new java.awt.Color(255, 51, 0));

        jLabel30.setBackground(new java.awt.Color(0, 204, 51));
        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel30.setText("Spendings:");

        lblSpendingsSept.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblSpendingsSept.setText("jLabel6");

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSpendingsSept, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(57, Short.MAX_VALUE))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
            .addComponent(lblSpendingsSept, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel24.add(jPanel22, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 270, 280, 40));

        jTextField9.setText("______________________________________________________");
        jTextField9.setBorder(null);
        jTextField9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField9ActionPerformed(evt);
            }
        });
        jPanel24.add(jTextField9, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 280, -1));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1)
                    .addComponent(jLabel79)
                    .addComponent(jPanel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.DEFAULT_SIZE, 522, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, 522, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1307, 1307, 1307)
                .addComponent(jPanel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(4, 4, 4)
                                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel79)
                                .addGap(11, 11, 11)
                                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, 367, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(305, 305, 305)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("tab1", jPanel2);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel11.setText("Create Savings");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel10.setText("Title");

        txtSavingsDetails.setColumns(20);
        txtSavingsDetails.setRows(5);
        jScrollPane6.setViewportView(txtSavingsDetails);

        txtSavingsPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSavingsPassActionPerformed(evt);
            }
        });

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setText("Savings Password (Optional)");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setText("Starting Balance");

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setText("Savings Details");

        btnCreateSavings.setBackground(new java.awt.Color(0, 204, 102));
        btnCreateSavings.setForeground(new java.awt.Color(255, 255, 255));
        btnCreateSavings.setText("Create");
        btnCreateSavings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateSavingsActionPerformed(evt);
            }
        });

        btnCreateSavings1.setBackground(new java.awt.Color(0, 204, 102));
        btnCreateSavings1.setForeground(new java.awt.Color(255, 255, 255));
        btnCreateSavings1.setText("Back");
        btnCreateSavings1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateSavings1ActionPerformed(evt);
            }
        });

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setText("Balance Goal");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnCreateSavings1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addGap(352, 352, 352))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnCreateSavings, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTitleCS, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10)
                                    .addComponent(txtSavingsPass, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 1375, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtStartingMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17)
                                    .addComponent(txtBalanceGoal, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel29))))
                        .addGap(26, 26, 26))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jLabel11))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(btnCreateSavings1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtTitleCS, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBalanceGoal, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtSavingsPass, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtStartingMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnCreateSavings, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("tab6", jPanel1);

        getContentPane().add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, -40, 880, 650));

        jPanel3.setBackground(new java.awt.Color(0, 178, 39));
        jPanel3.setForeground(new java.awt.Color(8, 124, 28));

        btnHome.setBackground(new java.awt.Color(0, 178, 39));
        btnHome.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnHome.setText("Home");
        btnHome.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnHome.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnLogout.setBackground(new java.awt.Color(0, 178, 39));
        btnLogout.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnLogout.setText("Logout");
        btnLogout.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnLogout.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnWallet.setBackground(new java.awt.Color(0, 178, 39));
        btnWallet.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnWallet.setText("Wallet");
        btnWallet.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnWallet.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnWallet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnWalletActionPerformed(evt);
            }
        });

        btnManageSavings.setBackground(new java.awt.Color(0, 178, 39));
        btnManageSavings.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnManageSavings.setText("Manage Savings");
        btnManageSavings.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnManageSavings.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnManageSavings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManageSavingsActionPerformed(evt);
            }
        });

        btnHistory.setBackground(new java.awt.Color(0, 178, 39));
        btnHistory.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnHistory.setText("History");
        btnHistory.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnHistory.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnHistory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHistoryActionPerformed(evt);
            }
        });

        btnProfile.setBackground(new java.awt.Color(0, 178, 39));
        btnProfile.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        btnProfile.setText("Profile");
        btnProfile.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        btnProfile.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnProfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProfileActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/PEBANK.png"))); // NOI18N

        lblPicIndicatorHome.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/circleindicator.png"))); // NOI18N

        lblPicIndicatorWallet.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/circleindicator.png"))); // NOI18N

        lblPicIndicatorManageSavings.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/circleindicator.png"))); // NOI18N

        lblPicIndicatorHistory.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/circleindicator.png"))); // NOI18N

        lblPicIndicatorProfile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/circleindicator.png"))); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPicIndicatorHome, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPicIndicatorWallet, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPicIndicatorManageSavings, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPicIndicatorHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPicIndicatorProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnLogout, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnHistory, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnManageSavings, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                            .addComponent(btnHome, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnWallet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnProfile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(31, 31, 31)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(btnHome, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(lblPicIndicatorHome)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(btnWallet, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnManageSavings, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addComponent(lblPicIndicatorWallet)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblPicIndicatorManageSavings)
                        .addGap(10, 10, 10)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(lblPicIndicatorHistory)
                        .addGap(72, 72, 72)
                        .addComponent(lblPicIndicatorProfile))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(btnHistory, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(btnProfile, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 100, Short.MAX_VALUE)
                .addComponent(btnLogout, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        getContentPane().add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 260, 610));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbPmoneyDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPmoneyDActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPmoneyDActionPerformed

    private void jTextField4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField4ActionPerformed

    private void jTextField9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField9ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField9ActionPerformed

    private void rbPmoneyWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbPmoneyWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbPmoneyWActionPerformed

    private void btnEditProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditProfileActionPerformed
        jTabbedPane2.setSelectedIndex(3);
        refreshData2();
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
    dbAccess.retrieveUserData();

    if (jCheckBox1.isSelected()) {
        try {
                        String newFirstName = txtFirstName.getText();
                        String newLastName = txtLastName.getText();
                        dbAccess.updateUserDetails(dbAccess.getAccountNumber(), newFirstName, newLastName);
                        JOptionPane.showMessageDialog(this, 
                                "User details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dbAccess.retrieveUserData();
                        txtFirstName.setText(dbAccess.getFirstName());
                        txtLastName.setText(dbAccess.getLastName());
                        
                        // Set fields to non-editable state
                        txtFirstName.setFocusable(false);
                        txtLastName.setFocusable(false);
                        txtFirstName.setBackground(Color.LIGHT_GRAY);
                        txtLastName.setBackground(Color.LIGHT_GRAY);
                        jCheckBox1.setSelected(false);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, 
                                "Error updating user details: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
    } else {
        JOptionPane.showMessageDialog(null, "Please select the fields to edit.", "Warning", JOptionPane.WARNING_MESSAGE);
    }
    }//GEN-LAST:event_btnEditProfileActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField8ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8ActionPerformed

    private void btnProfileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProfileActionPerformed
        jTabbedPane2.setSelectedIndex(3);
        refreshData2();
    }//GEN-LAST:event_btnProfileActionPerformed

    private void btnHistoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHistoryActionPerformed
        jTabbedPane2.setSelectedIndex(2);
        refreshData2();
    }//GEN-LAST:event_btnHistoryActionPerformed

    private void btnManageSavingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManageSavingsActionPerformed
        jTabbedPane2.setSelectedIndex(1);
        refreshData2();
    }//GEN-LAST:event_btnManageSavingsActionPerformed

    private void btnWalletActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnWalletActionPerformed
        jTabbedPane2.setSelectedIndex(0);
        refreshData2();
    }//GEN-LAST:event_btnWalletActionPerformed

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed

        int confirmed = JOptionPane.showConfirmDialog(
            Dashboard.this, // Parent component
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        if (confirmed == JOptionPane.YES_OPTION) {
            // Perform logout actions here
            
            Login.USERNAME = null;
            // Close the current window
            dispose();

            EventQueue.invokeLater(() -> {
            new Login().setVisible(true);
        });
        }
        // If NO_OPTION, do nothing (the window remains open)

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        jTabbedPane2.setSelectedIndex(4);
        refreshData2();
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnConfirmWDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmWDActionPerformed
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData();
        
        String amountText = txtAmountWD.getText();
        if (amountText.trim().isEmpty() || amountText.trim() == "0") {
            JOptionPane.showMessageDialog(null, "Please enter an amount.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(txtAmountWD.getText());
        if (amount <= 100) {
            JOptionPane.showMessageDialog(null, "The amount must be greater than 100.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String selectedItem = (String) cmbxDepositFrom.getSelectedItem();
        if (selectedItem == "Select Mode of Transaction..." || selectedItem.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select an account to withdraw from.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // Example for deposit
            dbAccess.addTransactionAndUpdateBalance( dbAccess.getAccountNumber(), Login.USERNAME, "Deposit", selectedItem, amount);
            JOptionPane.showMessageDialog(null, "Transaction successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtAmountWD.setText("");
            cmbxDepositFrom.setSelectedItem("Select Mode of Transaction...");
        } catch (NumberFormatException e) {
            // Handle case where amount is not a valid number
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while processing the transaction. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmWDActionPerformed

    private void btnConfirmWWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmWWActionPerformed

        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData();
        
        String amountText = txtAmountWW.getText();
        if (amountText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an amount.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(txtAmountWW.getText());
        String selectedItem = (String) cmbxWithdrawTo.getSelectedItem();
        if (selectedItem == "Select Mode of Transaction..." || selectedItem.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please select an account to withdraw from.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Example for deposit
            dbAccess.addTransactionAndUpdateBalance( dbAccess.getAccountNumber(), Login.USERNAME, "Withdraw", selectedItem, amount);
            JOptionPane.showMessageDialog(null, "Transaction successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtAmountWW.setText("");
            cmbxWithdrawTo.setSelectedItem("Select Mode of Transaction...");
        } catch (NumberFormatException e) {
            // Handle case where amount is not a valid number
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while processing the transaction. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmWWActionPerformed

    private void btnConfirmWTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmWTActionPerformed
       
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData();
        
        String amountText = txtAmountWT.getText();
        if (amountText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter an amount.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double amount = Double.parseDouble(txtAmountWT.getText());
        String accountnumber = txtTransferTo.getText();
        if (accountnumber == "" || accountnumber.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please Enter Recipient Account Number.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (accountnumber == dbAccess.getAccountNumber()) {
            JOptionPane.showMessageDialog(null, "Enter Someone's Account Number", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (dbAccess.getBalance() < amount) {
            JOptionPane.showMessageDialog(null, "Insufficient funds. Please enter a lesser amount.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Example for deposit
            dbAccess.sendMoney( dbAccess.getAccountNumber(), accountnumber, amount);
            JOptionPane.showMessageDialog(null, "Transaction successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtAmountWT.setText("");
            txtTransferTo.setText("");
        } catch (NumberFormatException e) {
            // Handle case where amount is not a valid number
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while processing the transaction. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnConfirmWTActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        if (jCheckBox1.isSelected()) {
            txtFirstName.setFocusable(true);
            txtFirstName.setBackground(Color.WHITE);
            txtLastName.setFocusable(true);
            txtLastName.setBackground(Color.WHITE);
        return;
        } else {
            txtFirstName.setFocusable(false);
            txtFirstName.setBackground(Color.LIGHT_GRAY);
            txtLastName.setFocusable(false);
            txtLastName.setBackground(Color.LIGHT_GRAY);
        }
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void txtSavingsPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSavingsPassActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSavingsPassActionPerformed

    private void rbtnWithdrawMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtnWithdrawMSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtnWithdrawMSActionPerformed

    private void btnAddSavingsMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddSavingsMSActionPerformed
        jTabbedPane2.setSelectedIndex(5);        // TODO add your handling code here:
    }//GEN-LAST:event_btnAddSavingsMSActionPerformed

    private void btnCreateSavings1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateSavings1ActionPerformed
        jTabbedPane2.setSelectedIndex(1);
    }//GEN-LAST:event_btnCreateSavings1ActionPerformed

    private void btnCreateSavingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateSavingsActionPerformed
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData();
        
        String titleText = txtTitleCS.getText();
        if (titleText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a title for your savings", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        for (DatabaseAccess.Savings savings : dbAccess.getSavingsList()) {
            if (titleText.trim() == savings.getTitle()) {
                JOptionPane.showMessageDialog(null, "Please enter a title for your savings", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        String goal = txtBalanceGoal.getText();
        if (goal.trim().isEmpty() || goal.trim() == "0" || goal.trim() == "0." || goal.trim() == "0.0" || goal.trim() == "0.00") {
            JOptionPane.showMessageDialog(null, "Please enter a valid balance goal amount", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String startMoney = txtStartingMoney.getText();
        if (startMoney.trim().isEmpty() || startMoney.trim() == "0" || startMoney.trim() == "0." || startMoney.trim() == "0.0" || startMoney.trim() == "0.00") {
            JOptionPane.showMessageDialog(null, "Please enter a valid starting amount", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String details = txtSavingsDetails.getText();
        if (details.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a details for your savings", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String savingsPass = txtSavingsPass.getText();
        
        double start = Double.parseDouble(txtStartingMoney.getText());
        double bGoals = Double.parseDouble(txtBalanceGoal.getText());
        
        if (bGoals < start) {
            JOptionPane.showMessageDialog(null, "Balance Goal should be higher than the starting balance.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (dbAccess.getBalance() < start) {
            JOptionPane.showMessageDialog(null, "Insufficient funds. Please enter a lesser starting amount.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String progress = "In progress";
        try {
            // Example for deposit
            dbAccess.addSavings( dbAccess.getAccountNumber(), titleText, details, savingsPass, start, bGoals, progress);
            JOptionPane.showMessageDialog(null, "Transaction successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            txtTitleCS.setText("");
            txtSavingsDetails.setText("");
            txtStartingMoney.setText("");
            txtBalanceGoal.setText("");
            txtSavingsPass.setText("");
            refreshData2();
        } catch (NumberFormatException e) {
            // Handle case where amount is not a valid number
            JOptionPane.showMessageDialog(null, "Invalid amount entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "An error occurred while processing the transaction. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCreateSavingsActionPerformed

    private void txtTransactionMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTransactionMSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTransactionMSActionPerformed

    private void btnEnterTransMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnterTransMSActionPerformed
        updateSavingsQty();
        DatabaseAccess dbAccess = new DatabaseAccess(Login.USERNAME);
        dbAccess.retrieveUserData();
                String selectedTitle = (String) cmbxMS.getSelectedItem();
                DatabaseAccess.Savings selectedSavings = dbAccess.getSavingsByTitle(selectedTitle);

                if (selectedSavings != null) {
                    lblprogress.setText(selectedSavings.getProgress());
                    txtTitleMS.setText(selectedSavings.getSavingsDetails());
                    txtSavingsDetails2.setText(selectedSavings.getSavingsDetails());
                    lblDateCreated.setText(selectedSavings.getDateCreated());
                    lblDateModifiedlMS.setText(selectedSavings.getLastModified());
                    lblCurrentBalanceMS.setText(Double.toString(selectedSavings.getSavingsQty()));
                    lblBalanceGoalMS.setText(Double.toString(selectedSavings.getSavingsGoal()));
                }
    }//GEN-LAST:event_btnEnterTransMSActionPerformed

    private void cmbxMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbxMSActionPerformed
        
    }//GEN-LAST:event_cmbxMSActionPerformed

    private void btnUpdateMSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateMSActionPerformed
        
    }//GEN-LAST:event_btnUpdateMSActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddSavingsMS;
    private javax.swing.JButton btnConfirmWD;
    private javax.swing.JButton btnConfirmWT;
    private javax.swing.JButton btnConfirmWW;
    private javax.swing.JButton btnCreateSavings;
    private javax.swing.JButton btnCreateSavings1;
    private javax.swing.JButton btnEditProfile;
    private javax.swing.JButton btnEnterTransMS;
    private javax.swing.JButton btnHistory;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnManageSavings;
    private javax.swing.JButton btnProfile;
    private javax.swing.JButton btnUpdateMS;
    private javax.swing.JButton btnWallet;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cmbxDepositFrom;
    private javax.swing.JComboBox<String> cmbxMS;
    private javax.swing.JComboBox<String> cmbxWithdrawTo;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel40;
    private javax.swing.JPanel jPanel42;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JLabel lblAccountNumber;
    private javax.swing.JLabel lblBalanceD;
    private javax.swing.JLabel lblBalanceGoalMS;
    private javax.swing.JLabel lblBalanceW;
    private javax.swing.JLabel lblCurrentBalanceMS;
    private javax.swing.JLabel lblDate1;
    private javax.swing.JLabel lblDate2;
    private javax.swing.JLabel lblDateCreated;
    private javax.swing.JLabel lblDateModifiedlMS;
    private javax.swing.JLabel lblEmailP;
    private javax.swing.JLabel lblPhoneNumP;
    private javax.swing.JLabel lblPicIndicatorHistory;
    private javax.swing.JLabel lblPicIndicatorHome;
    private javax.swing.JLabel lblPicIndicatorManageSavings;
    private javax.swing.JLabel lblPicIndicatorProfile;
    private javax.swing.JLabel lblPicIndicatorWallet;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JLabel lblSavingsAug;
    private javax.swing.JLabel lblSavingsSept;
    private javax.swing.JLabel lblSpendingsAug;
    private javax.swing.JLabel lblSpendingsSept;
    private javax.swing.JLabel lblTotalDeposit;
    private javax.swing.JLabel lblTotalReceived;
    private javax.swing.JLabel lblTotalSpent;
    private javax.swing.JLabel lblTotalWithdrawal;
    private javax.swing.JLabel lblprogress;
    private javax.swing.JPanel pnlDepositW;
    private javax.swing.JPanel pnlTransferW;
    private javax.swing.JPanel pnlWithdrawW;
    private javax.swing.JRadioButton rbEmoneyD;
    private javax.swing.JRadioButton rbEmoneyW;
    private javax.swing.JRadioButton rbPmoneyD;
    private javax.swing.JRadioButton rbPmoneyW;
    private javax.swing.JRadioButton rbtnDepositMS;
    private javax.swing.JRadioButton rbtnWithdrawMS;
    private javax.swing.JTable tblHistory;
    private javax.swing.JTextField txtAmountWD;
    private javax.swing.JTextField txtAmountWT;
    private javax.swing.JTextField txtAmountWW;
    private javax.swing.JTextField txtBalanceGoal;
    private javax.swing.JTextField txtFirstName;
    private javax.swing.JTextField txtLastName;
    private javax.swing.JTextArea txtSavingsDetails;
    private javax.swing.JTextArea txtSavingsDetails2;
    private javax.swing.JTextField txtSavingsPass;
    private javax.swing.JTextField txtSearchH;
    private javax.swing.JTextField txtStartingMoney;
    private javax.swing.JTextField txtTitleCS;
    private javax.swing.JTextField txtTitleMS;
    private javax.swing.JTextField txtTransactionMS;
    private javax.swing.JTextField txtTransferTo;
    // End of variables declaration//GEN-END:variables
}
