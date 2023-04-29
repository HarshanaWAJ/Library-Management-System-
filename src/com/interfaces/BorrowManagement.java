package com.interfaces;

import utilities.fine_managemnt;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;

public class BorrowManagement {
    private JPanel Jpanel;
    private JTextField txtISBN;
    private JTextField txtMemID;
    private JButton submitButton;
    private JButton backButton;
    private JButton recivedButton;
    private JTable table1;
    private JTextField textField1;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton btnBack;

    Connection conn;
    PreparedStatement pst;

    private String reg_id, ISBN;
    private Date B_date, R_date;
    private  String search;

    public BorrowManagement() {

        //DB Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management", "root", "root");
            System.out.println("Database Connection Success in BM");

            tableLoad();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard dashboard = new dashboard();
                dashboard.render();
                SwingUtilities.getWindowAncestor(Jpanel).dispose();
            }
        });
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ISBN = txtISBN.getText();
                reg_id = txtMemID.getText();
                B_date = Date.valueOf(LocalDate.now());
                R_date = null;

                if (ISBN.isEmpty() || reg_id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Select the all Required Field!");
                } else {
                    addBorrow(ISBN, reg_id, B_date);
                    tableLoad();
                }
            }
        });
        recivedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Date currentDate = Date.valueOf(LocalDate.now());
                try {
                    String isbn = txtISBN.getText();
                    String regNo = txtMemID.getText();
                    pst = conn.prepareStatement("SELECT b_date FROM borrows WHERE isbn = ? AND reg_no = ?");
                    pst.setString(1, isbn);
                    pst.setString(2, regNo);
                    ResultSet rs = pst.executeQuery();

                    if (rs.next()) {
                        Date borrowDate = rs.getDate("b_date");
                        fine_managemnt fineMgmt = new fine_managemnt();
                        double fine = fineMgmt.calFine(Date.valueOf(borrowDate.toLocalDate()), currentDate);

                        // Update the borrows table with the return date
                        pst = conn.prepareStatement("UPDATE borrows SET r_date = ? WHERE isbn = ? AND reg_no = ?");
                        pst.setDate(1, currentDate);
                        pst.setString(2, isbn);
                        pst.setString(3, regNo);
                        pst.executeUpdate();

                        if (fine > 0) {
                            JOptionPane.showMessageDialog(null, "Fine amount: $" + fine);
                        } else {
                            JOptionPane.showMessageDialog(null, "No fine.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No borrow record found.");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                search = textField1.getText();
                if (search.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please Enter Registration Number to Search!");
                }
                else {
                    searchMember();
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableLoad();
            }
        });
    }

        public void render(){
        JFrame frame = new JFrame("Borrow Management");
        Jpanel.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(Jpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    public void addBorrow(String isbn, String regNo, Date borrowDate) {
        try {
            pst = conn.prepareStatement("SELECT * FROM books WHERE ISBN=?");
            pst.setString(1, isbn);
            ResultSet rsBooks = pst.executeQuery();

            pst = conn.prepareStatement("SELECT * FROM members WHERE regno=?");
            pst.setString(1, regNo);
            ResultSet rsMembers = pst.executeQuery();

            if (!rsBooks.next()) {
                JOptionPane.showMessageDialog(null, "There is no book with the given ISBN in the system!");
            }
            else if (!rsMembers.next()) {
                JOptionPane.showMessageDialog(null, "There is no member with the given registration number in the system!");
            }
            else {
                pst = conn.prepareStatement("INSERT INTO borrows (isbn, reg_no, b_date) VALUES (?, ?, ?)");
                pst.setString(1, isbn);
                pst.setString(2, regNo);
                pst.setDate(3, borrowDate);
                pst.executeUpdate();

                JOptionPane.showMessageDialog(null, "Borrow details added to the system.");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }



    public void tableLoad() {
        try {
            pst = conn.prepareStatement("SELECT * FROM borrows");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();

            // add column names to model
            String[] columnNames = {"ISBN", "Reg_No", "Borrow Date", "Receive Date"};
            for (String columnName : columnNames) {
                model.addColumn(columnName);
            }

            // add data to model
            while (rs.next()) {
                Object[] rowData = new Object[numColumns];
                for (int i = 1; i <= numColumns; i++) {
                    rowData[i-1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }
            table1.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void searchMember(){
        try {
            pst = conn.prepareStatement("SELECT * FROM borrows WHERE reg_no=?");
            pst.setString(1, search);
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            // add column names to model
            for (int i = 1; i <= numColumns; i++) {
                model.addColumn(rsmd.getColumnName(i));
            }
            // add data to model
            while (rs.next()) {
                Object[] rowData = new Object[numColumns];
                for (int i = 1; i <= numColumns; i++) {
                    rowData[i-1] = rs.getObject(i);
                }
                model.addRow(rowData);
            }
            table1.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
