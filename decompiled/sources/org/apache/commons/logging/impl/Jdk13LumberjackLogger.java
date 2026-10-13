package org.apache.commons.logging.impl;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/logging/impl/Jdk13LumberjackLogger.class */
public class Jdk13LumberjackLogger implements Log, Serializable {
    private static final long serialVersionUID = -8649807923527610591L;
    protected transient Logger logger;
    protected String name;
    private String sourceClassName = "unknown";
    private String sourceMethodName = "unknown";
    private boolean classAndMethodFound = false;
    protected static final Level dummyLevel = Level.FINE;

    public Jdk13LumberjackLogger(String name) {
        this.logger = null;
        this.name = null;
        this.name = name;
        this.logger = getLogger();
    }

    private void log(Level level, String msg, Throwable ex) {
        if (getLogger().isLoggable(level)) {
            LogRecord record = new LogRecord(level, msg);
            if (!this.classAndMethodFound) {
                getClassAndMethod();
            }
            record.setSourceClassName(this.sourceClassName);
            record.setSourceMethodName(this.sourceMethodName);
            if (ex != null) {
                record.setThrown(ex);
            }
            getLogger().log(record);
        }
    }

    private void getClassAndMethod() {
        try {
            Throwable throwable = new Throwable();
            throwable.fillInStackTrace();
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            throwable.printStackTrace(printWriter);
            String traceString = stringWriter.getBuffer().toString();
            StringTokenizer tokenizer = new StringTokenizer(traceString, "\n");
            tokenizer.nextToken();
            String line = tokenizer.nextToken();
            while (line.indexOf(getClass().getName()) == -1) {
                line = tokenizer.nextToken();
            }
            while (line.indexOf(getClass().getName()) >= 0) {
                line = tokenizer.nextToken();
            }
            int start = line.indexOf("at ") + 3;
            int end = line.indexOf(40);
            String temp = line.substring(start, end);
            int lastPeriod = temp.lastIndexOf(46);
            this.sourceClassName = temp.substring(0, lastPeriod);
            this.sourceMethodName = temp.substring(lastPeriod + 1);
        } catch (Exception e) {
        }
        this.classAndMethodFound = true;
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
        log(Level.FINE, String.valueOf(message), null);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable exception) {
        log(Level.FINE, String.valueOf(message), exception);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable exception) {
        log(Level.SEVERE, String.valueOf(message), exception);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
        log(Level.SEVERE, String.valueOf(message), null);
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable exception) {
        log(Level.SEVERE, String.valueOf(message), exception);
    }

    public Logger getLogger() {
        if (this.logger == null) {
            this.logger = Logger.getLogger(this.name);
        }
        return this.logger;
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
        log(Level.INFO, String.valueOf(message), null);
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable exception) {
        log(Level.INFO, String.valueOf(message), exception);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isDebugEnabled() {
        return getLogger().isLoggable(Level.FINE);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isErrorEnabled() {
        return getLogger().isLoggable(Level.SEVERE);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isFatalEnabled() {
        return getLogger().isLoggable(Level.SEVERE);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isInfoEnabled() {
        return getLogger().isLoggable(Level.INFO);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isTraceEnabled() {
        return getLogger().isLoggable(Level.FINEST);
    }

    @Override // org.apache.commons.logging.Log
    public boolean isWarnEnabled() {
        return getLogger().isLoggable(Level.WARNING);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
        log(Level.FINEST, String.valueOf(message), null);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable exception) {
        log(Level.FINEST, String.valueOf(message), exception);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
        log(Level.WARNING, String.valueOf(message), null);
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable exception) {
        log(Level.WARNING, String.valueOf(message), exception);
    }
}
