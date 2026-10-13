package nu.xom;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Comment.class */
public class Comment extends Node {
    private String data;

    public Comment(String str) {
        _setValue(str);
    }

    public Comment(Comment comment) {
        this.data = comment.data;
    }

    private Comment() {
    }

    static Comment build(String str) {
        Comment comment = new Comment();
        comment.data = str;
        return comment;
    }

    @Override // nu.xom.Node
    public final String getValue() {
        return this.data;
    }

    public void setValue(String str) {
        _setValue(str);
    }

    private void _setValue(String str) {
        if (str == null) {
            str = "";
        } else {
            Verifier.checkPCDATA(str);
            if (str.indexOf("--") != -1) {
                IllegalDataException illegalDataException = new IllegalDataException("Comment data contains a double hyphen (--).");
                illegalDataException.setData(str);
                throw illegalDataException;
            }
            if (str.indexOf(13) != -1) {
                IllegalDataException illegalDataException2 = new IllegalDataException("Comment data cannot contain carriage returns.");
                illegalDataException2.setData(str);
                throw illegalDataException2;
            }
            if (str.endsWith("-")) {
                IllegalDataException illegalDataException3 = new IllegalDataException("Comment data ends with a hyphen.");
                illegalDataException3.setData(str);
                throw illegalDataException3;
            }
        }
        this.data = str;
    }

    @Override // nu.xom.Node
    public final Node getChild(int i) {
        throw new IndexOutOfBoundsException("LeafNodes do not have children");
    }

    @Override // nu.xom.Node
    public final int getChildCount() {
        return 0;
    }

    @Override // nu.xom.Node
    public Node copy() {
        return new Comment(this.data);
    }

    @Override // nu.xom.Node
    public final String toXML() {
        StringBuffer stringBuffer = new StringBuffer("<!--");
        stringBuffer.append(this.data);
        stringBuffer.append("-->");
        return stringBuffer.toString();
    }

    public final String toString() {
        String value = getValue();
        return value.length() <= 40 ? new StringBuffer().append("[").append(getClass().getName()).append(": ").append(Text.escapeLineBreaksAndTruncate(value)).append("]").toString() : new StringBuffer().append("[").append(getClass().getName()).append(": ").append(Text.escapeLineBreaksAndTruncate(value.substring(0, 35))).append("...]").toString();
    }

    @Override // nu.xom.Node
    boolean isComment() {
        return true;
    }
}
