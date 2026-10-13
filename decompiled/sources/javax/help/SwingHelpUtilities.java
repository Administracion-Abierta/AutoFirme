package javax.help;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.ImageIcon;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/* JADX INFO: loaded from: autofirma.jar:javax/help/SwingHelpUtilities.class */
public class SwingHelpUtilities implements PropertyChangeListener {
    private static UIDefaults uiDefaults = null;
    private static SwingHelpUtilities myLAFListener = new SwingHelpUtilities();
    private static String contentViewerUI = null;
    static Object basicOnItemCursor;
    static Object basicDnDCursor;
    static Object gtkOnItemCursor;
    static Object gtkDnDCursor;
    private static final boolean debug = false;
    static Class class$javax$help$HelpUtilities;
    static Class class$javax$help$plaf$basic$BasicHelpUI;
    static Class class$javax$help$plaf$gtk$GTKCursorFactory;
    static Class class$java$beans$PropertyChangeListener;

    static {
        installUIDefaults();
        basicOnItemCursor = new UIDefaults.LazyValue() { // from class: javax.help.SwingHelpUtilities.1
            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.basic.BasicCursorFactory", "getOnItemCursor");
            }
        };
        basicDnDCursor = new UIDefaults.LazyValue() { // from class: javax.help.SwingHelpUtilities.2
            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.basic.BasicCursorFactory", "getDnDCursor");
            }
        };
        gtkOnItemCursor = new UIDefaults.LazyValue() { // from class: javax.help.SwingHelpUtilities.3
            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.gtk.GTKCursorFactory", "getOnItemCursor");
            }
        };
        gtkDnDCursor = new UIDefaults.LazyValue() { // from class: javax.help.SwingHelpUtilities.4
            public Object createValue(UIDefaults uIDefaults) {
                return SwingHelpUtilities.createIcon("javax.help.plaf.gtk.GTKCursorFactory", "getDnDCursor");
            }
        };
    }

    @Override // java.beans.PropertyChangeListener
    public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
        if (propertyChangeEvent.getPropertyName().equals("lookAndFeel")) {
            installLookAndFeelDefaults();
        }
    }

    static void installUIDefaults() {
        UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        if (uiDefaults != lookAndFeelDefaults) {
            uiDefaults = lookAndFeelDefaults;
            UIManager.removePropertyChangeListener(myLAFListener);
            installLookAndFeelDefaults();
            UIManager.addPropertyChangeListener(myLAFListener);
        }
    }

    public static void setContentViewerUI(String str) {
        if (str != null) {
            try {
                Class.forName(str);
                contentViewerUI = str;
            } catch (Throwable th) {
                System.out.println(new StringBuffer().append("ContentViewerClass ").append(str).append(" doesn't exist").toString());
            }
        }
    }

    static void installLookAndFeelDefaults() {
        LookAndFeel lookAndFeel = UIManager.getLookAndFeel();
        UIDefaults lookAndFeelDefaults = UIManager.getLookAndFeelDefaults();
        debug(new StringBuffer().append("installLookAndFeelDefaults - ").append(lookAndFeel).toString());
        if (lookAndFeel != null && lookAndFeelDefaults != null) {
            if (lookAndFeel.getID().equals("Motif")) {
                installMotifDefaults(lookAndFeelDefaults);
            } else if (lookAndFeel.getID().equals("Windows")) {
                installWindowsDefaults(lookAndFeelDefaults);
            } else if (lookAndFeel.getID().equals("GTK")) {
                installGTKDefaults(lookAndFeelDefaults);
            } else {
                installMetalDefaults(lookAndFeelDefaults);
            }
        }
        debug(new StringBuffer().append("verifing UIDefaults; HelpUI=").append(lookAndFeelDefaults.getString("HelpUI")).toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Object createIcon(String str, String str2) {
        Class clsClass$;
        Class<?> clsLoadClass;
        if (class$javax$help$HelpUtilities == null) {
            clsClass$ = class$("javax.help.HelpUtilities");
            class$javax$help$HelpUtilities = clsClass$;
        } else {
            clsClass$ = class$javax$help$HelpUtilities;
        }
        ClassLoader classLoader = clsClass$.getClassLoader();
        try {
            Class<?>[] clsArr = new Class[0];
            Object[] objArr = new Object[0];
            if (classLoader == null) {
                clsLoadClass = Class.forName(str);
            } else {
                clsLoadClass = classLoader.loadClass(str);
            }
            return clsLoadClass.getMethod(str2, clsArr).invoke(null, objArr);
        } catch (Exception e) {
            return null;
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    private static Object makeBasicIcon(String str) {
        return new UIDefaults.LazyValue(str) { // from class: javax.help.SwingHelpUtilities.5
            private final String val$image;

            {
                this.val$image = str;
            }

            public Object createValue(UIDefaults uIDefaults) {
                Class clsClass$;
                if (SwingHelpUtilities.class$javax$help$plaf$basic$BasicHelpUI == null) {
                    clsClass$ = SwingHelpUtilities.class$("javax.help.plaf.basic.BasicHelpUI");
                    SwingHelpUtilities.class$javax$help$plaf$basic$BasicHelpUI = clsClass$;
                } else {
                    clsClass$ = SwingHelpUtilities.class$javax$help$plaf$basic$BasicHelpUI;
                }
                return SwingHelpUtilities.getImageIcon(clsClass$, this.val$image);
            }
        };
    }

    private static void installBasicDefaults(UIDefaults uIDefaults) {
        String string = new StringBuffer().append("javax.help.plaf.basic.").append("BasicContentViewerUI").toString();
        if (contentViewerUI != null) {
            string = contentViewerUI;
        }
        uIDefaults.putDefaults(new Object[]{"HelpUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicHelpUI").toString(), "HelpTOCNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicTOCNavigatorUI").toString(), "HelpIndexNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicIndexNavigatorUI").toString(), "HelpSearchNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicSearchNavigatorUI").toString(), "HelpGlossaryNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicGlossaryNavigatorUI").toString(), "HelpFavoritesNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicFavoritesNavigatorUI").toString(), "HelpContentViewerUI", string, "HelpDnDCursor", basicDnDCursor, "HelpOnItemCursor", basicOnItemCursor, "BackAction.icon", makeBasicIcon("images/Back.gif"), "ForwardAction.icon", makeBasicIcon("images/Forward.gif"), "PrintAction.icon", makeBasicIcon("images/Print.gif"), "PrintSetupAction.icon", makeBasicIcon("images/PrintSetup.gif"), "ReloadAction.icon", makeBasicIcon("images/Reload.gif"), "FavoritesAction.icon", makeBasicIcon("images/Favorites.gif"), "HomeAction.icon", makeBasicIcon("images/Home.gif"), "FavoritesNav.icon", makeBasicIcon("images/FavoritesNav.gif"), "IndexNav.icon", makeBasicIcon("images/IndexNav.gif"), "TOCNav.icon", makeBasicIcon("images/TOCNav.gif"), "SearchNav.icon", makeBasicIcon("images/SearchNav.gif"), "GlossaryNav.icon", makeBasicIcon("images/GlossaryNav.gif"), "HistoryNav.icon", makeBasicIcon("images/HistoryNav.gif"), "SearchLow.icon", makeBasicIcon("images/SearchLow.gif"), "SearchMedLow.icon", makeBasicIcon("images/SearchMedLow.gif"), "SearchMed.icon", makeBasicIcon("images/SearchMed.gif"), "SearchMedHigh.icon", makeBasicIcon("images/SearchMedHigh.gif"), "SearchHigh.icon", makeBasicIcon("images/SearchHigh.gif")});
    }

    private static void installMetalDefaults(UIDefaults uIDefaults) {
        installBasicDefaults(uIDefaults);
    }

    private static void installWindowsDefaults(UIDefaults uIDefaults) {
        installBasicDefaults(uIDefaults);
    }

    private static void installMotifDefaults(UIDefaults uIDefaults) {
        installBasicDefaults(uIDefaults);
    }

    private static Object makeGTKIcon(String str) {
        return new UIDefaults.LazyValue(str) { // from class: javax.help.SwingHelpUtilities.6
            private final String val$image;

            {
                this.val$image = str;
            }

            public Object createValue(UIDefaults uIDefaults) {
                Class clsClass$;
                if (SwingHelpUtilities.class$javax$help$plaf$gtk$GTKCursorFactory == null) {
                    clsClass$ = SwingHelpUtilities.class$("javax.help.plaf.gtk.GTKCursorFactory");
                    SwingHelpUtilities.class$javax$help$plaf$gtk$GTKCursorFactory = clsClass$;
                } else {
                    clsClass$ = SwingHelpUtilities.class$javax$help$plaf$gtk$GTKCursorFactory;
                }
                return SwingHelpUtilities.getImageIcon(clsClass$, this.val$image);
            }
        };
    }

    private static void installGTKDefaults(UIDefaults uIDefaults) {
        String string = new StringBuffer().append("javax.help.plaf.basic.").append("BasicContentViewerUI").toString();
        if (contentViewerUI != null) {
            string = contentViewerUI;
        }
        uIDefaults.putDefaults(new Object[]{"HelpUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicHelpUI").toString(), "HelpTOCNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicTOCNavigatorUI").toString(), "HelpIndexNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicIndexNavigatorUI").toString(), "HelpSearchNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicSearchNavigatorUI").toString(), "HelpGlossaryNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicGlossaryNavigatorUI").toString(), "HelpFavoritesNavigatorUI", new StringBuffer().append("javax.help.plaf.basic.").append("BasicFavoritesNavigatorUI").toString(), "HelpContentViewerUI", string, "HelpDnDCursor", gtkDnDCursor, "HelpOnItemCursor", gtkOnItemCursor, "BackAction.icon", makeGTKIcon("images/Back.png"), "ForwardAction.icon", makeGTKIcon("images/Forward.png"), "PrintAction.icon", makeGTKIcon("images/Print.png"), "PrintSetupAction.icon", makeGTKIcon("images/PrintSetup.png"), "ReloadAction.icon", makeGTKIcon("images/Reload.png"), "FavoritesAction.icon", makeGTKIcon("images/Favorites.png"), "HomeAction.icon", makeGTKIcon("images/Home.png"), "FavoritesNav.icon", makeGTKIcon("images/FavoritesNav.png"), "IndexNav.icon", makeGTKIcon("images/IndexNav.gif"), "TOCNav.icon", makeGTKIcon("images/TOCNav.gif"), "SearchNav.icon", makeGTKIcon("images/SearchNav.gif"), "GlossaryNav.icon", makeGTKIcon("images/GlossaryNav.gif"), "HistoryNav.icon", makeGTKIcon("images/HistoryNav.gif"), "SearchLow.icon", makeGTKIcon("images/SearchLow.gif"), "SearchMedLow.icon", makeGTKIcon("images/SearchMedLow.gif"), "SearchMed.icon", makeGTKIcon("images/SearchMed.gif"), "SearchMedHigh.icon", makeGTKIcon("images/SearchMedHigh.gif"), "SearchHigh.icon", makeGTKIcon("images/SearchHigh.gif")});
    }

    public static ImageIcon getImageIcon(Class cls, String str) {
        if (str == null) {
            return null;
        }
        byte[][] bArr = new byte[1][];
        try {
            InputStream resourceAsStream = cls.getResourceAsStream(str);
            if (resourceAsStream == null) {
                return null;
            }
            BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceAsStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
            bArr[0] = new byte[1024];
            while (true) {
                int i = bufferedInputStream.read(bArr[0]);
                if (i <= 0) {
                    break;
                }
                byteArrayOutputStream.write(bArr[0], 0, i);
            }
            bufferedInputStream.close();
            byteArrayOutputStream.flush();
            bArr[0] = byteArrayOutputStream.toByteArray();
            if (bArr[0] == null) {
                System.err.println(new StringBuffer().append(cls.getName()).append("/").append(str).append(" not found.").toString());
                return null;
            }
            if (bArr[0].length == 0) {
                System.err.println(new StringBuffer().append("warning: ").append(str).append(" is zero-length").toString());
                return null;
            }
            return new ImageIcon(bArr[0]);
        } catch (IOException e) {
            System.err.println(e.toString());
            return null;
        }
    }

    static void addPropertyChangeListener(Object obj, PropertyChangeListener propertyChangeListener) {
        Class<?> clsClass$;
        try {
            Class<?>[] clsArr = new Class[1];
            if (class$java$beans$PropertyChangeListener == null) {
                clsClass$ = class$("java.beans.PropertyChangeListener");
                class$java$beans$PropertyChangeListener = clsClass$;
            } else {
                clsClass$ = class$java$beans$PropertyChangeListener;
            }
            clsArr[0] = clsClass$;
            obj.getClass().getMethod("addPropertyChangeListener", clsArr).invoke(obj, propertyChangeListener);
        } catch (Exception e) {
        }
    }

    private static void debug(Object obj) {
    }
}
