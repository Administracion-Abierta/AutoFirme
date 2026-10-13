package nu.xom.jaxen.saxpath.helpers;

import nu.xom.jaxen.saxpath.SAXPathException;
import nu.xom.jaxen.saxpath.XPathReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/saxpath/helpers/XPathReaderFactory.class */
public class XPathReaderFactory {
    public static final String DRIVER_PROPERTY = "org.saxpath.driver";
    protected static final String DEFAULT_DRIVER = "nu.xom.jaxen.saxpath.base.XPathReader";
    static /* synthetic */ Class class$org$jaxen$saxpath$helpers$XPathReaderFactory;
    static /* synthetic */ Class class$org$jaxen$saxpath$XPathReader;

    private XPathReaderFactory() {
    }

    public static XPathReader createReader() throws SAXPathException {
        String property = null;
        try {
            property = System.getProperty(DRIVER_PROPERTY);
        } catch (SecurityException e) {
        }
        if (property == null || property.length() == 0) {
            property = DEFAULT_DRIVER;
        }
        return createReader(property);
    }

    public static XPathReader createReader(String str) throws SAXPathException {
        Class clsClass$;
        Class clsClass$2;
        try {
            if (class$org$jaxen$saxpath$helpers$XPathReaderFactory == null) {
                clsClass$ = class$("nu.xom.jaxen.saxpath.helpers.XPathReaderFactory");
                class$org$jaxen$saxpath$helpers$XPathReaderFactory = clsClass$;
            } else {
                clsClass$ = class$org$jaxen$saxpath$helpers$XPathReaderFactory;
            }
            Class<?> cls = Class.forName(str, true, clsClass$.getClassLoader());
            if (class$org$jaxen$saxpath$XPathReader == null) {
                clsClass$2 = class$("nu.xom.jaxen.saxpath.XPathReader");
                class$org$jaxen$saxpath$XPathReader = clsClass$2;
            } else {
                clsClass$2 = class$org$jaxen$saxpath$XPathReader;
            }
            if (!clsClass$2.isAssignableFrom(cls)) {
                throw new SAXPathException(new StringBuffer().append("Class [").append(str).append("] does not implement the org.jaxen.saxpath.XPathReader interface.").toString());
            }
            try {
                return (XPathReader) cls.newInstance();
            } catch (IllegalAccessException e) {
                throw new SAXPathException(e);
            } catch (InstantiationException e2) {
                throw new SAXPathException(e2);
            }
        } catch (ClassNotFoundException e3) {
            throw new SAXPathException(e3);
        }
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }
}
