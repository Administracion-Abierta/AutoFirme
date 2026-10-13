package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/RefState.class */
public class RefState extends ExpressionWithoutChildState {
    protected boolean parentRef;

    public RefState(boolean parentRef) {
        this.parentRef = parentRef;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        String name = this.startTag.getCollapsedAttribute("name");
        if (name == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "ref", "name");
            return Expression.nullSet;
        }
        TREXGrammar grammar = ((TREXBaseReader) this.reader).grammar;
        if (this.parentRef) {
            grammar = grammar.getParentGrammar();
            if (grammar == null) {
                this.reader.reportError(TREXBaseReader.ERR_NONEXISTENT_PARENT_GRAMMAR);
                return Expression.nullSet;
            }
        }
        ReferenceExp r = grammar.namedPatterns.getOrCreate(name);
        wrapUp(r);
        return r;
    }

    protected void wrapUp(ReferenceExp r) {
        this.reader.backwardReference.memorizeLink(r);
    }
}
