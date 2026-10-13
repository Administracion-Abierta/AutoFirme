package nu.xom.jaxen.saxpath.base;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/saxpath/base/Token.class */
class Token {
    private int tokenType;
    private String parseText;
    private int tokenBegin;
    private int tokenEnd;

    Token(int i, String str, int i2, int i3) {
        setTokenType(i);
        setParseText(str);
        setTokenBegin(i2);
        setTokenEnd(i3);
    }

    private void setTokenType(int i) {
        this.tokenType = i;
    }

    int getTokenType() {
        return this.tokenType;
    }

    private void setParseText(String str) {
        this.parseText = str;
    }

    String getTokenText() {
        return this.parseText.substring(getTokenBegin(), getTokenEnd());
    }

    private void setTokenBegin(int i) {
        this.tokenBegin = i;
    }

    int getTokenBegin() {
        return this.tokenBegin;
    }

    private void setTokenEnd(int i) {
        this.tokenEnd = i;
    }

    int getTokenEnd() {
        return this.tokenEnd;
    }

    public String toString() {
        return new StringBuffer().append("[ (").append(this.tokenType).append(") (").append(getTokenText()).append(")").toString();
    }
}
