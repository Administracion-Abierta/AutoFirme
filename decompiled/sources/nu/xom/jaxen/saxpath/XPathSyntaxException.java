package nu.xom.jaxen.saxpath;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/XPathSyntaxException.class */
public class XPathSyntaxException extends SAXPathException {
    private static final long serialVersionUID = 3567675610742422397L;
    private String xpath;
    private int position;
    private static final String lineSeparator = System.getProperty("line.separator");

    public XPathSyntaxException(String str, int i, String str2) {
        super(str2);
        this.position = i;
        this.xpath = str;
    }

    public int getPosition() {
        return this.position;
    }

    public String getXPath() {
        return this.xpath;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return new StringBuffer().append(getClass()).append(": ").append(getXPath()).append(": ").append(getPosition()).append(": ").append(getMessage()).toString();
    }

    private String getPositionMarker() {
        int position = getPosition();
        StringBuffer stringBuffer = new StringBuffer(position + 1);
        for (int i = 0; i < position; i++) {
            stringBuffer.append(" ");
        }
        stringBuffer.append("^");
        return stringBuffer.toString();
    }

    public String getMultilineMessage() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(getMessage());
        stringBuffer.append(lineSeparator);
        stringBuffer.append(getXPath());
        stringBuffer.append(lineSeparator);
        stringBuffer.append(getPositionMarker());
        return stringBuffer.toString();
    }
}
