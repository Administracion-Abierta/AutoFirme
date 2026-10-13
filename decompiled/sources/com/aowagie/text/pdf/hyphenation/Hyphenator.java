package com.aowagie.text.pdf.hyphenation;

import com.aowagie.text.pdf.BaseFont;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/Hyphenator.class */
public class Hyphenator {
    private HyphenationTree hyphenTree;
    private int remainCharCount;
    private int pushCharCount;
    private static final String defaultHyphLocation = "com/lowagie/text/pdf/hyphenation/hyph/";
    private static Hashtable hyphenTrees = new Hashtable();
    private static String hyphenDir = "";

    public Hyphenator(String lang, String country, int leftMin, int rightMin) {
        this.hyphenTree = null;
        this.remainCharCount = 2;
        this.pushCharCount = 2;
        this.hyphenTree = getHyphenationTree(lang, country);
        this.remainCharCount = leftMin;
        this.pushCharCount = rightMin;
    }

    private static HyphenationTree getHyphenationTree(String lang, String country) {
        String key = lang;
        if (country != null && !country.equals("none")) {
            key = key + "_" + country;
        }
        if (hyphenTrees.containsKey(key)) {
            return (HyphenationTree) hyphenTrees.get(key);
        }
        if (hyphenTrees.containsKey(lang)) {
            return (HyphenationTree) hyphenTrees.get(lang);
        }
        HyphenationTree hTree = getResourceHyphenationTree(key);
        if (hTree == null) {
            hTree = getFileHyphenationTree(key);
        }
        if (hTree != null) {
            hyphenTrees.put(key, hTree);
        }
        return hTree;
    }

    private static HyphenationTree getResourceHyphenationTree(String key) {
        try {
            InputStream stream = BaseFont.getResourceStream(defaultHyphLocation + key + ".xml");
            if (stream == null && key.length() > 2) {
                stream = BaseFont.getResourceStream(defaultHyphLocation + key.substring(0, 2) + ".xml");
            }
            if (stream == null) {
                return null;
            }
            HyphenationTree hTree = new HyphenationTree();
            hTree.loadSimplePatterns(stream);
            return hTree;
        } catch (Exception e) {
            return null;
        }
    }

    private static HyphenationTree getFileHyphenationTree(String key) {
        try {
            if (hyphenDir == null) {
                return null;
            }
            InputStream stream = null;
            File hyphenFile = new File(hyphenDir, key + ".xml");
            if (hyphenFile.canRead()) {
                stream = new FileInputStream(hyphenFile);
            }
            if (stream == null && key.length() > 2) {
                File hyphenFile2 = new File(hyphenDir, key.substring(0, 2) + ".xml");
                if (hyphenFile2.canRead()) {
                    stream = new FileInputStream(hyphenFile2);
                }
            }
            if (stream == null) {
                return null;
            }
            HyphenationTree hTree = new HyphenationTree();
            hTree.loadSimplePatterns(stream);
            return hTree;
        } catch (Exception e) {
            return null;
        }
    }

    public void setMinRemainCharCount(int min) {
        this.remainCharCount = min;
    }

    public void setMinPushCharCount(int min) {
        this.pushCharCount = min;
    }

    public Hyphenation hyphenate(String word) {
        if (this.hyphenTree == null) {
            return null;
        }
        return this.hyphenTree.hyphenate(word, this.remainCharCount, this.pushCharCount);
    }
}
