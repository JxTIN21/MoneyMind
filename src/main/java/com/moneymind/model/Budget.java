package main.java.com.moneymind.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

/**
 * Budget model class for managing spending limits
 */

public class Budget {
    public enum BudgetPeriod {
        MONTHLY, YEARLY
    }

    private Long id;
    private Long categoryId;
    private String categoryName;
    private BigDecimal amount;
    private BudgetPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal spent;
    private BigDecimal remaining;

    // Constructors
    public Budget() {}

    public Budget(Long categoryId, BigDecimal amount, BudgetPeriod period, LocalDate startDate, LocalDate endDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
        this.spent = BigDecimal.ZERO;
        this.remaining = amount;
    }

    //Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        updateRemaining();
    }

    public BudgetPeriod getPeriod() { return period; }
    public void setPeriod(BudgetPeriod period) { this.period = period; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getSpent() { return spent; }
    public void setSpent(BigDecimal spent) {
        this.spent = spent != null ? spent : BigDecimal.ZERO;
        updateRemaining();
    }

    public BigDecimal getRemaining() { return remaining; }

    // Utility methods
    private void updateRemaining() {
        if (amount != null && spent != null) {
            this.remaining = amount.subtract((spent));
        }
    }

    public double getUsagePercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return spent.divide(amount, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    public boolean isOverBudget() {
        return spent.compareTo(amount) > 0;
    }

    public boolean isNearLimit(double percentage) {
        return getUsagePercentage() >= percentage;
    }

    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }

    public String getStatus() {
        if (isOverBudget()) {
            return "OVER BUDGET";
        } else if (isNearLimit(90)) {
            return "NEAR LIMIT";
        } else if (isNearLimit(75)) {
            return "ON TRACK";
        } else {
            return "UNDER BUDGET";
        }
    }

    @Override
    public String toString() {
        return String.format("%s Budget: %.2f (%.1f%% used)",
                categoryName, amount, getUsagePercentage());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Budget budget = (Budget) obj;
        return id != null && id.equals(budget.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
