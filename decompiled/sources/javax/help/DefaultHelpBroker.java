package javax.help;

import com.aowagie.text.ElementTags;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.URL;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

/* JADX INFO: loaded from: autofirma.jar:javax/help/DefaultHelpBroker.class */
public class DefaultHelpBroker implements HelpBroker, KeyListener {
    private MainWindow mw;
    private HelpSet helpKeyHS = null;
    private String helpKeyPresentation = null;
    private String helpKeyPresentationName = null;
    protected ActionListener displayHelpFromFocus;
    protected ActionListener displayHelpFromSource;
    private static final boolean debug = false;
    static Class class$javax$help$HelpSet;
    static Class class$java$lang$String;

    public DefaultHelpBroker(HelpSet helpSet) {
        this.mw = null;
        this.mw = (MainWindow) MainWindow.getPresentation(helpSet, null);
    }

    public DefaultHelpBroker() {
        this.mw = null;
        this.mw = (MainWindow) MainWindow.getPresentation(null, null);
    }

    public WindowPresentation getWindowPresentation() {
        return this.mw;
    }

    @Override // javax.help.HelpBroker
    public HelpSet getHelpSet() {
        return this.mw.getHelpSet();
    }

    @Override // javax.help.HelpBroker
    public void setHelpSet(HelpSet helpSet) {
        debug("setHelpSet");
        this.mw.setHelpSet(helpSet);
    }

    @Override // javax.help.HelpBroker
    public void setHelpSetPresentation(HelpSet.Presentation presentation) {
        debug("setHelpSetPresentation");
        this.mw.setHelpSetPresentation(presentation);
    }

    @Override // javax.help.HelpBroker
    public Locale getLocale() {
        return this.mw.getLocale();
    }

    @Override // javax.help.HelpBroker
    public void setLocale(Locale locale) {
        this.mw.setLocale(locale);
    }

    @Override // javax.help.HelpBroker
    public Font getFont() {
        return this.mw.getFont();
    }

    @Override // javax.help.HelpBroker
    public void setFont(Font font) {
        this.mw.setFont(font);
    }

    @Override // javax.help.HelpBroker
    public void setCurrentView(String str) {
        this.mw.setCurrentView(str);
    }

    @Override // javax.help.HelpBroker
    public String getCurrentView() {
        return this.mw.getCurrentView();
    }

    @Override // javax.help.HelpBroker
    public void initPresentation() {
        this.mw.createHelpWindow();
    }

    @Override // javax.help.HelpBroker
    public void setDisplayed(boolean z) {
        debug("setDisplayed");
        this.mw.setDisplayed(z);
    }

    @Override // javax.help.HelpBroker
    public boolean isDisplayed() {
        return this.mw.isDisplayed();
    }

    @Override // javax.help.HelpBroker
    public void setLocation(Point point) {
        this.mw.setLocation(point);
    }

    @Override // javax.help.HelpBroker
    public Point getLocation() {
        return this.mw.getLocation();
    }

    @Override // javax.help.HelpBroker
    public void setSize(Dimension dimension) {
        this.mw.setSize(dimension);
    }

    @Override // javax.help.HelpBroker
    public Dimension getSize() throws UnsupportedOperationException {
        return this.mw.getSize();
    }

    @Override // javax.help.HelpBroker
    public void setScreen(int i) {
        this.mw.setScreen(i);
    }

    @Override // javax.help.HelpBroker
    public int getScreen() throws UnsupportedOperationException {
        return this.mw.getScreen();
    }

    @Override // javax.help.HelpBroker
    public void setViewDisplayed(boolean z) {
        this.mw.setViewDisplayed(z);
    }

    @Override // javax.help.HelpBroker
    public boolean isViewDisplayed() {
        return this.mw.isViewDisplayed();
    }

    @Override // javax.help.HelpBroker
    public void showID(String str, String str2, String str3) throws BadIDException {
        debug("showID - string");
        Presentation presentation = getPresentation(str2, str3);
        if (presentation != null) {
            presentation.setCurrentID(str);
            presentation.setDisplayed(true);
        }
    }

    @Override // javax.help.HelpBroker
    public void showID(Map.ID id, String str, String str2) throws InvalidHelpSetContextException {
        debug("showID - ID");
        Presentation presentation = getPresentation(str, str2);
        if (presentation != null) {
            presentation.setCurrentID(id);
            presentation.setDisplayed(true);
        }
    }

