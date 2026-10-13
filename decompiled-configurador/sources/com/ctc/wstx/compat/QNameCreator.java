package com.ctc.wstx.compat;

import java.util.logging.Logger;
import javax.xml.namespace.QName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/compat/QNameCreator.class */
public final class QNameCreator {
    private static final Helper _helper;

    static {
        Helper h = null;
        try {
            Helper h0 = new Helper();
            h0.create("elem", "http://dummy", "ns");
            h = h0;
        } catch (Throwable t) {
            String msg = "Could not construct QNameCreator.Helper; assume 3-arg QName constructor not available and use 2-arg method instead. Problem: " + t.getMessage();
            try {
                Logger.getLogger("com.ctc.wstx.compat.QNameCreator").warning(msg);
            } catch (Throwable th) {
                System.err.println("ERROR: failed to log error using Logger (problem " + t.getMessage() + "), original problem: " + msg);
            }
        }
        _helper = h;
    }

    public static QName create(String uri, String localName, String prefix) {
        if (_helper == null) {
            return new QName(uri, localName);
        }
        return _helper.create(uri, localName, prefix);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/compat/QNameCreator$Helper.class */
    private static final class Helper {
        public QName create(String localName, String nsURI, String prefix) {
            return new QName(localName, nsURI, prefix);
        }
    }
}
