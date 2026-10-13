package nu.xom;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Attribute.class */
public class Attribute extends Node {
    private String localName;
    private String prefix;
    private String URI;
    private String value;
    private Type type;

    /* JADX INFO: loaded from: autofirma.jar:nu/xom/Attribute$Type.class */
    public static final class Type {
        public static final Type CDATA = new Type(1);
        public static final Type ID = new Type(2);
        public static final Type IDREF = new Type(3);
        public static final Type IDREFS = new Type(4);
        public static final Type NMTOKEN = new Type(5);
        public static final Type NMTOKENS = new Type(6);
        public static final Type NOTATION = new Type(7);
        public static final Type ENTITY = new Type(8);
        public static final Type ENTITIES = new Type(9);
        public static final Type ENUMERATION = new Type(10);
        public static final Type UNDECLARED = new Type(0);
        private final int type;

        public String getName() {
            switch (this.type) {
                case 0:
                    return "UNDECLARED";
                case 1:
                    return "CDATA";
                case 2:
                    return "ID";
                case 3:
                    return DTDParser.TYPE_IDREF;
                case 4:
                    return DTDParser.TYPE_IDREFS;
                case 5:
                    return DTDParser.TYPE_NMTOKEN;
                case 6:
                    return DTDParser.TYPE_NMTOKENS;
                case 7:
                    return DTDParser.TYPE_NOTATION;
                case 8:
                    return DTDParser.TYPE_ENTITY;
                case 9:
                    return DTDParser.TYPE_ENTITIES;
                case 10:
                    return DTDParser.TYPE_ENUMERATION;
                default:
                    throw new RuntimeException(new StringBuffer().append("Bug in XOM: unexpected attribute type: ").append(this.type).toString());
            }
        }

        private Type(int i) {
            this.type = i;
        }

        public int hashCode() {
            return this.type;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            return obj != null && hashCode() == obj.hashCode() && obj.getClass().getName().equals("nu.xom.Attribute.Type");
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer("[Attribute.Type: ");
            stringBuffer.append(getName());
            stringBuffer.append(']');
            return stringBuffer.toString();
        }
    }

    public Attribute(String str, String str2) {
        this(str, "", str2, Type.UNDECLARED);
    }

    public Attribute(String str, String str2, Type type) {
        this(str, "", str2, type);
    }

    public Attribute(String str, String str2, String str3) {
        this(str, str2, str3, Type.UNDECLARED);
    }

    public Attribute(String str, String str2, String str3, Type type) {
        this.value = "";
        this.prefix = "";
        String strSubstring = str;
        int iIndexOf = str.indexOf(58);
        if (iIndexOf > 0) {
            this.prefix = str.substring(0, iIndexOf);
            strSubstring = str.substring(iIndexOf + 1);
        }
        try {
            _setLocalName(strSubstring);
            _setNamespace(this.prefix, str2);
            _setValue(str3);
            if (isXMLID()) {
                _setType(Type.ID);
            } else {
                _setType(type);
            }
        } catch (IllegalNameException e) {
            e.setData(str);
            throw e;
        }
    }

    public Attribute(Attribute attribute) {
        this.value = "";
        this.localName = attribute.localName;
        this.prefix = attribute.prefix;
        this.URI = attribute.URI;
        this.value = attribute.value;
        this.type = attribute.type;
    }

    private Attribute() {
        this.value = "";
    }

    static Attribute build(String str, String str2, String str3, Type type, String str4) {
        Attribute attribute = new Attribute();
        String strSubstring = "";
        int iIndexOf = str.indexOf(58);
        if (iIndexOf >= 0) {
            strSubstring = str.substring(0, iIndexOf);
            if ("xml:id".equals(str)) {
                type = Type.ID;
                str3 = normalize(str3);
            }
        }
        attribute.localName = str4;
        attribute.prefix = strSubstring;
        attribute.type = type;
        attribute.URI = str2;
        attribute.value = str3;
        return attribute;
    }

    private static String normalize(String str) {
        int length = str.length();
        int i = 0;
        while (i < length && str.charAt(i) == ' ') {
            i++;
        }
        String strSubstring = str.substring(i);
        int length2 = strSubstring.length() - 1;
        while (length2 > 0 && strSubstring.charAt(length2) == ' ') {
            length2--;
        }
        String strSubstring2 = strSubstring.substring(0, length2 + 1);
        int length3 = strSubstring2.length();
        StringBuffer stringBuffer = new StringBuffer(length3);
        boolean z = false;
        for (int i2 = 0; i2 < length3; i2++) {
            char cCharAt = strSubstring2.charAt(i2);
            if (cCharAt != ' ') {
                stringBuffer.append(cCharAt);
                z = false;
            } else if (!z) {
                stringBuffer.append(' ');
                z = true;
            }
        }
        return stringBuffer.toString();
    }

