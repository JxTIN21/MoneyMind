package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Modern toolbar with quick action buttons
 */
public class ModernToolBar extends JToolBar {
    private JFrame parentFrame;

    public ModernToolBar(JFrame parent) {
        this.parentFrame = parent;
        setupModernStyling();
        createToolBarButtons();
    }

    private void setupModernStyling() {
        setFloatable(false);
        setRollover(true);
        setBackground(ThemeManager.getSurfaceColor());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getColor("border")),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void createToolBarButtons() {
        add(createToolBarButton("New Transaction", "💳",
                e -> invokeMainFrameMethod("switchToTransactionsTab")));
        add(createToolBarButton("View Budgets", "💰",
                e -> invokeMainFrameMethod("switchToBudgetsTab")));
        add(createToolBarButton("Categories", "📁",
                e -> invokeMainFrameMethod("switchToCategoriesTab")));
        add(createToolBarButton("Reports", "📊",
                e -> invokeMainFrameMethod("switchToReportsTab")));

        addSeparator();

        add(createToolBarButton("Import", "📥",
                e -> invokeMainFrameMethod("importTransactions")));
        add(createToolBarButton("Export", "📤",
                e -> invokeMainFrameMethod("exportTransactions")));

        addSeparator();

        add(createToolBarButton("Refresh", "🔄",
                e -> invokeMainFrameMethod("refreshAllPanels")));
        add(createToolBarButton("Settings", "⚙️",
                e -> invokeMainFrameMethod("showSettings")));

        // Add spacer
        add(Box.createHorizontalGlue());

        // Add search box
        add(createSearchBox());
    }

    private JButton createToolBarButton(String tooltip, String icon, ActionListener action) {
        JButton button = new JButton();
        button.setToolTipText(tooltip);
        button.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        button.setText(icon);
        button.setPreferredSize(new Dimension(40, 36));
        button.setMaximumSize(new Dimension(40, 36));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setBackground(ThemeManager.getColor("hover"));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });

        if (action != null) button.addActionListener(action);
        return button;
    }

    private JPanel createSearchBox() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        searchPanel.setOpaque(false);

        JTextField searchField = new JTextField(15);
        searchField.setFont(ThemeManager.getModernFont());
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getColor("border"), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        searchField.setBackground(ThemeManager.getSurfaceColor());
        searchField.setForeground(ThemeManager.getTextColor());

        // Add placeholder text
        searchField.setText("Search transactions...");
        searchField.setForeground(ThemeManager.getSecondaryTextColor());

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search transactions...")) {
                    searchField.setText("");
                    searchField.setForeground(ThemeManager.getTextColor());
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search transactions...");
                    searchField.setForeground(ThemeManager.getSecondaryTextColor());
                }
            }
        });

        searchPanel.add(searchField);
        return searchPanel;
    }

    // Helper method to invoke MainFrame methods using reflection
    private void invokeMainFrameMethod(String methodName) {
        try {
            java.lang.reflect.Method method = parentFrame.getClass().getMethod(methodName);
            method.invoke(parentFrame);
        } catch (Exception ex) {
            System.err.println("Error invoking method: " + methodName + " - " + ex.getMessage());
        }
    }
}