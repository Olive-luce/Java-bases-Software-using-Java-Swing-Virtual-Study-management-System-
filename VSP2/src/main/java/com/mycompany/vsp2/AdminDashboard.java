package com.mycompany.vsp2;

import com.mycompany.vsp2.dao.UserDAO;
import com.mycompany.vsp2.model.User;
import com.mycompany.vsp2.ui.LoginPage;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class AdminDashboard extends javax.swing.JFrame {

    private DefaultTableModel model;

    // NEW: store logged-in admin
    private User admin;

    // NEW: Logout button
    private JButton logoutBtn;

    public AdminDashboard() {
        initComponents();
        model = (DefaultTableModel) userTable.getModel();
        refreshBtn.addActionListener(e -> loadUsers());
        addBtn.addActionListener(e -> addUserDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        logoutBtn.addActionListener(e -> logout());
        loadUsers();
    }

    // FIX & USE THIS VERSION
    public AdminDashboard(User admin) {
        this.admin = admin;
        initComponents();
        model = (DefaultTableModel) userTable.getModel();
        refreshBtn.addActionListener(e -> loadUsers());
        addBtn.addActionListener(e -> addUserDialog());
        deleteBtn.addActionListener(e -> deleteSelected());
        logoutBtn.addActionListener(e -> logout());
        loadUsers();
    }

    private void logout() {
        int opt = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to logout?", 
                "Logout", JOptionPane.YES_NO_OPTION);

        if (opt == JOptionPane.YES_OPTION) {
            this.dispose();
            new LoginPage().setVisible(true);
        }
    }

    private void loadUsers() {
        model.setRowCount(0);
        try {
            List<User> users = UserDAO.findAll();
            for (User u : users) {
                model.addRow(new Object[]{u.getId(), u.getName(), u.getEmail(), u.getRole()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to load users: " + ex.getMessage());
        }
    }

    private void addUserDialog() {
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JTextField pw = new JTextField();
        String[] roles = {"student", "teacher", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        Object[] fields = {"Name", name, "Email", email, "Password", pw, "Role", roleBox};

        int r = JOptionPane.showConfirmDialog(this, fields, "Add user", JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        User u = new User();
        u.setName(name.getText());
        u.setEmail(email.getText());
        u.setPassword(pw.getText());
        u.setRole((String) roleBox.getSelectedItem());

        try {
            UserDAO.insert(u);
            loadUsers();
            JOptionPane.showMessageDialog(this, "User added.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Add failed: " + ex.getMessage());
        }
    }

    private void deleteSelected() {
        int sel = userTable.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Select a row.");
            return;
        }
        int id = (int) model.getValueAt(sel, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Delete user id " + id + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            UserDAO.deleteById(id);
            loadUsers();
            JOptionPane.showMessageDialog(this, "Deleted.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage());
        }
    }

    private javax.swing.JTable userTable;
    private javax.swing.JButton refreshBtn;
    private javax.swing.JButton addBtn;
    private javax.swing.JButton deleteBtn;

    private void initComponents() {

        setTitle("Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- TOP PANEL WITH LOGOUT BUTTON ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutBtn = new JButton("Logout");
        topBar.add(logoutBtn);
        add(topBar, BorderLayout.NORTH);

        // --- MAIN TABLE ---
        JScrollPane scrollPane = new JScrollPane();
        userTable = new javax.swing.JTable();
        JPanel bottom = new JPanel();
        refreshBtn = new javax.swing.JButton();
        addBtn = new javax.swing.JButton();
        deleteBtn = new javax.swing.JButton();

        userTable.setModel(new DefaultTableModel(
            new Object[][] {},
            new String[]{"ID","Name","Email","Role"}) {
            public boolean isCellEditable(int row, int col) { return false; }
        });

        scrollPane.setViewportView(userTable);

        // --- BOTTOM BUTTONS ---
        refreshBtn.setText("Refresh");
        addBtn.setText("Add");
        deleteBtn.setText("Delete");

        bottom.add(refreshBtn);
        bottom.add(addBtn);
        bottom.add(deleteBtn);

        add(scrollPane, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        pack();
    }
}
