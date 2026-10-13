package nu.xom;

import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/ProcessingInstruction.class */
public class ProcessingInstruction extends Node {
    private String target;
    private String data;

    public ProcessingInstruction(String str, String str2) {
        _setTarget(str);
        _setValue(str2);
    }

    public ProcessingInstruction(ProcessingInstruction processingInstruction) {
        this.target = processingInstruction.target;
        this.data = processingInstruction.data;
    }

    private ProcessingInstruction() {
    }

    static ProcessingInstruction build(String str, String str2) {
        ProcessingInstruction processingInstruction = new ProcessingInstruction();
        processingInstruction.target = str;
        processingInstruction.data = str2;
        return processingInstruction;
    }

    public final String getTarget() {
        return this.target;
    }

    public void setTarget(String str) {
        _setTarget(str);
    }

    private void _setTarget(String str) {
        try {
            Verifier.checkNCName(str);
            if (!str.equalsIgnoreCase(CanonicalizerBase.XML)) {
                this.target = str;
            } else {
                IllegalTargetException illegalTargetException = new IllegalTargetException(new StringBuffer().append(str).append(" is not a legal processing instruction target.").toString());
                illegalTargetException.setData(str);
                throw illegalTargetException;
            }
        } catch (IllegalNameException e) {
            IllegalTargetException illegalTargetException2 = new IllegalTargetException(e.getMessage());
            illegalTargetException2.setData(str);
            throw illegalTargetException2;
        }
    }

    public void setValue(String str) {
        _setValue(str);
    }

    private void _setValue(String str) {
        Verifier.checkPCDATA(str);
        if (str.length() != 0) {
            if (str.indexOf("?>") >= 0) {
                IllegalDataException illegalDataException = new IllegalDataException("Processing instruction data must not contain \"?>\"");
                illegalDataException.setData(str);
                throw illegalDataException;
            }
            if (str.indexOf(13) >= 0) {
                IllegalDataException illegalDataException2 = new IllegalDataException("Processing instruction data cannot contain carriage returns");
                illegalDataException2.setData(str);
                throw illegalDataException2;
            }
            char cCharAt = str.charAt(0);
            if (cCharAt == ' ' || cCharAt == '\n' || cCharAt == '\t') {
                IllegalDataException illegalDataException3 = new IllegalDataException("Processing instruction data cannot contain leading white space");
                illegalDataException3.setData(str);
                throw illegalDataException3;
            }
        }
        this.data = str;
    }

    @Override // nu.xom.Node
    public final String getValue() {
        return this.data;
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
    public final String toXML() {
        StringBuffer stringBuffer = new StringBuffer("<?");
        stringBuffer.append(this.target);
        if (this.data.length() > 0) {
            stringBuffer.append(' ');
            stringBuffer.append(this.data);
        }
        stringBuffer.append("?>");
        return stringBuffer.toString();
    }

    @Override // nu.xom.Node
    public Node copy() {
        return new ProcessingInstruction(this.target, this.data);
    }

    @Override // nu.xom.Node
    boolean isProcessingInstruction() {
        return true;
    }

    public final String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": target=\"").append(this.target).append("\"; data=\"").append(Text.escapeLineBreaksAndTruncate(this.data)).append("\"]").toString();
    }
}