    public final Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        if (type == null) {
            throw new NullPointerException("Null attribute type");
        }
        if (isXMLID() && !Type.ID.equals(type)) {
            throw new IllegalDataException(new StringBuffer().append("Can't change type of xml:id attribute to ").append(type).toString());
        }
        _setType(type);
    }

    private boolean isXMLID() {
        return "xml".equals(this.prefix) && ElementTags.ID.equals(this.localName);
    }

    private void _setType(Type type) {
        this.type = type;
    }

    @Override // nu.xom.Node
    public final String getValue() {
        return this.value;
    }

    public void setValue(String str) {
        _setValue(str);
    }

    private void _setValue(String str) {
        Verifier.checkPCDATA(str);
        if (isXMLID()) {
            str = normalize(str);
        }
        this.value = str;
    }

    public final String getLocalName() {
        return this.localName;
    }

    public void setLocalName(String str) {
        if (ElementTags.ID.equals(str) && "http://www.w3.org/XML/1998/namespace".equals(this.URI)) {
            Verifier.checkNCName(this.value);
        }
        _setLocalName(str);
        if (isXMLID()) {
            setType(Type.ID);
        }
    }

    private void _setLocalName(String str) {
        Verifier.checkNCName(str);
        if (str.equals(CanonicalizerBase.XMLNS)) {
            throw new IllegalNameException("The Attribute class is not used for namespace declaration attributes.");
        }
        this.localName = str;
    }

    public final String getQualifiedName() {
        return this.prefix.length() == 0 ? this.localName : new StringBuffer().append(this.prefix).append(":").append(this.localName).toString();
    }

    public final String getNamespaceURI() {
        return this.URI;
    }

    public final String getNamespacePrefix() {
        return this.prefix;
    }

    public void setNamespace(String str, String str2) {
        _setNamespace(str, str2);
        if (isXMLID()) {
            setType(Type.ID);
        }
    }

    private void _setNamespace(String str, String str2) {
        String localNamespaceURI;
        if (str2 == null) {
            str2 = "";
        }
        if (str == null) {
            str = "";
        }
        if (str.equals(CanonicalizerBase.XMLNS)) {
            throw new IllegalNameException("Attribute objects are not used to represent  namespace declarations");
        }
        if (str.equals("xml") && !str2.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new NamespaceConflictException(new StringBuffer().append("Wrong namespace URI for xml prefix: ").append(str2).toString());
        }
        if (str2.equals("http://www.w3.org/XML/1998/namespace") && !str.equals("xml")) {
            throw new NamespaceConflictException(new StringBuffer().append("Wrong prefix for the XML namespace: ").append(str).toString());
        }
        if (str.length() == 0) {
            if (str2.length() != 0) {
                throw new NamespaceConflictException(new StringBuffer().append("Unprefixed attribute ").append(this.localName).append(" cannot be in default namespace ").append(str2).toString());
            }
            this.prefix = "";
            this.URI = "";
            return;
        }
        if (str2.length() == 0) {
            throw new NamespaceConflictException("Attribute prefixes must be declared.");
        }
        ParentNode parent = getParent();
        if (parent != null && (localNamespaceURI = ((Element) parent).getLocalNamespaceURI(str)) != null && !localNamespaceURI.equals(str2)) {
            throw new NamespaceConflictException(new StringBuffer().append("New prefix ").append(str).append("conflicts with existing namespace declaration").toString());
        }
        Verifier.checkAbsoluteURIReference(str2);
        Verifier.checkNCName(str);
        this.URI = str2;
        this.prefix = str;
    }

    @Override // nu.xom.Node
    public final Node getChild(int i) {
        throw new IndexOutOfBoundsException("Attributes do not have children");
    }

    @Override // nu.xom.Node
    public final int getChildCount() {
        return 0;
    }

    @Override // nu.xom.Node
    public Node copy() {
        return new Attribute(this);
    }

    @Override // nu.xom.Node
    public final String toXML() {
        return new StringBuffer().append(getQualifiedName()).append("=\"").append(escapeText(this.value)).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString();
    }

    public final String toString() {
        return new StringBuffer().append("[").append(getClass().getName()).append(": ").append(getQualifiedName()).append("=\"").append(Text.escapeLineBreaksAndTruncate(getValue())).append("\"]").toString();
    }

    private static String escapeText(String str) {
        int length = str.length();
        StringBuffer stringBuffer = new StringBuffer(length + 12);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            switch (cCharAt) {
                case '\t':
                    stringBuffer.append("&#x09;");
                    break;
                case '\n':
                    stringBuffer.append("&#x0A;");
                    break;
                case 11:
                case '\f':
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
                case '\r':
                    stringBuffer.append("&#x0D;");
                    break;
                case ' ':
                    stringBuffer.append(' ');
                    break;
                case '!':
                    stringBuffer.append('!');
                    break;
                case '\"':
                    stringBuffer.append("&quot;");
                    break;
                case '#':
                    stringBuffer.append('#');
                    break;
                case '$':
                    stringBuffer.append('$');
                    break;
                case '%':
                    stringBuffer.append('%');
                    break;
                case '&':
                    stringBuffer.append("&amp;");
                    break;
                case '\'':
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
                case '>':
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
    boolean isAttribute() {
        return true;
    }
}
