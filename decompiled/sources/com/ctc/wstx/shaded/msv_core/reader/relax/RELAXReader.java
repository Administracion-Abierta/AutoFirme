package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.reader.ChoiceState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReaderController;
import com.ctc.wstx.shaded.msv_core.reader.SequenceState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.TerminalState;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.FacetState;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.InlineElementState;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.xml.parsers.SAXParserFactory;
import org.ini4j.Config;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/RELAXReader.class */
public abstract class RELAXReader extends GrammarReader {
    public static final String RELAXCoreNamespace = "http://www.xml.gr.jp/xmlns/relaxCore";
    public final StateFactory sfactory;
    public static final String ERR_ILLEGAL_OCCURS = "RELAXReader.IllegalOccurs";
    public static final String ERR_MISPLACED_OCCURS = "RELAXReader.MisplacedOccurs";

    protected abstract Expression resolveElementRef(String str, String str2);

    protected abstract Expression resolveHedgeRef(String str, String str2);

    public RELAXReader(GrammarReaderController controller, SAXParserFactory parserFactory, StateFactory stateFactory, ExpressionPool pool, State initialState) {
        super(controller, parserFactory, pool, initialState);
        this.sfactory = stateFactory;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/RELAXReader$StateFactory.class */
    public static class StateFactory {
        protected State refLabel(State parent, StartTagInfo tag) {
            return new ElementRefState();
        }

        protected State hedgeRef(State parent, StartTagInfo tag) {
            return new HedgeRefState();
        }

        protected State choice(State parent, StartTagInfo tag) {
            return new ChoiceState();
        }

        protected State none(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.nullSet);
        }

        protected State empty(State parent, StartTagInfo tag) {
            return new TerminalState(Expression.epsilon);
        }

        protected State sequence(State parent, StartTagInfo tag) {
            return new SequenceState();
        }

        protected FacetState facets(State parent, StartTagInfo tag) {
            return new FacetState();
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    public State createExpressionChildState(State parent, StartTagInfo tag) {
        if (tag.localName.equals("ref")) {
            return this.sfactory.refLabel(parent, tag);
        }
        if (tag.localName.equals("hedgeRef")) {
            return this.sfactory.hedgeRef(parent, tag);
        }
        if (tag.localName.equals("choice")) {
            return this.sfactory.choice(parent, tag);
        }
        if (tag.localName.equals("none")) {
            return this.sfactory.none(parent, tag);
        }
        if (tag.localName.equals("empty")) {
            return this.sfactory.empty(parent, tag);
        }
        if (tag.localName.equals("sequence")) {
            return this.sfactory.sequence(parent, tag);
        }
        return null;
    }

    public FacetState createFacetState(State parent, StartTagInfo tag) {
        if (RELAXCoreNamespace.equals(tag.namespaceURI) && FacetState.facetNames.contains(tag.localName)) {
            return this.sfactory.facets(parent, tag);
        }
        return null;
    }

    protected boolean canHaveOccurs(State state) {
        return (state instanceof SequenceState) || (state instanceof ElementRefState) || (state instanceof HedgeRefState) || (state instanceof ChoiceState) || (state instanceof InlineElementState);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected Expression interceptExpression(State state, Expression exp) {
        String occurs = state.getStartTag().getAttribute("occurs");
        if (canHaveOccurs(state)) {
            if (occurs != null) {
                if (occurs.equals(Config.DEFAULT_GLOBAL_SECTION_NAME)) {
                    exp = this.pool.createOptional(exp);
                } else if (occurs.equals(Marker.ANY_NON_NULL_MARKER)) {
                    exp = this.pool.createOneOrMore(exp);
                } else if (occurs.equals("*")) {
                    exp = this.pool.createZeroOrMore(exp);
                } else {
                    reportError(ERR_ILLEGAL_OCCURS, occurs);
                }
            }
        } else if (occurs != null) {
            reportError(ERR_MISPLACED_OCCURS, state.getStartTag().localName);
        }
        return exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.GrammarReader
    protected String localizeMessage(String propertyName, Object[] args) {
        String format;
        try {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.relax.Messages").getString(propertyName);
        } catch (Exception e) {
            format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.reader.Messages").getString(propertyName);
        }
        return MessageFormat.format(format, args);
    }

    protected ExpressionPool getPool() {
        return this.pool;
    }
}
