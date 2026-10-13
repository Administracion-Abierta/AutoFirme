package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/TerminalState.class */
public class TerminalState extends ExpressionWithoutChildState {
    private final Expression exp;

    public TerminalState(Expression exp) {
        this.exp = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        return this.exp;
    }
}
