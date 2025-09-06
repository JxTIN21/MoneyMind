package main.java.com.moneymind.ui;

import main.java.com.moneymind.model.Transaction;
import main.java.com.moneymind.model.Category;
import main.java.com.moneymind.service.TransactionService;
import main.java.com.moneymind.service.CategoryService;
import main.java.com.moneymind.utils.CurrencyUtils;
import main.java.com.moneymind.utils.DateUtils;
import main.java.com.moneymind.utils.ValidationUtils;
import main.java.com.moneymind.datastructures.TransactionSorter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

/**
 * Panel for managing transactions with full CRUD operations
 */
public class TransactionPanel extends JPanel {
    private TransactionService transactionService;
    private CategoryService categoryService;

    // UI Components
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;

    // Form components
    private JTextField descriptionField;
    private JTextField amountField;
    private JComboBox<String> typeComboBox;
    private JComboBox<Category> categoryComboBox;
    private JSpinner dateSpinner;
    private JTextField searchField;

    // Buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;

    // Status components
    private JLabel totalIncomeLabel;
    private JLabel totalExpenseLabel;
    private JLabel netAmountLabel;
    private JLabel transactionCountLabel;

    // Current selection
    private Transaction selectedTransaction;

    public TransactionPanel(TransactionService transactionService, CategoryService categoryService) {
        this.transactionService = transactionService;
        this.categoryService = categoryService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Create table
        String[] columnNames = {"ID", "Date", "Description", "Category", "Type", "Amount"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setRowHeight(25);

        // Set up table sorting
        tableSorter = new TableRowSorter<>(tableModel);
        transactionTable.setRowSorter(tableSorter);

        // Hide ID column
        transactionTable.getColumnModel().getColumn(0).setMinWidth(0);
        transactionTable.getColumnModel().getColumn(0).setMaxWidth(0);
        transactionTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Date
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Description
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Category
        transactionTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Type
        transactionTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Amount

        // Custom renderer for amount column
        transactionTable.getColumnModel().getColumn(5).setCellRenderer(new AmountCellRenderer());

        // Form components
        descriptionField = new JTextField(20);
        amountField = new JTextField(10);

        typeComboBox = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});

        categoryComboBox = new JComboBox<>();
        categoryComboBox.setRenderer(new CategoryComboBoxRenderer());

        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MMM dd, yyyy");
        dateSpinner.setEditor(dateEditor);
        dateSpinner.setValue(new java.util.Date());

        searchField = new JTextField(20);

        // Buttons
        addButton = new JButton("Add Transaction");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        clearButton = new JButton("Clear Form");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Status labels
        totalIncomeLabel = new JLabel("Total Income: $0.00");
        totalExpenseLabel = new JLabel("Total Expense: $0.00");
        netAmountLabel = new JLabel("Net Amount: $0.00");
        transactionCountLabel = new JLabel("Transactions: 0");

        // Set initial colors for status labels
        totalIncomeLabel.setForeground(new Color(0, 150, 0));
        totalExpenseLabel.setForeground(new Color(200, 0, 0));
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel with form and search
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Transaction History"));
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with status
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = createFormPanel();
        topPanel.add(formPanel, BorderLayout.CENTER);

