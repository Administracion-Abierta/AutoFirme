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
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import java.util.Stack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/checker/ExportedHedgeRuleChecker.class */
public final class ExportedHedgeRuleChecker implements RELAXExpressionVisitorBoolean {
    private final RELAXModule module;
    private final Stack traversalStack = new Stack();
    public ReferenceExp[] errorSnapshot = null;

    public ExportedHedgeRuleChecker(RELAXModule module) {
        this.module = module;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onAttribute(AttributeExp exp) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onChoice(ChoiceExp exp) {
        return exp.exp1.visit((RELAXExpressionVisitorBoolean) this) && exp.exp2.visit((RELAXExpressionVisitorBoolean) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onSequence(SequenceExp exp) {
        return exp.exp1.visit((RELAXExpressionVisitorBoolean) this) && exp.exp2.visit((RELAXExpressionVisitorBoolean) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onElement(ElementExp exp) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onOneOrMore(OneOrMoreExp exp) {
        return exp.exp.visit((RELAXExpressionVisitorBoolean) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onMixed(MixedExp exp) {
        return exp.exp.visit((RELAXExpressionVisitorBoolean) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onRef(ReferenceExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onOther(OtherExp exp) {
        return exp.exp.visit((RELAXExpressionVisitorBoolean) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onEpsilon() {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onNullSet() {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onAnyString() {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onData(DataExp exp) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onValue(ValueExp exp) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean
    public boolean onAttPool(AttPoolClause exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean
    public boolean onTag(TagClause exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onInterleave(InterleaveExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onConcur(ConcurExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean
    public boolean onList(ListExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean
    public boolean onElementRules(ElementRules exp) {
        if (exp.ownerModule == this.module) {
            return true;
        }
        takeSnapshot(exp);
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorBoolean
    public boolean onHedgeRules(HedgeRules exp) {
        if (exp.ownerModule != this.module) {
            takeSnapshot(exp);
            return false;
        }
        this.traversalStack.push(exp);
        boolean r = exp.exp.visit((RELAXExpressionVisitorBoolean) this);
        this.traversalStack.pop();
        return r;
    }

    private void takeSnapshot(ReferenceExp lastExp) {
        this.errorSnapshot = new ReferenceExp[this.traversalStack.size() + 1];
        this.traversalStack.toArray(this.errorSnapshot);
        this.errorSnapshot[this.errorSnapshot.length - 1] = lastExp;
    }
}
