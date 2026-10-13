package com.ctc.wstx.shaded.msv_core.grammar.util;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/util/ExpressionFinder.class */
public abstract class ExpressionFinder implements ExpressionVisitorBoolean {
    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onSequence(SequenceExp exp) {
        return exp.exp1.visit(this) || exp.exp2.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onInterleave(InterleaveExp exp) {
        return exp.exp1.visit(this) || exp.exp2.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onConcur(ConcurExp exp) {
        return exp.exp1.visit(this) || exp.exp2.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onChoice(ChoiceExp exp) {
        return exp.exp1.visit(this) || exp.exp2.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onAttribute(AttributeExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onElement(ElementExp exp) {
        return exp.contentModel.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onMixed(MixedExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onList(ListExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onEpsilon() {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onNullSet() {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onAnyString() {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onData(DataExp exp) {
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onValue(ValueExp exp) {
        return false;
    }
}