        // Search panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Transaction Details"));
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Description and Amount
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        formPanel.add(descriptionField, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Amount:"), gbc);
        gbc.gridx = 3;
        formPanel.add(amountField, gbc);

        // Row 2: Type and Category
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeComboBox, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 3;
        formPanel.add(categoryComboBox, gbc);

        // Row 3: Date and buttons
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        gbc.gridx = 1;
        formPanel.add(dateSpinner, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 2; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        JPanel searchFieldPanel = new JPanel(new FlowLayout());
        searchFieldPanel.add(new JLabel("Search:"));
        searchFieldPanel.add(searchField);

        JButton searchButton = new JButton("Search");
        JButton clearSearchButton = new JButton("Clear");

        JPanel searchButtonPanel = new JPanel(new FlowLayout());
        searchButtonPanel.add(searchButton);
        searchButtonPanel.add(clearSearchButton);

        // Quick filter buttons
        JPanel filterPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        JButton incomeFilterButton = new JButton("Income Only");
        JButton expenseFilterButton = new JButton("Expense Only");
        JButton thisMonthButton = new JButton("This Month");
        JButton allTransactionsButton = new JButton("All");

        filterPanel.add(incomeFilterButton);
        filterPanel.add(expenseFilterButton);
        filterPanel.add(thisMonthButton);
        filterPanel.add(allTransactionsButton);

        searchPanel.add(searchFieldPanel);
        searchPanel.add(searchButtonPanel);
        searchPanel.add(Box.createVerticalStrut(10));
        searchPanel.add(new JLabel("Quick Filters:"));
        searchPanel.add(filterPanel);

        // Add event handlers for search and filter buttons
        searchButton.addActionListener(e -> performSearch());
        clearSearchButton.addActionListener(e -> clearSearch());
        incomeFilterButton.addActionListener(e -> filterByType(Transaction.TransactionType.INCOME));
        expenseFilterButton.addActionListener(e -> filterByType(Transaction.TransactionType.EXPENSE));
        thisMonthButton.addActionListener(e -> filterByCurrentMonth());
        allTransactionsButton.addActionListener(e -> clearFilters());

        // Add enter key support for search field
        searchField.addActionListener(e -> performSearch());

        return searchPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Summary"));

        bottomPanel.add(transactionCountLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(totalIncomeLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(totalExpenseLabel);
        bottomPanel.add(Box.createHorizontalStrut(20));
        bottomPanel.add(netAmountLabel);

        return bottomPanel;
    }

    private void setupEventHandlers() {
        // Table selection listener
        transactionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleTableSelection();
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addTransaction());
        editButton.addActionListener(e -> editTransaction());
        deleteButton.addActionListener(e -> deleteTransaction());
        clearButton.addActionListener(e -> clearForm());

        // Type combo box listener
        typeComboBox.addActionListener(e -> updateCategoriesForType());
    }

    private void handleTableSelection() {
        int selectedRow = transactionTable.getSelectedRow();

        if (selectedRow >= 0) {
            // Convert view row to model row
            int modelRow = transactionTable.convertRowIndexToModel(selectedRow);
            Long transactionId = (Long) tableModel.getValueAt(modelRow, 0);

            try {
                selectedTransaction = transactionService.getTransactionById(transactionId);
                if (selectedTransaction != null) {
                    populateForm(selectedTransaction);
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            } catch (Exception e) {
                showError("Error loading transaction: " + e.getMessage());
            }
        } else {
            selectedTransaction = null;
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    private void populateForm(Transaction transaction) {
        descriptionField.setText(transaction.getDescription());
        amountField.setText(CurrencyUtils.formatWithoutSymbol(transaction.getAmount()));
        typeComboBox.setSelectedItem(transaction.getType().name());

        // Set date
        java.util.Date date = java.sql.Date.valueOf(transaction.getTransactionDate());
        dateSpinner.setValue(date);

        // Set category
        updateCategoriesForType();
        for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
            Category category = categoryComboBox.getItemAt(i);
            if (category.getId().equals(transaction.getCategoryId())) {
                categoryComboBox.setSelectedIndex(i);
                break;
            }
        }
    }

    private void addTransaction() {
        if (validateForm()) {
            try {
                Transaction transaction = createTransactionFromForm();
                Long id = transactionService.addTransaction(transaction);

                if (id != null) {
                    showMessage("Transaction added successfully!");
                    clearForm();
                    refreshData();
                } else {
                    showError("Failed to add transaction.");
                }
            } catch (Exception e) {
                showError("Error adding transaction: " + e.getMessage());
            }
        }
    }

    private void editTransaction() {
        if (selectedTransaction != null && validateForm()) {
            try {
                Transaction transaction = createTransactionFromForm();
                transaction.setId(selectedTransaction.getId());

                boolean success = transactionService.updateTransaction(transaction);

                if (success) {
                    showMessage("Transaction updated successfully!");
                    clearForm();
                    refreshData();
                } else {
                    showError("Failed to update transaction.");
                }
            } catch (Exception e) {
                showError("Error updating transaction: " + e.getMessage());
            }
        }
    }

    private void deleteTransaction() {
        if (selectedTransaction != null) {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this transaction?\n\n" +
                            selectedTransaction.getDescription() + " - " +
                            CurrencyUtils.format(selectedTransaction.getAmount()),
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                try {
                    boolean success = transactionService.deleteTransaction(selectedTransaction.getId());

                    if (success) {
                        showMessage("Transaction deleted successfully!");
                        clearForm();
                        refreshData();
                    } else {
                        showError("Failed to delete transaction.");
                    }
                } catch (Exception e) {
                    showError("Error deleting transaction: " + e.getMessage());
                }
            }
        }
    }

    private void clearForm() {
        descriptionField.setText("");
        amountField.setText("");
        typeComboBox.setSelectedIndex(0);
        dateSpinner.setValue(new java.util.Date());
        updateCategoriesForType();
        selectedTransaction = null;
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        transactionTable.clearSelection();
    }

    private Transaction createTransactionFromForm() {
        String description = descriptionField.getText().trim();
        BigDecimal amount = CurrencyUtils.parse(amountField.getText());
        Transaction.TransactionType type = Transaction.TransactionType.valueOf((String) typeComboBox.getSelectedItem());
        Category category = (Category) categoryComboBox.getSelectedItem();

        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        LocalDate transactionDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();

        return new Transaction(description, amount, transactionDate, category.getId(), type);
    }

    private boolean validateForm() {
        String description = descriptionField.getText().trim();
        String amountStr = amountField.getText().trim();
        Category selectedCategory = (Category) categoryComboBox.getSelectedItem();

        java.util.Date selectedDate = (java.util.Date) dateSpinner.getValue();
        LocalDate transactionDate = new java.sql.Date(selectedDate.getTime()).toLocalDate();

        ValidationUtils.ValidationResult result = ValidationUtils.validateTransaction(
                description, amountStr, transactionDate,
                selectedCategory != null ? selectedCategory.getId() : null
        );

        if (!result.isValid()) {
            showError("Validation Error:\n" + result.getAllErrors());
            return false;
        }

        return true;
    }

    private void updateCategoriesForType() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        Category.CategoryType categoryType = Category.CategoryType.valueOf(selectedType);

        categoryComboBox.removeAllItems();

        try {
            List<Category> categories = categoryService.getCategoriesByType(categoryType);
            for (Category category : categories) {
                categoryComboBox.addItem(category);
            }

            if (categories.isEmpty()) {
                JLabel noCategories = new JLabel("No categories available");
                // You might want to handle this case differently
            }
        } catch (Exception e) {
            showError("Error loading categories: " + e.getMessage());
        }
    }

    // Search and filter methods
    private void performSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            clearFilters();
            return;
        }

        try {
            List<Transaction> searchResults = transactionService.searchTransactions(searchText);
            updateTable(searchResults);
            updateSummary(searchResults);
        } catch (Exception e) {
            showError("Error searching transactions: " + e.getMessage());
        }
    }

