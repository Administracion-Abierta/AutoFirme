package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.cfg.XmlConsts;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;
import java.util.Vector;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.ini4j.Config;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/DTDParser.class */
public class DTDParser {
    public static final String TYPE_CDATA = "CDATA";
    public static final String TYPE_ID = "ID";
    public static final String TYPE_IDREF = "IDREF";
    public static final String TYPE_IDREFS = "IDREFS";
    public static final String TYPE_ENTITY = "ENTITY";
    public static final String TYPE_ENTITIES = "ENTITIES";
    public static final String TYPE_NMTOKEN = "NMTOKEN";
    public static final String TYPE_NMTOKENS = "NMTOKENS";
    public static final String TYPE_NOTATION = "NOTATION";
    public static final String TYPE_ENUMERATION = "ENUMERATION";
    private InputEntity in;
    private StringBuffer strTmp;
    private char[] nameTmp;
    private NameCache nameCache;
    private boolean doLexicalPE;
    private DTDEventListener dtdHandler;
    private EntityResolver resolver;
    private Locale locale;
    static final String strANY = "ANY";
    static final String strEMPTY = "EMPTY";
    private static final String XmlLang = "xml:lang";
    static final Catalog messages = new Catalog();
    private char[] charTmp = new char[2];
    protected final Set declaredElements = new HashSet();
    private SimpleHashtable params = new SimpleHashtable(7);
    Hashtable notations = new Hashtable(7);
    SimpleHashtable entities = new SimpleHashtable(17);
    private SimpleHashtable ids = new SimpleHashtable();

