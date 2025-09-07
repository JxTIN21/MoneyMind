package main.java.com.moneymind.ui.components;

import main.java.com.moneymind.ui.theme.ThemeManager;
import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modern status bar with multiple information zones
 */
public class ModernStatusBar extends JPanel {
    private JLabel statusLabel;
    private JLabel activeTabLabel;
    private JLabel connectionLabel;
    private JLabel timeLabel;

    public ModernStatusBar() {
        setupModernStyling();
        createStatusComponents();
        startTimeUpdater();
    }

    private void setupModernStyling() {
        setLayout(new BorderLayout());
        setBackground(ThemeManager.getSurfaceColor());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeManager.getColor("border")),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        setPreferredSize(new Dimension(0, 30));
    }

    private void createStatusComponents() {
        // Left panel - Status and active tab
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(ThemeManager.getModernFont());
        statusLabel.setForeground(ThemeManager.getTextColor());

        activeTabLabel = new JLabel("| Transactions");
        activeTabLabel.setFont(ThemeManager.getModernFont());
        activeTabLabel.setForeground(ThemeManager.getSecondaryTextColor());
        activeTabLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        leftPanel.add(statusLabel);
        leftPanel.add(activeTabLabel);

        // Right panel - Connection and time
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        connectionLabel = new JLabel("● Database Connected");
        connectionLabel.setFont(ThemeManager.getModernFont());
        connectionLabel.setForeground(ThemeManager.getColor("success"));

        timeLabel = new JLabel();
        timeLabel.setFont(ThemeManager.getModernFont());
        timeLabel.setForeground(ThemeManager.getSecondaryTextColor());

        rightPanel.add(connectionLabel);
        rightPanel.add(timeLabel);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void startTimeUpdater() {
        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(LocalTime.now().format(
                    DateTimeFormatter.ofPattern("HH:mm:ss")));
        });
        timer.start();
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
        statusLabel.repaint();
    }

    public void updateActiveTab(String tabName) {
        activeTabLabel.setText("| " + tabName);
        activeTabLabel.repaint();
    }

    public void setConnectionStatus(boolean connected) {
        if (connected) {
            connectionLabel.setText("● Database Connected");
            connectionLabel.setForeground(ThemeManager.getColor("success"));
        } else {
            connectionLabel.setText("● Database Disconnected");
            connectionLabel.setForeground(ThemeManager.getColor("danger"));
        }
        connectionLabel.repaint();
    }
}