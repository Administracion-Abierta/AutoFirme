package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/AttributeState.class */
public class AttributeState extends NameClassAndExpressionState {
    protected boolean firstChild = true;

    @Override // com.ctc.wstx.shaded.msv_core.reader.SequenceState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        return Expression.anyString;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassAndExpressionState
    protected String getNamespace() {
        String ns = this.startTag.getAttribute("ns");
        boolean global = "true".equals(this.startTag.getAttribute("global"));
        if (ns != null) {
            return ns;
        }
        return global ? ((TREXBaseReader) this.reader).targetNamespace : "";
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SequenceState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression initialExpression, Expression newChild) {
        if (!this.firstChild) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
        }
        this.firstChild = false;
        return newChild;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentModel) {
        Expression e = this.reader.pool.createAttribute(this.nameClass, contentModel);
        if (e instanceof AttributeExp) {
            this.reader.setDeclaredLocationOf(e);
        }
        return e;
    }
}
