package com.ecs.library.ui;

import com.ecs.library.config.DatabaseConnection;
import com.ecs.library.model.UserRole;
import com.ecs.library.ui.admin.AdminDashboardFrame;
import com.ecs.library.ui.components.BaseLoginDialog;
import com.ecs.library.ui.components.UITheme;
import com.ecs.library.ui.librarian.LibrarianDashboardFrame;
import com.ecs.library.ui.student.StudentDashboardFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de Entrada Principal (Main Application Launcher).
 * Arquitectura Refactorizada v2.0 - Proyecto Evolución y Configuración de Software (UPN).
 * Unifica el flujo de inicio, provee diagnóstico de conexión a BD y selección de roles con polimorfismo.
 */
public class MainApp extends JFrame {

    private final JLabel dbStatusLabel = new JLabel("Verificando base de datos...");

    public MainApp() {
        super("Sistema de Gestión de Biblioteca - UPN (Arquitectura Refactorizada v2.0)");
        initUI();
        checkDatabaseConnectivity();
    }

    private void initUI() {
        setSize(780, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND);

        // Header
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 6, 6));
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel titleLabel = new JLabel("SISTEMA DE GESTIÓN BIBLIOTECARIA", JLabel.CENTER);
        titleLabel.setFont(UITheme.TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Evolución y Configuración de Software | Versión 2.0 (Clean Architecture)", JLabel.CENTER);
        subtitleLabel.setFont(UITheme.BODY_FONT);
        subtitleLabel.setForeground(new Color(210, 225, 245));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel: Role Selection Cards
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        centerPanel.setBackground(UITheme.BACKGROUND);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(35, 40, 35, 40));

        centerPanel.add(createRoleCard("ADMINISTRADOR",
                "Gestión de personal, seguridad y configuración general del sistema.",
                UITheme.PRIMARY,
                () -> openLogin(UserRole.ADMIN)));

        centerPanel.add(createRoleCard("BIBLIOTECARIO",
                "Control de inventario de libros, préstamos, devoluciones y estudiantes.",
                UITheme.SECONDARY,
                () -> openLogin(UserRole.LIBRARIAN)));

        centerPanel.add(createRoleCard("ESTUDIANTE",
                "Consulta de catálogo, búsqueda de libros y seguimiento de préstamos.",
                new Color(46, 117, 182),
                () -> openLogin(UserRole.STUDENT)));

        add(centerPanel, BorderLayout.CENTER);

        // Footer Bar con Diagnóstico de Conexión
        JPanel footerBar = new JPanel(new BorderLayout());
        footerBar.setBackground(new Color(30, 39, 46));
        footerBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel versionLabel = new JLabel("LMS v2.0 | UPN 2026 | Arquitectura SOLID & Clean Code");
        versionLabel.setFont(UITheme.SMALL_FONT);
        versionLabel.setForeground(Color.LIGHT_GRAY);

        dbStatusLabel.setFont(UITheme.SMALL_FONT);
        dbStatusLabel.setForeground(Color.YELLOW);

        footerBar.add(versionLabel, BorderLayout.WEST);
        footerBar.add(dbStatusLabel, BorderLayout.EAST);
        add(footerBar, BorderLayout.SOUTH);
    }

    private JPanel createRoleCard(String roleTitle, String description, Color accentColor, Runnable onSelect) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(UITheme.CARD_BG);
        card.setBorder(UITheme.CARD_BORDER);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(accentColor);
        JLabel title = new JLabel(roleTitle);
        title.setFont(UITheme.SUBTITLE_FONT);
        title.setForeground(Color.WHITE);
        header.add(title);

        JTextArea descArea = new JTextArea(description);
        descArea.setFont(UITheme.BODY_FONT);
        descArea.setForeground(UITheme.TEXT_DARK);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));

        JButton enterBtn = new JButton("Acceder");
        enterBtn.setFont(UITheme.BOLD_FONT);
        enterBtn.setBackground(accentColor);
        enterBtn.setForeground(Color.BLACK);
        enterBtn.setPreferredSize(new Dimension(100, 36));
        enterBtn.addActionListener(e -> onSelect.run());

        card.add(header, BorderLayout.NORTH);
        card.add(descArea, BorderLayout.CENTER);
        card.add(enterBtn, BorderLayout.SOUTH);

        return card;
    }

    private void openLogin(UserRole role) {
        BaseLoginDialog dialog = new BaseLoginDialog(this, role, () -> {
            setVisible(false);
            if (role == UserRole.ADMIN) {
                new AdminDashboardFrame(this::showSelf).setVisible(true);
            } else if (role == UserRole.LIBRARIAN) {
                new LibrarianDashboardFrame(this::showSelf).setVisible(true);
            } else if (role == UserRole.STUDENT) {
                new StudentDashboardFrame(this::showSelf).setVisible(true);
            }
        });
        dialog.setVisible(true);
    }

    private void showSelf() {
        setVisible(true);
    }

    private void checkDatabaseConnectivity() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.getInstance().testConnection();
            }

            @Override
            protected void done() {
                try {
                    boolean connected = get();
                    if (connected) {
                        dbStatusLabel.setText("● Base de Datos: Conectada (" + DatabaseConnection.getInstance().getDatabaseName() + ")");
                        dbStatusLabel.setForeground(new Color(46, 204, 113));
                    } else {
                        dbStatusLabel.setText("○ Base de Datos: Sin Conexión (Modo Offline / Configurar DB)");
                        dbStatusLabel.setForeground(new Color(231, 76, 60));
                    }
                } catch (Exception e) {
                    dbStatusLabel.setText("○ Error de Conexión");
                    dbStatusLabel.setForeground(Color.RED);
                }
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new MainApp().setVisible(true);
        });
    }
}
