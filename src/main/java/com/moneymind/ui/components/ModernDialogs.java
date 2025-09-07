package main.java.com.moneymind.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Modern dialog utilities
 */
public class ModernDialogs {

    public static void showInfoDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static int showConfirmDialog(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
    }

    public static void showErrorDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarningDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static String showInputDialog(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent, message, title, JOptionPane.QUESTION_MESSAGE);
    }

    public static String showInputDialog(Component parent, String message, String title, String initialValue) {
        return (String) JOptionPane.showInputDialog(parent, message, title,
                JOptionPane.QUESTION_MESSAGE, null, null, initialValue);
    }
}