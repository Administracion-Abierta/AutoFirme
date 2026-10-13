package com.twelvemonkeys.util;

/* JADX INFO: compiled from: TimeFormat.java */
/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeFormatter.class */
abstract class TimeFormatter {
    int digits = 0;

    abstract String format(Time time);

    TimeFormatter() {
    }
}
