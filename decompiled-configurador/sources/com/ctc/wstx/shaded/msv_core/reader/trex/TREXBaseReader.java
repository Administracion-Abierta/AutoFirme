package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.Grammar;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.ChoiceState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.InterleaveState;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.TerminalState;
import com.ctc.wstx.shaded.msv_core.util.LightStack;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/TREXBaseReader.class */
public abstract class TREXBaseReader extends GrammarReader {
    protected TREXGrammar grammar;
    private LightStack nsStack;
    protected String targetNamespace;
    public final StateFactory sfactory;
    public static final String ERR_MISSING_CHILD_NAMECLASS = "TREXGrammarReader.MissingChildNameClass";
    public static final String ERR_MORE_THAN_ONE_NAMECLASS = "TREXGrammarReader.MoreThanOneNameClass";
    public static final String ERR_UNDECLARED_PREFIX = "TREXGrammarReader.UndeclaredPrefix";
    public static final String ERR_UNDEFINED_PATTERN = "TREXGrammarReader.UndefinedPattern";
    public static final String ERR_UNKNOWN_DATATYPE_VOCABULARY = "TREXGrammarReader.UnknownDataTypeVocabulary";
    public static final String ERR_BAD_COMBINE = "TREXGrammarReader.BadCombine";
    public static final String ERR_COMBINE_MISSING = "TREXGrammarReader.CombineMissing";
    public static final String WRN_COMBINE_IGNORED = "TREXGrammarReader.Warning.CombineIgnored";
    public static final String WRN_OBSOLETED_XMLSCHEMA_NAMSPACE = "TREXGrammarReader.Warning.ObsoletedXMLSchemaNamespace";
    public static final String ERR_DUPLICATE_DEFINITION = "TREXGrammarReader.DuplicateDefinition";
    public static final String ERR_NONEXISTENT_PARENT_GRAMMAR = "TREXGrammarReader.NonExistentParentGrammar";
    public static final String ERR_INTERLEAVED_STRING = "TREXGrammarReader.InterleavedString";
    public static final String ERR_SEQUENCED_STRING = "TREXGrammarReader.SequencedString";
    public static final String ERR_REPEATED_STRING = "TREXGrammarReader.RepeatedString";
    public static final String ERR_INTERLEAVED_ANYSTRING = "TREXGrammarReader.InterleavedAnyString";

    public TREXBaseReader(GrammarReaderController controller, SAXParserFactory parserFactory, ExpressionPool pool, StateFactory stateFactory, State rootState) {
        super(controller, parserFactory, pool, rootState);
        this.nsStack = new LightStack();
        this.targetNamespace = "";
        this.sfactory = stateFactory;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        String format;
        try {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.trex.Messages").getString(propertyName);
        } catch (Exception e) {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.Messages").getString(propertyName);
        }
        return MessageFormat.format(format, args);
    }

