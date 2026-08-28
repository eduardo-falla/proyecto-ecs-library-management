package com.ecs.library.ui.components;

import java.awt.*;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

public class UITheme {
    // Paleta de Colores
    public static final Color PRIMARY = new Color(24, 43, 73);       // Navy Blue elegante
    public static final Color SECONDARY = new Color(41, 128, 185);   // Azul acento
    public static final Color ACCENT_GREEN = new Color(39, 174, 96); // Verde éxito
    public static final Color ACCENT_RED = new Color(192, 57, 43);   // Rojo alerta/mora
    public static final Color BACKGROUND = new Color(245, 247, 250); // Gris claro fondo
    public static final Color CARD_BG = Color.WHITE;
    public static final Color TEXT_DARK = new Color(44, 62, 80);
    public static final Color TEXT_MUTED = new Color(127, 140, 141);

    // Tipografías
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font SMALL_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    // Bordes y paddings
    public static final Border PADDING_BORDER = BorderFactory.createEmptyBorder(15, 15, 15, 15);
    public static final Border CARD_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 224, 230), 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
    );
}
