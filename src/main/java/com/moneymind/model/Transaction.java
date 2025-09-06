package main.java.com.moneymind.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transaction model class representing financial transactions
 */

public class Transaction {
    public enum TransactionType {
        INCOME, EXPENSE
    }

    private Long id;
    private String description;
    private BigDecimal amount;
    private LocalDate transactionDate;
    private Long categoryId;
    private String categoryName;
    private TransactionType type;
    private LocalDateTime createdAt;

    // Constructors
    public Transaction() {}

    public Transaction(String description, BigDecimal amount, LocalDate transactionDate, Long categoryId, TransactionType type) {
        this.description = description;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.categoryId = categoryId;
        this.type = type;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getTransactionDate() { return  transactionDate; }
    public void setTransactionDate(LocalDate transactionDate) { this.transactionDate = transactionDate; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Utility methods
    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return type == TransactionType.EXPENSE;
    }

    @Override
    public String toString() {
        return String.format("%s: %s %.2f (%s) on %s",
                type, description, amount, categoryName, transactionDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Transaction that = (Transaction) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