    public final TREXGrammar getResult() {
        if (this.controller.hadError()) {
            return null;
        }
        return this.grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public Grammar getResultAsGrammar() {
        return getResult();
    }

    public final String getTargetNamespace() {
        return this.targetNamespace;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/TREXBaseReader$StateFactory.class */
    public static abstract class StateFactory {
        public abstract State define(State state, StartTagInfo startTagInfo);

        public State nsName(State parent, StartTagInfo tag) {
            return new NameClassNameState();
        }

        public State nsAnyName(State parent, StartTagInfo tag) {
            return new NameClassAnyNameState();
        }

        public State nsNsName(State parent, StartTagInfo tag) {
            return new NameClassNsNameState();
        }

        public State nsNot(State parent, StartTagInfo tag) {
            return new NameClassNotState();
        }

        public State nsDifference(State parent, StartTagInfo tag) {
            return new NameClassDifferenceState();
        }

        public State nsChoice(State parent, StartTagInfo tag) {
            return new NameClassChoiceState();
        }

        public State element(State parent, StartTagInfo tag) {
            return new ElementState();
        }

        public State attribute(State parent, StartTagInfo tag) {
            return new AttributeState();
        }

        public State group(State parent, StartTagInfo tag) {
            return new SequenceState();
        }

        public State interleave(State parent, StartTagInfo tag) {
            return new InterleaveState();
        }

        public State choice(State parent, StartTagInfo tag) {
            return new ChoiceState();
        }

        public State optional(State parent, StartTagInfo tag) {
            return new OptionalState();
        }

        public State zeroOrMore(State parent, StartTagInfo tag) {
            return new ZeroOrMoreState();
        }

        public State oneOrMore(State parent, StartTagInfo tag) {
            return new OneOrMoreState();
        }

        public State mixed(State parent, StartTagInfo tag) {
            return new MixedState();
        }

        public State empty(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.epsilon);
        }

        public State notAllowed(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.nullSet);
        }

        public State includeGrammar(State parent, StartTagInfo tag) {
            return new IncludeMergeState();
        }

        public State grammar(State parent, StartTagInfo tag) {
            return new GrammarState();
        }

        public State start(State parent, StartTagInfo tag) {
            return new StartState();
        }

        public State ref(State parent, StartTagInfo tag) {
            return new RefState("true".equals(tag.getAttribute("parent")));
        }

        public State divInGrammar(State parent, StartTagInfo tag) {
            return null;
        }

        public TREXGrammar createGrammar(ExpressionPool pool, TREXGrammar parent) {
            return new TREXGrammar(pool, parent);
        }

        public State includedGrammar() {
            return new RootMergedGrammarState();
        }
    }

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
        if (tag.localName.equals("not")) {
            return this.sfactory.nsNot(parent, tag);
        }
        if (tag.localName.equals("difference")) {
            return this.sfactory.nsDifference(parent, tag);
        }
        if (tag.localName.equals("choice")) {
            return this.sfactory.nsChoice(parent, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("element")) {
            return this.sfactory.element(parent, tag);
        }
        if (tag.localName.equals("attribute")) {
            return this.sfactory.attribute(parent, tag);
        }
        if (tag.localName.equals("group")) {
            return this.sfactory.group(parent, tag);
        }
        if (tag.localName.equals("interleave")) {
            return this.sfactory.interleave(parent, tag);
        }
        if (tag.localName.equals("choice")) {
            return this.sfactory.choice(parent, tag);
        }
        if (tag.localName.equals("optional")) {
            return this.sfactory.optional(parent, tag);
        }
        if (tag.localName.equals("zeroOrMore")) {
            return this.sfactory.zeroOrMore(parent, tag);
        }
        if (tag.localName.equals("oneOrMore")) {
            return this.sfactory.oneOrMore(parent, tag);
        }
        if (tag.localName.equals("mixed")) {
            return this.sfactory.mixed(parent, tag);
        }
        if (tag.localName.equals("ref")) {
            return this.sfactory.ref(parent, tag);
        }
        if (tag.localName.equals("empty")) {
            return this.sfactory.empty(parent, tag);
        }
        if (tag.localName.equals("notAllowed")) {
            return this.sfactory.notAllowed(parent, tag);
        }
        if (tag.localName.equals("grammar")) {
            return this.sfactory.grammar(parent, tag);
        }
        return null;
    }

    public void wrapUp() {
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void startElement(String a, String b, String c, Attributes d) throws SAXException {
        this.nsStack.push(this.targetNamespace);
        if (d.getIndex("ns") != -1) {
            this.targetNamespace = d.getValue("ns");
        }
        super.startElement(a, b, c, d);
    }

    @Override // org.xml.sax.helpers.XMLFilterImpl, org.xml.sax.ContentHandler
    public void endElement(String a, String b, String c) throws SAXException {
        super.endElement(a, b, c);
        this.targetNamespace = (String) this.nsStack.pop();
    }
}
