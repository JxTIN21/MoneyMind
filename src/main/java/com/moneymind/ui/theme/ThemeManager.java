package main.java.com.moneymind.ui.theme;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Centralized theme management for modern UI styling
 */
public class ThemeManager {
    private static boolean isDarkMode = false;
    private static final Map<String, Color> lightTheme = new HashMap<>();
    private static final Map<String, Color> darkTheme = new HashMap<>();

    static {
        initializeThemes();
    }

    private static void initializeThemes() {
        // Light theme colors
        lightTheme.put("background", new Color(248, 249, 250));
        lightTheme.put("surface", Color.WHITE);
        lightTheme.put("primary", new Color(0, 123, 255));
        lightTheme.put("secondary", new Color(108, 117, 125));
        lightTheme.put("accent", new Color(40, 167, 69));
        lightTheme.put("text", new Color(33, 37, 41));
        lightTheme.put("textSecondary", new Color(108, 117, 125));
        lightTheme.put("border", new Color(222, 226, 230));
        lightTheme.put("hover", new Color(248, 249, 250));
        lightTheme.put("success", new Color(40, 167, 69));
        lightTheme.put("warning", new Color(255, 193, 7));
        lightTheme.put("danger", new Color(220, 53, 69));

        // Dark theme colors
        darkTheme.put("background", new Color(33, 37, 41));
        darkTheme.put("surface", new Color(52, 58, 64));
        darkTheme.put("primary", new Color(13, 110, 253));
        darkTheme.put("secondary", new Color(108, 117, 125));
        darkTheme.put("accent", new Color(25, 135, 84));
        darkTheme.put("text", new Color(248, 249, 250));
        darkTheme.put("textSecondary", new Color(173, 181, 189));
        darkTheme.put("border", new Color(73, 80, 87));
        darkTheme.put("hover", new Color(73, 80, 87));
        darkTheme.put("success", new Color(25, 135, 84));
        darkTheme.put("warning", new Color(255, 193, 7));
        darkTheme.put("danger", new Color(220, 53, 69));
    }