    private void clearSearch() {
        searchField.setText("");
        clearFilters();
    }

    private void filterByType(Transaction.TransactionType type) {
        try {
            List<Transaction> filteredTransactions = transactionService.getTransactionsByType(type);
            updateTable(filteredTransactions);
            updateSummary(filteredTransactions);
        } catch (Exception e) {
            showError("Error filtering transactions: " + e.getMessage());
        }
    }

    private void filterByCurrentMonth() {
        try {
            DateUtils.DateRange currentMonth = DateUtils.getCurrentMonth();
            List<Transaction> monthTransactions = transactionService.getTransactionsByDateRange(
                    currentMonth.getStartDate(), currentMonth.getEndDate()
            );
            updateTable(monthTransactions);
            updateSummary(monthTransactions);
        } catch (Exception e) {
            showError("Error filtering by current month: " + e.getMessage());
        }
    }

    private void clearFilters() {
        searchField.setText("");
        refreshData();
    }

    // Table management
    private void updateTable(List<Transaction> transactions) {
        tableModel.setRowCount(0);

        for (Transaction transaction : transactions) {
            Object[] row = {
                    transaction.getId(),
                    DateUtils.formatForDisplay(transaction.getTransactionDate()),
                    transaction.getDescription(),
                    transaction.getCategoryName(),
                    transaction.getType().name(),
                    transaction.getAmount()
            };
            tableModel.addRow(row);
        }
    }

