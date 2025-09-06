package main.java.com.moneymind.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Category model class for organizing transactions in a tree structure
 */
public class Category {
    public enum CategoryType {
        INCOME, EXPENSE
    }

    private Long id;
    private String name;
    private Long parentId;
    private CategoryType type;
    private List<Category> children;
    private Category parent;

    // Constructors
    public Category() {
        this.children = new ArrayList<>();
    }

    public Category(String name, CategoryType type) {
        this();
        this.name = name;
        this.type = type;
    }

    public Category(String name, CategoryType type, Long parentId) {
        this(name, type);
        this.parentId = parentId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public List<Category> getChildren() { return children; }
    public void setChildren(List<Category> children) { this.children = children; }

    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }

    // Tree operations
    public void addChild(Category child) {
        if (!children.contains(child)) {
            children.add(child);
            child.setParent(this);
            child.setParentId(this.id);
        }
    }

    public void removeChild(Category child) {
        children.remove(child);
        child.setParent(null);
        child.setParentId(null);
    }

    public boolean hasChildren() {
        return !children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null && parentId == null;
    }

    public String getFullPath() {
        if (isRoot()) {
            return name;
        }
        return (parent != null ? parent.getFullPath() : "") + " > " + name;
    }

    public int getLevel() {
        int level = 0;
        Category current = this.parent;
        while (current != null) {
            level++;
            current = current.getParent();
        }
        return level;
    }

    @Override
    public String toString() {
        return name + (hasChildren() ? " (" + children.size() + " subcategories)" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Category category = (Category) obj;
        return id != null && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
