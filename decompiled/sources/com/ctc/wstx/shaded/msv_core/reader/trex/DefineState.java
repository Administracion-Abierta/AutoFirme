package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/DefineState.class */
public abstract class DefineState extends SimpleState implements ExpressionOwner {
    protected Expression exp = null;
    private ReferenceExp ref;

    protected abstract Expression doCombine(ReferenceExp referenceExp, Expression expression, String str);

    public final ReferenceExp getRef() {
        return this.ref;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public final void onEndChild(Expression child) {
        if (this.exp == null) {
            this.exp = child;
        } else {
            this.exp = this.reader.pool.createSequence(this.exp, child);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        this.ref = getReference();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        if (this.exp == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_CHILD_EXPRESSION);
            this.exp = Expression.nullSet;
        }
        if (this.ref == null) {
            return;
        }
        TREXBaseReader reader = (TREXBaseReader) this.reader;
        String combine = this.startTag.getCollapsedAttribute("combine");
        this.exp = callInterceptExpression(this.exp);
        Expression newexp = doCombine(this.ref, this.exp, combine);
        if (newexp == null) {
            reader.reportError(TREXBaseReader.ERR_BAD_COMBINE, combine);
        } else {
            this.ref.exp = newexp;
        }
        reader.setDeclaredLocationOf(this.ref);
        ((ExpressionOwner) this.parentState).onEndChild(this.ref);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return this.reader.createExpressionChildState(this, tag);
    }

    protected ReferenceExp getReference() {
        String name = this.startTag.getCollapsedAttribute("name");
        if (name == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "ref", "name");
            return null;
        }
        TREXBaseReader reader = (TREXBaseReader) this.reader;
        return reader.grammar.namedPatterns.getOrCreate(name);
    }
}
