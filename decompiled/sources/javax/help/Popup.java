package javax.help;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JWindow;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

/* JADX INFO: loaded from: autofirma.jar:javax/help/Popup.class */
public class Popup extends Presentation implements ActionListener {
    private JHelpContentViewer jheditor;
    private Component invoker = null;
    private JWindow window = null;
    private Vector grabbed = null;
    private PopupComponentAdapter pca = null;
    private PopupKeyAdapter pka = null;
    private PopupMouseAdapter pma = null;
    private PopupMouseWheelListener pmwl = null;
    private PopupWindowAdapter pwa = null;
    private Rectangle internalBounds = null;
    private static final String SHOW = "show";
    private static final String CANCEL = "cancel";
    private static boolean on1dot4;
    static Popup currentPopup;
    private static final boolean debug = false;
    static Class class$javax$swing$JWindow;

    /* JADX INFO: renamed from: javax.help.Popup$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:javax/help/Popup$1.class */
    static class AnonymousClass1 {
    }

    static {
        on1dot4 = false;
        try {
            on1dot4 = Class.forName("java.awt.event.MouseWheelEvent") == null;
        } catch (ClassNotFoundException e) {
            on1dot4 = false;
        }
        currentPopup = null;
    }

    private Popup(HelpSet helpSet) {
        setHelpSet(helpSet);
    }

    public static Presentation getPresentation(HelpSet helpSet, String str) {
        Popup popup = new Popup(helpSet);
        if (helpSet != null) {
            HelpSet.Presentation defaultPresentation = null;
            if (str != null) {
                defaultPresentation = helpSet.getPresentation(str);
            }
            if (defaultPresentation == null) {
                defaultPresentation = helpSet.getDefaultPresentation();
            }
            popup.setHelpSetPresentation(defaultPresentation);
        }
        return popup;
    }

    public Component getInvoker() {
        return this.invoker;
    }

    public void setInvoker(Component component) {
        this.invoker = component;
        if (component instanceof JMenuItem) {
            while (!(this.invoker instanceof JMenu)) {
                this.invoker = this.invoker.getParent();
                if (this.invoker instanceof JPopupMenu) {
                    this.invoker = this.invoker.getInvoker();
                }
            }
        }
        if (this.invoker == null) {
            throw new IllegalArgumentException("invoker");
        }
    }

    public Rectangle getInvokerInternalBounds() {
        return this.internalBounds;
    }

    public void setInvokerInternalBounds(Rectangle rectangle) {
        this.internalBounds = rectangle;
    }

    private JWindow getWindow() {
        return this.window;
    }

    @Override // javax.help.Presentation
    public void setDisplayed(boolean z) {
        Container topMostContainer = getTopMostContainer();
        if (this.window == null && !z) {
            return;
        }
        if (this.window == null) {
            this.window = new JWindow(getTopMostContainer());
            this.jheditor = new JHelpContentViewer(getHelpModel());
            this.window.getRootPane().setBorder(BorderFactory.createLineBorder(Color.black, 2));
            this.window.getContentPane().add(this.jheditor, "Center");
        }
        if (this.grabbed == null) {
            this.grabbed = new Vector();
            this.pca = new PopupComponentAdapter(this, null);
            this.pma = new PopupMouseAdapter(this, null);
            this.pwa = new PopupWindowAdapter(this, null);
            this.pka = new PopupKeyAdapter(this, null);
            if (on1dot4) {
                this.pmwl = new PopupMouseWheelListener(this, null);
            }
        } else {
            this.grabbed.clear();
        }
        grabContainer(topMostContainer);
        this.window.addWindowListener(this.pwa);
        if (on1dot4) {
            try {
                this.window.getClass().getMethod("addWindowFocusListener", Class.forName("java.awt.event.WindowFocusListener")).invoke(this.window, this.pwa);
            } catch (Exception e) {
            }
        }
        JRootPane rootPane = getRootPane();
        if (rootPane != null) {
            rootPane.registerKeyboardAction(this, CANCEL, KeyStroke.getKeyStroke(27, 0), 1);
        } else if (topMostContainer != null) {
            topMostContainer.addKeyListener(this.pka);
        }
        this.window.getRootPane().registerKeyboardAction(this, CANCEL, KeyStroke.getKeyStroke(27, 0), 1);
        if (z) {
            showPopup();
        } else {
            cancelPopup();
        }
    }

