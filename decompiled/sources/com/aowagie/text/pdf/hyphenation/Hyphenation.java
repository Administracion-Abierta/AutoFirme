package com.aowagie.text.pdf.hyphenation;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/Hyphenation.class */
public class Hyphenation {
    private int[] hyphenPoints;
    private String word;
    private int len;

    Hyphenation(String word, int[] points) {
        this.word = word;
        this.hyphenPoints = points;
        this.len = points.length;
    }

    public int length() {
        return this.len;
    }

    public String getPreHyphenText(int index) {
        return this.word.substring(0, this.hyphenPoints[index]);
    }

    public String getPostHyphenText(int index) {
        return this.word.substring(this.hyphenPoints[index]);
    }

    public int[] getHyphenationPoints() {
        return this.hyphenPoints;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();
        int start = 0;
        for (int i = 0; i < this.len; i++) {
            str.append(this.word.substring(start, this.hyphenPoints[i])).append('-');
            start = this.hyphenPoints[i];
        }
        str.append(this.word.substring(start));
        return str.toString();
    }
}
