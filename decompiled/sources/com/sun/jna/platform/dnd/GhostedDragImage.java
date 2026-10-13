package com.sun.jna.platform.dnd;

import com.sun.jna.platform.WindowUtils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Area;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/dnd/GhostedDragImage.class */
public class GhostedDragImage {
    private static final float DEFAULT_ALPHA = 0.5f;
    private Window dragImage;
    private Point origin;
    private static final int SLIDE_INTERVAL = 33;

    public GhostedDragImage(Component dragSource, final Icon icon, Point initialScreenLoc, final Point cursorOffset) {
        Window parent = dragSource instanceof Window ? (Window) dragSource : SwingUtilities.getWindowAncestor(dragSource);
        GraphicsConfiguration gc = parent.getGraphicsConfiguration();
        this.dragImage = new Window(JOptionPane.getRootFrame(), gc) { // from class: com.sun.jna.platform.dnd.GhostedDragImage.1
            private static final long serialVersionUID = 1;

            public void paint(Graphics g) {
                icon.paintIcon(this, g, 0, 0);
            }

            public Dimension getPreferredSize() {
                return new Dimension(icon.getIconWidth(), icon.getIconHeight());
            }

            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        this.dragImage.setFocusableWindowState(false);
        this.dragImage.setName("###overrideRedirect###");
        Icon dragIcon = new Icon() { // from class: com.sun.jna.platform.dnd.GhostedDragImage.2
            public int getIconHeight() {
                return icon.getIconHeight();
            }

            public int getIconWidth() {
                return icon.getIconWidth();
            }

            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics g2 = g.create();
                Area area = new Area(new Rectangle(x, y, getIconWidth(), getIconHeight()));
                area.subtract(new Area(new Rectangle((x + cursorOffset.x) - 1, (y + cursorOffset.y) - 1, 3, 3)));
                g2.setClip(area);
                icon.paintIcon(c, g2, x, y);
                g2.dispose();
            }
        };
        this.dragImage.pack();
        WindowUtils.setWindowMask(this.dragImage, dragIcon);
        WindowUtils.setWindowAlpha(this.dragImage, 0.5f);
        move(initialScreenLoc);
        this.dragImage.setVisible(true);
    }

    public void setAlpha(float alpha) {
        WindowUtils.setWindowAlpha(this.dragImage, alpha);
    }

    public void dispose() {
        this.dragImage.dispose();
        this.dragImage = null;
    }

    public void move(Point screenLocation) {
        if (this.origin == null) {
            this.origin = screenLocation;
        }
        this.dragImage.setLocation(screenLocation.x, screenLocation.y);
    }

    public void returnToOrigin() {
        final Timer timer = new Timer(33, (ActionListener) null);
        timer.addActionListener(new ActionListener() { // from class: com.sun.jna.platform.dnd.GhostedDragImage.3
            public void actionPerformed(ActionEvent e) {
                Point location = GhostedDragImage.this.dragImage.getLocationOnScreen();
                Point dst = new Point(GhostedDragImage.this.origin);
                int dx = (dst.x - location.x) / 2;
                int dy = (dst.y - location.y) / 2;
                if (dx != 0 || dy != 0) {
                    location.translate(dx, dy);
                    GhostedDragImage.this.move(location);
                } else {
                    timer.stop();
                    GhostedDragImage.this.dispose();
                }
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }
}
