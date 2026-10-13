package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.aowagie.text.ElementTags;
import com.ctc.wstx.shaded.msv.org_isorelax.verifier.Schema;
import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import com.ctc.wstx.shaded.msv_core.datatype.ErrorDatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.ChoiceState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.TerminalState;
import com.ctc.wstx.shaded.msv_core.reader.trex.DivInGrammarState;
import com.ctc.wstx.shaded.msv_core.reader.trex.IncludePatternState;
import com.ctc.wstx.shaded.msv_core.reader.trex.NameClassChoiceState;
import com.ctc.wstx.shaded.msv_core.reader.trex.RootState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXSequencedStringChecker;
import com.ctc.wstx.shaded.msv_core.util.LightStack;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.Util;
import com.ctc.wstx.shaded.msv_core.verifier.jarv.RELAXNGFactoryImpl;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.mozilla.javascript.ES6Iterator;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/RELAXNGReader.class */
public class RELAXNGReader extends TREXBaseReader {
    protected static Schema relaxNGSchema4Schema = null;
    private final Map refExpParseInfos;
    protected RefExpParseInfo currentNamedPattern;
    protected boolean directRefernce;
    public static final String RELAXNGNamespace = "http://relaxng.org/ns/structure/1.0";
    private DatatypeLibraryFactory datatypeLibraryFactory;
    protected final RestrictionChecker restrictionChecker;
    private DatatypeLibrary datatypeLib;
    protected String datatypeLibURI;
    private final LightStack dtLibStack;
    private final LightStack dtLibURIStack;
    public static final String ERR_BAD_FACET = "RELAXNGReader.BadFacet";
    public static final String ERR_INVALID_PARAMETERS = "RELAXNGReader.InvalidParameters";
    public static final String ERR_BAD_DATA_VALUE = "RELAXNGReader.BadDataValue";
    public static final String ERR_UNDEFINED_KEY = "RELAXNGReader.UndefinedKey";
    public static final String ERR_UNDEFINED_DATATYPE_1 = "RELAXNGReader.UndefinedDataType1";
    public static final String ERR_INCONSISTENT_KEY_TYPE = "RELAXNGReader.InconsistentKeyType";
    public static final String ERR_INCONSISTENT_COMBINE = "RELAXNGReader.InconsistentCombine";
    public static final String ERR_REDEFINING_UNDEFINED = "RELAXNGReader.RedefiningUndefined";
    public static final String ERR_UNKNOWN_DATATYPE_VOCABULARY_1 = "RELAXNGReader.UnknownDatatypeVocabulary1";
    public static final String ERR_MULTIPLE_EXCEPT = "RELAXNGReader.MultipleExcept";
    public static final String ERR_NOT_ABSOLUTE_URI = "RELAXNGReader.NotAbsoluteURI";
    public static final String ERR_INFOSET_URI_ATTRIBUTE = "RELAXNGReader.InfosetUriAttribute";
    public static final String ERR_XMLNS_ATTRIBUTE = "RELAXNGReader.XmlnsAttribute";
    public static final String ERR_NAKED_INFINITE_ATTRIBUTE_NAMECLASS = "RELAXNGReader.NakedInfiniteAttributeNameClass";

    public static TREXGrammar parse(String grammarURL, SAXParserFactory factory, GrammarReaderController controller) {
        RELAXNGReader reader = new RELAXNGReader(controller, factory);
        reader.parse(grammarURL);
        return reader.getResult();
    }

    public static TREXGrammar parse(InputSource grammar, SAXParserFactory factory, GrammarReaderController controller) {
        RELAXNGReader reader = new RELAXNGReader(controller, factory);
        reader.parse(grammar);
        return reader.getResult();
    }

    public RELAXNGReader(GrammarReaderController controller) {
        this(controller, createParserFactory());
    }

    public RELAXNGReader(GrammarReaderController controller, SAXParserFactory parserFactory) {
        this(controller, parserFactory, new StateFactory(), new ExpressionPool());
    }

