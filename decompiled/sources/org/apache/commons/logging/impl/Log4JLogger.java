package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/logging/impl/Log4JLogger.class */
public class Log4JLogger implements Log, Serializable {
    private static final long serialVersionUID = 5160705895411730424L;
    private static final String FQCN;
    private volatile transient Logger logger;
    private final String name;
    private static final Priority traceLevel;
    static Class class$org$apache$commons$logging$impl$Log4JLogger;
    static Class class$org$apache$log4j$Level;
    static Class class$org$apache$log4j$Priority;

    static Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class clsClass$;
        Class clsClass$2;
        Class<?> clsClass$3;
        Priority _traceLevel;
        Class clsClass$4;
        if (class$org$apache$commons$logging$impl$Log4JLogger == null) {
            clsClass$ = class$("org.apache.commons.logging.impl.Log4JLogger");
            class$org$apache$commons$logging$impl$Log4JLogger = clsClass$;
        } else {
            clsClass$ = class$org$apache$commons$logging$impl$Log4JLogger;
        }
        FQCN = clsClass$.getName();
        if (class$org$apache$log4j$Priority == null) {
            clsClass$2 = class$("org.apache.log4j.Priority");
            class$org$apache$log4j$Priority = clsClass$2;
        } else {
            clsClass$2 = class$org$apache$log4j$Priority;
        }
        if (class$org$apache$log4j$Level == null) {
            clsClass$3 = class$("org.apache.log4j.Level");
            class$org$apache$log4j$Level = clsClass$3;
        } else {
            clsClass$3 = class$org$apache$log4j$Level;
        }
        if (!clsClass$2.isAssignableFrom(clsClass$3)) {
            throw new InstantiationError("Log4J 1.2 not available");
        }
        try {
            if (class$org$apache$log4j$Level == null) {
                clsClass$4 = class$("org.apache.log4j.Level");
                class$org$apache$log4j$Level = clsClass$4;
            } else {
                clsClass$4 = class$org$apache$log4j$Level;
            }
            _traceLevel = (Priority) clsClass$4.getDeclaredField("TRACE").get(null);
        } catch (Exception e) {
            _traceLevel = Level.DEBUG;
        }
        traceLevel = _traceLevel;
    }

    public Log4JLogger() {
        this.logger = null;
        this.name = null;
    }

    public Log4JLogger(String name) {
        this.logger = null;
        this.name = name;
        this.logger = getLogger();
    }

    public Log4JLogger(Logger logger) {
        this.logger = null;
        if (logger == null) {
            throw new IllegalArgumentException("Warning - null logger in constructor; possible log4j misconfiguration.");
        }
        this.name = logger.getName();
        this.logger = logger;
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
        getLogger().log(FQCN, traceLevel, message, (Throwable) null);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable t) {
        getLogger().log(FQCN, traceLevel, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
        getLogger().log(FQCN, Level.DEBUG, message, (Throwable) null);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable t) {
        getLogger().log(FQCN, Level.DEBUG, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
        getLogger().log(FQCN, Level.INFO, message, (Throwable) null);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable t) {
        getLogger().log(FQCN, Level.INFO, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
        getLogger().log(FQCN, Level.WARN, message, (Throwable) null);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable t) {
        getLogger().log(FQCN, Level.WARN, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
        getLogger().log(FQCN, Level.ERROR, message, (Throwable) null);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable t) {
        getLogger().log(FQCN, Level.ERROR, message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
        getLogger().log(FQCN, Level.FATAL, message, (Throwable) null);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable t) {
        getLogger().log(FQCN, Level.FATAL, message, t);
    }

    public Logger getLogger() {
        Logger result = this.logger;
        if (result == null) {
            synchronized (this) {
                result = this.logger;
                if (result == null) {
                    Logger logger = Logger.getLogger(this.name);
                    result = logger;
                    this.logger = logger;
                }
            }
        }
        return result;
    }

    @Override // org.apache.commons.logging.Log
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isErrorEnabled() {
        return getLogger().isEnabledFor(Level.ERROR);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isFatalEnabled() {
        return getLogger().isEnabledFor(Level.FATAL);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isTraceEnabled() {
        return getLogger().isEnabledFor(traceLevel);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isWarnEnabled() {
        return getLogger().isEnabledFor(Level.WARN);
    }
}
