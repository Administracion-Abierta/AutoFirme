package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.log.Hierarchy;
import org.apache.log.Logger;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/logging/impl/LogKitLogger.class */
public class LogKitLogger implements Log, Serializable {
    private static final long serialVersionUID = 3768538055836059519L;
    protected volatile transient Logger logger;
    protected String name;

    public LogKitLogger(String name) {
        this.logger = null;
        this.name = null;
        this.name = name;
        this.logger = getLogger();
    }

    public Logger getLogger() {
        Logger result = this.logger;
        if (result == null) {
            synchronized (this) {
                result = this.logger;
                if (result == null) {
                    Logger loggerFor = Hierarchy.getDefaultHierarchy().getLoggerFor(this.name);
                    result = loggerFor;
                    this.logger = loggerFor;
                }
            }
        }
        return result;
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message) {
        debug(message);
    }

    @Override // org.apache.commons.logging.Log
    public void trace(Object message, Throwable t) {
        debug(message, t);
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message) {
        if (message != null) {
            getLogger().debug(String.valueOf(message));
        }
    }

    @Override // org.apache.commons.logging.Log
    public void debug(Object message, Throwable t) {
        if (message != null) {
            getLogger().debug(String.valueOf(message), t);
        }
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message) {
        if (message != null) {
            getLogger().info(String.valueOf(message));
        }
    }

    @Override // org.apache.commons.logging.Log
    public void info(Object message, Throwable t) {
        if (message != null) {
            getLogger().info(String.valueOf(message), t);
        }
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message) {
        if (message != null) {
            getLogger().warn(String.valueOf(message));
        }
    }

    @Override // org.apache.commons.logging.Log
    public void warn(Object message, Throwable t) {
        if (message != null) {
            getLogger().warn(String.valueOf(message), t);
        }
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message) {
        if (message != null) {
            getLogger().error(String.valueOf(message));
        }
    }

    @Override // org.apache.commons.logging.Log
    public void error(Object message, Throwable t) {
        if (message != null) {
            getLogger().error(String.valueOf(message), t);
        }
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message) {
        if (message != null) {
            getLogger().fatalError(String.valueOf(message));
        }
    }

    @Override // org.apache.commons.logging.Log
    public void fatal(Object message, Throwable t) {
        if (message != null) {
            getLogger().fatalError(String.valueOf(message), t);
        }
    }

    @Override // org.apache.commons.logging.Log
    public boolean isDebugEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isErrorEnabled() {
        return getLogger().isErrorEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isFatalEnabled() {
        return getLogger().isFatalErrorEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isInfoEnabled() {
        return getLogger().isInfoEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isTraceEnabled() {
        return getLogger().isDebugEnabled();
    }

    @Override // org.apache.commons.logging.Log
    public boolean isWarnEnabled() {
        return getLogger().isWarnEnabled();
    }
}
