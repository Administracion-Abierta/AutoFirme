package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/GrammarState.class */
public class GrammarState extends DivInGrammarState {
    protected TREXGrammar previousGrammar;
    protected TREXGrammar newGrammar;

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.DivInGrammarState, com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        return this.newGrammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        this.previousGrammar = getReader().grammar;
        this.newGrammar = getReader().sfactory.createGrammar(this.reader.pool, this.previousGrammar);
        getReader().grammar = this.newGrammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    public void endSelf() {
        TREXGrammar grammar = getReader().grammar;
        this.reader.detectUndefinedOnes(grammar.namedPatterns, TREXBaseReader.ERR_UNDEFINED_PATTERN);
        if (grammar.exp == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_TOPLEVEL);
            grammar.exp = Expression.nullSet;
        }
        if (this.previousGrammar != null) {
            getReader().grammar = this.previousGrammar;
        }
        super.endSelf();
    }
}
