package org.mozilla.javascript.regexp;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/regexp/SubString.class */
public class SubString {
    String str;
    int index;
    int length;

    public SubString() {
    }

    public SubString(String str) {
        this.str = str;
        this.index = 0;
        this.length = str.length();
    }

    public SubString(String source, int start, int len) {
        this.str = source;
        this.index = start;
        this.length = len;
    }

    public String toString() {
        return this.str == null ? "" : this.str.substring(this.index, this.index + this.length);
    }
}
