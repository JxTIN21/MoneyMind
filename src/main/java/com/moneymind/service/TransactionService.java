package main.java.com.moneymind.service;

import main.java.com.moneymind.database.DatabaseManager;
import main.java.com.moneymind.model.Transaction;
import main.java.com.moneymind.datastructures.TransactionList;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Transaction CRUD operations and business logic
 */
public class TransactionService {
    private DatabaseManager dbManager;

    public TransactionService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    // Create operations
    public Long addTransaction(Transaction transaction) throws SQLException {
        String sql = """
            INSERT INTO transactions (description, amount, transaction_date, category_id, type)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, transaction.getDescription());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setDate(3, Date.valueOf(transaction.getTransactionDate()));
            stmt.setLong(4, transaction.getCategoryId());
            stmt.setString(5, transaction.getType().name());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    Long id = generatedKeys.getLong(1);
                    transaction.setId(id);
                    return id;
                }
            }
            return null;
        }
    }

    public void addTransactions(List<Transaction> transactions) throws SQLException {
        String sql = """
            INSERT INTO transactions (description, amount, transaction_date, category_id, type)
            VALUES (?, ?, ?, ?, ?)
        """;

        try {
            dbManager.beginTransaction();
            try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
                for (Transaction transaction : transactions) {
                    stmt.setString(1, transaction.getDescription());
                    stmt.setBigDecimal(2, transaction.getAmount());
                    stmt.setDate(3, Date.valueOf(transaction.getTransactionDate()));
                    stmt.setLong(4, transaction.getCategoryId());
                    stmt.setString(5, transaction.getType().name());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
            dbManager.commitTransaction();
        } catch (SQLException e) {
            dbManager.rollbackTransaction();
            throw e;
        }
    }

    // Read operations
    public Transaction getTransactionById(Long id) throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.id = ?
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            return null;
        }
    }

    public List<Transaction> getAllTransactions() throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            ORDER BY t.transaction_date DESC, t.created_at DESC
        """;

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByCategory(Long categoryId) throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.category_id = ?
            ORDER BY t.transaction_date DESC
        """;

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.transaction_date BETWEEN ? AND ?
            ORDER BY t.transaction_date DESC
        """;

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.type = ?
            ORDER BY t.transaction_date DESC
        """;

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setString(1, type.name());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    public List<Transaction> searchTransactions(String keyword) throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE t.description LIKE ? OR c.name LIKE ?
            ORDER BY t.transaction_date DESC
        """;

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    public List<Transaction> getRecentTransactions(int limit) throws SQLException {
        String sql = """
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            ORDER BY t.created_at DESC
            LIMIT ?
        """;

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }
        return transactions;
    }

    // Update operations
    public boolean updateTransaction(Transaction transaction) throws SQLException {
        String sql = """
            UPDATE transactions 
            SET description = ?, amount = ?, transaction_date = ?, category_id = ?, type = ?
            WHERE id = ?
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setString(1, transaction.getDescription());
            stmt.setBigDecimal(2, transaction.getAmount());
            stmt.setDate(3, Date.valueOf(transaction.getTransactionDate()));
            stmt.setLong(4, transaction.getCategoryId());
            stmt.setString(5, transaction.getType().name());
            stmt.setLong(6, transaction.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    // Delete operations
    public boolean deleteTransaction(Long id) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    public int deleteTransactionsByCategory(Long categoryId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE category_id = ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            return stmt.executeUpdate();
        }
    }

    public int deleteTransactionsOlderThan(LocalDate date) throws SQLException {
        String sql = "DELETE FROM transactions WHERE transaction_date < ?";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(date));
            return stmt.executeUpdate();
        }
    }

    // Statistics and aggregation methods
    public BigDecimal getTotalIncome() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'INCOME'";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalExpense() throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'EXPENSE'";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getNetAmount() throws SQLException {
        return getTotalIncome().subtract(getTotalExpense());
    }

    public BigDecimal getTotalForPeriod(LocalDate startDate, LocalDate endDate, Transaction.TransactionType type) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(amount), 0) 
            FROM transactions 
            WHERE transaction_date BETWEEN ? AND ? AND type = ?
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setString(3, type.name());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getTotalForCategory(Long categoryId, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(amount), 0) 
            FROM transactions 
            WHERE category_id = ? AND transaction_date BETWEEN ? AND ?
        """;

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            stmt.setLong(1, categoryId);
            stmt.setDate(2, Date.valueOf(startDate));
            stmt.setDate(3, Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        }
    }

    public int getTransactionCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions";

        try (PreparedStatement stmt = dbManager.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    // Advanced filtering with TransactionList
    public TransactionList getTransactionList() throws SQLException {
        return new TransactionList(getAllTransactions());
    }

    public TransactionList getFilteredTransactionList(LocalDate startDate, LocalDate endDate,
                                                      Transaction.TransactionType type, Long categoryId) throws SQLException {
        StringBuilder sql = new StringBuilder("""
            SELECT t.*, c.name as category_name 
            FROM transactions t
            JOIN categories c ON t.category_id = c.id
            WHERE 1=1
        """);

        List<Object> parameters = new ArrayList<>();

        if (startDate != null) {
            sql.append(" AND t.transaction_date >= ?");
            parameters.add(Date.valueOf(startDate));
        }

        if (endDate != null) {
            sql.append(" AND t.transaction_date <= ?");
            parameters.add(Date.valueOf(endDate));
        }

        if (type != null) {
            sql.append(" AND t.type = ?");
            parameters.add(type.name());
        }

        if (categoryId != null) {
            sql.append(" AND t.category_id = ?");
            parameters.add(categoryId);
        }

        sql.append(" ORDER BY t.transaction_date DESC");

        List<Transaction> transactions = new ArrayList<>();
        try (PreparedStatement stmt = dbManager.prepareStatement(sql.toString())) {
            for (int i = 0; i < parameters.size(); i++) {
                Object param = parameters.get(i);
                if (param instanceof Date) {
                    stmt.setDate(i + 1, (Date) param);
                } else if (param instanceof String) {
                    stmt.setString(i + 1, (String) param);
                } else if (param instanceof Long) {
                    stmt.setLong(i + 1, (Long) param);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
        }

        return new TransactionList(transactions);
    }

    // Helper method to map ResultSet to Transaction object
    private Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getLong("id"));
        transaction.setDescription(rs.getString("description"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setTransactionDate(rs.getDate("transaction_date").toLocalDate());
        transaction.setCategoryId(rs.getLong("category_id"));
        transaction.setCategoryName(rs.getString("category_name"));
        transaction.setType(Transaction.TransactionType.valueOf(rs.getString("type")));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            transaction.setCreatedAt(createdAt.toLocalDateTime());
        }

        return transaction;
    }
}

