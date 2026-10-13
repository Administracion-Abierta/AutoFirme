package org.apache.xml.security.utils;

import java.security.AccessController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/IgnoreAllErrorHandler.class */
public class IgnoreAllErrorHandler implements ErrorHandler {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) IgnoreAllErrorHandler.class);
    private static final boolean warnOnExceptions = getProperty("org.apache.xml.security.test.warn.on.exceptions");
    private static final boolean throwExceptions = getProperty("org.apache.xml.security.test.throw.exceptions");

    private static boolean getProperty(String name) {
        return ((Boolean) AccessController.doPrivileged(() -> {
            return Boolean.valueOf(Boolean.getBoolean(name));
        })).booleanValue();
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException ex) throws SAXException {
        if (warnOnExceptions) {
            LOG.warn("", (Throwable) ex);
        }
        if (throwExceptions) {
            throw ex;
        }
    }

    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException ex) throws SAXException {
        if (warnOnExceptions) {
            LOG.error("", (Throwable) ex);
        }
        if (throwExceptions) {
            throw ex;
        }
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException ex) throws SAXException {
        if (warnOnExceptions) {
            LOG.warn("", (Throwable) ex);
        }
        if (throwExceptions) {
            throw ex;
        }
    }
}
