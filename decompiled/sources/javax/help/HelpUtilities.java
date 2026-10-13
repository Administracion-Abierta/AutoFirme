package javax.help;

import java.awt.Component;
import java.awt.IllegalComponentStateException;
import java.beans.Introspector;
import java.io.InputStream;
import java.net.URL;
import java.text.CollationElementIterator;
import java.text.MessageFormat;
import java.text.RuleBasedCollator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:javax/help/HelpUtilities.class */
public class HelpUtilities {
    private static Hashtable bundles;
    private static final boolean debug = false;
    private static Hashtable tailsPerLocales = new Hashtable();
    private static ResourceBundle lastBundle = null;
    private static Locale lastLocale = null;

    public static String getHelpSetNameFromBean(Class cls) {
        String string;
        try {
            string = (String) Introspector.getBeanInfo(cls).getBeanDescriptor().getValue("helpSetName");
        } catch (Exception e) {
            string = null;
        }
        if (string == null) {
            String name = cls.getName();
            int iLastIndexOf = name.lastIndexOf(".");
            if (iLastIndexOf == -1) {
                string = new StringBuffer().append(name).append("Help.hs").toString();
            } else {
                string = new StringBuffer().append(name.substring(0, iLastIndexOf).replace('.', '/')).append("/Help.hs").toString();
            }
        }
        return string;
    }

    public static String getIDStringFromBean(Class cls) {
        String string;
        try {
            string = (String) Introspector.getBeanInfo(cls).getBeanDescriptor().getValue("helpID");
        } catch (Exception e) {
            string = null;
        }
        if (string == null) {
            string = new StringBuffer().append(cls.getName()).append(".topID").toString();
        }
        return string;
    }

    public static String getDefaultQueryEngine() {
        return "com.sun.java.help.search.DefaultSearchEngine";
    }

    public static URL getLocalizedResource(ClassLoader classLoader, String str, String str2, Locale locale) {
        return getLocalizedResource(classLoader, str, str2, locale, false);
    }

    public static URL getLocalizedResource(ClassLoader classLoader, String str, String str2, Locale locale, boolean z) {
        URL resource;
        Enumeration candidates = getCandidates(locale);
        while (candidates.hasMoreElements()) {
            String string = new StringBuffer(str).append((String) candidates.nextElement()).append(str2).toString();
            if (classLoader == null) {
                resource = ClassLoader.getSystemResource(string);
            } else {
                resource = classLoader.getResource(string);
            }
            if (resource != null) {
                if (z) {
                    try {
                        InputStream inputStream = resource.openConnection().getInputStream();
                        if (inputStream != null) {
                            int i = inputStream.read();
                            inputStream.close();
                            if (i != -1) {
                                return resource;
                            }
                        } else {
                            continue;
                        }
                    } catch (Throwable th) {
                    }
                } else {
                    return resource;
                }
            }
        }
        return null;
    }

    public static synchronized Enumeration getCandidates(Locale locale) {
        LocalePair localePair = new LocalePair(locale, Locale.getDefault());
        Vector vector = (Vector) tailsPerLocales.get(localePair);
        if (vector != null) {
            debug("getCandidates - cached copy");
            return vector.elements();
        }
        String string = locale.toString();
        StringBuffer stringBufferAppend = new StringBuffer("_").append(string);
        if (string == null) {
            stringBufferAppend.setLength(0);
        }
        Vector vector2 = new Vector();
        while (stringBufferAppend.length() != 0) {
            debug("  adding ", stringBufferAppend);
            String string2 = stringBufferAppend.toString();
            vector2.addElement(string2);
            int iLastIndexOf = string2.lastIndexOf(95);
            if (iLastIndexOf != -1) {
                stringBufferAppend.setLength(iLastIndexOf);
            }
        }
        debug("  addign -- null -- ");
        vector2.addElement("");
        if (locale != Locale.getDefault()) {
            String string3 = Locale.getDefault().toString();
            StringBuffer stringBufferAppend2 = new StringBuffer("_").append(string3);
            if (string3 == null) {
                stringBufferAppend2.setLength(0);
            }
            while (stringBufferAppend2.length() != 0) {
                debug("  adding ", stringBufferAppend2);
                String string4 = stringBufferAppend2.toString();
                vector2.addElement(string4);
                int iLastIndexOf2 = string4.lastIndexOf(95);
                if (iLastIndexOf2 != -1) {
                    stringBufferAppend2.setLength(iLastIndexOf2);
                }
            }
        }
        tailsPerLocales.put(localePair, vector2);
        debug("tails is == ", vector2);
        return vector2.elements();
    }

    /* JADX INFO: loaded from: autofirma.jar:javax/help/HelpUtilities$LocalePair.class */
    static class LocalePair {
        Locale locale1;
        Locale locale2;

        LocalePair(Locale locale, Locale locale2) {
            this.locale1 = locale;
            this.locale2 = locale2;
        }

