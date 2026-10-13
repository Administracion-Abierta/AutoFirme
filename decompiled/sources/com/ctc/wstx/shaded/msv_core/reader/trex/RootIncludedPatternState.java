package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/RootIncludedPatternState.class */
public class RootIncludedPatternState extends SimpleState implements ExpressionOwner {
    private final IncludePatternState grandParent;

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        State s = this.reader.createExpressionChildState(this, tag);
        return s;
    }

    protected RootIncludedPatternState(IncludePatternState grandpa) {
        this.grandParent = grandpa;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
        if (this.grandParent != null) {
            this.grandParent.onEndChild(exp);
        }
    }
}
