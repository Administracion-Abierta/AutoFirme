package com.twelvemonkeys.util;

/* JADX INFO: compiled from: TimeFormat.java */
/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TextFormatter.class */
class TextFormatter extends TimeFormatter {
    String text;

    TextFormatter(String str) {
        this.text = null;
        this.text = str;
        if (str != null) {
            this.digits = str.length();
        }
    }

    @Override // com.twelvemonkeys.util.TimeFormatter
    String format(Time time) {
        return this.text;
    }
}