    public void setLocale(Locale l) throws SAXException {
        if (l != null && !messages.isLocaleSupported(l.toString())) {
            throw new SAXException(messages.getMessage(this.locale, "P-078", new Object[]{l}));
        }
        this.locale = l;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public Locale chooseLocale(String[] languages) throws SAXException {
        Locale l = messages.chooseLocale(languages);
        if (l != null) {
            setLocale(l);
        }
        return l;
    }

    public void setEntityResolver(EntityResolver r) {
        this.resolver = r;
    }

    public EntityResolver getEntityResolver() {
        return this.resolver;
    }

    public void setDtdHandler(DTDEventListener handler) {
        this.dtdHandler = handler;
        if (handler != null) {
            handler.setDocumentLocator(new Locator() { // from class: com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser.1
                @Override // org.xml.sax.Locator
                public String getPublicId() {
                    return DTDParser.this.getPublicId();
                }

                @Override // org.xml.sax.Locator
                public String getSystemId() {
                    return DTDParser.this.getSystemId();
                }

                @Override // org.xml.sax.Locator
                public int getLineNumber() {
                    return DTDParser.this.getLineNumber();
                }

                @Override // org.xml.sax.Locator
                public int getColumnNumber() {
                    return DTDParser.this.getColumnNumber();
                }
            });
        }
    }

    public DTDEventListener getDtdHandler() {
        return this.dtdHandler;
    }

    public void parse(InputSource in) throws SAXException, IOException {
        init();
        parseInternal(in);
    }

    public void parse(String uri) throws SAXException, IOException {
        init();
        InputSource in = this.resolver.resolveEntity(null, uri);
        if (in == null) {
            in = Resolver.createInputSource(new URL(uri), false);
        } else if (in.getSystemId() == null) {
            warning("P-065", null);
            in.setSystemId(uri);
        }
        parseInternal(in);
    }

    private void init() {
        this.in = null;
        this.strTmp = new StringBuffer();
        this.nameTmp = new char[20];
        this.nameCache = new NameCache();
        this.doLexicalPE = false;
        this.entities.clear();
        this.notations.clear();
        this.params.clear();
        this.declaredElements.clear();
        builtin("amp", "&#38;");
        builtin("lt", "&#60;");
        builtin("gt", ">");
        builtin("quot", OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        builtin("apos", OperatorName.SHOW_TEXT_LINE);
        if (this.locale == null) {
            this.locale = Locale.getDefault();
        }
        if (this.resolver == null) {
            this.resolver = new Resolver();
        }
        if (this.dtdHandler == null) {
            this.dtdHandler = new DTDHandlerBase();
        }
    }

    private void builtin(String entityName, String entityValue) {
        InternalEntity entity = new InternalEntity(entityName, entityValue.toCharArray());
        this.entities.put(entityName, entity);
    }

    private void parseInternal(InputSource input) throws SAXException, IOException {
        if (input == null) {
            fatal("P-000");
        }
        try {
            try {
                try {
                    this.in = InputEntity.getInputEntity(this.dtdHandler, this.locale);
                    this.in.init(input, (String) null, (InputEntity) null, false);
                    this.dtdHandler.startDTD(this.in);
                    ExternalEntity externalSubset = new ExternalEntity(this.in);
                    externalParameterEntity(externalSubset);
                    if (!this.in.isEOF()) {
                        fatal("P-001", new Object[]{Integer.toHexString(getc())});
                    }
                    afterRoot();
                    this.dtdHandler.endDTD();
                    this.strTmp = null;
                    this.nameTmp = null;
                    this.nameCache = null;
                    if (this.in != null) {
                        this.in.close();
                        this.in = null;
                    }
                    this.params.clear();
                    this.entities.clear();
                    this.notations.clear();
                    this.declaredElements.clear();
                    this.ids.clear();
                } catch (RuntimeException e) {
                    System.err.print("Internal DTD parser error: ");
                    e.printStackTrace();
                    throw new SAXParseException(e.getMessage() != null ? e.getMessage() : e.getClass().getName(), getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
                }
            } catch (EndOfInputException e2) {
                if (!this.in.isDocument()) {
                    String name = this.in.getName();
                    do {
                        this.in = this.in.pop();
                    } while (this.in.isInternal());
                    fatal("P-002", new Object[]{name});
                } else {
                    fatal("P-003", null);
                }
                this.strTmp = null;
                this.nameTmp = null;
                this.nameCache = null;
                if (this.in != null) {
                    this.in.close();
                    this.in = null;
                }
                this.params.clear();
                this.entities.clear();
                this.notations.clear();
                this.declaredElements.clear();
                this.ids.clear();
            }
        } catch (Throwable th) {
            this.strTmp = null;
            this.nameTmp = null;
            this.nameCache = null;
            if (this.in != null) {
                this.in.close();
                this.in = null;
            }
            this.params.clear();
            this.entities.clear();
            this.notations.clear();
            this.declaredElements.clear();
            this.ids.clear();
            throw th;
        }
    }

    void afterRoot() throws SAXException {
        Enumeration e = this.ids.keys();
        while (e.hasMoreElements()) {
            String id = (String) e.nextElement();
            Boolean value = (Boolean) this.ids.get(id);
            if (Boolean.FALSE == value) {
                error("V-024", new Object[]{id});
            }
        }
    }

    private void whitespace(String roleId) throws SAXException, IOException {
        if (!maybeWhitespace()) {
            fatal("P-004", new Object[]{messages.getMessage(this.locale, roleId)});
        }
    }

    private boolean maybeWhitespace() throws SAXException, IOException {
        if (!this.doLexicalPE) {
            return this.in.maybeWhitespace();
        }
        char c = getc();
        boolean saw = false;
        while (true) {
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                saw = true;
                if (this.in.isEOF() && !this.in.isInternal()) {
                    return true;
                }
                c = getc();
            } else {
                ungetc();
                return saw;
            }
        }
    }

    private String maybeGetName() throws SAXException, IOException {
        NameCacheEntry entry = maybeGetNameCacheEntry();
        if (entry == null) {
            return null;
        }
        return entry.name;
    }

    private NameCacheEntry maybeGetNameCacheEntry() throws SAXException, IOException {
        char c = getc();
        if (!XmlChars.isLetter(c) && c != ':' && c != '_') {
            ungetc();
            return null;
        }
        return nameCharString(c);
    }

    private String getNmtoken() throws SAXException, IOException {
        char c = getc();
        if (!XmlChars.isNameChar(c)) {
            fatal("P-006", new Object[]{new Character(c)});
        }
        return nameCharString(c).name;
    }

    private NameCacheEntry nameCharString(char c) throws SAXException, IOException {
        int i = 1;
        this.nameTmp[0] = c;
        while (true) {
            char c2 = this.in.getNameChar();
            if (c2 != 0) {
                if (i >= this.nameTmp.length) {
                    char[] tmp = new char[this.nameTmp.length + 10];
                    System.arraycopy(this.nameTmp, 0, tmp, 0, this.nameTmp.length);
                    this.nameTmp = tmp;
                }
                int i2 = i;
                i++;
                this.nameTmp[i2] = c2;
            } else {
                return this.nameCache.lookupEntry(this.nameTmp, i);
            }
        }
    }

    private void parseLiteral(boolean isEntityValue) throws SAXException, IOException {
        char quote = getc();
        InputEntity source = this.in;
        if (quote != '\'' && quote != '\"') {
            fatal("P-007");
        }
        this.strTmp = new StringBuffer();
        while (true) {
            if (this.in != source && this.in.isEOF()) {
                this.in = this.in.pop();
            } else {
                char c = getc();
                if (c != quote || this.in != source) {
                    if (c == '&') {
                        String entityName = maybeGetName();
                        if (entityName != null) {
                            nextChar(';', "F-020", entityName);
                            if (isEntityValue) {
                                this.strTmp.append('&');
                                this.strTmp.append(entityName);
                                this.strTmp.append(';');
                            } else {
                                expandEntityInLiteral(entityName, this.entities, isEntityValue);
                            }
                        } else if (getc() == '#') {
                            int tmp = parseCharNumber();
                            if (tmp > 65535) {
                                int tmp2 = surrogatesToCharTmp(tmp);
                                this.strTmp.append(this.charTmp[0]);
                                if (tmp2 == 2) {
                                    this.strTmp.append(this.charTmp[1]);
                                }
                            } else {
                                this.strTmp.append((char) tmp);
                            }
                        } else {
                            fatal("P-009");
                        }
                    } else {
                        if (c == '%' && isEntityValue) {
                            String entityName2 = maybeGetName();
                            if (entityName2 != null) {
                                nextChar(';', "F-021", entityName2);
                                expandEntityInLiteral(entityName2, this.params, isEntityValue);
                            } else {
                                fatal("P-011");
                            }
                        }
                        if (!isEntityValue) {
                            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                                this.strTmp.append(' ');
                            } else if (c == '<') {
                                fatal("P-012");
                            }
                        }
                        this.strTmp.append(c);
                    }
                } else {
                    return;
                }
            }
        }
    }

    private void expandEntityInLiteral(String name, SimpleHashtable table, boolean isEntityValue) throws SAXException, IOException {
        Object entity = table.get(name);
        if (entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity) entity;
            pushReader(value.buf, name, !value.isPE);
        } else if (entity instanceof ExternalEntity) {
            if (!isEntityValue) {
                fatal("P-013", new Object[]{name});
            }
            pushReader((ExternalEntity) entity);
        } else if (entity == null) {
            fatal(table == this.params ? "V-022" : "P-014", new Object[]{name});
        }
    }

