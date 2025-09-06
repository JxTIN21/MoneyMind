package main.java.com.moneymind.ui;

import main.java.com.moneymind.model.Category;
import main.java.com.moneymind.service.CategoryService;
import main.java.com.moneymind.utils.ValidationUtils;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for managing categories with tree structure visualization
 */
public class CategoryPanel extends JPanel {
    private CategoryService categoryService;

    // UI Components
    private JTree categoryTree;
    private DefaultMutableTreeNode rootNode;
    private DefaultTreeModel treeModel;

    // Form components
    private JTextField nameField;
    private JComboBox<String> typeComboBox;
    private JComboBox<Category> parentComboBox;

    // Buttons
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton clearButton;

    // Info panel
    private JLabel selectedCategoryLabel;
    private JLabel categoryPathLabel;
    private JLabel categoryLevelLabel;
    private JLabel subcategoryCountLabel;
    private JLabel transactionCountLabel;

    // Current selection
    private Category selectedCategory;

    public CategoryPanel(CategoryService categoryService) {
        this.categoryService = categoryService;

        initializeComponents();
        setupLayout();
        setupEventHandlers();
        refreshData();
    }

    private void initializeComponents() {
        // Create tree
        rootNode = new DefaultMutableTreeNode("Categories");
        treeModel = new DefaultTreeModel(rootNode);
        categoryTree = new JTree(treeModel);
        categoryTree.setShowsRootHandles(true);
        categoryTree.setRootVisible(true);
        categoryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Custom tree renderer
        categoryTree.setCellRenderer(new CategoryTreeCellRenderer());

        // Form components
        nameField = new JTextField(20);
        typeComboBox = new JComboBox<>(new String[]{"INCOME", "EXPENSE"});
        parentComboBox = new JComboBox<>();
        parentComboBox.addItem(null); // Allow null parent for root categories

        // Buttons
        addButton = new JButton("Add Category");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");
        clearButton = new JButton("Clear Form");

        editButton.setEnabled(false);
        deleteButton.setEnabled(false);

        // Info labels
        selectedCategoryLabel = new JLabel("No category selected");
        categoryPathLabel = new JLabel("");
        categoryLevelLabel = new JLabel("");
        subcategoryCountLabel = new JLabel("");
        transactionCountLabel = new JLabel("");
    }

    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Left panel with tree
        JPanel treePanel = createTreePanel();
        add(treePanel, BorderLayout.WEST);

