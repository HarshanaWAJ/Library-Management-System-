package com.interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class dashboard {

    //Create UI Object

    private JPanel dashboard;
    private JButton btnBooks;
    private JButton btnMembers;
    private JButton btnBorrows;

    public dashboard() {
        btnBooks.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                books books = new books();
                books.render();
                closeFrame();
            }
        });
        btnMembers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                member member = new member();
                member.render();
                closeFrame();
            }
        });
        btnBorrows.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BorrowManagement borrow = new BorrowManagement();
                borrow.render();
                SwingUtilities.getWindowAncestor(dashboard).dispose();
            }
        });
    }

    public void render(){
        JFrame frame = new JFrame("Library Dashboard");
        dashboard.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(dashboard);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    public void closeFrame() {
        SwingUtilities.getWindowAncestor(dashboard).dispose();
    }
}
