package com.ecs.library.ui.admin;

import com.ecs.library.model.Gender;
import com.ecs.library.model.Librarian;
import com.ecs.library.service.LibrarianService;
import com.ecs.library.service.ServiceFactory;
import com.ecs.library.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrarianManagementPanel extends JPanel {

    private final LibrarianService librarianService;
    private final JTable table;
    private final DefaultTableModel tableModel;

    public LibrarianManagementPanel() {
        this.librarianService = ServiceFactory.getInstance().getLibrarianService();
        this.tableModel = new DefaultTableModel(
                new Object[]{"ID", "Staff ID", "Nombre Completo", "Usuario", "Email", "Género", "Teléfono", "Fecha Contrato"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);

        initUI();
        loadLibrarians();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);
        setBorder(UITheme.PADDING_BORDER);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topBar.setBackground(UITheme.BACKGROUND);

        JButton addBtn = new JButton("+ Registrar Bibliotecario");
        addBtn.setFont(UITheme.BOLD_FONT);
        addBtn.setBackground(UITheme.ACCENT_GREEN);
        addBtn.setForeground(Color.BLACK);
        addBtn.addActionListener(e -> showRegisterLibrarianDialog());

        JButton deleteBtn = new JButton("Eliminar");
        deleteBtn.setFont(UITheme.BODY_FONT);
        deleteBtn.addActionListener(e -> handleDeleteLibrarian());

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.setFont(UITheme.BODY_FONT);
        refreshBtn.addActionListener(e -> loadLibrarians());

        topBar.add(addBtn);
        topBar.add(deleteBtn);
        topBar.add(refreshBtn);
        add(topBar, BorderLayout.NORTH);

        table.setFont(UITheme.BODY_FONT);
        table.setRowHeight(26);
        table.getTableHeader().setFont(UITheme.BOLD_FONT);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(UITheme.CARD_BORDER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLibrarians() {
        tableModel.setRowCount(0);
        List<Librarian> list = librarianService.getAllLibrarians();
        for (Librarian lib : list) {
            tableModel.addRow(new Object[]{
                    lib.getLibrarianId(),
                    lib.getStaffId(),
                    lib.getFullName(),
                    lib.getUsername(),
                    lib.getEmail(),
                    lib.getGender().getDescription(),
                    lib.getPhone(),
                    lib.getHiredDate()
            });
        }
    }

    private void handleDeleteLibrarian() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un bibliotecario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int libId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar a " + name + " del personal de biblioteca?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            librarianService.getLibrarianByStaffId((String) tableModel.getValueAt(selectedRow, 1))
                    .ifPresent(l -> {
                        librarianService.deleteLibrarian(l.getLibrarianId(), l.getUserId());
                        loadLibrarians();
                        JOptionPane.showMessageDialog(this, "Bibliotecario eliminado exitosamente.");
                    });
        }
    }

    private void showRegisterLibrarianDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Personal Bibliotecario", true);
        dialog.setSize(460, 440);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField staffIdField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField usernameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<Gender> genderCombo = new JComboBox<>(Gender.values());
        JTextField phoneField = new JTextField();

        form.add(new JLabel("Staff ID / Código:"));
        form.add(staffIdField);
        form.add(new JLabel("Nombre Completo:"));
        form.add(nameField);
        form.add(new JLabel("Usuario de Acceso:"));
        form.add(usernameField);
        form.add(new JLabel("Email Institucional:"));
        form.add(emailField);
        form.add(new JLabel("Género:"));
        form.add(genderCombo);
        form.add(new JLabel("Teléfono de Contacto:"));
        form.add(phoneField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Registrar");
        saveBtn.setFont(UITheme.BOLD_FONT);
        saveBtn.setBackground(UITheme.SECONDARY);
        saveBtn.addActionListener(e -> {
            try {
                Librarian lib = new Librarian();
                lib.setStaffId(staffIdField.getText().trim());
                lib.setFullName(nameField.getText().trim());
                lib.setUsername(usernameField.getText().trim());
                lib.setEmail(emailField.getText().trim());
                lib.setGender((Gender) genderCombo.getSelectedItem());
                lib.setPhone(phoneField.getText().trim());

                librarianService.registerLibrarian(lib, "lib123");
                dialog.dispose();
                loadLibrarians();
                JOptionPane.showMessageDialog(this, "Bibliotecario registrado exitosamente con clave temporal: lib123");
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
