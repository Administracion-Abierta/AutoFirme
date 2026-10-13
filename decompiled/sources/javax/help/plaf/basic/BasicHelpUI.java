package javax.help.plaf.basic;

import com.aowagie.text.ElementTags;
import com.sun.jna.platform.win32.WinNT;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.BackAction;
import javax.help.FavoritesAction;
import javax.help.ForwardAction;
import javax.help.HelpAction;
import javax.help.HelpHistoryModel;
import javax.help.HelpModel;
import javax.help.HelpSet;
import javax.help.HelpUtilities;
import javax.help.JHelp;
import javax.help.JHelpFavoritesNavigator;
import javax.help.JHelpNavigator;
import javax.help.PrintAction;
import javax.help.PrintSetupAction;
import javax.help.SeparatorAction;
import javax.help.SwingHelpUtilities;
import javax.help.plaf.HelpUI;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicHelpUI.class */
public class BasicHelpUI extends HelpUI implements PropertyChangeListener, Serializable {
    protected JHelp help;
    protected JToolBar toolbar;
    protected JSplitPane splitPane;
    protected JTabbedPane tabbedPane;
    private static Dimension PREF_SIZE = new Dimension(600, 600);
    private static Dimension MIN_SIZE = new Dimension(300, 200);
    static boolean noPageSetup;
    private static boolean debug;
    static Class class$java$awt$datatransfer$DataFlavor;
    static Class class$java$lang$String;
    static Class class$java$beans$PropertyChangeListener;
    static Class class$javax$help$plaf$basic$BasicHelpUI;
    protected Vector navs = new Vector();
    private int dividerLocation = 0;
    private final double dividerLocationRatio = 0.3d;
    private JHelpFavoritesNavigator favorites = null;

