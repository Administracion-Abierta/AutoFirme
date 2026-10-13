package com.twelvemonkeys.util;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/Time.class */
public class Time {
    private int time;
    public static final int SECONDS_IN_MINUTE = 60;

    public Time() {
        this(0);
    }

    public Time(int i) {
        this.time = -1;
        setTime(i);
    }

    public void setTime(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Time argument must be 0 or positive!");
        }
        this.time = i;
    }

    public int getTime() {
        return this.time;
    }

    public long getTimeInMillis() {
        return ((long) this.time) * 1000;
    }

    public void setSeconds(int i) {
        this.time = (getMinutes() * 60) + i;
    }

    public int getSeconds() {
        return this.time % 60;
    }

    public void setMinutes(int i) {
        this.time = (i * 60) + getSeconds();
    }

    public int getMinutes() {
        return this.time / 60;
    }

    public String toString() {
        return getMinutes() + ":" + (getSeconds() < 10 ? "0" : "") + getSeconds();
    }

    @Deprecated
    public String toString(String str) {
        return new TimeFormat(str).format(this);
    }

    @Deprecated
    public static Time parseTime(String str) {
        return TimeFormat.getInstance().parse(str);
    }
}
