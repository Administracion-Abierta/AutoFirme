package com.ctc.wstx.shaded.msv_core.reader.relax.core.checker;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/checker/DblAttrConstraintChecker.class */
public class DblAttrConstraintChecker implements RELAXExpressionVisitorVoid {
    private final Map atts = new HashMap();
    private ReferenceExp current;

    public void check(TagClause clause, RELAXCoreReader reader) {
        this.atts.clear();
        this.current = clause;
        try {
            clause.visit((RELAXExpressionVisitorVoid) this);
        } catch (Eureka e) {
            reader.reportError(new Locator[]{reader.getDeclaredLocationOf(this.current), reader.getDeclaredLocationOf((ReferenceExp) this.atts.get(e.name))}, RELAXCoreReader.ERR_MULTIPLE_ATTRIBUTE_CONSTRAINT, new Object[]{e.name.localName});
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/checker/DblAttrConstraintChecker$Eureka.class */
    private static final class Eureka extends RuntimeException {
        final StringPair name;

        Eureka(StringPair an) {
            this.name = an;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAttribute(AttributeExp exp) {
        if (exp.nameClass instanceof SimpleNameClass) {
            SimpleNameClass nc = (SimpleNameClass) exp.nameClass;
            StringPair p = new StringPair(nc.namespaceURI, nc.localName);
            if (this.atts.containsKey(p)) {
                throw new Eureka(p);
            }
            this.atts.put(p, this.current);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onAttPool(AttPoolClause exp) {
        ReferenceExp old = this.current;
        this.current = exp;
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
        this.current = old;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onSequence(SequenceExp exp) {
        exp.exp1.visit((RELAXExpressionVisitorVoid) this);
        exp.exp2.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onChoice(ChoiceExp exp) {
        exp.exp1.visit((RELAXExpressionVisitorVoid) this);
        exp.exp2.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onEpsilon() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onRef(ReferenceExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOther(OtherExp exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onElement(ElementExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onMixed(MixedExp exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onNullSet() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAnyString() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onData(DataExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onValue(ValueExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onTag(TagClause exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onElementRules(ElementRules exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onHedgeRules(HedgeRules exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onConcur(ConcurExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onInterleave(InterleaveExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onList(ListExp exp) {
    }
}