    public RELAXNGReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool) {
        super(controller, parserFactory, pool, stateFactory, new RootState());
        this.refExpParseInfos = new HashMap();
        this.currentNamedPattern = null;
        this.directRefernce = true;
        this.datatypeLibraryFactory = new DefaultDatatypeLibraryFactory();
        this.restrictionChecker = new RestrictionChecker(this);
        this.datatypeLib = resolveDataTypeLibrary("");
        this.datatypeLibURI = "";
        this.dtLibStack = new LightStack();
        this.dtLibURIStack = new LightStack();
    }

    public static Schema getRELAXNGSchema4Schema() {
        if (relaxNGSchema4Schema == null) {
            try {
                relaxNGSchema4Schema = new RELAXNGFactoryImpl().compileSchema(RELAXNGReader.class.getResourceAsStream("relaxng.rng"));
            } catch (Exception e) {
                e.printStackTrace();
                throw new Error("unable to load schema-for-schema for RELAX NG");
            }
        }
        return relaxNGSchema4Schema;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        try {
            String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.trex.ng.Messages").getString(propertyName);
            return MessageFormat.format(format, args);
        } catch (Exception e) {
            return super.localizeMessage(propertyName, args);
        }
    }

    protected TREXGrammar getGrammar() {
        return this.grammar;
    }

    protected RefExpParseInfo getRefExpParseInfo(ReferenceExp exp) {
        RefExpParseInfo r = (RefExpParseInfo) this.refExpParseInfos.get(exp);
        if (r == null) {
            Map map = this.refExpParseInfos;
            RefExpParseInfo refExpParseInfo = new RefExpParseInfo();
            r = refExpParseInfo;
            map.put(exp, refExpParseInfo);
        }
        return r;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/RELAXNGReader$RefExpParseInfo.class */
    protected static class RefExpParseInfo {
        public static final RedefinitionStatus notBeingRedefined = new RedefinitionStatus();
        public static final RedefinitionStatus originalNotFoundYet = new RedefinitionStatus();
        public static final RedefinitionStatus originalFound = new RedefinitionStatus();
        public boolean haveHead = false;
        public String combineMethod = null;
        public RedefinitionStatus redefinition = notBeingRedefined;
        public final Vector directRefs = new Vector();
        public final Vector indirectRefs = new Vector();

        /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/RELAXNGReader$RefExpParseInfo$RedefinitionStatus.class */
        public static class RedefinitionStatus {
        }

        protected RefExpParseInfo() {
        }

        public void set(RefExpParseInfo rhs) {
            this.haveHead = rhs.haveHead;
            this.combineMethod = rhs.combineMethod;
            this.redefinition = rhs.redefinition;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected boolean isGrammarElement(StartTagInfo tag) {
        return RELAXNGNamespace.equals(tag.namespaceURI) || XMLValidationSchema.SCHEMA_ID_RELAXNG.equals(tag.namespaceURI);
    }

    public DatatypeLibraryFactory getDatatypeLibraryFactory() {
        return this.datatypeLibraryFactory;
    }

    public void setDatatypeLibraryFactory(DatatypeLibraryFactory datatypeLibraryFactory) {
        this.datatypeLibraryFactory = datatypeLibraryFactory;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/RELAXNGReader$StateFactory.class */
    public static class StateFactory extends TREXBaseReader.StateFactory {
        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State nsAnyName(State parent, StartTagInfo tag) {
            return new NGNameState.AnyNameState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State nsNsName(State parent, StartTagInfo tag) {
            return new NGNameState.NsNameState();
        }

        public State nsExcept(State parent, StartTagInfo tag) {
            return new NameClassChoiceState();
        }

        public State text(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.anyString);
        }

        public State data(State parent, StartTagInfo tag) {
            return new DataState();
        }

        public State dataParam(State parent, StartTagInfo tag) {
            return new DataParamState();
        }

        public State value(State parent, StartTagInfo tag) {
            return new ValueState();
        }

        public State list(State parent, StartTagInfo tag) {
            return new ListState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State define(State parent, StartTagInfo tag) {
            return new DefineState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State start(State parent, StartTagInfo tag) {
            return new StartState();
        }

        public State redefine(State parent, StartTagInfo tag) {
            return new DefineState();
        }

        public State redefineStart(State parent, StartTagInfo tag) {
            return new StartState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State includeGrammar(State parent, StartTagInfo tag) {
            return new IncludeMergeState();
        }

        public State externalRef(State parent, StartTagInfo tag) {
            return new IncludePatternState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State divInGrammar(State parent, StartTagInfo tag) {
            return new DivInGrammarState();
        }

        public State dataExcept(State parent, StartTagInfo tag) {
            return new ChoiceState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State attribute(State parent, StartTagInfo tag) {
            return new AttributeState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State element(State parent, StartTagInfo tag) {
            return new ElementState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State grammar(State parent, StartTagInfo tag) {
            return new GrammarState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State ref(State parent, StartTagInfo tag) {
            return new RefState(false);
        }

        public State parentRef(State parent, StartTagInfo tag) {
            return new RefState(true);
        }

        protected final DatatypeLibrary getDatatypeLibrary(String namespaceURI) throws Exception {
            throw new UnsupportedOperationException();
        }
    }

    protected StateFactory getStateFactory() {
        return (StateFactory) this.sfactory;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader
    protected State createNameClassChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("name")) {
            return this.sfactory.nsName(parent, tag);
        }
        if (tag.localName.equals("anyName")) {
            return this.sfactory.nsAnyName(parent, tag);
        }
        if (tag.localName.equals("nsName")) {
            return this.sfactory.nsNsName(parent, tag);
        }
        if (tag.localName.equals("choice")) {
            return this.sfactory.nsChoice(parent, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("text")) {
            return getStateFactory().text(parent, tag);
        }
        if (tag.localName.equals("data")) {
            return getStateFactory().data(parent, tag);
        }
        if (tag.localName.equals(ES6Iterator.VALUE_PROPERTY)) {
            return getStateFactory().value(parent, tag);
        }
        if (tag.localName.equals(ElementTags.LIST)) {
            return getStateFactory().list(parent, tag);
        }
        if (tag.localName.equals("externalRef")) {
            return getStateFactory().externalRef(parent, tag);
        }
        return tag.localName.equals("parentRef") ? getStateFactory().parentRef(parent, tag) : super.createExpressionChildState(parent, tag);
    }

    public Datatype resolveDataType(String localName) {
        try {
            return getCurrentDatatypeLibrary().createDatatype(localName);
        } catch (DatatypeException dte) {
            reportError(ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage());
            return StringType.theInstance;
        }
    }

    public DatatypeLibrary resolveDataTypeLibrary(String uri) {
        try {
            DatatypeLibrary lib = this.datatypeLibraryFactory.createDatatypeLibrary(uri);
            if (lib != null) {
                return lib;
            }
            reportError(TREXBaseReader.ERR_UNKNOWN_DATATYPE_VOCABULARY, uri);
        } catch (Throwable e) {
            reportError(ERR_UNKNOWN_DATATYPE_VOCABULARY_1, uri, e.toString());
        }
        return ErrorDatatypeLibrary.theInstance;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/RELAXNGReader$AbortException.class */
    private static class AbortException extends Exception {
        private AbortException() {
        }
    }

    private void checkRunawayExpression(ReferenceExp node, Stack items, Set visitedExps) throws AbortException {
        if (!visitedExps.add(node)) {
            return;
        }
        items.push(node);
        for (ReferenceExp child : getRefExpParseInfo(node).directRefs) {
            int idx = items.lastIndexOf(child);
            if (idx != -1) {
                String s = "";
                Vector locs = new Vector();
                while (idx < items.size()) {
                    ReferenceExp e = (ReferenceExp) items.get(idx);
                    if (e.name != null) {
                        if (s.length() != 0) {
                            s = s + " > ";
                        }
                        s = s + e.name;
                        Locator loc = getDeclaredLocationOf(e);
                        if (loc != null) {
                            locs.add(loc);
                        }
                    }
                    idx++;
                }
                reportError((Locator[]) locs.toArray(new Locator[locs.size()]), GrammarReader.ERR_RUNAWAY_EXPRESSION, new Object[]{s + " > " + child.name});
                throw new AbortException();
            }
            checkRunawayExpression(child, items, visitedExps);
        }
        Stack empty = new Stack();
        Iterator itr = getRefExpParseInfo(node).indirectRefs.iterator();
        while (itr.hasNext()) {
            checkRunawayExpression((ReferenceExp) itr.next(), empty, visitedExps);
        }
        items.pop();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader
    public void wrapUp() {
        try {
            checkRunawayExpression(this.grammar, new Stack(), new HashSet());
        } catch (AbortException e) {
        }
        if (!this.controller.hadError()) {
            this.grammar.visit(new TREXSequencedStringChecker(this, true));
        }
        if (!this.controller.hadError()) {
            this.restrictionChecker.check();
        }
    }

    public DatatypeLibrary getCurrentDatatypeLibrary() {
        if (this.datatypeLib == null) {
            this.datatypeLib = resolveDataTypeLibrary(this.datatypeLibURI);
            if (this.datatypeLib == null) {
                throw new Error();
            }
        }
        return this.datatypeLib;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader, com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
    public String resolveNamespacePrefix(String prefix) {
        return prefix.equals("") ? this.targetNamespace : super.resolveNamespacePrefix(prefix);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
        this.dtLibStack.push(this.datatypeLib);
        this.dtLibURIStack.push(this.datatypeLibURI);
        this.datatypeLib = resolveDataTypeLibrary("");
        this.datatypeLibURI = "";
        super.startDocument();
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
        super.endDocument();
        this.datatypeLib = (DatatypeLibrary) this.dtLibStack.pop();
        this.datatypeLibURI = (String) this.dtLibURIStack.pop();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startElement(String a, String b, String c, Attributes d) throws SAXException {
        this.dtLibStack.push(this.datatypeLib);
        this.dtLibURIStack.push(this.datatypeLibURI);
        if (d.getIndex("datatypeLibrary") != -1) {
            this.datatypeLibURI = d.getValue("datatypeLibrary");
            this.datatypeLib = null;
            if (!Util.isAbsoluteURI(this.datatypeLibURI)) {
                reportError(ERR_NOT_ABSOLUTE_URI, this.datatypeLibURI);
            }
            if (this.datatypeLibURI.indexOf(35) >= 0) {
                reportError(GrammarReader.ERR_FRAGMENT_IDENTIFIER, this.datatypeLibURI);
            }
        }
        super.startElement(a, b, c, d);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endElement(String a, String b, String c) throws SAXException {
        super.endElement(a, b, c);
        this.datatypeLib = (DatatypeLibrary) this.dtLibStack.pop();
        this.datatypeLibURI = (String) this.dtLibURIStack.pop();
    }
}
