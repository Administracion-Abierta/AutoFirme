package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/HedgeRuleBaseState.class */
public abstract class HedgeRuleBaseState extends SimpleState implements ExpressionOwner {
    private Expression contentModel = null;

    protected abstract void endSelf(Expression expression);

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
        if (this.contentModel != null) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
        }
        this.contentModel = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected final void endSelf() {
        super.endSelf();
        if (this.contentModel == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_CHILD_EXPRESSION);
        } else {
            endSelf(this.contentModel);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return this.reader.createExpressionChildState(this, tag);
    }
}
