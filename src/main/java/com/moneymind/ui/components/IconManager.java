package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Icon management with modern SVG-style icons
 */
public class IconManager {

    public static Icon getTransactionIcon() {
        return createModernIcon("💳", ThemeManager.getPrimaryColor());
    }

    public static Icon getBudgetIcon() {
        return createModernIcon("💰", ThemeManager.getColor("success"));
    }

    public static Icon getCategoryIcon() {
        return createModernIcon("📁", ThemeManager.getColor("warning"));
    }

    public static Icon getReportIcon() {
        return createModernIcon("📊", ThemeManager.getColor("primary"));
    }

    public static Icon getSettingsIcon() {
        return createModernIcon("⚙️", ThemeManager.getColor("secondary"));
    }

    public static Icon getImportIcon() {
        return createModernIcon("📥", ThemeManager.getColor("primary"));
    }

    public static Icon getExportIcon() {
        return createModernIcon("📤", ThemeManager.getColor("primary"));
    }

    public static Icon getRefreshIcon() {
        return createModernIcon("🔄", ThemeManager.getColor("success"));
    }

    public static Icon getHelpIcon() {
        return createModernIcon("❓", ThemeManager.getColor("primary"));
    }

    public static Icon getInfoIcon() {
        return createModernIcon("ℹ️", ThemeManager.getColor("primary"));
    }

    private static Icon createModernIcon(String emoji, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background circle
                g2d.setColor(color);
                g2d.fillOval(x, y, 20, 20);

                // Draw emoji
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int emojiWidth = fm.stringWidth(emoji);
                int emojiHeight = fm.getHeight();
                g2d.drawString(emoji, x + (20 - emojiWidth) / 2, y + (20 + emojiHeight) / 2 - 2);
            }

            @Override
            public int getIconWidth() { return 20; }

            @Override
            public int getIconHeight() { return 20; }
        };
    }

    public static Icon createSimpleIcon(String text, Color backgroundColor) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw background
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(x, y, 16, 16, 4, 4);

                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getHeight();
                g2d.drawString(text, x + (16 - textWidth) / 2, y + (16 + textHeight) / 2 - 2);
            }

            @Override
            public int getIconWidth() { return 16; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    public static Image createModernAppIcon() {
        int size = 48;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create gradient background
        GradientPaint gradient = new GradientPaint(0, 0, ThemeManager.getPrimaryColor(),
                size, size, ThemeManager.getColor("accent"));
        g2d.setPaint(gradient);
        g2d.fillRoundRect(4, 4, size - 8, size - 8, 12, 12);

        // Add border
        g2d.setStroke(new BasicStroke(2));
        g2d.setColor(ThemeManager.getColor("border"));
        g2d.drawRoundRect(4, 4, size - 8, size - 8, 12, 12);

        // Draw money symbol
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String symbol = "$";
        int symbolWidth = fm.stringWidth(symbol);
        int symbolHeight = fm.getHeight();
        g2d.drawString(symbol, (size - symbolWidth) / 2, (size + symbolHeight) / 2 - 3);

        g2d.dispose();
        return image;
    }
}