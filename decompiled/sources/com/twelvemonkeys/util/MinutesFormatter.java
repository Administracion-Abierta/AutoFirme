package com.twelvemonkeys.util;

import com.twelvemonkeys.lang.StringUtil;

/* JADX INFO: compiled from: TimeFormat.java */
/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/MinutesFormatter.class */
class MinutesFormatter extends TimeFormatter {
    MinutesFormatter(int i) {
        this.digits = i;
    }

    @Override // com.twelvemonkeys.util.TimeFormatter
    String format(Time time) {
        if (time.getMinutes() >= Math.pow(10.0d, this.digits)) {
            return Integer.toString(time.getMinutes());
        }
        return StringUtil.pad(String.valueOf(time.getMinutes()), this.digits, "0", true);
    }
}
