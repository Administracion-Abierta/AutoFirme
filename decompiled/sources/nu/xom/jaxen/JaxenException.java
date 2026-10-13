package nu.xom.jaxen;

import nu.xom.jaxen.saxpath.SAXPathException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/JaxenException.class */
public class JaxenException extends SAXPathException {
    private static final long serialVersionUID = 7132891439526672639L;
    static double javaVersion;

    public JaxenException(String str) {
        super(str);
    }

    public JaxenException(Throwable th) {
        super(th);
    }

    public JaxenException(String str, Throwable th) {
        super(str, th);
    }

    static {
        javaVersion = 1.4d;
        try {
            javaVersion = Double.valueOf(System.getProperty("java.version").substring(0, 3)).doubleValue();
        } catch (RuntimeException e) {
        }
    }
}
