package com.twelvemonkeys.util;

import com.twelvemonkeys.lang.StringUtil;

/* JADX INFO: compiled from: TimeFormat.java */
/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/SecondsFormatter.class */
class SecondsFormatter extends TimeFormatter {
    SecondsFormatter(int i) {
        this.digits = i;
    }

    @Override // com.twelvemonkeys.util.TimeFormatter
    String format(Time time) {
        if (this.digits < 0) {
            return Integer.toString(time.getTime());
        }
        if (time.getSeconds() >= Math.pow(10.0d, this.digits)) {
            return Integer.toString(time.getSeconds());
        }
        return StringUtil.pad(String.valueOf(time.getSeconds()), this.digits, "0", true);
    }
}
