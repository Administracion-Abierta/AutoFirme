package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import java.util.Collection;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ElementsOfConcernCollector.class */
public class ElementsOfConcernCollector implements ExpressionVisitor {
    private Collection result;

    public final void collect(Expression exp, Collection result) {
        this.result = result;
        exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onAttribute(AttributeExp exp) {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onChoice(ChoiceExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onElement(ElementExp exp) {
        this.result.add(exp);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onMixed(MixedExp exp) {
        exp.exp.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onEpsilon() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onNullSet() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onAnyString() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onData(DataExp exp) {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onValue(ValueExp exp) {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onList(ListExp exp) {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onSequence(SequenceExp exp) {
        exp.exp1.visit(this);
        if (exp.exp1.isEpsilonReducible()) {
            exp.exp2.visit(this);
            return null;
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onConcur(ConcurExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor
    public final Object onInterleave(InterleaveExp exp) {
        exp.exp1.visit(this);
        exp.exp2.visit(this);
        return null;
    }
}
