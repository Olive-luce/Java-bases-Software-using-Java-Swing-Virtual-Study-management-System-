package com.mycompany.vsp2;

import com.mycompany.vsp2.dao.ClassDAO;
import com.mycompany.vsp2.dao.MarksDAO;
import com.mycompany.vsp2.dao.MaterialDAO;
import com.mycompany.vsp2.dao.DatabaseConnection;
import com.mycompany.vsp2.model.User;
import com.mycompany.vsp2.ui.LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;


public class StudentDashboard extends javax.swing.JFrame {

    private User student;

    // Card names
    private static final String CARD_HOME = "home";
    private static final String CARD_CLASSES = "classes";
    private static final String CARD_MATERIALS = "materials";
    private static final String CARD_MARKS = "marks";
    private static final String CARD_PROGRESS = "progress";

    // UI components (sidebar)
    private JPanel sidebar;
    private JButton btnHome;
    private JButton btnClasses;
    private JButton btnMaterials;
    private JButton btnMarks;
    private JButton btnProgress;
    private JButton btnJoinClass;

    // main card panel
    private JPanel cardPanel;
    private CardLayout cardLayout;

    // Home panel
    private JLabel lblWelcome;

    // Classes panel
    private JPanel classesListPanel;
    private JScrollPane classesScroll;

    // Materials panel
    private DefaultTableModel materialsModel;
    private JTable materialsTable;

    // Marks panel
    private DefaultTableModel marksModel;
    private JTable marksTable;

    // Progress panel
    private JProgressBar progressBar;
    private JLabel progressLabel;

    public StudentDashboard() {
        initComponents();
    }

