package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.IgnoreState;
import com.ctc.wstx.shaded.msv_core.reader.RunAwayExpressionChecker;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.TerminalState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDVocabulary;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver;
import com.ctc.wstx.shaded.msv_core.reader.trex.IncludePatternState;
import com.ctc.wstx.shaded.msv_core.reader.trex.RootState;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader;
import com.ctc.wstx.shaded.msv_core.reader.trex.TREXSequencedStringChecker;
import com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParserFactory;
import org.ini4j.Config;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/classic/TREXGrammarReader.class */
public class TREXGrammarReader extends TREXBaseReader implements XSDatatypeResolver {
    protected String currentGrammarURI;
    public static final String TREXNamespace = "http://www.thaiopensource.com/trex";
    private boolean issueObsoletedXMLSchemaNamespace;

    public static TREXGrammar parse(String grammarURL, SAXParserFactory factory, GrammarReaderController controller) {
        TREXGrammarReader reader = new TREXGrammarReader(controller, factory, new ExpressionPool());
        reader.parse(grammarURL);
        return reader.getResult();
    }

    public static TREXGrammar parse(InputSource grammar, SAXParserFactory factory, GrammarReaderController controller) {
        TREXGrammarReader reader = new TREXGrammarReader(controller, factory, new ExpressionPool());
        reader.parse(grammar);
        return reader.getResult();
    }

    public TREXGrammarReader(GrammarReaderController controller) {
        this(controller, createParserFactory(), new ExpressionPool());
    }

    public TREXGrammarReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool) {
        this(controller, parserFactory, new StateFactory(), pool);
    }

    public TREXGrammarReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool) {
        super(controller, parserFactory, pool, stateFactory, new RootState());
        this.issueObsoletedXMLSchemaNamespace = false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        try {
            String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.trex.classic.Messages").getString(propertyName);
            return MessageFormat.format(format, args);
        } catch (Exception e) {
            return super.localizeMessage(propertyName, args);
        }
    }

    protected TREXGrammar getGrammar() {
        return this.grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected boolean isGrammarElement(StartTagInfo tag) {
        if (this.currentGrammarURI != null) {
            return this.currentGrammarURI.equals(tag.namespaceURI) || tag.containsAttribute("http://www.thaiopensource.com/trex", "role");
        }
        if (tag.namespaceURI.equals("http://www.thaiopensource.com/trex")) {
            this.currentGrammarURI = "http://www.thaiopensource.com/trex";
            return true;
        }
        if (tag.namespaceURI.equals("")) {
            this.currentGrammarURI = "";
            return true;
        }
        return false;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/classic/TREXGrammarReader$StateFactory.class */
    public static class StateFactory extends TREXBaseReader.StateFactory {
        public State concur(State parent, StartTagInfo tag) {
            return new ConcurState();
        }

        public State anyString(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.anyString);
        }

        public State string(State parent, StartTagInfo tag) {
            return new StringState();
        }

        public State data(State parent, StartTagInfo tag) {
            return new DataState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader.StateFactory
        public State define(State parent, StartTagInfo tag) {
            return new DefineState();
        }

        public State includePattern(State parent, StartTagInfo tag) {
            return new IncludePatternState();
        }
    }

    protected StateFactory getStateFactory() {
        return (StateFactory) this.sfactory;
    }

    private String mapNamespace(String namespace) {
        if (namespace.equals(XMLSchemaReader.XMLSchemaNamespace_old) || namespace.equals("http://www.w3.org/2000/10/XMLSchema-datatypes")) {
            if (!this.issueObsoletedXMLSchemaNamespace) {
                reportWarning(TREXBaseReader.WRN_OBSOLETED_XMLSCHEMA_NAMSPACE, namespace);
            }
            this.issueObsoletedXMLSchemaNamespace = true;
            return XSDVocabulary.XMLSchemaNamespace;
        }
        return namespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader, com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("concur")) {
            return getStateFactory().concur(parent, tag);
        }
        if (tag.localName.equals("anyString")) {
            return getStateFactory().anyString(parent, tag);
        }
        if (tag.localName.equals("string")) {
            return getStateFactory().string(parent, tag);
        }
        if (tag.localName.equals("data")) {
            return getStateFactory().data(parent, tag);
        }
        if (tag.localName.equals(Config.PROP_INCLUDE)) {
            return getStateFactory().includePattern(parent, tag);
        }
        String role = tag.getAttribute("http://www.thaiopensource.com/trex", "role");
        if ("datatype".equals(role)) {
            String namespaceURI = mapNamespace(tag.namespaceURI);
            DataTypeVocabulary v = this.grammar.dataTypes.get(namespaceURI);
            if (v == null) {
                reportError(TREXBaseReader.ERR_UNKNOWN_DATATYPE_VOCABULARY, tag.namespaceURI);
                this.grammar.dataTypes.put(tag.namespaceURI, new UndefinedDataTypeVocabulary());
                return new IgnoreState();
            }
            return v.createTopLevelReaderState(tag);
        }
        return super.createExpressionChildState(parent, tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeResolver
    public XSDatatypeExp resolveXSDatatype(String qName) {
        return new XSDatatypeExp((XSDatatype) resolveDatatype(qName), this.pool);
    }

    public Datatype resolveDatatype(String qName) {
        String[] s = splitQName(qName);
        if (s == null) {
            reportError(TREXBaseReader.ERR_UNDECLARED_PREFIX, qName);
            return StringType.theInstance;
        }
        s[0] = mapNamespace(s[0]);
        DataTypeVocabulary v = this.grammar.dataTypes.get(s[0]);
        if (v == null) {
            reportError(TREXBaseReader.ERR_UNKNOWN_DATATYPE_VOCABULARY, s[0]);
            this.grammar.dataTypes.put(s[0], new UndefinedDataTypeVocabulary());
        } else {
            try {
                return v.getType(s[1]);
            } catch (DatatypeException e) {
                reportError(GrammarReader.ERR_UNDEFINED_DATATYPE, qName);
            }
        }
        return StringType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.TREXBaseReader
    public void wrapUp() {
        RunAwayExpressionChecker.check(this, this.grammar);
        if (!this.controller.hadError()) {
            this.grammar.visit(new TREXSequencedStringChecker(this, false));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/classic/TREXGrammarReader$UndefinedDataTypeVocabulary.class */
    private static class UndefinedDataTypeVocabulary implements DataTypeVocabulary {
        private UndefinedDataTypeVocabulary() {
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary
        public State createTopLevelReaderState(StartTagInfo tag) {
            return new IgnoreState();
        }

        @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.DataTypeVocabulary
        public Datatype getType(String localTypeName) {
            return StringType.theInstance;
        }
    }
}
