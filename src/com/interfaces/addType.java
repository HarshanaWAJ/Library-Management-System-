package com.interfaces;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class addType {

    private JPanel addType;
    private JLabel head;
    private JTextField type_Name;
    private JLabel c_name;
    private JButton btnAddType;
    private JTable category_table;
    private JButton refreshButton;
    private JButton cancelButton;

    private String Category_name;
    private String id;

    Connection conn;
    PreparedStatement pst;
    public addType() {


        //DB connection
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

        btnAddType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Category_name = type_Name.getText();

                if (Category_name.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter Type!");
                }
                else {
                    addType();
                    SwingUtilities.getWindowAncestor(addType).dispose();
                }

            }
        });
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableLoad();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(addType).dispose();
            }
        });
    }

    public void render(){
        JFrame frame = new JFrame("Add Book Type");
        addType.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(addType);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
        tableLoad();
    }

    public void addType(){
        try {
            pst = conn.prepareStatement("SELECT * FROM type WHERE id_type = ?");

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Type is Already Added!");
            } else {
                pst = conn.prepareStatement("INSERT INTO type(type) VALUES (?)");
                pst.setString(1, Category_name);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record Added!");
                tableLoad();
                type_Name.setText("");

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void tableLoad(){
        try {
            pst = conn.prepareStatement("SELECT * FROM type");
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
            category_table.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
