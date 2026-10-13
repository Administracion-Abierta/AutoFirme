package nu.xom.jaxen;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/XPathSyntaxException.class */
public class XPathSyntaxException extends JaxenException {
    private static final long serialVersionUID = 1980601567207604059L;
    private String xpath;
    private int position;

    public XPathSyntaxException(nu.xom.jaxen.saxpath.XPathSyntaxException xPathSyntaxException) {
        super(xPathSyntaxException);
        this.xpath = xPathSyntaxException.getXPath();
        this.position = xPathSyntaxException.getPosition();
    }

    public XPathSyntaxException(String str, int i, String str2) {
        super(str2);
        this.xpath = str;
        this.position = i;
    }

    public int getPosition() {
        return this.position;
    }

    public String getXPath() {
        return this.xpath;
    }

    public String getPositionMarker() {
        StringBuffer stringBuffer = new StringBuffer();
        int position = getPosition();
        for (int i = 0; i < position; i++) {
            stringBuffer.append(" ");
        }
        stringBuffer.append("^");
        return stringBuffer.toString();
    }

    public String getMultilineMessage() {
        StringBuffer stringBuffer = new StringBuffer(getMessage());
        stringBuffer.append("\n");
        stringBuffer.append(getXPath());
        stringBuffer.append("\n");
        stringBuffer.append(getPositionMarker());
        return stringBuffer.toString();
    }
}
