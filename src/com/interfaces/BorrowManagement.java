package com.interfaces;

import javax.swing.*;
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
    private JButton btnBack;

    Connection conn;
    PreparedStatement pst;

    private String reg_id, ISBN;
    private Date B_date, R_date;

    public BorrowManagement() {

        //DB Connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_management", "root", "root");
            System.out.println("Database Connection Success in BM");
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

                if (ISBN.isEmpty()||reg_id.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Select the all Required Field!");
                }
                else {
                    //Rest of the code

                }
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

    public void addBorrow(){
        try {
            pst = conn.prepareStatement("SELECT id FROM books WHERE ISBN=?");
            pst.setString(1, ISBN);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "There is no book in the system!");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
