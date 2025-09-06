package main.java.com.moneymind.ui;

import main.java.com.moneymind.service.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main application window with tabbed interface
 */
public class MainFrame extends JFrame {
    private TransactionService transactionService;
    private CategoryService categoryService;
    private BudgetService budgetService;
    private ReportService reportService;

    private JTabbedPane tabbedPane;
    private TransactionPanel transactionPanel;
    private BudgetPanel budgetPanel;
    private CategoryPanel categoryPanel;
    private ReportsPanel reportsPanel;

    public MainFrame() {
        initializeServices();
        setupUI();
        setupEventHandlers();
    }

    private void initializeServices() {
        transactionService = new TransactionService();
        categoryService = new CategoryService();
        budgetService = new BudgetService();
        reportService = new ReportService();
    }

    private void setupUI() {
        setTitle("MoneyMind - Personal Finance Tracker");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);

        // Set application icon
        try {
            setIconImage(createAppIcon());
        } catch (Exception e) {
            // Icon creation failed, continue without icon
        }

        // Create menu bar
        setJMenuBar(createMenuBar());

        // Create main content
        createMainContent();

        // Create status bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private void createMainContent() {
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));

        // Create panels
        transactionPanel = new TransactionPanel(transactionService, categoryService);
        budgetPanel = new BudgetPanel(budgetService, categoryService);
        categoryPanel = new CategoryPanel(categoryService);
        reportsPanel = new ReportsPanel(reportService, categoryService);

        // Add tabs
        tabbedPane.addTab("Transactions", createIcon("💳"), transactionPanel, "Manage your transactions");
        tabbedPane.addTab("Budgets", createIcon("💰"), budgetPanel, "Plan and track your budgets");
        tabbedPane.addTab("Categories", createIcon("📁"), categoryPanel, "Organize your categories");
        tabbedPane.addTab("Reports", createIcon("📊"), reportsPanel, "View financial reports and analysis");

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        JMenuItem importItem = new JMenuItem("Import Transactions...");
        importItem.setMnemonic('I');
        importItem.setAccelerator(KeyStroke.getKeyStroke("ctrl I"));
        importItem.addActionListener(e -> importTransactions());

        JMenuItem exportItem = new JMenuItem("Export Transactions...");
        exportItem.setMnemonic('E');
        exportItem.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        exportItem.addActionListener(e -> exportTransactions());

        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setMnemonic('X');
        exitItem.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        exitItem.addActionListener(e -> exitApplication());

        fileMenu.add(exitItem);

        // Tools menu
        JMenu toolsMenu = new JMenu("Tools");
        toolsMenu.setMnemonic('T');

        JMenuItem backupItem = new JMenuItem("Backup Database");
        backupItem.addActionListener(e -> backupDatabase());

        JMenuItem restoreItem = new JMenuItem("Restore Database");
        restoreItem.addActionListener(e -> restoreDatabase());

        JMenuItem settingsItem = new JMenuItem("Settings");
        settingsItem.setMnemonic('S');
        settingsItem.addActionListener(e -> showSettings());

        toolsMenu.add(backupItem);
        toolsMenu.add(restoreItem);
        toolsMenu.addSeparator();
        toolsMenu.add(settingsItem);

        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = new JMenuItem("About MoneyMind");
        aboutItem.setMnemonic('A');
        aboutItem.addActionListener(e -> showAbout());

        JMenuItem helpItem = new JMenuItem("User Guide");
        helpItem.setMnemonic('U');
        helpItem.setAccelerator(KeyStroke.getKeyStroke("F1"));
        helpItem.addActionListener(e -> showHelp());

        helpMenu.add(helpItem);
        helpMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(toolsMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        statusBar.setPreferredSize(new Dimension(0, 25));

        JLabel statusLabel = new JLabel("Ready");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        JLabel connectionLabel = new JLabel("Database Connected");
        connectionLabel.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));
        connectionLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(connectionLabel, BorderLayout.EAST);

        return statusBar;
    }

    private void setupEventHandlers() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitApplication();
            }
        });

        // Add tab change listener to refresh data
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            refreshCurrentPanel(selectedIndex);
        });
    }

    private void refreshCurrentPanel(int tabIndex) {
        SwingUtilities.invokeLater(() -> {
            switch (tabIndex) {
                case 0: // Transactions
                    transactionPanel.refreshData();
                    break;
                case 1: // Budgets
                    budgetPanel.refreshData();
                    break;
                case 2: // Categories
                    categoryPanel.refreshData();
                    break;
                case 3: // Reports
                    reportsPanel.refreshData();
                    break;
            }
        });
    }

    // Menu action methods
    private void importTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "CSV files", "csv"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implementation for CSV import
            JOptionPane.showMessageDialog(this,
                    "Import functionality will be implemented in next version",
                    "Feature Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "CSV files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implementation for CSV export
            JOptionPane.showMessageDialog(this,
                    "Export functionality will be implemented in next version",
                    "Feature Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void backupDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Database files", "db"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Backup functionality will be implemented in next version",
                    "Feature Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void restoreDatabase() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Database files", "db"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            JOptionPane.showMessageDialog(this,
                    "Restore functionality will be implemented in next version",
                    "Feature Coming Soon",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showSettings() {
        JOptionPane.showMessageDialog(this,
                "Settings panel will be implemented in next version",
                "Feature Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        String aboutText = """
            MoneyMind - Personal Finance Tracker
            Version 1.0
            
            A comprehensive personal finance management application
            built with Java and SQLite.
            
            Features:
            • Transaction Management
            • Category Organization
            • Budget Planning & Tracking
            • Financial Reports & Analysis
            • Data Import/Export
            
            © 2024 MoneyMind Project
            """;

        JOptionPane.showMessageDialog(this, aboutText, "About MoneyMind",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showHelp() {
        String helpText = """
            MoneyMind User Guide
            
            Getting Started:
            1. Add categories to organize your transactions
            2. Record your income and expenses
            3. Set up budgets to track spending
            4. View reports to analyze your finances
            
            Tips:
            • Use Ctrl+N to add new transactions quickly
            • Set up monthly budgets for better tracking
            • Check the Reports tab for insights
            • Use categories to organize expenses
            
            For more help, visit the documentation.
            """;

        JTextArea textArea = new JTextArea(helpText);
        textArea.setEditable(false);
        textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "User Guide",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void exitApplication() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to exit MoneyMind?",
                "Exit Application",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            try {
                // Close database connection
                main.java.com.moneymind.database.DatabaseManager.getInstance().disconnect();
            } catch (Exception e) {
                System.err.println("Error closing database: " + e.getMessage());
            }

            System.exit(0);
        }
    }

    // Utility methods
    private Icon createIcon(String emoji) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
                g2d.drawString(emoji, x, y + 12);
            }

            @Override
            public int getIconWidth() { return 20; }

            @Override
            public int getIconHeight() { return 16; }
        };
    }

    private Image createAppIcon() {
        // Create a simple icon programmatically
        int size = 32;
        java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = image.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw a simple money symbol
        g2d.setColor(new Color(34, 139, 34)); // Forest green
        g2d.fillOval(4, 4, size - 8, size - 8);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        g2d.drawString("$", size/2 - 5, size/2 + 6);

        g2d.dispose();
        return image;
    }

    // Public methods for panel interaction
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
        transactionPanel.refreshData();
        budgetPanel.refreshData();
        categoryPanel.refreshData();
        reportsPanel.refreshData();
    }
}
