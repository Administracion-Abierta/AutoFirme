package com.ctc.wstx.util;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/util/TextAccumulator.class */
public final class TextAccumulator {
    private String mText = null;
    private StringBuilder mBuilder = null;

    public boolean hasText() {
        return (this.mBuilder == null && this.mText == null) ? false : true;
    }

    public void addText(String text) {
        int len = text.length();
        if (len > 0) {
            if (this.mText != null) {
                this.mBuilder = new StringBuilder(this.mText.length() + len);
                this.mBuilder.append(this.mText);
                this.mText = null;
            }
            if (this.mBuilder != null) {
                this.mBuilder.append(text);
            } else {
                this.mText = text;
            }
        }
    }

    public void addText(char[] buf, int start, int end) {
        int len = end - start;
        if (len > 0) {
            if (this.mText != null) {
                this.mBuilder = new StringBuilder(this.mText.length() + len);
                this.mBuilder.append(this.mText);
                this.mText = null;
            } else if (this.mBuilder == null) {
                this.mBuilder = new StringBuilder(len);
            }
            this.mBuilder.append(buf, start, end - start);
        }
    }

    public String getAndClear() {
        if (this.mText != null) {
            String result = this.mText;
            this.mText = null;
            return result;
        }
        if (this.mBuilder != null) {
            String result2 = this.mBuilder.toString();
            this.mBuilder = null;
            return result2;
        }
        return "";
    }
}