    private String getQuotedString(String type, String extra) throws SAXException, IOException {
        char quote = this.in.getc();
        if (quote != '\'' && quote != '\"') {
            fatal("P-015", new Object[]{messages.getMessage(this.locale, type, new Object[]{extra})});
        }
        this.strTmp = new StringBuffer();
        while (true) {
            char c = this.in.getc();
            if (c != quote) {
                this.strTmp.append(c);
            } else {
                return this.strTmp.toString();
            }
        }
    }

    private String parsePublicId() throws SAXException, IOException {
        String retval = getQuotedString("F-033", null);
        for (int i = 0; i < retval.length(); i++) {
            char c = retval.charAt(i);
            if (" \r\n-'()+,./:=?;!*#@$_%0123456789".indexOf(c) == -1 && ((c < 'A' || c > 'Z') && (c < 'a' || c > 'z'))) {
                fatal("P-016", new Object[]{new Character(c)});
            }
        }
        this.strTmp = new StringBuffer();
        this.strTmp.append(retval);
        return normalize(false);
    }

    private boolean maybeComment(boolean skipStart) throws SAXException, IOException {
        if (!this.in.peek(skipStart ? "!--" : "<!--", null)) {
            return false;
        }
        boolean savedLexicalPE = this.doLexicalPE;
        this.doLexicalPE = false;
        if (0 != 0) {
            this.strTmp = new StringBuffer();
        }
        while (true) {
            try {
                int c = getc();
                if (c == 45) {
                    if (getc() != 45) {
                        if (0 != 0) {
                            this.strTmp.append('-');
                        }
                        ungetc();
                    } else {
                        nextChar('>', "F-022", null);
                        break;
                    }
                } else if (0 != 0) {
                    this.strTmp.append((char) c);
                }
            } catch (EndOfInputException e) {
                if (this.in.isInternal()) {
                    error("V-021", null);
                }
                fatal("P-017");
            }
        }
        this.doLexicalPE = savedLexicalPE;
        if (0 != 0) {
            this.dtdHandler.comment(this.strTmp.toString());
            return true;
        }
        return true;
    }

    private boolean maybePI(boolean skipStart) throws SAXException, IOException {
        boolean savedLexicalPE = this.doLexicalPE;
        if (!this.in.peek(skipStart ? Config.DEFAULT_GLOBAL_SECTION_NAME : "<?", null)) {
            return false;
        }
        this.doLexicalPE = false;
        String target = maybeGetName();
        if (target == null) {
            fatal("P-018");
        }
        if ("xml".equals(target)) {
            fatal("P-019");
        }
        if ("xml".equalsIgnoreCase(target)) {
            fatal("P-020", new Object[]{target});
        }
        if (maybeWhitespace()) {
            this.strTmp = new StringBuffer();
            while (true) {
                try {
                    char c = this.in.getc();
                    if (c == '?' && this.in.peekc('>')) {
                        break;
                    }
                    this.strTmp.append(c);
                } catch (EndOfInputException e) {
                    fatal("P-021");
                }
            }
            this.dtdHandler.processingInstruction(target, this.strTmp.toString());
        } else {
            if (!this.in.peek("?>", null)) {
                fatal("P-022");
            }
            this.dtdHandler.processingInstruction(target, "");
        }
        this.doLexicalPE = savedLexicalPE;
        return true;
    }

    private String maybeReadAttribute(String name, boolean must) throws SAXException, IOException {
        if (!maybeWhitespace()) {
            if (!must) {
                return null;
            }
            fatal("P-024", new Object[]{name});
        }
        if (!peek(name)) {
            if (must) {
                fatal("P-024", new Object[]{name});
            } else {
                ungetc();
                return null;
            }
        }
        maybeWhitespace();
        nextChar('=', "F-023", null);
        maybeWhitespace();
        return getQuotedString("F-035", name);
    }

    private void readVersion(boolean must, String versionNum) throws SAXException, IOException {
        String value = maybeReadAttribute(XmlConsts.XML_DECL_KW_VERSION, must);
        if (must && value == null) {
            fatal("P-025", new Object[]{versionNum});
        }
        if (value != null) {
            int length = value.length();
            for (int i = 0; i < length; i++) {
                char c = value.charAt(i);
                if ((c < '0' || c > '9') && c != '_' && c != '.' && ((c < 'a' || c > 'z') && ((c < 'A' || c > 'Z') && c != ':' && c != '-'))) {
                    fatal("P-026", new Object[]{value});
                }
            }
        }
        if (value != null && !value.equals(versionNum)) {
            error("P-027", new Object[]{versionNum, value});
        }
    }

    private String getMarkupDeclname(String roleId, boolean qname) throws SAXException, IOException {
        whitespace(roleId);
        String name = maybeGetName();
        if (name == null) {
            fatal("P-005", new Object[]{messages.getMessage(this.locale, roleId)});
        }
        return name;
    }

