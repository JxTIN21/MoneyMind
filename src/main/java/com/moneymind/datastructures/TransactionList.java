package main.java.com.moneymind.datastructures;

import main.java.com.moneymind.model.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * Custom ArrayList implementation with enhanced search and filter capabilities
 * for Transaction management
 */

public class TransactionList {
    private List<Transaction> transactions;
    private Map<Long, Integer> indexMap; // For O(1) lookup by ID

    public TransactionList() {
        this.transactions = new ArrayList<>();
        this.indexMap = new HashMap<>();
    }

    public TransactionList(List<Transaction> transactions) {
        this();
        addAll(transactions);
    }

    // Basic operations
    public void add(Transaction transaction) {
        transactions.add(transaction);
        if (transaction.getId() != null) {
            indexMap.put(transaction.getId(), transactions.size() - 1);
        }
    }

    public void addAll(List<Transaction> transactionList) {
        for (Transaction transaction : transactionList) {
            add(transaction);
        }
    }

    public boolean remove(Transaction transaction) {
        int index = transactions.indexOf(transaction);
        if (index != -1) {
            transactions.remove(index);
            rebuildIndexMap();
            return true;
        }
        return false;
    }

    public Transaction get(int index) {
        return transactions.get(index);
    }

    public int size() {
        return transactions.size();
    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    public void clear() {
        transactions.clear();
        indexMap.clear();
    }

    // Search operations
    public Transaction findById(Long id) {
        Integer index = indexMap.get(id);
        return index != null ? transactions.get(index) : null;
    }

    public List<Transaction> findByCategory(Long categoryId) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getCategoryId().equals(categoryId)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> findByType(Transaction.TransactionType type) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (transaction.getType() == type) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            LocalDate transDate = transaction.getTransactionDate();
            if (!transDate.isBefore(startDate) && !transDate.isAfter(endDate)) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> findByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            BigDecimal amount = transaction.getAmount();
            if (amount.compareTo(minAmount) >= 0 && amount.compareTo(maxAmount) <= 0) {
                result.add(transaction);
            }
        }
        return result;
    }

    public List<Transaction> searchByDescription(String keyword) {
        List<Transaction> result = new ArrayList<>();
        String lowerKeyword = keyword.toLowerCase();
        for (Transaction transaction : transactions) {
            if (transaction.getDescription().toLowerCase().contains(lowerKeyword)) {
                result.add(transaction);
            }
        }
        return result;
    }

    // Advanced filtering with multiple criteria
    public List<Transaction> filter(TransactionFilter filter) {
        List<Transaction> result = new ArrayList<>();
        for (Transaction transaction : transactions) {
            if (filter.matches(transaction)) {
                result.add(transaction);
            }
        }
        return result;
    }

    // Statistics and aggregation
    public BigDecimal getTotalAmount() {
        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalIncome() {
        return transactions.stream()
                .filter(Transaction::isIncome)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpense() {
        return transactions.stream()
                .filter(Transaction::isExpense)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getNetAmount() {
        return getTotalIncome().subtract(getTotalExpense());
    }

    public Map<Long, BigDecimal> getAmountByCategory() {
        Map<Long, BigDecimal> categoryTotals = new HashMap<>();
        for (Transaction transaction : transactions) {
            Long categoryId = transaction.getCategoryId();
            categoryTotals.merge(categoryId, transaction.getAmount(), BigDecimal::add);
        }
        return categoryTotals;
    }

    public Map<LocalDate, BigDecimal> getDailyTotals() {
        Map<LocalDate, BigDecimal> dailyTotals = new TreeMap<>();
        for (Transaction transaction : transactions) {
            LocalDate date = transaction.getTransactionDate();
            dailyTotals.merge(date, transaction.getAmount(), BigDecimal::add);
        }
        return dailyTotals;
    }

    // Utility methods
    public List<Transaction> getAll() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> getRecent(int count) {
        List<Transaction> sorted = new ArrayList<>(transactions);
        sorted.sort(Comparator.comparing(Transaction::getTransactionDate).reversed());
        return sorted.subList(0, Math.min(count, sorted.size()));
    }

    public Transaction getLargestTransaction() {
        return transactions.stream()
                .max(Comparator.comparing(Transaction::getAmount))
                .orElse(null);
    }

    public Transaction getSmallestTransaction() {
        return transactions.stream()
                .min(Comparator.comparing(Transaction::getAmount))
                .orElse(null);
    }

    private void rebuildIndexMap() {
        indexMap.clear();
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            if (transaction.getId() != null) {
                indexMap.put(transaction.getId(), i);
            }
        }
    }

    // Inner class for complex filtering
    public static class TransactionFilter {
        private Transaction.TransactionType type;
        private Long categoryId;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal minAmount;
        private BigDecimal maxAmount;
        private String descriptionKeyword;

        public TransactionFilter() {}

        public TransactionFilter setType(Transaction.TransactionType type) {
            this.type = type;
            return this;
        }

        public TransactionFilter setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public TransactionFilter setDateRange(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            return this;
        }

        public TransactionFilter setAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
            this.minAmount = minAmount;
            this.maxAmount = maxAmount;
            return this;
        }

        public TransactionFilter setDescriptionKeyword(String keyword) {
            this.descriptionKeyword = keyword;
            return this;
        }

        public boolean matches(Transaction transaction) {
            if (type != null && transaction.getType() != type) {
                return false;
            }

            if (categoryId != null && !transaction.getCategoryId().equals(categoryId)) {
                return false;
            }

            if (startDate != null && transaction.getTransactionDate().isBefore(startDate)) {
                return false;
            }

            if (endDate != null && transaction.getTransactionDate().isAfter(endDate)) {
                return false;
            }

            if (minAmount != null && transaction.getAmount().compareTo(minAmount) < 0) {
                return false;
            }

            if (maxAmount != null && transaction.getAmount().compareTo(maxAmount) > 0) {
                return false;
            }

            if (descriptionKeyword != null && !transaction.getDescription()
                    .toLowerCase().contains(descriptionKeyword.toLowerCase())) {
                return false;
            }

            return true;
        }
    }
}