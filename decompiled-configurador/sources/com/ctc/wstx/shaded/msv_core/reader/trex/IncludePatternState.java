package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/IncludePatternState.class */
public class IncludePatternState extends ExpressionWithoutChildState implements ExpressionOwner {
    protected Expression included = Expression.nullSet;

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression included) {
        this.included = included;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String href = this.startTag.getAttribute("href");
        if (href == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "href");
            return Expression.nullSet;
        }
        try {
            this.reader.switchSource(this, href, new RootIncludedPatternState(this));
        } catch (AbortException e) {
        }
        return this.included;
    }
}
