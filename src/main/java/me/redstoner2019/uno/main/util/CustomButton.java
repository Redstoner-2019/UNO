package me.redstoner2019.uno.main.util;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

public class CustomButton extends JComponent {
    private int width;
    private int height;
    private Color color;

    public CustomButton(int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;

        setPreferredSize(new Dimension(width, height));
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Handle button press here
            }
        });

        setBackground(color);
    }

    @Override
    protected void paintComponent(Graphics gg) {
        super.paintComponent(gg);
        Graphics2D g = (Graphics2D) gg;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setStroke(new BasicStroke(2));
        g.setColor(Color.DARK_GRAY);
        g.drawRoundRect(0, 0, width - 1, height - 1, 10, 10);

        g.setColor(color.darker());
        g.fillRoundRect(1, 1, width - 3, height - 3, 10, 10);

        g.setColor(color);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics metrics = g.getFontMetrics();
        String text = "Button";
        int x = (width - metrics.stringWidth(text)) / 2;
        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();
        g.drawString(text, x, y);
    }
}
