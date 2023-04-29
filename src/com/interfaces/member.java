package com.interfaces;

import javax.naming.NamingEnumeration;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

import static java.lang.Integer.parseInt;

public class member {
    private JPanel memFrame;
    private JTextField txtReg;
    private JTextField txtMemName;
    private JComboBox gender;
    private JComboBox type;
    private JButton registerButton;
    private JButton refreshButton;
    private JTable table1;
    private JTextField txtSearch;
    private JButton searchButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton;
    private JTextField txtEmail;

    private String regNo, name, gen, memType, email_id, Search;

        Connection conn;
        PreparedStatement pst;

    public member() {
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard dashboard = new dashboard();
                dashboard.render();
                SwingUtilities.getWindowAncestor(memFrame).dispose();
            }
        });

        //DB Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management", "root", "root");
            System.out.println("Database Connection Success");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        //gender
        gender.insertItemAt("Select One", 0);
        gender.insertItemAt("Male", 1);
        gender.insertItemAt("Female", 2);
        gender.setSelectedIndex(0);

        //Type
        type.insertItemAt("Select One", 0);
        type.insertItemAt("Student", 1);
        type.insertItemAt("Staff", 2);
        type.setSelectedIndex(0);
        txtReg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtMemName.requestFocus();
            }
        });
        txtMemName.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gender.requestFocus();
            }
        });
        gender.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                type.requestFocus();
            }
        });

        type.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtEmail.requestFocus();
            }
        });

        txtEmail.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerButton.requestFocus();
            }
        });
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                regNo = txtReg.getText();
                name = txtMemName.getText();
                gen = gender.getSelectedItem().toString();
                memType = type.getSelectedItem().toString();
                email_id = txtEmail.getText();

                if (regNo.isEmpty() || name.isEmpty() || gen.equals("Select One") ||
                        memType.equals("Select One") || email_id.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all the required fields.");
                    return; // Stop execution if any field is empty or not selected
                }
                else {
                    addMember();
                }
            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableLoad();
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Search = txtSearch.getText();
                if (Search.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please Enter Registration Number to Search!");
                }
                else {
                   searchMember();
                }
            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Search = txtSearch.getText();

                if (Search.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter Register Number to Delete!");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Member Details Deleted!");
                    deleteMember();
                    tableLoad();
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Search= txtSearch.getText();
                if (Search.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter Register Number to Update!");
                } else {
                    regNo = txtSearch.getText();
                    name = txtMemName.getText();
                    gen = gender.getSelectedItem().toString();
                    memType = type.getSelectedItem().toString();
                    email_id = txtEmail.getText();

                    // call updateBooks() method
                    boolean isSuccess = updateMember();
                    if(isSuccess){
                        tableLoad();
                        JOptionPane.showMessageDialog(null, "Record Updated!");
                    }
                    else{
                        JOptionPane.showMessageDialog(null, "Failed to update record!");
                    }
                }
            }
        });
    }

    public void render(){
        JFrame frame = new JFrame("Add Members");
        memFrame.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(memFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);

        tableLoad();
    }

    public void tableLoad() {
        try {
            pst = conn.prepareStatement("SELECT * FROM members");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            // add column names to model
            String[] columnNames = {"ID", "Reg-No", "Name", "Gender", "Membership", "Email"};
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

    public void addMember(){
        //Send Data
        try {
            pst = conn.prepareStatement("SELECT * FROM members WHERE regno = ?");
            pst.setString(1, regNo);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Member with the same registration number already exists.");
            } else {
                pst = conn.prepareStatement("INSERT INTO members(regno, name, gender, mem_type, email) VALUES (?,?,?,?,?)");
                pst.setString(1, regNo);
                pst.setString(2, name);
                pst.setString(3, gen);
                pst.setString(4, memType);
                pst.setString(5, email_id);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record Added!");
                tableLoad();
                txtReg.setText("");
                txtMemName.setText("");
                gender.setSelectedIndex(0);
                type.setSelectedIndex(0);
                txtEmail.setText("");
                txtReg.requestFocus();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void searchMember(){
        try {
            pst = conn.prepareStatement("SELECT * FROM members WHERE regno=?");
            pst.setString(1, Search);
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

    public boolean deleteMember() {
        try {
            pst = conn.prepareStatement("DELETE FROM members WHERE regno=?");
            pst.setString(1, Search);

            // execute the delete statement
            int rowsDeleted = pst.executeUpdate();
            if (rowsDeleted > 0) {
                return true; // deletion was successful
            } else {
                return false; // no rows were affected, deletion failed
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // an exception occurred, deletion failed
        }
    }

    public boolean updateMember(){
        try {
            // prepare statement for updating books table
            pst = conn.prepareStatement("UPDATE members SET name=?, gender=?, mem_type=?, email=? WHERE regno=?");
            pst.setString(5, regNo);
            pst.setString(1, name);
            pst.setString(2, gen);
            pst.setString(3, memType);
            pst.setString(4, email_id);


            // execute the update statement 
            int rowsUpdated = pst.executeUpdate();
            if(rowsUpdated > 0){
                return true;
            }
            else{
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

}
