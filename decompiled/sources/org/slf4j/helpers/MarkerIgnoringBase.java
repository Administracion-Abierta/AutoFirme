package org.slf4j.helpers;

import org.slf4j.Logger;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/helpers/MarkerIgnoringBase.class */
public abstract class MarkerIgnoringBase extends NamedLoggerBase implements Logger {
    private static final long serialVersionUID = 9044267456635152283L;

    @Override // org.slf4j.helpers.NamedLoggerBase, org.slf4j.Logger
    public /* bridge */ /* synthetic */ String getName() {
        return super.getName();
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg) {
        trace(msg);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg) {
        trace(format, arg);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        trace(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object... arguments) {
        trace(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg, Throwable t) {
        trace(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg) {
        debug(msg);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg) {
        debug(format, arg);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        debug(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object... arguments) {
        debug(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg, Throwable t) {
        debug(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg) {
        info(msg);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg) {
        info(format, arg);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        info(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object... arguments) {
        info(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg, Throwable t) {
        info(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg) {
        warn(msg);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg) {
        warn(format, arg);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        warn(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object... arguments) {
        warn(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg, Throwable t) {
        warn(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg) {
        error(msg);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg) {
        error(format, arg);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        error(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object... arguments) {
        error(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg, Throwable t) {
        error(msg, t);
    }

    public String toString() {
        return getClass().getName() + "(" + getName() + ")";
    }
}
