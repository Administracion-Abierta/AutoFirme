package com.ctc.wstx.shaded.msv_core.reader.dtd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ngimpl.DataTypeLibraryImpl;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.dtd.LocalNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.trex.ElementPattern;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.Controller;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.InputEntity;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/dtd/DTDReader.class */
public class DTDReader implements DTDEventListener {
    protected final Controller controller;
    protected static final String ABANDON_URI_SNIFFING = "*";
    protected final TREXGrammar grammar;
    protected Locator locator;
    protected Context contextStack;
    protected Expression contentModel;
    protected short connectorType;
    public static final String ERR_UNDEFINED_ELEMENT = "DTDReader.UndefinedElement";
    public static final String WRN_ATTEMPT_TO_USE_NAMESPACE = "DTDReader.Warning.AttemptToUseNamespace";
    public static final String ERR_UNDECLARED_PREFIX = "DTDReader.UndeclaredPrefix";
    private DatatypeLibrary datatypeLibrary = new DataTypeLibraryImpl();
    protected final Map namespaces = createInitialNamespaceMap();
    protected final Map elementDecls = new HashMap();
    protected final Map attributeDecls = new HashMap();
    protected final short CONNECTOR_UNKNOWN = -999;
    private boolean reportedXmlnsWarning = false;
    private final Map declaredLocations = new HashMap();

    public DTDReader(GrammarReaderController controller, ExpressionPool pool) {
        this.controller = new Controller(controller);
        this.grammar = new TREXGrammar(pool);
    }

    public static TREXGrammar parse(InputSource source, GrammarReaderController controller) {
        return parse(source, controller, new ExpressionPool());
    }

    public static TREXGrammar parse(InputSource source, GrammarReaderController controller, ExpressionPool pool) {
        try {
            DTDReader reader = new DTDReader(controller, pool);
            DTDParser parser = new DTDParser();
            parser.setDtdHandler(reader);
            parser.setEntityResolver(controller);
            parser.parse(source);
            return reader.getResult();
        } catch (SAXParseException e) {
            return null;
        } catch (Exception e2) {
            controller.error(new Locator[0], e2.getMessage(), e2);
            return null;
        }
    }

    public void setDatatypeLibrary(DatatypeLibrary datatypeLibrary) {
        this.datatypeLibrary = datatypeLibrary;
    }

    public Datatype createDatatype(String name) {
        try {
            if ("CDATA".equals(name)) {
                return this.datatypeLibrary.createDatatype("normalizedString");
            }
            if (DTDParser.TYPE_ENUMERATION.equals(name)) {
                return this.datatypeLibrary.createDatatype("token");
            }
            return this.datatypeLibrary.createDatatype(name);
        } catch (DatatypeException e) {
            e.printStackTrace();
            throw new InternalError();
        }
    }

    protected static final Map createInitialNamespaceMap() {
        Map m = new HashMap();
        HashSet hashSet = new HashSet();
        hashSet.add("http://www.w3.org/XML/1998/namespace");
        m.put("xml", hashSet);
        return m;
    }

    protected NameClass getNameClass(String maybeQName, boolean handleAsAttribute) {
        String[] s = splitQName(maybeQName);
        if (s[0].length() == 0 && handleAsAttribute) {
            return new SimpleNameClass(s[0], s[1]);
        }
        Set vec = (Set) this.namespaces.get(s[0]);
        if (vec == null) {
            if (s[0].equals("")) {
                return new SimpleNameClass("", s[1]);
            }
            this.controller.error(new Locator[]{this.locator}, Localizer.localize(ERR_UNDECLARED_PREFIX, s[0]), null);
            return new LocalNameClass(s[1]);
        }
        if (vec.contains("*")) {
            return new LocalNameClass(s[1]);
        }
        String[] candidates = (String[]) vec.toArray(new String[vec.size()]);
        NameClass nc = new SimpleNameClass(candidates[0], s[1]);
        for (int i = 1; i < vec.size(); i++) {
            nc = new ChoiceNameClass(nc, new SimpleNameClass(candidates[i], s[1]));
        }
        return nc;
    }

    protected String[] splitQName(String maybeQName) {
        int idx = maybeQName.indexOf(58);
        if (idx < 0) {
            return new String[]{"", maybeQName};
        }
        return new String[]{maybeQName.substring(0, idx), maybeQName.substring(idx + 1)};
    }