        // Right panel with form and info
        JPanel rightPanel = createRightPanel();
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createTreePanel() {
        JPanel treePanel = new JPanel(new BorderLayout());
        treePanel.setBorder(BorderFactory.createTitledBorder("Category Structure"));
        treePanel.setPreferredSize(new Dimension(300, 0));

        JScrollPane treeScrollPane = new JScrollPane(categoryTree);
        treePanel.add(treeScrollPane, BorderLayout.CENTER);

        // Tree toolbar
        JPanel treeToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton expandAllButton = new JButton("Expand All");
        JButton collapseAllButton = new JButton("Collapse All");
        JButton refreshTreeButton = new JButton("Refresh");

        expandAllButton.addActionListener(e -> expandAllNodes());
        collapseAllButton.addActionListener(e -> collapseAllNodes());
        refreshTreeButton.addActionListener(e -> refreshData());

        treeToolbar.add(expandAllButton);
        treeToolbar.add(collapseAllButton);
        treeToolbar.add(refreshTreeButton);

        treePanel.add(treeToolbar, BorderLayout.SOUTH);

        return treePanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = createFormPanel();
        rightPanel.add(formPanel, BorderLayout.NORTH);

        // Info panel
        JPanel infoPanel = createInfoPanel();
        rightPanel.add(infoPanel, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createTitledBorder("Category Details"));
        formPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1: Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(nameField, gbc);

        // Row 2: Type
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Type:"), gbc);
        gbc.gridx = 1;
        formPanel.add(typeComboBox, gbc);

        // Row 3: Parent Category
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Parent:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        formPanel.add(parentComboBox, gbc);

        // Row 4: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3;
        formPanel.add(buttonPanel, gbc);

        return formPanel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Category Information"));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        selectedCategoryLabel.setFont(selectedCategoryLabel.getFont().deriveFont(Font.BOLD, 14f));

        infoPanel.add(selectedCategoryLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(categoryPathLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(categoryLevelLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(subcategoryCountLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(transactionCountLabel);

        // Add some spacing and additional info
        infoPanel.add(Box.createVerticalStrut(20));

        // Category statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setBorder(BorderFactory.createTitledBorder("Category Statistics"));
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));

        JLabel totalCategoriesLabel = new JLabel("Loading...");
        JLabel incomeCategoriesLabel = new JLabel("Loading...");
        JLabel expenseCategoriesLabel = new JLabel("Loading...");

        statsPanel.add(totalCategoriesLabel);
        statsPanel.add(incomeCategoriesLabel);
        statsPanel.add(expenseCategoriesLabel);

        infoPanel.add(statsPanel);

        // Update stats in background
        SwingUtilities.invokeLater(() -> updateCategoryStats(totalCategoriesLabel, incomeCategoriesLabel, expenseCategoriesLabel));

        return infoPanel;
    }

    private void updateCategoryStats(JLabel totalLabel, JLabel incomeLabel, JLabel expenseLabel) {
        try {
            List<Category> allCategories = categoryService.getAllCategories();
            List<Category> incomeCategories = categoryService.getCategoriesByType(Category.CategoryType.INCOME);
            List<Category> expenseCategories = categoryService.getCategoriesByType(Category.CategoryType.EXPENSE);

            totalLabel.setText("Total Categories: " + allCategories.size());
            incomeLabel.setText("Income Categories: " + incomeCategories.size());
            expenseLabel.setText("Expense Categories: " + expenseCategories.size());
        } catch (Exception e) {
            totalLabel.setText("Error loading statistics");
            incomeLabel.setText("");
            expenseLabel.setText("");
        }
    }

    private void setupEventHandlers() {
        // Tree selection listener
        categoryTree.addTreeSelectionListener(e -> handleTreeSelection());

        // Tree mouse listener for right-click context menu
        categoryTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showContextMenu(e.getX(), e.getY());
                }
            }
        });

        // Button listeners
        addButton.addActionListener(e -> addCategory());
        editButton.addActionListener(e -> editCategory());
        deleteButton.addActionListener(e -> deleteCategory());
        clearButton.addActionListener(e -> clearForm());

        // Type combo box listener
        typeComboBox.addActionListener(e -> updateParentComboBox());
    }

    private void handleTreeSelection() {
        TreePath selectionPath = categoryTree.getSelectionPath();

        if (selectionPath != null) {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectionPath.getLastPathComponent();

            if (selectedNode.getUserObject() instanceof Category) {
                selectedCategory = (Category) selectedNode.getUserObject();
                populateForm(selectedCategory);
                updateInfoPanel(selectedCategory);
                editButton.setEnabled(true);
                deleteButton.setEnabled(true);
            } else {
                // Root node selected
                selectedCategory = null;
                clearForm();
                clearInfoPanel();
                editButton.setEnabled(false);
                deleteButton.setEnabled(false);
            }
        }
    }

    private void populateForm(Category category) {
        nameField.setText(category.getName());
        typeComboBox.setSelectedItem(category.getType().name());

        updateParentComboBox();

        // Set parent category
        if (category.getParentId() != null) {
            for (int i = 0; i < parentComboBox.getItemCount(); i++) {
                Category parent = parentComboBox.getItemAt(i);
                if (parent != null && parent.getId().equals(category.getParentId())) {
                    parentComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            parentComboBox.setSelectedIndex(0); // null parent
        }
    }

    private void updateInfoPanel(Category category) {
        selectedCategoryLabel.setText("Selected: " + category.getName());
        categoryPathLabel.setText("Path: " + category.getFullPath());
        categoryLevelLabel.setText("Level: " + category.getLevel());
        subcategoryCountLabel.setText("Subcategories: " + category.getChildren().size());

        // Get transaction count (this would require a service method)
        try {
            boolean hasTransactions = categoryService.hasTransactions(category.getId());
            transactionCountLabel.setText("Has Transactions: " + (hasTransactions ? "Yes" : "No"));
        } catch (Exception e) {
            transactionCountLabel.setText("Transaction Info: Error loading");
        }
    }

    private void clearInfoPanel() {
        selectedCategoryLabel.setText("No category selected");
        categoryPathLabel.setText("");
        categoryLevelLabel.setText("");
        subcategoryCountLabel.setText("");
        transactionCountLabel.setText("");
    }

    private void addCategory() {
        if (validateForm()) {
            try {
                Category category = createCategoryFromForm();
                Long id = categoryService.addCategory(category);

                if (id != null) {
                    showMessage("Category added successfully!");
                    clearForm();
                    refreshData();
                } else {
                    showError("Failed to add category.");
                }
            } catch (Exception e) {
                showError("Error adding category: " + e.getMessage());
            }
        }
    }

    private void editCategory() {
        if (selectedCategory != null && validateForm()) {
            try {
                Category category = createCategoryFromForm();
                category.setId(selectedCategory.getId());

                boolean success = categoryService.updateCategory(category);

                if (success) {
                    showMessage("Category updated successfully!");
                    clearForm();
                    refreshData();
                } else {
                    showError("Failed to update category.");
                }
            } catch (Exception e) {
                showError("Error updating category: " + e.getMessage());
            }
        }
    }

    private void deleteCategory() {
        if (selectedCategory != null) {
            try {
                // Check if category has transactions or subcategories
                boolean hasTransactions = categoryService.hasTransactions(selectedCategory.getId());
                boolean hasSubcategories = categoryService.hasSubcategories(selectedCategory.getId());

                String warningMessage = "Are you sure you want to delete this category?\n\n" +
                        "Category: " + selectedCategory.getName();

                if (hasTransactions) {
                    warningMessage += "\n\nWarning: This category has existing transactions and cannot be deleted.";
                    showError(warningMessage);
                    return;
                }

                if (hasSubcategories) {
                    warningMessage += "\n\nWarning: This category has subcategories.";

                    String[] options = {"Delete with Subcategories", "Cancel"};
                    int result = JOptionPane.showOptionDialog(
                            this, warningMessage, "Confirm Delete",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                            null, options, options[1]
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        boolean success = categoryService.deleteCategoryAndSubcategories(selectedCategory.getId());
                        if (success) {
                            showMessage("Category and subcategories deleted successfully!");
                            clearForm();
                            refreshData();
                        } else {
                            showError("Failed to delete category tree.");
                        }
                    }
                } else {
                    int result = JOptionPane.showConfirmDialog(
                            this, warningMessage, "Confirm Delete",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE
                    );

                    if (result == JOptionPane.YES_OPTION) {
                        boolean success = categoryService.deleteCategory(selectedCategory.getId());
                        if (success) {
                            showMessage("Category deleted successfully!");
                            clearForm();
                            refreshData();
                        } else {
                            showError("Failed to delete category.");
                        }
                    }
                }
            } catch (Exception e) {
                showError("Error deleting category: " + e.getMessage());
            }
        }
    }

    private void clearForm() {
        nameField.setText("");
        typeComboBox.setSelectedIndex(0);
        updateParentComboBox();
        selectedCategory = null;
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        categoryTree.clearSelection();
        clearInfoPanel();
    }

    private Category createCategoryFromForm() {
        String name = nameField.getText().trim();
        Category.CategoryType type = Category.CategoryType.valueOf((String) typeComboBox.getSelectedItem());
        Category parent = (Category) parentComboBox.getSelectedItem();
        Long parentId = parent != null ? parent.getId() : null;

        return new Category(name, type, parentId);
    }

    private boolean validateForm() {
        String name = nameField.getText().trim();
        String type = (String) typeComboBox.getSelectedItem();
        Category parent = (Category) parentComboBox.getSelectedItem();

        ValidationUtils.ValidationResult result = ValidationUtils.validateCategory(
                name, type, parent != null ? parent.getId() : null
        );

        if (!result.isValid()) {
            showError("Validation Error:\n" + result.getAllErrors());
            return false;
        }

        // Additional validation for category name uniqueness
        try {
            Category.CategoryType categoryType = Category.CategoryType.valueOf(type);
            if (categoryService.categoryExists(name, categoryType)) {
                if (selectedCategory == null || !selectedCategory.getName().equals(name)) {
                    showError("A category with this name already exists for the selected type.");
                    return false;
                }
            }
        } catch (Exception e) {
            showError("Error validating category: " + e.getMessage());
            return false;
        }

        return true;
    }

    private void updateParentComboBox() {
        String selectedType = (String) typeComboBox.getSelectedItem();
        Category.CategoryType categoryType = Category.CategoryType.valueOf(selectedType);

        parentComboBox.removeAllItems();
        parentComboBox.addItem(null); // Allow null parent for root categories

        try {
            List<Category> categories = categoryService.getCategoriesByType(categoryType);
            for (Category category : categories) {
                // Don't allow a category to be its own parent
                if (selectedCategory == null || !selectedCategory.getId().equals(category.getId())) {
                    parentComboBox.addItem(category);
                }
            }
        } catch (Exception e) {
            showError("Error loading parent categories: " + e.getMessage());
        }
    }

    private void buildTreeFromCategories(List<Category> categories) {
        rootNode.removeAllChildren();

        // Create nodes for income and expense categories
        DefaultMutableTreeNode incomeNode = new DefaultMutableTreeNode("Income Categories");
        DefaultMutableTreeNode expenseNode = new DefaultMutableTreeNode("Expense Categories");

        rootNode.add(incomeNode);
        rootNode.add(expenseNode);

        // Add root categories first
        for (Category category : categories) {
            if (category.isRoot()) {
                DefaultMutableTreeNode categoryNode = new DefaultMutableTreeNode(category);

                if (category.getType() == Category.CategoryType.INCOME) {
                    incomeNode.add(categoryNode);
                } else {
                    expenseNode.add(categoryNode);
                }

                // Add subcategories recursively
                addSubcategoriesToNode(categoryNode, category);
            }
        }

        treeModel.reload();

        // Expand income and expense nodes by default
        categoryTree.expandPath(new TreePath(incomeNode.getPath()));
        categoryTree.expandPath(new TreePath(expenseNode.getPath()));
    }

    private void addSubcategoriesToNode(DefaultMutableTreeNode parentNode, Category parentCategory) {
        for (Category child : parentCategory.getChildren()) {
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
            parentNode.add(childNode);

            // Recursively add subcategories
            if (child.hasChildren()) {
                addSubcategoriesToNode(childNode, child);
            }
        }
    }

    private void expandAllNodes() {
        for (int i = 0; i < categoryTree.getRowCount(); i++) {
            categoryTree.expandRow(i);
        }
    }

    private void collapseAllNodes() {
        for (int i = categoryTree.getRowCount() - 1; i >= 0; i--) {
            categoryTree.collapseRow(i);
        }

        // Keep root expanded
        categoryTree.expandRow(0);
    }

    private void showContextMenu(int x, int y) {
        TreePath path = categoryTree.getPathForLocation(x, y);
        if (path != null) {
            categoryTree.setSelectionPath(path);

            JPopupMenu contextMenu = new JPopupMenu();

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            if (node.getUserObject() instanceof Category) {
                Category category = (Category) node.getUserObject();

                JMenuItem editItem = new JMenuItem("Edit Category");
                editItem.addActionListener(e -> {
                    selectedCategory = category;
                    populateForm(category);
                    updateInfoPanel(category);
                });

                JMenuItem deleteItem = new JMenuItem("Delete Category");
                deleteItem.addActionListener(e -> {
                    selectedCategory = category;
                    deleteCategory();
                });

                JMenuItem addSubcategoryItem = new JMenuItem("Add Subcategory");
                addSubcategoryItem.addActionListener(e -> {
                    clearForm();
                    typeComboBox.setSelectedItem(category.getType().name());
                    updateParentComboBox();

                    // Set this category as parent
                    for (int i = 0; i < parentComboBox.getItemCount(); i++) {
                        Category parent = parentComboBox.getItemAt(i);
                        if (parent != null && parent.getId().equals(category.getId())) {
                            parentComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                });

                JMenuItem showDetailsItem = new JMenuItem("Show Details");
                showDetailsItem.addActionListener(e -> showCategoryDetails(category));

                contextMenu.add(editItem);
                contextMenu.add(deleteItem);
                contextMenu.addSeparator();
                contextMenu.add(addSubcategoryItem);
                contextMenu.addSeparator();
                contextMenu.add(showDetailsItem);
            } else {
                // Root node or category type node
                JMenuItem addCategoryItem = new JMenuItem("Add New Category");
                addCategoryItem.addActionListener(e -> clearForm());

                JMenuItem expandAllItem = new JMenuItem("Expand All");
                expandAllItem.addActionListener(e -> expandAllNodes());

                JMenuItem collapseAllItem = new JMenuItem("Collapse All");
                collapseAllItem.addActionListener(e -> collapseAllNodes());

                contextMenu.add(addCategoryItem);
                contextMenu.addSeparator();
                contextMenu.add(expandAllItem);
                contextMenu.add(collapseAllItem);
            }

            contextMenu.show(categoryTree, x, y);
        }
    }

    private void showCategoryDetails(Category category) {
        try {
            boolean hasTransactions = categoryService.hasTransactions(category.getId());
            boolean hasSubcategories = categoryService.hasSubcategories(category.getId());

            String details = String.format(
                    "Category Details\n\n" +
                            "Name: %s\n" +
                            "Type: %s\n" +
                            "Level: %d\n" +
                            "Full Path: %s\n" +
                            "Has Subcategories: %s\n" +
                            "Has Transactions: %s\n" +
                            "Subcategory Count: %d\n",
                    category.getName(),
                    category.getType(),
                    category.getLevel(),
                    category.getFullPath(),
                    hasSubcategories ? "Yes" : "No",
                    hasTransactions ? "Yes" : "No",
                    category.getChildren().size()
            );

            JTextArea textArea = new JTextArea(details);
            textArea.setEditable(false);
            textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(400, 250));

            JOptionPane.showMessageDialog(this, scrollPane, "Category Details", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            showError("Error loading category details: " + e.getMessage());
        }
    }

    public void refreshData() {
        try {
            categoryService.refreshCategoryTree();
            List<Category> categories = categoryService.getAllCategories();
            buildTreeFromCategories(categories);
            updateParentComboBox();

            // Update statistics if they exist
            Component[] components = getComponents();
            for (Component comp : components) {
                if (comp instanceof JPanel) {
                    updateStatsIfExists((JPanel) comp);
                }
            }
        } catch (Exception e) {
            showError("Error refreshing data: " + e.getMessage());
        }
    }

    private void updateStatsIfExists(JPanel panel) {
        // This is a helper method to update stats when refreshing
        // Implementation would traverse the panel hierarchy to find stats labels
        // For simplicity, we'll skip this complex traversal
    }

    // Utility methods
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Custom tree cell renderer
    private class CategoryTreeCellRenderer extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                      boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            if (node.getUserObject() instanceof Category) {
                Category category = (Category) node.getUserObject();
                setText(category.getName());

                // Set icons based on category type
                if (category.getType() == Category.CategoryType.INCOME) {
                    setIcon(createColoredIcon(new Color(0, 150, 0))); // Green for income
                } else {
                    setIcon(createColoredIcon(new Color(200, 0, 0))); // Red for expense
                }

                // Add transaction indicator if available
                try {
                    boolean hasTransactions = categoryService.hasTransactions(category.getId());
                    if (hasTransactions) {
                        setText(category.getName() + " (💰)");
                    }
                } catch (Exception e) {
                    // Ignore error, just show category name
                }

                // Add subcategory indicator
                if (category.hasChildren()) {
                    setText(getText() + " [" + category.getChildren().size() + "]");
                }
            } else if (node.getUserObject().toString().contains("Income")) {
                setIcon(createColoredIcon(new Color(0, 150, 0)));
                setFont(getFont().deriveFont(Font.BOLD));
            } else if (node.getUserObject().toString().contains("Expense")) {
                setIcon(createColoredIcon(new Color(200, 0, 0)));
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setIcon(createColoredIcon(new Color(0, 100, 200))); // Blue for root
                setFont(getFont().deriveFont(Font.BOLD));
            }

            return this;
        }

        private Icon createColoredIcon(Color color) {
            return new Icon() {
                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(color);
                    g2d.fillOval(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(x + 2, y + 2, getIconWidth() - 4, getIconHeight() - 4);
                }

                @Override
                public int getIconWidth() { return 16; }

                @Override
                public int getIconHeight() { return 16; }
            };
        }
    }

    // Custom combo box renderer for parent selection
    private class CategoryComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value == null) {
                setText("(Root Category)");
                setFont(getFont().deriveFont(Font.ITALIC));
            } else if (value instanceof Category) {
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

    // Public methods for external access
    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void selectCategory(Long categoryId) {
        if (categoryId != null) {
            try {
                Category category = categoryService.getCategoryById(categoryId);
                if (category != null) {
                    // Find and select the node in the tree
                    DefaultMutableTreeNode nodeToSelect = findNodeForCategory(rootNode, category);
                    if (nodeToSelect != null) {
                        TreePath path = new TreePath(nodeToSelect.getPath());
                        categoryTree.setSelectionPath(path);
                        categoryTree.scrollPathToVisible(path);
                    }
                }
            } catch (Exception e) {
                showError("Error selecting category: " + e.getMessage());
            }
        }
    }

    private DefaultMutableTreeNode findNodeForCategory(DefaultMutableTreeNode node, Category targetCategory) {
        if (node.getUserObject() instanceof Category) {
            Category category = (Category) node.getUserObject();
            if (category.getId().equals(targetCategory.getId())) {
                return node;
            }
        }

        // Search children
        for (int i = 0; i < node.getChildCount(); i++) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
            DefaultMutableTreeNode found = findNodeForCategory(child, targetCategory);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public void addQuickCategory(String name, Category.CategoryType type, Long parentId) {
        try {
            Category category = new Category(name, type, parentId);

            ValidationUtils.ValidationResult result = ValidationUtils.validateCategory(
                    name, type.name(), parentId
            );

            if (result.isValid()) {
                Long id = categoryService.addCategory(category);
                if (id != null) {
                    refreshData();
                    showMessage("Category '" + name + "' added successfully!");
                } else {
                    showError("Failed to add category.");
                }
            } else {
                showError("Validation Error:\n" + result.getAllErrors());
            }
        } catch (Exception e) {
            showError("Error adding quick category: " + e.getMessage());
        }
    }

    public List<Category> getExpenseCategories() {
        try {
            return categoryService.getCategoriesByType(Category.CategoryType.EXPENSE);
        } catch (Exception e) {
            showError("Error loading expense categories: " + e.getMessage());
            return List.of();
        }
    }

    public List<Category> getIncomeCategories() {
        try {
            return categoryService.getCategoriesByType(Category.CategoryType.INCOME);
        } catch (Exception e) {
            showError("Error loading income categories: " + e.getMessage());
            return List.of();
        }
    }

    // Import/Export functionality
    public void exportCategories() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Export implementation would go here
                showMessage("Category export functionality coming soon!");
            } catch (Exception e) {
                showError("Error exporting categories: " + e.getMessage());
            }
        }
    }

    public void importCategories() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("JSON files", "json"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Import implementation would go here
                showMessage("Category import functionality coming soon!");
                refreshData();
            } catch (Exception e) {
                showError("Error importing categories: " + e.getMessage());
            }
        }
    }

    // Search functionality
    public void searchCategories(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            refreshData();
            return;
        }

        try {
            List<Category> searchResults = categoryService.searchCategories(searchTerm);

            if (searchResults.isEmpty()) {
                showMessage("No categories found matching: " + searchTerm);
            } else {
                // Highlight matching categories in the tree
                highlightCategories(searchResults);
            }
        } catch (Exception e) {
            showError("Error searching categories: " + e.getMessage());
        }
    }

    private void highlightCategories(List<Category> categories) {
        // Expand all nodes first
        expandAllNodes();

        // Select the first matching category
        if (!categories.isEmpty()) {
            selectCategory(categories.get(0).getId());
        }

        // You could also implement visual highlighting here
        showMessage("Found " + categories.size() + " matching categories");
    }

    // Category validation helpers
    public boolean isValidCategoryName(String name) {
        try {
            String type = (String) typeComboBox.getSelectedItem();
            Category.CategoryType categoryType = Category.CategoryType.valueOf(type);
            return !categoryService.categoryExists(name, categoryType);
        } catch (Exception e) {
            return false;
        }
    }

    public String[] suggestCategoryNames(String prefix) {
        // This could implement intelligent category name suggestions
        // Based on common category patterns
        String[] commonIncome = {"Salary", "Freelance", "Investments", "Business", "Rental", "Other Income"};
        String[] commonExpense = {"Food", "Transportation", "Housing", "Utilities", "Entertainment",
                "Healthcare", "Shopping", "Insurance", "Education", "Travel"};

        String type = (String) typeComboBox.getSelectedItem();
        String[] suggestions = "INCOME".equals(type) ? commonIncome : commonExpense;

        // Filter suggestions based on prefix
        return java.util.Arrays.stream(suggestions)
                .filter(s -> s.toLowerCase().startsWith(prefix.toLowerCase()))
                .toArray(String[]::new);
    }

    // Category management utilities
    public void reorganizeCategory(Long categoryId, Long newParentId) {
        try {
            Category category = categoryService.getCategoryById(categoryId);
            if (category != null) {
                category.setParentId(newParentId);
                boolean success = categoryService.updateCategory(category);

                if (success) {
                    refreshData();
                    showMessage("Category reorganized successfully!");
                } else {
                    showError("Failed to reorganize category.");
                }
            }
        } catch (Exception e) {
            showError("Error reorganizing category: " + e.getMessage());
        }
    }

    public void duplicateCategory(Category category) {
        if (category != null) {
            String newName = category.getName() + " (Copy)";

            // Check if name already exists and create unique name
            int counter = 1;
            String baseName = newName;
            while (isValidCategoryName(newName) == false) {
                counter++;
                newName = baseName.replace("(Copy)", "(Copy " + counter + ")");
            }

            try {
                Category duplicatedCategory = new Category(newName, category.getType(), category.getParentId());
                Long id = categoryService.addCategory(duplicatedCategory);

                if (id != null) {
                    refreshData();
                    showMessage("Category duplicated as: " + newName);
                } else {
                    showError("Failed to duplicate category.");
                }
            } catch (Exception e) {
                showError("Error duplicating category: " + e.getMessage());
            }
        }
    }

    // Drag and drop support (placeholder for future implementation)
    public void enableDragAndDrop() {
        // This would implement drag and drop functionality for reorganizing categories
        // For now, it's a placeholder
        categoryTree.setDragEnabled(false); // Disabled until implemented
    }

    // Category health check
    public void performCategoryHealthCheck() {
        try {
            List<Category> allCategories = categoryService.getAllCategories();
            List<String> issues = new ArrayList<>();

            // Check for orphaned categories
            for (Category category : allCategories) {
                if (category.getParentId() != null) {
                    Category parent = categoryService.getCategoryById(category.getParentId());
                    if (parent == null) {
                        issues.add("Orphaned category: " + category.getName());
                    }
                }
            }

            // Check for empty categories (no transactions and no subcategories)
            for (Category category : allCategories) {
                if (!category.hasChildren() && !categoryService.hasTransactions(category.getId())) {
                    issues.add("Empty category: " + category.getName());
                }
            }

            // Show results
            if (issues.isEmpty()) {
                showMessage("Category health check passed! No issues found.");
            } else {
                StringBuilder report = new StringBuilder("Category Health Check Results:\n\n");
                for (String issue : issues) {
                    report.append("• ").append(issue).append("\n");
                }

                JTextArea textArea = new JTextArea(report.toString());
                textArea.setEditable(false);
                textArea.setRows(10);
                textArea.setColumns(50);

                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "Category Health Check",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception e) {
            showError("Error performing health check: " + e.getMessage());
        }
    }

    // Tree navigation helpers
    public void selectFirstCategory() {
        if (rootNode.getChildCount() > 0) {
            DefaultMutableTreeNode firstTypeNode = (DefaultMutableTreeNode) rootNode.getChildAt(0);
            if (firstTypeNode.getChildCount() > 0) {
                DefaultMutableTreeNode firstCategory = (DefaultMutableTreeNode) firstTypeNode.getChildAt(0);
                TreePath path = new TreePath(firstCategory.getPath());
                categoryTree.setSelectionPath(path);
                categoryTree.scrollPathToVisible(path);
            }
        }
    }

    public void selectNextCategory() {
        TreePath currentPath = categoryTree.getSelectionPath();
        if (currentPath != null) {
            int currentRow = categoryTree.getRowForPath(currentPath);
            if (currentRow < categoryTree.getRowCount() - 1) {
                categoryTree.setSelectionRow(currentRow + 1);
            }
        } else {
            selectFirstCategory();
        }
    }

    public void selectPreviousCategory() {
        TreePath currentPath = categoryTree.getSelectionPath();
        if (currentPath != null) {
            int currentRow = categoryTree.getRowForPath(currentPath);
            if (currentRow > 0) {
                categoryTree.setSelectionRow(currentRow - 1);
            }
        } else {
            selectFirstCategory();
        }
    }

    // Keyboard shortcuts support
    private void setupKeyboardShortcuts() {
        // Add common keyboard shortcuts
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        // Ctrl+N for new category
        inputMap.put(KeyStroke.getKeyStroke("ctrl N"), "newCategory");
        actionMap.put("newCategory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
                nameField.requestFocus();
            }
        });

        // Delete key for delete category
        inputMap.put(KeyStroke.getKeyStroke("DELETE"), "deleteCategory");
        actionMap.put("deleteCategory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedCategory != null) {
                    deleteCategory();
                }
            }
        });

        // F2 for edit category
        inputMap.put(KeyStroke.getKeyStroke("F2"), "editCategory");
        actionMap.put("editCategory", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedCategory != null) {
                    nameField.requestFocus();
                    nameField.selectAll();
                }
            }
        });

        // F5 for refresh
        inputMap.put(KeyStroke.getKeyStroke("F5"), "refresh");
        actionMap.put("refresh", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshData();
            }
        });
    }

    // Initialize keyboard shortcuts in constructor
    {
        setupKeyboardShortcuts();
    }
}
