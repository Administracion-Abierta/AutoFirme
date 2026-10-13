package org.slf4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.EventRecodingLogger;
import org.slf4j.event.LoggingEvent;
import org.slf4j.event.SubstituteLoggingEvent;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/helpers/SubstituteLogger.class */
public class SubstituteLogger implements Logger {
    private final String name;
    private volatile Logger _delegate;
    private Boolean delegateEventAware;
    private Method logMethodCache;
    private EventRecodingLogger eventRecodingLogger;
    private Queue<SubstituteLoggingEvent> eventQueue;
    private final boolean createdPostInitialization;

    public SubstituteLogger(String name, Queue<SubstituteLoggingEvent> eventQueue, boolean createdPostInitialization) {
        this.name = name;
        this.eventQueue = eventQueue;
        this.createdPostInitialization = createdPostInitialization;
    }

    @Override // org.slf4j.Logger
    public String getName() {
        return this.name;
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled() {
        return delegate().isTraceEnabled();
    }

    @Override // org.slf4j.Logger
    public void trace(String msg) {
        delegate().trace(msg);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object arg) {
        delegate().trace(format, arg);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object arg1, Object arg2) {
        delegate().trace(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object... arguments) {
        delegate().trace(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void trace(String msg, Throwable t) {
        delegate().trace(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled(Marker marker) {
        return delegate().isTraceEnabled(marker);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg) {
        delegate().trace(marker, msg);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg) {
        delegate().trace(marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        delegate().trace(marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object... arguments) {
        delegate().trace(marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg, Throwable t) {
        delegate().trace(marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled() {
        return delegate().isDebugEnabled();
    }

    @Override // org.slf4j.Logger
    public void debug(String msg) {
        delegate().debug(msg);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object arg) {
        delegate().debug(format, arg);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object arg1, Object arg2) {
        delegate().debug(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object... arguments) {
        delegate().debug(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void debug(String msg, Throwable t) {
        delegate().debug(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled(Marker marker) {
        return delegate().isDebugEnabled(marker);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg) {
        delegate().debug(marker, msg);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg) {
        delegate().debug(marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        delegate().debug(marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object... arguments) {
        delegate().debug(marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg, Throwable t) {
        delegate().debug(marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled() {
        return delegate().isInfoEnabled();
    }

    @Override // org.slf4j.Logger
    public void info(String msg) {
        delegate().info(msg);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg) {
        delegate().info(format, arg);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg1, Object arg2) {
        delegate().info(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object... arguments) {
        delegate().info(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void info(String msg, Throwable t) {
        delegate().info(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled(Marker marker) {
        return delegate().isInfoEnabled(marker);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg) {
        delegate().info(marker, msg);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg) {
        delegate().info(marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        delegate().info(marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object... arguments) {
        delegate().info(marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg, Throwable t) {
        delegate().info(marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled() {
        return delegate().isWarnEnabled();
    }

    @Override // org.slf4j.Logger
    public void warn(String msg) {
        delegate().warn(msg);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg) {
        delegate().warn(format, arg);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg1, Object arg2) {
        delegate().warn(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object... arguments) {
        delegate().warn(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void warn(String msg, Throwable t) {
        delegate().warn(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled(Marker marker) {
        return delegate().isWarnEnabled(marker);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg) {
        delegate().warn(marker, msg);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg) {
        delegate().warn(marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        delegate().warn(marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object... arguments) {
        delegate().warn(marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg, Throwable t) {
        delegate().warn(marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled() {
        return delegate().isErrorEnabled();
    }

    @Override // org.slf4j.Logger
    public void error(String msg) {
        delegate().error(msg);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg) {
        delegate().error(format, arg);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg1, Object arg2) {
        delegate().error(format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object... arguments) {
        delegate().error(format, arguments);
    }

    @Override // org.slf4j.Logger
    public void error(String msg, Throwable t) {
        delegate().error(msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled(Marker marker) {
        return delegate().isErrorEnabled(marker);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg) {
        delegate().error(marker, msg);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg) {
        delegate().error(marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        delegate().error(marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object... arguments) {
        delegate().error(marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg, Throwable t) {
        delegate().error(marker, msg, t);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SubstituteLogger that = (SubstituteLogger) o;
        if (!this.name.equals(that.name)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    Logger delegate() {
        if (this._delegate != null) {
            return this._delegate;
        }
        if (this.createdPostInitialization) {
            return NOPLogger.NOP_LOGGER;
        }
        return getEventRecordingLogger();
    }

    private Logger getEventRecordingLogger() {
        if (this.eventRecodingLogger == null) {
            this.eventRecodingLogger = new EventRecodingLogger(this, this.eventQueue);
        }
        return this.eventRecodingLogger;
    }

    public void setDelegate(Logger delegate) {
        this._delegate = delegate;
    }

    public boolean isDelegateEventAware() {
        if (this.delegateEventAware != null) {
            return this.delegateEventAware.booleanValue();
        }
        try {
            this.logMethodCache = this._delegate.getClass().getMethod("log", LoggingEvent.class);
            this.delegateEventAware = Boolean.TRUE;
        } catch (NoSuchMethodException e) {
            this.delegateEventAware = Boolean.FALSE;
        }
        return this.delegateEventAware.booleanValue();
    }

    public void log(LoggingEvent event) {
        if (isDelegateEventAware()) {
            try {
                this.logMethodCache.invoke(this._delegate, event);
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e2) {
            } catch (InvocationTargetException e3) {
            }
        }
    }

    public boolean isDelegateNull() {
        return this._delegate == null;
    }

    public boolean isDelegateNOP() {
        return this._delegate instanceof NOPLogger;
    }
}
