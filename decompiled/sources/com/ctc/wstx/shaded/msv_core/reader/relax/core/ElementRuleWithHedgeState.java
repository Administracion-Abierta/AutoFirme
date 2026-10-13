package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.relax.RELAXReader;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/ElementRuleWithHedgeState.class */
public class ElementRuleWithHedgeState extends ElementRuleBaseState implements ExpressionOwner {
    protected Expression contentModel = null;

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
        if (this.contentModel != null) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
        }
        this.contentModel = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState
    protected Expression getContentModel() {
        if (this.contentModel == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_CHILD_EXPRESSION);
            return Expression.epsilon;
        }
        return this.contentModel;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.core.ElementRuleBaseState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (!tag.namespaceURI.equals(RELAXReader.RELAXCoreNamespace)) {
            return null;
        }
        State next = this.reader.createExpressionChildState(this, tag);
        return next != null ? next : super.createChildState(tag);
    }
}
