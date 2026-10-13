package org.slf4j.spi;

import org.slf4j.ILoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/spi/LoggerFactoryBinder.class */
public interface LoggerFactoryBinder {
    ILoggerFactory getLoggerFactory();

    String getLoggerFactoryClassStr();
}
