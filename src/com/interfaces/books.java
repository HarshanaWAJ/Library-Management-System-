package com.interfaces;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

import static java.lang.Integer.parseInt;


public class books {

    public String ISBN, book_name, author, type, category, searchName;
    public int count;
    private JPanel books;
    private JButton btnAddBook;
    private JTextField txtISBN;
    private JTextField txtbook_name;
    private JTextField txtauthor;
    private JTable books_details;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnDelete;
    private JButton btnRefresh;
    private JButton btnUpdate;
    private JComboBox txtType;
    private JComboBox txtCategory;
    private JButton btnAddCategory;
    private JButton btnAddType;
    private JTextField txtCount;
    private JButton btnBack;

    Connection conn;
    PreparedStatement pst;
    public books() {
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

// Values for txtCategory
        try {
            pst = conn.prepareStatement("SELECT * FROM category");
            ResultSet rs = pst.executeQuery();
            txtCategory.insertItemAt("Select One", 0);
            while (rs.next()) {
                txtCategory.addItem(rs.getString("category"));
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

//Values for txtType
        try {
            pst = conn.prepareStatement("SELECT * FROM type");
            ResultSet rs = pst.executeQuery();
            txtType.insertItemAt("Select One", 0);
            while (rs.next()) {
                txtType.addItem(rs.getString("type"));
            }
            rs.close();
            pst.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtCategory.setSelectedIndex(0);
        txtType.setSelectedIndex(0);

        //Display Book Table
        tableLoad();
        btnAddBook.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ISBN = txtISBN.getText();
                book_name = txtbook_name.getText();
                author = txtauthor.getText();
                type = txtType.getSelectedItem().toString();
                category = txtCategory.getSelectedItem().toString();
                String c = txtCount.getText();
                count = parseInt(c);

                if (ISBN.isEmpty() || book_name.isEmpty() || author.isEmpty() || category.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please fill in all required fields!");

                } else if (type.equals("Select One")){
                   JOptionPane.showMessageDialog(null, "Please Select Type!");
                }
                else if (category.equals("Select One")){
                    JOptionPane.showMessageDialog(null, "Please Select Category of Book!");
                }
                else{
                    addBooks();
                }

            }
        });
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableLoad();
                reloadComboBoxData();
            }
        });

        //Add Type
        btnAddType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addType addType = new addType();
                addType.render();
            }
        });
        btnAddCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCategory addCategory = new addCategory();
                addCategory.render();
            }
        });
        btnSearch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchName = txtSearch.getText();
                if (searchName.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter Book Name to Search!");
                } else {
                    searchBooks();
                }

            }
        });
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchName = txtSearch.getText();

                if (searchName.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter Book Name to Update!");
                } else {

                    txtISBN.setEnabled(false);
                    txtISBN.setFocusable(false);
                    ISBN = txtSearch.getText();
                    book_name = txtbook_name.getText();
                    author = txtauthor.getText();
                    type = txtType.getSelectedItem().toString();
                    category = txtCategory.getSelectedItem().toString();
                    String c = txtCount.getText();
                    count = parseInt(c);

                    // call updateBooks() method
                    boolean isSuccess = updateBooks();
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
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchName = txtSearch.getText();

                if (searchName.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Please Enter ISBN to Delete!");
                }
                else {
                    JOptionPane.showMessageDialog(null, "Book Details Deleted!");
                    deleteBook();
                    tableLoad();
                }
            }
        });
        txtISBN.addFocusListener(new FocusAdapter() {
        });
        //
        //Move to the next
        txtISBN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtbook_name.requestFocus();
            }
        });
        txtbook_name.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtauthor.requestFocus();
            }
        });


        txtauthor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtType.requestFocus();
            }
        });
        txtType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCategory.requestFocus();
            }
        });

        txtCategory.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtCount.requestFocus();
            }
        });
        btnBack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dashboard dashboard = new dashboard();
                dashboard.render();
                SwingUtilities.getWindowAncestor(books).dispose();
            }
        });
    }

    public void render() {
        JFrame frame = new JFrame("Book Management");
        books.setPreferredSize(new Dimension(1024, 728)); // Set the preferred size of the LoginFram panel
        frame.setContentPane(books);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); // Resize the frame to fit its contents
        frame.setSize(1024, 728); // Set the size of the frame explicitly
        frame.setLocationRelativeTo(null); // Center the frame on the screen
        frame.setVisible(true);
    }

    //Database
    public void addBooks(){
        //Send Data
        try {
            pst = conn.prepareStatement("SELECT * FROM books WHERE ISBN = ?");
            pst.setString(1, ISBN);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Book with the same ISBN already exists.");
            } else {
                pst = conn.prepareStatement("INSERT INTO books(ISBN, book_name, author, type, category, count) VALUES (?,?,?,?,?,?)");
                pst.setString(1, ISBN);
                pst.setString(2, book_name);
                pst.setString(3, author);
                pst.setString(4, type);
                pst.setString(5, category);
                pst.setInt(6, count);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(null, "Record Added!");
                tableLoad();
                txtISBN.setText("");
                txtbook_name.setText("");
                txtauthor.setText("");
                txtType.setSelectedItem("");
                txtCategory.setSelectedItem("");
                txtCount.setText("");
                txtISBN.requestFocus();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }


    //Read Data
    public void tableLoad() {
        try {
            pst = conn.prepareStatement("SELECT * FROM books");
            ResultSet rs = pst.executeQuery();
            DefaultTableModel model = new DefaultTableModel();
            ResultSetMetaData rsmd = rs.getMetaData();
            int numColumns = rsmd.getColumnCount();
            // add column names to model
            String[] columnNames = {"ID", "ISBN", "Name of Book", "Author", "Type", "Category No", "No of Copies"};
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
            books_details.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //Reload Categories and Types
    private void reloadComboBoxData() {
        try {
            // Reload values for txtCategory
            pst = conn.prepareStatement("SELECT *  FROM category");
            ResultSet rs = pst.executeQuery();
            txtCategory.removeAllItems();
            txtCategory.addItem("Select One");
            while (rs.next()) {
                txtCategory.addItem(rs.getString("category"));
            }
            rs.close();
            pst.close();


            // Reload values for txtType
            pst = conn.prepareStatement("SELECT * FROM type");
            rs = pst.executeQuery();
            txtType.removeAllItems();
            txtType.insertItemAt("Select One", 0);
            while (rs.next()) {
                txtType.addItem(rs.getString("type"));
            }
            rs.close();
            pst.close();

            // Set selected index to "Select One"
            txtCategory.setSelectedIndex(0);
            txtType.setSelectedIndex(0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Search Books
    public void searchBooks(){
        try {
            pst = conn.prepareStatement("SELECT * FROM books WHERE book_name=?");
            pst.setString(1, searchName);
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
            books_details.setModel(model);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateBooks(){
        try {
            // prepare statement for updating books table
            pst = conn.prepareStatement("UPDATE books SET book_name=?, author=?, type=?, category=?, count=? WHERE ISBN=?");
            pst.setString(1, book_name);
            pst.setString(2, author);
            pst.setString(3, type);
            pst.setString(4, category);
            pst.setInt(5, count);
            pst.setString(6, ISBN);

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

    public boolean deleteBook() {
        try {
            pst = conn.prepareStatement("DELETE FROM books WHERE ISBN=?");
            pst.setString(1, searchName);

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




}
