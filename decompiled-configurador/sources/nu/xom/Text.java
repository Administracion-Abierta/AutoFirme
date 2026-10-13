package nu.xom;

import java.io.UnsupportedEncodingException;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/Text.class */
public class Text extends Node {
    private byte[] data;

    public Text(String str) {
        _setValue(str);
    }

    public Text(Text text) {
        this.data = text.data;
    }

    private Text() {
    }

    static Text build(String str) {
        Text text = new Text();
        try {
            text.data = str.getBytes("UTF8");
            return text;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Bad VM! Does not support UTF-8");
        }
    }

    public void setValue(String str) {
        _setValue(str);
    }

    private void _setValue(String str) {
        if (str == null) {
            str = "";
        } else {
            Verifier.checkPCDATA(str);
        }
        try {
            this.data = str.getBytes("UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Bad VM! Does not support UTF-8");
        }
    }

    @Override // nu.xom.Node
    public final String getValue() {
        try {
            return new String(this.data, "UTF8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Bad VM! Does not support UTF-8");
        }
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
        return isCDATASection() ? new CDATASection(this) : new Text(this);
    }

    @Override // nu.xom.Node
    public final String toXML() {
        return escapeText();
    }

    String escapeText() {
        String value = getValue();
        int length = value.length();
        StringBuffer stringBuffer = new StringBuffer(length + 12);
        for (int i = 0; i < length; i++) {
            char cCharAt = value.charAt(i);
            switch (cCharAt) {
                case '\r':
                    stringBuffer.append("&#x0D;");
                    break;
                case 14:
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                    break;
                case ' ':
                    stringBuffer.append(' ');
                    break;
                case '!':
                    stringBuffer.append('!');
                    break;
                case '\"':
                    stringBuffer.append('\"');
                    break;
                case '#':
                    stringBuffer.append('#');
                    break;
                case EACTags.APPLICATION_EXPIRATION_DATE /* 36 */:
                    stringBuffer.append('$');
                    break;
                case EACTags.APPLICATION_EFFECTIVE_DATE /* 37 */:
                    stringBuffer.append('%');
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    stringBuffer.append("&amp;");
                    break;
                case EACTags.INTERCHANGE_CONTROL /* 39 */:
                    stringBuffer.append('\'');
                    break;
                case '(':
                    stringBuffer.append('(');
                    break;
                case ')':
                    stringBuffer.append(')');
                    break;
                case '*':
                    stringBuffer.append('*');
                    break;
                case '+':
                    stringBuffer.append('+');
                    break;
                case ',':
                    stringBuffer.append(',');
                    break;
                case '-':
                    stringBuffer.append('-');
                    break;
                case '.':
                    stringBuffer.append('.');
                    break;
                case '/':
                    stringBuffer.append('/');
                    break;
                case '0':
                    stringBuffer.append('0');
                    break;
                case '1':
                    stringBuffer.append('1');
                    break;
                case '2':
                    stringBuffer.append('2');
                    break;
                case '3':
                    stringBuffer.append('3');
                    break;
                case '4':
                    stringBuffer.append('4');
                    break;
                case '5':
                    stringBuffer.append('5');
                    break;
                case '6':
                    stringBuffer.append('6');
                    break;
                case '7':
                    stringBuffer.append('7');
                    break;
                case '8':
                    stringBuffer.append('8');
                    break;
                case '9':
                    stringBuffer.append('9');
                    break;
                case ':':
                    stringBuffer.append(':');
                    break;
                case ';':
                    stringBuffer.append(';');
                    break;
                case '<':
                    stringBuffer.append("&lt;");
                    break;
                case '=':
                    stringBuffer.append('=');
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    stringBuffer.append("&gt;");
                    break;
                default:
                    stringBuffer.append(cCharAt);
                    break;
            }
        }
        return stringBuffer.toString();
    }

    @Override // nu.xom.Node
    boolean isText() {
        return true;
    }

    public final String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(escapeLineBreaksAndTruncate(getValue())).append("]").toString();
    }

    static String escapeLineBreaksAndTruncate(String str) {
        int length = str.length();
        boolean z = length > 40;
        if (length > 40) {
            length = 35;
            str = str.substring(0, 35);
        }
        StringBuffer stringBuffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case '\t':
                    stringBuffer.append("\\t");
                    break;
                case '\n':
                    stringBuffer.append("\\n");
                    break;
                case 11:
                case '\f':
                default:
                    stringBuffer.append(cCharAt);
                    break;
                case '\r':
                    stringBuffer.append("\\r");
                    break;
            }
        }
        if (z) {
            stringBuffer.append("...");
        }
        return stringBuffer.toString();
    }

    boolean isCDATASection() {
        return false;
    }

    boolean isEmpty() {
        return this.data.length == 0;
    }
}