        public int hashCode() {
            return this.locale1.hashCode() + this.locale2.hashCode();
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof LocalePair)) {
                return false;
            }
            LocalePair localePair = (LocalePair) obj;
            return this.locale1.equals(localePair.locale1) && this.locale2.equals(localePair.locale2);
        }
    }

    public static Locale getLocale(Component component) {
        if (component == null) {
            return Locale.getDefault();
        }
        try {
            return component.getLocale();
        } catch (IllegalComponentStateException e) {
            return Locale.getDefault();
        }
    }

    private static synchronized ResourceBundle getBundle(Locale locale) {
        if (lastLocale == locale) {
            return lastBundle;
        }
        if (bundles == null) {
            bundles = new Hashtable();
        }
        ResourceBundle bundle = (ResourceBundle) bundles.get(locale);
        if (bundle == null) {
            try {
                bundle = ResourceBundle.getBundle("javax.help.resources.Constants", locale);
                bundles.put(locale, bundle);
            } catch (MissingResourceException e) {
                throw new Error("Fatal: Resource for javahelp is missing");
            }
        }
        lastBundle = bundle;
        lastLocale = locale;
        return bundle;
    }

    public static String getString(String str) {
        return getString(Locale.getDefault(), str);
    }

    public static String getText(String str) {
        return getText(Locale.getDefault(), str, (String) null, (String) null);
    }

    public static String getText(String str, String str2) {
        return getText(Locale.getDefault(), str, str2, (String) null);
    }

    public static String getText(String str, String str2, String str3) {
        return getText(Locale.getDefault(), str, str2, str3);
    }

    public static String getText(String str, String str2, String str3, String str4) {
        return getText(Locale.getDefault(), str, str2, str3, str4);
    }

    public static String getString(Locale locale, String str) {
        try {
            return getBundle(locale).getString(str);
        } catch (MissingResourceException e) {
            throw new Error(new StringBuffer().append("Fatal: Localization data for JavaHelp is broken.  Missing ").append(str).append(" key.").toString());
        }
    }

    public static String[] getStringArray(Locale locale, String str) {
        try {
            return getBundle(locale).getStringArray(str);
        } catch (MissingResourceException e) {
            throw new Error(new StringBuffer().append("Fatal: Localization data for JavaHelp is broken.  Missing ").append(str).append(" key.").toString());
        }
    }

    public static String getText(Locale locale, String str) {
        return getText(locale, str, null, null, null);
    }

    public static String getText(Locale locale, String str, String str2) {
        return getText(locale, str, str2, null, null);
    }

    public static String getText(Locale locale, String str, String str2, String str3) {
        return getText(locale, str, str2, str3, null);
    }

    public static String getText(Locale locale, String str, String str2, String str3, String str4) {
        ResourceBundle bundle = getBundle(locale);
        if (str2 == null) {
            str2 = "null";
        }
        if (str3 == null) {
            str3 = "null";
        }
        if (str4 == null) {
            str4 = "null";
        }
        try {
            String string = bundle.getString(str);
            String[] strArr = {str2, str3, str4};
            MessageFormat messageFormat = new MessageFormat(string);
            try {
                messageFormat.setLocale(locale);
            } catch (NullPointerException e) {
            }
            return messageFormat.format(strArr);
        } catch (MissingResourceException e2) {
            throw new Error(new StringBuffer().append("Fatal: Localization data for JavaHelp is broken.  Missing ").append(str).append(" key.").toString());
        }
    }

    public static Locale localeFromLang(String str) {
        Locale locale;
        if (str == null) {
            return null;
        }
        int iIndexOf = str.indexOf("_");
        int iIndexOf2 = str.indexOf("-");
        if (iIndexOf == -1 && iIndexOf2 == -1) {
            locale = new Locale(str, "");
        } else {
            if (iIndexOf == -1 && iIndexOf2 != -1) {
                iIndexOf = iIndexOf2;
            }
            String strSubstring = str.substring(0, iIndexOf);
            int iIndexOf3 = str.indexOf("_", iIndexOf + 1);
            int iIndexOf4 = str.indexOf("-", iIndexOf + 1);
            if (iIndexOf3 == -1 && iIndexOf4 == -1) {
                locale = new Locale(strSubstring, str.substring(iIndexOf + 1));
            } else {
                if (iIndexOf3 == -1 && iIndexOf4 != -1) {
                    iIndexOf3 = iIndexOf4;
                }
                locale = new Locale(strSubstring, str.substring(iIndexOf + 1, iIndexOf3), str.substring(iIndexOf3 + 1));
            }
        }
        return locale;
    }

    public static boolean isStringInString(RuleBasedCollator ruleBasedCollator, String str, String str2) {
        int next;
        debug(new StringBuffer().append("isStringInString source=").append(str).append(" targe =").append(str2).toString());
        if (str == null || str2 == null) {
            return false;
        }
        if (str.length() == 0 && str2.length() == 0) {
            return true;
        }
        boolean z = false;
        ruleBasedCollator.setDecomposition(2);
        if (ruleBasedCollator.getRules().startsWith(Registry.Key.DEFAULT_NAME)) {
            z = true;
        }
        CollationElementIterator collationElementIterator = ruleBasedCollator.getCollationElementIterator(str);
        CollationElementIterator collationElementIterator2 = ruleBasedCollator.getCollationElementIterator(str2);
        int offset = 0;
        while (true) {
            debug(new StringBuffer().append("while(true) toffset=").append(offset).toString());
            try {
                collationElementIterator.setOffset(0);
            } catch (NoSuchMethodError e) {
            }
            int next2 = collationElementIterator.next();
            try {
                collationElementIterator2.setOffset(offset);
            } catch (Exception e2) {
                return false;
            } catch (NoSuchMethodError e3) {
            }
            int next3 = collationElementIterator2.next();
            if (next3 != -1) {
                debug(new StringBuffer().append("sOrder=").append(next2).append(" tOrder=").append(next3).toString());
                while (next3 != -1) {
                    if (next2 == next3) {
                        try {
                            collationElementIterator2.getOffset();
                            break;
                        } catch (NoSuchMethodError e4) {
                        }
                    } else if (CollationElementIterator.primaryOrder(next2) == CollationElementIterator.primaryOrder(next3)) {
                        try {
                            collationElementIterator2.getOffset();
                            break;
                        } catch (NoSuchMethodError e5) {
                        }
                    } else {
                        next3 = collationElementIterator2.next();
                        debug(new StringBuffer().append("next tOrder=").append(next3).toString());
                    }
                }
                if (next3 == -1) {
                    return false;
                }
                boolean z2 = false;
                boolean z3 = false;
                boolean z4 = ruleBasedCollator.getStrength() >= 1;
                boolean z5 = z4;
                boolean z6 = ruleBasedCollator.getStrength() >= 2;
                while (true) {
                    if (z2) {
                        next2 = collationElementIterator.next();
                    } else {
                        z2 = true;
                    }
                    if (z3) {
                        next3 = collationElementIterator2.next();
                    } else {
                        z3 = true;
                    }
                    if (next2 == -1 || next3 == -1) {
                        debug("One string at end");
                        if (next2 != -1) {
                            do {
                                if (CollationElementIterator.primaryOrder(next2) != 0) {
                                    collationElementIterator2.next();
                                    offset = collationElementIterator2.getOffset();
                                    debug("Additional non-ignborable base character in source string - source is greater");
                                    break;
                                }
                                if (CollationElementIterator.secondaryOrder(next2) != 0 && z5) {
                                    collationElementIterator2.next();
                                    offset = collationElementIterator2.getOffset();
                                    debug("Additional secondary elements source is greater");
                                    break;
                                }
                                next = collationElementIterator.next();
                                next2 = next;
                            } while (next != -1);
                            return true;
                        }
                        return true;
                    }
                    int iPrimaryOrder = CollationElementIterator.primaryOrder(next2);
                    int iPrimaryOrder2 = CollationElementIterator.primaryOrder(next3);
                    if (next2 == next3) {
                        if (z && iPrimaryOrder != 0 && !z5) {
                            z5 = z4;
                            z6 = false;
                        }
                        debug("No diff at this positon continue");
                    } else if (iPrimaryOrder != iPrimaryOrder2) {
                        if (next2 == 0) {
                            z3 = false;
                        } else if (next3 == 0) {
                            z2 = false;
                        } else if (iPrimaryOrder == 0) {
                            if (z5) {
                                collationElementIterator2.next();
                                offset = collationElementIterator2.getOffset();
                                debug("Strength is secondary pSOrder === 0");
                                break;
                            }
                            z3 = false;
                        } else if (iPrimaryOrder2 == 0) {
                            if (z5) {
                                collationElementIterator2.next();
                                offset = collationElementIterator2.getOffset();
                                debug("Strength is secondary - pTOrder == 0");
                                break;
                            }
                            z2 = false;
                        } else {
                            collationElementIterator2.next();
                            offset = collationElementIterator2.getOffset();
                            debug("Order are ignorable");
                            break;
                        }
                    } else if (!z5) {
                        continue;
                    } else {
                        if (CollationElementIterator.secondaryOrder(next2) != CollationElementIterator.secondaryOrder(next3)) {
                            collationElementIterator2.next();
                            offset = collationElementIterator2.getOffset();
                            debug("Secondary Difference");
                            break;
                        }
                        if (z6 && CollationElementIterator.tertiaryOrder(next2) != CollationElementIterator.tertiaryOrder(next3)) {
                            collationElementIterator2.next();
                            offset = collationElementIterator2.getOffset();
                            debug("Tertiary difference");
                            break;
                        }
                    }
                }
            } else {
                return false;
            }
        }
    }

    private static void debug(Object obj, Object obj2, Object obj3) {
    }

    private static void debug(Object obj) {
        debug(obj, "", "");
    }

    private static void debug(Object obj, Object obj2) {
        debug(obj, obj2, "");
    }
}
