package com.ecs.library.ui.components;

import com.ecs.library.security.UserSession;
import com.ecs.library.service.AuthService;
import com.ecs.library.service.ServiceFactory;

import javax.swing.*;
import java.awt.*;

/**
 * Panel genérico de Cambio de Contraseña.
 * Aplica DRY: Reemplaza las 3 clases duplicadas (PasswordPanel, LibrarianPasswordPanel,
 * StudentPasswordPanel) unificando la lógica de validación y hashing seguro.
 */
public class BasePasswordPanel extends JPanel {

    private final JPasswordField oldPasswordField = new JPasswordField(20);
    private final JPasswordField newPasswordField = new JPasswordField(20);
    private final JPasswordField confirmPasswordField = new JPasswordField(20);
    private final AuthService authService;

    public BasePasswordPanel() {
        this.authService = ServiceFactory.getInstance().getAuthService();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(UITheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(UITheme.CARD_BORDER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Seguridad: Cambio de Contraseña");
        title.setFont(UITheme.TITLE_FONT);
        title.setForeground(UITheme.PRIMARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        card.add(title, gbc);

        gbc.gridwidth = 1;

        // Contraseña actual
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblOld = new JLabel("Contraseña Actual:");
        lblOld.setFont(UITheme.BOLD_FONT);
        card.add(lblOld, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        oldPasswordField.setFont(UITheme.BODY_FONT);
        oldPasswordField.setPreferredSize(new Dimension(280, 34));
        card.add(oldPasswordField, gbc);

        // Nueva contraseña
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblNew = new JLabel("Nueva Contraseña:");
        lblNew.setFont(UITheme.BOLD_FONT);
        card.add(lblNew, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        newPasswordField.setFont(UITheme.BODY_FONT);
        newPasswordField.setPreferredSize(new Dimension(280, 34));
        card.add(newPasswordField, gbc);

        // Confirmar nueva contraseña
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblConfirm = new JLabel("Confirmar Nueva Contraseña:");
        lblConfirm.setFont(UITheme.BOLD_FONT);
        card.add(lblConfirm, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        confirmPasswordField.setFont(UITheme.BODY_FONT);
        confirmPasswordField.setPreferredSize(new Dimension(280, 34));
        card.add(confirmPasswordField, gbc);

        // Botón
        gbc.gridx = 1; gbc.gridy = 4;
        JButton btnChange = new JButton("Actualizar Contraseña");
        btnChange.setFont(UITheme.BOLD_FONT);
        btnChange.setBackground(UITheme.SECONDARY);
        btnChange.setPreferredSize(new Dimension(200, 38));
        btnChange.addActionListener(e -> handleChangePassword());
        card.add(btnChange, gbc);

        add(card, BorderLayout.NORTH);
    }

    private void handleChangePassword() {
        String oldPass = new String(oldPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Las nuevas contraseñas no coinciden.",
                    "Error de coincidencia", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "La contraseña debe tener al menos 6 caracteres.",
                    "Seguridad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = UserSession.getUsername();
        boolean success = authService.updatePassword(username, oldPass, newPass);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "¡Contraseña actualizada exitosamente con cifrado seguro SHA-256!",
                    "Operación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            oldPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "La contraseña actual ingresada es incorrecta.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
