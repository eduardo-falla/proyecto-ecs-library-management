package com.ecs.library.ui.components;

import com.ecs.library.model.UserRole;
import com.ecs.library.security.UserSession;
import com.ecs.library.service.AuthService;
import com.ecs.library.service.ServiceFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Diálogo de Login Genérico y Polimórfico.
 * Aplica DRY: Elimina la duplicación de código que existía en AdminLogin,
 * LibrarianLogin y StudentLogin en el sistema legado.
 * Aplica OCP: Puede soportar cualquier rol nuevo sin modificar el layout ni duplicar clases.
 */
public class BaseLoginDialog extends JDialog {

    @FunctionalInterface
    public interface LoginSuccessCallback {
        void onLoginSuccess();
    }

    private final UserRole expectedRole;
    private final JTextField usernameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final AuthService authService;
    private final LoginSuccessCallback successCallback;

    public BaseLoginDialog(Frame owner, UserRole expectedRole, LoginSuccessCallback successCallback) {
        super(owner, "Inicio de Sesión - " + expectedRole.getDisplayName(), true);
        this.expectedRole = expectedRole;
        this.successCallback = successCallback;
        this.authService = ServiceFactory.getInstance().getAuthService();

        initUI();
    }

    private void initUI() {
        setSize(440, 360);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BACKGROUND);

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 4, 4));
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JLabel titleLabel = new JLabel("SISTEMA DE GESTIÓN BIBLIOTECARIA", JLabel.CENTER);
        titleLabel.setFont(UITheme.SUBTITLE_FONT);
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Acceso para: " + expectedRole.getDisplayName(), JLabel.CENTER);
        subtitleLabel.setFont(UITheme.BODY_FONT);
        subtitleLabel.setForeground(new Color(200, 220, 240));

        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Usuario / Código:");
        userLabel.setFont(UITheme.BOLD_FONT);
        formPanel.add(userLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        usernameField.setFont(UITheme.BODY_FONT);
        usernameField.setPreferredSize(new Dimension(300, 36));
        formPanel.add(usernameField, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(UITheme.BOLD_FONT);
        formPanel.add(passLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        passwordField.setFont(UITheme.BODY_FONT);
        passwordField.setPreferredSize(new Dimension(300, 36));
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        buttonsPanel.setBackground(UITheme.BACKGROUND);

        JButton cancelButton = new JButton("Cancelar");
        cancelButton.setFont(UITheme.BODY_FONT);
        cancelButton.setPreferredSize(new Dimension(110, 36));
        cancelButton.addActionListener(e -> dispose());

        JButton loginButton = new JButton("Ingresar");
        loginButton.setFont(UITheme.BOLD_FONT);
        loginButton.setBackground(UITheme.SECONDARY);
        loginButton.setForeground(Color.BLACK);
        loginButton.setPreferredSize(new Dimension(120, 36));
        loginButton.addActionListener(e -> handleLogin());

        // Permitir Enter para login
        getRootPane().setDefaultButton(loginButton);

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(loginButton);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor complete todos los campos obligatorios.",
                    "Campos requeridos",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = authService.authenticate(username, password, expectedRole);
        if (success) {
            dispose();
            if (successCallback != null) {
                successCallback.onLoginSuccess();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Credenciales incorrectas o usuario sin permisos para este rol.",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
