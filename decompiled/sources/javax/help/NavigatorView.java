package javax.help;

import java.awt.Component;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/NavigatorView.class */
public abstract class NavigatorView implements Serializable {
    private HelpSet hs;
    private String name;
    private String label;
    private Locale locale;
    private Hashtable params;
    private Map.ID imageID;
    private String mergeType;
    static Class class$javax$help$HelpSet;
    static Class class$java$lang$String;
    static Class class$java$util$Locale;
    static Class class$java$util$Hashtable;

    public abstract Component createNavigator(HelpModel helpModel);

    protected NavigatorView(HelpSet helpSet, String str, String str2, Locale locale, Hashtable hashtable) {
        if (str == null || str2 == null) {
            throw new NullPointerException("Invalid name or label");
        }
        this.imageID = null;
        if (hashtable != null) {
            String str3 = (String) hashtable.get("imageID");
            if (str3 != null) {
                this.imageID = Map.ID.create(str3, helpSet);
            }
            this.mergeType = (String) hashtable.get("mergetype");
        }
        this.hs = helpSet;
        this.name = str;
        this.label = str2;
        this.locale = locale;
        this.params = hashtable;
    }

    public static NavigatorView create(HelpSet helpSet, String str, String str2, Locale locale, String str3, Hashtable hashtable) throws InvalidNavigatorViewException {
        Class<?> clsClass$;
        Class<?> clsClass$2;
        Class<?> clsClass$3;
        Class<?> clsClass$4;
        Class<?> clsClass$5;
        Class<?> clsLoadClass;
        try {
            ClassLoader loader = helpSet.getLoader();
            Class<?>[] clsArr = new Class[5];
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
            if (class$java$lang$String == null) {
                clsClass$3 = class$("java.lang.String");
                class$java$lang$String = clsClass$3;
            } else {
                clsClass$3 = class$java$lang$String;
            }
            clsArr[2] = clsClass$3;
            if (class$java$util$Locale == null) {
                clsClass$4 = class$("java.util.Locale");
                class$java$util$Locale = clsClass$4;
            } else {
                clsClass$4 = class$java$util$Locale;
            }
            clsArr[3] = clsClass$4;
            if (class$java$util$Hashtable == null) {
                clsClass$5 = class$("java.util.Hashtable");
                class$java$util$Hashtable = clsClass$5;
            } else {
                clsClass$5 = class$java$util$Hashtable;
            }
            clsArr[4] = clsClass$5;
            Object[] objArr = {helpSet, str, str2, locale, hashtable};
            if (loader == null) {
                clsLoadClass = Class.forName(str3);
            } else {
                clsLoadClass = loader.loadClass(str3);
            }
            return (NavigatorView) clsLoadClass.getConstructor(clsArr).newInstance(objArr);
        } catch (Exception e) {
            throw new InvalidNavigatorViewException("Could not create", helpSet, str, str2, locale, str3, hashtable);
        }
    }

    static Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel(Locale locale) {
        return getLabel();
    }

    public String getLabel() {
        return this.label;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Hashtable getParameters() {
        return this.params;
    }

    public String getMergeType() {
        return this.mergeType;
    }

    public Map.ID getImageID() {
        return this.imageID;
    }
}
