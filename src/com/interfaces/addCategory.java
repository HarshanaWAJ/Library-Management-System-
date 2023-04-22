package com.interfaces;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class addCategory {
    private JPanel addCategory;
    private JTextField category;
    private JButton btnAddCategory;
    private JTable categoryTable;
    private JButton btnRefresh;
    private JButton cancelButton;
    private String Category_name;

    Connection conn;
    PreparedStatement pst;

    private String id;


    public addCategory() {
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

        btnAddCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Category_name = category.getText();

                if (Category_name.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter Category!");
                }
                else {
                    addCategory();
                    SwingUtilities.getWindowAncestor(addCategory).dispose();
                }
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.getWindowAncestor(addCategory).dispose();
            }
        });
    }

    public void render(){
        JFrame frame = new JFrame("Add Book Type");
        addCategory.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(addCategory);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
        tableLoad();
    }

    public void addCategory(){
        try {
            pst = conn.prepareStatement("SELECT * FROM category WHERE id_category = ?");

            pst.setString(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Category is Already Added!");
            } else {
                pst = conn.prepareStatement("INSERT INTO category(Category) VALUES (?)");
                pst.setString(1, Category_name);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record Added!");
                tableLoad();
                category.setText("");

            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }



    public void tableLoad() {
        try {
            pst = conn.prepareStatement("SELECT * FROM category");
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
            categoryTable.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
