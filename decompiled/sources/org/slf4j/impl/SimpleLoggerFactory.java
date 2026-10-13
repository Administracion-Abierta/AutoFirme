package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/impl/SimpleLoggerFactory.class */
public class SimpleLoggerFactory implements ILoggerFactory {
    ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap();

    public SimpleLoggerFactory() {
        SimpleLogger.lazyInit();
    }

    @Override // org.slf4j.ILoggerFactory
    public Logger getLogger(String name) {
        Logger simpleLogger = this.loggerMap.get(name);
        if (simpleLogger != null) {
            return simpleLogger;
        }
        Logger newInstance = new SimpleLogger(name);
        Logger oldInstance = this.loggerMap.putIfAbsent(name, newInstance);
        return oldInstance == null ? newInstance : oldInstance;
    }

    void reset() {
        this.loggerMap.clear();
    }
}
