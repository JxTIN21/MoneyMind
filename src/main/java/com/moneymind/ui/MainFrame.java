package main.java.com.moneymind.ui;

import main.java.com.moneymind.service.*;
import main.java.com.moneymind.ui.components.*;
import main.java.com.moneymind.ui.theme.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Modern main application window with enhanced UI and modular design
 */
public class MainFrame extends JFrame {
    // Services (unchanged)
    private TransactionService transactionService;
    private CategoryService categoryService;
    private BudgetService budgetService;
    private ReportService reportService;

    // UI Components - now modularized
    private ModernTabbedPane tabbedPane;
    private ModernMenuBar menuBar;
    private ModernStatusBar statusBar;
    private ModernToolBar toolBar;

    // Panels (unchanged functionality)
    private TransactionPanel transactionPanel;
    private BudgetPanel budgetPanel;
    private CategoryPanel categoryPanel;
    private ReportsPanel reportsPanel;

    public MainFrame() {
        // Apply modern look and feel
        ThemeManager.applyModernTheme();
        initializeServices();
        setupModernUI();
        setupEventHandlers();
    }

    private void initializeServices() {
        transactionService = new TransactionService();
        categoryService = new CategoryService();
        budgetService = new BudgetService();
        reportService = new ReportService();
    }

    private void setupModernUI() {
        configureMainWindow();
        createModernComponents();
        layoutComponents();
    }

    private void configureMainWindow() {
        setTitle("MoneyMind - Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1400, 900); // Slightly larger for modern look
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));

