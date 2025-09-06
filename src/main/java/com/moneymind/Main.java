package main.java.com.moneymind;

import main.java.com.moneymind.database.DatabaseManager;
import main.java.com.moneymind.database.DatabaseInitializer;
import main.java.com.moneymind.ui.MainFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * MoneyMind - Personal Finance Tracker
 * Main application entry point
 */

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting MoneyMind - Personal Finance Tracker...");

        try {
            // Set Look and Feel to system default
           UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }

        // Initialize database
        initializeDatabase();

        // Start GUI application
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame frame = new MainFrame();
                frame.setVisible(true);
                System.out.println("MoneyMind application started successfully");
            } catch (Exception e) {
                System.err.println("Error strting application: " + e.getMessage());
            }
        });
    }

    private static void initializeDatabase() {
        try {
            DatabaseManager.getInstance().connect();
            DatabaseInitializer.initializeDatabase();
            System.out.println("Database initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