    public TREXGrammar getResult() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void setDocumentLocator(Locator loc) {
        this.locator = loc;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/dtd/DTDReader$AttModel.class */
    private static class AttModel {
        Expression value;
        boolean required;

        AttModel(Expression value, boolean required) {
            this.value = value;
            this.required = required;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startContentModel(String elementName, short type) {
        if (this.contentModel != null) {
            throw new Error();
        }
        if (type == 2) {
            this.contentModel = Expression.nullSet;
        }
        if (type == 1) {
            this.contentModel = getAnyExp();
        }
        if (type == 0) {
            this.contentModel = Expression.epsilon;
        }
    }

    protected final ReferenceExp getAnyExp() {
        return this.grammar.namedPatterns.getOrCreate("$  all  $");
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endContentModel(String elementName, short type) {
        if (this.contentModel == null) {
            throw new Error();
        }
        switch (type) {
            case 0:
                this.contentModel = Expression.epsilon;
                break;
            case 2:
                if (this.contentModel != Expression.nullSet) {
                    this.contentModel = this.grammar.pool.createMixed(this.grammar.pool.createZeroOrMore(this.contentModel));
                } else {
                    this.contentModel = Expression.anyString;
                }
                break;
        }
        setDeclaredLocationOf(this.grammar.namedPatterns.getOrCreate(elementName));
        this.elementDecls.put(elementName, this.contentModel);
        this.contentModel = null;
    }

    protected Expression processOccurs(Expression item, short occurence) {
        switch (occurence) {
            case 0:
                return this.grammar.pool.createZeroOrMore(item);
            case 1:
                return this.grammar.pool.createOneOrMore(item);
            case 2:
                return this.grammar.pool.createOptional(item);
            case 3:
                return item;
            default:
                throw new Error();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/dtd/DTDReader$Context.class */
    protected class Context {
        final Expression exp;
        final short connectorType;
        final Context previous;

        Context(Context prev, Expression exp, short connector) {
            this.exp = exp;
            this.connectorType = connector;
            this.previous = prev;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void childElement(String elementName, short occurence) {
        Expression exp = processOccurs(this.grammar.namedPatterns.getOrCreate(elementName), occurence);
        if (this.connectorType == -999) {
            if (this.contentModel != null) {
                throw new Error();
            }
            this.contentModel = exp;
            return;
        }
        combineToContentModel(exp);
    }

    protected void combineToContentModel(Expression exp) {
        switch (this.connectorType) {
            case 0:
                this.contentModel = this.grammar.pool.createChoice(this.contentModel, exp);
                return;
            case 1:
                this.contentModel = this.grammar.pool.createSequence(this.contentModel, exp);
                return;
            default:
                throw new Error();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void mixedElement(String elementName) {
        if (this.contentModel == null) {
            throw new Error();
        }
        this.contentModel = this.grammar.pool.createChoice(this.contentModel, this.grammar.namedPatterns.getOrCreate(elementName));
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startModelGroup() {
        this.contextStack = new Context(this.contextStack, this.contentModel, this.connectorType);
        this.contentModel = null;
        this.connectorType = (short) -999;
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endModelGroup(short occurence) {
        Expression exp = processOccurs(this.contentModel, occurence);
        this.contentModel = this.contextStack.exp;
        this.connectorType = this.contextStack.connectorType;
        this.contextStack = this.contextStack.previous;
        if (this.contentModel == null) {
            this.contentModel = exp;
        } else {
            combineToContentModel(exp);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void connector(short type) throws SAXException {
        if (this.connectorType == -999) {
            this.connectorType = type;
        } else if (this.connectorType != type) {
            throw new Error();
        }
    }

    private Set getPossibleNamespaces(String prefix) {
        Set s = (Set) this.namespaces.get(prefix);
        if (s != null) {
            return s;
        }
        HashSet hashSet = new HashSet();
        this.namespaces.put(prefix, hashSet);
        return hashSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void attributeDecl(String elementName, String attributeName, String attributeType, String[] enums, short attributeUse, String defaultValue) throws SAXException {
        Set s;
        if (attributeName.startsWith(CanonicalizerBase.XMLNS)) {
            if (!this.reportedXmlnsWarning) {
                this.controller.warning(new Locator[]{this.locator}, Localizer.localize(WRN_ATTEMPT_TO_USE_NAMESPACE));
            }
            this.reportedXmlnsWarning = true;
            if (defaultValue == null) {
                defaultValue = "*";
            }
            if (attributeName.equals(CanonicalizerBase.XMLNS)) {
                s = getPossibleNamespaces("");
            } else {
                s = getPossibleNamespaces(attributeName.substring(6));
            }
            s.add(defaultValue);
            return;
        }
        Map attList = (Map) this.attributeDecls.get(elementName);
        if (attList == null) {
            attList = new HashMap();
            this.attributeDecls.put(elementName, attList);
        }
        Expression body = createAttributeBody(elementName, attributeName, attributeType, enums, attributeUse, defaultValue);
        AttModel am = new AttModel(body, attributeUse == 3);
        setDeclaredLocationOf(am);
        attList.put(attributeName, am);
    }

    protected Expression createAttributeBody(String elementName, String attributeName, String attributeType, String[] enums, short attributeUse, String defaultValue) throws SAXException {
        Datatype dt = createDatatype(attributeType);
        StringPair str = new StringPair("", attributeType);
        if (enums != null) {
            Expression exp = Expression.nullSet;
            for (String str2 : enums) {
                exp = this.grammar.pool.createChoice(exp, this.grammar.pool.createValue(dt, str, dt.createValue(str2, null)));
            }
            return exp;
        }
        if (attributeUse == 2) {
            return this.grammar.pool.createValue(dt, str, dt.createValue(defaultValue, null));
        }
        return this.grammar.pool.createData(dt, str);
    }

    protected ReferenceExp createElementDeclaration(String elementName) {
        Map attList = (Map) this.attributeDecls.get(elementName);
        Expression contentModel = Expression.epsilon;
        if (attList != null) {
            for (String attName : attList.keySet()) {
                AttModel model = (AttModel) attList.get(attName);
                Expression exp = this.grammar.pool.createAttribute(getNameClass(attName, true), model.value);
                if (!model.required) {
                    exp = this.grammar.pool.createOptional(exp);
                }
                contentModel = this.grammar.pool.createSequence(contentModel, exp);
            }
        }
        ReferenceExp er = this.grammar.namedPatterns.getOrCreate(elementName);
        er.exp = new ElementPattern(getNameClass(elementName, false), this.grammar.pool.createSequence(contentModel, (Expression) this.elementDecls.get(elementName)));
        this.declaredLocations.put(er.exp, getDeclaredLocationOf(er));
        return er;
    }

    protected Expression createElementDeclarations() {
        Expression allExp = Expression.nullSet;
        Iterator itr = this.elementDecls.keySet().iterator();
        while (itr.hasNext()) {
            Expression exp = createElementDeclaration((String) itr.next());
            allExp = this.grammar.pool.createChoice(allExp, exp);
        }
        return allExp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endDTD() throws SAXException {
        Expression allExp = createElementDeclarations();
        getAnyExp().exp = this.grammar.pool.createMixed(this.grammar.pool.createZeroOrMore(allExp));
        this.grammar.exp = allExp;
        ReferenceExp[] exps = this.grammar.namedPatterns.getAll();
        for (int i = 0; i < exps.length; i++) {
            if (exps[i].exp == null) {
                this.controller.error(new Locator[]{this.locator}, Localizer.localize(ERR_UNDEFINED_ELEMENT, new Object[]{exps[i].name}), null);
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void fatalError(SAXParseException e) throws SAXException {
        this.controller.fatalError(e);
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void error(SAXParseException e) throws SAXException {
        this.controller.error(e);
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void warning(SAXParseException e) throws SAXException {
        this.controller.warning(e);
    }

    public void setDeclaredLocationOf(Object o) {
        this.declaredLocations.put(o, new LocatorImpl(this.locator));
    }

    public Locator getDeclaredLocationOf(Object o) {
        return (Locator) this.declaredLocations.get(o);
    }

    public boolean isUnparsedEntity(String entityName) {
        return true;
    }

    public String resolveNamespacePrefix(String prefix) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void externalGeneralEntityDecl(String n, String p, String s) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void internalGeneralEntityDecl(String n, String v) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void externalParameterEntityDecl(String n, String p, String s) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void internalParameterEntityDecl(String n, String v) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startDTD(InputEntity in) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void comment(String n) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startCDATA() throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endCDATA() throws SAXException {
    }
}
