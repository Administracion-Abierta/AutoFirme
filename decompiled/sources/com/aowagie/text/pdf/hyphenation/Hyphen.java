package com.aowagie.text.pdf.hyphenation;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/hyphenation/Hyphen.class */
class Hyphen implements Serializable {
    private static final long serialVersionUID = -7666138517324763063L;
    private final String preBreak;
    String noBreak;
    private final String postBreak;

    Hyphen(String pre, String no, String post) {
        this.preBreak = pre;
        this.noBreak = no;
        this.postBreak = post;
    }

    public String toString() {
        if (this.noBreak == null && this.postBreak == null && this.preBreak != null && this.preBreak.equals("-")) {
            return "-";
        }
        StringBuffer res = new StringBuffer("{");
        res.append(this.preBreak);
        res.append("}{");
        res.append(this.postBreak);
        res.append("}{");
        res.append(this.noBreak);
        res.append('}');
        return res.toString();
    }
}
