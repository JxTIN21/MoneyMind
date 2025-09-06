package main.java.com.moneymind.service;

import main.java.com.moneymind.database.DatabaseManager;
import main.java.com.moneymind.model.Budget;
import main.java.com.moneymind.model.Transaction;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Budget CRUD operations and budget tracking
 */
public class BudgetService {
    private DatabaseManager dbManager;
    private TransactionService transactionService;

    public BudgetService() {
        this.dbManager = DatabaseManager.getInstance();
        this.transactionService = new TransactionService();
    }

    // Create operations
    public Long addBudget(Budget budget) throws SQLException {
        String sql = """
            INSERT INTO budgets (category_id, amount, period, start_date, end_date)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, budget.getCategoryId());
            stmt.setBigDecimal(2, budget.getAmount());
            stmt.setString(3, budget.getPeriod().name());
            stmt.setDate(4, Date.valueOf(budget.getStartDate()));
            stmt.setDate(5, Date.valueOf(budget.getEndDate()));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    budget.setId(id);
                    return id;
                }
            }
            return null;
        }
    }

    // Read operations
    public Budget getBudgetById(Long id) throws SQLException {
        String sql = """
            SELECT b.*, c.name as category_name 
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.id = ?
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                updateBudgetSpentAmount(budget);
                return budget;
            }
            return null;
        }
    }

    public List<Budget> getAllBudgets() throws SQLException {
        String sql = """
            SELECT b.*, c.name as category_name 
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            ORDER BY b.start_date DESC
        """;

        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                updateBudgetSpentAmount(budget);
                budgets.add(budget);
            }
        }
        return budgets;
    }

    public List<Budget> getActiveBudgets() throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = """
            SELECT b.*, c.name as category_name 
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.start_date <= ? AND b.end_date >= ?
            ORDER BY b.start_date DESC
        """;

        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(today));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                updateBudgetSpentAmount(budget);
                budgets.add(budget);
            }
        }
        return budgets;
    }

    public List<Budget> getBudgetsByCategory(Long categoryId) throws SQLException {
        String sql = """
            SELECT b.*, c.name as category_name 
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.category_id = ?
            ORDER BY b.start_date DESC
        """;

        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                updateBudgetSpentAmount(budget);
                budgets.add(budget);
            }
        }
        return budgets;
    }

    public List<Budget> getBudgetsByPeriod(Budget.BudgetPeriod period) throws SQLException {
        String sql = """
            SELECT b.*, c.name as category_name 
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.period = ?
            ORDER BY b.start_date DESC
        """;

        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setString(1, period.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                updateBudgetSpentAmount(budget);
                budgets.add(budget);
            }
        }
        return budgets;
    }

    public List<Budget> getOverBudgets() throws SQLException {
        List<Budget> allBudgets = getAllBudgets();
        List<Budget> overBudgets = new ArrayList<>();

        for (Budget budget : allBudgets) {
            if (budget.isOverBudget()) {
                overBudgets.add(budget);
            }
        }

        return overBudgets;
    }

    public List<Budget> getNearLimitBudgets(double percentage) throws SQLException {
        List<Budget> allBudgets = getAllBudgets();
        List<Budget> nearLimitBudgets = new ArrayList<>();

        for (Budget budget : allBudgets) {
            if (budget.isNearLimit(percentage) && !budget.isOverBudget()) {
                nearLimitBudgets.add(budget);
            }
        }

        return nearLimitBudgets;
    }

    // Update operations
    public boolean updateBudget(Budget budget) throws SQLException {
        String sql = """
            UPDATE budgets 
            SET category_id = ?, amount = ?, period = ?, start_date = ?, end_date = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, budget.getCategoryId());
            stmt.setBigDecimal(2, budget.getAmount());
            stmt.setString(3, budget.getPeriod().name());
            stmt.setDate(4, Date.valueOf(budget.getStartDate()));
            stmt.setDate(5, Date.valueOf(budget.getEndDate()));
            stmt.setLong(6, budget.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete operations
    public boolean deleteBudget(Long id) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public int deleteBudgetsByCategory(Long categoryId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE category_id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            return stmt.executeUpdate();
        }
    }

    public int deleteExpiredBudgets() throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = "DELETE FROM budgets WHERE end_date < ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));
            return stmt.executeUpdate();
        }
    }

    // Budget analysis methods
    public Budget getCurrentBudgetForCategory(Long categoryId) throws SQLException {
        LocalDate today = LocalDate.now();
        String sql = """
            SELECT b.*, c.name as category_name 
            FROM budgets b
            JOIN categories c ON b.category_id = c.id
            WHERE b.category_id = ? AND b.start_date <= ? AND b.end_date >= ?
            ORDER BY b.start_date DESC
            LIMIT 1
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            stmt.setDate(2, Date.valueOf(today));
            stmt.setDate(3, Date.valueOf(today));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Budget budget = mapResultSetToBudget(rs);
                updateBudgetSpentAmount(budget);
                return budget;
            }
            return null;
        }
    }

    public BigDecimal getTotalBudgetAmount() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM budgets WHERE start_date <= ? AND end_date >= ?";
        LocalDate today = LocalDate.now();

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(today));
            stmt.setDate(2, Date.valueOf(today));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalSpentAmount() throws SQLException {
        List<Budget> activeBudgets = getActiveBudgets();
        BigDecimal totalSpent = BigDecimal.ZERO;

        for (Budget budget : activeBudgets) {
            totalSpent = totalSpent.add(budget.getSpent());
        }

        return totalSpent;
    }

    public double getOverallBudgetUsage() throws SQLException {
        BigDecimal totalBudget = getTotalBudgetAmount();
        BigDecimal totalSpent = getTotalSpentAmount();

        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }

        return totalSpent.divide(totalBudget, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    // Budget recommendations and alerts
    public List<String> getBudgetAlerts() throws SQLException {
        List<String> alerts = new ArrayList<>();
        List<Budget> activeBudgets = getActiveBudgets();

        for (Budget budget : activeBudgets) {
            if (budget.isOverBudget()) {
                alerts.add(String.format("OVER BUDGET: %s is %.2f over the limit",
                        budget.getCategoryName(),
                        budget.getSpent().subtract(budget.getAmount())));
            } else if (budget.isNearLimit(90)) {
                alerts.add(String.format("NEAR LIMIT: %s has used %.1f%% of budget",
                        budget.getCategoryName(),
                        budget.getUsagePercentage()));
            }
        }

        return alerts;
    }

    public BigDecimal getRemainingBudget(Long categoryId) throws SQLException {
        Budget currentBudget = getCurrentBudgetForCategory(categoryId);
        if (currentBudget != null) {
            return currentBudget.getRemaining();
        }
        return BigDecimal.ZERO;
    }

    public int getDaysRemainingInBudget(Long budgetId) throws SQLException {
        Budget budget = getBudgetById(budgetId);
        if (budget != null) {
            LocalDate today = LocalDate.now();
            if (today.isAfter(budget.getEndDate())) {
                return 0;
            }
            return (int) today.until(budget.getEndDate()).getDays();
        }
        return 0;
    }

    // Utility methods for budget creation
    public Budget createMonthlyBudget(Long categoryId, BigDecimal amount, int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        return new Budget(categoryId, amount, Budget.BudgetPeriod.MONTHLY, startDate, endDate);
    }

    public Budget createYearlyBudget(Long categoryId, BigDecimal amount, int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return new Budget(categoryId, amount, Budget.BudgetPeriod.YEARLY, startDate, endDate);
    }

    public boolean budgetExistsForPeriod(Long categoryId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM budgets 
            WHERE category_id = ? AND (
                (start_date <= ? AND end_date >= ?) OR 
                (start_date <= ? AND end_date >= ?) OR
                (start_date >= ? AND end_date <= ?)
            )
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(startDate));
            stmt.setDate(4, Date.valueOf(endDate));
            stmt.setDate(5, Date.valueOf(endDate));
            stmt.setDate(6, Date.valueOf(startDate));
            stmt.setDate(7, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    // Budget performance analysis
    public List<Budget> getTopPerformingBudgets(int limit) throws SQLException {
        List<Budget> allBudgets = getActiveBudgets();

        // Sort by best performance (lowest usage percentage that's still reasonable)
        allBudgets.sort((b1, b2) -> {
            double usage1 = b1.getUsagePercentage();
            double usage2 = b2.getUsagePercentage();

            // Prefer budgets with 50-80% usage (good utilization without going over)
            double score1 = calculatePerformanceScore(usage1);
            double score2 = calculatePerformanceScore(usage2);

            return Double.compare(score2, score1); // Higher score is better
        });

        return allBudgets.subList(0, Math.min(limit, allBudgets.size()));
    }

    private double calculatePerformanceScore(double usagePercentage) {
        if (usagePercentage > 100) return 0; // Over budget is worst
        if (usagePercentage >= 50 && usagePercentage <= 80) return 100; // Ideal range
        if (usagePercentage >= 30 && usagePercentage < 50) return 80; // Good utilization
        if (usagePercentage >= 80 && usagePercentage <= 95) return 60; // Close to limit
        if (usagePercentage > 95) return 20; // Too close to limit
        return 40; // Very low utilization
    }

    public List<Budget> getWorstPerformingBudgets(int limit) throws SQLException {
        List<Budget> allBudgets = getActiveBudgets();

        // Sort by worst performance (over budget or very close)
        allBudgets.sort((b1, b2) -> {
            if (b1.isOverBudget() && !b2.isOverBudget()) return -1;
            if (!b1.isOverBudget() && b2.isOverBudget()) return 1;

            // Both over budget or both under - sort by usage percentage descending
            return Double.compare(b2.getUsagePercentage(), b1.getUsagePercentage());
        });

        return allBudgets.subList(0, Math.min(limit, allBudgets.size()));
    }

    // Budget forecasting
    public BigDecimal predictBudgetUtilization(Long budgetId, LocalDate targetDate) throws SQLException {
        Budget budget = getBudgetById(budgetId);
        if (budget == null || targetDate.isAfter(budget.getEndDate())) {
            return BigDecimal.ZERO;
        }

        LocalDate today = LocalDate.now();
        if (targetDate.isBefore(today)) {
            return budget.getSpent(); // Historical data
        }

        // Calculate spending rate per day
        long daysElapsed = budget.getStartDate().until(today).getDays();
        if (daysElapsed <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal dailySpendingRate = budget.getSpent().divide(
                BigDecimal.valueOf(daysElapsed), 2, BigDecimal.ROUND_HALF_UP);

        long daysToTarget = budget.getStartDate().until(targetDate).getDays();
        return dailySpendingRate.multiply(BigDecimal.valueOf(daysToTarget));
    }

    // Budget health metrics
    public double getBudgetHealthScore() throws SQLException {
        List<Budget> activeBudgets = getActiveBudgets();
        if (activeBudgets.isEmpty()) {
            return 100.0; // No budgets = perfect score?
        }

        double totalScore = 0.0;
        int budgetCount = 0;

        for (Budget budget : activeBudgets) {
            double usage = budget.getUsagePercentage();
            double budgetScore = calculatePerformanceScore(usage);
            totalScore += budgetScore;
            budgetCount++;
        }

        return totalScore / budgetCount;
    }

    public String getBudgetHealthDescription(double healthScore) {
        if (healthScore >= 90) {
            return "Excellent - Your budgets are well-managed and on track";
        } else if (healthScore >= 70) {
            return "Good - Most budgets are performing well with room for minor improvements";
        } else if (healthScore >= 50) {
            return "Fair - Some budgets need attention to avoid overspending";
        } else if (healthScore >= 30) {
            return "Poor - Multiple budgets are at risk or already exceeded";
        } else {
            return "Critical - Immediate budget review and spending cuts needed";
        }
    }

    // Advanced budget operations
    public boolean copyBudgetToNextPeriod(Long budgetId) throws SQLException {
        Budget originalBudget = getBudgetById(budgetId);
        if (originalBudget == null) {
            return false;
        }

        LocalDate newStartDate, newEndDate;

        if (originalBudget.getPeriod() == Budget.BudgetPeriod.MONTHLY) {
            newStartDate = originalBudget.getStartDate().plusMonths(1);
            newEndDate = originalBudget.getEndDate().plusMonths(1);
        } else {
            newStartDate = originalBudget.getStartDate().plusYears(1);
            newEndDate = originalBudget.getEndDate().plusYears(1);
        }

        Budget newBudget = new Budget(
                originalBudget.getCategoryId(),
                originalBudget.getAmount(),
                originalBudget.getPeriod(),
                newStartDate,
                newEndDate
        );

        return addBudget(newBudget) != null;
    }

    public void adjustBudgetBasedOnSpending(Long budgetId, double adjustmentFactor) throws SQLException {
        Budget budget = getBudgetById(budgetId);
        if (budget == null) {
            return;
        }

        // Calculate new budget amount based on current spending patterns
        BigDecimal currentSpent = budget.getSpent();
        BigDecimal suggestedAmount = currentSpent.multiply(BigDecimal.valueOf(adjustmentFactor));

        // Don't reduce budget below current spending
        if (suggestedAmount.compareTo(currentSpent) < 0) {
            suggestedAmount = currentSpent.multiply(BigDecimal.valueOf(1.1)); // 10% buffer
        }

        budget.setAmount(suggestedAmount);
        updateBudget(budget);
    }

    // Batch operations
    public void createBudgetsForAllCategories(BigDecimal defaultAmount, Budget.BudgetPeriod period,
                                              LocalDate startDate, LocalDate endDate) throws SQLException {
        // This would create budgets for all expense categories
        // Implementation would require CategoryService integration
        // For now, this is a placeholder for the interface
        throw new UnsupportedOperationException("Batch budget creation requires CategoryService integration");
    }

    // Private helper methods
    private void updateBudgetSpentAmount(Budget budget) throws SQLException {
        BigDecimal spent = transactionService.getTotalForCategory(
                budget.getCategoryId(),
                budget.getStartDate(),
                budget.getEndDate()
        );
        budget.setSpent(spent);
    }

    private Budget mapResultSetToBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getLong("id"));
        budget.setCategoryId(rs.getLong("category_id"));
        budget.setCategoryName(rs.getString("category_name"));
        budget.setAmount(rs.getBigDecimal("amount"));
        budget.setPeriod(Budget.BudgetPeriod.valueOf(rs.getString("period")));
        budget.setStartDate(rs.getDate("start_date").toLocalDate());
        budget.setEndDate(rs.getDate("end_date").toLocalDate());

        return budget;
    }
}
