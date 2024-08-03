/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package copl_finals;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 *
 * @author Admin
 */
public class Register extends javax.swing.JFrame {
    
    private Connection getConnection() throws SQLException {
        // Implement this method to get your database connection
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/copl_finals", "root", "pass0403");
    }

    private String generateUniqueAccountNumber(Connection connection) throws SQLException {
        String accountNumber;
        boolean isUnique;
        do {
            accountNumber = String.valueOf((long) (Math.random() * 1e15));
            String checkQuery = "SELECT accountnumber FROM tb_users WHERE accountnumber = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, accountNumber);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    isUnique = !resultSet.next();
                }
            }
        } while (!isUnique);
        return accountNumber;
    }

    private int calculateAge(String birthdate) {
        // Implement this method to calculate age from the birthdate string
        // Assuming the birthdate is in format "yyyy-MM-dd"
        java.time.LocalDate birthDate = java.time.LocalDate.parse(birthdate);
        java.time.LocalDate currentDate = java.time.LocalDate.now();
        return java.time.Period.between(birthDate, currentDate).getYears();
    }
    
    private boolean isValidDate(String dateStr) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(dateStr, dateFormatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public void signup() {
        String firstname = jTextField1.getText().trim();
        String lastname = jTextField2.getText().trim();
        String username = jTextField3.getText().trim();
        String email = jTextField4.getText().trim();
        String phonenumber = jTextField5.getText().trim();
        String birthdate = jTextField6.getText().trim();
        String gender = jTextField7.getText().trim();
        String nationality = jTextField8.getText().trim();
        String password = new String(jPasswordField1.getPassword()).trim();
        String confirmpassword = new String(jPasswordField2.getPassword()).trim();

        if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || email.isEmpty() ||
            phonenumber.isEmpty() || birthdate.isEmpty() || gender.isEmpty() || nationality.isEmpty() ||
            password.isEmpty() || confirmpassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", 
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmpassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", 
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phonenumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone number must be numeric.", 
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
        Matcher emailMatcher = emailPattern.matcher(email);
        if (!emailMatcher.matches()) {
            JOptionPane.showMessageDialog(this, "Invalid email format.", 
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isValidDate(birthdate)) {
            JOptionPane.showMessageDialog(this, "Invalid birthdate format. Use yyyy-MM-dd.", 
            "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String checkQuery = "SELECT * FROM tb_userdetails WHERE username = ? OR email = ? OR phonenumber = ?";
        String insertUserQuery = "INSERT INTO tb_users (accountnumber, username, email, password, status) VALUES (?, ?, ?, ?, ?)";
        String insertUserDetailsQuery = "INSERT INTO tb_userdetails (accountnumber, username, firstname, lastname, email, phonenumber, birthdate, age, gender, nationality, password, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String getUserIdQuery = "SELECT userid FROM tb_users WHERE accountnumber = ?";
        String insertLogQuery = "INSERT INTO tb_userlogs (userid, username, details) VALUES (?, ?, ?)";
        String insertBalanceQuery = "INSERT INTO tb_userbalance (userid, accountnumber, username, balance) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = getConnection()) {
            // Check for existing username, email, or phone number
            try (PreparedStatement checkStatement = connection.prepareStatement(checkQuery)) {
                checkStatement.setString(1, username);
                checkStatement.setString(2, email);
                checkStatement.setString(3, phonenumber);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(this, "Username, email, or phone number already exists.", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }

            // Generate unique account number
            String accountNumber = generateUniqueAccountNumber(connection);

            // Insert into tb_users
            try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                insertUserStatement.setString(1, accountNumber);
                insertUserStatement.setString(2, username);
                insertUserStatement.setString(3, email);
                insertUserStatement.setString(4, password);
                insertUserStatement.setInt(5, 0); // status
                insertUserStatement.executeUpdate();
            }

            // Insert into tb_userdetails
            try (PreparedStatement insertUserDetailsStatement = connection.prepareStatement(insertUserDetailsQuery)) {
                int age = calculateAge(birthdate);
                insertUserDetailsStatement.setString(1, accountNumber);
                insertUserDetailsStatement.setString(2, username);
                insertUserDetailsStatement.setString(3, firstname);
                insertUserDetailsStatement.setString(4, lastname);
                insertUserDetailsStatement.setString(5, email);
                insertUserDetailsStatement.setString(6, phonenumber);
                insertUserDetailsStatement.setString(7, birthdate);
                insertUserDetailsStatement.setInt(8, age);
                insertUserDetailsStatement.setString(9, gender);
                insertUserDetailsStatement.setString(10, nationality);
                insertUserDetailsStatement.setString(11, password);
                insertUserDetailsStatement.setInt(12, 0); // status
                insertUserDetailsStatement.executeUpdate();
            }
            
            int userId;
            try (PreparedStatement getUserIdStatement = connection.prepareStatement(getUserIdQuery)) {
                getUserIdStatement.setString(1, accountNumber);
                try (ResultSet resultSet = getUserIdStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userId = resultSet.getInt("userid");
                    } else {
                        throw new SQLException("Failed to retrieve user ID for new account.");
                    }
                }
            }

            // Insert into tb_userlogs
            try (PreparedStatement insertLogStatement = connection.prepareStatement(insertLogQuery)) {
                insertLogStatement.setInt(1, userId);
                insertLogStatement.setString(2, username);
                insertLogStatement.setString(3, "CREATE ACCOUNT");
                insertLogStatement.executeUpdate();
            }
            
            try (PreparedStatement insertBalanceStatement = connection.prepareStatement(insertBalanceQuery)) {
                insertBalanceStatement.setInt(1, userId);
                insertBalanceStatement.setString(2, accountNumber);
                insertBalanceStatement.setString(3, username);
                insertBalanceStatement.setInt(4, 0);
                insertBalanceStatement.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Signup successful! Your Account Number is: "+accountNumber+"", 
            "Success", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                new Login().setVisible(true);
                            }
                        });
            dispose();
            //new login().setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public Register() {
        myinitComponents();
        setLocationRelativeTo(null);
        scaleImageToLabel(jLabel14);
        jLabel13.setCursor(new Cursor(Cursor.HAND_CURSOR));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SwingUtilities.invokeLater(() -> new Login().setVisible(true));
                dispose();
            }
        });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnCreate = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPasswordField1 = new javax.swing.JPasswordField();
        jPasswordField2 = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(968, 670));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/PEBankBGRegister.png"))); // NOI18N
        jLabel14.setMaximumSize(new java.awt.Dimension(294, 670));
        jLabel14.setMinimumSize(new java.awt.Dimension(294, 670));
        jLabel14.setPreferredSize(new java.awt.Dimension(294, 670));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jLabel1.setText("First Name");

        jLabel2.setText("Last Name");

        jLabel3.setText("Username");

        jLabel4.setText("Email");

        jLabel5.setText("Phone Number");

        jLabel6.setText("Birth Date (Ex: \"YYYY-MM-DD\")");

        jLabel7.setText("Gender");

        jLabel8.setText("Nationality");

        jLabel9.setText("Password");

        jLabel10.setText("Confirm Password");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("REGISTER");

        btnCreate.setBackground(new java.awt.Color(0, 204, 51));
        btnCreate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Create Account");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        jLabel12.setText("Already have an account?");

        jLabel13.setText("Login");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField5)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13))
                            .addComponent(jPasswordField1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField4)
                            .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(btnCreate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(jPasswordField2))
                        .addGap(31, 31, 31))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(314, 314, 314)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel11)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addComponent(jPasswordField2))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void myinitComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jTextField2 = new Login.RoundedTextField(20, 15);
        jTextField1 = new Login.RoundedTextField(20, 15);
        jTextField3 = new Login.RoundedTextField(20, 15);
        jTextField4 = new Login.RoundedTextField(20, 15);
        jTextField5 = new Login.RoundedTextField(20, 15);
        jTextField6 = new Login.RoundedTextField(20, 15);
        jTextField7 = new Login.RoundedTextField(20, 15);
        jTextField8 = new Login.RoundedTextField(20, 15);
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnCreate =new Login.RoundedButton(20, 15);
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jPasswordField1 = new Login.RoundedPasswordField(20, 15);
        jPasswordField2 = new Login.RoundedPasswordField(20, 15);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(968, 670));

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/copl_finals/PEBankBGRegister.png"))); // NOI18N
        jLabel14.setMaximumSize(new java.awt.Dimension(294, 670));
        jLabel14.setMinimumSize(new java.awt.Dimension(294, 670));
        jLabel14.setPreferredSize(new java.awt.Dimension(294, 670));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jLabel1.setText("First Name");

        jLabel2.setText("Last Name");

        jLabel3.setText("Username");

        jLabel4.setText("Email");

        jLabel5.setText("Phone Number");

        jLabel6.setText("Birth Date (Ex: \"YYYY-MM-DD\")");

        jLabel7.setText("Gender");

        jLabel8.setText("Nationality");

        jLabel9.setText("Password");

        jLabel10.setText("Confirm Password");

        jLabel11.setBackground(new java.awt.Color(255, 255, 255));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel11.setText("REGISTER");

        btnCreate.setBackground(new java.awt.Color(0, 204, 51));
        btnCreate.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnCreate.setForeground(new java.awt.Color(255, 255, 255));
        btnCreate.setText("Create Account");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        jLabel12.setText("Already have an account?");
        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel13.setText("<html><u>Login</u><html>");
        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 15)); // NOI18N
        jLabel13.setForeground(Color.BLUE);
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField5)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel9)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel13))
                            .addComponent(jPasswordField1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                                .addComponent(jTextField4))
                            .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .addComponent(btnCreate, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel8)
                            .addComponent(jLabel10)
                            .addComponent(jPasswordField2))
                        .addGap(31, 31, 31))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(314, 314, 314)
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel11)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                    .addGap(8, 8, 8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                    .addGap(8, 8, 8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10))
                    .addGap(8, 8, 8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPasswordField1, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addComponent(jPasswordField2))
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel13))
                .addContainerGap(39, Short.MAX_VALUE))
        );

        pack();
    }
    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnCreateActionPerformed

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        this.dispose();
        SwingUtilities.invokeLater(() -> new Login().setVisible(true));
    }//GEN-LAST:event_jLabel13MouseClicked

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
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Register.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Register().setVisible(true);
            }
        });
    }
    private void scaleImageToLabel(JLabel label) {
        ImageIcon icon = (ImageIcon) label.getIcon();
        if (icon != null) {
            Image originalImage = icon.getImage();
            Image scaledImage = originalImage.getScaledInstance(label.getWidth(), label.getHeight(), Image.SCALE_SMOOTH);
            label.setIcon(new ImageIcon(scaledImage));
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreate;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1;
    private javax.swing.JPasswordField jPasswordField2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables
}
