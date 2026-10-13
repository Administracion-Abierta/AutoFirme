package es.gob.afirma.keystores.filters.rfc;

import java.util.StringTokenizer;
import java.util.Vector;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InvalidSearchFilterException;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SearchFilter.class */
final class SearchFilter implements AttrFilter {
    String filter;
    int pos = 0;
    private final StringFilter rootFilter;
    protected static final char BEGIN_FILTER_TOKEN = '(';
    protected static final char END_FILTER_TOKEN = ')';
    protected static final char AND_TOKEN = '&';
    protected static final char OR_TOKEN = '|';
    protected static final char NOT_TOKEN = '!';
    protected static final char EQUAL_TOKEN = '=';
    protected static final char APPROX_TOKEN = '~';
    protected static final char LESS_TOKEN = '<';
    protected static final char GREATER_TOKEN = '>';
    protected static final char EXTEND_TOKEN = ':';
    protected static final char WILDCARD_TOKEN = '*';
    static final int EQUAL_MATCH = 1;
    static final int APPROX_MATCH = 2;
    static final int GREATER_MATCH = 3;
    static final int LESS_MATCH = 4;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SearchFilter$StringFilter.class */
    interface StringFilter extends AttrFilter {
        void parse() throws InvalidSearchFilterException;
    }

    SearchFilter(String str) throws InvalidSearchFilterException {
        this.filter = str;
        normalizeFilter();
        this.rootFilter = createNextFilter();
    }

    @Override // es.gob.afirma.keystores.filters.rfc.AttrFilter
    public boolean check(Attributes attributes) throws NamingException {
        if (attributes == null) {
            return false;
        }
        return this.rootFilter.check(attributes);
    }

    protected void normalizeFilter() {
        skipWhiteSpace();
        if (getCurrentChar() != '(') {
            this.filter = '(' + this.filter + ')';
        }
    }

