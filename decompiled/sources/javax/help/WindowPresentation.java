package javax.help;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/* JADX INFO: loaded from: autofirma.jar:javax/help/WindowPresentation.class */
public abstract class WindowPresentation extends Presentation {
    WindowListener dl;
    private static final boolean debug = false;
    static Class class$java$awt$Frame;
    static Class class$java$awt$Window;
    private HelpSet.Presentation hsPres = null;
    private JFrame frame = null;
    private JHelp jhelp = null;
    private JDialog dialog = null;
    private Window ownerWindow = null;
    private boolean modallyActivated = false;
    private Point location = null;
    private String title = null;
    private Image image = null;
    private String currentView = null;
    private boolean viewDisplayed = true;
    private boolean toolbarDisplayed = true;
    private boolean destroyOnExit = false;
    private boolean titleFromDocument = false;
    private WindowPropertyChangeListener propertyChangeListener = null;
    private int screen = 0;
    boolean modalDeactivated = true;

    public WindowPresentation(HelpSet helpSet) {
        setHelpSet(helpSet);
    }

    @Override // javax.help.Presentation
    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        debug("setHelpSetPrsentation");
        if (presentation == null) {
            return;
        }
        super.setHelpSetPresentation(presentation);
        Point location = presentation.getLocation();
        if (location != null) {
            setLocation(location);
        }
        String title = presentation.getTitle();
        if (title != null) {
            setTitle(title);
        }
        Map.ID imageID = presentation.getImageID();
        if (imageID != null) {
            try {
                this.image = new ImageIcon(getHelpSet().getCombinedMap().getURLFromID(imageID)).getImage();
            } catch (Exception e) {
            }
        }
        if (presentation.isToolbar()) {
            setToolbarDisplayed(true);
        }
        if (presentation.isViewDisplayed()) {
            setViewDisplayed(true);
        }
        this.hsPres = presentation;
    }

    public HelpSet.Presentation getHelpSetPresentation() {
        return this.hsPres;
    }

    public Window getActivationWindow() {
        debug("getActivationWindow");
        return this.ownerWindow;
    }

    public void setActivationWindow(Window window) {
        debug("setActivationWindow");
        if (window != null && (window instanceof Dialog)) {
            if (((Dialog) window).isModal()) {
                this.ownerWindow = window;
                this.modallyActivated = true;
                return;
            } else {
                this.ownerWindow = null;
                this.modallyActivated = false;
                return;
            }
        }
        this.ownerWindow = null;
        this.modallyActivated = false;
    }

    public void setActivationObject(Object obj) {
        debug("setActivationObject");
        while (obj instanceof MenuComponent) {
            obj = ((MenuComponent) obj).getParent();
        }
        Window windowWindowForComponent = null;
        if (obj instanceof Frame) {
            windowWindowForComponent = (Window) obj;
        } else if (obj instanceof Component) {
            windowWindowForComponent = SwingUtilities.windowForComponent((Component) obj);
        }
        setActivationWindow(windowWindowForComponent);
    }

    public String getCurrentView() {
        debug("getCurrentView");
        if (this.jhelp != null) {
            this.currentView = this.jhelp.getCurrentNavigator().getNavigatorName();
        }
        return this.currentView;
    }

    public void setCurrentView(String str) {
        debug("setCurrentView");
        if (this.jhelp != null) {
            JHelpNavigator navigatorByName = getNavigatorByName(str);
            if (navigatorByName == null) {
                throw new IllegalArgumentException("Invalid view name");
            }
            this.jhelp.setCurrentNavigator(navigatorByName);
        } else if (getHelpSet().getNavigatorView(str) == null) {
            throw new IllegalArgumentException("Invalid view name");
        }
        this.currentView = str;
    }

    private JHelpNavigator getNavigatorByName(String str) {
        JHelpNavigator jHelpNavigator = null;
        if (this.jhelp != null) {
            Enumeration helpNavigators = this.jhelp.getHelpNavigators();
            while (helpNavigators.hasMoreElements()) {
                jHelpNavigator = (JHelpNavigator) helpNavigators.nextElement();
                if (jHelpNavigator.getNavigatorName().equals(str)) {
                    break;
                }
                jHelpNavigator = null;
            }
        }
        return jHelpNavigator;
    }

    public boolean isDestroyedOnExit() {
        debug("isDestoryedOnExit");
        return this.destroyOnExit;
    }

    public void setDestroyOnExit(boolean z) {
        debug("setDestoryOnExit");
        this.destroyOnExit = z;
    }

    public void destroy() {
        this.frame = null;
        this.jhelp = null;
        this.dialog = null;
        this.ownerWindow = null;
        this.location = null;
        this.title = null;
        this.currentView = null;
        this.propertyChangeListener = null;
        this.screen = 0;
    }

    @Override // javax.help.Presentation
    public void setHelpSet(HelpSet helpSet) {
        debug("setHelpSet");
        HelpSet helpSet2 = super.getHelpSet();
        if (helpSet != null && helpSet2 != helpSet) {
            super.setHelpSet(helpSet);
            if (this.jhelp != null) {
                this.jhelp.setModel(super.getHelpModel());
            }
        }
    }

    @Override // javax.help.Presentation
    public void setDisplayed(boolean z) {
        Class clsClass$;
        debug("setDisplayed");
        if (this.jhelp != null || z) {
            createHelpWindow();
            if (this.modallyActivated) {
                if (z) {
                    this.dialog.show();
                    return;
                } else {
                    this.dialog.hide();
                    return;
                }
            }
            this.frame.setVisible(z);
            try {
                Class<?>[] clsArr = {Integer.TYPE};
                if (class$java$awt$Frame == null) {
                    clsClass$ = class$("java.awt.Frame");
                    class$java$awt$Frame = clsClass$;
                } else {
                    clsClass$ = class$java$awt$Frame;
                }
                Method method = clsClass$.getMethod("setState", clsArr);
                if (method != null) {
                    method.invoke(this.frame, new Integer(0));
                }
            } catch (IllegalAccessException e) {
            } catch (NoSuchMethodError e2) {
            } catch (NoSuchMethodException e3) {
            } catch (InvocationTargetException e4) {
            }
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // javax.help.Presentation
    public boolean isDisplayed() {
        Class clsClass$;
        debug("isDisplayed");
        if (this.jhelp == null) {
            return false;
        }
        if (this.modallyActivated) {
            if (this.dialog != null) {
                return this.dialog.isShowing();
            }
            return false;
        }
        if (this.frame == null || !this.frame.isShowing()) {
            return false;
        }
        try {
            if (class$java$awt$Frame == null) {
                clsClass$ = class$("java.awt.Frame");
                class$java$awt$Frame = clsClass$;
            } else {
                clsClass$ = class$java$awt$Frame;
            }
            Method method = clsClass$.getMethod("getState", (Class[]) null);
            return method == null || ((Integer) method.invoke(this.frame, (Object[]) null)).intValue() == 0;
        } catch (IllegalAccessException e) {
            return true;
        } catch (NoSuchMethodError e2) {
            return true;
        } catch (NoSuchMethodException e3) {
            return true;
        } catch (InvocationTargetException e4) {
            return true;
        }
    }

    @Override // javax.help.Presentation
    public void setFont(Font font) {
        debug("setFont");
        super.setFont(font);
        if (this.jhelp != null && font != null) {
            this.jhelp.setFont(font);
        }
    }

    @Override // javax.help.Presentation
    public Font getFont() {
        debug("getFont");
        Font font = super.getFont();
        if (font == null) {
            if (this.jhelp == null) {
                createHelpWindow();
            }
            return this.jhelp.getFont();
        }
        return font;
    }

    @Override // javax.help.Presentation
    public void setLocale(Locale locale) {
        debug("setLocale");
        super.setLocale(locale);
        if (this.jhelp != null) {
            this.jhelp.setLocale(locale);
        }
    }

    private boolean isXinerama() {
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screenDevices.length == 1) {
            return false;
        }
        for (GraphicsDevice graphicsDevice : screenDevices) {
            Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
            if (bounds.x != 0 || bounds.y != 0) {
                return true;
            }
        }
        return false;
    }

    public Point getLocation() {
        debug("getLocation");
        if (this.location != null && this.jhelp == null) {
            return this.location;
        }
        if (this.jhelp == null) {
            createHelpWindow();
        }
        if (this.modallyActivated) {
            Point location = this.dialog.getLocation();
            if (isXinerama()) {
                Rectangle bounds = this.dialog.getGraphicsConfiguration().getBounds();
                return new Point(location.x - bounds.x, location.y - bounds.y);
            }
            return location;
        }
        Point location2 = this.frame.getLocation();
        if (isXinerama()) {
            Rectangle bounds2 = this.frame.getGraphicsConfiguration().getBounds();
            return new Point(location2.x - bounds2.x, location2.y - bounds2.y);
        }
        return location2;
    }

    public void setLocation(Point point) {
        debug("setLocation");
        this.location = point;
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                if (this.dialog != null) {
                    Rectangle bounds = this.dialog.getGraphicsConfiguration().getBounds();
                    this.dialog.setLocation(new Point(bounds.x + point.x, bounds.y + point.y));
                    return;
                }
                return;
            }
            if (this.frame != null) {
                Rectangle bounds2 = this.frame.getGraphicsConfiguration().getBounds();
                this.frame.setLocation(new Point(bounds2.x + point.x, bounds2.y + point.y));
            }
        }
    }

    public int getScreen() {
        debug("getScreen");
        if (this.jhelp == null) {
            return this.screen;
        }
        GraphicsConfiguration graphicsConfiguration = null;
        if (this.modallyActivated) {
            if (this.dialog != null) {
                graphicsConfiguration = this.dialog.getGraphicsConfiguration();
            }
        } else if (this.frame != null) {
            graphicsConfiguration = this.frame.getGraphicsConfiguration();
        }
        if (graphicsConfiguration != null) {
            GraphicsDevice device = graphicsConfiguration.getDevice();
            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            for (int i = 0; i < screenDevices.length; i++) {
                if (screenDevices[i] == device) {
                    this.screen = i;
                    return this.screen;
                }
            }
        }
        return this.screen;
    }

    public void setScreen(int i) {
        debug("setScreen");
        if (i == this.screen) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("Invalid screen");
        }
        GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        if (screenDevices.length <= i) {
            throw new IllegalArgumentException("Invalid Screen");
        }
        this.screen = i;
        if (this.jhelp != null) {
            boolean zIsXinerama = isXinerama();
            Rectangle bounds = screenDevices[i].getDefaultConfiguration().getBounds();
            if (this.modallyActivated) {
                if (this.dialog != null) {
                    if (zIsXinerama) {
                        Point location = getLocation();
                        this.dialog.setLocation(new Point(bounds.x + location.x, bounds.y + location.y));
                        return;
                    } else {
                        this.location = getLocation();
                        this.dialog.hide();
                        this.dialog = null;
                        createHelpWindow();
                        return;
                    }
                }
                return;
            }
            if (this.frame != null) {
                if (zIsXinerama) {
                    Point location2 = getLocation();
                    this.frame.setLocation(new Point(bounds.x + location2.x, bounds.y + location2.y));
                } else {
                    this.location = getLocation();
                    this.frame.setVisible(false);
                    this.frame = null;
                    createHelpWindow();
                }
            }
        }
    }

    @Override // javax.help.Presentation
    public Dimension getSize() {
        debug("getSize");
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                if (this.dialog != null) {
                    return this.dialog.getSize();
                }
            } else if (this.frame != null) {
                return this.frame.getSize();
            }
        }
        return super.getSize();
    }

    @Override // javax.help.Presentation
    public void setSize(Dimension dimension) {
        debug("setSize");
        super.setSize(dimension);
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                this.dialog.setSize(dimension);
                this.dialog.validate();
            } else {
                this.frame.setSize(dimension);
                this.frame.validate();
            }
        }
    }

    public String getTitle() {
        String documentTitle;
        debug("getTitle");
        if (this.titleFromDocument && this.jhelp != null && (documentTitle = this.jhelp.getContentViewer().getDocumentTitle()) != null) {
            return documentTitle;
        }
        if (this.title != null) {
            return this.title;
        }
        HelpSet helpSet = getHelpSet();
        if (helpSet != null) {
            this.title = helpSet.getTitle();
        }
        return this.title;
    }

    public void setTitle(String str) {
        debug("setTitle");
        this.title = str;
        if (this.jhelp != null) {
            if (this.modallyActivated) {
                this.dialog.setTitle(str);
                this.dialog.validate();
            } else {
                this.frame.setTitle(str);
                this.frame.validate();
            }
        }
    }

    public boolean isTitleSetFromDocument() {
        debug("isTitleSetFromDocument");
        return this.titleFromDocument;
    }

    public void setTitleFromDocument(boolean z) {
        debug("setTitleFromDocument");
        if (this.titleFromDocument != z) {
            this.titleFromDocument = z;
            if (this.titleFromDocument) {
                this.propertyChangeListener = new WindowPropertyChangeListener(this, null);
                if (this.jhelp != null) {
                    this.jhelp.getContentViewer().addPropertyChangeListener("page", this.propertyChangeListener);
                    return;
                }
                return;
            }
            if (this.jhelp != null) {
                this.jhelp.getContentViewer().removePropertyChangeListener("page", this.propertyChangeListener);
            }
        }
    }

    public boolean isViewDisplayed() {
        debug("isViewDisplayed");
        if (this.jhelp != null) {
            return this.jhelp.isNavigatorDisplayed();
        }
        return this.viewDisplayed;
    }

    public void setViewDisplayed(boolean z) {
        debug("setViewDisplayed");
        if (this.jhelp != null) {
            this.jhelp.setNavigatorDisplayed(z);
        }
        this.viewDisplayed = z;
    }

    public boolean isToolbarDisplayed() {
        debug("isToolbarDisplayed");
        if (this.jhelp != null) {
            return this.jhelp.isToolbarDisplayed();
        }
        return this.toolbarDisplayed;
    }

    public void setToolbarDisplayed(boolean z) {
        debug(new StringBuffer().append("setToolbarDisplayed=").append(z).toString());
        if (this.jhelp != null) {
            this.jhelp.setToolbarDisplayed(z);
        }
        this.toolbarDisplayed = z;
    }

    private synchronized void createJHelp() {
        JHelpNavigator navigatorByName;
        debug("createJHelp");
        if (this.jhelp == null) {
            this.jhelp = new JHelp(getHelpModel(), null, getHelpSetPresentation());
            Font font = super.getFont();
            if (font != null) {
                this.jhelp.setFont(font);
            }
            Locale locale = getLocale();
            if (locale != null) {
                this.jhelp.setLocale(locale);
            }
            this.jhelp.setToolbarDisplayed(this.toolbarDisplayed);
            this.jhelp.setNavigatorDisplayed(this.viewDisplayed);
            if (this.currentView != null && (navigatorByName = getNavigatorByName(this.currentView)) != null) {
                this.jhelp.setCurrentNavigator(navigatorByName);
            }
            if (this.titleFromDocument) {
                this.jhelp.getContentViewer().addPropertyChangeListener("page", this.propertyChangeListener);
            }
        }
    }

    public synchronized void createHelpWindow() {
        Class clsClass$;
        debug("createHelpWindow");
        Point location = null;
        Dimension size = getSize();
        JDialog jDialog = null;
        createJHelp();
        GraphicsConfiguration defaultConfiguration = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[this.screen].getDefaultConfiguration();
        Rectangle bounds = defaultConfiguration.getBounds();
        if (!this.modallyActivated) {
            if (this.frame == null) {
                this.frame = new JFrame(getTitle(), defaultConfiguration);
                this.frame.addWindowListener(new WindowAdapter(this) { // from class: javax.help.WindowPresentation.2
                    private final WindowPresentation this$0;

                    public void windowClosing(WindowEvent windowEvent) {
                        if (!this.this$0.destroyOnExit) {
                            this.this$0.frame.setVisible(false);
                        } else {
                            this.this$0.destroy();
                        }
                    }

                    {
                        this.this$0 = this;
                    }

                    public void windowClosed(WindowEvent windowEvent) {
                        this.this$0.frame.setVisible(false);
                        if (this.this$0.destroyOnExit) {
                            this.this$0.destroy();
                        }
                    }
                });
                if (this.image != null) {
                    this.frame.setIconImage(this.image);
                }
            }
            if (this.dialog != null) {
                location = this.dialog.getLocation();
                size = this.dialog.getSize();
                this.dialog.hide();
                this.dialog = null;
                this.ownerWindow = null;
            }
            if (size != null) {
                this.frame.setSize(size);
            } else {
                this.frame.setSize(getSize());
            }
            if (location != null) {
                this.frame.setLocation(location);
            } else if (this.location != null) {
                this.frame.setLocation(isXinerama() ? new Point(bounds.x + this.location.x, bounds.y + this.location.y) : this.location);
            }
            this.frame.getContentPane().add(this.jhelp);
            this.frame.setTitle(getTitle());
            return;
        }
        Window window = null;
        try {
            if (class$java$awt$Window == null) {
                clsClass$ = class$("java.awt.Window");
                class$java$awt$Window = clsClass$;
            } else {
                clsClass$ = class$java$awt$Window;
            }
            Method method = clsClass$.getMethod("getOwner", (Class[]) null);
            if (method != null && this.dialog != null) {
                window = (Window) method.invoke(this.dialog, (Class[]) null);
            }
        } catch (IllegalAccessException e) {
        } catch (NoSuchMethodError e2) {
        } catch (NoSuchMethodException e3) {
        } catch (InvocationTargetException e4) {
        }
        if (this.dialog == null || window != this.ownerWindow || this.modalDeactivated) {
            if (this.frame != null) {
                location = this.frame.getLocation();
                size = this.frame.getSize();
                this.frame = null;
            }
            if (this.dialog != null) {
                location = this.dialog.getLocation();
                size = this.dialog.getSize();
                jDialog = this.dialog;
            }
            this.dialog = new JDialog(this.ownerWindow, getTitle(), false, defaultConfiguration);
            this.dl = new WindowAdapter(this) { // from class: javax.help.WindowPresentation.1
                private final WindowPresentation this$0;

                public void windowClosing(WindowEvent windowEvent) {
                    WindowPresentation.debug("modal window closing");
                    if (!this.this$0.destroyOnExit) {
                        if (this.this$0.dialog.isShowing()) {
                            this.this$0.dialog.hide();
                        }
                        if (this.this$0.ownerWindow != null) {
                            this.this$0.ownerWindow.removeWindowListener(this.this$0.dl);
                        }
                        this.this$0.ownerWindow = null;
                        this.this$0.modalDeactivated = true;
                        return;
                    }
                    this.this$0.destroy();
                }

                {
                    this.this$0 = this;
                }

                public void windowClosed(WindowEvent windowEvent) {
                    WindowPresentation.debug("modal window closing");
                    if (this.this$0.destroyOnExit) {
                        this.this$0.destroy();
                    }
                }
            };
            debug("adding windowlistener");
            this.ownerWindow.addWindowListener(this.dl);
            this.modalDeactivated = false;
            if (size != null) {
                this.dialog.setSize(size);
            } else {
                this.dialog.setSize(getSize());
            }
            if (location != null) {
                this.dialog.setLocation(location);
            } else if (this.location != null) {
                this.dialog.setLocation(isXinerama() ? new Point(bounds.x + this.location.x, bounds.y + this.location.y) : this.location);
            }
            this.dialog.setTitle(getTitle());
            this.dialog.getContentPane().add(this.jhelp);
            if (jDialog != null) {
                jDialog.hide();
            }
        }
    }

    public Window getHelpWindow() {
        if (this.modallyActivated) {
            return this.dialog;
        }
        return this.frame;
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/WindowPresentation$WindowPropertyChangeListener.class */
    private class WindowPropertyChangeListener implements PropertyChangeListener {
        private final WindowPresentation this$0;

        private WindowPropertyChangeListener(WindowPresentation windowPresentation) {
            this.this$0 = windowPresentation;
        }

        WindowPropertyChangeListener(WindowPresentation windowPresentation, AnonymousClass1 anonymousClass1) {
            this(windowPresentation);
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("page")) {
                String title = this.this$0.getTitle();
                if (this.this$0.modallyActivated) {
                    this.this$0.dialog.setTitle(title);
                } else {
                    this.this$0.frame.setTitle(title);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void debug(Object obj) {
    }
}
