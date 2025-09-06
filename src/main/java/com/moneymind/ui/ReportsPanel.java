package main.java.com.moneymind.ui;

import main.java.com.moneymind.model.Category;
import main.java.com.moneymind.service.ReportService;
import main.java.com.moneymind.service.CategoryService;
import main.java.com.moneymind.utils.CurrencyUtils;
import main.java.com.moneymind.utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying financial reports and analytics
 */
public class ReportsPanel extends JPanel {
    private ReportService reportService;
    private CategoryService categoryService;

    // UI Components
    private JTabbedPane reportTabs;

    // Summary Report Components
    private JComboBox<String> summaryPeriodComboBox;
    private JSpinner summaryYearSpinner;
    private JSpinner summaryMonthSpinner;
    private JButton generateSummaryButton;
    private JTextArea summaryTextArea;

    // Category Analysis Components
    private JSpinner categoryStartDateSpinner;
    private JSpinner categoryEndDateSpinner;
    private JButton generateCategoryButton;
    private JTable categoryAnalysisTable;
    private DefaultTableModel categoryTableModel;

    // Trend Analysis Components
    private JSpinner trendStartDateSpinner;
    private JSpinner trendEndDateSpinner;
    private JButton generateTrendButton;
    private JTable trendTable;
    private DefaultTableModel trendTableModel;

    // Budget Analysis Components
    private JButton generateBudgetButton;
    private JTable budgetAnalysisTable;
    private DefaultTableModel budgetTableModel;
    private JProgressBar budgetHealthBar;
    private JLabel budgetSummaryLabel;

    // Financial Health Components
    private JSpinner healthStartDateSpinner;
    private JSpinner healthEndDateSpinner;
    private JButton generateHealthButton;
    private JProgressBar healthScoreBar;
    private JLabel healthLevelLabel;
    private JTextArea healthRecommendationArea;
    private JList<String> healthFactorsList;