    void skipWhiteSpace() {
        while (Character.isWhitespace(getCurrentChar())) {
            consumeChar();
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.directory.InvalidSearchFilterException */
    protected StringFilter createNextFilter() throws InvalidSearchFilterException {
        StringFilter atomicFilter;
        skipWhiteSpace();
        try {
            if (getCurrentChar() != '(') {
                throw new InvalidSearchFilterException("expected \"(\" at position " + this.pos);
            }
            consumeChar();
            skipWhiteSpace();
            switch (getCurrentChar()) {
                case '!':
                    atomicFilter = new NotFilter();
                    atomicFilter.parse();
                    break;
                case '&':
                    atomicFilter = new CompoundFilter(true);
                    atomicFilter.parse();
                    break;
                case '|':
                    atomicFilter = new CompoundFilter(false);
                    atomicFilter.parse();
                    break;
                default:
                    atomicFilter = new AtomicFilter();
                    atomicFilter.parse();
                    break;
            }
            skipWhiteSpace();
            if (getCurrentChar() != ')') {
                throw new InvalidSearchFilterException("expected \")\" at position " + this.pos);
            }
            consumeChar();
            return atomicFilter;
        } catch (InvalidSearchFilterException e) {
            throw e;
        } catch (Exception e2) {
            throw new InvalidSearchFilterException("Unable to parse character " + this.pos + " in \"" + this.filter + "\": " + e2);
        }
    }

    protected char getCurrentChar() {
        return this.filter.charAt(this.pos);
    }

    protected char relCharAt(int i) {
        return this.filter.charAt(this.pos + i);
    }

    protected void consumeChar() {
        this.pos++;
    }

    protected void consumeChars(int i) {
        this.pos += i;
    }

    protected int relIndexOf(int i) {
        return this.filter.indexOf(i, this.pos) - this.pos;
    }

    protected String relSubstring(int i, int i2) {
        return this.filter.substring(i + this.pos, i2 + this.pos);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SearchFilter$CompoundFilter.class */
    final class CompoundFilter implements StringFilter {
        private final Vector<StringFilter> subFilters = new Vector<>();
        private final boolean polarity;

        CompoundFilter(boolean z) {
            this.polarity = z;
        }

        @Override // es.gob.afirma.keystores.filters.rfc.SearchFilter.StringFilter
        public void parse() throws InvalidSearchFilterException {
            SearchFilter.this.consumeChar();
            while (SearchFilter.this.getCurrentChar() != ')') {
                this.subFilters.addElement(SearchFilter.this.createNextFilter());
                SearchFilter.this.skipWhiteSpace();
            }
        }

        @Override // es.gob.afirma.keystores.filters.rfc.AttrFilter
        public boolean check(Attributes attributes) throws NamingException {
            for (int i = 0; i < this.subFilters.size(); i++) {
                if (this.subFilters.elementAt(i).check(attributes) != this.polarity) {
                    return !this.polarity;
                }
            }
            return this.polarity;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SearchFilter$NotFilter.class */
    final class NotFilter implements StringFilter {
        private StringFilter fltr;

        NotFilter() {
        }

        @Override // es.gob.afirma.keystores.filters.rfc.SearchFilter.StringFilter
        public void parse() throws InvalidSearchFilterException {
            SearchFilter.this.consumeChar();
            this.fltr = SearchFilter.this.createNextFilter();
        }

        @Override // es.gob.afirma.keystores.filters.rfc.AttrFilter
        public boolean check(Attributes attributes) throws NamingException {
            return !this.fltr.check(attributes);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SearchFilter$AtomicFilter.class */
    final class AtomicFilter implements StringFilter {
        private String attrID;
        private String value;
        private int matchType;

        AtomicFilter() {
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.OperationNotSupportedException */
        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.directory.InvalidSearchFilterException */
        @Override // es.gob.afirma.keystores.filters.rfc.SearchFilter.StringFilter
        public void parse() throws OperationNotSupportedException, InvalidSearchFilterException {
            SearchFilter.this.skipWhiteSpace();
            try {
                int iRelIndexOf = SearchFilter.this.relIndexOf(41);
                int iRelIndexOf2 = SearchFilter.this.relIndexOf(61);
                switch (SearchFilter.this.relCharAt(iRelIndexOf2 - 1)) {
                    case ':':
                        throw new OperationNotSupportedException("Extensible match not supported");
                    case '<':
                        this.matchType = 4;
                        this.attrID = SearchFilter.this.relSubstring(0, iRelIndexOf2 - 1);
                        this.value = SearchFilter.this.relSubstring(iRelIndexOf2 + 1, iRelIndexOf);
                        break;
                    case '>':
                        this.matchType = 3;
                        this.attrID = SearchFilter.this.relSubstring(0, iRelIndexOf2 - 1);
                        this.value = SearchFilter.this.relSubstring(iRelIndexOf2 + 1, iRelIndexOf);
                        break;
                    case '~':
                        this.matchType = 2;
                        this.attrID = SearchFilter.this.relSubstring(0, iRelIndexOf2 - 1);
                        this.value = SearchFilter.this.relSubstring(iRelIndexOf2 + 1, iRelIndexOf);
                        break;
                    default:
                        this.matchType = 1;
                        this.attrID = SearchFilter.this.relSubstring(0, iRelIndexOf2);
                        this.value = SearchFilter.this.relSubstring(iRelIndexOf2 + 1, iRelIndexOf);
                        break;
                }
                this.attrID = this.attrID.trim();
                this.value = this.value.trim();
                SearchFilter.this.consumeChars(iRelIndexOf);
            } catch (Exception e) {
                InvalidSearchFilterException invalidSearchFilterException = new InvalidSearchFilterException("Unable to parse character " + SearchFilter.this.pos + " in \"" + SearchFilter.this.filter + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                invalidSearchFilterException.setRootCause(e);
                throw invalidSearchFilterException;
            }
        }

        @Override // es.gob.afirma.keystores.filters.rfc.AttrFilter
        public boolean check(Attributes attributes) {
            try {
                Attribute attribute = attributes.get(this.attrID);
                if (attribute == null) {
                    return false;
                }
                NamingEnumeration all = attribute.getAll();
                while (all.hasMoreElements()) {
                    String string = all.nextElement().toString();
                    switch (this.matchType) {
                        case 1:
                        case 2:
                            if (substringMatch(this.value, string)) {
                                return true;
                            }
                            break;
                        case 3:
                            if (string.compareTo(this.value) >= 0) {
                                return true;
                            }
                            break;
                        case 4:
                            if (string.compareTo(this.value) <= 0) {
                                return true;
                            }
                            break;
                    }
                }
                return false;
            } catch (NamingException e) {
                return false;
            }
        }

        private boolean substringMatch(String str, String str2) {
            Character ch = '*';
            if (str.equals(ch.toString())) {
                return true;
            }
            if (str.indexOf(42) == -1) {
                return str.equalsIgnoreCase(str2);
            }
            int length = 0;
            StringTokenizer stringTokenizer = new StringTokenizer(str, "*", false);
            if (str.charAt(0) != '*' && !str2.toString().toLowerCase().startsWith(stringTokenizer.nextToken().toLowerCase())) {
                return false;
            }
            while (stringTokenizer.hasMoreTokens()) {
                String strNextToken = stringTokenizer.nextToken();
                int iIndexOf = str2.toLowerCase().indexOf(strNextToken.toLowerCase(), length);
                if (iIndexOf == -1) {
                    return false;
                }
                length = iIndexOf + strNextToken.length();
            }
            if (str.charAt(str.length() - 1) != '*' && length != str2.length()) {
                return false;
            }
            return true;
        }
    }

    static String format(Attributes attributes) throws NamingException {
        if (attributes == null || attributes.size() == 0) {
            return "objectClass=*";
        }
        String str = "(& ";
        NamingEnumeration all = attributes.getAll();
        while (all.hasMore()) {
            Attribute attribute = (Attribute) all.next();
            if (attribute.size() == 0 || (attribute.size() == 1 && attribute.get() == null)) {
                str = str + "(" + attribute.getID() + "=*)";
            } else {
                NamingEnumeration all2 = attribute.getAll();
                while (all2.hasMore()) {
                    String encodedStringRep = getEncodedStringRep(all2.next());
                    if (encodedStringRep != null) {
                        str = str + "(" + attribute.getID() + "=" + encodedStringRep + ")";
                    }
                }
            }
        }
        return str + ")";
    }

    private static void hexDigit(StringBuffer stringBuffer, byte b) {
        char c;
        char c2;
        char c3 = (char) ((b >> 4) & 15);
        if (c3 > '\t') {
            c = (char) ((c3 - '\n') + 65);
        } else {
            c = (char) (c3 + '0');
        }
        stringBuffer.append(c);
        char c4 = (char) (b & 15);
        if (c4 > '\t') {
            c2 = (char) ((c4 - '\n') + 65);
        } else {
            c2 = (char) (c4 + '0');
        }
        stringBuffer.append(c2);
    }

    private static String getEncodedStringRep(Object obj) {
        String string;
        if (obj == null) {
            return null;
        }
        if (obj instanceof byte[]) {
            byte[] bArr = (byte[]) obj;
            StringBuffer stringBuffer = new StringBuffer(bArr.length * 3);
            for (byte b : bArr) {
                stringBuffer.append('\\');
                hexDigit(stringBuffer, b);
            }
            return stringBuffer.toString();
        }
        if (!(obj instanceof String)) {
            string = obj.toString();
        } else {
            string = (String) obj;
        }
        int length = string.length();
        StringBuffer stringBuffer2 = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = string.charAt(i);
            switch (cCharAt) {
                case 0:
                    stringBuffer2.append("\\00");
                    break;
                case '(':
                    stringBuffer2.append("\\28");
                    break;
                case ')':
                    stringBuffer2.append("\\29");
                    break;
                case '*':
                    stringBuffer2.append("\\2a");
                    break;
                case '\\':
                    stringBuffer2.append("\\5c");
                    break;
                default:
                    stringBuffer2.append(cCharAt);
                    break;
            }
        }
        return stringBuffer2.toString();
    }

    static int findUnescaped(char c, String str, int i) {
        int length = str.length();
        int i2 = i;
        while (true) {
            int i3 = i2;
            if (i3 < length) {
                int iIndexOf = str.indexOf(c, i3);
                if (iIndexOf == i3 || iIndexOf == -1 || str.charAt(iIndexOf - 1) != '\\') {
                    return iIndexOf;
                }
                i2 = iIndexOf + 1;
            } else {
                return -1;
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.directory.InvalidSearchFilterException */
    static String format(String str, Object[] objArr) throws InvalidSearchFilterException, NamingException {
        int i = 0;
        StringBuffer stringBuffer = new StringBuffer(str.length());
        while (true) {
            int iFindUnescaped = findUnescaped('{', str, i);
            if (iFindUnescaped >= 0) {
                int i2 = iFindUnescaped + 1;
                int iIndexOf = str.indexOf(125, i2);
                if (iIndexOf < 0) {
                    throw new InvalidSearchFilterException("unbalanced {: " + str);
                }
                try {
                    int i3 = Integer.parseInt(str.substring(i2, iIndexOf));
                    if (i3 >= objArr.length) {
                        throw new InvalidSearchFilterException("number exceeds argument list: " + i3);
                    }
                    stringBuffer.append(str.substring(i, iFindUnescaped)).append(getEncodedStringRep(objArr[i3]));
                    i = iIndexOf + 1;
                } catch (NumberFormatException e) {
                    throw new InvalidSearchFilterException("integer expected inside {}: '" + str + "': " + e);
                }
            } else {
                if (i < str.length()) {
                    stringBuffer.append(str.substring(i));
                }
                return stringBuffer.toString();
            }
        }
    }

    static Attributes selectAttributes(Attributes attributes, String[] strArr) {
        if (strArr == null) {
            return attributes;
        }
        BasicAttributes basicAttributes = new BasicAttributes();
        for (String str : strArr) {
            Attribute attribute = attributes.get(str);
            if (attribute != null) {
                basicAttributes.put(attribute);
            }
        }
        return basicAttributes;
    }
}