    private boolean maybeMarkupDecl() throws SAXException, IOException {
        return maybeElementDecl() || maybeAttlistDecl() || maybeEntityDecl() || maybeNotationDecl() || maybePI(false) || maybeComment(false);
    }

    private boolean isXmlLang(String value) {
        char c;
        int nextSuffix;
        if (value.length() < 2) {
            return false;
        }
        char c2 = value.charAt(1);
        if (c2 == '-') {
            c = value.charAt(0);
            if (c != 'i' && c != 'I' && c != 'x' && c != 'X') {
                return false;
            }
            nextSuffix = 1;
        } else if ((c2 >= 'a' && c2 <= 'z') || (c2 >= 'A' && c2 <= 'Z')) {
            c = value.charAt(0);
            if ((c < 'a' || c > 'z') && (c < 'A' || c > 'Z')) {
                return false;
            }
            nextSuffix = 2;
        } else {
            return false;
        }
        while (nextSuffix < value.length()) {
            c = value.charAt(nextSuffix);
            if (c == '-') {
                while (true) {
                    nextSuffix++;
                    if (nextSuffix >= value.length()) {
                        break;
                    }
                    c = value.charAt(nextSuffix);
                    if (c < 'a' || c > 'z') {
                        if (c < 'A' || c > 'Z') {
                            break;
                        }
                    }
                }
            } else {
                break;
            }
        }
        return value.length() == nextSuffix && c != '-';
    }

    private boolean maybeElementDecl() throws SAXException, IOException {
        short modelType;
        InputEntity start = peekDeclaration("!ELEMENT");
        if (start == null) {
            return false;
        }
        String name = getMarkupDeclname("F-015", true);
        if (this.declaredElements.contains(name)) {
            error("V-012", new Object[]{name});
        } else {
            this.declaredElements.add(name);
        }
        whitespace("F-000");
        if (peek(strEMPTY)) {
            modelType = 0;
            this.dtdHandler.startContentModel(name, (short) 0);
        } else if (peek(strANY)) {
            modelType = 1;
            this.dtdHandler.startContentModel(name, (short) 1);
        } else {
            modelType = getMixedOrChildren(name);
        }
        this.dtdHandler.endContentModel(name, modelType);
        maybeWhitespace();
        char c = getc();
        if (c != '>') {
            fatal("P-036", new Object[]{name, new Character(c)});
        }
        if (start != this.in) {
            error("V-013", null);
            return true;
        }
        return true;
    }

    private short getMixedOrChildren(String elementName) throws SAXException, IOException {
        short modelType;
        this.strTmp = new StringBuffer();
        nextChar('(', "F-028", elementName);
        InputEntity start = this.in;
        maybeWhitespace();
        this.strTmp.append('(');
        if (peek("#PCDATA")) {
            this.strTmp.append("#PCDATA");
            modelType = 2;
            this.dtdHandler.startContentModel(elementName, (short) 2);
            getMixed(elementName, start);
        } else {
            modelType = 3;
            this.dtdHandler.startContentModel(elementName, (short) 3);
            getcps(elementName, start);
        }
        return modelType;
    }

    private void getcps(String elementName, InputEntity start) throws SAXException, IOException {
        String str;
        boolean decided = false;
        char type = 0;
        this.dtdHandler.startModelGroup();
        do {
            String tag = maybeGetName();
            if (tag != null) {
                this.strTmp.append(tag);
                this.dtdHandler.childElement(tag, getFrequency());
            } else if (peek("(")) {
                InputEntity next = this.in;
                this.strTmp.append('(');
                maybeWhitespace();
                getcps(elementName, next);
            } else {
                if (type == 0) {
                    str = "P-039";
                } else {
                    str = type == ',' ? "P-037" : "P-038";
                }
                fatal(str, new Object[]{new Character(getc())});
            }
            maybeWhitespace();
            if (decided) {
                char c = getc();
                if (c == type) {
                    this.strTmp.append(type);
                    maybeWhitespace();
                    reportConnector(type);
                } else if (c == ')') {
                    ungetc();
                } else {
                    fatal(type == 0 ? "P-041" : "P-040", new Object[]{new Character(c), new Character(type)});
                    maybeWhitespace();
                }
            } else {
                type = getc();
                switch (type) {
                    case ',':
                    case '|':
                        reportConnector(type);
                        decided = true;
                        this.strTmp.append(type);
                        maybeWhitespace();
                        break;
                    default:
                        ungetc();
                        break;
                }
            }
        } while (!peek(")"));
        if (this.in != start) {
            error("V-014", new Object[]{elementName});
        }
        this.strTmp.append(')');
        this.dtdHandler.endModelGroup(getFrequency());
    }

    private void reportConnector(char type) throws SAXException {
        switch (type) {
            case ',':
                this.dtdHandler.connector((short) 1);
                return;
            case '|':
                this.dtdHandler.connector((short) 0);
                return;
            default:
                throw new Error();
        }
    }

    private short getFrequency() throws SAXException, IOException {
        char c = getc();
        if (c == '?') {
            this.strTmp.append(c);
            return (short) 2;
        }
        if (c == '+') {
            this.strTmp.append(c);
            return (short) 1;
        }
        if (c == '*') {
            this.strTmp.append(c);
            return (short) 0;
        }
        ungetc();
        return (short) 3;
    }

