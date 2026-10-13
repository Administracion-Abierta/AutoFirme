package javax.help;

import java.awt.Dimension;
import java.awt.Point;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpSetFactory.class */
interface HelpSetFactory {
    void parsingStarted(URL url);

    void processDOCTYPE(String str, String str2, String str3);

    void processPI(HelpSet helpSet, String str, String str2);

    void processTitle(HelpSet helpSet, String str);

    void processHomeID(HelpSet helpSet, String str);

    void processMapRef(HelpSet helpSet, Hashtable hashtable);

    void processView(HelpSet helpSet, String str, String str2, String str3, Hashtable hashtable, String str4, Hashtable hashtable2, Locale locale);

    void processSubHelpSet(HelpSet helpSet, Hashtable hashtable);

    void processPresentation(HelpSet helpSet, String str, boolean z, boolean z2, boolean z3, Dimension dimension, Point point, String str2, String str3, boolean z4, Vector vector);

    void reportMessage(String str, boolean z);

    Enumeration listMessages();

    HelpSet parsingEnded(HelpSet helpSet);

    /* JADX INFO: loaded from: autofirma.jar:javax/help/HelpSetFactory$HelpAction.class */
    public static class HelpAction {
        public String className;
        public Hashtable attr;

        public HelpAction(String str, Hashtable hashtable) {
            this.className = null;
            this.attr = null;
            this.className = str;
            this.attr = hashtable;
        }
    }
}
