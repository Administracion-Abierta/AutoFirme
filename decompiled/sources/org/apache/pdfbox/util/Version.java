package org.apache.pdfbox.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.pdfbox.io.IOUtils;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/Version.class */
public final class Version {
    private static final String PDFBOX_VERSION_PROPERTIES = "/org/apache/pdfbox/resources/version.properties";

    private Version() {
    }

    public static String getVersion() {
        InputStream is;
        InputStream is2 = null;
        try {
            is = new BufferedInputStream(Version.class.getResourceAsStream(PDFBOX_VERSION_PROPERTIES));
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty("pdfbox.version", null);
        } catch (IOException e) {
            is = is2;
            return null;
        } finally {
            IOUtils.closeQuietly(is2);
        }
    }
}
