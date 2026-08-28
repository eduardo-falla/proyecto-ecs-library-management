package com.ecs.library.ui.librarian;

import com.ecs.library.model.Book;
import com.ecs.library.model.BookStatus;
import com.ecs.library.service.BookService;
import com.ecs.library.service.ServiceFactory;
import com.ecs.library.ui.components.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel de Gestión Completa de Libros (CRUD y Búsqueda).
 * Convierte el antiguo cascarón vacío (ManageBookPanel) en un módulo de inventario robusto y funcional.
 */
public class BookManagementPanel extends JPanel {

    private final BookService bookService;
    private final JTable bookTable;
    private final DefaultTableModel tableModel;
    private final JTextField searchField = new JTextField(20);

    public BookManagementPanel() {
        this.bookService = ServiceFactory.getInstance().getBookService();
        this.tableModel = new DefaultTableModel(
                new Object[]{"ID", "ISBN", "Título", "Autor", "Editorial", "Año", "Categoría", "Stock Total", "Disponible", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.bookTable = new JTable(tableModel);

        initUI();
        loadBooks(null);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BACKGROUND);
        setBorder(UITheme.PADDING_BORDER);

        // Barra Superior: Búsqueda y Botones de Acción
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(UITheme.BACKGROUND);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setBackground(UITheme.BACKGROUND);
        JLabel searchLabel = new JLabel("Buscar Libro:");
        searchLabel.setFont(UITheme.BOLD_FONT);
        searchField.setFont(UITheme.BODY_FONT);
        searchField.setPreferredSize(new Dimension(250, 32));

        JButton searchButton = new JButton("Buscar");
        searchButton.setFont(UITheme.BODY_FONT);
        searchButton.addActionListener(e -> loadBooks(searchField.getText().trim()));

        JButton clearButton = new JButton("Limpiar");
        clearButton.setFont(UITheme.BODY_FONT);
        clearButton.addActionListener(e -> {
            searchField.setText("");
            loadBooks(null);
        });

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearButton);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actionsPanel.setBackground(UITheme.BACKGROUND);

        JButton addButton = new JButton("+ Nuevo Libro");
        addButton.setFont(UITheme.BOLD_FONT);
        addButton.setBackground(UITheme.ACCENT_GREEN);
        addButton.setForeground(Color.BLACK);
        addButton.addActionListener(e -> showBookDialog(null));

        JButton editButton = new JButton("Editar");
        editButton.setFont(UITheme.BODY_FONT);
        editButton.addActionListener(e -> handleEditSelected());

        JButton deleteButton = new JButton("Eliminar");
        deleteButton.setFont(UITheme.BODY_FONT);
        deleteButton.addActionListener(e -> handleDeleteSelected());

        JButton refreshButton = new JButton("Actualizar");
        refreshButton.setFont(UITheme.BODY_FONT);
        refreshButton.addActionListener(e -> loadBooks(null));

        actionsPanel.add(addButton);
        actionsPanel.add(editButton);
        actionsPanel.add(deleteButton);
        actionsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionsPanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Tabla central
        bookTable.setFont(UITheme.BODY_FONT);
        bookTable.setRowHeight(26);
        bookTable.getTableHeader().setFont(UITheme.BOLD_FONT);
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(UITheme.CARD_BORDER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadBooks(String keyword) {
        tableModel.setRowCount(0);
        List<Book> books;
        if (keyword != null && !keyword.isEmpty()) {
            books = bookService.searchBooks(keyword);
        } else {
            books = bookService.getAllBooks();
        }

        for (Book b : books) {
            tableModel.addRow(new Object[]{
                    b.getBookId(),
                    b.getIsbn(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getPublisher(),
                    b.getPublicationYear(),
                    b.getCategoryName() != null ? b.getCategoryName() : "General",
                    b.getTotalCopies(),
                    b.getAvailableCopies(),
                    b.getStatus().getDescription()
            });
        }
    }

    private void handleEditSelected() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro de la tabla para editar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        bookService.getBookById(bookId).ifPresent(this::showBookDialog);
    }

    private void handleDeleteSelected() {
        int selectedRow = bookTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un libro para eliminar.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bookId = (int) tableModel.getValueAt(selectedRow, 0);
        String title = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de eliminar el libro: '" + title + "'?",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = bookService.deleteBook(bookId);
                if (deleted) {
                    JOptionPane.showMessageDialog(this, "Libro eliminado correctamente.");
                    loadBooks(null);
                } else {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar el libro.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: Puede que el libro tenga préstamos asociados activos.",
                        "Restricción de Integridad", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showBookDialog(Book existingBook) {
        boolean isEdit = existingBook != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Editar Libro" : "Registrar Nuevo Libro", true);
        dialog.setSize(480, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(8, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField isbnField = new JTextField(isEdit ? existingBook.getIsbn() : "");
        JTextField titleField = new JTextField(isEdit ? existingBook.getTitle() : "");
        JTextField authorField = new JTextField(isEdit ? existingBook.getAuthor() : "");
        JTextField publisherField = new JTextField(isEdit ? existingBook.getPublisher() : "");
        JTextField yearField = new JTextField(isEdit ? String.valueOf(existingBook.getPublicationYear()) : "2024");
        JTextField copiesField = new JTextField(isEdit ? String.valueOf(existingBook.getTotalCopies()) : "3");
        JTextField locationField = new JTextField(isEdit ? existingBook.getShelfLocation() : "Estante General");
        JComboBox<BookStatus> statusCombo = new JComboBox<>(BookStatus.values());
        if (isEdit) statusCombo.setSelectedItem(existingBook.getStatus());

        form.add(new JLabel("ISBN:"));
        form.add(isbnField);
        form.add(new JLabel("Título:"));
        form.add(titleField);
        form.add(new JLabel("Autor:"));
        form.add(authorField);
        form.add(new JLabel("Editorial:"));
        form.add(publisherField);
        form.add(new JLabel("Año Publicación:"));
        form.add(yearField);
        form.add(new JLabel("Ejemplares Totales:"));
        form.add(copiesField);
        form.add(new JLabel("Ubicación en Estante:"));
        form.add(locationField);
        form.add(new JLabel("Estado:"));
        form.add(statusCombo);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("Guardar");
        saveBtn.setFont(UITheme.BOLD_FONT);
        saveBtn.setBackground(UITheme.SECONDARY);
        saveBtn.addActionListener(e -> {
            try {
                String isbn = isbnField.getText().trim();
                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                int year = Integer.parseInt(yearField.getText().trim());
                int totalCopies = Integer.parseInt(copiesField.getText().trim());

                if (isEdit) {
                    existingBook.setIsbn(isbn);
                    existingBook.setTitle(title);
                    existingBook.setAuthor(author);
                    existingBook.setPublisher(publisherField.getText().trim());
                    existingBook.setPublicationYear(year);
                    existingBook.setTotalCopies(totalCopies);
                    existingBook.setShelfLocation(locationField.getText().trim());
                    existingBook.setStatus((BookStatus) statusCombo.getSelectedItem());
                    bookService.updateBook(existingBook);
                } else {
                    Book newBook = new Book();
                    newBook.setIsbn(isbn);
                    newBook.setTitle(title);
                    newBook.setAuthor(author);
                    newBook.setPublisher(publisherField.getText().trim());
                    newBook.setPublicationYear(year);
                    newBook.setTotalCopies(totalCopies);
                    newBook.setShelfLocation(locationField.getText().trim());
                    newBook.setStatus((BookStatus) statusCombo.getSelectedItem());
                    bookService.registerBook(newBook);
                }

                dialog.dispose();
                loadBooks(null);
                JOptionPane.showMessageDialog(this, "Libro guardado con éxito.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error al guardar", JOptionPane.ERROR_MESSAGE);
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
