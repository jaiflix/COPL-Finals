/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package copl_finals;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.swing.Timer;


public class DatabaseAccess {
    private Timer timer;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/copl_finals"; // Replace with your database URL
    private static final String DB_USER = "root"; // Replace with your database username
    private static final String DB_PASSWORD = "pass0403"; // Replace with your database password
    
    private String username; // Instance variable for username

    // Variables to hold retrieved data
    private String accountNumber;
    private double balance;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String birthDate;
    private int age;
    private String gender;
    private String nationality;
    private String password;
    private String status;
    

    // List to hold transaction data
    private List<Transaction> transactions = new ArrayList<>();
    
    private List<Savings> savingsList = new ArrayList<>();

    // Constructor to initialize username
    public DatabaseAccess(String username) {
        this.username = username;
    }
    
    // Public method to retrieve user data and populate variables
    public void retrieveUserData() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Retrieve data from tb_userbalance
            String balanceQuery = "SELECT accountnumber, balance FROM tb_userbalance WHERE username = ?";
            try (PreparedStatement balanceStmt = connection.prepareStatement(balanceQuery)) {
                balanceStmt.setString(1, username);
                try (ResultSet rs = balanceStmt.executeQuery()) {
                    if (rs.next()) {
                        accountNumber = rs.getString("accountnumber");
                        balance = rs.getDouble("balance");
                    }
                }
            }

            // Retrieve data from tb_userdetails
            String userDetailsQuery = "SELECT firstname, lastname, email, phonenumber, birthdate, age, gender, nationality, password, status FROM tb_userdetails WHERE username = ?";
            try (PreparedStatement userDetailsStmt = connection.prepareStatement(userDetailsQuery)) {
                userDetailsStmt.setString(1, username);
                try (ResultSet rs = userDetailsStmt.executeQuery()) {
                    if (rs.next()) {
                        firstName = rs.getString("firstname");
                        lastName = rs.getString("lastname");
                        email = rs.getString("email");
                        phoneNumber = rs.getString("phonenumber");
                        birthDate = rs.getString("birthdate");
                        age = rs.getInt("age");
                        gender = rs.getString("gender");
                        nationality = rs.getString("nationality");
                        password = rs.getString("password");
                        status = rs.getString("status");
                    }
                }
            }

            // Retrieve data from tb_transaction
            String transactionQuery = "SELECT transactionid, accountnumber, transac_type, transac_account, amount, transfer_date FROM tb_transaction WHERE username = ?";
            try (PreparedStatement transactionStmt = connection.prepareStatement(transactionQuery)) {
                transactionStmt.setString(1, username);
                try (ResultSet rs = transactionStmt.executeQuery()) {
                    transactions.clear(); // Clear previous transactions
                    while (rs.next()) {
                        int transactionId = rs.getInt("transactionid");
                        String accountNum = rs.getString("accountnumber");
                        String transacType = rs.getString("transac_type");
                        String transacAccount = rs.getString("transac_account");
                        String amount = rs.getString("amount");
                        String transferDate = rs.getString("transfer_date");

                        // Add transaction to the list
                        transactions.add(new Transaction(transactionId, accountNum, transacType, transacAccount, amount, transferDate));
                    }
                }
            }

