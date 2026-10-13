package javax.help;

import java.util.Hashtable;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:javax/help/InvalidNavigatorViewException.class */
public class InvalidNavigatorViewException extends Exception {
    private HelpSet hs;
    private String name;
    private String label;
    private Locale locale;
    private String className;
    private Hashtable params;

    public InvalidNavigatorViewException(String str, HelpSet helpSet, String str2, String str3, Locale locale, String str4, Hashtable hashtable) {
        super(str);
        this.hs = helpSet;
        this.name = str2;
        this.label = str3;
        this.locale = locale;
        this.className = str4;
        this.params = hashtable;
    }

    public HelpSet getHelpSet() {
        return this.hs;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel() {
        return this.label;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public String getClassName() {
        return this.className;
    }

    public Hashtable getParams() {
        return this.params;
    }
}
