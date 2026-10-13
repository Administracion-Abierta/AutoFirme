package org.slf4j.event;

import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.helpers.SubstituteLogger;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/event/EventRecodingLogger.class */
public class EventRecodingLogger implements Logger {
    String name;
    SubstituteLogger logger;
    Queue<SubstituteLoggingEvent> eventQueue;
    static final boolean RECORD_ALL_EVENTS = true;

    public EventRecodingLogger(SubstituteLogger logger, Queue<SubstituteLoggingEvent> eventQueue) {
        this.logger = logger;
        this.name = logger.getName();
        this.eventQueue = eventQueue;
    }

    @Override // org.slf4j.Logger
    public String getName() {
        return this.name;
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void trace(String msg) {
        recordEvent_0Args(Level.TRACE, null, msg, null);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object arg) {
        recordEvent_1Args(Level.TRACE, null, format, arg);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.TRACE, null, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void trace(String format, Object... arguments) {
        recordEventArgArray(Level.TRACE, null, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void trace(String msg, Throwable t) {
        recordEvent_0Args(Level.TRACE, null, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg) {
        recordEvent_0Args(Level.TRACE, marker, msg, null);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg) {
        recordEvent_1Args(Level.TRACE, marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.TRACE, marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String format, Object... argArray) {
        recordEventArgArray(Level.TRACE, marker, format, argArray);
    }

    @Override // org.slf4j.Logger
    public void trace(Marker marker, String msg, Throwable t) {
        recordEvent_0Args(Level.TRACE, marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void debug(String msg) {
        recordEvent_0Args(Level.DEBUG, null, msg, null);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object arg) {
        recordEvent_1Args(Level.DEBUG, null, format, arg);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.DEBUG, null, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void debug(String format, Object... arguments) {
        recordEventArgArray(Level.DEBUG, null, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void debug(String msg, Throwable t) {
        recordEvent_0Args(Level.DEBUG, null, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg) {
        recordEvent_0Args(Level.DEBUG, marker, msg, null);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg) {
        recordEvent_1Args(Level.DEBUG, marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.DEBUG, marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String format, Object... arguments) {
        recordEventArgArray(Level.DEBUG, marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void debug(Marker marker, String msg, Throwable t) {
        recordEvent_0Args(Level.DEBUG, marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void info(String msg) {
        recordEvent_0Args(Level.INFO, null, msg, null);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg) {
        recordEvent_1Args(Level.INFO, null, format, arg);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.INFO, null, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void info(String format, Object... arguments) {
        recordEventArgArray(Level.INFO, null, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void info(String msg, Throwable t) {
        recordEvent_0Args(Level.INFO, null, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg) {
        recordEvent_0Args(Level.INFO, marker, msg, null);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg) {
        recordEvent_1Args(Level.INFO, marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.INFO, marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String format, Object... arguments) {
        recordEventArgArray(Level.INFO, marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void info(Marker marker, String msg, Throwable t) {
        recordEvent_0Args(Level.INFO, marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void warn(String msg) {
        recordEvent_0Args(Level.WARN, null, msg, null);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg) {
        recordEvent_1Args(Level.WARN, null, format, arg);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.WARN, null, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void warn(String format, Object... arguments) {
        recordEventArgArray(Level.WARN, null, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void warn(String msg, Throwable t) {
        recordEvent_0Args(Level.WARN, null, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg) {
        recordEvent_0Args(Level.WARN, marker, msg, null);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg) {
        recordEvent_1Args(Level.WARN, marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.WARN, marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String format, Object... arguments) {
        recordEventArgArray(Level.WARN, marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void warn(Marker marker, String msg, Throwable t) {
        recordEvent_0Args(Level.WARN, marker, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled() {
        return true;
    }

    @Override // org.slf4j.Logger
    public void error(String msg) {
        recordEvent_0Args(Level.ERROR, null, msg, null);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg) {
        recordEvent_1Args(Level.ERROR, null, format, arg);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.ERROR, null, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void error(String format, Object... arguments) {
        recordEventArgArray(Level.ERROR, null, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void error(String msg, Throwable t) {
        recordEvent_0Args(Level.ERROR, null, msg, t);
    }

    @Override // org.slf4j.Logger
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg) {
        recordEvent_0Args(Level.ERROR, marker, msg, null);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg) {
        recordEvent_1Args(Level.ERROR, marker, format, arg);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        recordEvent2Args(Level.ERROR, marker, format, arg1, arg2);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String format, Object... arguments) {
        recordEventArgArray(Level.ERROR, marker, format, arguments);
    }

    @Override // org.slf4j.Logger
    public void error(Marker marker, String msg, Throwable t) {
        recordEvent_0Args(Level.ERROR, marker, msg, t);
    }

    private void recordEvent_0Args(Level level, Marker marker, String msg, Throwable t) {
        recordEvent(level, marker, msg, null, t);
    }

    private void recordEvent_1Args(Level level, Marker marker, String msg, Object arg1) {
        recordEvent(level, marker, msg, new Object[]{arg1}, null);
    }

    private void recordEvent2Args(Level level, Marker marker, String msg, Object arg1, Object arg2) {
        if (arg2 instanceof Throwable) {
            recordEvent(level, marker, msg, new Object[]{arg1}, (Throwable) arg2);
        } else {
            recordEvent(level, marker, msg, new Object[]{arg1, arg2}, null);
        }
    }

    private void recordEventArgArray(Level level, Marker marker, String msg, Object[] args) {
        Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(args);
        if (throwableCandidate != null) {
            Object[] trimmedCopy = MessageFormatter.trimmedCopy(args);
            recordEvent(level, marker, msg, trimmedCopy, throwableCandidate);
        } else {
            recordEvent(level, marker, msg, args, null);
        }
    }

    private void recordEvent(Level level, Marker marker, String msg, Object[] args, Throwable throwable) {
        SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
        loggingEvent.setTimeStamp(System.currentTimeMillis());
        loggingEvent.setLevel(level);
        loggingEvent.setLogger(this.logger);
        loggingEvent.setLoggerName(this.name);
        loggingEvent.setMarker(marker);
        loggingEvent.setMessage(msg);
        loggingEvent.setThreadName(Thread.currentThread().getName());
        loggingEvent.setArgumentArray(args);
        loggingEvent.setThrowable(throwable);
        this.eventQueue.add(loggingEvent);
    }
}
