package org.slf4j;

import java.io.Closeable;
import java.util.Map;
import org.slf4j.helpers.NOPMDCAdapter;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticMDCBinder;
import org.slf4j.spi.MDCAdapter;

/* JADX INFO: loaded from: autofirma.jar:org/slf4j/MDC.class */
public class MDC {
    static final String NULL_MDCA_URL = "http://www.slf4j.org/codes.html#null_MDCA";
    static final String NO_STATIC_MDC_BINDER_URL = "http://www.slf4j.org/codes.html#no_static_mdc_binder";
    static MDCAdapter mdcAdapter;

    /* JADX INFO: loaded from: autofirma.jar:org/slf4j/MDC$MDCCloseable.class */
    public static class MDCCloseable implements Closeable {
        private final String key;

        private MDCCloseable(String key) {
            this.key = key;
        }

        @Override // java.io.Closeable, java.lang.AutoCloseable
        public void close() {
            MDC.remove(this.key);
        }
    }

    private MDC() {
    }

    private static MDCAdapter bwCompatibleGetMDCAdapterFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMDCBinder.getSingleton().getMDCA();
        } catch (NoSuchMethodError e) {
            return StaticMDCBinder.SINGLETON.getMDCA();
        }
    }

    static {
        try {
            mdcAdapter = bwCompatibleGetMDCAdapterFromBinder();
        } catch (Exception e) {
            Util.report("MDC binding unsuccessful.", e);
        } catch (NoClassDefFoundError ncde) {
            mdcAdapter = new NOPMDCAdapter();
            String msg = ncde.getMessage();
            if (msg != null && msg.contains("StaticMDCBinder")) {
                Util.report("Failed to load class \"org.slf4j.impl.StaticMDCBinder\".");
                Util.report("Defaulting to no-operation MDCAdapter implementation.");
                Util.report("See http://www.slf4j.org/codes.html#no_static_mdc_binder for further details.");
                return;
            }
            throw ncde;
        }
    }

    public static void put(String key, String val) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        mdcAdapter.put(key, val);
    }

    public static MDCCloseable putCloseable(String key, String val) throws IllegalArgumentException {
        put(key, val);
        return new MDCCloseable(key);
    }

    public static String get(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return mdcAdapter.get(key);
    }

    public static void remove(String key) throws IllegalArgumentException {
        if (key == null) {
            throw new IllegalArgumentException("key parameter cannot be null");
        }
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        mdcAdapter.remove(key);
    }

    public static void clear() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        mdcAdapter.clear();
    }

    public static Map<String, String> getCopyOfContextMap() {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        return mdcAdapter.getCopyOfContextMap();
    }

    public static void setContextMap(Map<String, String> contextMap) {
        if (mdcAdapter == null) {
            throw new IllegalStateException("MDCAdapter cannot be null. See also http://www.slf4j.org/codes.html#null_MDCA");
        }
        mdcAdapter.setContextMap(contextMap);
    }

    public static MDCAdapter getMDCAdapter() {
        return mdcAdapter;
    }
}
