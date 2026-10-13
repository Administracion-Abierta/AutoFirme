package javax.help;

import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:javax/help/SecondaryWindow.class */
public class SecondaryWindow extends WindowPresentation {
    private static Hashtable windows = new Hashtable();
    private String name;
    private static final boolean debug = false;

    private SecondaryWindow(HelpSet helpSet, String str) {
        super(helpSet);
        this.name = str;
    }

    public static Presentation getPresentation(HelpSet helpSet, String str) {
        debug("getPresentation");
        String str2 = str;
        if (str == null) {
            str2 = "";
        }
        SecondaryWindow secondaryWindow = (SecondaryWindow) windows.get(str2);
        if (secondaryWindow != null) {
            if (secondaryWindow.getHelpSet() != helpSet) {
                secondaryWindow.setHelpSet(helpSet);
            }
            return secondaryWindow;
        }
        debug("no Presentation - start again");
        SecondaryWindow secondaryWindow2 = new SecondaryWindow(helpSet, str2);
        secondaryWindow2.setViewDisplayed(false);
        secondaryWindow2.setToolbarDisplayed(false);
        secondaryWindow2.setDestroyOnExit(true);
        secondaryWindow2.setTitleFromDocument(true);
        if (helpSet != null) {
            HelpSet.Presentation defaultPresentation = null;
            if (str != null) {
                defaultPresentation = helpSet.getPresentation(str);
            }
            if (defaultPresentation == null) {
                defaultPresentation = helpSet.getDefaultPresentation();
            }
            secondaryWindow2.setHelpSetPresentation(defaultPresentation);
            windows.put(str2, secondaryWindow2);
        }
        return secondaryWindow2;
    }

    public static SecondaryWindow getPresentation(String str) {
        debug("getPresenation(name)");
        return (SecondaryWindow) windows.get(str);
    }

    @Override // javax.help.WindowPresentation
    public void destroy() {
        super.destroy();
        windows.remove(this.name);
    }

    private static void debug(Object obj) {
    }
}
