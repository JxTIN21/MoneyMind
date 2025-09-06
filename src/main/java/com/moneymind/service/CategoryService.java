package main.java.com.moneymind.service;

import main.java.com.moneymind.database.DatabaseManager;
import main.java.com.moneymind.model.Category;
import main.java.com.moneymind.datastructures.CategoryTree;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Category CRUD operations and tree management
 */
public class CategoryService {
    private DatabaseManager dbManager;
    private CategoryTree categoryTree;

    public CategoryService() {
        this.dbManager = DatabaseManager.getInstance();
        this.categoryTree = new CategoryTree();
        loadCategoryTree();
    }

    // Create operations
    public Long addCategory(Category category) throws SQLException {
        String sql = "INSERT INTO categories (name, parent_id, type) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getName());
            if (category.getParentId() != null) {
                stmt.setLong(2, category.getParentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, category.getType().name());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    category.setId(id);

                    // Add to tree
                    categoryTree.insert(category);
                    loadCategoryTree(); // Rebuild tree to ensure hierarchy

                    return id;
                }
            }
            return null;
        }
    }

    // Read operations
    public Category getCategoryById(Long id) throws SQLException {
        Category category = categoryTree.search(id);
        if (category != null) {
            return category;
        }

        // Fallback to database if not in tree
        String sql = "SELECT * FROM categories WHERE id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
            return null;
        }
    }

    public Category getCategoryByName(String name) throws SQLException {
        Category category = categoryTree.searchByName(name);
        if (category != null) {
            return category;
        }

        // Fallback to database
        String sql = "SELECT * FROM categories WHERE name = ? COLLATE NOCASE";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
            return null;
        }
    }

    public List<Category> getAllCategories() throws SQLException {
        return categoryTree.getInOrder();
    }

    public List<Category> getCategoriesByType(Category.CategoryType type) throws SQLException {
        return categoryTree.getByType(type);
    }

    public List<Category> getRootCategories() throws SQLException {
        return categoryTree.getRootCategories();
    }

    public List<Category> getSubcategories(Long parentId) throws SQLException {
        Category parent = getCategoryById(parentId);
        if (parent != null) {
            return new ArrayList<>(parent.getChildren());
        }
        return new ArrayList<>();
    }

    public List<Category> searchCategories(String pattern) throws SQLException {
        return categoryTree.searchByPattern(pattern);
    }

    // Update operations
    public boolean updateCategory(Category category) throws SQLException {
        String sql = "UPDATE categories SET name = ?, parent_id = ?, type = ? WHERE id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setString(1, category.getName());
            if (category.getParentId() != null) {
                stmt.setLong(2, category.getParentId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, category.getType().name());
            stmt.setLong(4, category.getId());

            boolean updated = stmt.executeUpdate() > 0;
            if (updated) {
                loadCategoryTree(); // Rebuild tree
            }
            return updated;
        }
    }

    // Delete operations
    public boolean deleteCategory(Long id) throws SQLException {
        // Check if category has transactions
        if (hasTransactions(id)) {
            throw new SQLException("Cannot delete category with existing transactions");
        }

        // Check if category has subcategories
        Category category = getCategoryById(id);
        if (category != null && category.hasChildren()) {
            throw new SQLException("Cannot delete category with subcategories");
        }

        String sql = "DELETE FROM categories WHERE id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, id);
            boolean deleted = stmt.executeUpdate() > 0;
            if (deleted) {
                loadCategoryTree(); // Rebuild tree
            }
            return deleted;
        }
    }

    public boolean deleteCategoryAndSubcategories(Long id) throws SQLException {
        // Check if any category in the subtree has transactions
        Category category = getCategoryById(id);
        if (category != null && hasTransactionsInSubtree(category)) {
            throw new SQLException("Cannot delete category tree with existing transactions");
        }

        String sql = "DELETE FROM categories WHERE id = ? OR parent_id = ?";

        try {
            dbManager.beginTransaction();

            // Delete recursively
            deleteSubtreeRecursive(id);

            dbManager.commitTransaction();
            loadCategoryTree(); // Rebuild tree
            return true;

        } catch (SQLException e) {
            dbManager.rollbackTransaction();
            throw e;
        }
    }

    // Tree operations
    public CategoryTree getCategoryTree() {
        return categoryTree;
    }

    public void refreshCategoryTree() throws SQLException {
        loadCategoryTree();
    }

    public String getCategoryPath(Long categoryId) throws SQLException {
        Category category = getCategoryById(categoryId);
        return category != null ? category.getFullPath() : "";
    }

    public int getCategoryLevel(Long categoryId) throws SQLException {
        Category category = getCategoryById(categoryId);
        return category != null ? category.getLevel() : 0;
    }

    public boolean isCategoryAncestor(Long ancestorId, Long descendantId) throws SQLException {
        Category descendant = getCategoryById(descendantId);
        while (descendant != null && descendant.getParent() != null) {
            if (descendant.getParent().getId().equals(ancestorId)) {
                return true;
            }
            descendant = descendant.getParent();
        }
        return false;
    }

    // Validation methods
    public boolean categoryExists(String name, Category.CategoryType type) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE name = ? AND type = ? COLLATE NOCASE";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, type.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public boolean hasTransactions(Long categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE category_id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public boolean hasSubcategories(Long categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM categories WHERE parent_id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    // Private helper methods
    private void loadCategoryTree() {
        try {
            List<Category> categories = loadCategoriesFromDatabase();
            categoryTree.buildHierarchy(categories);
        } catch (SQLException e) {
            System.err.println("Error loading category tree: " + e.getMessage());
        }
    }

    private List<Category> loadCategoriesFromDatabase() throws SQLException {
        String sql = "SELECT * FROM categories ORDER BY name";
        List<Category> categories = new ArrayList<>();

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categories.add(mapResultSetToCategory(rs));
            }
        }

        return categories;
    }

    private boolean hasTransactionsInSubtree(Category category) throws SQLException {
        if (hasTransactions(category.getId())) {
            return true;
        }

        for (Category child : category.getChildren()) {
            if (hasTransactionsInSubtree(child)) {
                return true;
            }
        }

        return false;
    }

    private void deleteSubtreeRecursive(Long categoryId) throws SQLException {
        // First delete all subcategories
        String selectSubcategoriesSql = "SELECT id FROM categories WHERE parent_id = ?";
        List<Long> subcategoryIds = new ArrayList<>();

        try (PreparedStatement stmt = dbManager.prepareStatement(selectSubcategoriesSql)) {
            stmt.setLong(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                subcategoryIds.add(rs.getLong("id"));
            }
        }

        // Recursively delete subcategories
        for (Long subcategoryId : subcategoryIds) {
            deleteSubtreeRecursive(subcategoryId);
        }

        // Finally delete the category itself
        String deleteSql = "DELETE FROM categories WHERE id = ?";
        try (PreparedStatement stmt = dbManager.prepareStatement(deleteSql)) {
            stmt.setLong(1, categoryId);
            stmt.executeUpdate();
        }
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setId(rs.getLong("id"));
        category.setName(rs.getString("name"));

        Long parentId = rs.getLong("parent_id");
        if (!rs.wasNull()) {
            category.setParentId(parentId);
        }

        category.setType(Category.CategoryType.valueOf(rs.getString("type")));

        return category;
    }
}
