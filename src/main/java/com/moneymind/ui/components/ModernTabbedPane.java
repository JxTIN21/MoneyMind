package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * Modern tabbed pane with enhanced styling
 */
public class ModernTabbedPane extends JTabbedPane {
    public ModernTabbedPane() {
        super();
        setupModernStyling();
    }

    private void setupModernStyling() {
        setFont(ThemeManager.getHeaderFont());
        setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        setTabPlacement(JTabbedPane.TOP);

        // Custom UI for better appearance
        setUI(new ModernTabbedPaneUI());

        setBackground(ThemeManager.getBackgroundColor());
        setForeground(ThemeManager.getTextColor());
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    public void addModernTab(String title, Icon icon, Component component, String tip) {
        addTab(title, icon, component, tip);
        int index = getTabCount() - 1;

        // Add close button for tabs if needed
        // setTabComponentAt(index, new ModernTabComponent(title, icon));
    }
}

/**
 * Modern tabbed pane UI for better appearance
 */
class ModernTabbedPaneUI extends BasicTabbedPaneUI {

    @Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) {
            g2d.setColor(ThemeManager.getSurfaceColor());
        } else {
            g2d.setColor(ThemeManager.getBackgroundColor());
        }

        g2d.fillRoundRect(x, y, w, h, 8, 8);

        if (isSelected) {
            g2d.setColor(ThemeManager.getPrimaryColor());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x, y + h - 2, x + w, y + h - 2);
        }
    }

    @Override
    protected void paintText(Graphics g, int tabPlacement, Font font,
                             FontMetrics metrics, int tabIndex, String title,
                             Rectangle textRect, boolean isSelected) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isSelected) {
            g2d.setColor(ThemeManager.getTextColor());
            g2d.setFont(ThemeManager.getHeaderFont());
        } else {
            g2d.setColor(ThemeManager.getSecondaryTextColor());
            g2d.setFont(ThemeManager.getModernFont());
        }

        super.paintText(g2d, tabPlacement, g2d.getFont(), metrics, tabIndex, title, textRect, isSelected);
    }
}