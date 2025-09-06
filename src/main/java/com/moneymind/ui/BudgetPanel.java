package main.java.com.moneymind.ui;

import main.java.com.moneymind.model.Budget;
import main.java.com.moneymind.model.Category;
import main.java.com.moneymind.service.BudgetService;
import main.java.com.moneymind.service.CategoryService;
import main.java.com.moneymind.utils.CurrencyUtils;
import main.java.com.moneymind.utils.DateUtils;
import main.java.com.moneymind.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Panel for managing budgets with tracking and analysis
 */
public class BudgetPanel extends JPanel {
    private BudgetService budgetService;
    private CategoryService categoryService;

    // UI Components
    private JTable budgetTable;
    private DefaultTableModel tableModel;

    // Form components
    private JComboBox<Category> categoryComboBox;
    private JTextField amountField;
    private JComboBox<String> periodComboBox;
    private JSpinner startDateSpinner;
    private JSpinner endDateSpinner;

    // Quick setup components
    private JComboBox<String> quickSetupComboBox;
    private JSpinner yearSpinner;
    private JSpinner monthSpinner;

    // Buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton quickSetupButton;

    // Analysis components
    private JProgressBar overallProgressBar;
    private JLabel totalBudgetLabel;
    private JLabel totalSpentLabel;
    private JLabel totalRemainingLabel;
    private JPanel alertPanel;

    // Current selection
    private Budget selectedBudget;

    public BudgetPanel(BudgetService budgetService, CategoryService categoryService) {
        this.budgetService = budgetService;
        this.categoryService = categoryService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Create table
        String[] columnNames = {"ID", "Category", "Period", "Budget Amount", "Spent", "Remaining", "Usage %", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        budgetTable = new JTable(tableModel);
        budgetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        budgetTable.setRowHeight(30);

        // Hide ID column
        budgetTable.getColumnModel().getColumn(0).setMinWidth(0);
        budgetTable.getColumnModel().getColumn(0).setMaxWidth(0);
        budgetTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        budgetTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Category
        budgetTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Period
        budgetTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Budget Amount
        budgetTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Spent
        budgetTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Remaining
        budgetTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Usage %
        budgetTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Status

        // Custom renderers
        budgetTable.getColumnModel().getColumn(3).setCellRenderer(new AmountCellRenderer());
        budgetTable.getColumnModel().getColumn(4).setCellRenderer(new AmountCellRenderer());
        budgetTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());
        budgetTable.getColumnModel().getColumn(6).setCellRenderer(new PercentageCellRenderer());
        budgetTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());

        // Form components
        categoryComboBox = new JComboBox<>();
        loadCategories();

        amountField = new JTextField(15);

        periodComboBox = new JComboBox<>(new String[]{"MONTHLY", "YEARLY"});

        startDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "MMM dd, yyyy");
        startDateSpinner.setEditor(startDateEditor);

        endDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "MMM dd, yyyy");
        endDateSpinner.setEditor(endDateEditor);

        // Quick setup components
        quickSetupComboBox = new JComboBox<>(new String[]{
                "Current Month", "Next Month", "Current Year", "Next Year", "Current Quarter"
        });

        yearSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2020, 2030, 1));
        monthSpinner = new JSpinner(new SpinnerNumberModel(LocalDate.now().getMonthValue(), 1, 12, 1));

        // Buttons
        addButton = new JButton("Add Budget");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        clearButton = new JButton("Clear Form");
        quickSetupButton = new JButton("Quick Setup");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Analysis components
        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setStringPainted(true);
        overallProgressBar.setString("0%");

        totalBudgetLabel = new JLabel("Total Budget: $0.00");
        totalSpentLabel = new JLabel("Total Spent: $0.00");
        totalRemainingLabel = new JLabel("Total Remaining: $0.00");

        alertPanel = new JPanel();
        alertPanel.setLayout(new BoxLayout(alertPanel, BoxLayout.Y_AXIS));
        alertPanel.setBorder(BorderFactory.createTitledBorder("Budget Alerts"));

        // Set date spinner defaults
        updateDateSpinners();
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with form and quick setup
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(budgetTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Budget Overview"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with analysis
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = createFormPanel();
        topPanel.add(formPanel, BorderLayout.CENTER);

        // Quick setup panel
        JPanel quickSetupPanel = createQuickSetupPanel();
        topPanel.add(quickSetupPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Budget Details"));
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Category and Amount
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        formPanel.add(categoryComboBox, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 3;
        formPanel.add(amountField, gbc);

        // Row 2: Period
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Period:"), gbc);
        gbc.gridx = 1;
        formPanel.add(periodComboBox, gbc);

        // Row 3: Date Range
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Start Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(startDateSpinner, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("End Date:"), gbc);
        gbc.gridx = 3;
        formPanel.add(endDateSpinner, gbc);

        // Row 4: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 4;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createQuickSetupPanel() {
        JPanel quickPanel = new JPanel();
        quickPanel.setBorder(BorderFactory.createTitledBorder("Quick Setup"));
        quickPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        quickPanel.add(new JLabel("Period:"), gbc);
        gbc.gridx = 1;
        quickPanel.add(quickSetupComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        quickPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        quickPanel.add(yearSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        quickPanel.add(new JLabel("Month:"), gbc);
        gbc.gridx = 1;
        quickPanel.add(monthSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        quickPanel.add(quickSetupButton, gbc);

        return quickPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Budget Summary"));

        summaryPanel.add(totalBudgetLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(totalSpentLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(totalRemainingLabel);
        summaryPanel.add(Box.createHorizontalStrut(20));
        summaryPanel.add(new JLabel("Overall Progress:"));
        summaryPanel.add(overallProgressBar);

        bottomPanel.add(summaryPanel, BorderLayout.CENTER);

        // Alerts panel
        JScrollPane alertScrollPane = new JScrollPane(alertPanel);
        alertScrollPane.setPreferredSize(new Dimension(0, 100));
        alertScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        bottomPanel.add(alertScrollPane, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private void setupEventHandlers() {
        // Table selection listener
        budgetTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addBudget());
        editButton.addActionListener(e -> editBudget());
        deleteButton.addActionListener(e -> deleteBudget());
        clearButton.addActionListener(e -> clearForm());
        quickSetupButton.addActionListener(e -> performQuickSetup());

        // Period combo box listener
        periodComboBox.addActionListener(e -> updateDateSpinners());

        // Quick setup combo box listener
        quickSetupComboBox.addActionListener(e -> updateQuickSetupFields());
    }

    private void handleTableSelection() {
        int selectedRow = budgetTable.getSelectedRow();

        if (selectedRow >= 0) {
            Long budgetId = (Long) tableModel.getValueAt(selectedRow, 0);

            try {
                selectedBudget = budgetService.getBudgetById(budgetId);
                if (selectedBudget != null) {
                    populateForm(selectedBudget);
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            } catch (Exception e) {
                showError("Error loading budget: " + e.getMessage());
            }
        } else {
            selectedBudget = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private void populateForm(Budget budget) {
        // Set category
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            Category category = categoryComboBox.getItemAt(i);
            if (category.getId().equals(budget.getCategoryId())) {
                categoryComboBox.setSelectedIndex(i);
                break;
            }
        }

        amountField.setText(CurrencyUtils.formatWithoutSymbol(budget.getAmount()));
        periodComboBox.setSelectedItem(budget.getPeriod().name());

        // Set dates
        startDateSpinner.setValue(java.sql.Date.valueOf(budget.getStartDate()));
        endDateSpinner.setValue(java.sql.Date.valueOf(budget.getEndDate()));
    }

    private void addBudget() {
        if (validateForm()) {
            try {
                Budget budget = createBudgetFromForm();

                // Check for overlapping budgets
                ValidationUtils.ValidationResult overlapCheck = ValidationUtils.validateBudgetOverlap(
                        budget.getStartDate(), budget.getEndDate(), budget.getCategoryId(), null
                );

                if (!overlapCheck.isValid()) {
                    showError("Budget Overlap:\n" + overlapCheck.getAllErrors());
                    return;
                }

                Long id = budgetService.addBudget(budget);

                if (id != null) {
                    showMessage("Budget added successfully!");
                    clearForm();
                    refreshData();
                } else {
                    showError("Failed to add budget.");
                }
            } catch (Exception e) {
                showError("Error adding budget: " + e.getMessage());
            }
        }
    }

    private void editBudget() {
        if (selectedBudget != null && validateForm()) {
            try {
                Budget budget = createBudgetFromForm();
                budget.setId(selectedBudget.getId());

                boolean success = budgetService.updateBudget(budget);

                if (success) {
                    showMessage("Budget updated successfully!");
                    clearForm();
                    refreshData();
                } else {
                    showError("Failed to update budget.");
                }
            } catch (Exception e) {
                showError("Error updating budget: " + e.getMessage());
            }
        }
    }

    private void deleteBudget() {
        if (selectedBudget != null) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this budget?\n\n" +
                            selectedBudget.getCategoryName() + " - " +
                            CurrencyUtils.format(selectedBudget.getAmount()),
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                try {
                    boolean success = budgetService.deleteBudget(selectedBudget.getId());

                    if (success) {
                        showMessage("Budget deleted successfully!");
                        clearForm();
                        refreshData();
                    } else {
                        showError("Failed to delete budget.");
                    }
                } catch (Exception e) {
                    showError("Error deleting budget: " + e.getMessage());
                }
            }
        }
    }

    private void clearForm() {
        categoryComboBox.setSelectedIndex(0);
        amountField.setText("");
        periodComboBox.setSelectedIndex(0);
        updateDateSpinners();
        selectedBudget = null;
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        budgetTable.clearSelection();
    }

    private Budget createBudgetFromForm() {
        Category category = (Category) categoryComboBox.getSelectedItem();
        BigDecimal amount = CurrencyUtils.parse(amountField.getText());
        Budget.BudgetPeriod period = Budget.BudgetPeriod.valueOf((String) periodComboBox.getSelectedItem());

        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

        LocalDate start = new java.sql.Date(startDate.getTime()).toLocalDate();
        LocalDate end = new java.sql.Date(endDate.getTime()).toLocalDate();

        return new Budget(category.getId(), amount, period, start, end);
    }

    private boolean validateForm() {
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();
        String amountStr = amountField.getText().trim();

        java.util.Date startDate = (java.util.Date) startDateSpinner.getValue();
        java.util.Date endDate = (java.util.Date) endDateSpinner.getValue();

        LocalDate start = new java.sql.Date(startDate.getTime()).toLocalDate();
        LocalDate end = new java.sql.Date(endDate.getTime()).toLocalDate();

        ValidationUtils.ValidationResult result = ValidationUtils.validateBudget(
                CurrencyUtils.parseSafely(amountStr), start, end,
                selectedCategory != null ? selectedCategory.getId() : null
        );

        if (!result.isValid()) {
            showError("Validation Error:\n" + result.getAllErrors());
            return false;
        }

        return true;
    }

    private void performQuickSetup() {
        String selectedPeriod = (String) quickSetupComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        int month = (Integer) monthSpinner.getValue();

        LocalDate startDate, endDate;
        Budget.BudgetPeriod budgetPeriod;

        switch (selectedPeriod) {
            case "Current Month":
                startDate = DateUtils.getStartOfMonth(LocalDate.now());
                endDate = DateUtils.getEndOfMonth(LocalDate.now());
                budgetPeriod = Budget.BudgetPeriod.MONTHLY;
                break;
            case "Next Month":
                LocalDate nextMonth = LocalDate.now().plusMonths(1);
                startDate = DateUtils.getStartOfMonth(nextMonth);
                endDate = DateUtils.getEndOfMonth(nextMonth);
                budgetPeriod = Budget.BudgetPeriod.MONTHLY;
                break;
            case "Current Year":
                startDate = LocalDate.of(LocalDate.now().getYear(), 1, 1);
                endDate = LocalDate.of(LocalDate.now().getYear(), 12, 31);
                budgetPeriod = Budget.BudgetPeriod.YEARLY;
                break;
            case "Next Year":
                int nextYear = LocalDate.now().getYear() + 1;
                startDate = LocalDate.of(nextYear, 1, 1);
                endDate = LocalDate.of(nextYear, 12, 31);
                budgetPeriod = Budget.BudgetPeriod.YEARLY;
                break;
            case "Current Quarter":
                DateUtils.DateRange currentQuarter = DateUtils.getCurrentQuarter();
                startDate = currentQuarter.getStartDate();
                endDate = currentQuarter.getEndDate();
                budgetPeriod = Budget.BudgetPeriod.MONTHLY;
                break;
            default:
                YearMonth selectedMonth = YearMonth.of(year, month);
                startDate = selectedMonth.atDay(1);
                endDate = selectedMonth.atEndOfMonth();
                budgetPeriod = Budget.BudgetPeriod.MONTHLY;
        }

        // Update form fields
        periodComboBox.setSelectedItem(budgetPeriod.name());
        startDateSpinner.setValue(java.sql.Date.valueOf(startDate));
        endDateSpinner.setValue(java.sql.Date.valueOf(endDate));
    }

    private void updateDateSpinners() {
        String selectedPeriod = (String) periodComboBox.getSelectedItem();
        LocalDate today = LocalDate.now();

        if ("MONTHLY".equals(selectedPeriod)) {
            LocalDate startOfMonth = DateUtils.getStartOfMonth(today);
            LocalDate endOfMonth = DateUtils.getEndOfMonth(today);

            startDateSpinner.setValue(java.sql.Date.valueOf(startOfMonth));
            endDateSpinner.setValue(java.sql.Date.valueOf(endOfMonth));
        } else if ("YEARLY".equals(selectedPeriod)) {
            LocalDate startOfYear = DateUtils.getStartOfYear(today);
            LocalDate endOfYear = DateUtils.getEndOfYear(today);

            startDateSpinner.setValue(java.sql.Date.valueOf(startOfYear));
            endDateSpinner.setValue(java.sql.Date.valueOf(endOfYear));
        }
    }

    private void updateQuickSetupFields() {
        String selectedPeriod = (String) quickSetupComboBox.getSelectedItem();
        LocalDate today = LocalDate.now();

        if (selectedPeriod.contains("Month")) {
            monthSpinner.setEnabled(true);
            yearSpinner.setValue(today.getYear());
            monthSpinner.setValue(today.getMonthValue());
        } else {
            monthSpinner.setEnabled(false);
            yearSpinner.setValue(today.getYear());
        }
    }

    private void loadCategories() {
        categoryComboBox.removeAllItems();

        try {
            // Only load expense categories for budgets
            List<Category> categories = categoryService.getCategoriesByType(Category.CategoryType.EXPENSE);
            for (Category category : categories) {
                categoryComboBox.addItem(category);
            }
        } catch (Exception e) {
            showError("Error loading categories: " + e.getMessage());
        }
    }

    private void updateTable(List<Budget> budgets) {
        tableModel.setRowCount(0);

        for (Budget budget : budgets) {
            Object[] row = {
                    budget.getId(),
                    budget.getCategoryName(),
                    budget.getPeriod() + " (" + DateUtils.formatShort(budget.getStartDate()) +
                            " - " + DateUtils.formatShort(budget.getEndDate()) + ")",
                    budget.getAmount(),
                    budget.getSpent(),
                    budget.getRemaining(),
                    budget.getUsagePercentage(),
                    budget.getStatus()
            };
            tableModel.addRow(row);
        }
    }

    private void updateAnalysis(List<Budget> budgets) {
        BigDecimal totalBudget = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (Budget budget : budgets) {
            totalBudget = totalBudget.add(budget.getAmount());
            totalSpent = totalSpent.add(budget.getSpent());
        }

        BigDecimal totalRemaining = totalBudget.subtract(totalSpent);
        double overallUsage = totalBudget.compareTo(BigDecimal.ZERO) > 0 ?
                CurrencyUtils.getPercentage(totalSpent, totalBudget) : 0.0;

        totalBudgetLabel.setText("Total Budget: " + CurrencyUtils.format(totalBudget));
        totalSpentLabel.setText("Total Spent: " + CurrencyUtils.format(totalSpent));
        totalRemainingLabel.setText("Total Remaining: " + CurrencyUtils.format(totalRemaining));

        overallProgressBar.setValue((int) Math.round(overallUsage));
        overallProgressBar.setString(String.format("%.1f%%", overallUsage));

        // Set progress bar color based on usage
        if (overallUsage >= 100) {
            overallProgressBar.setForeground(Color.RED);
        } else if (overallUsage >= 90) {
            overallProgressBar.setForeground(Color.ORANGE);
        } else if (overallUsage >= 75) {
            overallProgressBar.setForeground(Color.YELLOW);
        } else {
            overallProgressBar.setForeground(Color.GREEN);
        }

        updateAlerts(budgets);
    }

    private void updateAlerts(List<Budget> budgets) {
        alertPanel.removeAll();

        try {
            List<String> alerts = budgetService.getBudgetAlerts();

            if (alerts.isEmpty()) {
                JLabel noAlertsLabel = new JLabel("No budget alerts");
                noAlertsLabel.setForeground(Color.GREEN);
                alertPanel.add(noAlertsLabel);
            } else {
                for (String alert : alerts) {
                    JLabel alertLabel = new JLabel(alert);
                    if (alert.startsWith("OVER BUDGET")) {
                        alertLabel.setForeground(Color.RED);
                        alertLabel.setFont(alertLabel.getFont().deriveFont(Font.BOLD));
                    } else {
                        alertLabel.setForeground(Color.ORANGE);
                    }
                    alertPanel.add(alertLabel);
                }
            }
        } catch (Exception e) {
            JLabel errorLabel = new JLabel("Error loading alerts: " + e.getMessage());
            errorLabel.setForeground(Color.RED);
            alertPanel.add(errorLabel);
        }

        alertPanel.revalidate();
        alertPanel.repaint();
    }

    public void refreshData() {
        try {
            List<Budget> budgets = budgetService.getActiveBudgets();
            updateTable(budgets);
            updateAnalysis(budgets);
            loadCategories();
        } catch (Exception e) {
            showError("Error refreshing data: " + e.getMessage());
        }
    }

    // Utility methods
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Custom cell renderers
    private class AmountCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof BigDecimal) {
                BigDecimal amount = (BigDecimal) value;
                setText(CurrencyUtils.format(amount));

                if (!isSelected) {
                    // Color coding for remaining amount (column 5)
                    if (column == 5) {
                        if (amount.compareTo(BigDecimal.ZERO) < 0) {
                            setForeground(Color.RED);
                        } else if (amount.compareTo(BigDecimal.ZERO) == 0) {
                            setForeground(Color.ORANGE);
                        } else {
                            setForeground(Color.GREEN);
                        }
                    } else {
                        setForeground(Color.BLACK);
                    }
                }
            }

            setHorizontalAlignment(SwingConstants.RIGHT);
            return c;
        }
    }

    private class PercentageCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof Double) {
                Double percentage = (Double) value;
                setText(String.format("%.1f%%", percentage));

                if (!isSelected) {
                    if (percentage >= 100) {
                        setForeground(Color.RED);
                    } else if (percentage >= 90) {
                        setForeground(Color.ORANGE);
                    } else if (percentage >= 75) {
                        setForeground(new Color(255, 165, 0)); // Orange
                    } else {
                        setForeground(Color.GREEN);
                    }
                }
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof String) {
                String status = (String) value;
                setText(status);

                if (!isSelected) {
                    switch (status) {
                        case "OVER BUDGET":
                            setForeground(Color.RED);
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "NEAR LIMIT":
                            setForeground(Color.ORANGE);
                            setFont(getFont().deriveFont(Font.BOLD));
                            break;
                        case "ON TRACK":
                            setForeground(new Color(255, 165, 0));
                            break;
                        case "UNDER BUDGET":
                            setForeground(Color.GREEN);
                            break;
                        default:
                            setForeground(Color.BLACK);
                    }
                } else {
                    setFont(getFont().deriveFont(Font.PLAIN));
                }
            }

            setHorizontalAlignment(SwingConstants.CENTER);
            return c;
        }
    }

    // Public methods for external access
    public Budget getSelectedBudget() {
        return selectedBudget;
    }

    public void showBudgetDetails(Budget budget) {
        if (budget != null) {
            String details = String.format(
                    "Budget Details\n\n" +
                            "Category: %s\n" +
                            "Period: %s\n" +
                            "Budget Amount: %s\n" +
                            "Amount Spent: %s\n" +
                            "Remaining: %s\n" +
                            "Usage: %.1f%%\n" +
                            "Status: %s\n" +
                            "Date Range: %s - %s",
                    budget.getCategoryName(),
                    budget.getPeriod(),
                    CurrencyUtils.format(budget.getAmount()),
                    CurrencyUtils.format(budget.getSpent()),
                    CurrencyUtils.format(budget.getRemaining()),
                    budget.getUsagePercentage(),
                    budget.getStatus(),
                    DateUtils.formatForDisplay(budget.getStartDate()),
                    DateUtils.formatForDisplay(budget.getEndDate())
            );

            JOptionPane.showMessageDialog(this, details, "Budget Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void copyBudgetToNextPeriod(Budget budget) {
        if (budget != null) {
            try {
                LocalDate newStart, newEnd;

                if (budget.getPeriod() == Budget.BudgetPeriod.MONTHLY) {
                    newStart = budget.getStartDate().plusMonths(1);
                    newEnd = budget.getEndDate().plusMonths(1);
                } else {
                    newStart = budget.getStartDate().plusYears(1);
                    newEnd = budget.getEndDate().plusYears(1);
                }

                Budget newBudget = new Budget(
                        budget.getCategoryId(),
                        budget.getAmount(),
                        budget.getPeriod(),
                        newStart,
                        newEnd
                );

                budgetService.addBudget(newBudget);
                showMessage("Budget copied to next period successfully!");
                refreshData();

            } catch (Exception e) {
                showError("Error copying budget: " + e.getMessage());
            }
        }
    }
}
