package com.ctc.wstx.shaded.msv_core.reader.relax.core;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/ClauseState.class */
abstract class ClauseState extends SimpleState implements ExpressionOwner {
    protected Expression exp = initialExpression();

    ClauseState() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("ref")) {
            return getReader().getStateFactory().refRole(this, tag);
        }
        if (tag.localName.equals("attribute")) {
            return getReader().getStateFactory().attribute(this, tag);
        }
        return null;
    }

    protected Expression initialExpression() {
        return Expression.epsilon;
    }

    protected Expression castExpression(Expression exp, Expression child) {
        return this.reader.pool.createSequence(exp, child);
    }

    protected RELAXCoreReader getReader() {
        return (RELAXCoreReader) this.reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public final void onEndChild(Expression childExpression) {
        this.exp = castExpression(this.exp, childExpression);
    }
}