    private Presentation getPresentation(String str, String str2) {
        Class<?> clsClass$;
        Class<?> clsClass$2;
        Class<?> clsLoadClass;
        HelpSet helpSet = this.mw.getHelpSet();
        if (helpSet == null) {
            return null;
        }
        Class<?>[] clsArr = new Class[2];
        if (class$javax$help$HelpSet == null) {
            clsClass$ = class$("javax.help.HelpSet");
            class$javax$help$HelpSet = clsClass$;
        } else {
            clsClass$ = class$javax$help$HelpSet;
        }
        clsArr[0] = clsClass$;
        if (class$java$lang$String == null) {
            clsClass$2 = class$("java.lang.String");
            class$java$lang$String = clsClass$2;
        } else {
            clsClass$2 = class$java$lang$String;
        }
        clsArr[1] = clsClass$2;
        Object[] objArr = {helpSet, str2};
        try {
            ClassLoader loader = helpSet.getLoader();
            if (loader == null) {
                clsLoadClass = Class.forName(str);
            } else {
                clsLoadClass = loader.loadClass(str);
            }
            Presentation presentation = (Presentation) clsLoadClass.getMethod("getPresentation", clsArr).invoke(null, objArr);
            if (presentation == null || (presentation instanceof Popup)) {
                return null;
            }
            return presentation;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(new StringBuffer().append(str).append("presentation  invalid").toString());
        } catch (Exception e2) {
            throw new RuntimeException("error invoking presentation");
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    @Override // javax.help.HelpBroker
    public void setCurrentID(String str) throws BadIDException {
        debug("setCurrentID - string");
        this.mw.setCurrentID(str);
    }

    @Override // javax.help.HelpBroker
    public void setCurrentID(Map.ID id) throws InvalidHelpSetContextException {
        debug("setCurrentID - ID");
        this.mw.setCurrentID(id);
    }

    @Override // javax.help.HelpBroker
    public Map.ID getCurrentID() {
        return this.mw.getCurrentID();
    }

    @Override // javax.help.HelpBroker
    public void setCurrentURL(URL url) {
        debug("setCurrentURL");
        this.mw.setCurrentURL(url);
    }

    @Override // javax.help.HelpBroker
    public URL getCurrentURL() {
        return this.mw.getCurrentURL();
    }

    @Override // javax.help.HelpBroker
    public void enableHelpKey(Component component, String str, HelpSet helpSet) {
        enableHelpKey(component, str, helpSet, null, null);
    }

    @Override // javax.help.HelpBroker
    public void enableHelpKey(Component component, String str, HelpSet helpSet, String str2, String str3) {
        ActionListener displayHelpFromFocus;
        if (str == null) {
            throw new NullPointerException(ElementTags.ID);
        }
        if (str2 != null && helpSet == null) {
            throw new IllegalArgumentException("hs");
        }
        CSH.setHelpIDString(component, str);
        if (helpSet != null) {
            CSH.setHelpSet(component, helpSet);
        }
        if (component instanceof JComponent) {
            JComponent jComponent = (JComponent) component;
            if (str2 == null) {
                displayHelpFromFocus = getDisplayHelpFromFocus();
            } else {
                displayHelpFromFocus = new CSH.DisplayHelpFromFocus(helpSet, str2, str3);
            }
            jComponent.registerKeyboardAction(displayHelpFromFocus, KeyStroke.getKeyStroke(156, 0), 1);
            jComponent.registerKeyboardAction(displayHelpFromFocus, KeyStroke.getKeyStroke(112, 0), 1);
            return;
        }
        this.helpKeyHS = helpSet;
        this.helpKeyPresentation = str2;
        this.helpKeyPresentationName = str3;
        component.addKeyListener(this);
    }

    public void keyTyped(KeyEvent keyEvent) {
    }

    public void keyPressed(KeyEvent keyEvent) {
    }

    public void keyReleased(KeyEvent keyEvent) {
        ActionListener displayHelpFromFocus;
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 112 || keyCode == 156) {
            if (this.helpKeyHS != null) {
                displayHelpFromFocus = new CSH.DisplayHelpFromFocus(this.helpKeyHS, this.helpKeyPresentation, this.helpKeyPresentationName);
            } else {
                displayHelpFromFocus = getDisplayHelpFromFocus();
            }
            displayHelpFromFocus.actionPerformed(new ActionEvent(keyEvent.getComponent(), 1001, (String) null));
        }
    }

    @Override // javax.help.HelpBroker
    public void enableHelp(Component component, String str, HelpSet helpSet) {
        if (str == null) {
            throw new NullPointerException(ElementTags.ID);
        }
        CSH.setHelpIDString(component, str);
        if (helpSet != null) {
            CSH.setHelpSet(component, helpSet);
        }
    }

    @Override // javax.help.HelpBroker
    public void enableHelp(MenuItem menuItem, String str, HelpSet helpSet) {
        if (str == null) {
            throw new NullPointerException(ElementTags.ID);
        }
        CSH.setHelpIDString(menuItem, str);
        if (helpSet != null) {
            CSH.setHelpSet(menuItem, helpSet);
        }
    }

    @Override // javax.help.HelpBroker
    public void enableHelpOnButton(Component component, String str, HelpSet helpSet) {
        if (!(component instanceof AbstractButton) && !(component instanceof Button)) {
            throw new IllegalArgumentException("Invalid Component");
        }
        if (str == null) {
            throw new NullPointerException(ElementTags.ID);
        }
        CSH.setHelpIDString(component, str);
        if (helpSet != null) {
            CSH.setHelpSet(component, helpSet);
        }
        if (component instanceof AbstractButton) {
            ((AbstractButton) component).addActionListener(getDisplayHelpFromSource());
        } else if (component instanceof Button) {
            ((Button) component).addActionListener(getDisplayHelpFromSource());
        }
    }

    @Override // javax.help.HelpBroker
    public void enableHelpOnButton(MenuItem menuItem, String str, HelpSet helpSet) {
        if (menuItem == null) {
            throw new IllegalArgumentException("Invalid Component");
        }
        if (str == null) {
            throw new NullPointerException(ElementTags.ID);
        }
        CSH.setHelpIDString(menuItem, str);
        if (helpSet != null) {
            CSH.setHelpSet(menuItem, helpSet);
        }
        menuItem.addActionListener(getDisplayHelpFromSource());
    }

    @Override // javax.help.HelpBroker
    public void enableHelpOnButton(Object obj, String str, HelpSet helpSet, String str2, String str3) {
        if (!(obj instanceof AbstractButton) && !(obj instanceof Button) && !(obj instanceof MenuItem)) {
            throw new IllegalArgumentException("Invalid Object");
        }
        if (str == null) {
            throw new NullPointerException(ElementTags.ID);
        }
        if ((obj instanceof AbstractButton) || (obj instanceof Button)) {
            CSH.setHelpIDString((Component) obj, str);
            if (helpSet != null) {
                CSH.setHelpSet((Component) obj, helpSet);
            }
        } else {
            CSH.setHelpIDString((MenuItem) obj, str);
            if (helpSet != null) {
                CSH.setHelpSet((MenuItem) obj, helpSet);
            }
        }
        if (str2 == null) {
            if (obj instanceof AbstractButton) {
                ((AbstractButton) obj).addActionListener(getDisplayHelpFromSource());
                return;
            } else if (obj instanceof Button) {
                ((Button) obj).addActionListener(getDisplayHelpFromSource());
                return;
            } else {
                if (obj instanceof MenuItem) {
                    ((MenuItem) obj).addActionListener(getDisplayHelpFromSource());
                    return;
                }
                return;
            }
        }
        if (obj instanceof AbstractButton) {
            ((AbstractButton) obj).addActionListener(new CSH.DisplayHelpFromSource(helpSet, str2, str3));
        } else if (obj instanceof Button) {
            ((Button) obj).addActionListener(new CSH.DisplayHelpFromSource(helpSet, str2, str3));
        } else if (obj instanceof MenuItem) {
            ((MenuItem) obj).addActionListener(new CSH.DisplayHelpFromSource(helpSet, str2, str3));
        }
    }

    protected ActionListener getDisplayHelpFromFocus() {
        if (this.displayHelpFromFocus == null) {
            this.displayHelpFromFocus = new CSH.DisplayHelpFromFocus(this);
        }
        return this.displayHelpFromFocus;
    }

    protected ActionListener getDisplayHelpFromSource() {
        if (this.displayHelpFromSource == null) {
            this.displayHelpFromSource = new CSH.DisplayHelpFromSource(this);
        }
        return this.displayHelpFromSource;
    }

    public void setActivationObject(Object obj) {
        this.mw.setActivationObject(obj);
    }

    public void setActivationWindow(Window window) {
        this.mw.setActivationWindow(window);
    }

    static {
        SwingHelpUtilities.installLookAndFeelDefaults();
    }

    private static void debug(Object obj) {
    }
}