    public static void applyModernTheme() {
        try {
            // Set Nimbus look and feel for modern appearance
            UIManager.setLookAndFeel(new NimbusLookAndFeel());

            // Customize Nimbus defaults
            UIManager.put("control", getBackgroundColor());
            UIManager.put("info", getSurfaceColor());
            UIManager.put("nimbusBase", getColor("primary"));
            UIManager.put("nimbusAlertYellow", getColor("warning"));
            UIManager.put("nimbusDisabledText", getColor("textSecondary"));
            UIManager.put("nimbusFocus", getColor("primary"));
            UIManager.put("nimbusGreen", getColor("success"));
            UIManager.put("nimbusInfoBlue", getColor("primary"));
            UIManager.put("nimbusLightBackground", getSurfaceColor());
            UIManager.put("nimbusOrange", getColor("warning"));
            UIManager.put("nimbusRed", getColor("danger"));
            UIManager.put("nimbusSelectedText", Color.WHITE);
            UIManager.put("nimbusSelectionBackground", getColor("primary"));
            UIManager.put("text", getTextColor());

            // Customize specific components
            customizeComponents();

        } catch (Exception e) {
            System.err.println("Failed to set modern theme: " + e.getMessage());
            // Fallback to system look and feel
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                // Use default
            }
        }
    }

    private static void customizeComponents() {
        // Button styling
        UIManager.put("Button.font", getModernFont());
        UIManager.put("Button[Default].backgroundPainter", null);

        // Tab styling
        UIManager.put("TabbedPane.font", getModernFont());
        UIManager.put("TabbedPane.tabAreaBackground", getBackgroundColor());
        UIManager.put("TabbedPane.contentAreaColor", getSurfaceColor());
        UIManager.put("TabbedPane.selected", getColor("primary"));

        // Table styling
        UIManager.put("Table.alternateRowColor", getColor("hover"));
        UIManager.put("Table.background", getSurfaceColor());
        UIManager.put("Table.foreground", getTextColor());
        UIManager.put("Table.selectionBackground", getColor("primary"));
        UIManager.put("Table.selectionForeground", Color.WHITE);
        UIManager.put("Table.gridColor", getColor("border"));

        // Panel styling
        UIManager.put("Panel.background", getBackgroundColor());
        UIManager.put("Panel.foreground", getTextColor());

        // Menu styling
        UIManager.put("Menu.font", getModernFont());
        UIManager.put("MenuItem.font", getModernFont());
        UIManager.put("MenuBar.background", getSurfaceColor());
        UIManager.put("Menu.background", getSurfaceColor());
        UIManager.put("MenuItem.background", getSurfaceColor());

        // TextField styling
        UIManager.put("TextField.background", getSurfaceColor());
        UIManager.put("TextField.foreground", getTextColor());
        UIManager.put("TextField.selectionBackground", getColor("primary"));
        UIManager.put("TextField.selectionForeground", Color.WHITE);
    }

    public static void applyPanelStyling(JComponent panel) {
        panel.setBackground(getBackgroundColor());
        panel.setForeground(getTextColor());

        // Apply rounded border
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getColor("border"), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Apply modern font to all components
        applyFontRecursively(panel, getModernFont());
    }

    public static void applyButtonStyling(JButton button, ButtonStyle style) {
        button.setFont(getModernFont());
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        switch (style) {
            case PRIMARY:
                button.setBackground(getColor("primary"));
                button.setForeground(Color.WHITE);
                break;
            case SECONDARY:
                button.setBackground(getColor("secondary"));
                button.setForeground(Color.WHITE);
                break;
            case SUCCESS:
                button.setBackground(getColor("success"));
                button.setForeground(Color.WHITE);
                break;
            case WARNING:
                button.setBackground(getColor("warning"));
                button.setForeground(getTextColor());
                break;
            case DANGER:
                button.setBackground(getColor("danger"));
                button.setForeground(Color.WHITE);
                break;
            case OUTLINE:
                button.setBackground(getSurfaceColor());
                button.setForeground(getColor("primary"));
                button.setBorder(BorderFactory.createLineBorder(getColor("primary"), 1));
                button.setBorderPainted(true);
                break;
        }

        // Add hover effects
        addHoverEffects(button, style);
    }

    private static void addHoverEffects(JButton button, ButtonStyle style) {
        Color originalBg = button.getBackground();
        Color hoverBg = brightenColor(originalBg, 0.9f);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
    }

    public static void applyCardStyling(JPanel card) {
        card.setBackground(getSurfaceColor());
        card.setBorder(BorderFactory.createCompoundBorder(
                createShadowBorder(),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    }

    public static javax.swing.border.Border createShadowBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getColor("border"), 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        );
    }

    private static void applyFontRecursively(Container container, Font font) {
        for (Component component : container.getComponents()) {
            if (component instanceof Container) {
                applyFontRecursively((Container) component, font);
            }
            if (component instanceof JComponent) {
                component.setFont(font);
            }
        }
    }

    // Color getters
    public static Color getBackgroundColor() {
        return getColor("background");
    }

    public static Color getSurfaceColor() {
        return getColor("surface");
    }

    public static Color getPrimaryColor() {
        return getColor("primary");
    }

    public static Color getTextColor() {
        return getColor("text");
    }

    public static Color getSecondaryTextColor() {
        return getColor("textSecondary");
    }

    public static Color getColor(String colorName) {
        Map<String, Color> currentTheme = isDarkMode ? darkTheme : lightTheme;
        return currentTheme.getOrDefault(colorName, Color.BLACK);
    }

    // Font management
    public static Font getModernFont() {
        return new Font("Segoe UI", Font.PLAIN, 13);
    }

    public static Font getHeaderFont() {
        return new Font("Segoe UI", Font.BOLD, 16);
    }

    public static Font getTitleFont() {
        return new Font("Segoe UI", Font.BOLD, 20);
    }

    // Theme switching
    public static void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        applyModernTheme();
    }

    public static boolean isDarkMode() {
        return isDarkMode;
    }

    // Utility methods
    private static Color brightenColor(Color color, float factor) {
        int r = (int) Math.min(255, color.getRed() * factor);
        int g = (int) Math.min(255, color.getGreen() * factor);
        int b = (int) Math.min(255, color.getBlue() * factor);
        return new Color(r, g, b);
    }

    // Button styles enum
    public enum ButtonStyle {
        PRIMARY, SECONDARY, SUCCESS, WARNING, DANGER, OUTLINE
    }
}