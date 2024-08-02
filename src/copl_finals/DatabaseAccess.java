/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package copl_finals;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;


public class DatabaseAccess {
    // Database URL, username, and password
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
                        transactions.add(new Transaction(transactionId, accountNum, transacType,transacAccount, amount, transferDate));
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
}