    public ReportsPanel(ReportService reportService, CategoryService categoryService) {
        this.reportService = reportService;
        this.categoryService = categoryService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        reportTabs = new JTabbedPane();

        // Summary Report Components
        summaryPeriodComboBox = new JComboBox<>(new String[]{"Monthly", "Yearly", "Custom"});
        summaryYearSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
        summaryMonthSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));
        generateSummaryButton = new JButton("Generate Summary");

        summaryTextArea = new JTextArea(20, 50);
        summaryTextArea.setEditable(false);
        summaryTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        // Category Analysis Components
        categoryStartDateSpinner = new JSpinner(new SpinnerDateModel());
        categoryEndDateSpinner = new JSpinner(new SpinnerDateModel());
        generateCategoryButton = new JButton("Generate Analysis");

        setupDateSpinners(categoryStartDateSpinner, categoryEndDateSpinner);

        String[] categoryColumns = {"Category", "Amount", "Transactions", "Percentage"};
        categoryTableModel = new DefaultTableModel(categoryColumns, 0);
        categoryAnalysisTable = new JTable(categoryTableModel);
        categoryAnalysisTable.setDefaultRenderer(Object.class, new CategoryAnalysisCellRenderer());

        // Trend Analysis Components
        trendStartDateSpinner = new JSpinner(new SpinnerDateModel());
        trendEndDateSpinner = new JSpinner(new SpinnerDateModel());
        generateTrendButton = new JButton("Generate Trend");

        setupDateSpinners(trendStartDateSpinner, trendEndDateSpinner);

        String[] trendColumns = {"Month", "Income", "Expense", "Net Amount"};
        trendTableModel = new DefaultTableModel(trendColumns, 0);
        trendTable = new JTable(trendTableModel);
        trendTable.setDefaultRenderer(Object.class, new TrendAnalysisCellRenderer());

        // Budget Analysis Components
        generateBudgetButton = new JButton("Generate Budget Analysis");

        String[] budgetColumns = {"Category", "Budget", "Spent", "Remaining", "Usage %", "Status"};
        budgetTableModel = new DefaultTableModel(budgetColumns, 0);
        budgetAnalysisTable = new JTable(budgetTableModel);
        budgetAnalysisTable.setDefaultRenderer(Object.class, new BudgetAnalysisCellRenderer());

        budgetHealthBar = new JProgressBar(0, 100);
        budgetHealthBar.setStringPainted(true);
        budgetSummaryLabel = new JLabel("Budget summary will appear here");

        // Financial Health Components
        healthStartDateSpinner = new JSpinner(new SpinnerDateModel());
        healthEndDateSpinner = new JSpinner(new SpinnerDateModel());
        generateHealthButton = new JButton("Calculate Health Score");

        setupDateSpinners(healthStartDateSpinner, healthEndDateSpinner);

        healthScoreBar = new JProgressBar(0, 100);
        healthScoreBar.setStringPainted(true);
        healthScoreBar.setString("0/100");

        healthLevelLabel = new JLabel("Financial Health: Not Calculated");
        healthLevelLabel.setFont(healthLevelLabel.getFont().deriveFont(Font.BOLD, 16f));

        healthRecommendationArea = new JTextArea(5, 40);
        healthRecommendationArea.setEditable(false);
        healthRecommendationArea.setLineWrap(true);
        healthRecommendationArea.setWrapStyleWord(true);

        healthFactorsList = new JList<>();
    }

    private void setupDateSpinners(JSpinner startSpinner, JSpinner endSpinner) {
        JSpinner.DateEditor startEditor = new JSpinner.DateEditor(startSpinner, "MMM dd, yyyy");
        JSpinner.DateEditor endEditor = new JSpinner.DateEditor(endSpinner, "MMM dd, yyyy");

        startSpinner.setEditor(startEditor);
        endSpinner.setEditor(endEditor);

        // Set default date range (last 3 months)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(3);

        startSpinner.setValue(java.sql.Date.valueOf(startDate));
        endSpinner.setValue(java.sql.Date.valueOf(endDate));
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Create tabs
        reportTabs.addTab("Summary", createIcon("📋"), createSummaryPanel(), "Financial Summary Reports");
        reportTabs.addTab("Categories", createIcon("📊"), createCategoryPanel(), "Category Analysis");
        reportTabs.addTab("Trends", createIcon("📈"), createTrendPanel(), "Trend Analysis");
        reportTabs.addTab("Budgets", createIcon("💰"), createBudgetPanel(), "Budget Performance");
        reportTabs.addTab("Health Score", createIcon("❤️"), createHealthPanel(), "Financial Health Assessment");

        add(reportTabs, BorderLayout.CENTER);
    }

    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Summary Options"));

        controlsPanel.add(new JLabel("Period:"));
        controlsPanel.add(summaryPeriodComboBox);
        controlsPanel.add(new JLabel("Year:"));
        controlsPanel.add(summaryYearSpinner);
        controlsPanel.add(new JLabel("Month:"));
        controlsPanel.add(summaryMonthSpinner);
        controlsPanel.add(generateSummaryButton);

        summaryPanel.add(controlsPanel, BorderLayout.NORTH);

        // Summary text area
        JScrollPane summaryScrollPane = new JScrollPane(summaryTextArea);
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Financial Summary"));
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);

        return summaryPanel;
    }

    private JPanel createCategoryPanel() {
        JPanel categoryPanel = new JPanel(new BorderLayout(10, 10));
        categoryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Analysis Period"));

        controlsPanel.add(new JLabel("From:"));
        controlsPanel.add(categoryStartDateSpinner);
        controlsPanel.add(new JLabel("To:"));
        controlsPanel.add(categoryEndDateSpinner);
        controlsPanel.add(generateCategoryButton);

        categoryPanel.add(controlsPanel, BorderLayout.NORTH);

        // Category analysis table
        JScrollPane tableScrollPane = new JScrollPane(categoryAnalysisTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Category Breakdown"));
        categoryPanel.add(tableScrollPane, BorderLayout.CENTER);

        return categoryPanel;
    }

    private JPanel createTrendPanel() {
        JPanel trendPanel = new JPanel(new BorderLayout(10, 10));
        trendPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Trend Period"));

        controlsPanel.add(new JLabel("From:"));
        controlsPanel.add(trendStartDateSpinner);
        controlsPanel.add(new JLabel("To:"));
        controlsPanel.add(trendEndDateSpinner);
        controlsPanel.add(generateTrendButton);

        trendPanel.add(controlsPanel, BorderLayout.NORTH);

        // Trend table
        JScrollPane tableScrollPane = new JScrollPane(trendTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Monthly Trends"));
        trendPanel.add(tableScrollPane, BorderLayout.CENTER);

        return trendPanel;
    }

    private JPanel createBudgetPanel() {
        JPanel budgetPanel = new JPanel(new BorderLayout(10, 10));
        budgetPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Budget Analysis"));

        controlsPanel.add(generateBudgetButton);

        budgetPanel.add(controlsPanel, BorderLayout.NORTH);

        // Budget table
        JScrollPane tableScrollPane = new JScrollPane(budgetAnalysisTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Budget Performance"));
        budgetPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Overall Budget Health"));
        summaryPanel.add(budgetSummaryLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(budgetHealthBar);

        budgetPanel.add(summaryPanel, BorderLayout.SOUTH);

        return budgetPanel;
    }

    private JPanel createHealthPanel() {
        JPanel healthPanel = new JPanel(new BorderLayout(10, 10));
        healthPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Controls panel
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Health Assessment Period"));

        controlsPanel.add(new JLabel("From:"));
        controlsPanel.add(healthStartDateSpinner);
        controlsPanel.add(new JLabel("To:"));
        controlsPanel.add(healthEndDateSpinner);
        controlsPanel.add(generateHealthButton);

        healthPanel.add(controlsPanel, BorderLayout.NORTH);

        // Main health panel
        JPanel mainHealthPanel = new JPanel(new BorderLayout(10, 10));

        // Score panel
        JPanel scorePanel = new JPanel(new BorderLayout());
        scorePanel.setBorder(BorderFactory.createTitledBorder("Financial Health Score"));

        JPanel scoreLabelPanel = new JPanel(new FlowLayout());
        scoreLabelPanel.add(healthLevelLabel);

        JPanel scoreBarPanel = new JPanel(new FlowLayout());
        scoreBarPanel.add(new JLabel("Score:"));
        scoreBarPanel.add(healthScoreBar);

        scorePanel.add(scoreLabelPanel, BorderLayout.NORTH);
        scorePanel.add(scoreBarPanel, BorderLayout.CENTER);

        mainHealthPanel.add(scorePanel, BorderLayout.NORTH);

        // Factors panel
        JScrollPane factorsScrollPane = new JScrollPane(healthFactorsList);
        factorsScrollPane.setBorder(BorderFactory.createTitledBorder("Contributing Factors"));
        factorsScrollPane.setPreferredSize(new Dimension(0, 150));

        mainHealthPanel.add(factorsScrollPane, BorderLayout.CENTER);

        // Recommendation panel
        JScrollPane recommendationScrollPane = new JScrollPane(healthRecommendationArea);
        recommendationScrollPane.setBorder(BorderFactory.createTitledBorder("Recommendations"));
        recommendationScrollPane.setPreferredSize(new Dimension(0, 120));

        mainHealthPanel.add(recommendationScrollPane, BorderLayout.SOUTH);

        healthPanel.add(mainHealthPanel, BorderLayout.CENTER);

        return healthPanel;
    }

    private void setupEventHandlers() {
        // Summary report handlers
        generateSummaryButton.addActionListener(e -> generateSummaryReport());
        summaryPeriodComboBox.addActionListener(e -> updateSummaryControls());

        // Category analysis handlers
        generateCategoryButton.addActionListener(e -> generateCategoryAnalysis());

        // Trend analysis handlers
        generateTrendButton.addActionListener(e -> generateTrendAnalysis());

        // Budget analysis handlers
        generateBudgetButton.addActionListener(e -> generateBudgetAnalysis());

        // Health score handlers
        generateHealthButton.addActionListener(e -> generateHealthScore());

        // Initialize controls
        updateSummaryControls();
    }

    private void updateSummaryControls() {
        String selectedPeriod = (String) summaryPeriodComboBox.getSelectedItem();

        summaryMonthSpinner.setEnabled(!"Yearly".equals(selectedPeriod) && !"Custom".equals(selectedPeriod));
    }

    private void generateSummaryReport() {
        try {
            String selectedPeriod = (String) summaryPeriodComboBox.getSelectedItem();
            ReportService.FinancialSummary summary;

            switch (selectedPeriod) {
                case "Monthly":
                    int year = (Integer) summaryYearSpinner.getValue();
                    int month = (Integer) summaryMonthSpinner.getValue();
                    summary = reportService.generateMonthlySummary(year, month);
                    break;
                case "Yearly":
                    year = (Integer) summaryYearSpinner.getValue();
                    summary = reportService.generateYearlySummary(year);
                    break;
                case "Custom":
                    // For custom, we'll use current month as example
                    summary = reportService.generateMonthlySummary(
                            LocalDate.now().getYear(),
                            LocalDate.now().getMonthValue()
                    );
                    break;
                default:
                    return;
            }

            displaySummaryReport(summary);

        } catch (Exception e) {
            showError("Error generating summary report: " + e.getMessage());
        }
    }

    private void displaySummaryReport(ReportService.FinancialSummary summary) {
        StringBuilder report = new StringBuilder();

        report.append("FINANCIAL SUMMARY REPORT\n");
        report.append("=".repeat(50)).append("\n\n");

        report.append("Period: ").append(summary.getPeriodType()).append("\n");
        report.append("From: ").append(DateUtils.formatForDisplay(summary.getStartDate())).append("\n");
        report.append("To: ").append(DateUtils.formatForDisplay(summary.getEndDate())).append("\n\n");

        report.append("INCOME & EXPENSES\n");
        report.append("-".repeat(25)).append("\n");
        report.append(String.format("Total Income:        %s\n", CurrencyUtils.format(summary.getTotalIncome())));
        report.append(String.format("Total Expenses:      %s\n", CurrencyUtils.format(summary.getTotalExpense())));
        report.append(String.format("Net Amount:          %s\n\n", CurrencyUtils.format(summary.getNetAmount())));

        report.append("TRANSACTION COUNT\n");
        report.append("-".repeat(25)).append("\n");
        report.append(String.format("Income Transactions: %d\n", summary.getIncomeTransactionCount()));
        report.append(String.format("Expense Transactions:%d\n", summary.getExpenseTransactionCount()));
        report.append(String.format("Total Transactions:  %d\n\n", summary.getTotalTransactionCount()));

        if (!summary.getCategoryTotals().isEmpty()) {
            report.append("CATEGORY BREAKDOWN\n");
            report.append("-".repeat(25)).append("\n");

            summary.getCategoryTotals().entrySet().stream()
                    .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                    .forEach(entry ->
                            report.append(String.format("%-20s %s\n",
                                    entry.getKey(),
                                    CurrencyUtils.format(entry.getValue()))));
        }

        // Financial insights
        report.append("\nFINANCIAL INSIGHTS\n");
        report.append("-".repeat(25)).append("\n");

        if (summary.getTotalIncome().compareTo(BigDecimal.ZERO) > 0) {
            double expenseRatio = summary.getTotalExpense()
                    .divide(summary.getTotalIncome(), 4, BigDecimal.ROUND_HALF_UP)
                    .doubleValue() * 100;
            report.append(String.format("Expense Ratio:       %.1f%%\n", expenseRatio));

            if (expenseRatio > 100) {
                report.append("⚠️  WARNING: Expenses exceed income!\n");
            } else if (expenseRatio > 90) {
                report.append("⚠️  CAUTION: High expense ratio\n");
            } else if (expenseRatio < 70) {
                report.append("✅ GOOD: Healthy savings rate\n");
            }
        }

        if (summary.getTotalTransactionCount() > 0) {
            BigDecimal avgTransactionAmount = summary.getTotalExpense()
                    .add(summary.getTotalIncome())
                    .divide(BigDecimal.valueOf(summary.getTotalTransactionCount()), 2, BigDecimal.ROUND_HALF_UP);
            report.append(String.format("Avg Transaction:     %s\n", CurrencyUtils.format(avgTransactionAmount)));
        }

        summaryTextArea.setText(report.toString());
        summaryTextArea.setCaretPosition(0);
    }

    private void generateCategoryAnalysis() {
        try {
            LocalDate startDate = getDateFromSpinner(categoryStartDateSpinner);
            LocalDate endDate = getDateFromSpinner(categoryEndDateSpinner);

            ReportService.CategoryAnalysis analysis = reportService.generateCategoryAnalysis(startDate, endDate);
            displayCategoryAnalysis(analysis);

        } catch (Exception e) {
            showError("Error generating category analysis: " + e.getMessage());
        }
    }

    private void displayCategoryAnalysis(ReportService.CategoryAnalysis analysis) {
        categoryTableModel.setRowCount(0);

        for (ReportService.CategoryData categoryData : analysis.getCategoryData()) {
            Object[] row = {
                    categoryData.getCategoryName(),
                    categoryData.getAmount(),
                    categoryData.getTransactionCount(),
                    categoryData.getPercentage()
            };
            categoryTableModel.addRow(row);
        }
    }

    private void generateTrendAnalysis() {
        try {
            LocalDate startDate = getDateFromSpinner(trendStartDateSpinner);
            LocalDate endDate = getDateFromSpinner(trendEndDateSpinner);

            ReportService.TrendAnalysis analysis = reportService.generateTrendAnalysis(startDate, endDate);
            displayTrendAnalysis(analysis);

        } catch (Exception e) {
            showError("Error generating trend analysis: " + e.getMessage());
        }
    }

    private void displayTrendAnalysis(ReportService.TrendAnalysis analysis) {
        trendTableModel.setRowCount(0);

        for (ReportService.MonthlyData monthlyData : analysis.getMonthlyData()) {
            Object[] row = {
                    monthlyData.getMonth().toString(),
                    monthlyData.getIncome(),
                    monthlyData.getExpense(),
                    monthlyData.getNetAmount()
            };
            trendTableModel.addRow(row);
        }
    }

    private void generateBudgetAnalysis() {
        try {
            ReportService.BudgetAnalysis analysis = reportService.generateBudgetAnalysis();
            displayBudgetAnalysis(analysis);

        } catch (Exception e) {
            showError("Error generating budget analysis: " + e.getMessage());
        }
    }

    private void displayBudgetAnalysis(ReportService.BudgetAnalysis analysis) {
        budgetTableModel.setRowCount(0);

        for (ReportService.BudgetPerformance performance : analysis.getBudgetPerformances()) {
            Object[] row = {
                    performance.getCategoryName(),
                    performance.getBudgetAmount(),
                    performance.getSpentAmount(),
                    performance.getRemainingAmount(),
                    performance.getUsagePercentage(),
                    performance.getStatus()
            };
            budgetTableModel.addRow(row);
        }

        // Update summary
        if (analysis.getTotalBudgeted().compareTo(BigDecimal.ZERO) > 0) {
            double overallUsage = CurrencyUtils.getPercentage(
                    analysis.getTotalSpent(),
                    analysis.getTotalBudgeted()
            );

            budgetHealthBar.setValue((int) Math.round(overallUsage));
            budgetHealthBar.setString(String.format("%.1f%%", overallUsage));

            // Set color based on usage
            if (overallUsage >= 100) {
                budgetHealthBar.setForeground(Color.RED);
            } else if (overallUsage >= 90) {
                budgetHealthBar.setForeground(Color.ORANGE);
            } else {
                budgetHealthBar.setForeground(Color.GREEN);
            }
        }

        budgetSummaryLabel.setText(String.format(
                "Budgeted: %s | Spent: %s | Over Budget: %d categories",
                CurrencyUtils.format(analysis.getTotalBudgeted()),
                CurrencyUtils.format(analysis.getTotalSpent()),
                analysis.getOverBudgetCount()
        ));
    }

    private void generateHealthScore() {
        try {
            LocalDate startDate = getDateFromSpinner(healthStartDateSpinner);
            LocalDate endDate = getDateFromSpinner(healthEndDateSpinner);

            ReportService.FinancialHealthScore healthScore = reportService.calculateFinancialHealth(startDate, endDate);
            displayHealthScore(healthScore);

        } catch (Exception e) {
            showError("Error calculating health score: " + e.getMessage());
        }
    }

    private void displayHealthScore(ReportService.FinancialHealthScore healthScore) {
        // Update score bar
        healthScoreBar.setValue(healthScore.getScore());
        healthScoreBar.setString(healthScore.getScore() + "/100");

        // Set color based on score
        if (healthScore.getScore() >= 80) {
            healthScoreBar.setForeground(Color.GREEN);
        } else if (healthScore.getScore() >= 60) {
            healthScoreBar.setForeground(Color.YELLOW);
        } else if (healthScore.getScore() >= 40) {
            healthScoreBar.setForeground(Color.ORANGE);
        } else {
            healthScoreBar.setForeground(Color.RED);
        }

        // Update health level
        healthLevelLabel.setText("Financial Health: " + healthScore.getHealthLevel());

        // Set color for health level
        switch (healthScore.getHealthLevel()) {
            case "Excellent":
                healthLevelLabel.setForeground(Color.GREEN);
                break;
            case "Good":
                healthLevelLabel.setForeground(new Color(150, 200, 0));
                break;
            case "Fair":
                healthLevelLabel.setForeground(Color.ORANGE);
                break;
            case "Poor":
                healthLevelLabel.setForeground(Color.RED);
                break;
            default:
                healthLevelLabel.setForeground(Color.BLACK);
        }

        // Update recommendation
        healthRecommendationArea.setText(healthScore.getRecommendation());

        // Update factors list
        DefaultListModel<String> factorsModel = new DefaultListModel<>();
        for (String factor : healthScore.getFactors()) {
            factorsModel.addElement(factor);
        }
        healthFactorsList.setModel(factorsModel);
    }

    private LocalDate getDateFromSpinner(JSpinner dateSpinner) {
        java.util.Date date = (java.util.Date) dateSpinner.getValue();
        return new java.sql.Date(date.getTime()).toLocalDate();
    }

    public void refreshData() {
        // Auto-generate some reports on panel load
        SwingUtilities.invokeLater(() -> {
            try {
                generateSummaryReport();
                generateBudgetAnalysis();
            } catch (Exception e) {
                // Silently handle errors on initial load
            }
        });
    }

    // Utility methods
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

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

    // Custom cell renderers
    private class CategoryAnalysisCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 1 && value instanceof BigDecimal) { // Amount column
                setText(CurrencyUtils.format((BigDecimal) value));
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else if (column == 3 && value instanceof Double) { // Percentage column
                setText(String.format("%.1f%%", (Double) value));
                setHorizontalAlignment(SwingConstants.RIGHT);
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return c;
        }
    }

    private class TrendAnalysisCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof BigDecimal && column > 0) {
                BigDecimal amount = (BigDecimal) value;
                setText(CurrencyUtils.format(amount));
                setHorizontalAlignment(SwingConstants.RIGHT);

                if (!isSelected && column == 3) { // Net Amount column
                    if (amount.compareTo(BigDecimal.ZERO) > 0) {
                        setForeground(Color.GREEN);
                    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
                        setForeground(Color.RED);
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return c;
        }
    }

    private class BudgetAnalysisCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof BigDecimal && column >= 1 && column <= 3) {
                setText(CurrencyUtils.format((BigDecimal) value));
                setHorizontalAlignment(SwingConstants.RIGHT);

                if (!isSelected && column == 3) { // Remaining column
                    BigDecimal remaining = (BigDecimal) value;
                    if (remaining.compareTo(BigDecimal.ZERO) < 0) {
                        setForeground(Color.RED);
                    } else {
                        setForeground(Color.GREEN);
                    }
                }
            } else if (value instanceof Double && column == 4) { // Usage % column
                Double percentage = (Double) value;
                setText(String.format("%.1f%%", percentage));
                setHorizontalAlignment(SwingConstants.RIGHT);

                if (!isSelected) {
                    if (percentage >= 100) {
                        setForeground(Color.RED);
                    } else if (percentage >= 90) {
                        setForeground(Color.ORANGE);
                    } else {
                        setForeground(Color.GREEN);
                    }
                }
            } else if (value instanceof String && column == 5) { // Status column
                String status = (String) value;
                setText(status);
                setHorizontalAlignment(SwingConstants.CENTER);

                if (!isSelected) {
                    switch (status) {
                        case "OVER BUDGET":
                            setForeground(Color.RED);
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "NEAR LIMIT":
                            setForeground(Color.ORANGE);
                            break;
                        default:
                            setForeground(Color.BLACK);
                            setFont(getFont().deriveFont(Font.PLAIN));
                    }
                }
            } else {
                setHorizontalAlignment(SwingConstants.LEFT);
            }

            return c;
        }
    }

    // Public methods for external access
    public void showSummaryReport() {
        reportTabs.setSelectedIndex(0);
        generateSummaryReport();
    }

    public void showCategoryAnalysis() {
        reportTabs.setSelectedIndex(1);
        generateCategoryAnalysis();
    }

    public void showTrendAnalysis() {
        reportTabs.setSelectedIndex(2);
        generateTrendAnalysis();
    }

    public void showBudgetAnalysis() {
        reportTabs.setSelectedIndex(3);
        generateBudgetAnalysis();
    }

    public void showHealthScore() {
        reportTabs.setSelectedIndex(4);
        generateHealthScore();
    }

    public void exportReport(String reportType) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files", "txt"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Export implementation would go here
                JOptionPane.showMessageDialog(this, "Report export functionality coming soon!",
                        "Export", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                showError("Error exporting report: " + e.getMessage());
            }
        }
    }
}
