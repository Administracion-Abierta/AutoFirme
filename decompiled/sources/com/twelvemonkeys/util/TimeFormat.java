package com.twelvemonkeys.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/TimeFormat.class */
public class TimeFormat extends Format {
    static final String MINUTE = "m";
    static final String SECOND = "s";
    static final String TIME = "S";
    static final String ESCAPE = "\\";
    private static final TimeFormat DEFAULT_FORMAT = new TimeFormat("m:ss");
    protected String formatString;
    protected TimeFormatter[] formatter;

    static void main(String[] strArr) {
        TimeFormat timeFormat;
        Time time;
        TimeFormat timeFormat2 = null;
        if (strArr.length >= 3) {
            System.out.println("Creating out TimeFormat: \"" + strArr[2] + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            timeFormat2 = new TimeFormat(strArr[2]);
        }
        if (strArr.length >= 2) {
            System.out.println("Creating in TimeFormat: \"" + strArr[1] + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            timeFormat = new TimeFormat(strArr[1]);
        } else {
            System.out.println("Using default format for in");
            timeFormat = DEFAULT_FORMAT;
        }
        if (timeFormat2 == null) {
            timeFormat2 = timeFormat;
        }
        if (strArr.length >= 1) {
            System.out.println("Parsing: \"" + strArr[0] + "\" with format \"" + timeFormat.formatString + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            time = timeFormat.parse(strArr[0]);
        } else {
            time = new Time();
        }
        System.out.println("Time is \"" + timeFormat2.format(time) + "\" according to format \"" + timeFormat2.formatString + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
    }

    public TimeFormat(String str) {
        this.formatString = null;
        this.formatString = str;
        Vector vector = new Vector();
        StringTokenizer stringTokenizer = new StringTokenizer(str, "\\msS", true);
        String str2 = null;
        int i = 0;
        while (stringTokenizer.hasMoreElements()) {
            String strNextToken = stringTokenizer.nextToken();
            if (str2 != null && str2.equals(ESCAPE)) {
                strNextToken = (strNextToken != null ? strNextToken : "") + (stringTokenizer.hasMoreElements() ? stringTokenizer.nextToken() : "");
                str2 = null;
                i = 0;
            }
            if (str2 == null || str2.equals(strNextToken)) {
                i++;
                str2 = strNextToken;
            } else {
                if (str2.equals("m")) {
                    vector.add(new MinutesFormatter(i));
                } else if (str2.equals("s")) {
                    vector.add(new SecondsFormatter(i));
                } else if (str2.equals("S")) {
                    vector.add(new SecondsFormatter(-1));
                } else {
                    vector.add(new TextFormatter(str2));
                }
                i = 1;
                str2 = strNextToken;
            }
        }
        if (str2 != null) {
            if (str2.equals("m")) {
                vector.add(new MinutesFormatter(i));
            } else if (str2.equals("s")) {
                vector.add(new SecondsFormatter(i));
            } else if (str2.equals("S")) {
                vector.add(new SecondsFormatter(-1));
            } else {
                vector.add(new TextFormatter(str2));
            }
        }
        this.formatter = (TimeFormatter[]) vector.toArray(new TimeFormatter[vector.size()]);
    }

    public static TimeFormat getInstance() {
        return DEFAULT_FORMAT;
    }

    public String getFormatString() {
        return this.formatString;
    }

    @Override // java.text.Format
    public StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        if (!(obj instanceof Time)) {
            throw new IllegalArgumentException("Must be instance of " + Time.class);
        }
        return stringBuffer.append(format(obj));
    }

    public String format(Time time) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.formatter.length; i++) {
            sb.append(this.formatter[i].format(time));
        }
        return sb.toString();
    }

    @Override // java.text.Format
    public Object parseObject(String str, ParsePosition parsePosition) {
        Time time = parse(str);
        parsePosition.setIndex(str.length());
        return time;
    }

    public Time parse(String str) {
        int length;
        Time time = new Time();
        int i = 0;
        int i2 = 0;
        int i3 = 0;
        int length2 = 0;
        boolean z = false;
        for (int i4 = 0; i4 < this.formatter.length && i3 + length2 < str.length(); i4++) {
            i3 += length2;
            if (this.formatter[i4] instanceof MinutesFormatter) {
                if (i4 + 1 < this.formatter.length && (this.formatter[i4 + 1] instanceof TextFormatter)) {
                    length2 = str.indexOf(((TextFormatter) this.formatter[i4 + 1]).text, i3);
                    if (length2 < 0) {
                        length2 = str.length();
                    }
                } else if (i4 + 1 >= this.formatter.length) {
                    length2 = str.length();
                } else {
                    length2 = this.formatter[i4].digits;
                }
                if (length2 > i3) {
                    i2 = Integer.parseInt(str.substring(i3, length2));
                }
            } else if (this.formatter[i4] instanceof SecondsFormatter) {
                if (this.formatter[i4].digits == -1) {
                    if (i4 + 1 < this.formatter.length && (this.formatter[i4 + 1] instanceof TextFormatter)) {
                        length = str.indexOf(((TextFormatter) this.formatter[i4 + 1]).text, i3);
                    } else if (i4 + 1 >= this.formatter.length) {
                        length = str.length();
                    } else {
                        length2 = 0;
                    }
                    i = Integer.parseInt(str.substring(i3, length));
                    z = true;
                    break;
                }
                if (i4 + 1 < this.formatter.length && (this.formatter[i4 + 1] instanceof TextFormatter)) {
                    length2 = str.indexOf(((TextFormatter) this.formatter[i4 + 1]).text, i3);
                } else if (i4 + 1 >= this.formatter.length) {
                    length2 = str.length();
                } else {
                    length2 = this.formatter[i4].digits;
                }
                i = Integer.parseInt(str.substring(i3, length2));
            } else if (this.formatter[i4] instanceof TextFormatter) {
                length2 = this.formatter[i4].digits;
            }
        }
        if (!z) {
            time.setMinutes(i2);
        }
        time.setSeconds(i);
        return time;
    }
}