    static {
        boolean z;
        Class clsClass$;
        noPageSetup = false;
        try {
            if (class$java$awt$datatransfer$DataFlavor == null) {
                clsClass$ = class$("java.awt.datatransfer.DataFlavor");
                class$java$awt$datatransfer$DataFlavor = clsClass$;
            } else {
                clsClass$ = class$java$awt$datatransfer$DataFlavor;
            }
            z = clsClass$.getMethod("getTextPlainUnicodeFlavor", (Class[]) null) == null;
        } catch (NoSuchMethodException e) {
            z = true;
        }
        if (z) {
            String[] strArr = {""};
            strArr[0] = System.getProperty("os.name");
            if (strArr[0] != null && (strArr[0].indexOf("Solaris") != -1 || strArr[0].indexOf("SunOS") != -1 || strArr[0].indexOf("Linux") != -1 || strArr[0].indexOf("HP-UX") != -1)) {
                noPageSetup = true;
            }
        }
        debug = false;
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public static ComponentUI createUI(JComponent jComponent) {
        return new BasicHelpUI((JHelp) jComponent);
    }

    public BasicHelpUI(JHelp jHelp) {
        debug("createUI - sort of");
    }

    public void installUI(JComponent jComponent) {
        debug("installUI");
        this.help = (JHelp) jComponent;
        this.help.setLayout(new BorderLayout());
        this.help.addPropertyChangeListener(this);
        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setVisible(false);
        this.splitPane = new JSplitPane(1, false, this.tabbedPane, this.help.getContentViewer());
        this.splitPane.setOneTouchExpandable(true);
        this.help.add("Center", this.splitPane);
        JHelpNavigator jHelpNavigator = null;
        Enumeration helpNavigators = this.help.getHelpNavigators();
        while (helpNavigators.hasMoreElements()) {
            JHelpNavigator jHelpNavigator2 = (JHelpNavigator) helpNavigators.nextElement();
            if (jHelpNavigator2 instanceof JHelpFavoritesNavigator) {
                this.favorites = (JHelpFavoritesNavigator) jHelpNavigator2;
            }
            addNavigator(jHelpNavigator2);
            if (jHelpNavigator == null) {
                jHelpNavigator = jHelpNavigator2;
            }
        }
        debug("setting the current Navigator");
        if (jHelpNavigator != null) {
            setCurrentNavigator(jHelpNavigator);
        }
        this.toolbar = createToolBar(HelpUtilities.getLocale(jComponent));
        if (this.toolbar != null) {
            this.toolbar.setFloatable(false);
            this.help.add("North", this.toolbar);
        }
        rebuild();
    }

    protected JToolBar createToolBar(Locale locale) {
        this.toolbar = new JToolBar();
        Enumeration enumerationCreateDefaultActions = null;
        HelpSet.Presentation helpSetPresentation = this.help.getHelpSetPresentation();
        if (helpSetPresentation != null && helpSetPresentation.isToolbar()) {
            enumerationCreateDefaultActions = helpSetPresentation.getHelpActions(getModel().getHelpSet(), this.help);
        }
        if (enumerationCreateDefaultActions == null || !enumerationCreateDefaultActions.hasMoreElements()) {
            enumerationCreateDefaultActions = createDefaultActions();
        }
        while (enumerationCreateDefaultActions.hasMoreElements()) {
            HelpAction helpAction = (HelpAction) enumerationCreateDefaultActions.nextElement();
            if (helpAction instanceof SeparatorAction) {
                this.toolbar.addSeparator();
            } else {
                this.toolbar.add(new HelpButton(this, helpAction));
            }
        }
        return this.toolbar;
    }

    private Enumeration createDefaultActions() {
        Vector vector = new Vector(5);
        vector.add(new BackAction(this.help));
        vector.add(new ForwardAction(this.help));
        vector.add(new SeparatorAction(this.help));
        vector.add(new PrintAction(this.help));
        vector.add(new PrintSetupAction(this.help));
        vector.add(new SeparatorAction(this.help));
        if (this.favorites != null) {
            vector.add(new FavoritesAction(this.help));
        }
        return vector.elements();
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/plaf/basic/BasicHelpUI$HelpButton.class */
    private class HelpButton extends JButton implements PropertyChangeListener {
        private final BasicHelpUI this$0;

        HelpButton(BasicHelpUI basicHelpUI, HelpAction helpAction) {
            this.this$0 = basicHelpUI;
            setEnabled(helpAction.isEnabled());
            Icon icon = (Icon) helpAction.getValue("icon");
            setIcon(icon == null ? UIManager.getIcon("HelpAction.icon") : icon);
            try {
                basicHelpUI.help.getModel().getHelpSet().getLocale();
            } catch (NullPointerException e) {
                Locale.getDefault();
            }
            setToolTipText((String) helpAction.getValue("tooltip"));
            getAccessibleContext().setAccessibleName((String) helpAction.getValue("access"));
            if (helpAction instanceof MouseListener) {
                addMouseListener((MouseListener) helpAction);
            }
            if (helpAction instanceof ActionListener) {
                addActionListener((ActionListener) helpAction);
            }
            helpAction.addPropertyChangeListener(this);
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
            if (propertyChangeEvent.getPropertyName().equals("enabled")) {
                setEnabled(((Boolean) propertyChangeEvent.getNewValue()).booleanValue());
            }
        }

        private boolean createEnablePropertyChangeSupport(HelpAction helpAction) {
            Class<?> clsClass$;
            Class<?> clsClass$2;
            boolean z = false;
            try {
                Class<?>[] clsArr = new Class[2];
                if (BasicHelpUI.class$java$lang$String == null) {
                    clsClass$ = BasicHelpUI.class$("java.lang.String");
                    BasicHelpUI.class$java$lang$String = clsClass$;
                } else {
                    clsClass$ = BasicHelpUI.class$java$lang$String;
                }
                clsArr[0] = clsClass$;
                if (BasicHelpUI.class$java$beans$PropertyChangeListener == null) {
                    clsClass$2 = BasicHelpUI.class$("java.beans.PropertyChangeListener");
                    BasicHelpUI.class$java$beans$PropertyChangeListener = clsClass$2;
                } else {
                    clsClass$2 = BasicHelpUI.class$java$beans$PropertyChangeListener;
                }
                clsArr[1] = clsClass$2;
                helpAction.getClass().getMethod("addPropertyChangeListener", clsArr).invoke(helpAction, "enabled", this);
                z = true;
            } catch (Exception e) {
            }
            return z;
        }

        private boolean createPropertyChangeSupport(HelpAction helpAction) {
            Class<?> clsClass$;
            boolean z = false;
            try {
                Class<?>[] clsArr = new Class[1];
                if (BasicHelpUI.class$java$beans$PropertyChangeListener == null) {
                    clsClass$ = BasicHelpUI.class$("java.beans.PropertyChangeListener");
                    BasicHelpUI.class$java$beans$PropertyChangeListener = clsClass$;
                } else {
                    clsClass$ = BasicHelpUI.class$java$beans$PropertyChangeListener;
                }
                clsArr[0] = clsClass$;
                helpAction.getClass().getMethod("addPropertyChangeListener", clsArr).invoke(helpAction, this);
                z = true;
            } catch (Exception e) {
            }
            return z;
        }
    }

    public void uninstallUI(JComponent jComponent) {
        debug("uninstallUI");
        this.help.removePropertyChangeListener(this);
        this.help.setLayout(null);
        this.help.removeAll();
        if (getModel() != null) {
        }
        this.help = null;
        this.toolbar = null;
    }

    public Dimension getPreferredSize(JComponent jComponent) {
        return PREF_SIZE;
    }

    public Dimension getMinimumSize(JComponent jComponent) {
        return MIN_SIZE;
    }

    public Dimension getMaximumSize(JComponent jComponent) {
        return new Dimension(WinNT.MAXLONG, WinNT.MAXLONG);
    }

    private void rebuild() {
        HelpModel model = getModel();
        if (model == null) {
            return;
        }
        HelpHistoryModel historyModel = getHistoryModel();
        if (historyModel != null) {
            historyModel.discard();
        }
        try {
            if (model.getCurrentID() == null) {
                HelpSet helpSet = model.getHelpSet();
                model.setCurrentID(helpSet.getHomeID(), HelpUtilities.getString(helpSet.getLocale(), "history.homePage"), null);
            }
        } catch (Exception e) {
        }
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        Object source = propertyChangeEvent.getSource();
        String propertyName = propertyChangeEvent.getPropertyName();
        debug(new StringBuffer().append("propertyChange: ").append(propertyName).toString());
        if (source == this.help) {
            if (propertyName.equals("helpModel")) {
                rebuild();
                return;
            }
            if (propertyName.equals(ElementTags.FONT)) {
                debug("Font change");
                Font font = (Font) propertyChangeEvent.getNewValue();
                this.help.getContentViewer().setFont(font);
                this.help.getContentViewer().invalidate();
                Enumeration helpNavigators = this.help.getHelpNavigators();
                while (helpNavigators.hasMoreElements()) {
                    ((JHelpNavigator) helpNavigators.nextElement()).setFont(font);
                }
                return;
            }
            if (propertyName.equals("navigatorDisplayed")) {
                if (((Boolean) propertyChangeEvent.getNewValue()).booleanValue()) {
                    this.help.add("Center", this.splitPane);
                    return;
                } else {
                    this.help.add("Center", this.help.getContentViewer());
                    return;
                }
            }
            if (propertyName.equals("toolbarDisplayed")) {
                this.toolbar.setVisible(((Boolean) propertyChangeEvent.getNewValue()).booleanValue());
            }
        }
    }

    protected HelpModel getModel() {
        if (this.help == null) {
            return null;
        }
        return this.help.getModel();
    }

    protected HelpHistoryModel getHistoryModel() {
        if (this.help == null) {
            return null;
        }
        return this.help.getHistoryModel();
    }

    @Override // javax.help.plaf.HelpUI
    public void addNavigator(JHelpNavigator jHelpNavigator) {
        debug("addNavigator");
        this.navs.addElement(jHelpNavigator);
        Icon icon = null;
        HelpSet.Presentation helpSetPresentation = this.help.getHelpSetPresentation();
        if (helpSetPresentation == null || helpSetPresentation.isViewImagesDisplayed()) {
            icon = jHelpNavigator.getIcon();
        }
        if (icon != null) {
            this.tabbedPane.addTab("", icon, jHelpNavigator, jHelpNavigator.getNavigatorLabel());
        } else {
            String navigatorLabel = jHelpNavigator.getNavigatorLabel();
            if (navigatorLabel == null) {
                navigatorLabel = "<unknown>";
            }
            this.tabbedPane.addTab(navigatorLabel, icon, jHelpNavigator);
        }
        jHelpNavigator.setVisible(false);
        this.tabbedPane.setVisible(this.help.isNavigatorDisplayed());
        this.help.invalidate();
        SwingUtilities.invokeLater(new Runnable(this) { // from class: javax.help.plaf.basic.BasicHelpUI.1
            private final BasicHelpUI this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                if (this.this$0.dividerLocation == 0.0d) {
                    Dimension size = this.this$0.splitPane.getSize();
                    if (size.width != 0) {
                        this.this$0.splitPane.setDividerLocation((int) (((double) (size.width - this.this$0.splitPane.getDividerSize())) * 0.3d));
                    }
                    this.this$0.dividerLocation = this.this$0.splitPane.getDividerLocation();
                }
            }
        });
    }

    @Override // javax.help.plaf.HelpUI
    public void removeNavigator(JHelpNavigator jHelpNavigator) {
        debug("removeNavigator");
        this.navs.removeElement(jHelpNavigator);
        this.tabbedPane.remove(jHelpNavigator);
        this.help.invalidate();
    }

    public Enumeration getHelpNavigators() {
        return this.navs.elements();
    }

    @Override // javax.help.plaf.HelpUI
    public void setCurrentNavigator(JHelpNavigator jHelpNavigator) {
        try {
            this.tabbedPane.setSelectedComponent(jHelpNavigator);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("JHelpNavigator must be added first");
        }
    }

    @Override // javax.help.plaf.HelpUI
    public JHelpNavigator getCurrentNavigator() {
        return this.tabbedPane.getSelectedComponent();
    }

    private ImageIcon getIcon(String str) {
        Class clsClass$;
        if (class$javax$help$plaf$basic$BasicHelpUI == null) {
            clsClass$ = class$("javax.help.plaf.basic.BasicHelpUI");
            class$javax$help$plaf$basic$BasicHelpUI = clsClass$;
        } else {
            clsClass$ = class$javax$help$plaf$basic$BasicHelpUI;
        }
        return getIcon(clsClass$, str);
    }

    public static ImageIcon getIcon(Class cls, String str) {
        ImageIcon imageIcon = null;
        try {
            imageIcon = SwingHelpUtilities.getImageIcon(cls, str);
        } catch (Exception e) {
        }
        if (debug || imageIcon == null) {
            System.err.println("GetIcon");
            System.err.println(new StringBuffer().append("  name: ").append(str).toString());
            System.err.println(new StringBuffer().append("  klass: ").append(cls).toString());
            System.err.println(new StringBuffer().append("  URL is ").append(cls.getResource(str)).toString());
            System.err.println(new StringBuffer().append("  ImageIcon is ").append(imageIcon).toString());
        }
        return imageIcon;
    }

    private static void debug(String str) {
        if (debug) {
            System.out.println(new StringBuffer().append("BasicHelpUI: ").append(str).toString());
        }
    }
}
