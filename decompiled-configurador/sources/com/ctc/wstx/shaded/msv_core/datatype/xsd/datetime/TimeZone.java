package com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime;

import java.io.Serializable;
import java.util.SimpleTimeZone;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/TimeZone.class */
public class TimeZone implements Serializable {
    public int minutes;
    public static final java.util.TimeZone ZERO = new JavaZeroTimeZone();
    public static final java.util.TimeZone MISSING = new JavaMissingTimeZone();
    private static final long serialVersionUID = 1;

    private Object readResolve() {
        return new SimpleTimeZone(this.minutes * 60 * 1000, "");
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/TimeZone$ZeroTimeZone.class */
    static class ZeroTimeZone extends TimeZone {
        private static final long serialVersionUID = 1;

        ZeroTimeZone() {
        }

        protected Object readResolve() {
            return ZERO;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/TimeZone$JavaZeroTimeZone.class */
    private static class JavaZeroTimeZone extends SimpleTimeZone implements Serializable {
        private static final long serialVersionUID = 1;

        JavaZeroTimeZone() {
            super(0, "XSD 'Z' timezone");
        }

        protected Object readResolve() {
            return TimeZone.ZERO;
        }
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/datetime/TimeZone$JavaMissingTimeZone.class */
    private static class JavaMissingTimeZone extends SimpleTimeZone implements Serializable {
        private static final long serialVersionUID = 1;

        JavaMissingTimeZone() {
            super(0, "XSD missing timezone");
        }

        protected Object readResolve() {
            return TimeZone.MISSING;
        }
    }
}
