package com.sun.java.help.search;

import java.util.StringTokenizer;
import java.util.Vector;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Rule.class */
public class Rule {
    private String[] pattern;
    private int killnum = 0;
    private String[] expansions;
    private Vector words;
    private LiteMorph morph;
    private static final boolean debugFlag = false;

    public Rule(String str, String str2, LiteMorph liteMorph) {
        boolean z = false;
        this.morph = liteMorph;
        if (str.length() > 0) {
            Vector vector = new Vector(str.length());
            StringTokenizer stringTokenizer = new StringTokenizer(str, " \t\n\r");
            while (stringTokenizer.hasMoreTokens()) {
                if (z) {
                    this.killnum++;
                }
                String strNextToken = stringTokenizer.nextToken();
                if (strNextToken.equals(Marker.ANY_NON_NULL_MARKER)) {
                    z = true;
                } else {
                    vector.addElement(strNextToken);
                }
            }
            this.pattern = new String[vector.size()];
            vector.copyInto(this.pattern);
        } else {
            this.pattern = new String[0];
        }
        if (str2.length() > 0) {
            Vector vector2 = new Vector(str2.length());
            StringTokenizer stringTokenizer2 = new StringTokenizer(str2, ", \t\n\r");
            while (stringTokenizer2.hasMoreTokens()) {
                vector2.addElement(stringTokenizer2.nextToken());
            }
            this.expansions = new String[vector2.size()];
            vector2.copyInto(this.expansions);
            return;
        }
        this.expansions = new String[0];
    }

    public String[] match(String str, int i, int i2) {
        this.words = new Vector();
        boolean z = true;
        int length = (str.length() - 1) - i2;
        int length2 = (this.pattern.length - 1) - i2;
        while (length2 > -1) {
            debug(new StringBuffer().append("   trying ").append(this.pattern[length2]).append(" at ").append(length).append(" for i = ").append(length2).toString());
            if (length < 0) {
                z = false;
                break;
            }
            if (this.pattern[length2].equals("&")) {
                if (length < 1 || str.charAt(length) != str.charAt(length - 1)) {
                    z = false;
                    break;
                }
                length2--;
                length--;
            } else {
                if (this.pattern[length2].startsWith(".")) {
                    if (this.pattern[length2].indexOf(str.charAt(length), 1) >= 0) {
                        length2--;
                    }
                } else {
                    if (this.pattern[length2].indexOf(str.charAt(length)) < 0) {
                        z = false;
                        break;
                    }
                    length2--;
                }
                length--;
            }
        }
        if (z) {
            String strSubstring = str.substring(0, str.length() - this.killnum);
            for (int i3 = 0; i3 < this.expansions.length; i3++) {
                makeForm(strSubstring, this.expansions[i3], i);
            }
        }
        String[] strArr = new String[this.words.size()];
        this.words.copyInto(strArr);
        this.words = null;
        return strArr;
    }

    private void makeForm(String str, String str2, int i) {
        switch (str2.charAt(0)) {
            case '&':
                this.words.addElement(new StringBuffer().append(str).append(str.charAt(str.length() - 1)).append(str2.substring(1)).toString());
                break;
            case '*':
                debug(new StringBuffer().append(" starting redo: with ").append(str).append(" + ").append(str2).append(" from depth ").append(i).toString());
                if (str2.charAt(1) == '_') {
                    this.morph.morphWord(str, i + 1);
                } else {
                    this.morph.morphWord(new StringBuffer().append(str).append(str2.substring(1)).toString(), i + 1);
                }
                break;
            case '_':
                this.words.addElement(str);
                break;
            default:
                this.words.addElement(new StringBuffer().append(str).append(str2).toString());
                break;
        }
    }

    private static void debug(String str) {
    }
}
