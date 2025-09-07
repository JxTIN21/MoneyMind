package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * About dialog with modern design
 */
public class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {
        super(parent, "About MoneyMind", true);
        setupDialog();
        createContent();
    }

    private void setupDialog() {
        setSize(450, 350);
        setLocationRelativeTo(getParent());
        setResizable(false);

        getContentPane().setBackground(ThemeManager.getBackgroundColor());
    }

    private void createContent() {
        setLayout(new BorderLayout());

        // Header with icon
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ThemeManager.getPrimaryColor());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel iconLabel = new JLabel("💰", SwingConstants.CENTER);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 48));
        iconLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel("MoneyMind", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.getTitleFont());
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(iconLabel, BorderLayout.CENTER);
        headerPanel.add(titleLabel, BorderLayout.SOUTH);

        // Content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ThemeManager.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Version info
        JLabel versionLabel = new JLabel("Personal Finance Tracker", SwingConstants.CENTER);
        versionLabel.setFont(ThemeManager.getHeaderFont());
        versionLabel.setForeground(ThemeManager.getTextColor());
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionNumberLabel = new JLabel("Version 1.0", SwingConstants.CENTER);
        versionNumberLabel.setFont(ThemeManager.getModernFont());
        versionNumberLabel.setForeground(ThemeManager.getSecondaryTextColor());
        versionNumberLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(versionLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(versionNumberLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Description
        String descriptionText = "A comprehensive personal finance management\n" +
                "application built with Java and SQLite.";
        JTextArea descriptionArea = new JTextArea(descriptionText);
        descriptionArea.setFont(ThemeManager.getModernFont());
        descriptionArea.setBackground(ThemeManager.getBackgroundColor());
        descriptionArea.setForeground(ThemeManager.getTextColor());
        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        contentPanel.add(descriptionArea);
        contentPanel.add(Box.createVerticalStrut(20));

        // Features
        JLabel featuresLabel = new JLabel("Features:", SwingConstants.LEFT);
        featuresLabel.setFont(ThemeManager.getHeaderFont());
        featuresLabel.setForeground(ThemeManager.getTextColor());
        featuresLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String featuresText = "• Transaction Management\n" +
                "• Category Organization\n" +
                "• Budget Planning & Tracking\n" +
                "• Financial Reports & Analysis\n" +
                "• Data Import/Export\n" +
                "• Modern Dark/Light Themes";

        JTextArea featuresArea = new JTextArea(featuresText);
        featuresArea.setFont(ThemeManager.getModernFont());
        featuresArea.setBackground(ThemeManager.getBackgroundColor());
        featuresArea.setForeground(ThemeManager.getTextColor());
        featuresArea.setEditable(false);
        featuresArea.setOpaque(false);
        featuresArea.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(featuresLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(featuresArea);
        contentPanel.add(Box.createVerticalStrut(20));

        // Copyright
        JLabel copyrightLabel = new JLabel("© 2024 MoneyMind Project", SwingConstants.CENTER);
        copyrightLabel.setFont(new Font(ThemeManager.getModernFont().getName(), Font.ITALIC, 11));
        copyrightLabel.setForeground(ThemeManager.getSecondaryTextColor());
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(copyrightLabel);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(ThemeManager.getBackgroundColor());

        JButton closeButton = new JButton("Close");
        ThemeManager.applyButtonStyling(closeButton, ThemeManager.ButtonStyle.PRIMARY);
        closeButton.addActionListener(e -> dispose());

        JButton systemInfoButton = new JButton("System Info");
        ThemeManager.applyButtonStyling(systemInfoButton, ThemeManager.ButtonStyle.OUTLINE);
        systemInfoButton.addActionListener(e -> showSystemInfo());

        buttonPanel.add(systemInfoButton);
        buttonPanel.add(closeButton);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void showSystemInfo() {
        String systemInfo = "System Information:\n\n" +
                "Java Version: " + System.getProperty("java.version") + "\n" +
                "Java Vendor: " + System.getProperty("java.vendor") + "\n" +
                "Operating System: " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n" +
                "Architecture: " + System.getProperty("os.arch") + "\n" +
                "Available Processors: " + Runtime.getRuntime().availableProcessors() + "\n" +
                "Total Memory: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MB\n" +
                "Free Memory: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MB\n" +
                "Max Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MB";

        JTextArea systemInfoArea = new JTextArea(systemInfo);
        systemInfoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        systemInfoArea.setEditable(false);
        systemInfoArea.setBackground(ThemeManager.getSurfaceColor());
        systemInfoArea.setForeground(ThemeManager.getTextColor());
        systemInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(systemInfoArea);
        scrollPane.setPreferredSize(new Dimension(400, 250));

        JOptionPane.showMessageDialog(this, scrollPane, "System Information",
                JOptionPane.INFORMATION_MESSAGE);
    }
}