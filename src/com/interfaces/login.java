package com.interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class login extends JFrame {

    //UI Object
    dashboard dashboard = new dashboard();

    //Form Variables
    private JPanel LoginFram;
    private JLabel Labal;
    private JTextField inputUsername;
    private JPasswordField inputPassword;
    private JButton btnLogin;
    private JButton btn1;

    //Variables
    private String userName;
    private String password;

    private String User = "KMVLB";
    private String Pass = "KMVLB";
    public login(){

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userName = inputUsername.getText();
                password = inputPassword.getText();

                if (userName.equals(User) && password.equals(Pass)){
                    dashboard.render();
                    closeFrame();
                }
                else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password");
                }

            }
        });
        inputUsername.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputPassword.requestFocus();
            }
        });
        inputPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnLogin.requestFocus();
            }
        });
    }

    public void render(){
        JFrame frame = new JFrame("Login");
        LoginFram.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(LoginFram);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    //Close the frame
    public void closeFrame() {
        SwingUtilities.getWindowAncestor(LoginFram).dispose();
    }


}
