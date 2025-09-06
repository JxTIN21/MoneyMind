package main.java.com.moneymind.datastructures;

import main.java.com.moneymind.model.Category;
import java.util.*;

/**
 * Binary Search Tree implementation for Category hierarchy
 * Supports efficiency searching, insertion, and traversal of categories
 */

public class CategoryTree {
    private TreeNode root;
    private Map<Long, Category> categoryMap;

    private class TreeNode {
        Category category;
        TreeNode left, right;

        TreeNode(Category category) {
            this.category = category;
        }
    }

    public CategoryTree() {
        this.categoryMap = new HashMap<>();
    }

    // Public methods
    public void insert(Category category) {
        root = insertRec(root, category);
        categoryMap.put(category.getId(), category);
    }

    public Category search(Long id) {
        return categoryMap.get(id);
    }

    public Category searchByName(String name) {
        return searchByNameRec(root, name);
    }

    public List<Category> getInOrder() {
        List<Category> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    public List<Category> getByType(Category.CategoryType type) {
        List<Category> result = new ArrayList<>();
        filterByType(root, type, result);
        return result;
    }

    public List<Category> getRootCategories() {
        List<Category> roots = new ArrayList<>();
        for (Category category : categoryMap.values()) {
            if (category.isRoot()) {
                roots.add(category);
            }
        }
        roots.sort(Comparator.comparing(Category::getName));
        return roots;
    }

    public void buildHierarchy(List<Category> categories) {
        // Clear existing data
        root = null;
        categoryMap.clear();

        // First pass: add all categories to map
        for (Category category : categories) {
            categoryMap.put(category.getId(), category);
            root = insertRec(root, category);
        }

        // Second pass: build parent-child relationships
        for (Category category : categories) {
            if (category.getParentId() != null) {
                Category parent = categoryMap.get(category.getParentId());
                if (parent != null) {
                    parent.addChild(category);
                    category.setParent(parent);
                }
            }
        }
    }

    public List<Category> searchByPattern(String pattern) {
        List<Category> results = new ArrayList<>();
        String lowerPattern = pattern.toLowerCase();

        for (Category category : categoryMap.values()) {
            if (category.getName().toLowerCase().contains(lowerPattern)) {
                results.add(category);
            }
        }

        results.sort(Comparator.comparing(Category::getName));
        return results;
    }

    // Private helper methods
    private TreeNode insertRec(TreeNode node, Category category) {
        if (node == null) {
            return new TreeNode(category);
        }

        int comparison = category.getName().compareToIgnoreCase(node.category.getName());
        if (comparison < 0) {
            node.left = insertRec(node.left, category);
        } else if (comparison > 0) {
            node.right = insertRec(node.right, category);
        }

        return node;
    }

    private Category searchByNameRec(TreeNode node, String name) {
        if (node == null) {
            return null;
        }

        int comparison = name.compareToIgnoreCase(node.category.getName());
        if (comparison == 0) {
            return node.category;
        } else if (comparison < 0) {
            return searchByNameRec(node.left, name);
        } else {
            return searchByNameRec(node.right, name);
        }
    }

    private void inOrderRec(TreeNode node, List<Category> result) {
        if (node != null) {
            inOrderRec(node.left, result);
            result.add(node.category);
            inOrderRec(node.right, result);
        }
    }

    private void filterByType(TreeNode node, Category.CategoryType type, List<Category> result) {
        if (node != null) {
            if (node.category.getType() == type) {
                result.add(node.category);
            }
            filterByType(node.left, type, result);
            filterByType(node.right, type, result);
        }
    }

    // Tree traversal methods for different use cases
    public void printTree() {
        System.out.println("Category Tree Structure:");
        printTreeRec(root, 0);
    }

    private void printTreeRec(TreeNode node, int depth) {
        if (node != null) {
            printTreeRec(node.right, depth + 1);
            System.out.println("  ".repeat(depth) + node.category.getName());
            printTreeRec(node.left, depth + 1);
        }
    }

    public int getSize() {
        return categoryMap.size();
    }

    public boolean isEmpty() {
        return categoryMap.isEmpty();
    }

    public void clear() {
        root = null;
        categoryMap.clear();
    }
}
