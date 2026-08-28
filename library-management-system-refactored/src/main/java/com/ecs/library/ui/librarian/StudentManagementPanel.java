package com.ecs.library.ui.librarian;

import com.ecs.library.model.Gender;
import com.ecs.library.model.Student;
import com.ecs.library.service.ServiceFactory;
import com.ecs.library.service.StudentService;
import com.ecs.library.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.util.List;

/**
 * Panel de Gestión de Estudiantes.
 * Corrige los errores críticos de 'ManageStudentPanel.java' del sistema legado
 * (donde se intentaba agregar un JFileChooser a un JComboBox y los botones no tenían persistencia).
 */
public class StudentManagementPanel extends JPanel {

    private final StudentService studentService;
    private final JTable studentTable;
    private final DefaultTableModel tableModel;

    public StudentManagementPanel() {
        this.studentService = ServiceFactory.getInstance().getStudentService();
        this.tableModel = new DefaultTableModel(
                new Object[]{"ID", "Matrícula / Reg. No", "Nombre Completo", "Usuario", "Email", "Carrera / Dept.", "Género", "Contacto"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.studentTable = new JTable(tableModel);

        initUI();
        loadStudents();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);
        setBorder(UITheme.PADDING_BORDER);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topBar.setBackground(UITheme.BACKGROUND);

        JButton addBtn = new JButton("+ Registrar Estudiante");
        addBtn.setFont(UITheme.BOLD_FONT);
        addBtn.setBackground(UITheme.ACCENT_GREEN);
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e -> showRegisterStudentDialog());

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.setFont(UITheme.BODY_FONT);
        refreshBtn.addActionListener(e -> loadStudents());

        topBar.add(addBtn);
        topBar.add(refreshBtn);
        add(topBar, BorderLayout.NORTH);

        studentTable.setFont(UITheme.BODY_FONT);
        studentTable.setRowHeight(26);
        studentTable.getTableHeader().setFont(UITheme.BOLD_FONT);

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(UITheme.CARD_BORDER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> list = studentService.getAllStudents();
        for (Student s : list) {
            tableModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getRegNo(),
                    s.getFullName(),
                    s.getUsername(),
                    s.getEmail(),
                    s.getDepartment(),
                    s.getGender().getDescription(),
                    s.getContact()
            });
        }
    }

    private void showRegisterStudentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Estudiante", true);
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField regNoField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{
                "Ingeniería de Sistemas", "Ingeniería de Software", "Ciencias de la Computación",
                "Administración y Negocios", "Derecho", "Comunicaciones"
        });
        JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());
        JTextField contactField = new JTextField();

        form.add(new JLabel("Código / Reg. No:"));
        form.add(regNoField);
        form.add(new JLabel("Nombre Completo:"));
        form.add(nameField);
        form.add(new JLabel("Usuario de Acceso:"));
        form.add(usernameField);
        form.add(new JLabel("Correo Electrónico:"));
        form.add(emailField);
        form.add(new JLabel("Carrera / Departamento:"));
        form.add(deptCombo);
        form.add(new JLabel("Género:"));
        form.add(genderCombo);
        form.add(new JLabel("Teléfono / Contacto:"));
        form.add(contactField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Guardar");
        saveBtn.setFont(UITheme.BOLD_FONT);
        saveBtn.setBackground(UITheme.SECONDARY);
        saveBtn.addActionListener(e -> {
            try {
                Student s = new Student();
                s.setRegNo(regNoField.getText().trim());
                s.setFullName(nameField.getText().trim());
                s.setUsername(usernameField.getText().trim());
                s.setEmail(emailField.getText().trim());
                s.setDepartment((String) deptCombo.getSelectedItem());
                s.setGender((Gender) genderCombo.getSelectedItem());
                s.setContact(contactField.getText().trim());
                s.setDateOfBirth(Date.valueOf("2003-01-01"));

                studentService.registerStudent(s, "student123");
                dialog.dispose();
                loadStudents();
                JOptionPane.showMessageDialog(this, "Estudiante registrado con éxito (Clave temporal: student123).");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error al registrar", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
}
