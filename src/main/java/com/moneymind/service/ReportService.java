package main.java.com.moneymind.service;

import main.java.com.moneymind.model.Transaction;
import main.java.com.moneymind.model.Category;
import main.java.com.moneymind.model.Budget;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for generating financial reports and analytics
 */
public class ReportService {
    private TransactionService transactionService;
    private CategoryService categoryService;
    private BudgetService budgetService;

    public ReportService() {
        this.transactionService = new TransactionService();
        this.categoryService = new CategoryService();
        this.budgetService = new BudgetService();
    }

    // Summary Reports
    public FinancialSummary generateMonthlySummary(int year, int month) throws Exception {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return generateSummaryForPeriod(startDate, endDate, "Monthly");
    }

    public FinancialSummary generateYearlySummary(int year) throws Exception {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);

        return generateSummaryForPeriod(startDate, endDate, "Yearly");
    }

    public FinancialSummary generateCustomSummary(LocalDate startDate, LocalDate endDate) throws Exception {
        return generateSummaryForPeriod(startDate, endDate, "Custom Period");
    }

    private FinancialSummary generateSummaryForPeriod(LocalDate startDate, LocalDate endDate, String periodType) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

        BigDecimal totalIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = transactions.stream()
                .filter(Transaction::isExpense)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal netAmount = totalIncome.subtract(totalExpense);

        // Category breakdown
        Map<String, BigDecimal> categoryTotals = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryName,
                        Collectors.mapping(Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));

        // Transaction counts
        long incomeTransactionCount = transactions.stream().filter(Transaction::isIncome).count();
        long expenseTransactionCount = transactions.stream().filter(Transaction::isExpense).count();

        return new FinancialSummary(
                periodType, startDate, endDate,
                totalIncome, totalExpense, netAmount,
                categoryTotals, incomeTransactionCount, expenseTransactionCount,
                transactions.size()
        );
    }

    // Category Analysis
    public CategoryAnalysis generateCategoryAnalysis(LocalDate startDate, LocalDate endDate) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

        // Group by category
        Map<String, List<Transaction>> categoryGroups = transactions.stream()
                .collect(Collectors.groupingBy(Transaction::getCategoryName));

        List<CategoryData> categoryDataList = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map.Entry<String, List<Transaction>> entry : categoryGroups.entrySet()) {
            String categoryName = entry.getKey();
            List<Transaction> categoryTransactions = entry.getValue();

            BigDecimal categoryTotal = categoryTransactions.stream()
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalAmount = totalAmount.add(categoryTotal);

            Transaction.TransactionType type = categoryTransactions.get(0).getType();

            categoryDataList.add(new CategoryData(
                    categoryName, categoryTotal, categoryTransactions.size(), type
            ));
        }

        // Calculate percentages
        for (CategoryData data : categoryDataList) {
            if (totalAmount.compareTo(BigDecimal.ZERO) > 0) {
                double percentage = data.getAmount()
                        .divide(totalAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                data.setPercentage(percentage);
            }
        }

        // Sort by amount descending
        categoryDataList.sort((a, b) -> b.getAmount().compareTo(a.getAmount()));

        return new CategoryAnalysis(categoryDataList, totalAmount, startDate, endDate);
    }

    // Trend Analysis
    public TrendAnalysis generateTrendAnalysis(LocalDate startDate, LocalDate endDate) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

        // Group by month
        Map<YearMonth, List<Transaction>> monthlyGroups = transactions.stream()
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getTransactionDate())
                ));

        List<MonthlyData> monthlyDataList = new ArrayList<>();

        // Generate data for each month in the range
        YearMonth start = YearMonth.from(startDate);
        YearMonth end = YearMonth.from(endDate);

        for (YearMonth month = start; !month.isAfter(end); month = month.plusMonths(1)) {
            List<Transaction> monthTransactions = monthlyGroups.getOrDefault(month, new ArrayList<>());

            BigDecimal monthlyIncome = monthTransactions.stream()
                    .filter(Transaction::isIncome)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal monthlyExpense = monthTransactions.stream()
                    .filter(Transaction::isExpense)
                    .map(Transaction::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal netAmount = monthlyIncome.subtract(monthlyExpense);

            monthlyDataList.add(new MonthlyData(month, monthlyIncome, monthlyExpense, netAmount));
        }

        return new TrendAnalysis(monthlyDataList, startDate, endDate);
    }

    // Budget Performance Analysis
    public BudgetAnalysis generateBudgetAnalysis() throws Exception {
        List<Budget> activeBudgets = budgetService.getActiveBudgets();
        List<BudgetPerformance> performances = new ArrayList<>();

        BigDecimal totalBudgeted = BigDecimal.ZERO;
        BigDecimal totalSpent = BigDecimal.ZERO;
        int overBudgetCount = 0;

        for (Budget budget : activeBudgets) {
            totalBudgeted = totalBudgeted.add(budget.getAmount());
            totalSpent = totalSpent.add(budget.getSpent());

            if (budget.isOverBudget()) {
                overBudgetCount++;
            }

            BudgetPerformance performance = new BudgetPerformance(
                    budget.getCategoryName(),
                    budget.getAmount(),
                    budget.getSpent(),
                    budget.getRemaining(),
                    budget.getUsagePercentage(),
                    budget.getStatus(),
                    budget.isOverBudget()
            );

            performances.add(performance);
        }

        // Sort by usage percentage descending
        performances.sort((a, b) -> Double.compare(b.getUsagePercentage(), a.getUsagePercentage()));

        return new BudgetAnalysis(
                performances, totalBudgeted, totalSpent,
                totalBudgeted.subtract(totalSpent), overBudgetCount
        );
    }

    // Top Transactions Analysis
    public TopTransactionsReport generateTopTransactionsReport(LocalDate startDate, LocalDate endDate, int limit) throws Exception {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);

        // Top expenses
        List<Transaction> topExpenses = transactions.stream()
                .filter(Transaction::isExpense)
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .limit(limit)
                .collect(Collectors.toList());

        // Top income
        List<Transaction> topIncome = transactions.stream()
                .filter(Transaction::isIncome)
                .sorted((a, b) -> b.getAmount().compareTo(a.getAmount()))
                .limit(limit)
                .collect(Collectors.toList());

        // Most frequent categories
        Map<String, Long> categoryFrequency = transactions.stream()
                .collect(Collectors.groupingBy(
                        Transaction::getCategoryName,
                        Collectors.counting()
                ));

        List<Map.Entry<String, Long>> topCategories = categoryFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toList());

        return new TopTransactionsReport(topExpenses, topIncome, topCategories, startDate, endDate);
    }

    // Financial Health Score
    public FinancialHealthScore calculateFinancialHealth(LocalDate startDate, LocalDate endDate) throws Exception {
        FinancialSummary summary = generateSummaryForPeriod(startDate, endDate, "Health Analysis");
        BudgetAnalysis budgetAnalysis = generateBudgetAnalysis();

        int score = 0;
        List<String> factors = new ArrayList<>();

        // Income vs Expense ratio (40 points max)
        if (summary.getTotalIncome().compareTo(BigDecimal.ZERO) > 0) {
            double incomeExpenseRatio = summary.getTotalExpense()
                    .divide(summary.getTotalIncome(), 4, RoundingMode.HALF_UP)
                    .doubleValue();

            if (incomeExpenseRatio <= 0.5) {
                score += 40;
                factors.add("Excellent expense control (≤50% of income)");
            } else if (incomeExpenseRatio <= 0.7) {
                score += 30;
                factors.add("Good expense control (≤70% of income)");
            } else if (incomeExpenseRatio <= 0.9) {
                score += 20;
                factors.add("Moderate expense control (≤90% of income)");
            } else if (incomeExpenseRatio <= 1.0) {
                score += 10;
                factors.add("Living paycheck to paycheck");
            } else {
                factors.add("Spending exceeds income - immediate attention needed");
            }
        }

        // Budget adherence (30 points max)
        if (!budgetAnalysis.getBudgetPerformances().isEmpty()) {
            long budgetsOnTrack = budgetAnalysis.getBudgetPerformances().stream()
                    .filter(bp -> !bp.isOverBudget() && bp.getUsagePercentage() <= 90)
                    .count();

            double budgetAdherence = (double) budgetsOnTrack / budgetAnalysis.getBudgetPerformances().size();

            if (budgetAdherence >= 0.9) {
                score += 30;
                factors.add("Excellent budget adherence (90%+ on track)");
            } else if (budgetAdherence >= 0.7) {
                score += 20;
                factors.add("Good budget adherence (70%+ on track)");
            } else if (budgetAdherence >= 0.5) {
                score += 10;
                factors.add("Fair budget adherence (50%+ on track)");
            } else {
                factors.add("Poor budget adherence - review spending habits");
            }
        }

        // Savings rate (20 points max)
        if (summary.getTotalIncome().compareTo(BigDecimal.ZERO) > 0) {
            double savingsRate = summary.getNetAmount()
                    .divide(summary.getTotalIncome(), 4, RoundingMode.HALF_UP)
                    .doubleValue();

            if (savingsRate >= 0.2) {
                score += 20;
                factors.add("Excellent savings rate (≥20%)");
            } else if (savingsRate >= 0.1) {
                score += 15;
                factors.add("Good savings rate (≥10%)");
            } else if (savingsRate >= 0.05) {
                score += 10;
                factors.add("Fair savings rate (≥5%)");
            } else if (savingsRate > 0) {
                score += 5;
                factors.add("Low savings rate (<5%)");
            } else {
                factors.add("No savings - consider reducing expenses");
            }
        }

        // Transaction consistency (10 points max)
        if (summary.getTotalTransactionCount() >= 30) {
            score += 10;
            factors.add("Good transaction tracking consistency");
        } else if (summary.getTotalTransactionCount() >= 15) {
            score += 5;
            factors.add("Moderate transaction tracking");
        } else {
            factors.add("Consider tracking more transactions for better insights");
        }

        String healthLevel;
        String recommendation;

        if (score >= 80) {
            healthLevel = "Excellent";
            recommendation = "Your financial health is excellent! Keep up the good work and consider investment opportunities.";
        } else if (score >= 60) {
            healthLevel = "Good";
            recommendation = "Your financial health is good. Focus on areas for improvement to reach excellent status.";
        } else if (score >= 40) {
            healthLevel = "Fair";
            recommendation = "Your financial health needs attention. Review your spending and budget adherence.";
        } else {
            healthLevel = "Poor";
            recommendation = "Your financial health requires immediate attention. Consider consulting a financial advisor.";
        }

        return new FinancialHealthScore(score, healthLevel, recommendation, factors);
    }

    // Data classes for reports
    public static class FinancialSummary {
        private String periodType;
        private LocalDate startDate, endDate;
        private BigDecimal totalIncome, totalExpense, netAmount;
        private Map<String, BigDecimal> categoryTotals;
        private long incomeTransactionCount, expenseTransactionCount, totalTransactionCount;

        public FinancialSummary(String periodType, LocalDate startDate, LocalDate endDate,
                                BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netAmount,
                                Map<String, BigDecimal> categoryTotals,
                                long incomeTransactionCount, long expenseTransactionCount, long totalTransactionCount) {
            this.periodType = periodType;
            this.startDate = startDate;
            this.endDate = endDate;
            this.totalIncome = totalIncome;
            this.totalExpense = totalExpense;
            this.netAmount = netAmount;
            this.categoryTotals = categoryTotals;
            this.incomeTransactionCount = incomeTransactionCount;
            this.expenseTransactionCount = expenseTransactionCount;
            this.totalTransactionCount = totalTransactionCount;
        }

        // Getters
        public String getPeriodType() { return periodType; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
        public BigDecimal getTotalIncome() { return totalIncome; }
        public BigDecimal getTotalExpense() { return totalExpense; }
        public BigDecimal getNetAmount() { return netAmount; }
        public Map<String, BigDecimal> getCategoryTotals() { return categoryTotals; }
        public long getIncomeTransactionCount() { return incomeTransactionCount; }
        public long getExpenseTransactionCount() { return expenseTransactionCount; }
        public long getTotalTransactionCount() { return totalTransactionCount; }
    }

    public static class CategoryData {
        private String categoryName;
        private BigDecimal amount;
        private int transactionCount;
        private Transaction.TransactionType type;
        private double percentage;

        public CategoryData(String categoryName, BigDecimal amount, int transactionCount, Transaction.TransactionType type) {
            this.categoryName = categoryName;
            this.amount = amount;
            this.transactionCount = transactionCount;
            this.type = type;
        }

        // Getters and setters
        public String getCategoryName() { return categoryName; }
        public BigDecimal getAmount() { return amount; }
        public int getTransactionCount() { return transactionCount; }
        public Transaction.TransactionType getType() { return type; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public static class CategoryAnalysis {
        private List<CategoryData> categoryData;
        private BigDecimal totalAmount;
        private LocalDate startDate, endDate;

        public CategoryAnalysis(List<CategoryData> categoryData, BigDecimal totalAmount, LocalDate startDate, LocalDate endDate) {
            this.categoryData = categoryData;
            this.totalAmount = totalAmount;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public List<CategoryData> getCategoryData() { return categoryData; }
        public BigDecimal getTotalAmount() { return totalAmount; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
    }

    public static class MonthlyData {
        private YearMonth month;
        private BigDecimal income, expense, netAmount;

        public MonthlyData(YearMonth month, BigDecimal income, BigDecimal expense, BigDecimal netAmount) {
            this.month = month;
            this.income = income;
            this.expense = expense;
            this.netAmount = netAmount;
        }

        public YearMonth getMonth() { return month; }
        public BigDecimal getIncome() { return income; }
        public BigDecimal getExpense() { return expense; }
        public BigDecimal getNetAmount() { return netAmount; }
    }

    public static class TrendAnalysis {
        private List<MonthlyData> monthlyData;
        private LocalDate startDate, endDate;

        public TrendAnalysis(List<MonthlyData> monthlyData, LocalDate startDate, LocalDate endDate) {
            this.monthlyData = monthlyData;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public List<MonthlyData> getMonthlyData() { return monthlyData; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
    }

    public static class BudgetPerformance {
        private String categoryName;
        private BigDecimal budgetAmount, spentAmount, remainingAmount;
        private double usagePercentage;
        private String status;
        private boolean overBudget;

        public BudgetPerformance(String categoryName, BigDecimal budgetAmount, BigDecimal spentAmount,
                                 BigDecimal remainingAmount, double usagePercentage, String status, boolean overBudget) {
            this.categoryName = categoryName;
            this.budgetAmount = budgetAmount;
            this.spentAmount = spentAmount;
            this.remainingAmount = remainingAmount;
            this.usagePercentage = usagePercentage;
            this.status = status;
            this.overBudget = overBudget;
        }

        // Getters
        public String getCategoryName() { return categoryName; }
        public BigDecimal getBudgetAmount() { return budgetAmount; }
        public BigDecimal getSpentAmount() { return spentAmount; }
        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public double getUsagePercentage() { return usagePercentage; }
        public String getStatus() { return status; }
        public boolean isOverBudget() { return overBudget; }
    }

    public static class BudgetAnalysis {
        private List<BudgetPerformance> budgetPerformances;
        private BigDecimal totalBudgeted, totalSpent, totalRemaining;
        private int overBudgetCount;

        public BudgetAnalysis(List<BudgetPerformance> budgetPerformances, BigDecimal totalBudgeted,
                              BigDecimal totalSpent, BigDecimal totalRemaining, int overBudgetCount) {
            this.budgetPerformances = budgetPerformances;
            this.totalBudgeted = totalBudgeted;
            this.totalSpent = totalSpent;
            this.totalRemaining = totalRemaining;
            this.overBudgetCount = overBudgetCount;
        }

        // Getters
        public List<BudgetPerformance> getBudgetPerformances() { return budgetPerformances; }
        public BigDecimal getTotalBudgeted() { return totalBudgeted; }
        public BigDecimal getTotalSpent() { return totalSpent; }
        public BigDecimal getTotalRemaining() { return totalRemaining; }
        public int getOverBudgetCount() { return overBudgetCount; }
    }

    public static class TopTransactionsReport {
        private List<Transaction> topExpenses, topIncome;
        private List<Map.Entry<String, Long>> topCategories;
        private LocalDate startDate, endDate;

        public TopTransactionsReport(List<Transaction> topExpenses, List<Transaction> topIncome,
                                     List<Map.Entry<String, Long>> topCategories, LocalDate startDate, LocalDate endDate) {
            this.topExpenses = topExpenses;
            this.topIncome = topIncome;
            this.topCategories = topCategories;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        // Getters
        public List<Transaction> getTopExpenses() { return topExpenses; }
        public List<Transaction> getTopIncome() { return topIncome; }
        public List<Map.Entry<String, Long>> getTopCategories() { return topCategories; }
        public LocalDate getStartDate() { return startDate; }
        public LocalDate getEndDate() { return endDate; }
    }

    public static class FinancialHealthScore {
        private int score;
        private String healthLevel;
        private String recommendation;
        private List<String> factors;

        public FinancialHealthScore(int score, String healthLevel, String recommendation, List<String> factors) {
            this.score = score;
            this.healthLevel = healthLevel;
            this.recommendation = recommendation;
            this.factors = factors;
        }

        // Getters
        public int getScore() { return score; }
        public String getHealthLevel() { return healthLevel; }
        public String getRecommendation() { return recommendation; }
        public List<String> getFactors() { return factors; }
    }
}