    private void getMixed(String elementName, InputEntity start) throws SAXException, IOException {
        maybeWhitespace();
        if (peek(")*") || peek(")")) {
            if (this.in != start) {
                error("V-014", new Object[]{elementName});
            }
            this.strTmp.append(')');
            return;
        }
        ArrayList l = new ArrayList();
        while (peek("|")) {
            this.strTmp.append('|');
            maybeWhitespace();
            this.doLexicalPE = true;
            String name = maybeGetName();
            if (name == null) {
                fatal("P-042", new Object[]{elementName, Integer.toHexString(getc())});
            }
            if (l.contains(name)) {
                error("V-015", new Object[]{name});
            } else {
                l.add(name);
                this.dtdHandler.mixedElement(name);
            }
            this.strTmp.append(name);
            maybeWhitespace();
        }
        if (!peek(")*")) {
            fatal("P-043", new Object[]{elementName, new Character(getc())});
        }
        if (this.in != start) {
            error("V-014", new Object[]{elementName});
        }
        this.strTmp.append(')');
    }

    private boolean maybeAttlistDecl() throws SAXException, IOException {
        String typeName;
        short attributeUse;
        InputEntity start = peekDeclaration("!ATTLIST");
        if (start == null) {
            return false;
        }
        String elementName = getMarkupDeclname("F-016", true);
        while (!peek(">")) {
            maybeWhitespace();
            char c = getc();
            if (c == '%') {
                String entityName = maybeGetName();
                if (entityName != null) {
                    nextChar(';', "F-021", entityName);
                    whitespace("F-021");
                } else {
                    fatal("P-011");
                }
            }
            ungetc();
            String attName = maybeGetName();
            if (attName == null) {
                fatal("P-044", new Object[]{new Character(getc())});
            }
            whitespace("F-001");
            Vector values = null;
            if (peek("CDATA")) {
                typeName = "CDATA";
            } else if (peek(TYPE_IDREFS)) {
                typeName = TYPE_IDREFS;
            } else if (peek(TYPE_IDREF)) {
                typeName = TYPE_IDREF;
            } else if (peek("ID")) {
                typeName = "ID";
            } else if (peek(TYPE_ENTITY)) {
                typeName = TYPE_ENTITY;
            } else if (peek(TYPE_ENTITIES)) {
                typeName = TYPE_ENTITIES;
            } else if (peek(TYPE_NMTOKENS)) {
                typeName = TYPE_NMTOKENS;
            } else if (peek(TYPE_NMTOKEN)) {
                typeName = TYPE_NMTOKEN;
            } else if (peek(TYPE_NOTATION)) {
                typeName = TYPE_NOTATION;
                whitespace("F-002");
                nextChar('(', "F-029", null);
                maybeWhitespace();
                values = new Vector();
                do {
                    String name = maybeGetName();
                    if (name == null) {
                        fatal("P-068");
                    }
                    if (this.notations.get(name) == null) {
                        this.notations.put(name, name);
                    }
                    values.addElement(name);
                    maybeWhitespace();
                    if (peek("|")) {
                        maybeWhitespace();
                    }
                } while (!peek(")"));
            } else if (peek("(")) {
                typeName = TYPE_ENUMERATION;
                maybeWhitespace();
                values = new Vector();
                do {
                    values.addElement(getNmtoken());
                    maybeWhitespace();
                    if (peek("|")) {
                        maybeWhitespace();
                    }
                } while (!peek(")"));
            } else {
                fatal("P-045", new Object[]{attName, new Character(getc())});
                typeName = null;
            }
            String defaultValue = null;
            whitespace("F-003");
            if (peek("#REQUIRED")) {
                attributeUse = 3;
            } else if (peek("#FIXED")) {
                if (typeName == "ID") {
                    error("V-017", new Object[]{attName});
                }
                attributeUse = 2;
                whitespace("F-004");
                parseLiteral(false);
                defaultValue = typeName == "CDATA" ? normalize(false) : this.strTmp.toString();
            } else if (!peek("#IMPLIED")) {
                attributeUse = 1;
                if (typeName == "ID") {
                    error("V-018", new Object[]{attName});
                }
                parseLiteral(false);
                defaultValue = typeName == "CDATA" ? normalize(false) : this.strTmp.toString();
            } else {
                attributeUse = 0;
            }
            if (XmlLang.equals(attName) && defaultValue != null && !isXmlLang(defaultValue)) {
                error("P-033", new Object[]{defaultValue});
            }
            String[] v = values != null ? (String[]) values.toArray(new String[0]) : null;
            this.dtdHandler.attributeDecl(elementName, attName, typeName, v, attributeUse, defaultValue);
            maybeWhitespace();
        }
        if (start != this.in) {
            error("V-013", null);
            return true;
        }
        return true;
    }