            // Retrieve data from tb_savingsmanagement
            String savingsQuery = "SELECT savingsid, accountnumber, title, savings_details, savings_password, savings_qty, savings_goal, progress, date_created, last_modified FROM tb_savingsmanagement WHERE accountnumber = ?";
            try (PreparedStatement savingsStmt = connection.prepareStatement(savingsQuery)) {
                savingsStmt.setString(1, accountNumber);
                try (ResultSet rs = savingsStmt.executeQuery()) {
                    savingsList.clear(); // Clear previous savings
                    while (rs.next()) {
                        int savingsId = rs.getInt("savingsid");
                        String title = rs.getString("title");
                        String savingsDetails = rs.getString("savings_details");
                        String savingsPassword = rs.getString("savings_password");
                        double savingsQty = rs.getDouble("savings_qty");
                        double savingsGoal = rs.getDouble("savings_goal");
                        String progress = rs.getString("progress");
                        String dateCreated = rs.getString("date_created");
                        String lastModified = rs.getString("last_modified");

                        // Add savings to the list
                        savingsList.add(new Savings(savingsId, accountNumber, title, savingsDetails, savingsPassword, savingsQty, savingsGoal, progress, dateCreated, lastModified));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
   public void updateUserDetails(String accountNumber, String newFirstName, String newLastName) throws SQLException {
    String sql = "UPDATE tb_userdetails SET firstname = ?, lastname = ? WHERE accountnumber = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, newFirstName);
        pstmt.setString(2, newLastName);
        pstmt.setString(3, accountNumber);
        pstmt.executeUpdate();
    }
}
   
   public void deleteSavings(String accountNumber, String title) throws SQLException {
    String sql = "DELETE FROM tb_savingsmanagement WHERE accountnumber = ? AND title = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, accountNumber);
        pstmt.setString(2, title);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("No rows affected, deletion might have failed.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    }
}
   
   public void refund(String accountNumber, double balance) throws SQLException {
    String sql = "UPDATE tb_userbalance SET balance = balance + ?, date_modified = ? "
                    + "WHERE accountnumber = ?";
    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setDouble(1, balance);
        pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
        pstmt.setString(3, accountNumber);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            throw new SQLException("No rows affected, deletion might have failed.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    }
}

    public Savings getSavingsByTitle(String title) {
        for (Savings savings : savingsList) {
            if (savings.getTitle().equals(title)) {
                return savings;
            }
        }
        return null; // Return null if no matching savings is found
    }
    
    
    
      public void addTransactionAndUpdateBalance(String accountNumber, String username, String transacType, 
                                                String transacAccount, double amount) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmtTransaction = null;
        PreparedStatement pstmtUpdateBalance = null;

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // Prepare the amount based on transaction type
            double amountToStore = amount;
            if ("Withdraw".equals(transacType) || "Send".equals(transacType)) {
                amountToStore = -amount; // Store negative amount for withdrawals and sends
            }

            // Insert into tb_transaction
            String insertTransactionSQL = "INSERT INTO tb_transaction (accountnumber, username, transac_type, "
                    + "transac_account, amount) VALUES (?, ?, ?, ?, ?)";
            pstmtTransaction = conn.prepareStatement(insertTransactionSQL);
            pstmtTransaction.setString(1, accountNumber);
            pstmtTransaction.setString(2, username);
            pstmtTransaction.setString(3, transacType);
            pstmtTransaction.setString(4, transacAccount);
            pstmtTransaction.setString(5, Double.toString(amountToStore));
            pstmtTransaction.executeUpdate();

            // Update tb_userbalance
            String updateBalanceSQL = "UPDATE tb_userbalance SET balance = balance + ?, date_modified = ? "
                    + "WHERE accountnumber = ?";
            pstmtUpdateBalance = conn.prepareStatement(updateBalanceSQL);
            pstmtUpdateBalance.setDouble(1, amountToStore); // Update balance with the amountToStore
            pstmtUpdateBalance.setTimestamp(2, new Timestamp(new Date().getTime()));
            pstmtUpdateBalance.setString(3, accountNumber);
            pstmtUpdateBalance.executeUpdate();
        } finally {
            if (pstmtTransaction != null) pstmtTransaction.close();
            if (pstmtUpdateBalance != null) pstmtUpdateBalance.close();
            if (conn != null) conn.close();
        }
    }
       public void sendMoney(String senderAccountNumber, String recipientAccountNumber, double amount) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmtSenderTransaction = null;
    PreparedStatement pstmtRecipientTransaction = null;
    PreparedStatement pstmtUpdateSenderBalance = null;
    PreparedStatement pstmtUpdateRecipientBalance = null;
    String recipientUsername = null;

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false); // Start transaction
        
        recipientUsername = getUsernameByAccountNumber(recipientAccountNumber);
        if (recipientUsername == null) {
            throw new SQLException("Recipient account number not found.");
        }
        
        // Insert transaction for sender
        String insertSenderTransactionSQL = "INSERT INTO tb_transaction (accountnumber, username, transac_type, transac_account, amount) VALUES (?, ?, ?, ?, ?)";
        pstmtSenderTransaction = conn.prepareStatement(insertSenderTransactionSQL);
        pstmtSenderTransaction.setString(1, senderAccountNumber);
        pstmtSenderTransaction.setString(2, username);
        pstmtSenderTransaction.setString(3, "Send");
        pstmtSenderTransaction.setString(4, recipientAccountNumber);
        pstmtSenderTransaction.setDouble(5, -amount);
        pstmtSenderTransaction.executeUpdate();

        // Insert transaction for recipient
        String insertRecipientTransactionSQL = "INSERT INTO tb_transaction (accountnumber, username, transac_type, transac_account, amount) VALUES (?, ?, ?, ?, ?)";
        pstmtRecipientTransaction = conn.prepareStatement(insertRecipientTransactionSQL);
        pstmtRecipientTransaction.setString(1, recipientAccountNumber);
        pstmtRecipientTransaction.setString(2, recipientUsername); // Assuming the recipient's username is the same as the sender's for now
        pstmtRecipientTransaction.setString(3, "Receive");
        pstmtRecipientTransaction.setString(4, senderAccountNumber);
        pstmtRecipientTransaction.setDouble(5, amount);
        pstmtRecipientTransaction.executeUpdate();

        // Update sender's balance
        String updateSenderBalanceSQL = "UPDATE tb_userbalance SET balance = balance - ?, date_modified = ? WHERE accountnumber = ?";
        pstmtUpdateSenderBalance = conn.prepareStatement(updateSenderBalanceSQL);
        pstmtUpdateSenderBalance.setDouble(1, amount);
        pstmtUpdateSenderBalance.setTimestamp(2, new Timestamp(new Date().getTime()));
        pstmtUpdateSenderBalance.setString(3, senderAccountNumber);
        pstmtUpdateSenderBalance.executeUpdate();

        // Update recipient's balance
        String updateRecipientBalanceSQL = "UPDATE tb_userbalance SET balance = balance + ?, date_modified = ? WHERE accountnumber = ?";
        pstmtUpdateRecipientBalance = conn.prepareStatement(updateRecipientBalanceSQL);
        pstmtUpdateRecipientBalance.setDouble(1, amount);
        pstmtUpdateRecipientBalance.setTimestamp(2, new Timestamp(new Date().getTime()));
        pstmtUpdateRecipientBalance.setString(3, recipientAccountNumber);
        pstmtUpdateRecipientBalance.executeUpdate();

        conn.commit(); // Commit transaction
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback(); // Rollback transaction on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        e.printStackTrace();
    } finally {
        if (pstmtSenderTransaction != null) pstmtSenderTransaction.close();
        if (pstmtRecipientTransaction != null) pstmtRecipientTransaction.close();
        if (pstmtUpdateSenderBalance != null) pstmtUpdateSenderBalance.close();
        if (pstmtUpdateRecipientBalance != null) pstmtUpdateRecipientBalance.close();
        if (conn != null) conn.close();
    }
}
       
       public void addSavings(String accountNumber, String title, String savingsDetails, String savingsPassword, double savingsQty, double savingsGoal, String progress) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmt = null;

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        String insertSQL = "INSERT INTO tb_savingsmanagement (accountnumber, title, savings_details, savings_password, savings_qty, savings_goal, progress) VALUES (?, ?, ?, ?, ?, ?, ?)";
        pstmt = conn.prepareStatement(insertSQL);
        pstmt.setString(1, accountNumber);
        pstmt.setString(2, title);
        pstmt.setString(3, savingsDetails);
        pstmt.setString(4, savingsPassword);
        pstmt.setDouble(5, savingsQty);
        pstmt.setDouble(6, savingsGoal);
        pstmt.setString(7, progress);
        pstmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        throw e; // Re-throw the exception for handling higher up the call stack if necessary
    } finally {
        if (pstmt != null) pstmt.close();
        if (conn != null) conn.close();
    }
}
       
       public void updateSavingsQty(String accountNumber, String savingsTitle, double amount, boolean isDeposit) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmtUpdateSavings = null;
    PreparedStatement pstmtUpdateBalance = null;

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        conn.setAutoCommit(false); // Start transaction

        // Retrieve current savings quantity and goal
        String selectSavingsSQL = "SELECT savings_qty, savings_goal FROM tb_savingsmanagement WHERE accountnumber = ? AND title = ?";
        try (PreparedStatement pstmtSelectSavings = conn.prepareStatement(selectSavingsSQL)) {
            pstmtSelectSavings.setString(1, accountNumber);
            pstmtSelectSavings.setString(2, savingsTitle);
            try (ResultSet rs = pstmtSelectSavings.executeQuery()) {
                if (!rs.next()) {
                    throw new SQLException("Savings not found.");
                }
                double currentSavingsQty = rs.getDouble("savings_qty");
                double goal = rs.getDouble("savings_goal");

                // Update savings quantity
                double newSavingsQty = isDeposit ? currentSavingsQty + amount : currentSavingsQty - amount;
                if (newSavingsQty < 0) {
                    throw new SQLException("Insufficient savings.");
                }

                // Check if the goal has been reached
                String progress = newSavingsQty >= goal ? "completed" : "in progress";

                String updateSavingsSQL = "UPDATE tb_savingsmanagement SET savings_qty = ?, last_modified = ?, progress = ? WHERE accountnumber = ? AND title = ?";
                pstmtUpdateSavings = conn.prepareStatement(updateSavingsSQL);
                pstmtUpdateSavings.setDouble(1, newSavingsQty);
                pstmtUpdateSavings.setTimestamp(2, new Timestamp(new Date().getTime()));
                pstmtUpdateSavings.setString(3, progress);
                pstmtUpdateSavings.setString(4, accountNumber);
                pstmtUpdateSavings.setString(5, savingsTitle);
                pstmtUpdateSavings.executeUpdate();

                // Update user balance
                double amountToUpdate = isDeposit ? -amount : amount;
                String updateBalanceSQL = "UPDATE tb_userbalance SET balance = balance + ?, date_modified = ? WHERE accountnumber = ?";
                pstmtUpdateBalance = conn.prepareStatement(updateBalanceSQL);
                pstmtUpdateBalance.setDouble(1, amountToUpdate);
                pstmtUpdateBalance.setTimestamp(2, new Timestamp(new Date().getTime()));
                pstmtUpdateBalance.setString(3, accountNumber);
                pstmtUpdateBalance.executeUpdate();
            }
        }

        conn.commit(); // Commit transaction
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback(); // Rollback transaction on error
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        e.printStackTrace();
    } finally {
        if (pstmtUpdateSavings != null) pstmtUpdateSavings.close();
        if (pstmtUpdateBalance != null) pstmtUpdateBalance.close();
        if (conn != null) conn.close();
    }
}



       private String getUsernameByAccountNumber(String accountNumber) throws SQLException {
    Connection conn = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    String username = null;

    try {
        conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        String query = "SELECT username FROM tb_userbalance WHERE accountnumber = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setString(1, accountNumber);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            username = rs.getString("username");
        }
    } finally {
        if (rs != null) rs.close();
        if (pstmt != null) pstmt.close();
        if (conn != null) conn.close();
    }

    return username;
}


    // Getters for the variables
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getBirthDate() { return birthDate; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getNationality() { return nationality; }
    public String getPassword() { return password; }
    public String getStatus() { return status; }
    public List<Transaction> getTransactions() { return transactions; } // Getter for transactions
    public List<Savings> getSavingsList() {return savingsList;}

    // Transaction class to hold transaction data
    public static class Transaction {
        private int transactionId;
        private String accountNumber;
        private String transacType;
        private String transacAccount;
        private String amount;
        private String transferDate;

        public Transaction(int transactionId, String accountNumber, String transacType, String transacAccount, String amount, String transferDate) {
            this.transactionId = transactionId;
            this.accountNumber = accountNumber;
            this.transacType = transacType;
            this.transacAccount = transacAccount;
            this.amount = amount;
            this.transferDate = transferDate;
        }

        // Getters
        public int getTransactionId() { return transactionId; }
        public String getAccountNumber() { return accountNumber; }
        public String getTransacType() { return transacType; }
        public String getTransacAccount() { return transacAccount; }
        public String getAmount() { return amount; }
        public String getTransferDate() { return transferDate; }
    }
    
    public static class Savings {
        private int savingsId;
        private String accountNumber;
        private String title;
        private String savingsDetails;
        private String savingsPassword;
        private double savingsQty;
        private double savingsGoal;
        private String progress;
        private String dateCreated;
        private String lastModified;

        public Savings(int savingsId, String accountNumber, String title, String savingsDetails, String savingsPassword, double savingsQty, double savingsGoal, String progress, String dateCreated, String lastModified) {
            this.savingsId = savingsId;
            this.accountNumber = accountNumber;
            this.title = title;
            this.savingsDetails = savingsDetails;
            this.savingsPassword = savingsPassword;
            this.savingsQty = savingsQty;
            this.savingsGoal = savingsGoal;
            this.progress = progress;
            this.dateCreated = dateCreated;
            this.lastModified = lastModified;
        }

        // Getters
        public int getSavingsId() { return savingsId; }
        public String getAccountNumber() { return accountNumber; }
        public String getTitle() { return title; }
        public String getSavingsDetails() { return savingsDetails; }
        public String getSavingsPassword() { return savingsPassword; }
        public double getSavingsQty() { return savingsQty; }
        public double getSavingsGoal() { return savingsGoal; }
        public String getProgress() { return progress; }
        public String getDateCreated() { return dateCreated; }
        public String getLastModified() { return lastModified; }
        
    }

    // Other inner classes and methods ...
}


