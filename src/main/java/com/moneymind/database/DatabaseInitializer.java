package main.java.com.moneymind.database;

import java.sql.*;

/**
 * Database schema initialization and sample data setup
 */
public class DatabaseInitializer {

    public static void initializeDatabase() throws SQLException {
        createTables();
        insertSampleCategories();
        System.out.println("Database initialization completed.");
    }

    private static void createTables() throws SQLException {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Create categories table
        String createCategoriesTable = """
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name VARCHAR(100) NOT NULL,
                parent_id INTEGER,
                type VARCHAR(20) CHECK(type IN ('INCOME', 'EXPENSE')) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE CASCADE
            )
        """;

        // Create transactions table
        String createTransactionsTable = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                description VARCHAR(255) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                transaction_date DATE NOT NULL,
                category_id INTEGER NOT NULL,
                type VARCHAR(20) CHECK(type IN ('INCOME', 'EXPENSE')) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT
            )
        """;

        // Create budgets table
        String createBudgetsTable = """
            CREATE TABLE IF NOT EXISTS budgets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                category_id INTEGER NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                period VARCHAR(20) CHECK(period IN ('MONTHLY', 'YEARLY')) NOT NULL,
                start_date DATE NOT NULL,
                end_date DATE NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
            )
        """;

        // Create indexes for better performance
        String createIndexes = """
            CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(transaction_date);
            CREATE INDEX IF NOT EXISTS idx_transactions_category ON transactions(category_id);
            CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
            CREATE INDEX IF NOT EXISTS idx_categories_parent ON categories(parent_id);
            CREATE INDEX IF NOT EXISTS idx_budgets_category ON budgets(category_id);
            CREATE INDEX IF NOT EXISTS idx_budgets_period ON budgets(start_date, end_date);
        """;

        try {
            dbManager.executeUpdate(createCategoriesTable);
            System.out.println("Categories table created/verified.");

            dbManager.executeUpdate(createTransactionsTable);
            System.out.println("Transactions table created/verified.");

            dbManager.executeUpdate(createBudgetsTable);
            System.out.println("Budgets table created/verified.");

            // Create indexes
            String[] indexes = createIndexes.split(";");
            for (String index : indexes) {
                if (!index.trim().isEmpty()) {
                    dbManager.executeUpdate(index.trim());
                }
            }
            System.out.println("Database indexes created/verified.");

        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            throw e;
        }
    }

    private static void insertSampleCategories() throws SQLException {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Check if categories already exist
        String checkSql = "SELECT COUNT(*) FROM categories";
        try (PreparedStatement stmt = dbManager.prepareStatement(checkSql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Categories already exist, skipping sample data insertion.");
                return;
            }
        }

        // Insert sample categories
        String insertSql = "INSERT INTO categories (name, parent_id, type) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = dbManager.prepareStatement(insertSql)) {
            dbManager.beginTransaction();

            // Income categories
            insertCategory(stmt, "Salary", null, "INCOME");
            insertCategory(stmt, "Freelance", null, "INCOME");
            insertCategory(stmt, "Investments", null, "INCOME");
            insertCategory(stmt, "Business Income", null, "INCOME");
            insertCategory(stmt, "Other Income", null, "INCOME");

            dbManager.commitTransaction();

            // Get parent IDs for subcategories
            dbManager.beginTransaction();

            // Expense categories with subcategories
            Long foodId = insertCategory(stmt, "Food & Dining", null, "EXPENSE");
            insertCategory(stmt, "Restaurants", foodId, "EXPENSE");
            insertCategory(stmt, "Groceries", foodId, "EXPENSE");
            insertCategory(stmt, "Fast Food", foodId, "EXPENSE");

            Long transportId = insertCategory(stmt, "Transportation", null, "EXPENSE");
            insertCategory(stmt, "Gas & Fuel", transportId, "EXPENSE");
            insertCategory(stmt, "Public Transport", transportId, "EXPENSE");
            insertCategory(stmt, "Parking", transportId, "EXPENSE");
            insertCategory(stmt, "Car Maintenance", transportId, "EXPENSE");

            Long shoppingId = insertCategory(stmt, "Shopping", null, "EXPENSE");
            insertCategory(stmt, "Clothing", shoppingId, "EXPENSE");
            insertCategory(stmt, "Electronics", shoppingId, "EXPENSE");
            insertCategory(stmt, "General Merchandise", shoppingId, "EXPENSE");

            Long billsId = insertCategory(stmt, "Bills & Utilities", null, "EXPENSE");
            insertCategory(stmt, "Electricity", billsId, "EXPENSE");
            insertCategory(stmt, "Water", billsId, "EXPENSE");
            insertCategory(stmt, "Internet", billsId, "EXPENSE");
            insertCategory(stmt, "Phone", billsId, "EXPENSE");
            insertCategory(stmt, "Insurance", billsId, "EXPENSE");

            Long entertainmentId = insertCategory(stmt, "Entertainment", null, "EXPENSE");
            insertCategory(stmt, "Movies", entertainmentId, "EXPENSE");
            insertCategory(stmt, "Games", entertainmentId, "EXPENSE");
            insertCategory(stmt, "Music", entertainmentId, "EXPENSE");
            insertCategory(stmt, "Sports", entertainmentId, "EXPENSE");

            Long healthId = insertCategory(stmt, "Health & Fitness", null, "EXPENSE");
            insertCategory(stmt, "Doctor", healthId, "EXPENSE");
            insertCategory(stmt, "Pharmacy", healthId, "EXPENSE");
            insertCategory(stmt, "Gym", healthId, "EXPENSE");

            Long travelId = insertCategory(stmt, "Travel", null, "EXPENSE");
            insertCategory(stmt, "Hotels", travelId, "EXPENSE");
            insertCategory(stmt, "Flights", travelId, "EXPENSE");
            insertCategory(stmt, "Vacation", travelId, "EXPENSE");

            insertCategory(stmt, "Education", null, "EXPENSE");
            insertCategory(stmt, "Personal Care", null, "EXPENSE");
            insertCategory(stmt, "Gifts & Donations", null, "EXPENSE");
            insertCategory(stmt, "Other Expenses", null, "EXPENSE");

            dbManager.commitTransaction();
            System.out.println("Sample categories inserted successfully.");

        } catch (SQLException e) {
            dbManager.rollbackTransaction();
            System.err.println("Error inserting sample categories: " + e.getMessage());
            throw e;
        }
    }

    private static Long insertCategory(PreparedStatement stmt, String name, Long parentId, String type)
            throws SQLException {
        stmt.setString(1, name);
        if (parentId != null) {
            stmt.setLong(2, parentId);
        } else {
            stmt.setNull(2, Types.INTEGER);
        }
        stmt.setString(3, type);
        stmt.executeUpdate();

        // Get the generated ID
        ResultSet generatedKeys = stmt.getGeneratedKeys();
        if (generatedKeys.next()) {
            return generatedKeys.getLong(1);
        }
        return null;
    }

    public static void resetDatabase() throws SQLException {
        DatabaseManager dbManager = DatabaseManager.getInstance();

        try {
            dbManager.beginTransaction();

            // Drop tables in reverse order due to foreign keys
            dbManager.executeUpdate("DROP TABLE IF EXISTS budgets");
            dbManager.executeUpdate("DROP TABLE IF EXISTS transactions");
            dbManager.executeUpdate("DROP TABLE IF EXISTS categories");

            dbManager.commitTransaction();

            // Recreate everything
            initializeDatabase();

            System.out.println("Database reset completed.");

        } catch (SQLException e) {
            dbManager.rollbackTransaction();
            System.err.println("Error resetting database: " + e.getMessage());
            throw e;
        }
    }
}
