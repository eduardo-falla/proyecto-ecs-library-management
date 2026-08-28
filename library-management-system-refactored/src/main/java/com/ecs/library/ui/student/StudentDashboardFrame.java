package com.ecs.library.ui.student;

import com.ecs.library.model.Book;
import com.ecs.library.model.Loan;
import com.ecs.library.model.Student;
import com.ecs.library.security.UserSession;
import com.ecs.library.service.BookService;
import com.ecs.library.service.LoanService;
import com.ecs.library.service.ServiceFactory;
import com.ecs.library.service.StudentService;
import com.ecs.library.ui.components.BasePasswordPanel;
import com.ecs.library.ui.components.UITheme;
import com.ecs.library.util.DateUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public class StudentDashboardFrame extends JFrame {

    private final Runnable onLogoutCallback;
    private final BookService bookService;
    private final LoanService loanService;
    private final StudentService studentService;

    public StudentDashboardFrame(Runnable onLogoutCallback) {
        super("Portal del Estudiante - Biblioteca Universitaria");
        this.onLogoutCallback = onLogoutCallback;
        ServiceFactory factory = ServiceFactory.getInstance();
        this.bookService = factory.getBookService();
        this.loanService = factory.getLoanService();
        this.studentService = factory.getStudentService();

        initUI();
    }

    private void initUI() {
        setSize(1080, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("PORTAL ACADÉMICO DE BIBLIOTECA");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(Color.WHITE);

        JPanel rightUserPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightUserPanel.setBackground(UITheme.PRIMARY);

        JLabel userLbl = new JLabel("Estudiante: " + UserSession.getUsername());
        userLbl.setFont(UITheme.BOLD_FONT);
        userLbl.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Cerrar Sesión");
        logoutBtn.setFont(UITheme.SMALL_FONT);
        logoutBtn.addActionListener(e -> {
            UserSession.clear();
            dispose();
            if (onLogoutCallback != null) onLogoutCallback.run();
        });

        rightUserPanel.add(userLbl);
        rightUserPanel.add(logoutBtn);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightUserPanel, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.SUBTITLE_FONT);

        tabbedPane.addTab("Catálogo de Libros", createCatalogPanel());
        tabbedPane.addTab("Mis Préstamos y Estado", createMyLoansPanel());
        tabbedPane.addTab("Mi Contraseña", new BasePasswordPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCatalogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(UITheme.PADDING_BORDER);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ISBN", "Título", "Autor", "Editorial", "Año", "Categoría", "Disponibles", "Ubicación"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(UITheme.BODY_FONT);
        table.setRowHeight(26);
        table.getTableHeader().setFont(UITheme.BOLD_FONT);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setBackground(UITheme.BACKGROUND);
        JTextField searchTxt = new JTextField(20);
        searchTxt.setFont(UITheme.BODY_FONT);

        Runnable loadCatalog = () -> {
            model.setRowCount(0);
            String q = searchTxt.getText().trim();
            List<Book> list = q.isEmpty() ? bookService.getAllBooks() : bookService.searchBooks(q);
            for (Book b : list) {
                model.addRow(new Object[]{
                        b.getIsbn(),
                        b.getTitle(),
                        b.getAuthor(),
                        b.getPublisher(),
                        b.getPublicationYear(),
                        b.getCategoryName() != null ? b.getCategoryName() : "General",
                        b.getAvailableCopies(),
                        b.getShelfLocation()
                });
            }
        };

        JButton searchBtn = new JButton("Buscar");
        searchBtn.addActionListener(e -> loadCatalog.run());
        JButton clearBtn = new JButton("Ver Todos");
        clearBtn.addActionListener(e -> { searchTxt.setText(""); loadCatalog.run(); });

        searchPanel.add(new JLabel("Buscar por Título o Autor:"));
        searchPanel.add(searchTxt);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadCatalog.run();
        return panel;
    }

    private JPanel createMyLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BACKGROUND);
        panel.setBorder(UITheme.PADDING_BORDER);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Título del Libro", "ISBN", "Fecha Préstamo", "Fecha Vencimiento", "Devuelto el", "Estado", "Mora (S/.)"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setFont(UITheme.BODY_FONT);
        table.setRowHeight(26);
        table.getTableHeader().setFont(UITheme.BOLD_FONT);

        Runnable loadMyLoans = () -> {
            model.setRowCount(0);
            if (UserSession.getCurrentUser() != null) {
                Optional<Student> stdOpt = studentService.getStudentByUserId(UserSession.getCurrentUser().getUserId());
                if (stdOpt.isPresent()) {
                    List<Loan> myLoans = loanService.getLoansByStudent(stdOpt.get().getStudentId());
                    for (Loan l : myLoans) {
                        model.addRow(new Object[]{
                                l.getLoanId(),
                                l.getBookTitle(),
                                l.getBookIsbn(),
                                DateUtil.format(l.getIssueDate()),
                                DateUtil.format(l.getDueDate()),
                                l.getReturnDate() != null ? DateUtil.format(l.getReturnDate()) : "En Préstamo",
                                l.getStatus().getDescription(),
                                l.getFineAmount() != null ? String.format("%.2f", l.getFineAmount()) : "0.00"
                        });
                    }
                }
            }
        };

        JPanel top = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        top.setBackground(UITheme.BACKGROUND);
        JButton refreshBtn = new JButton("Actualizar Mis Préstamos");
        refreshBtn.addActionListener(e -> loadMyLoans.run());
        top.add(refreshBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        loadMyLoans.run();
        return panel;
    }
}
