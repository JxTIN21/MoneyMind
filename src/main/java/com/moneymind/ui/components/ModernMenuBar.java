package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Modern menu bar with enhanced styling
 */
public class ModernMenuBar extends JMenuBar {
    private JFrame parentFrame;

    public ModernMenuBar(JFrame parent) {
        this.parentFrame = parent;
        setupModernStyling();
        createMenus();
    }

    private void setupModernStyling() {
        setBackground(ThemeManager.getSurfaceColor());
        setForeground(ThemeManager.getTextColor());
        setBorderPainted(false);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, ThemeManager.getColor("border")),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void createMenus() {
        add(createFileMenu());
        add(createToolsMenu());
        add(createViewMenu());
        add(createHelpMenu());
    }

    private JMenu createFileMenu() {
        JMenu fileMenu = new JMenu("File");
        styleMenu(fileMenu);
        fileMenu.setMnemonic('F');

        fileMenu.add(createMenuItem("Import Transactions...", 'I', "ctrl I",
                e -> invokeMainFrameMethod("importTransactions")));
        fileMenu.add(createMenuItem("Export Transactions...", 'E', "ctrl E",
                e -> invokeMainFrameMethod("exportTransactions")));
        fileMenu.addSeparator();
        fileMenu.add(createMenuItem("Exit", 'X', "ctrl Q",
                e -> System.exit(0)));

        return fileMenu;
    }

    private JMenu createToolsMenu() {
        JMenu toolsMenu = new JMenu("Tools");
        styleMenu(toolsMenu);
        toolsMenu.setMnemonic('T');

        toolsMenu.add(createMenuItem("Backup Database",
                e -> invokeMainFrameMethod("backupDatabase")));
        toolsMenu.add(createMenuItem("Restore Database",
                e -> invokeMainFrameMethod("restoreDatabase")));
        toolsMenu.addSeparator();
        toolsMenu.add(createMenuItem("Settings", 'S',
                e -> invokeMainFrameMethod("showSettings")));

        return toolsMenu;
    }

    private JMenu createViewMenu() {
        JMenu viewMenu = new JMenu("View");
        styleMenu(viewMenu);
        viewMenu.setMnemonic('V');

        viewMenu.add(createMenuItem("Toggle Full Screen", "F11",
                e -> invokeMainFrameMethod("toggleFullScreen")));
        viewMenu.add(createMenuItem("Toggle Dark Mode", "ctrl D",
                e -> invokeMainFrameMethod("toggleDarkMode")));
        viewMenu.addSeparator();
        viewMenu.add(createMenuItem("Refresh All", "F5",
                e -> invokeMainFrameMethod("refreshAllPanels")));

        return viewMenu;
    }

    private JMenu createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        styleMenu(helpMenu);
        helpMenu.setMnemonic('H');

        helpMenu.add(createMenuItem("User Guide", 'U', "F1",
                e -> invokeMainFrameMethod("showHelp")));
        helpMenu.addSeparator();
        helpMenu.add(createMenuItem("About MoneyMind", 'A',
                e -> invokeMainFrameMethod("showAbout")));

        return helpMenu;
    }

    private JMenuItem createMenuItem(String text, ActionListener action) {
        return createMenuItem(text, 0, null, action);
    }

    private JMenuItem createMenuItem(String text, int mnemonic, ActionListener action) {
        return createMenuItem(text, mnemonic, null, action);
    }

    private JMenuItem createMenuItem(String text, String accelerator, ActionListener action) {
        return createMenuItem(text, 0, accelerator, action);
    }

    private JMenuItem createMenuItem(String text, int mnemonic, String accelerator, ActionListener action) {
        JMenuItem item = new JMenuItem(text);
        styleMenuItem(item);

        if (mnemonic != 0) item.setMnemonic(mnemonic);
        if (accelerator != null) item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        if (action != null) item.addActionListener(action);

        return item;
    }

    private void styleMenu(JMenu menu) {
        menu.setFont(ThemeManager.getModernFont());
        menu.setBackground(ThemeManager.getSurfaceColor());
        menu.setForeground(ThemeManager.getTextColor());
    }

    private void styleMenuItem(JMenuItem item) {
        item.setFont(ThemeManager.getModernFont());
        item.setBackground(ThemeManager.getSurfaceColor());
        item.setForeground(ThemeManager.getTextColor());
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