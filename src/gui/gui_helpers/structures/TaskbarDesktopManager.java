package gui.gui_helpers.structures;

import java.awt.Component;

import javax.swing.DefaultDesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class TaskbarDesktopManager extends DefaultDesktopManager {

    private static final int ICON_SPACING = 5;
    private static final int ICON_Y = 0; // bottom of desktop

    public void repositionIcons(JDesktopPane desktop) {
        if (desktop == null) return;

        Component[] comps = desktop.getComponents();
        int x = ICON_SPACING;

        for (Component c : comps) {
            if (c instanceof JInternalFrame.JDesktopIcon) {
                JInternalFrame frame = ((JInternalFrame.JDesktopIcon) c).getInternalFrame();
                if (frame.isIcon() && frame.isVisible()) {
                	int iconHeight = c.getHeight();
                    int y = desktop.getHeight() - iconHeight - ICON_SPACING;
                    c.setBounds(x, y, c.getWidth(), c.getHeight());
                    x += c.getWidth() + ICON_SPACING;
                }
            }
        }
    }

    @Override
    public void iconifyFrame(JInternalFrame f) {
        super.iconifyFrame(f);
        repositionIcons(f.getDesktopPane());
    }

    @Override
    public void deiconifyFrame(JInternalFrame f) {
        super.deiconifyFrame(f);
        repositionIcons(f.getDesktopPane());
    }
}
