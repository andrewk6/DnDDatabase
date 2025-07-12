package gui.gui_helpers;

import javax.swing.*;

import gui.gui_helpers.structures.StyleContainer;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class CustomDesktopIcon extends JInternalFrame.JDesktopIcon {

    public CustomDesktopIcon(JInternalFrame frame, BufferedImage image) {
    	Image scaledImage = image.getScaledInstance(StyleContainer.ICON_SIZE, 
    			StyleContainer.ICON_SIZE, BufferedImage.SCALE_SMOOTH);
        this(frame, new ImageIcon(scaledImage));
    }

    public CustomDesktopIcon(JInternalFrame frame, ImageIcon icon) {
        super(frame);

        setPreferredSize(new Dimension(StyleContainer.ICON_SIZE, StyleContainer.ICON_SIZE));
        setLayout(new BorderLayout());

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalAlignment(JLabel.CENTER);

        removeAll();
        add(iconLabel, BorderLayout.CENTER);

        iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        iconLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    frame.setIcon(false);       // Restore the internal frame
                    frame.setSelected(true);    // Optionally select it
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        revalidate();
        repaint();
    }
}