    public StudentDashboard(User u) {
        this.student = u;
        initComponents();
        lblWelcome.setText("Welcome, " + (student != null ? student.getName() : "Student"));
        showHome();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Student Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar
        sidebar = new JPanel();
        sidebar.setLayout(new GridBagLayout());
        sidebar.setBackground(new Color(245,245,245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(8,8,8,8);

        btnHome = new JButton("Home");
        btnClasses = new JButton("Classes");
        btnMaterials = new JButton("Materials");
        btnMarks = new JButton("Marks");
        btnProgress = new JButton("Progress");
        btnJoinClass = new JButton("Join Class");

        // ★ LOGOUT BUTTON ADDED
        JButton btnLogout = new JButton("Logout");

        gbc.gridy = 0; sidebar.add(btnHome, gbc);
        gbc.gridy = 1; sidebar.add(btnClasses, gbc);
        gbc.gridy = 2; sidebar.add(btnMaterials, gbc);
        gbc.gridy = 3; sidebar.add(btnMarks, gbc);
        gbc.gridy = 4; sidebar.add(btnProgress, gbc);

        gbc.gridy = 5; sidebar.add(Box.createVerticalStrut(20), gbc);

        gbc.gridy = 6; sidebar.add(btnJoinClass, gbc);

        gbc.gridy = 7; sidebar.add(btnLogout, gbc);

        add(sidebar, BorderLayout.WEST);

        // Wire logout
        btnLogout.addActionListener(e -> {
            new LoginPage().setVisible(true);
            this.dispose();
        });

        // Card panel (right)
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Home panel
        JPanel home = new JPanel(new BorderLayout());
        lblWelcome = new JLabel("Welcome, Student");
        lblWelcome.setFont(lblWelcome.getFont().deriveFont(18f));
        JPanel welcomeWrap = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomeWrap.add(lblWelcome);
        home.add(welcomeWrap, BorderLayout.NORTH);
        JTextArea homeText = new JTextArea(
            "This is your student dashboard. Use the sidebar to navigate.\n\n" +
            "Classes: view and join classes.\nMaterials: view materials.\nMarks: see your marks.\nProgress: average marks as percentage."
        );
        homeText.setEditable(false);
        homeText.setBackground(getBackground());
        home.add(homeText, BorderLayout.CENTER);

        // Classes panel
        JPanel classesPanel = new JPanel(new BorderLayout());
        classesListPanel = new JPanel();
        classesListPanel.setLayout(new BoxLayout(classesListPanel, BoxLayout.Y_AXIS));
        classesScroll = new JScrollPane(classesListPanel);
        classesPanel.add(classesScroll, BorderLayout.CENTER);
        JButton refreshClassesBtn = new JButton("Refresh Classes");
        classesPanel.add(refreshClassesBtn, BorderLayout.NORTH);

        // Materials panel
        JPanel materialsPanel = new JPanel(new BorderLayout());
        materialsModel = new DefaultTableModel(new String[]{"Class","Title","Path"}, 0);
        materialsTable = new JTable(materialsModel);
        materialsPanel.add(new JScrollPane(materialsTable), BorderLayout.CENTER);
        JButton refreshMaterialsBtn = new JButton("Refresh Materials");
        materialsPanel.add(refreshMaterialsBtn, BorderLayout.NORTH);

        // Marks panel
        JPanel marksPanel = new JPanel(new BorderLayout());
        marksModel = new DefaultTableModel(new String[]{"Class","Marks"}, 0);
        marksTable = new JTable(marksModel);
        marksPanel.add(new JScrollPane(marksTable), BorderLayout.CENTER);
        JButton refreshMarksBtn = new JButton("Refresh Marks");
        marksPanel.add(refreshMarksBtn, BorderLayout.NORTH);

        // Progress panel
        JPanel progPanel = new JPanel(new BorderLayout());
        JPanel topProg = new JPanel(new FlowLayout(FlowLayout.LEFT));
        progressLabel = new JLabel("Overall Progress:");
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        topProg.add(progressLabel);
        topProg.add(progressBar);
        progPanel.add(topProg, BorderLayout.NORTH);
        JTextArea progHelp = new JTextArea("Progress is the average of marks across joined classes (0-100).");
        progHelp.setEditable(false);
        progHelp.setBackground(getBackground());
        progPanel.add(progHelp, BorderLayout.CENTER);
        JButton refreshProgressBtn = new JButton("Refresh Progress");
        progPanel.add(refreshProgressBtn, BorderLayout.SOUTH);

        // Add cards
        cardPanel.add(home, CARD_HOME);
        cardPanel.add(classesPanel, CARD_CLASSES);
        cardPanel.add(materialsPanel, CARD_MATERIALS);
        cardPanel.add(marksPanel, CARD_MARKS);
        cardPanel.add(progPanel, CARD_PROGRESS);

        add(cardPanel, BorderLayout.CENTER);

        // Buttons
        btnHome.addActionListener(e -> showHome());
        btnClasses.addActionListener(e -> showClasses());
        btnMaterials.addActionListener(e -> showMaterialsCard());
        btnMarks.addActionListener(e -> showMarksCard());
        btnProgress.addActionListener(e -> showProgressCard());
        btnJoinClass.addActionListener(e -> joinClassDialog());

        refreshClassesBtn.addActionListener(e -> loadJoinedClasses());
        refreshMaterialsBtn.addActionListener(e -> loadMaterialsForJoined());
        refreshMarksBtn.addActionListener(e -> loadMarks());
        refreshProgressBtn.addActionListener(e -> updateProgress());

        cardLayout.show(cardPanel, CARD_HOME);
    }

    private void showHome() { cardLayout.show(cardPanel, CARD_HOME); }
    private void showClasses() { cardLayout.show(cardPanel, CARD_CLASSES); loadJoinedClasses(); }
    private void showMaterialsCard() { cardLayout.show(cardPanel, CARD_MATERIALS); loadMaterialsForJoined(); }
    private void showMarksCard() { cardLayout.show(cardPanel, CARD_MARKS); loadMarks(); }
    private void showProgressCard() { cardLayout.show(cardPanel, CARD_PROGRESS); updateProgress(); }

    private void joinClassDialog() {
        try {
            List<ClassDAO.ClassSummary> classes = ClassDAO.listAllWithId();
            if (classes.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No classes available.");
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (ClassDAO.ClassSummary cs : classes)
                sb.append(cs.id).append(": ").append(cs.name).append("\n");

            String input = JOptionPane.showInputDialog(
                this, "Available classes:\n" + sb + "\nEnter class ID to join:"
            );

            if (input == null || input.trim().isEmpty()) return;

            int cid = Integer.parseInt(input.trim());

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("INSERT INTO class_students (classId, studentId) VALUES (?, ?)")) {
                ps.setInt(1, cid);
                ps.setInt(2, student.getId());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Joined class successfully.");
                loadJoinedClasses();
                loadMaterialsForJoined();
                loadMarks();
                updateProgress();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to join class: " + ex.getMessage());
        }
    }

    private void loadJoinedClasses() {
        classesListPanel.removeAll();
        try {
            if (student != null) {
                List<String> cls = ClassDAO.getClassesByStudent(student.getId());
                if (cls == null || cls.isEmpty()) {
                    List<ClassDAO.ClassSummary> all = ClassDAO.listAllWithId();
                    for (ClassDAO.ClassSummary cs : all) {
                        classesListPanel.add(makeClassBlock(cs.id, cs.name, false));
                    }
                } else {
                    for (String cname : cls) {
                        classesListPanel.add(makeClassBlock(-1, cname, true));
                    }
                }
            }
        } catch (Exception ex) {
            classesListPanel.add(makeClassBlock(-1, "Demo Class", true));
        }

        classesListPanel.revalidate();
        classesListPanel.repaint();
    }

    private JPanel makeClassBlock(int id, String title, boolean joined) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        JLabel lbl = new JLabel(title + (joined ? " (Joined)" : ""));
        p.add(lbl, BorderLayout.CENTER);

        if (!joined && id > 0) {
            JButton join = new JButton("Join");
            join.addActionListener(e -> {
                try (Connection con = DatabaseConnection.getConnection();
                     PreparedStatement ps = con.prepareStatement(
                             "INSERT INTO class_students (classId, studentId) VALUES (?, ?)"
                     )) {
                    ps.setInt(1, id);
                    ps.setInt(2, student.getId());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Joined " + title);
                    loadJoinedClasses();
                    loadMaterialsForJoined();
                    loadMarks();
                    updateProgress();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Failed to join: " + ex.getMessage());
                }
            });
            p.add(join, BorderLayout.EAST);
        }

        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        return p;
    }

    private void loadMaterialsForJoined() {
        materialsModel.setRowCount(0);
        try {
            if (student == null) return;
            List<String> classes = ClassDAO.getClassesByStudent(student.getId());
            if (classes == null || classes.isEmpty()) return;

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT classId FROM classroom WHERE className = ?")) {

                for (String cname : classes) {
                    ps.setString(1, cname);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int cid = rs.getInt(1);
                            List<String> mats = MaterialDAO.getMaterialsByClass(cid);
                            for (String m : mats) {
                                String[] parts = m.split("\\s*\\|\\s*");
                                String title = parts.length > 0 ? parts[0] : m;
                                String path = parts.length > 1 ? parts[1] : "";
                                materialsModel.addRow(new Object[]{cname, title, path});
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {}
    }

    private void loadMarks() {
        marksModel.setRowCount(0);
        try {
            if (student == null) return;
            List<String> marks = MarksDAO.getMarksByStudent(student.getId());
            for (String m : marks) {
                marksModel.addRow(new Object[]{
                        m.split("->")[0].trim(),
                        m.contains("->") ? m.split("->")[1].trim() : ""
                });
            }
        } catch (Exception ex) {
            marksModel.addRow(new Object[]{"Demo Class","78"});
        }
    }

    private void updateProgress() {
        try {
            if (student == null) {
                progressBar.setValue(0);
                progressBar.setString("0%");
                return;
            }
            int p = MarksDAO.getProgress(student.getId());
            progressBar.setValue(p);
            progressBar.setString(p + "% (avg)");
        } catch (Exception ex) {
            progressBar.setValue(0);
            progressBar.setString("0%");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard().setVisible(true));
    }
}
