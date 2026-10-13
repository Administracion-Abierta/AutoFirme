package org.mozilla.javascript.typedarrays;

import org.mozilla.javascript.ScriptRuntime;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/typedarrays/Conversions.class */
public class Conversions {
    public static int toInt8(Object arg) {
        return (byte) ScriptRuntime.toInt32(arg);
    }

    public static int toUint8(Object arg) {
        return ScriptRuntime.toInt32(arg) & 255;
    }

    public static int toUint8Clamp(Object arg) {
        double d = ScriptRuntime.toNumber(arg);
        if (d <= 0.0d) {
            return 0;
        }
        if (d >= 255.0d) {
            return 255;
        }
        double f = Math.floor(d);
        if (f + 0.5d < d) {
            return (int) (f + 1.0d);
        }
        if (d < f + 0.5d) {
            return (int) f;
        }
        if (((int) f) % 2 != 0) {
            return ((int) f) + 1;
        }
        return (int) f;
    }

    public static int toInt16(Object arg) {
        return (short) ScriptRuntime.toInt32(arg);
    }

    public static int toUint16(Object arg) {
        return ScriptRuntime.toInt32(arg) & 65535;
    }

    public static int toInt32(Object arg) {
        return ScriptRuntime.toInt32(arg);
    }

    public static long toUint32(Object arg) {
        return ScriptRuntime.toUint32(arg);
    }
}
