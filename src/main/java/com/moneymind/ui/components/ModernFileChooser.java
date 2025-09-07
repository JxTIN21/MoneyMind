package main.java.com.moneymind.ui.components;

import javax.swing.*;
import java.awt.*;

/**
 * Modern file chooser with enhanced UI
 */
public class ModernFileChooser {
    private JFileChooser fileChooser;

    public ModernFileChooser() {
        fileChooser = new JFileChooser();
        setupModernStyling();
    }

    private void setupModernStyling() {
        // Apply modern theme to file chooser
        SwingUtilities.updateComponentTreeUI(fileChooser);
    }

    public void setFileFilter(String description, String extension) {
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                description, extension));
    }

    public int showOpenDialog(Component parent) {
        return fileChooser.showOpenDialog(parent);
    }

    public int showSaveDialog(Component parent) {
        return fileChooser.showSaveDialog(parent);
    }

    public java.io.File getSelectedFile() {
        return fileChooser.getSelectedFile();
    }

    public void setSelectedFile(java.io.File file) {
        fileChooser.setSelectedFile(file);
    }

    public void setCurrentDirectory(java.io.File dir) {
        fileChooser.setCurrentDirectory(dir);
    }

    // Constants for compatibility
    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
    public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    public static final int ERROR_OPTION = JFileChooser.ERROR_OPTION;
}