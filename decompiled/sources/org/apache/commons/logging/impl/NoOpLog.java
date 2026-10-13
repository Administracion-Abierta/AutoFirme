package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.commons.logging.Log;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/logging/impl/NoOpLog.class */
public class NoOpLog implements Log, Serializable {
    private static final long serialVersionUID = 561423906191706148L;

    public NoOpLog() {
    }

    public NoOpLog(String name) {
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable t) {
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable t) {
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable t) {
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable t) {
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable t) {
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable t) {
    }

    @Override // org.apache.commons.logging.Log
    public final boolean isDebugEnabled() {
        return false;
    }

    @Override // org.apache.commons.logging.Log
    public final boolean isErrorEnabled() {
        return false;
    }

    @Override // org.apache.commons.logging.Log
    public final boolean isFatalEnabled() {
        return false;
    }

    @Override // org.apache.commons.logging.Log
    public final boolean isInfoEnabled() {
        return false;
    }

    @Override // org.apache.commons.logging.Log
    public final boolean isTraceEnabled() {
        return false;
    }

    @Override // org.apache.commons.logging.Log
    public final boolean isWarnEnabled() {
        return false;
    }
}