    @Override // javax.help.Presentation
    public boolean isDisplayed() {
        if (this.window != null) {
            return true;
        }
        return false;
    }

    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == CANCEL) {
            cancelPopup();
        }
    }

    private void showPopup() {
        Class clsClass$;
        if (currentPopup != null) {
            if (class$javax$swing$JWindow == null) {
                clsClass$ = class$("javax.swing.JWindow");
                class$javax$swing$JWindow = clsClass$;
            } else {
                clsClass$ = class$javax$swing$JWindow;
            }
            if (SwingUtilities.getAncestorOfClass(clsClass$, this.invoker) == currentPopup.getWindow()) {
                setInvoker(currentPopup.getInvoker());
            }
        }
        Rectangle rectangleComputePopupBounds = computePopupBounds(getSize());
        this.jheditor.setPreferredSize(new Dimension(rectangleComputePopupBounds.width, rectangleComputePopupBounds.height));
        this.window.setBounds(rectangleComputePopupBounds.x, rectangleComputePopupBounds.y, rectangleComputePopupBounds.width, rectangleComputePopupBounds.height);
        this.window.pack();
        this.window.show();
        currentPopup = this;
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    private Rectangle computePopupBounds(Dimension dimension) {
        Rectangle rectangle;
        if (inModalDialog()) {
            JDialog dialog = getDialog();
            if (dialog instanceof JDialog) {
                JRootPane rootPane = dialog.getRootPane();
                Point locationOnScreen = rootPane.getLocationOnScreen();
                rectangle = rootPane.getBounds();
                rectangle.x = locationOnScreen.x;
                rectangle.y = locationOnScreen.y;
            } else {
                rectangle = dialog.getBounds();
            }
        } else {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            rectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);
        }
        if (this.internalBounds == null) {
            this.internalBounds = this.invoker.getBounds();
        }
        debug(new StringBuffer().append("\nabsBounds=").append(rectangle).append("\ninternalBounds=").append(this.internalBounds).append("\n").toString());
        Point point = new Point(this.internalBounds.x, this.internalBounds.y + this.internalBounds.height);
        SwingUtilities.convertPointToScreen(point, this.invoker);
        Rectangle rectangle2 = new Rectangle(point.x, point.y, dimension.width, dimension.height);
        debug(new StringBuffer().append("below/right ").append(rectangle2).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle2)) {
            return rectangle2;
        }
        Rectangle rectangle3 = new Rectangle(rectangle2);
        rectangle3.setLocation((rectangle2.x + this.internalBounds.width) - rectangle2.width, rectangle2.y);
        debug(new StringBuffer().append("below/right adjust ").append(rectangle3).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle3)) {
            return rectangle3;
        }
        Rectangle rectangle4 = new Rectangle(rectangle2.x, rectangle2.y - (rectangle2.height + this.internalBounds.height), rectangle2.width, rectangle2.height);
        debug(new StringBuffer().append("above/right ").append(rectangle4).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle4)) {
            return rectangle4;
        }
        Rectangle rectangle5 = new Rectangle(rectangle4);
        rectangle5.setLocation((rectangle4.x + this.internalBounds.width) - rectangle4.width, rectangle5.y);
        debug(new StringBuffer().append("above/right adjust ").append(rectangle5).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle5)) {
            return rectangle5;
        }
        Point point2 = new Point(this.internalBounds.x + this.internalBounds.width, this.internalBounds.y);
        SwingUtilities.convertPointToScreen(point2, this.invoker);
        Rectangle rectangle6 = new Rectangle(point2.x, point2.y, dimension.width, dimension.height);
        debug(new StringBuffer().append("right/below ").append(rectangle6).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle6)) {
            return rectangle6;
        }
        Rectangle rectangle7 = new Rectangle(rectangle6);
        rectangle7.setLocation(rectangle7.x, (rectangle6.y + this.internalBounds.height) - rectangle6.height);
        debug(new StringBuffer().append("right/below adjust ").append(rectangle7).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle7)) {
            return rectangle7;
        }
        Rectangle rectangle8 = new Rectangle(rectangle6.x - (rectangle6.width + this.internalBounds.width), rectangle6.y, rectangle6.width, rectangle6.height);
        debug(new StringBuffer().append("left/below ").append(rectangle8).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle8)) {
            return rectangle8;
        }
        Rectangle rectangle9 = new Rectangle(rectangle8);
        rectangle9.setLocation(rectangle9.x, (rectangle8.y + this.internalBounds.height) - rectangle8.height);
        debug(new StringBuffer().append("left/below adjust ").append(rectangle9).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle9)) {
            return rectangle9;
        }
        Rectangle rectangle10 = new Rectangle(0, 0, dimension.width, dimension.height);
        debug(new StringBuffer().append("upper left hand corner ").append(rectangle10).append("\n").toString());
        if (SwingUtilities.isRectangleContainingRectangle(rectangle, rectangle10)) {
            return rectangle10;
        }
        SwingUtilities.computeIntersection(rectangle.x, rectangle.y, rectangle.width, rectangle.height, rectangle10);
        return rectangle10;
    }

    private Container getTopMostContainer() {
        Container container;
        if (this.invoker == null) {
            return null;
        }
        Container parent = this.invoker instanceof Container ? (Container) this.invoker : this.invoker.getParent();
        while (true) {
            container = parent;
            if (container != null) {
                if (container instanceof JPopupMenu) {
                    Container invoker = ((JPopupMenu) container).getInvoker();
                    if (invoker instanceof Container) {
                        container = invoker;
                    }
                }
                if ((container instanceof Window) || (container instanceof Applet)) {
                    break;
                }
                parent = container.getParent();
            } else {
                return null;
            }
        }
        return container;
    }

    private JRootPane getRootPane() {
        Container container;
        if (this.invoker == null || !(this.invoker instanceof JComponent)) {
            return null;
        }
        JRootPane rootPane = this.invoker.getRootPane();
        if (rootPane != null) {
            return rootPane;
        }
        Container parent = this.invoker instanceof Container ? (Container) this.invoker : this.invoker.getParent();
        while (true) {
            container = parent;
            if (container == null || (container instanceof JDialog) || (container instanceof JWindow) || (container instanceof JFrame)) {
                break;
            }
            if (container instanceof JPopupMenu) {
                Container invoker = ((JPopupMenu) container).getInvoker();
                if (invoker instanceof Container) {
                    container = invoker;
                }
            }
            parent = container.getParent();
        }
        if (container instanceof JFrame) {
            rootPane = ((JFrame) container).getRootPane();
        } else if (container instanceof JWindow) {
            rootPane = ((JWindow) container).getRootPane();
        } else if (container instanceof JDialog) {
            rootPane = ((JDialog) container).getRootPane();
        }
        return rootPane;
    }

    private Dialog getDialog() {
        Container container;
        if (this.invoker == null) {
            return null;
        }
        Container parent = this.invoker instanceof Container ? (Container) this.invoker : this.invoker.getParent();
        while (true) {
            container = parent;
            if (container == null || (container instanceof Dialog)) {
                break;
            }
            parent = container.getParent();
        }
        if (container instanceof Dialog) {
            return (Dialog) container;
        }
        return null;
    }

    private boolean inModalDialog() {
        return getDialog() != null;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Popup$PopupWindowAdapter.class */
    private class PopupWindowAdapter extends WindowAdapter {
        private final Popup this$0;

        private PopupWindowAdapter(Popup popup) {
            this.this$0 = popup;
        }

        PopupWindowAdapter(Popup popup, AnonymousClass1 anonymousClass1) {
            this(popup);
        }

        public void windowClosing(WindowEvent windowEvent) {
            this.this$0.cancelPopup();
        }

        public void windowClosed(WindowEvent windowEvent) {
            this.this$0.cancelPopup();
        }

        public void windowIconified(WindowEvent windowEvent) {
            this.this$0.cancelPopup();
        }

        public void windowGainedFocus(WindowEvent windowEvent) {
            this.this$0.window.toFront();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Popup$PopupMouseAdapter.class */
    private class PopupMouseAdapter extends MouseInputAdapter {
        private final Popup this$0;

        private PopupMouseAdapter(Popup popup) {
            this.this$0 = popup;
        }

        PopupMouseAdapter(Popup popup, AnonymousClass1 anonymousClass1) {
            this(popup);
        }

        public void mousePressed(MouseEvent mouseEvent) {
            this.this$0.cancelPopup();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Popup$PopupMouseWheelListener.class */
    private class PopupMouseWheelListener implements MouseWheelListener {
        private final Popup this$0;

        private PopupMouseWheelListener(Popup popup) {
            this.this$0 = popup;
        }

        PopupMouseWheelListener(Popup popup, AnonymousClass1 anonymousClass1) {
            this(popup);
        }

        public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
            this.this$0.cancelPopup();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Popup$PopupComponentAdapter.class */
    private class PopupComponentAdapter extends ComponentAdapter {
        private final Popup this$0;

        private PopupComponentAdapter(Popup popup) {
            this.this$0 = popup;
        }

        PopupComponentAdapter(Popup popup, AnonymousClass1 anonymousClass1) {
            this(popup);
        }

        public void componentResized(ComponentEvent componentEvent) {
            this.this$0.cancelPopup();
        }

        public void componentMoved(ComponentEvent componentEvent) {
            this.this$0.cancelPopup();
        }

        public void componentShown(ComponentEvent componentEvent) {
            this.this$0.cancelPopup();
        }

        public void componentHidden(ComponentEvent componentEvent) {
            this.this$0.cancelPopup();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/Popup$PopupKeyAdapter.class */
    private class PopupKeyAdapter extends KeyAdapter {
        private final Popup this$0;

        private PopupKeyAdapter(Popup popup) {
            this.this$0 = popup;
        }

        PopupKeyAdapter(Popup popup, AnonymousClass1 anonymousClass1) {
            this(popup);
        }

        public void keyReleased(KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == 27) {
                this.this$0.cancelPopup();
            }
        }
    }

    private void grabContainer(Container container) {
        if (container instanceof Window) {
            ((Window) container).addWindowListener(this.pwa);
            ((Window) container).addComponentListener(this.pca);
            this.grabbed.addElement(container);
        }
        synchronized (container.getTreeLock()) {
            int componentCount = container.getComponentCount();
            Component[] components = container.getComponents();
            for (int i = 0; i < componentCount; i++) {
                Component component = components[i];
                if (component.isVisible()) {
                    component.addMouseListener(this.pma);
                    component.addMouseMotionListener(this.pma);
                    if (on1dot4) {
                        try {
                            this.window.getClass().getMethod("addMouseWheelListener", Class.forName("java.awt.event.MouseWheelListener")).invoke(component, this.pmwl);
                        } catch (Exception e) {
                        }
                    }
                    this.grabbed.addElement(component);
                    if (component instanceof Container) {
                        grabContainer((Container) component);
                    }
                }
            }
        }
    }

    void ungrabContainers() {
        int size = this.grabbed.size();
        for (int i = 0; i < size; i++) {
            Window window = (Component) this.grabbed.elementAt(i);
            if (window instanceof Window) {
                window.removeWindowListener(this.pwa);
                window.removeComponentListener(this.pca);
            } else {
                window.removeMouseListener(this.pma);
                window.removeMouseMotionListener(this.pma);
                if (on1dot4) {
                    try {
                        this.window.getClass().getMethod("removeMouseWheelListener", Class.forName("java.awt.event.MouseWheelListener")).invoke(window, this.pmwl);
                    } catch (Exception e) {
                    }
                }
            }
        }
        this.grabbed = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelPopup() {
        Container topMostContainer = getTopMostContainer();
        ungrabContainers();
        JRootPane rootPane = getRootPane();
        if (rootPane != null) {
            rootPane.unregisterKeyboardAction(KeyStroke.getKeyStroke(27, 0));
        } else if (topMostContainer != null) {
            topMostContainer.removeKeyListener(this.pka);
        }
        this.window.removeWindowListener(this.pwa);
        if (on1dot4) {
            try {
                this.window.getClass().getMethod("removeWindowFocusListener", Class.forName("java.awt.event.WindowFocusListener")).invoke(this.window, this.pwa);
            } catch (Exception e) {
            }
        }
        this.window.getRootPane().unregisterKeyboardAction(KeyStroke.getKeyStroke(27, 0));
        this.window.dispose();
        this.window = null;
        this.jheditor = null;
        currentPopup = null;
    }

    private static void debug(Object obj) {
    }
}
