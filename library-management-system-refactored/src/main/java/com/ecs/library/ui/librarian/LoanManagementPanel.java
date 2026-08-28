package com.ecs.library.ui.librarian;

import com.ecs.library.model.Book;
import com.ecs.library.model.Loan;
import com.ecs.library.model.Student;
import com.ecs.library.service.BookService;
import com.ecs.library.service.LoanService;
import com.ecs.library.service.ServiceFactory;
import com.ecs.library.service.StudentService;
import com.ecs.library.ui.components.UITheme;
import com.ecs.library.util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de Gestión de Préstamos y Devoluciones.
 * Reemplaza el archivo vacío 'ManageBorrowerPanel.java' del sistema legado.
 * Automatiza el control de inventario, cálculo de fechas de vencimiento y moras.
 */
public class LoanManagementPanel extends JPanel {

    private final LoanService loanService;
    private final BookService bookService;
    private final StudentService studentService;

    private final JTable loanTable;
    private final DefaultTableModel tableModel;
    private final JComboBox<String> filterCombo = new JComboBox<>(new String[]{
            "Préstamos Activos", "Préstamos Vencidos (Mora)", "Historial Completo"
    });

    public LoanManagementPanel() {
        ServiceFactory factory = ServiceFactory.getInstance();
        this.loanService = factory.getLoanService();
        this.bookService = factory.getBookService();
        this.studentService = factory.getStudentService();

        this.tableModel = new DefaultTableModel(
                new Object[]{"ID", "Libro", "ISBN", "Estudiante", "Reg. No", "Fecha Préstamo", "Fecha Límite", "Devolución", "Estado", "Mora (S/.)"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.loanTable = new JTable(tableModel);

        initUI();
        loadLoans();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);
        setBorder(UITheme.PADDING_BORDER);

        // Header Panel con Filtros y Botones
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BACKGROUND);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.setBackground(UITheme.BACKGROUND);
        JLabel filterLabel = new JLabel("Mostrar:");
        filterLabel.setFont(UITheme.BOLD_FONT);
        filterCombo.setFont(UITheme.BODY_FONT);
        filterCombo.addActionListener(e -> loadLoans());
        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.setBackground(UITheme.BACKGROUND);

        JButton issueBtn = new JButton("+ Prestar Libro");
        issueBtn.setFont(UITheme.BOLD_FONT);
        issueBtn.setBackground(UITheme.ACCENT_GREEN);
        issueBtn.setForeground(Color.BLACK);
        issueBtn.addActionListener(e -> showIssueLoanDialog());

        JButton returnBtn = new JButton("Registrar Devolución");
        returnBtn.setFont(UITheme.BOLD_FONT);
        returnBtn.setBackground(UITheme.SECONDARY);
        returnBtn.setForeground(Color.BLACK);
        returnBtn.addActionListener(e -> handleReturnBook());

        JButton refreshBtn = new JButton("Actualizar");
        refreshBtn.setFont(UITheme.BODY_FONT);
        refreshBtn.addActionListener(e -> loadLoans());

        actionPanel.add(issueBtn);
        actionPanel.add(returnBtn);
        actionPanel.add(refreshBtn);

        topPanel.add(filterPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabla
        loanTable.setFont(UITheme.BODY_FONT);
        loanTable.setRowHeight(26);
        loanTable.getTableHeader().setFont(UITheme.BOLD_FONT);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(UITheme.CARD_BORDER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadLoans() {
        tableModel.setRowCount(0);
        int selectedFilter = filterCombo.getSelectedIndex();

        List<Loan> list;
        if (selectedFilter == 0) {
            list = loanService.getActiveLoans();
        } else if (selectedFilter == 1) {
            list = loanService.getOverdueLoans();
        } else {
            list = loanService.getAllLoans();
        }

        for (Loan l : list) {
            tableModel.addRow(new Object[]{
                    l.getLoanId(),
                    l.getBookTitle(),
                    l.getBookIsbn(),
                    l.getStudentName(),
                    l.getStudentRegNo(),
                    DateUtil.format(l.getIssueDate()),
                    DateUtil.format(l.getDueDate()),
                    l.getReturnDate() != null ? DateUtil.format(l.getReturnDate()) : "Pendiente",
                    l.getStatus().getDescription(),
                    l.getFineAmount() != null ? String.format("%.2f", l.getFineAmount()) : "0.00"
            });
        }
    }

    private void handleReturnBook() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un préstamo de la tabla para registrar su devolución.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int loanId = (int) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);
        String student = (String) tableModel.getValueAt(selectedRow, 3);
        String status = (String) tableModel.getValueAt(selectedRow, 8);

        if ("Devuelto".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Este libro ya fue devuelto previamente.",
                    "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Confirmar recepción y devolución del libro: '" + bookTitle + "' prestado a " + student + "?",
                "Confirmar Devolución", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = loanService.returnBook(loanId, "Devolución registrada en mostrador");
                if (success) {
                    JOptionPane.showMessageDialog(this, "Devolución procesada con éxito. Stock repuesto al inventario.");
                    loadLoans();
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo procesar la devolución.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showIssueLoanDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Préstamo de Libro", true);
        dialog.setSize(480, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        // Combo de estudiantes
        List<Student> students = studentService.getAllStudents();
        JComboBox<StudentItem> studentCombo = new JComboBox<>();
        for (Student s : students) {
            studentCombo.addItem(new StudentItem(s.getStudentId(), s.getFullName() + " (" + s.getRegNo() + ")"));
        }

        // Combo de libros disponibles
        List<Book> books = bookService.getAvailableBooks();
        JComboBox<BookItem> bookCombo = new JComboBox<>();
        for (Book b : books) {
            bookCombo.addItem(new BookItem(b.getBookId(), b.getTitle() + " [Disp: " + b.getAvailableCopies() + "]"));
        }

        JTextField notesField = new JTextField("Préstamo regular");

        form.add(new JLabel("Seleccionar Estudiante:"));
        form.add(studentCombo);
        form.add(new JLabel("Seleccionar Libro Disponible:"));
        form.add(bookCombo);
        form.add(new JLabel("Plazo de Devolución:"));
        form.add(new JLabel("14 Días calendario (Automático)"));
        form.add(new JLabel("Observaciones / Notas:"));
        form.add(notesField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton issueBtn = new JButton("Confirmar Préstamo");
        issueBtn.setFont(UITheme.BOLD_FONT);
        issueBtn.setBackground(UITheme.ACCENT_GREEN);
        issueBtn.addActionListener(e -> {
            StudentItem selStudent = (StudentItem) studentCombo.getSelectedItem();
            BookItem selBook = (BookItem) bookCombo.getSelectedItem();

            if (selStudent == null || selBook == null) {
                JOptionPane.showMessageDialog(dialog, "Debe seleccionar un estudiante y un libro.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                loanService.issueLoan(selBook.id, selStudent.id, 1, notesField.getText());
                dialog.dispose();
                loadLoans();
                JOptionPane.showMessageDialog(this, "¡Préstamo registrado exitosamente!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Regla de Negocio", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelBtn = new JButton("Cancelar");
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(issueBtn);
        dialog.add(btnPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private static class StudentItem {
        final int id;
        final String label;
        StudentItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }

    private static class BookItem {
        final int id;
        final String label;
        BookItem(int id, String label) { this.id = id; this.label = label; }
        @Override public String toString() { return label; }
    }
}