    private String normalize(boolean invalidIfNeeded) {
        String s = this.strTmp.toString();
        String s2 = s.trim();
        boolean didStrip = false;
        if (s != s2) {
            s = s2;
            didStrip = true;
        }
        this.strTmp = new StringBuffer();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (!XmlChars.isSpace(c)) {
                this.strTmp.append(c);
            } else {
                this.strTmp.append(' ');
                while (true) {
                    i++;
                    if (i >= s.length() || !XmlChars.isSpace(s.charAt(i))) {
                        break;
                    }
                    didStrip = true;
                }
                i--;
            }
            i++;
        }
        if (didStrip) {
            return this.strTmp.toString();
        }
        return s;
    }

    private boolean maybeConditionalSect() throws SAXException, IOException {
        if (!peek("<![")) {
            return false;
        }
        InputEntity start = this.in;
        maybeWhitespace();
        String keyword = maybeGetName();
        if (keyword == null) {
            fatal("P-046");
        }
        maybeWhitespace();
        nextChar('[', "F-030", null);
        if (!"INCLUDE".equals(keyword)) {
            if ("IGNORE".equals(keyword)) {
                int nestlevel = 1;
                this.doLexicalPE = false;
                while (nestlevel > 0) {
                    char c = getc();
                    if (c == '<') {
                        if (peek("![")) {
                            nestlevel++;
                        }
                    } else if (c == ']' && peek("]>")) {
                        nestlevel--;
                    }
                }
                return true;
            }
            fatal("P-048", new Object[]{keyword});
            return true;
        }
        while (true) {
            if (this.in.isEOF() && this.in != start) {
                this.in = this.in.pop();
            } else {
                if (this.in.isEOF()) {
                    error("V-020", null);
                }
                if (!peek("]]>")) {
                    this.doLexicalPE = false;
                    if (!maybeWhitespace() && !maybePEReference()) {
                        this.doLexicalPE = true;
                        if (!maybeMarkupDecl() && !maybeConditionalSect()) {
                            fatal("P-047");
                        }
                    }
                } else {
                    return true;
                }
            }
        }
    }

    private int parseCharNumber() throws SAXException, IOException {
        int retval = 0;
        if (getc() != 'x') {
            ungetc();
            while (true) {
                char c = getc();
                if (c >= '0' && c <= '9') {
                    retval = (retval * 10) + (c - '0');
                } else {
                    if (c == ';') {
                        return retval;
                    }
                    fatal("P-049");
                }
            }
        } else {
            while (true) {
                char c2 = getc();
                if (c2 >= '0' && c2 <= '9') {
                    retval = (retval << 4) + (c2 - '0');
                } else if (c2 >= 'a' && c2 <= 'f') {
                    retval = (retval << 4) + 10 + (c2 - 'a');
                } else if (c2 >= 'A' && c2 <= 'F') {
                    retval = (retval << 4) + 10 + (c2 - 'A');
                } else {
                    if (c2 == ';') {
                        return retval;
                    }
                    fatal("P-050");
                }
            }
        }
    }

    private int surrogatesToCharTmp(int ucs4) throws SAXException {
        if (ucs4 <= 65535) {
            if (XmlChars.isChar(ucs4)) {
                this.charTmp[0] = (char) ucs4;
                return 1;
            }
        } else if (ucs4 <= 1114111) {
            int ucs5 = ucs4 - 65536;
            this.charTmp[0] = (char) (55296 | ((ucs5 >> 10) & WinUser.CF_GDIOBJLAST));
            this.charTmp[1] = (char) (56320 | (ucs5 & WinUser.CF_GDIOBJLAST));
            return 2;
        }
        fatal("P-051", new Object[]{Integer.toHexString(ucs4)});
        return -1;
    }

    private boolean maybePEReference() throws SAXException, IOException {
        if (!this.in.peekc('%')) {
            return false;
        }
        String name = maybeGetName();
        if (name == null) {
            fatal("P-011");
        }
        nextChar(';', "F-021", name);
        Object entity = this.params.get(name);
        if (entity instanceof InternalEntity) {
            InternalEntity value = (InternalEntity) entity;
            pushReader(value.buf, name, false);
            return true;
        }
        if (entity instanceof ExternalEntity) {
            pushReader((ExternalEntity) entity);
            externalParameterEntity((ExternalEntity) entity);
            return true;
        }
        if (entity == null) {
            error("V-022", new Object[]{name});
            return true;
        }
        return true;
    }

    private boolean maybeEntityDecl() throws SAXException, IOException {
        SimpleHashtable defns;
        InputEntity start = peekDeclaration("!ENTITY");
        if (start == null) {
            return false;
        }
        this.doLexicalPE = false;
        whitespace("F-005");
        if (this.in.peekc('%')) {
            whitespace("F-006");
            defns = this.params;
        } else {
            defns = this.entities;
        }
        ungetc();
        this.doLexicalPE = true;
        String entityName = getMarkupDeclname("F-017", false);
        whitespace("F-007");
        ExternalEntity externalId = maybeExternalID();
        boolean doStore = defns.get(entityName) == null;
        if (!doStore && defns == this.entities) {
            warning("P-054", new Object[]{entityName});
        }
        if (externalId == null) {
            this.doLexicalPE = false;
            parseLiteral(true);
            this.doLexicalPE = true;
            if (doStore) {
                char[] value = new char[this.strTmp.length()];
                if (value.length != 0) {
                    this.strTmp.getChars(0, value.length, value, 0);
                }
                InternalEntity entity = new InternalEntity(entityName, value);
                entity.isPE = defns == this.params;
                entity.isFromInternalSubset = false;
                defns.put(entityName, entity);
                if (defns == this.entities) {
                    this.dtdHandler.internalGeneralEntityDecl(entityName, new String(value));
                }
            }
        } else {
            if (defns == this.entities && maybeWhitespace() && peek("NDATA")) {
                externalId.notation = getMarkupDeclname("F-018", false);
                if (this.notations.get(externalId.notation) == null) {
                    this.notations.put(externalId.notation, Boolean.TRUE);
                }
            }
            externalId.name = entityName;
            externalId.isPE = defns == this.params;
            externalId.isFromInternalSubset = false;
            if (doStore) {
                defns.put(entityName, externalId);
                if (externalId.notation != null) {
                    this.dtdHandler.unparsedEntityDecl(entityName, externalId.publicId, externalId.systemId, externalId.notation);
                } else if (defns == this.entities) {
                    this.dtdHandler.externalGeneralEntityDecl(entityName, externalId.publicId, externalId.systemId);
                }
            }
        }
        maybeWhitespace();
        nextChar('>', "F-031", entityName);
        if (start != this.in) {
            error("V-013", null);
            return true;
        }
        return true;
    }

    private ExternalEntity maybeExternalID() throws SAXException, IOException {
        String temp = null;
        if (peek("PUBLIC")) {
            whitespace("F-009");
            temp = parsePublicId();
        } else if (!peek("SYSTEM")) {
            return null;
        }
        ExternalEntity retval = new ExternalEntity(this.in);
        retval.publicId = temp;
        whitespace("F-008");
        retval.systemId = parseSystemId();
        return retval;
    }

    private String parseSystemId() throws SAXException, IOException {
        String uri = getQuotedString("F-034", null);
        int temp = uri.indexOf(58);
        if (temp == -1 || uri.indexOf(47) < temp) {
            String baseURI = this.in.getSystemId();
            if (baseURI == null) {
                fatal("P-055", new Object[]{uri});
            }
            if (uri.length() == 0) {
                uri = ".";
            }
            String baseURI2 = baseURI.substring(0, baseURI.lastIndexOf(47) + 1);
            if (uri.charAt(0) != '/') {
                uri = baseURI2 + uri;
            } else {
                throw new InternalError();
            }
        }
        if (uri.indexOf(35) != -1) {
            error("P-056", new Object[]{uri});
        }
        return uri;
    }

    private void maybeTextDecl() throws SAXException, IOException {
        if (peek("<?xml")) {
            readVersion(false, "1.0");
            readEncoding(true);
            maybeWhitespace();
            if (!peek("?>")) {
                fatal("P-057");
            }
        }
    }

    private void externalParameterEntity(ExternalEntity next) throws SAXException, IOException {
        InputEntity pe = this.in;
        maybeTextDecl();
        while (!pe.isEOF()) {
            if (this.in.isEOF()) {
                this.in = this.in.pop();
            } else {
                this.doLexicalPE = false;
                if (!maybeWhitespace() && !maybePEReference()) {
                    this.doLexicalPE = true;
                    if (!maybeMarkupDecl() && !maybeConditionalSect()) {
                        break;
                    }
                }
            }
        }
        if (!pe.isEOF()) {
            fatal("P-059", new Object[]{this.in.getName()});
        }
    }

    private void readEncoding(boolean must) throws SAXException, IOException {
        String name = maybeReadAttribute("encoding", must);
        if (name == null) {
            return;
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if ((c < 'A' || c > 'Z') && ((c < 'a' || c > 'z') && (i == 0 || ((c < '0' || c > '9') && c != '-' && c != '_' && c != '.')))) {
                fatal("P-060", new Object[]{new Character(c)});
            }
        }
        String currentEncoding = this.in.getEncoding();
        if (currentEncoding != null && !name.equalsIgnoreCase(currentEncoding)) {
            warning("P-061", new Object[]{name, currentEncoding});
        }
    }

    private boolean maybeNotationDecl() throws SAXException, IOException {
        InputEntity start = peekDeclaration("!NOTATION");
        if (start == null) {
            return false;
        }
        String name = getMarkupDeclname("F-019", false);
        ExternalEntity entity = new ExternalEntity(this.in);
        whitespace("F-011");
        if (peek("PUBLIC")) {
            whitespace("F-009");
            entity.publicId = parsePublicId();
            if (maybeWhitespace()) {
                if (!peek(">")) {
                    entity.systemId = parseSystemId();
                } else {
                    ungetc();
                }
            }
        } else if (peek("SYSTEM")) {
            whitespace("F-008");
            entity.systemId = parseSystemId();
        } else {
            fatal("P-062");
        }
        maybeWhitespace();
        nextChar('>', "F-032", name);
        if (start != this.in) {
            error("V-013", null);
        }
        if (entity.systemId != null && entity.systemId.indexOf(35) != -1) {
            error("P-056", new Object[]{entity.systemId});
        }
        Object value = this.notations.get(name);
        if (value != null && (value instanceof ExternalEntity)) {
            warning("P-063", new Object[]{name});
            return true;
        }
        this.notations.put(name, entity);
        this.dtdHandler.notationDecl(name, entity.publicId, entity.systemId);
        return true;
    }

    private char getc() throws SAXException, IOException {
        if (!this.doLexicalPE) {
            return this.in.getc();
        }
        while (this.in.isEOF()) {
            if (this.in.isInternal() || (this.doLexicalPE && !this.in.isDocument())) {
                this.in = this.in.pop();
            } else {
                fatal("P-064", new Object[]{this.in.getName()});
            }
        }
        char c = this.in.getc();
        if (c == '%' && this.doLexicalPE) {
            String name = maybeGetName();
            if (name == null) {
                fatal("P-011");
            }
            nextChar(';', "F-021", name);
            Object entity = this.params.get(name);
            pushReader(" ".toCharArray(), null, false);
            if (entity instanceof InternalEntity) {
                pushReader(((InternalEntity) entity).buf, name, false);
            } else if (entity instanceof ExternalEntity) {
                pushReader((ExternalEntity) entity);
            } else if (entity == null) {
                fatal("V-022");
            } else {
                throw new InternalError();
            }
            pushReader(" ".toCharArray(), null, false);
            return this.in.getc();
        }
        return c;
    }

    private void ungetc() {
        this.in.ungetc();
    }

    private boolean peek(String s) throws SAXException, IOException {
        return this.in.peek(s, null);
    }

    private InputEntity peekDeclaration(String s) throws SAXException, IOException {
        if (!this.in.peekc('<')) {
            return null;
        }
        InputEntity start = this.in;
        if (this.in.peek(s, null)) {
            return start;
        }
        this.in.ungetc();
        return null;
    }

    private void nextChar(char c, String location, String near) throws SAXException, IOException {
        while (this.in.isEOF() && !this.in.isDocument()) {
            this.in = this.in.pop();
        }
        if (!this.in.peekc(c)) {
            Object[] objArr = new Object[3];
            objArr[0] = new Character(c);
            objArr[1] = messages.getMessage(this.locale, location);
            objArr[2] = near == null ? "" : '\"' + near + '\"';
            fatal("P-008", objArr);
        }
    }

    private void pushReader(char[] buf, String name, boolean isGeneral) throws SAXException {
        InputEntity r = InputEntity.getInputEntity(this.dtdHandler, this.locale);
        r.init(buf, name, this.in, !isGeneral);
        this.in = r;
    }

    private boolean pushReader(ExternalEntity next) throws SAXException, IOException {
        InputEntity r = InputEntity.getInputEntity(this.dtdHandler, this.locale);
        try {
            InputSource s = next.getInputSource(this.resolver);
            r.init(s, next.name, this.in, next.isPE);
            this.in = r;
            return true;
        } catch (IOException e) {
            String msg = "unable to open the external entity from :" + next.systemId;
            if (next.publicId != null) {
                msg = msg + " (public id:" + next.publicId + ")";
            }
            SAXParseException spe = new SAXParseException(msg, getPublicId(), getSystemId(), getLineNumber(), getColumnNumber(), e);
            this.dtdHandler.fatalError(spe);
            throw e;
        }
    }

    public String getPublicId() {
        if (this.in == null) {
            return null;
        }
        return this.in.getPublicId();
    }

    public String getSystemId() {
        if (this.in == null) {
            return null;
        }
        return this.in.getSystemId();
    }

    public int getLineNumber() {
        if (this.in == null) {
            return -1;
        }
        return this.in.getLineNumber();
    }

    public int getColumnNumber() {
        if (this.in == null) {
            return -1;
        }
        return this.in.getColumnNumber();
    }

    private void warning(String messageId, Object[] parameters) throws SAXException {
        SAXParseException e = new SAXParseException(messages.getMessage(this.locale, messageId, parameters), getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        this.dtdHandler.warning(e);
    }

    void error(String messageId, Object[] parameters) throws SAXException {
        SAXParseException e = new SAXParseException(messages.getMessage(this.locale, messageId, parameters), getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        this.dtdHandler.error(e);
    }

    private void fatal(String messageId) throws SAXException {
        fatal(messageId, null);
    }

    private void fatal(String messageId, Object[] parameters) throws SAXException {
        SAXParseException e = new SAXParseException(messages.getMessage(this.locale, messageId, parameters), getPublicId(), getSystemId(), getLineNumber(), getColumnNumber());
        this.dtdHandler.fatalError(e);
        throw e;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/DTDParser$NameCache.class */
    static class NameCache {
        NameCacheEntry[] hashtable = new NameCacheEntry[WinError.ERROR_TIMER_NOT_CANCELED];

        NameCache() {
        }

        String lookup(char[] value, int len) {
            return lookupEntry(value, len).name;
        }

        NameCacheEntry lookupEntry(char[] value, int len) {
            int index = 0;
            for (int i = 0; i < len; i++) {
                index = (index * 31) + value[i];
            }
            int index2 = (index & WinNT.MAXLONG) % this.hashtable.length;
            NameCacheEntry nameCacheEntry = this.hashtable[index2];
            while (true) {
                NameCacheEntry entry = nameCacheEntry;
                if (entry != null) {
                    if (!entry.matches(value, len)) {
                        nameCacheEntry = entry.next;
                    } else {
                        return entry;
                    }
                } else {
                    NameCacheEntry entry2 = new NameCacheEntry();
                    entry2.chars = new char[len];
                    System.arraycopy(value, 0, entry2.chars, 0, len);
                    entry2.name = new String(entry2.chars);
                    entry2.name = entry2.name.intern();
                    entry2.next = this.hashtable[index2];
                    this.hashtable[index2] = entry2;
                    return entry2;
                }
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/DTDParser$NameCacheEntry.class */
    static class NameCacheEntry {
        String name;
        char[] chars;
        NameCacheEntry next;

        NameCacheEntry() {
        }

        boolean matches(char[] value, int len) {
            if (this.chars.length != len) {
                return false;
            }
            for (int i = 0; i < len; i++) {
                if (value[i] != this.chars[i]) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/DTDParser$Catalog.class */
    static final class Catalog extends MessageCatalog {
        Catalog() {
            super(DTDParser.class);
        }
    }
}
