package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/XPathTypeException.class */
public class XPathTypeException extends XPathException {
    private static final long serialVersionUID = 1247817719683497718L;
    private final Object returnValue;

    XPathTypeException(Object obj) {
        super("");
        this.returnValue = obj;
    }

    public Object getReturnValue() {
        return this.returnValue;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        String xPath = getXPath();
        String name = this.returnValue.getClass().getName();
        return xPath == null ? new StringBuffer().append("XPath expression returned a ").append(name).append(" instead of a node-set.").toString() : new StringBuffer().append("XPath expression ").append(xPath).append(" returned a ").append(name).append(" instead of a node-set.").toString();
    }
}
