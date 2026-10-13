package com.sun.jna.platform;

import com.aowagie.text.pdf.ColumnText;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.PsapiUtil;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.awt.AWTEvent;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils.class */
public class WindowUtils {
    private static final String TRANSPARENT_OLD_BG = "transparent-old-bg";
    private static final String TRANSPARENT_OLD_OPAQUE = "transparent-old-opaque";
    private static final String TRANSPARENT_ALPHA = "transparent-alpha";
    private static final Logger LOG = Logger.getLogger(WindowUtils.class.getName());
    public static final Shape MASK_NONE = null;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$HeavyweightForcer.class */
    private static class HeavyweightForcer extends Window {
        private static final long serialVersionUID = 1;
        private final boolean packed;

        public HeavyweightForcer(Window parent) {
            super(parent);
            pack();
            this.packed = true;
        }

        public boolean isVisible() {
            return this.packed;
        }

        public Rectangle getBounds() {
            return getOwner().getBounds();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$RepaintTrigger.class */
    protected static class RepaintTrigger extends JComponent {
        private static final long serialVersionUID = 1;
        private final Listener listener = createListener();
        private final JComponent content;
        private Rectangle dirty;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$RepaintTrigger$Listener.class */
        protected class Listener extends WindowAdapter implements ComponentListener, HierarchyListener, AWTEventListener {
            protected Listener() {
            }

            public void windowOpened(WindowEvent e) {
                RepaintTrigger.this.repaint();
            }

            public void componentHidden(ComponentEvent e) {
            }

            public void componentMoved(ComponentEvent e) {
            }

            public void componentResized(ComponentEvent e) {
                RepaintTrigger.this.setSize(RepaintTrigger.this.getParent().getSize());
                RepaintTrigger.this.repaint();
            }

            public void componentShown(ComponentEvent e) {
                RepaintTrigger.this.repaint();
            }

            public void hierarchyChanged(HierarchyEvent e) {
                RepaintTrigger.this.repaint();
            }

            public void eventDispatched(AWTEvent e) {
                Component src;
                if ((e instanceof MouseEvent) && (src = ((MouseEvent) e).getComponent()) != null && SwingUtilities.isDescendingFrom(src, RepaintTrigger.this.content)) {
                    MouseEvent me = SwingUtilities.convertMouseEvent(src, (MouseEvent) e, RepaintTrigger.this.content);
                    Component c = SwingUtilities.getDeepestComponentAt(RepaintTrigger.this.content, me.getX(), me.getY());
                    if (c != null) {
                        RepaintTrigger.this.setCursor(c.getCursor());
                    }
                }
            }
        }

        public RepaintTrigger(JComponent content) {
            this.content = content;
        }

        public void addNotify() {
            super.addNotify();
            Window w = SwingUtilities.getWindowAncestor(this);
            setSize(getParent().getSize());
            w.addComponentListener(this.listener);
            w.addWindowListener(this.listener);
            Toolkit.getDefaultToolkit().addAWTEventListener(this.listener, 48L);
        }

        public void removeNotify() {
            Toolkit.getDefaultToolkit().removeAWTEventListener(this.listener);
            Window w = SwingUtilities.getWindowAncestor(this);
            w.removeComponentListener(this.listener);
            w.removeWindowListener(this.listener);
            super.removeNotify();
        }

        protected void paintComponent(Graphics g) {
            Rectangle bounds = g.getClipBounds();
            if (this.dirty == null || !this.dirty.contains(bounds)) {
                if (this.dirty == null) {
                    this.dirty = bounds;
                } else {
                    this.dirty = this.dirty.union(bounds);
                }
                this.content.repaint(this.dirty);
                return;
            }
            this.dirty = null;
        }

        protected Listener createListener() {
            return new Listener();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$NativeWindowUtils.class */
    public static abstract class NativeWindowUtils {

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$NativeWindowUtils$TransparentContentPane.class */
        protected abstract class TransparentContentPane extends JPanel implements AWTEventListener {
            private static final long serialVersionUID = 1;
            private boolean transparent;

            protected abstract void paintDirect(BufferedImage bufferedImage, Rectangle rectangle);

            public TransparentContentPane(Container oldContent) {
                super(new BorderLayout());
                add(oldContent, "Center");
                setTransparent(true);
                if (oldContent instanceof JPanel) {
                    ((JComponent) oldContent).setOpaque(false);
                }
            }

            public void addNotify() {
                super.addNotify();
                Toolkit.getDefaultToolkit().addAWTEventListener(this, 2L);
            }

            public void removeNotify() {
                Toolkit.getDefaultToolkit().removeAWTEventListener(this);
                super.removeNotify();
            }

            public void setTransparent(boolean transparent) {
                this.transparent = transparent;
                setOpaque(!transparent);
                setDoubleBuffered(!transparent);
                repaint();
            }

            public void eventDispatched(AWTEvent e) {
                if (e.getID() == 300 && SwingUtilities.isDescendingFrom(((ContainerEvent) e).getChild(), this)) {
                    Component child = ((ContainerEvent) e).getChild();
                    NativeWindowUtils.this.setDoubleBuffered(child, false);
                }
            }

            public void paint(Graphics gr) {
                if (this.transparent) {
                    Rectangle r = gr.getClipBounds();
                    int w = r.width;
                    int h = r.height;
                    if (getWidth() > 0 && getHeight() > 0) {
                        BufferedImage buf = new BufferedImage(w, h, 3);
                        Graphics2D g = buf.createGraphics();
                        g.setComposite(AlphaComposite.Clear);
                        g.fillRect(0, 0, w, h);
                        g.dispose();
                        Graphics2D g2 = buf.createGraphics();
                        g2.translate(-r.x, -r.y);
                        super.paint(g2);
                        g2.dispose();
                        paintDirect(buf, r);
                        return;
                    }
                    return;
                }
                super.paint(gr);
            }
        }

        protected Window getWindow(Component c) {
            return c instanceof Window ? (Window) c : SwingUtilities.getWindowAncestor(c);
        }

        protected void whenDisplayable(Component w, final Runnable action) {
            if (w.isDisplayable() && (!Holder.requiresVisible || w.isVisible())) {
                action.run();
            } else if (Holder.requiresVisible) {
                getWindow(w).addWindowListener(new WindowAdapter() { // from class: com.sun.jna.platform.WindowUtils.NativeWindowUtils.1
                    public void windowOpened(WindowEvent e) {
                        e.getWindow().removeWindowListener(this);
                        action.run();
                    }

                    public void windowClosed(WindowEvent e) {
                        e.getWindow().removeWindowListener(this);
                    }
                });
            } else {
                w.addHierarchyListener(new HierarchyListener() { // from class: com.sun.jna.platform.WindowUtils.NativeWindowUtils.2
                    public void hierarchyChanged(HierarchyEvent e) {
                        if ((e.getChangeFlags() & 2) != 0 && e.getComponent().isDisplayable()) {
                            e.getComponent().removeHierarchyListener(this);
                            action.run();
                        }
                    }
                });
            }
        }

        protected Raster toRaster(Shape mask) {
            WritableRaster raster = null;
            if (mask != WindowUtils.MASK_NONE) {
                Rectangle bounds = mask.getBounds();
                if (bounds.width > 0 && bounds.height > 0) {
                    BufferedImage clip = new BufferedImage(bounds.x + bounds.width, bounds.y + bounds.height, 12);
                    Graphics2D g = clip.createGraphics();
                    g.setColor(Color.black);
                    g.fillRect(0, 0, bounds.x + bounds.width, bounds.y + bounds.height);
                    g.setColor(Color.white);
                    g.fill(mask);
                    raster = clip.getRaster();
                }
            }
            return raster;
        }

        protected Raster toRaster(Component c, Icon mask) {
            WritableRaster alphaRaster = null;
            if (mask != null) {
                Rectangle bounds = new Rectangle(0, 0, mask.getIconWidth(), mask.getIconHeight());
                BufferedImage clip = new BufferedImage(bounds.width, bounds.height, 2);
                Graphics2D g = clip.createGraphics();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, bounds.width, bounds.height);
                g.setComposite(AlphaComposite.SrcOver);
                mask.paintIcon(c, g, 0, 0);
                alphaRaster = clip.getAlphaRaster();
            }
            return alphaRaster;
        }

        protected Shape toShape(Raster raster) {
            final Area area = new Area(new Rectangle(0, 0, 0, 0));
            RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() { // from class: com.sun.jna.platform.WindowUtils.NativeWindowUtils.3
                @Override // com.sun.jna.platform.RasterRangesUtils.RangesOutput
                public boolean outputRange(int x, int y, int w, int h) {
                    area.add(new Area(new Rectangle(x, y, w, h)));
                    return true;
                }
            });
            return area;
        }

        public void setWindowAlpha(Window w, float alpha) {
        }

        public boolean isWindowAlphaSupported() {
            return false;
        }

        public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice dev = env.getDefaultScreenDevice();
            return dev.getDefaultConfiguration();
        }

        public void setWindowTransparent(Window w, boolean transparent) {
        }

        protected void setDoubleBuffered(Component root, boolean buffered) {
            if (root instanceof JComponent) {
                ((JComponent) root).setDoubleBuffered(buffered);
            }
            if ((root instanceof JRootPane) && buffered) {
                ((JRootPane) root).setDoubleBuffered(true);
                return;
            }
            if (root instanceof Container) {
                Component[] kids = ((Container) root).getComponents();
                for (Component component : kids) {
                    setDoubleBuffered(component, buffered);
                }
            }
        }

        protected void setLayersTransparent(Window w, boolean transparent) {
            Color bg = transparent ? new Color(0, 0, 0, 0) : null;
            if (w instanceof RootPaneContainer) {
                RootPaneContainer rpc = (RootPaneContainer) w;
                JRootPane root = rpc.getRootPane();
                JLayeredPane lp = root.getLayeredPane();
                JComponent contentPane = root.getContentPane();
                JComponent content = contentPane instanceof JComponent ? contentPane : null;
                if (transparent) {
                    lp.putClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE, Boolean.valueOf(lp.isOpaque()));
                    lp.setOpaque(false);
                    root.putClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE, Boolean.valueOf(root.isOpaque()));
                    root.setOpaque(false);
                    if (content != null) {
                        content.putClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE, Boolean.valueOf(content.isOpaque()));
                        content.setOpaque(false);
                    }
                    root.putClientProperty(WindowUtils.TRANSPARENT_OLD_BG, root.getParent().getBackground());
                } else {
                    lp.setOpaque(Boolean.TRUE.equals(lp.getClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE)));
                    lp.putClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE, (Object) null);
                    root.setOpaque(Boolean.TRUE.equals(root.getClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE)));
                    root.putClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE, (Object) null);
                    if (content != null) {
                        content.setOpaque(Boolean.TRUE.equals(content.getClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE)));
                        content.putClientProperty(WindowUtils.TRANSPARENT_OLD_OPAQUE, (Object) null);
                    }
                    bg = (Color) root.getClientProperty(WindowUtils.TRANSPARENT_OLD_BG);
                    root.putClientProperty(WindowUtils.TRANSPARENT_OLD_BG, (Object) null);
                }
            }
            w.setBackground(bg);
        }

        protected void setMask(Component c, Raster raster) {
            throw new UnsupportedOperationException("Window masking is not available");
        }

        protected void setWindowMask(Component w, Raster raster) {
            if (w.isLightweight()) {
                throw new IllegalArgumentException("Component must be heavyweight: " + w);
            }
            setMask(w, raster);
        }

        public void setWindowMask(Component w, Shape mask) {
            setWindowMask(w, toRaster(mask));
        }

        public void setWindowMask(Component w, Icon mask) {
            setWindowMask(w, toRaster(w, mask));
        }

        protected void setForceHeavyweightPopups(Window w, boolean force) {
            if (!(w instanceof HeavyweightForcer)) {
                Window[] owned = w.getOwnedWindows();
                for (int i = 0; i < owned.length; i++) {
                    if (owned[i] instanceof HeavyweightForcer) {
                        if (force) {
                            return;
                        } else {
                            owned[i].dispose();
                        }
                    }
                }
                Boolean b = Boolean.valueOf(System.getProperty("jna.force_hw_popups", "true"));
                if (force && b.booleanValue()) {
                    new HeavyweightForcer(w);
                }
            }
        }

        protected BufferedImage getWindowIcon(WinDef.HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        protected Dimension getIconSize(WinDef.HICON hIcon) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        protected List<DesktopWindow> getAllWindows(boolean onlyVisibleWindows) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        protected String getWindowTitle(WinDef.HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        protected String getProcessFilePath(WinDef.HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }

        protected Rectangle getWindowLocationAndSize(WinDef.HWND hwnd) {
            throw new UnsupportedOperationException("This platform is not supported, yet.");
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$Holder.class */
    private static class Holder {
        public static boolean requiresVisible;
        public static final NativeWindowUtils INSTANCE;

        private Holder() {
        }

        static {
            if (Platform.isWindows()) {
                INSTANCE = new W32WindowUtils();
                return;
            }
            if (Platform.isMac()) {
                INSTANCE = new MacWindowUtils();
            } else if (Platform.isX11()) {
                INSTANCE = new X11WindowUtils();
                requiresVisible = System.getProperty("java.version").matches("^1\\.4\\..*");
            } else {
                String os = System.getProperty("os.name");
                throw new UnsupportedOperationException("No support for " + os);
            }
        }
    }

    private static NativeWindowUtils getInstance() {
        return Holder.INSTANCE;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$W32WindowUtils.class */
    private static class W32WindowUtils extends NativeWindowUtils {
        private W32WindowUtils() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public WinDef.HWND getHWnd(Component w) {
            WinDef.HWND hwnd = new WinDef.HWND();
            hwnd.setPointer(Native.getComponentPointer(w));
            return hwnd;
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public boolean isWindowAlphaSupported() {
            return Boolean.getBoolean("sun.java2d.noddraw");
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean usingUpdateLayeredWindow(Window w) {
            if (w instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer) w).getRootPane();
                return root.getClientProperty(WindowUtils.TRANSPARENT_OLD_BG) != null;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void storeAlpha(Window w, byte alpha) {
            if (w instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer) w).getRootPane();
                Byte b = alpha == -1 ? null : Byte.valueOf(alpha);
                root.putClientProperty(WindowUtils.TRANSPARENT_ALPHA, b);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public byte getAlpha(Window w) {
            if (w instanceof RootPaneContainer) {
                JRootPane root = ((RootPaneContainer) w).getRootPane();
                Byte b = (Byte) root.getClientProperty(WindowUtils.TRANSPARENT_ALPHA);
                if (b != null) {
                    return b.byteValue();
                }
                return (byte) -1;
            }
            return (byte) -1;
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowAlpha(final Window w, final float alpha) {
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows");
            }
            whenDisplayable(w, new Runnable() { // from class: com.sun.jna.platform.WindowUtils.W32WindowUtils.1
                @Override // java.lang.Runnable
                public void run() {
                    WinDef.HWND hWnd = W32WindowUtils.this.getHWnd(w);
                    User32 user = User32.INSTANCE;
                    int flags = user.GetWindowLong(hWnd, -20);
                    byte level = (byte) (((int) (255.0f * alpha)) & 255);
                    if (W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
                        WinUser.BLENDFUNCTION blend = new WinUser.BLENDFUNCTION();
                        blend.SourceConstantAlpha = level;
                        blend.AlphaFormat = (byte) 1;
                        user.UpdateLayeredWindow(hWnd, null, null, null, null, null, 0, blend, 2);
                    } else if (alpha == 1.0f) {
                        user.SetWindowLong(hWnd, -20, flags & (-524289));
                    } else {
                        user.SetWindowLong(hWnd, -20, flags | 524288);
                        user.SetLayeredWindowAttributes(hWnd, 0, level, 2);
                    }
                    W32WindowUtils.this.setForceHeavyweightPopups(w, alpha != 1.0f);
                    W32WindowUtils.this.storeAlpha(w, level);
                }
            });
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$W32WindowUtils$W32TransparentContentPane.class */
        private class W32TransparentContentPane extends NativeWindowUtils.TransparentContentPane {
            private static final long serialVersionUID = 1;
            private WinDef.HDC memDC;
            private WinDef.HBITMAP hBitmap;
            private Pointer pbits;
            private Dimension bitmapSize;

            public W32TransparentContentPane(Container content) {
                super(content);
            }

            private void disposeBackingStore() {
                GDI32 gdi = GDI32.INSTANCE;
                if (this.hBitmap != null) {
                    gdi.DeleteObject(this.hBitmap);
                    this.hBitmap = null;
                }
                if (this.memDC != null) {
                    gdi.DeleteDC(this.memDC);
                    this.memDC = null;
                }
            }

            @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils.TransparentContentPane
            public void removeNotify() {
                super.removeNotify();
                disposeBackingStore();
            }

            @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils.TransparentContentPane
            public void setTransparent(boolean transparent) {
                super.setTransparent(transparent);
                if (!transparent) {
                    disposeBackingStore();
                }
            }

            @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils.TransparentContentPane
            protected void paintDirect(BufferedImage buf, Rectangle bounds) {
                Window win = SwingUtilities.getWindowAncestor(this);
                GDI32 gdi = GDI32.INSTANCE;
                User32 user = User32.INSTANCE;
                int x = bounds.x;
                int y = bounds.y;
                Point origin = SwingUtilities.convertPoint(this, x, y, win);
                int w = bounds.width;
                int h = bounds.height;
                int ww = win.getWidth();
                int wh = win.getHeight();
                WinDef.HDC screenDC = user.GetDC(null);
                WinNT.HANDLE oldBitmap = null;
                try {
                    if (this.memDC == null) {
                        this.memDC = gdi.CreateCompatibleDC(screenDC);
                    }
                    if (this.hBitmap == null || !win.getSize().equals(this.bitmapSize)) {
                        if (this.hBitmap != null) {
                            gdi.DeleteObject(this.hBitmap);
                            this.hBitmap = null;
                        }
                        WinGDI.BITMAPINFO bmi = new WinGDI.BITMAPINFO();
                        bmi.bmiHeader.biWidth = ww;
                        bmi.bmiHeader.biHeight = wh;
                        bmi.bmiHeader.biPlanes = (short) 1;
                        bmi.bmiHeader.biBitCount = (short) 32;
                        bmi.bmiHeader.biCompression = 0;
                        bmi.bmiHeader.biSizeImage = ww * wh * 4;
                        PointerByReference ppbits = new PointerByReference();
                        this.hBitmap = gdi.CreateDIBSection(this.memDC, bmi, 0, ppbits, null, 0);
                        this.pbits = ppbits.getValue();
                        this.bitmapSize = new Dimension(ww, wh);
                    }
                    WinNT.HANDLE oldBitmap2 = gdi.SelectObject(this.memDC, this.hBitmap);
                    Raster raster = buf.getData();
                    int[] pixel = new int[4];
                    int[] bits = new int[w];
                    for (int row = 0; row < h; row++) {
                        for (int col = 0; col < w; col++) {
                            raster.getPixel(col, row, pixel);
                            int alpha = (pixel[3] & 255) << 24;
                            int red = pixel[2] & 255;
                            int green = (pixel[1] & 255) << 8;
                            int blue = (pixel[0] & 255) << 16;
                            bits[col] = alpha | red | green | blue;
                        }
                        int v = (wh - (origin.y + row)) - 1;
                        this.pbits.write(((v * ww) + origin.x) * 4, bits, 0, bits.length);
                    }
                    WinUser.SIZE winSize = new WinUser.SIZE();
                    winSize.cx = win.getWidth();
                    winSize.cy = win.getHeight();
                    WinDef.POINT winLoc = new WinDef.POINT();
                    winLoc.x = win.getX();
                    winLoc.y = win.getY();
                    WinDef.POINT srcLoc = new WinDef.POINT();
                    WinUser.BLENDFUNCTION blend = new WinUser.BLENDFUNCTION();
                    WinDef.HWND hWnd = W32WindowUtils.this.getHWnd(win);
                    ByteByReference bref = new ByteByReference();
                    IntByReference iref = new IntByReference();
                    byte level = W32WindowUtils.this.getAlpha(win);
                    try {
                        if (user.GetLayeredWindowAttributes(hWnd, null, bref, iref) && (iref.getValue() & 2) != 0) {
                            level = bref.getValue();
                        }
                    } catch (UnsatisfiedLinkError e) {
                    }
                    blend.SourceConstantAlpha = level;
                    blend.AlphaFormat = (byte) 1;
                    user.UpdateLayeredWindow(hWnd, screenDC, winLoc, winSize, this.memDC, srcLoc, 0, blend, 2);
                    user.ReleaseDC(null, screenDC);
                    if (this.memDC != null && oldBitmap2 != null) {
                        WinDef.HDC hdc = this.memDC;
                    }
                } finally {
                    user.ReleaseDC(null, screenDC);
                    if (this.memDC != null && oldBitmap != null) {
                        gdi.SelectObject(this.memDC, oldBitmap);
                    }
                }
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowTransparent(final Window w, final boolean transparent) {
            if (!(w instanceof RootPaneContainer)) {
                throw new IllegalArgumentException("Window must be a RootPaneContainer");
            }
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("Set sun.java2d.noddraw=true to enable transparent windows");
            }
            boolean isTransparent = w.getBackground() != null && w.getBackground().getAlpha() == 0;
            if (transparent == isTransparent) {
                return;
            }
            whenDisplayable(w, new Runnable() { // from class: com.sun.jna.platform.WindowUtils.W32WindowUtils.2
                @Override // java.lang.Runnable
                public void run() {
                    User32 user = User32.INSTANCE;
                    WinDef.HWND hWnd = W32WindowUtils.this.getHWnd(w);
                    int flags = user.GetWindowLong(hWnd, -20);
                    JRootPane root = w.getRootPane();
                    JLayeredPane lp = root.getLayeredPane();
                    W32TransparentContentPane contentPane = root.getContentPane();
                    if (contentPane instanceof W32TransparentContentPane) {
                        contentPane.setTransparent(transparent);
                    } else if (transparent) {
                        W32TransparentContentPane w32content = W32WindowUtils.this.new W32TransparentContentPane(contentPane);
                        root.setContentPane(w32content);
                        lp.add(new RepaintTrigger(w32content), JLayeredPane.DRAG_LAYER);
                    }
                    if (transparent && !W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
                        user.SetWindowLong(hWnd, -20, flags | 524288);
                    } else if (!transparent && W32WindowUtils.this.usingUpdateLayeredWindow(w)) {
                        user.SetWindowLong(hWnd, -20, flags & (-524289));
                    }
                    W32WindowUtils.this.setLayersTransparent(w, transparent);
                    W32WindowUtils.this.setForceHeavyweightPopups(w, transparent);
                    W32WindowUtils.this.setDoubleBuffered(w, !transparent);
                }
            });
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowMask(Component w, Shape mask) {
            if ((mask instanceof Area) && ((Area) mask).isPolygonal()) {
                setMask(w, (Area) mask);
            } else {
                super.setWindowMask(w, mask);
            }
        }

        private void setWindowRegion(final Component w, final WinDef.HRGN hrgn) {
            whenDisplayable(w, new Runnable() { // from class: com.sun.jna.platform.WindowUtils.W32WindowUtils.3
                @Override // java.lang.Runnable
                public void run() {
                    GDI32 gdi = GDI32.INSTANCE;
                    User32 user = User32.INSTANCE;
                    WinDef.HWND hWnd = W32WindowUtils.this.getHWnd(w);
                    try {
                        user.SetWindowRgn(hWnd, hrgn, true);
                        W32WindowUtils.this.setForceHeavyweightPopups(W32WindowUtils.this.getWindow(w), hrgn != null);
                    } finally {
                        gdi.DeleteObject(hrgn);
                    }
                }
            });
        }

        private void setMask(Component w, Area area) {
            GDI32 gdi = GDI32.INSTANCE;
            PathIterator pi = area.getPathIterator((AffineTransform) null);
            int mode = pi.getWindingRule() == 1 ? 2 : 1;
            float[] coords = new float[6];
            List<WinDef.POINT> points = new ArrayList<>();
            int size = 0;
            List<Integer> sizes = new ArrayList<>();
            while (!pi.isDone()) {
                int type = pi.currentSegment(coords);
                if (type == 0) {
                    size = 1;
                    points.add(new WinDef.POINT((int) coords[0], (int) coords[1]));
                } else if (type == 1) {
                    size++;
                    points.add(new WinDef.POINT((int) coords[0], (int) coords[1]));
                } else if (type == 4) {
                    sizes.add(Integer.valueOf(size));
                } else {
                    throw new RuntimeException("Area is not polygonal: " + area);
                }
                pi.next();
            }
            WinDef.POINT[] lppt = (WinDef.POINT[]) new WinDef.POINT().toArray(points.size());
            WinDef.POINT[] pts = (WinDef.POINT[]) points.toArray(new WinDef.POINT[points.size()]);
            for (int i = 0; i < lppt.length; i++) {
                lppt[i].x = pts[i].x;
                lppt[i].y = pts[i].y;
            }
            int[] counts = new int[sizes.size()];
            for (int i2 = 0; i2 < counts.length; i2++) {
                counts[i2] = sizes.get(i2).intValue();
            }
            WinDef.HRGN hrgn = gdi.CreatePolyPolygonRgn(lppt, counts, counts.length, mode);
            setWindowRegion(w, hrgn);
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        protected void setMask(Component w, Raster raster) {
            GDI32 gdi = GDI32.INSTANCE;
            final WinDef.HRGN region = raster != null ? gdi.CreateRectRgn(0, 0, 0, 0) : null;
            if (region != null) {
                final WinDef.HRGN tempRgn = gdi.CreateRectRgn(0, 0, 0, 0);
                try {
                    RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() { // from class: com.sun.jna.platform.WindowUtils.W32WindowUtils.4
                        @Override // com.sun.jna.platform.RasterRangesUtils.RangesOutput
                        public boolean outputRange(int x, int y, int w2, int h) {
                            GDI32 gdi2 = GDI32.INSTANCE;
                            gdi2.SetRectRgn(tempRgn, x, y, x + w2, y + h);
                            return gdi2.CombineRgn(region, region, tempRgn, 2) != 0;
                        }
                    });
                    gdi.DeleteObject(tempRgn);
                } catch (Throwable th) {
                    gdi.DeleteObject(tempRgn);
                    throw th;
                }
            }
            setWindowRegion(w, region);
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public BufferedImage getWindowIcon(WinDef.HWND hwnd) {
            WinDef.DWORDByReference hIconNumber = new WinDef.DWORDByReference();
            WinDef.LRESULT result = User32.INSTANCE.SendMessageTimeout(hwnd, 127, new WinDef.WPARAM(1L), new WinDef.LPARAM(0L), 2, 500, hIconNumber);
            if (result.intValue() == 0) {
                result = User32.INSTANCE.SendMessageTimeout(hwnd, 127, new WinDef.WPARAM(0L), new WinDef.LPARAM(0L), 2, 500, hIconNumber);
            }
            if (result.intValue() == 0) {
                result = User32.INSTANCE.SendMessageTimeout(hwnd, 127, new WinDef.WPARAM(2L), new WinDef.LPARAM(0L), 2, 500, hIconNumber);
            }
            if (result.intValue() == 0) {
                result = new WinDef.LRESULT(User32.INSTANCE.GetClassLongPtr(hwnd, -14).intValue());
                hIconNumber.getValue().setValue(result.intValue());
            }
            if (result.intValue() == 0) {
                result = new WinDef.LRESULT(User32.INSTANCE.GetClassLongPtr(hwnd, -34).intValue());
                hIconNumber.getValue().setValue(result.intValue());
            }
            if (result.intValue() == 0) {
                return null;
            }
            WinDef.HICON hIcon = new WinDef.HICON(new Pointer(hIconNumber.getValue().longValue()));
            Dimension iconSize = getIconSize(hIcon);
            if (iconSize.width == 0 || iconSize.height == 0) {
                return null;
            }
            int width = iconSize.width;
            int height = iconSize.height;
            byte[] lpBitsColor = new byte[((width * height) * 24) / 8];
            Pointer lpBitsColorPtr = new Memory(lpBitsColor.length);
            byte[] lpBitsMask = new byte[((width * height) * 24) / 8];
            Pointer lpBitsMaskPtr = new Memory(lpBitsMask.length);
            WinGDI.BITMAPINFO bitmapInfo = new WinGDI.BITMAPINFO();
            WinGDI.BITMAPINFOHEADER hdr = new WinGDI.BITMAPINFOHEADER();
            bitmapInfo.bmiHeader = hdr;
            hdr.biWidth = width;
            hdr.biHeight = height;
            hdr.biPlanes = (short) 1;
            hdr.biBitCount = (short) 24;
            hdr.biCompression = 0;
            hdr.write();
            bitmapInfo.write();
            WinDef.HDC hDC = User32.INSTANCE.GetDC(null);
            WinGDI.ICONINFO iconInfo = new WinGDI.ICONINFO();
            User32.INSTANCE.GetIconInfo(hIcon, iconInfo);
            iconInfo.read();
            GDI32.INSTANCE.GetDIBits(hDC, iconInfo.hbmColor, 0, height, lpBitsColorPtr, bitmapInfo, 0);
            lpBitsColorPtr.read(0L, lpBitsColor, 0, lpBitsColor.length);
            GDI32.INSTANCE.GetDIBits(hDC, iconInfo.hbmMask, 0, height, lpBitsMaskPtr, bitmapInfo, 0);
            lpBitsMaskPtr.read(0L, lpBitsMask, 0, lpBitsMask.length);
            BufferedImage image = new BufferedImage(width, height, 2);
            int x = 0;
            int y = height - 1;
            int i = 0;
            while (true) {
                int i2 = i;
                if (i2 < lpBitsColor.length) {
                    int b = lpBitsColor[i2] & 255;
                    int g = lpBitsColor[i2 + 1] & 255;
                    int r = lpBitsColor[i2 + 2] & 255;
                    int a = (255 - lpBitsMask[i2]) & 255;
                    int argb = (a << 24) | (r << 16) | (g << 8) | b;
                    image.setRGB(x, y, argb);
                    x = (x + 1) % width;
                    if (x == 0) {
                        y--;
                    }
                    i = i2 + 3;
                } else {
                    User32.INSTANCE.ReleaseDC(null, hDC);
                    return image;
                }
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public Dimension getIconSize(WinDef.HICON hIcon) {
            WinGDI.ICONINFO iconInfo = new WinGDI.ICONINFO();
            try {
                if (!User32.INSTANCE.GetIconInfo(hIcon, iconInfo)) {
                    return new Dimension();
                }
                iconInfo.read();
                WinGDI.BITMAP bmp = new WinGDI.BITMAP();
                if (iconInfo.hbmColor != null && iconInfo.hbmColor.getPointer() != Pointer.NULL) {
                    int nWrittenBytes = GDI32.INSTANCE.GetObject(iconInfo.hbmColor, bmp.size(), bmp.getPointer());
                    bmp.read();
                    if (nWrittenBytes > 0) {
                        return new Dimension(bmp.bmWidth.intValue(), bmp.bmHeight.intValue());
                    }
                } else if (iconInfo.hbmMask != null && iconInfo.hbmMask.getPointer() != Pointer.NULL) {
                    int nWrittenBytes2 = GDI32.INSTANCE.GetObject(iconInfo.hbmMask, bmp.size(), bmp.getPointer());
                    bmp.read();
                    if (nWrittenBytes2 > 0) {
                        return new Dimension(bmp.bmWidth.intValue(), bmp.bmHeight.intValue() / 2);
                    }
                }
                return new Dimension();
            } finally {
                if (iconInfo.hbmColor != null && iconInfo.hbmColor.getPointer() != Pointer.NULL) {
                    GDI32.INSTANCE.DeleteObject(iconInfo.hbmColor);
                }
                if (iconInfo.hbmMask != null && iconInfo.hbmMask.getPointer() != Pointer.NULL) {
                    GDI32.INSTANCE.DeleteObject(iconInfo.hbmMask);
                }
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public List<DesktopWindow> getAllWindows(final boolean onlyVisibleWindows) {
            final List<DesktopWindow> result = new LinkedList<>();
            WinUser.WNDENUMPROC lpEnumFunc = new WinUser.WNDENUMPROC() { // from class: com.sun.jna.platform.WindowUtils.W32WindowUtils.5
                @Override // com.sun.jna.platform.win32.WinUser.WNDENUMPROC
                public boolean callback(WinDef.HWND hwnd, Pointer arg1) {
                    try {
                        boolean visible = !onlyVisibleWindows || User32.INSTANCE.IsWindowVisible(hwnd);
                        if (visible) {
                            String title = W32WindowUtils.this.getWindowTitle(hwnd);
                            String filePath = W32WindowUtils.this.getProcessFilePath(hwnd);
                            Rectangle locAndSize = W32WindowUtils.this.getWindowLocationAndSize(hwnd);
                            result.add(new DesktopWindow(hwnd, title, filePath, locAndSize));
                        }
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return true;
                    }
                }
            };
            if (!User32.INSTANCE.EnumWindows(lpEnumFunc, null)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            return result;
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public String getWindowTitle(WinDef.HWND hwnd) {
            int requiredLength = User32.INSTANCE.GetWindowTextLength(hwnd) + 1;
            char[] title = new char[requiredLength];
            int length = User32.INSTANCE.GetWindowText(hwnd, title, title.length);
            return Native.toString(Arrays.copyOfRange(title, 0, length));
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public String getProcessFilePath(WinDef.HWND hwnd) {
            IntByReference pid = new IntByReference();
            User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);
            WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(1024, false, pid.getValue());
            if (process == null) {
                if (Kernel32.INSTANCE.GetLastError() != 5) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
                process = Kernel32.INSTANCE.OpenProcess(4096, false, pid.getValue());
                if (process == null) {
                    if (Kernel32.INSTANCE.GetLastError() != 5) {
                        throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                    }
                    return "";
                }
            }
            try {
                String processImagePath = PsapiUtil.GetProcessImageFileName(process);
                if (processImagePath.startsWith("\\Device\\Mup\\")) {
                    String str = "\\" + processImagePath.substring(11);
                    Kernel32.INSTANCE.CloseHandle(process);
                    return str;
                }
                char[] volumeUUID = new char[50];
                WinNT.HANDLE h = Kernel32.INSTANCE.FindFirstVolume(volumeUUID, 50);
                if (h == null || h.equals(WinBase.INVALID_HANDLE_VALUE)) {
                    throw new Win32Exception(Native.getLastError());
                }
                do {
                    try {
                        String volumePath = Native.toString(volumeUUID);
                        for (String s : Kernel32Util.getVolumePathNamesForVolumeName(volumePath)) {
                            if (s.matches("[a-zA-Z]:\\\\")) {
                                for (String path : Kernel32Util.queryDosDevice(s.substring(0, 2), 1024)) {
                                    if (processImagePath.startsWith(path)) {
                                        String str2 = s + processImagePath.substring(path.length() + 1);
                                        Kernel32.INSTANCE.FindVolumeClose(h);
                                        Kernel32.INSTANCE.CloseHandle(process);
                                        return str2;
                                    }
                                }
                            }
                        }
                    } catch (Throwable th) {
                        Kernel32.INSTANCE.FindVolumeClose(h);
                        throw th;
                    }
                } while (Kernel32.INSTANCE.FindNextVolume(h, volumeUUID, 50));
                if (Native.getLastError() != 18) {
                    throw new Win32Exception(Native.getLastError());
                }
                Kernel32.INSTANCE.FindVolumeClose(h);
                Kernel32.INSTANCE.CloseHandle(process);
                return processImagePath;
            } catch (Throwable th2) {
                Kernel32.INSTANCE.CloseHandle(process);
                throw th2;
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public Rectangle getWindowLocationAndSize(WinDef.HWND hwnd) {
            WinDef.RECT lpRect = new WinDef.RECT();
            if (!User32.INSTANCE.GetWindowRect(hwnd, lpRect)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            return new Rectangle(lpRect.left, lpRect.top, Math.abs(lpRect.right - lpRect.left), Math.abs(lpRect.bottom - lpRect.top));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$MacWindowUtils.class */
    private static class MacWindowUtils extends NativeWindowUtils {
        private static final String WDRAG = "apple.awt.draggableWindowBackground";

        private MacWindowUtils() {
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public boolean isWindowAlphaSupported() {
            return true;
        }

        private OSXMaskingContentPane installMaskingPane(Window w) {
            Component oSXMaskingContentPane;
            if (w instanceof RootPaneContainer) {
                RootPaneContainer rpc = (RootPaneContainer) w;
                Component contentPane = rpc.getContentPane();
                if (contentPane instanceof OSXMaskingContentPane) {
                    oSXMaskingContentPane = (OSXMaskingContentPane) contentPane;
                } else {
                    oSXMaskingContentPane = new OSXMaskingContentPane(contentPane);
                    rpc.setContentPane(oSXMaskingContentPane);
                }
            } else {
                Component oldContent = w.getComponentCount() > 0 ? w.getComponent(0) : null;
                if (oldContent instanceof OSXMaskingContentPane) {
                    oSXMaskingContentPane = (OSXMaskingContentPane) oldContent;
                } else {
                    oSXMaskingContentPane = new OSXMaskingContentPane(oldContent);
                    w.add(oSXMaskingContentPane);
                }
            }
            return oSXMaskingContentPane;
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowTransparent(Window w, boolean transparent) {
            boolean isTransparent = w.getBackground() != null && w.getBackground().getAlpha() == 0;
            if (transparent != isTransparent) {
                setBackgroundTransparent(w, transparent, "setWindowTransparent");
            }
        }

        private void fixWindowDragging(Window w, String context) {
            if (w instanceof RootPaneContainer) {
                JRootPane p = ((RootPaneContainer) w).getRootPane();
                Boolean oldDraggable = (Boolean) p.getClientProperty(WDRAG);
                if (oldDraggable == null) {
                    p.putClientProperty(WDRAG, Boolean.FALSE);
                    if (w.isDisplayable()) {
                        WindowUtils.LOG.log(Level.WARNING, "{0}(): To avoid content dragging, {1}() must be called before the window is realized, or apple.awt.draggableWindowBackground must be set to Boolean.FALSE before the window is realized.  If you really want content dragging, set apple.awt.draggableWindowBackground on the window''s root pane to Boolean.TRUE before calling {2}() to hide this message.", new Object[]{context, context, context});
                    }
                }
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowAlpha(final Window w, final float alpha) {
            if (w instanceof RootPaneContainer) {
                JRootPane p = ((RootPaneContainer) w).getRootPane();
                p.putClientProperty("Window.alpha", Float.valueOf(alpha));
                fixWindowDragging(w, "setWindowAlpha");
            }
            whenDisplayable(w, new Runnable() { // from class: com.sun.jna.platform.WindowUtils.MacWindowUtils.1
                @Override // java.lang.Runnable
                public void run() {
                    try {
                        Method getPeer = w.getClass().getMethod("getPeer", new Class[0]);
                        Object peer = getPeer.invoke(w, new Object[0]);
                        Method setAlpha = peer.getClass().getMethod("setAlpha", Float.TYPE);
                        setAlpha.invoke(peer, Float.valueOf(alpha));
                    } catch (Exception e) {
                    }
                }
            });
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        protected void setWindowMask(Component w, Raster raster) {
            if (raster != null) {
                setWindowMask(w, toShape(raster));
            } else {
                setWindowMask(w, (Shape) new Rectangle(0, 0, w.getWidth(), w.getHeight()));
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowMask(Component c, Shape shape) {
            if (c instanceof Window) {
                Window w = (Window) c;
                OSXMaskingContentPane content = installMaskingPane(w);
                content.setMask(shape);
                setBackgroundTransparent(w, shape != WindowUtils.MASK_NONE, "setWindowMask");
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$MacWindowUtils$OSXMaskingContentPane.class */
        private static class OSXMaskingContentPane extends JPanel {
            private static final long serialVersionUID = 1;
            private Shape shape;

            public OSXMaskingContentPane(Component oldContent) {
                super(new BorderLayout());
                if (oldContent != null) {
                    add(oldContent, "Center");
                }
            }

            public void setMask(Shape shape) {
                this.shape = shape;
                repaint();
            }

            public void paint(Graphics graphics) {
                Graphics2D g = graphics.create();
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.dispose();
                if (this.shape != null) {
                    Graphics2D g2 = graphics.create();
                    g2.setClip(this.shape);
                    super.paint(g2);
                    g2.dispose();
                    return;
                }
                super.paint(graphics);
            }
        }

        private void setBackgroundTransparent(Window w, boolean transparent, String context) {
            JRootPane rp = w instanceof RootPaneContainer ? ((RootPaneContainer) w).getRootPane() : null;
            if (transparent) {
                if (rp != null) {
                    rp.putClientProperty(WindowUtils.TRANSPARENT_OLD_BG, w.getBackground());
                }
                w.setBackground(new Color(0, 0, 0, 0));
            } else if (rp != null) {
                Color bg = (Color) rp.getClientProperty(WindowUtils.TRANSPARENT_OLD_BG);
                if (bg != null) {
                    bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), bg.getAlpha());
                }
                w.setBackground(bg);
                rp.putClientProperty(WindowUtils.TRANSPARENT_OLD_BG, (Object) null);
            } else {
                w.setBackground((Color) null);
            }
            fixWindowDragging(w, context);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$X11WindowUtils.class */
    private static class X11WindowUtils extends NativeWindowUtils {
        private boolean didCheck;
        private long[] alphaVisualIDs;
        private static final long OPAQUE = 4294967295L;
        private static final String OPACITY = "_NET_WM_WINDOW_OPACITY";

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$X11WindowUtils$PixmapSource.class */
        private interface PixmapSource {
            X11.Pixmap getPixmap(X11.Display display, X11.Window window);
        }

        private X11WindowUtils() {
            this.alphaVisualIDs = new long[0];
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static X11.Pixmap createBitmap(X11.Display dpy, X11.Window win, Raster raster) {
            X11 x11 = X11.INSTANCE;
            Rectangle bounds = raster.getBounds();
            int width = bounds.x + bounds.width;
            int height = bounds.y + bounds.height;
            X11.Pixmap pm = x11.XCreatePixmap(dpy, win, width, height, 1);
            X11.GC gc = x11.XCreateGC(dpy, pm, new NativeLong(0L), null);
            if (gc == null) {
                return null;
            }
            x11.XSetForeground(dpy, gc, new NativeLong(0L));
            x11.XFillRectangle(dpy, pm, gc, 0, 0, width, height);
            final List<Rectangle> rlist = new ArrayList<>();
            try {
                RasterRangesUtils.outputOccupiedRanges(raster, new RasterRangesUtils.RangesOutput() { // from class: com.sun.jna.platform.WindowUtils.X11WindowUtils.1
                    @Override // com.sun.jna.platform.RasterRangesUtils.RangesOutput
                    public boolean outputRange(int x, int y, int w, int h) {
                        rlist.add(new Rectangle(x, y, w, h));
                        return true;
                    }
                });
                X11.XRectangle[] rects = (X11.XRectangle[]) new X11.XRectangle().toArray(rlist.size());
                for (int i = 0; i < rects.length; i++) {
                    Rectangle r = rlist.get(i);
                    rects[i].x = (short) r.x;
                    rects[i].y = (short) r.y;
                    rects[i].width = (short) r.width;
                    rects[i].height = (short) r.height;
                    Pointer p = rects[i].getPointer();
                    p.setShort(0L, (short) r.x);
                    p.setShort(2L, (short) r.y);
                    p.setShort(4L, (short) r.width);
                    p.setShort(6L, (short) r.height);
                    rects[i].setAutoSynch(false);
                }
                x11.XSetForeground(dpy, gc, new NativeLong(1L));
                x11.XFillRectangles(dpy, pm, gc, rects, rects.length);
                return pm;
            } finally {
                x11.XFreeGC(dpy, gc);
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public boolean isWindowAlphaSupported() {
            return getAlphaVisualIDs().length > 0;
        }

        private static long getVisualID(GraphicsConfiguration config) {
            try {
                Object o = config.getClass().getMethod("getVisual", (Class[]) null).invoke(config, (Object[]) null);
                return ((Number) o).longValue();
            } catch (Exception e) {
                e.printStackTrace();
                return -1L;
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
            if (isWindowAlphaSupported()) {
                GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice[] devices = env.getScreenDevices();
                for (GraphicsDevice graphicsDevice : devices) {
                    GraphicsConfiguration[] configs = graphicsDevice.getConfigurations();
                    for (int j = 0; j < configs.length; j++) {
                        long visualID = getVisualID(configs[j]);
                        long[] ids = getAlphaVisualIDs();
                        for (long j2 : ids) {
                            if (visualID == j2) {
                                return configs[j];
                            }
                        }
                    }
                }
            }
            return super.getAlphaCompatibleGraphicsConfiguration();
        }

        private synchronized long[] getAlphaVisualIDs() {
            if (this.didCheck) {
                return this.alphaVisualIDs;
            }
            this.didCheck = true;
            X11 x11 = X11.INSTANCE;
            X11.Display dpy = x11.XOpenDisplay(null);
            if (dpy == null) {
                return this.alphaVisualIDs;
            }
            X11.XVisualInfo info = null;
            try {
                int screen = x11.XDefaultScreen(dpy);
                X11.XVisualInfo template = new X11.XVisualInfo();
                template.screen = screen;
                template.depth = 32;
                template.c_class = 4;
                NativeLong mask = new NativeLong(14L);
                IntByReference pcount = new IntByReference();
                X11.XVisualInfo info2 = x11.XGetVisualInfo(dpy, mask, template, pcount);
                if (info2 != null) {
                    List<X11.VisualID> list = new ArrayList<>();
                    X11.XVisualInfo[] infos = (X11.XVisualInfo[]) info2.toArray(pcount.getValue());
                    for (int i = 0; i < infos.length; i++) {
                        X11.Xrender.XRenderPictFormat format = X11.Xrender.INSTANCE.XRenderFindVisualFormat(dpy, infos[i].visual);
                        if (format.type == 1 && format.direct.alphaMask != 0) {
                            list.add(infos[i].visualid);
                        }
                    }
                    this.alphaVisualIDs = new long[list.size()];
                    for (int i2 = 0; i2 < this.alphaVisualIDs.length; i2++) {
                        this.alphaVisualIDs[i2] = list.get(i2).longValue();
                    }
                    long[] jArr = this.alphaVisualIDs;
                    if (info2 != null) {
                    }
                    return jArr;
                }
                if (info2 != null) {
                }
                return this.alphaVisualIDs;
            } finally {
                if (info != null) {
                    x11.XFree(info.getPointer());
                }
                x11.XCloseDisplay(dpy);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static X11.Window getContentWindow(Window w, X11.Display dpy, X11.Window win, Point offset) {
            if (((w instanceof Frame) && !((Frame) w).isUndecorated()) || ((w instanceof Dialog) && !((Dialog) w).isUndecorated())) {
                X11 x11 = X11.INSTANCE;
                X11.WindowByReference rootp = new X11.WindowByReference();
                X11.WindowByReference parentp = new X11.WindowByReference();
                PointerByReference childrenp = new PointerByReference();
                IntByReference countp = new IntByReference();
                x11.XQueryTree(dpy, win, rootp, parentp, childrenp, countp);
                Pointer p = childrenp.getValue();
                int[] ids = p.getIntArray(0L, countp.getValue());
                if (0 < ids.length) {
                    int id = ids[0];
                    X11.Window child = new X11.Window(id);
                    X11.XWindowAttributes xwa = new X11.XWindowAttributes();
                    x11.XGetWindowAttributes(dpy, child, xwa);
                    offset.x = -xwa.x;
                    offset.y = -xwa.y;
                    win = child;
                }
                if (p != null) {
                    x11.XFree(p);
                }
            }
            return win;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static X11.Window getDrawable(Component w) {
            int id = (int) Native.getComponentID(w);
            if (id == 0) {
                return null;
            }
            return new X11.Window(id);
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowAlpha(final Window w, final float alpha) {
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual");
            }
            Runnable action = new Runnable() { // from class: com.sun.jna.platform.WindowUtils.X11WindowUtils.2
                @Override // java.lang.Runnable
                public void run() {
                    X11 x11 = X11.INSTANCE;
                    X11.Display dpy = x11.XOpenDisplay(null);
                    if (dpy != null) {
                        try {
                            X11.Window win = X11WindowUtils.getDrawable(w);
                            if (alpha == 1.0f) {
                                x11.XDeleteProperty(dpy, win, x11.XInternAtom(dpy, X11WindowUtils.OPACITY, false));
                            } else {
                                int opacity = (int) (((long) (alpha * 4.2949673E9f)) & (-1));
                                IntByReference patom = new IntByReference(opacity);
                                x11.XChangeProperty(dpy, win, x11.XInternAtom(dpy, X11WindowUtils.OPACITY, false), X11.XA_CARDINAL, 32, 0, patom.getPointer(), 1);
                            }
                        } finally {
                            x11.XCloseDisplay(dpy);
                        }
                    }
                }
            };
            whenDisplayable(w, action);
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/WindowUtils$X11WindowUtils$X11TransparentContentPane.class */
        private class X11TransparentContentPane extends NativeWindowUtils.TransparentContentPane {
            private static final long serialVersionUID = 1;
            private Memory buffer;
            private int[] pixels;
            private final int[] pixel;

            public X11TransparentContentPane(Container oldContent) {
                super(oldContent);
                this.pixel = new int[4];
            }

            @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils.TransparentContentPane
            protected void paintDirect(BufferedImage buf, Rectangle bounds) {
                Window window = SwingUtilities.getWindowAncestor(this);
                X11 x11 = X11.INSTANCE;
                X11.Display dpy = x11.XOpenDisplay(null);
                X11.Window win = X11WindowUtils.getDrawable(window);
                Point offset = new Point();
                X11.Window win2 = X11WindowUtils.getContentWindow(window, dpy, win, offset);
                X11.GC gc = x11.XCreateGC(dpy, win2, new NativeLong(0L), null);
                Raster raster = buf.getData();
                int w = bounds.width;
                int h = bounds.height;
                if (this.buffer == null || this.buffer.size() != w * h * 4) {
                    this.buffer = new Memory(w * h * 4);
                    this.pixels = new int[w * h];
                }
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        raster.getPixel(x, y, this.pixel);
                        int alpha = this.pixel[3] & 255;
                        int red = this.pixel[2] & 255;
                        int green = this.pixel[1] & 255;
                        int blue = this.pixel[0] & 255;
                        this.pixels[(y * w) + x] = (alpha << 24) | (blue << 16) | (green << 8) | red;
                    }
                }
                X11.XWindowAttributes xwa = new X11.XWindowAttributes();
                x11.XGetWindowAttributes(dpy, win2, xwa);
                X11.XImage image = x11.XCreateImage(dpy, xwa.visual, 32, 2, 0, this.buffer, w, h, 32, w * 4);
                this.buffer.write(0L, this.pixels, 0, this.pixels.length);
                offset.x += bounds.x;
                offset.y += bounds.y;
                x11.XPutImage(dpy, win2, gc, image, 0, 0, offset.x, offset.y, w, h);
                x11.XFree(image.getPointer());
                x11.XFreeGC(dpy, gc);
                x11.XCloseDisplay(dpy);
            }
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        public void setWindowTransparent(final Window w, final boolean transparent) {
            if (!(w instanceof RootPaneContainer)) {
                throw new IllegalArgumentException("Window must be a RootPaneContainer");
            }
            if (!isWindowAlphaSupported()) {
                throw new UnsupportedOperationException("This X11 display does not provide a 32-bit visual");
            }
            if (!w.getGraphicsConfiguration().equals(getAlphaCompatibleGraphicsConfiguration())) {
                throw new IllegalArgumentException("Window GraphicsConfiguration '" + w.getGraphicsConfiguration() + "' does not support transparency");
            }
            boolean isTransparent = w.getBackground() != null && w.getBackground().getAlpha() == 0;
            if (transparent == isTransparent) {
                return;
            }
            whenDisplayable(w, new Runnable() { // from class: com.sun.jna.platform.WindowUtils.X11WindowUtils.3
                @Override // java.lang.Runnable
                public void run() {
                    JRootPane root = w.getRootPane();
                    JLayeredPane lp = root.getLayeredPane();
                    X11TransparentContentPane contentPane = root.getContentPane();
                    if (contentPane instanceof X11TransparentContentPane) {
                        contentPane.setTransparent(transparent);
                    } else if (transparent) {
                        X11TransparentContentPane x11content = X11WindowUtils.this.new X11TransparentContentPane(contentPane);
                        root.setContentPane(x11content);
                        lp.add(new RepaintTrigger(x11content), JLayeredPane.DRAG_LAYER);
                    }
                    X11WindowUtils.this.setLayersTransparent(w, transparent);
                    X11WindowUtils.this.setForceHeavyweightPopups(w, transparent);
                    X11WindowUtils.this.setDoubleBuffered(w, !transparent);
                }
            });
        }

        private void setWindowShape(final Window w, final PixmapSource src) {
            Runnable action = new Runnable() { // from class: com.sun.jna.platform.WindowUtils.X11WindowUtils.4
                @Override // java.lang.Runnable
                public void run() {
                    X11 x11 = X11.INSTANCE;
                    X11.Display dpy = x11.XOpenDisplay(null);
                    if (dpy == null) {
                        return;
                    }
                    X11.Pixmap pm = null;
                    try {
                        X11.Window win = X11WindowUtils.getDrawable(w);
                        pm = src.getPixmap(dpy, win);
                        X11.Xext ext = X11.Xext.INSTANCE;
                        ext.XShapeCombineMask(dpy, win, 0, 0, 0, pm == null ? X11.Pixmap.None : pm, 0);
                        if (pm != null) {
                            x11.XFreePixmap(dpy, pm);
                        }
                        x11.XCloseDisplay(dpy);
                        X11WindowUtils.this.setForceHeavyweightPopups(X11WindowUtils.this.getWindow(w), pm != null);
                    } catch (Throwable th) {
                        if (pm != null) {
                            x11.XFreePixmap(dpy, pm);
                        }
                        x11.XCloseDisplay(dpy);
                        throw th;
                    }
                }
            };
            whenDisplayable(w, action);
        }

        @Override // com.sun.jna.platform.WindowUtils.NativeWindowUtils
        protected void setMask(Component w, final Raster raster) {
            setWindowShape(getWindow(w), new PixmapSource() { // from class: com.sun.jna.platform.WindowUtils.X11WindowUtils.5
                @Override // com.sun.jna.platform.WindowUtils.X11WindowUtils.PixmapSource
                public X11.Pixmap getPixmap(X11.Display dpy, X11.Window win) {
                    if (raster != null) {
                        return X11WindowUtils.createBitmap(dpy, win, raster);
                    }
                    return null;
                }
            });
        }
    }

    public static void setWindowMask(Window w, Shape mask) {
        getInstance().setWindowMask((Component) w, mask);
    }

    public static void setComponentMask(Component c, Shape mask) {
        getInstance().setWindowMask(c, mask);
    }

    public static void setWindowMask(Window w, Icon mask) {
        getInstance().setWindowMask((Component) w, mask);
    }

    public static boolean isWindowAlphaSupported() {
        return getInstance().isWindowAlphaSupported();
    }

    public static GraphicsConfiguration getAlphaCompatibleGraphicsConfiguration() {
        return getInstance().getAlphaCompatibleGraphicsConfiguration();
    }

    public static void setWindowAlpha(Window w, float alpha) {
        getInstance().setWindowAlpha(w, Math.max(ColumnText.GLOBAL_SPACE_CHAR_RATIO, Math.min(alpha, 1.0f)));
    }

    public static void setWindowTransparent(Window w, boolean transparent) {
        getInstance().setWindowTransparent(w, transparent);
    }

    public static BufferedImage getWindowIcon(WinDef.HWND hwnd) {
        return getInstance().getWindowIcon(hwnd);
    }

    public static Dimension getIconSize(WinDef.HICON hIcon) {
        return getInstance().getIconSize(hIcon);
    }

    public static List<DesktopWindow> getAllWindows(boolean onlyVisibleWindows) {
        return getInstance().getAllWindows(onlyVisibleWindows);
    }

    public static String getWindowTitle(WinDef.HWND hwnd) {
        return getInstance().getWindowTitle(hwnd);
    }

    public static String getProcessFilePath(WinDef.HWND hwnd) {
        return getInstance().getProcessFilePath(hwnd);
    }

    public static Rectangle getWindowLocationAndSize(WinDef.HWND hwnd) {
        return getInstance().getWindowLocationAndSize(hwnd);
    }
}