    private void updateSummary(List<Transaction> transactions) {
        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                totalIncome = totalIncome.add(transaction.getAmount());
            } else {
                totalExpense = totalExpense.add(transaction.getAmount());
            }
        }

        BigDecimal netAmount = totalIncome.subtract(totalExpense);

        totalIncomeLabel.setText("Total Income: " + CurrencyUtils.format(totalIncome));
        totalExpenseLabel.setText("Total Expense: " + CurrencyUtils.format(totalExpense));
        netAmountLabel.setText("Net Amount: " + CurrencyUtils.format(netAmount));
        transactionCountLabel.setText("Transactions: " + transactions.size());

        // Update net amount color
        if (netAmount.compareTo(BigDecimal.ZERO) > 0) {
            netAmountLabel.setForeground(new Color(0, 150, 0));
        } else if (netAmount.compareTo(BigDecimal.ZERO) < 0) {
            netAmountLabel.setForeground(new Color(200, 0, 0));
        } else {
            netAmountLabel.setForeground(Color.BLACK);
        }
    }

    public void refreshData() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            updateTable(transactions);
            updateSummary(transactions);
            updateCategoriesForType();
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

    // Custom cell renderer for amount column
    private class AmountCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (value instanceof BigDecimal) {
                BigDecimal amount = (BigDecimal) value;
                setText(CurrencyUtils.format(amount));

                if (!isSelected) {
                    // Get transaction type from the same row
                    String type = (String) table.getValueAt(row, 4);
                    if ("INCOME".equals(type)) {
                        setForeground(new Color(0, 150, 0));
                    } else {
                        setForeground(new Color(200, 0, 0));
                    }
                }
            }

            setHorizontalAlignment(SwingConstants.RIGHT);
            return c;
        }
    }

    // Custom combo box renderer for categories
    private class CategoryComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Category) {
                Category category = (Category) value;
                setText(category.getName());

                // Add indentation for subcategories
                int level = category.getLevel();
                if (level > 0) {
                    setText("  ".repeat(level) + "└ " + category.getName());
                }
            }

            return this;
        }
    }

    // Export functionality
    public void exportTransactions() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Implementation for CSV export would go here
                showMessage("Export functionality coming soon!");
            } catch (Exception e) {
                showError("Error exporting transactions: " + e.getMessage());
            }
        }
    }

    // Quick add functionality
    public void quickAddTransaction(String description, BigDecimal amount, Transaction.TransactionType type, String categoryName) {
        try {
            // Find category by name
            List<Category> categories = categoryService.getCategoriesByType(
                    type == Transaction.TransactionType.INCOME ?
                            Category.CategoryType.INCOME : Category.CategoryType.EXPENSE
            );

            Category selectedCategory = categories.stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName))
                    .findFirst()
                    .orElse(null);

            if (selectedCategory != null) {
                Transaction transaction = new Transaction(
                        description, amount, LocalDate.now(), selectedCategory.getId(), type
                );

                transactionService.addTransaction(transaction);
                refreshData();
                showMessage("Transaction added quickly!");
            } else {
                showError("Category '" + categoryName + "' not found!");
            }
        } catch (Exception e) {
            showError("Error adding quick transaction: " + e.getMessage());
        }
    }

    // Sorting functionality
    public void sortTransactionsByDate(boolean ascending) {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            TransactionSorter.sort(transactions,
                    TransactionSorter.SortBy.DATE,
                    ascending ? TransactionSorter.SortOrder.ASCENDING : TransactionSorter.SortOrder.DESCENDING
            );
            updateTable(transactions);
        } catch (Exception e) {
            showError("Error sorting transactions: " + e.getMessage());
        }
    }

    public void sortTransactionsByAmount(boolean ascending) {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            TransactionSorter.sort(transactions,
                    TransactionSorter.SortBy.AMOUNT,
                    ascending ? TransactionSorter.SortOrder.ASCENDING : TransactionSorter.SortOrder.DESCENDING
            );
            updateTable(transactions);
        } catch (Exception e) {
            showError("Error sorting transactions: " + e.getMessage());
        }
    }

    // Getters for external access
    public Transaction getSelectedTransaction() {
        return selectedTransaction;
    }

    public JTable getTransactionTable() {
        return transactionTable;
    }
}
