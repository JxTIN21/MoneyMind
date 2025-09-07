package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Help dialog with user guide
 */
public class HelpDialog extends JDialog {

    public HelpDialog(JFrame parent) {
        super(parent, "User Guide", true);
        setupDialog();
        createContent();
    }

    private void setupDialog() {
        setSize(600, 500);
        setLocationRelativeTo(getParent());

        getContentPane().setBackground(ThemeManager.getBackgroundColor());
    }

    private void createContent() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("MoneyMind User Guide", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.getTitleFont());
        titleLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content
        String helpText = """
                Getting Started:
                1. Add categories to organize your transactions
                2. Record your income and expenses in the Transactions tab
                3. Set up budgets to track your spending goals
                4. View reports to analyze your financial patterns
                            
                Tips for Better Management:
                • Use descriptive transaction names
                • Set up monthly budgets for better tracking
                • Review reports regularly for insights
                • Use categories consistently for better organization
                • Export data regularly for backup
                            
                Keyboard Shortcuts:
                • Ctrl+I: Import transactions
                • Ctrl+E: Export transactions  
                • Ctrl+Q: Exit application
                • F1: Show this help
                • F5: Refresh all data
                • F11: Toggle full screen
                • Ctrl+D: Toggle dark mode
                            
                Navigation:
                • Use the toolbar for quick actions
                • Tab between different sections
                • Use the search box to find transactions
                • Right-click for context menus
                            
                Troubleshooting:
                • If data doesn't appear, try refreshing (F5)
                • Check database connection in status bar
                • Restart application if issues persist
                • Contact support for technical issues
                """;

        JTextArea textArea = new JTextArea(helpText);
        textArea.setFont(ThemeManager.getModernFont());
        textArea.setBackground(ThemeManager.getSurfaceColor());
        textArea.setForeground(ThemeManager.getTextColor());
        textArea.setEditable(false);
        textArea.setMargin(new Insets(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBackground(ThemeManager.getBackgroundColor());
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // Button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(ThemeManager.getBackgroundColor());

        JButton closeButton = new JButton("Close");
        ThemeManager.applyButtonStyling(closeButton, ThemeManager.ButtonStyle.PRIMARY);
        closeButton.addActionListener(e -> dispose());

        buttonPanel.add(closeButton);

        add(titleLabel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}

