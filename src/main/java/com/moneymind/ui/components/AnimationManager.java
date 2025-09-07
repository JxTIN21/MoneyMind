package main.java.com.moneymind.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Animation manager for smooth UI transitions
 */
public class AnimationManager {

    /**
     * Creates a fade in effect for panels
     */
    public static void fadeInPanel(Runnable onComplete) {
        SwingUtilities.invokeLater(() -> {
            // Simple implementation - can be enhanced with actual fade effects
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    /**
     * Creates a fade out effect for windows
     */
    public static void fadeOutWindow(Window window, Runnable onComplete) {
        // Create fade out effect
        Timer fadeTimer = new Timer(50, null);
        final float[] opacity = {1.0f};

        fadeTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity[0] -= 0.1f;
                if (opacity[0] <= 0) {
                    fadeTimer.stop();
                    if (onComplete != null) {
                        onComplete.run();
                    }
                } else {
                    // Check if transparency is supported
                    if (window.getGraphicsConfiguration().getDevice().isWindowTranslucencySupported(
                            GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
                        window.setOpacity(Math.max(0, opacity[0]));
                    }
                }
            }
        });

        // Check if transparency is supported
        if (window.getGraphicsConfiguration().getDevice().isWindowTranslucencySupported(
                GraphicsDevice.WindowTranslucency.TRANSLUCENT)) {
            window.setOpacity(1.0f);
            fadeTimer.start();
        } else {
            // Fallback - just run completion
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }

    /**
     * Creates a slide in effect for panels
     */
    public static void slideInPanel(JPanel panel, int direction) {
        // Implementation for slide animations
        panel.setVisible(true);
        panel.revalidate();
        panel.repaint();
    }

    /**
     * Creates a bounce effect for buttons
     */
    public static void bounceButton(JButton button) {
        Timer bounceTimer = new Timer(50, null);
        final int[] step = {0};
        final Dimension originalSize = button.getSize();

        bounceTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step[0]++;
                if (step[0] <= 5) {
                    // Grow phase
                    int growth = step[0] * 2;
                    button.setSize(originalSize.width + growth, originalSize.height + growth);
                } else if (step[0] <= 10) {
                    // Shrink phase
                    int shrink = (10 - step[0]) * 2;
                    button.setSize(originalSize.width + shrink, originalSize.height + shrink);
                } else {
                    // Reset and stop
                    button.setSize(originalSize);
                    bounceTimer.stop();
                }
                button.revalidate();
                button.repaint();
            }
        });

        bounceTimer.start();
    }

    /**
     * Creates a smooth color transition
     */
    public static void colorTransition(JComponent component, Color fromColor, Color toColor, int duration) {
        Timer colorTimer = new Timer(20, null);
        final int[] step = {0};
        final int totalSteps = duration / 20;

        colorTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step[0]++;
                if (step[0] <= totalSteps) {
                    float ratio = (float) step[0] / totalSteps;

                    int red = (int) (fromColor.getRed() + (toColor.getRed() - fromColor.getRed()) * ratio);
                    int green = (int) (fromColor.getGreen() + (toColor.getGreen() - fromColor.getGreen()) * ratio);
                    int blue = (int) (fromColor.getBlue() + (toColor.getBlue() - fromColor.getBlue()) * ratio);

                    Color currentColor = new Color(red, green, blue);
                    component.setBackground(currentColor);
                    component.repaint();
                } else {
                    component.setBackground(toColor);
                    component.repaint();
                    colorTimer.stop();
                }
            }
        });

        colorTimer.start();
    }

    /**
     * Creates a pulsing effect for components
     */
    public static void pulseComponent(JComponent component, int duration) {
        Timer pulseTimer = new Timer(50, null);
        final int[] step = {0};
        final int totalSteps = duration / 50;
        final Color originalColor = component.getBackground();

        pulseTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step[0]++;
                if (step[0] <= totalSteps) {
                    // Calculate pulse intensity using sine wave
                    double intensity = Math.sin((double) step[0] / totalSteps * Math.PI * 4) * 0.3 + 0.7;

                    int red = (int) (originalColor.getRed() * intensity);
                    int green = (int) (originalColor.getGreen() * intensity);
                    int blue = (int) (originalColor.getBlue() * intensity);

                    Color pulseColor = new Color(
                            Math.min(255, Math.max(0, red)),
                            Math.min(255, Math.max(0, green)),
                            Math.min(255, Math.max(0, blue))
                    );

                    component.setBackground(pulseColor);
                    component.repaint();
                } else {
                    component.setBackground(originalColor);
                    component.repaint();
                    pulseTimer.stop();
                }
            }
        });

        pulseTimer.start();
    }

    // Animation direction constants
    public static final int SLIDE_LEFT = 1;
    public static final int SLIDE_RIGHT = 2;
    public static final int SLIDE_UP = 3;
    public static final int SLIDE_DOWN = 4;
}