        // Modern window properties
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Set application icon
        try {
            setIconImage(IconManager.createModernAppIcon());
        } catch (Exception e) {
            // Icon creation failed, continue without icon
        }
    }

    private void createModernComponents() {
        // Create modern menu bar
        menuBar = new ModernMenuBar(this);
        setJMenuBar(menuBar);

        // Create modern toolbar
        toolBar = new ModernToolBar(this);

        // Create modern tabbed pane
        tabbedPane = new ModernTabbedPane();

        // Create panels with modern styling
        transactionPanel = new TransactionPanel(transactionService, categoryService);
        budgetPanel = new BudgetPanel(budgetService, categoryService);
        categoryPanel = new CategoryPanel(categoryService);
        reportsPanel = new ReportsPanel(reportService, categoryService);

        // Apply modern styling to panels
        ThemeManager.applyPanelStyling(transactionPanel);
        ThemeManager.applyPanelStyling(budgetPanel);
        ThemeManager.applyPanelStyling(categoryPanel);
        ThemeManager.applyPanelStyling(reportsPanel);

        // Add tabs with modern icons and styling
        tabbedPane.addModernTab("Transactions", IconManager.getTransactionIcon(),
                transactionPanel, "Manage your transactions");
        tabbedPane.addModernTab("Budgets", IconManager.getBudgetIcon(),
                budgetPanel, "Plan and track your budgets");
        tabbedPane.addModernTab("Categories", IconManager.getCategoryIcon(),
                categoryPanel, "Organize your categories");
        tabbedPane.addModernTab("Reports", IconManager.getReportIcon(),
                reportsPanel, "View financial reports and analysis");

        // Create modern status bar
        statusBar = new ModernStatusBar();
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(0, 0));

        // Create main panel with modern styling
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ThemeManager.getBackgroundColor());

        // Add toolbar
        mainPanel.add(toolBar, BorderLayout.NORTH);

        // Add tabbed pane with padding
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(ThemeManager.getBackgroundColor());
        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(statusBar, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // Add tab change listener with animation
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            AnimationManager.fadeInPanel(() -> refreshCurrentPanel(selectedIndex));
            statusBar.updateActiveTab(tabbedPane.getTitleAt(selectedIndex));
        });

        // Add modern window state listeners
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                // Update responsive layout
                updateResponsiveLayout();
            }
        });
    }

    private void updateResponsiveLayout() {
        // Adjust UI based on window size
        Dimension size = getSize();
        if (size.width < 1200) {
            tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        } else {
            tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
        }
    }

    private void refreshCurrentPanel(int tabIndex) {
        SwingUtilities.invokeLater(() -> {
            statusBar.setStatus("Refreshing data...");

            switch (tabIndex) {
                case 0: // Transactions
                    transactionPanel.refreshData();
                    statusBar.setStatus("Transactions updated");
                    break;
                case 1: // Budgets
                    budgetPanel.refreshData();
                    statusBar.setStatus("Budgets updated");
                    break;
                case 2: // Categories
                    categoryPanel.refreshData();
                    statusBar.setStatus("Categories updated");
                    break;
                case 3: // Reports
                    reportsPanel.refreshData();
                    statusBar.setStatus("Reports updated");
                    break;
            }

            // Clear status after delay
            Timer timer = new Timer(2000, e -> statusBar.setStatus("Ready"));
            timer.setRepeats(false);
            timer.start();
        });
    }

    // Menu action methods (unchanged functionality, enhanced UI)
    public void importTransactions() {
        ModernFileChooser fileChooser = new ModernFileChooser();
        fileChooser.setFileFilter("CSV files", "csv");

        if (fileChooser.showOpenDialog(this) == ModernFileChooser.APPROVE_OPTION) {
            statusBar.setStatus("Importing transactions...");
            // Implementation for CSV import
            ModernDialogs.showInfoDialog(this,
                    "Import functionality will be implemented in next version",
                    "Feature Coming Soon");
            statusBar.setStatus("Ready");
        }
    }

    public void exportTransactions() {
        ModernFileChooser fileChooser = new ModernFileChooser();
        fileChooser.setFileFilter("CSV files", "csv");

        if (fileChooser.showSaveDialog(this) == ModernFileChooser.APPROVE_OPTION) {
            statusBar.setStatus("Exporting transactions...");
            // Implementation for CSV export
            ModernDialogs.showInfoDialog(this,
                    "Export functionality will be implemented in next version",
                    "Feature Coming Soon");
            statusBar.setStatus("Ready");
        }
    }

    public void backupDatabase() {
        ModernFileChooser fileChooser = new ModernFileChooser();
        fileChooser.setFileFilter("Database files", "db");

        if (fileChooser.showSaveDialog(this) == ModernFileChooser.APPROVE_OPTION) {
            statusBar.setStatus("Creating backup...");
            ModernDialogs.showInfoDialog(this,
                    "Backup functionality will be implemented in next version",
                    "Feature Coming Soon");
            statusBar.setStatus("Ready");
        }
    }

    public void restoreDatabase() {
        ModernFileChooser fileChooser = new ModernFileChooser();
        fileChooser.setFileFilter("Database files", "db");

        if (fileChooser.showOpenDialog(this) == ModernFileChooser.APPROVE_OPTION) {
            statusBar.setStatus("Restoring database...");
            ModernDialogs.showInfoDialog(this,
                    "Restore functionality will be implemented in next version",
                    "Feature Coming Soon");
            statusBar.setStatus("Ready");
        }
    }

    public void showSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.setVisible(true);
    }

    public void showAbout() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.setVisible(true);
    }

    public void showHelp() {
        HelpDialog helpDialog = new HelpDialog(this);
        helpDialog.setVisible(true);
    }

    private void exitApplication() {
        int result = ModernDialogs.showConfirmDialog(
                this,
                "Are you sure you want to exit MoneyMind?",
                "Exit Application"
        );

        if (result == JOptionPane.YES_OPTION) {
            statusBar.setStatus("Closing application...");
            AnimationManager.fadeOutWindow(this, () -> {
                try {
                    // Close database connection
                    main.java.com.moneymind.database.DatabaseManager.getInstance().disconnect();
                } catch (Exception e) {
                    System.err.println("Error closing database: " + e.getMessage());
                }
                System.exit(0);
            });
        }
    }

    // Public methods for panel interaction (unchanged functionality)
    public void switchToTransactionsTab() {
        tabbedPane.setSelectedIndex(0);
    }

    public void switchToBudgetsTab() {
        tabbedPane.setSelectedIndex(1);
    }

    public void switchToCategoriesTab() {
        tabbedPane.setSelectedIndex(2);
    }

    public void switchToReportsTab() {
        tabbedPane.setSelectedIndex(3);
    }

    public void refreshAllPanels() {
        statusBar.setStatus("Refreshing all data...");
        transactionPanel.refreshData();
        budgetPanel.refreshData();
        categoryPanel.refreshData();
        reportsPanel.refreshData();
        statusBar.setStatus("All data refreshed");
    }

    // Additional modern features
    public void toggleFullScreen() {
        if (getExtendedState() == JFrame.MAXIMIZED_BOTH) {
            setExtendedState(JFrame.NORMAL);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }
    }

    public void toggleDarkMode() {
        ThemeManager.toggleDarkMode();
        SwingUtilities.updateComponentTreeUI(this);
        repaint();
    }

    public ModernStatusBar getStatusBar() {
        return statusBar;
    }

    public ModernTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}