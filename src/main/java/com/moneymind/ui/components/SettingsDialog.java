package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Settings dialog with modern UI
 */
public class SettingsDialog extends JDialog {

    public SettingsDialog(JFrame parent) {
        super(parent, "Settings", true);
        setupDialog();
        createContent();
    }

    private void setupDialog() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);

        // Apply modern styling
        getContentPane().setBackground(ThemeManager.getBackgroundColor());
    }

    private void createContent() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Settings", SwingConstants.CENTER);
        titleLabel.setFont(ThemeManager.getTitleFont());
        titleLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(ThemeManager.getBackgroundColor());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Theme setting
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel themeLabel = new JLabel("Theme:");
        themeLabel.setFont(ThemeManager.getModernFont());
        themeLabel.setForeground(ThemeManager.getTextColor());
        contentPanel.add(themeLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> themeCombo = new JComboBox<>(new String[]{"Light", "Dark"});
        themeCombo.setSelectedIndex(ThemeManager.isDarkMode() ? 1 : 0);
        themeCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (themeCombo.getSelectedIndex() == 1 && !ThemeManager.isDarkMode()) {
                    ThemeManager.toggleDarkMode();
                    SwingUtilities.updateComponentTreeUI(getParent());
                    SwingUtilities.updateComponentTreeUI(SettingsDialog.this);
                } else if (themeCombo.getSelectedIndex() == 0 && ThemeManager.isDarkMode()) {
                    ThemeManager.toggleDarkMode();
                    SwingUtilities.updateComponentTreeUI(getParent());
                    SwingUtilities.updateComponentTreeUI(SettingsDialog.this);
                }
            }
        });
        contentPanel.add(themeCombo, gbc);

        // Currency setting
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel currencyLabel = new JLabel("Currency:");
        currencyLabel.setFont(ThemeManager.getModernFont());
        currencyLabel.setForeground(ThemeManager.getTextColor());
        contentPanel.add(currencyLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> currencyCombo = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "CAD", "AUD"});
        contentPanel.add(currencyCombo, gbc);

        // Date format setting
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dateLabel = new JLabel("Date Format:");
        dateLabel.setFont(ThemeManager.getModernFont());
        dateLabel.setForeground(ThemeManager.getTextColor());
        contentPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> dateCombo = new JComboBox<>(new String[]{"MM/DD/YYYY", "DD/MM/YYYY", "YYYY-MM-DD"});
        contentPanel.add(dateCombo, gbc);

        // Language setting
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel languageLabel = new JLabel("Language:");
        languageLabel.setFont(ThemeManager.getModernFont());
        languageLabel.setForeground(ThemeManager.getTextColor());
        contentPanel.add(languageLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> languageCombo = new JComboBox<>(new String[]{"English", "Spanish", "French", "German"});
        contentPanel.add(languageCombo, gbc);

        // Auto-save setting
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JCheckBox autoSaveBox = new JCheckBox("Enable auto-save");
        autoSaveBox.setFont(ThemeManager.getModernFont());
        autoSaveBox.setForeground(ThemeManager.getTextColor());
        autoSaveBox.setBackground(ThemeManager.getBackgroundColor());
        autoSaveBox.setSelected(true);
        contentPanel.add(autoSaveBox, gbc);

        // Backup reminder setting
        gbc.gridy = 5;
        JCheckBox backupReminderBox = new JCheckBox("Show backup reminders");
        backupReminderBox.setFont(ThemeManager.getModernFont());
        backupReminderBox.setForeground(ThemeManager.getTextColor());
        backupReminderBox.setBackground(ThemeManager.getBackgroundColor());
        backupReminderBox.setSelected(true);
        contentPanel.add(backupReminderBox, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(ThemeManager.getBackgroundColor());

        JButton okButton = new JButton("OK");
        ThemeManager.applyButtonStyling(okButton, ThemeManager.ButtonStyle.PRIMARY);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Save settings logic would go here
                ModernDialogs.showInfoDialog(SettingsDialog.this,
                        "Settings saved successfully!", "Settings");
                dispose();
            }
        });

        JButton cancelButton = new JButton("Cancel");
        ThemeManager.applyButtonStyling(cancelButton, ThemeManager.ButtonStyle.SECONDARY);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        JButton resetButton = new JButton("Reset to Defaults");
        ThemeManager.applyButtonStyling(resetButton, ThemeManager.ButtonStyle.OUTLINE);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = ModernDialogs.showConfirmDialog(SettingsDialog.this,
                        "Are you sure you want to reset all settings to defaults?",
                        "Reset Settings");
                if (result == JOptionPane.YES_OPTION) {
                    // Reset logic would go here
                    themeCombo.setSelectedIndex(0);
                    currencyCombo.setSelectedIndex(0);
                    dateCombo.setSelectedIndex(0);
                    languageCombo.setSelectedIndex(0);
                    autoSaveBox.setSelected(true);
                    backupReminderBox.setSelected(true);
                }
            }
        });

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(resetButton);

        add(titleLabel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}