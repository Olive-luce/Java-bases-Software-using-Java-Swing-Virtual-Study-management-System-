package com.mycompany.vsp2;

import com.mycompany.vsp2.dao.ClassDAO;
import com.mycompany.vsp2.dao.MaterialDAO;
import com.mycompany.vsp2.dao.MarksDAO;
import com.mycompany.vsp2.dao.UserDAO;
import com.mycompany.vsp2.model.User;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class TeacherDashboard extends javax.swing.JFrame {

    private User teacher;
    private javax.swing.JButton logout_btn;

    public TeacherDashboard() {
        initComponents();
    }

    public TeacherDashboard(User u) {
        initComponents();
        this.teacher = u;

        // ===== LOGOUT =====
        logout_btn.addActionListener(e -> {
            this.dispose();
            new com.mycompany.vsp2.ui.LoginPage().setVisible(true);
        });

        // ===== Create Class =====
        createclasses_btn.addActionListener(e -> {
            String cls = JOptionPane.showInputDialog(this, "Enter Class Name:");
            if (cls != null && !cls.trim().isEmpty()) {
                try {
                    ClassDAO.createClass(cls.trim(), teacher.getId());
                    JOptionPane.showMessageDialog(this, "Class created.");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error creating class: " + ex.getMessage());
                }
            }
        });

        // ===== Upload Material =====
        takeExam_btn.setText("Upload Material");
        takeExam_btn.addActionListener(e -> {
            try {
                List<ClassDAO.ClassSummary> classes = ClassDAO.listAllWithId();
                StringBuilder sb = new StringBuilder();
                for (ClassDAO.ClassSummary cs : classes)
                    sb.append(cs.id).append(": ").append(cs.name).append("\n");

                String classIdStr = JOptionPane.showInputDialog(this,
                    "Available classes:\n" + sb + "\nEnter class ID to upload to:");

                if (classIdStr == null) return;

                int classId = Integer.parseInt(classIdStr.trim());
                String title = JOptionPane.showInputDialog(this, "Material title:");

                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    String path = chooser.getSelectedFile().getAbsolutePath();
                    MaterialDAO.uploadMaterial(classId, title, path);
                    JOptionPane.showMessageDialog(this, "Material uploaded.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Upload failed: " + ex.getMessage());
            }
        });

        // ===== Evaluate / Set Marks =====
        evaluate_btn.addActionListener(e -> {
            try {
                List<User> studs = UserDAO.findStudents();
                StringBuilder sb = new StringBuilder();
                for (User s : studs) sb.append(s.getId()).append(": ").append(s.getName()).append("\n");

                String sidStr = JOptionPane.showInputDialog(this,
                    "Students:\n" + sb + "\nEnter student ID:");
                if (sidStr == null) return;

                int sid = Integer.parseInt(sidStr.trim());

                List<ClassDAO.ClassSummary> classes = ClassDAO.listAllWithId();
                StringBuilder sbc = new StringBuilder();
                for (ClassDAO.ClassSummary cs : classes)
                    sbc.append(cs.id).append(": ").append(cs.name).append("\n");

                String cidStr = JOptionPane.showInputDialog(this,
                    "Classes:\n" + sbc + "\nEnter class ID:");
                if (cidStr == null) return;

                int cid = Integer.parseInt(cidStr.trim());

                String marksStr = JOptionPane.showInputDialog(this, "Enter marks (integer):");
                if (marksStr == null) return;

                int marks = Integer.parseInt(marksStr.trim());
                MarksDAO.setMarks(sid, cid, marks);

                JOptionPane.showMessageDialog(this, "Marks updated.");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        });

        // ===== View Students in a Class =====
        classes_btn.addActionListener(e -> {
            try {
                List<ClassDAO.ClassSummary> classes = ClassDAO.listAllWithId();
                StringBuilder sb = new StringBuilder();
                for (ClassDAO.ClassSummary cs : classes)
                    sb.append(cs.id).append(": ").append(cs.name).append("\n");

                String cidStr = JOptionPane.showInputDialog(this,
                    "Classes:\n" + sb + "\nEnter class ID to list students:");

                if (cidStr == null) return;

                int cid = Integer.parseInt(cidStr.trim());

                String sql = """
                    SELECT u.id, u.name, u.email
                    FROM class_students cs
                    JOIN users u ON cs.studentId = u.id
                    WHERE cs.classId = ?
                """;

                try (java.sql.Connection con = com.mycompany.vsp2.dao.DatabaseConnection.getConnection();
                     java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

                    ps.setInt(1, cid);
                    try (java.sql.ResultSet rs = ps.executeQuery()) {
                        StringBuilder out = new StringBuilder();
                        while (rs.next()) {
                            out.append(rs.getInt("id"))
                               .append(": ")
                               .append(rs.getString("name"))
                               .append(" (")
                               .append(rs.getString("email"))
                               .append(")\n");
                        }

                        if (out.length() == 0)
                            out.append("No students in this class.");

                        JOptionPane.showMessageDialog(
                            this,
                            new JScrollPane(new JTextArea(out.toString())),
                            "Students",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed: " + ex.getMessage());
            }
        });

        // ===== STUDENT PROGRESS =====
        studentPerformance_btn.addActionListener(e -> showStudentProgress());
    }

    // =======================================================
    //          STUDENT PROGRESS IMPLEMENTATION
    // =======================================================
    private void showStudentProgress() {
        try {
            // Get all students
            List<User> students = UserDAO.findStudents();
            StringBuilder sb = new StringBuilder();
            for (User s : students)
                sb.append(s.getId()).append(": ").append(s.getName()).append("\n");

            String sidStr = JOptionPane.showInputDialog(this,
                    "Students:\n" + sb + "\nEnter student ID:");

            if (sidStr == null) return;

            int sid = Integer.parseInt(sidStr.trim());

            // Query marks for that student
            String sql = """
                    SELECT c.className, m.marks
                    FROM marks m
                    JOIN classroom c ON m.classId = c.classId
                    WHERE m.studentId = ?
                """;

            StringBuilder out = new StringBuilder();

            try (java.sql.Connection con = com.mycompany.vsp2.dao.DatabaseConnection.getConnection();
                 java.sql.PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, sid);

                try (java.sql.ResultSet rs = ps.executeQuery()) {

                    out.append("Marks for Student ID ").append(sid).append(":\n\n");

                    while (rs.next()) {
                        out.append(rs.getString("className"))
                           .append(" → ")
                           .append(rs.getInt("marks"))
                           .append("\n");
                    }
                }
            }

            JOptionPane.showMessageDialog(
                this,
                new JScrollPane(new JTextArea(out.toString())),
                "Student Performance",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }


    @SuppressWarnings("unchecked")
    private void initComponents() {
        classes_btn = new javax.swing.JButton();
        takeExam_btn = new javax.swing.JButton();
        evaluate_btn = new javax.swing.JButton();
        studentPerformance_btn = new javax.swing.JButton();
        createclasses_btn = new javax.swing.JButton();
        logout_btn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Teacher Dashboard");

        classes_btn.setText("Classes");
        takeExam_btn.setText("Upload Material");
        evaluate_btn.setText("Evaluate");
        studentPerformance_btn.setText("Student Progress");
        createclasses_btn.setFont(new java.awt.Font("Segoe UI Historic", 0, 18));
        createclasses_btn.setText("Create Class +");
        logout_btn.setText("Logout");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addComponent(classes_btn)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(takeExam_btn)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(evaluate_btn)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(studentPerformance_btn)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(logout_btn)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                    .addComponent(createclasses_btn)
                    .addGap(25, 25, 25))
        );

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addGap(20, 20, 20)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(classes_btn)
                        .addComponent(takeExam_btn)
                        .addComponent(evaluate_btn)
                        .addComponent(studentPerformance_btn)
                        .addComponent(logout_btn)
                        .addComponent(createclasses_btn, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(460, Short.MAX_VALUE))
        );

        pack();
    }

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> new TeacherDashboard().setVisible(true));
    }

    private javax.swing.JButton classes_btn;
    private javax.swing.JButton createclasses_btn;
    private javax.swing.JButton evaluate_btn;
    private javax.swing.JButton studentPerformance_btn;
    private javax.swing.JButton takeExam_btn;
}
