package com.github.markusbernhardt.proxy.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/Logger.class */
public class Logger {
    private static LogBackEnd backend;

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/Logger$LogBackEnd.class */
    public interface LogBackEnd {
        void log(Class<?> cls, LogLevel logLevel, String str, Object... objArr);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/Logger$LogLevel.class */
    public enum LogLevel {
        ERROR,
        WARNING,
        INFO,
        TRACE,
        DEBUG
    }

    /* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/Logger$Slf4jLogBackEnd.class */
    public static class Slf4jLogBackEnd implements LogBackEnd {
        protected Map<Class<?>, org.slf4j.Logger> loggers = new ConcurrentHashMap();

        @Override // com.github.markusbernhardt.proxy.util.Logger.LogBackEnd
        public void log(Class<?> clazz, LogLevel loglevel, String msg, Object... params) {
            org.slf4j.Logger log = getLogger(clazz);
            switch (loglevel) {
                case ERROR:
                    if (log.isErrorEnabled()) {
                        log.error(msg, params);
                    }
                    break;
                case WARNING:
                    if (log.isWarnEnabled()) {
                        log.warn(msg, params);
                    }
                    break;
                case INFO:
                    if (log.isInfoEnabled()) {
                        log.info(msg, params);
                    }
                    break;
                case TRACE:
                    if (log.isTraceEnabled()) {
                        log.trace(msg, params);
                    }
                    break;
                case DEBUG:
                    if (log.isDebugEnabled()) {
                        log.debug(msg, params);
                    }
                    break;
            }
        }

        protected org.slf4j.Logger getLogger(Class<?> clazz) {
            org.slf4j.Logger logger = this.loggers.get(clazz);
            if (logger == null) {
                logger = LoggerFactory.getLogger(clazz);
                this.loggers.put(clazz, logger);
            }
            return logger;
        }
    }

    public static LogBackEnd getBackend() {
        return backend;
    }

    public static void setBackend(LogBackEnd backend2) {
        backend = backend2;
    }

    public static void log(Class<?> clazz, LogLevel loglevel, String msg, Object... params) {
        if (backend != null) {
            backend.log(clazz, loglevel, msg, params);
        }
    }
}
