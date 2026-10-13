package com.sun.java.help.search;

import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/LiteMorph.class */
public abstract class LiteMorph {
    protected static Vector variants;
    protected static Hashtable rulesTable;
    protected static Hashtable blockedVariants;
    protected static Hashtable exceptions;
    private static final boolean debugFlag = false;

    protected abstract void initialize();

    public LiteMorph() {
        initialize();
    }

    public static LiteMorph getMorph() {
        return null;
    }

    protected void initialize(String[] strArr) {
        if (exceptions == null || strArr == null) {
            return;
        }
        for (int i = 0; i < strArr.length; i++) {
            StringTokenizer stringTokenizer = new StringTokenizer(strArr[i], " ");
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                String str = (String) exceptions.get(strNextToken);
                if (str == null) {
                    exceptions.put(strNextToken, strArr[i]);
                } else {
                    exceptions.put(strNextToken, new StringBuffer().append(str).append(" ").append(strArr[i]).toString());
                }
            }
        }
    }

    public synchronized String[] variantsOf(String str) {
        variants = new Vector();
        blockedVariants = new Hashtable();
        blockedVariants.put(str, str);
        morphWord(str, 0);
        blockedVariants = null;
        String[] strArr = new String[variants.size()];
        variants.copyInto(strArr);
        variants = null;
        return strArr;
    }

    protected void morphWord(String str, int i) {
        debug(new StringBuffer().append(" analyzing: ").append(str).append(" at depth ").append(i).toString());
        if (i > 2) {
            return;
        }
        String str2 = (String) exceptions.get(str);
        if (str2 == null) {
            str2 = "";
        }
        if (str2.length() > 0) {
            StringTokenizer stringTokenizer = new StringTokenizer(str2, " ");
            while (stringTokenizer.hasMoreTokens()) {
                addVariant(stringTokenizer.nextToken());
            }
            debug(new StringBuffer().append("   ").append(str).append(": found match in exceptions -- ").append(str2).append(", at depth ").append(i).toString());
            return;
        }
        if (str.indexOf("-") >= 0) {
            return;
        }
        Rule[] ruleArr = null;
        int length = 0;
        Enumeration enumerationKeys = rulesTable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str3 = (String) enumerationKeys.nextElement();
            if (str.endsWith(str3) && !str3.equals(PreferencesManager.VALUE_KEYSTORE_DEFAULT)) {
                ruleArr = (Rule[]) rulesTable.get(str3);
                length = str3.length();
                break;
            }
        }
        if (ruleArr == null) {
            ruleArr = (Rule[]) rulesTable.get(PreferencesManager.VALUE_KEYSTORE_DEFAULT);
            length = 0;
        }
        for (int i2 = 0; i2 < ruleArr.length; i2++) {
            debug(new StringBuffer().append("  ").append(str).append(": trying rule: ").append(ruleArr[i2]).append(", at depth ").append(i).toString());
            String[] strArrMatch = ruleArr[i2].match(str, i, length);
            if (strArrMatch.length > 0) {
                debug(new StringBuffer().append("  ").append(str).append(": found match for: ").append(ruleArr[i2]).append(", at depth ").append(i).toString());
                addVariant(str);
                for (String str4 : strArrMatch) {
                    addVariant(str4);
                }
                return;
            }
        }
    }

    private void addVariant(String str) {
        if (blockedVariants.get(str) == null) {
            variants.addElement(str);
            blockedVariants.put(str, str);
        }
    }

    protected static Rule r(String str, String str2, LiteMorph liteMorph) {
        return new Rule(str, str2, liteMorph);
    }

    private static void debug(String str) {
    }
}
