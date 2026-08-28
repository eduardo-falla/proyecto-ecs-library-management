package com.ecs.library.ui.librarian;

import com.ecs.library.security.UserSession;
import com.ecs.library.ui.components.BasePasswordPanel;
import com.ecs.library.ui.components.UITheme;

import javax.swing.*;
import java.awt.*;

public class LibrarianDashboardFrame extends JFrame {

    private final Runnable onLogoutCallback;

    public LibrarianDashboardFrame(Runnable onLogoutCallback) {
        super("Panel de Bibliotecario - LMS v2.0 (Refactorizado)");
        this.onLogoutCallback = onLogoutCallback;
        initUI();
    }

    private void initUI() {
        setSize(1180, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header Superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(UITheme.PRIMARY);
        topBar.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel title = new JLabel("PANEL DE GESTIÓN BIBLIOTECARIA");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(Color.WHITE);

        JPanel rightUserPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightUserPanel.setBackground(UITheme.PRIMARY);

        JLabel userLbl = new JLabel("Bibliotecario: " + UserSession.getUsername());
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

        // Pestañas Principales Restauradas y Refactorizadas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.SUBTITLE_FONT);

        tabbedPane.addTab("Inventario de Libros", new BookManagementPanel());
        tabbedPane.addTab("Préstamos y Devoluciones", new LoanManagementPanel());
        tabbedPane.addTab("Gestión de Estudiantes", new StudentManagementPanel());
        tabbedPane.addTab("Seguridad / Clave", new BasePasswordPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }
}
