package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/DivInGrammarState.class */
public class DivInGrammarState extends ExpressionState implements ExpressionOwner {
    protected final TREXBaseReader getReader() {
        return (TREXBaseReader) this.reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("start")) {
            return getReader().sfactory.start(this, tag);
        }
        if (tag.localName.equals("define")) {
            return getReader().sfactory.define(this, tag);
        }
        if (tag.localName.equals(Config.PROP_INCLUDE)) {
            return getReader().sfactory.includeGrammar(this, tag);
        }
        if (tag.localName.equals("div")) {
            return getReader().sfactory.divInGrammar(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
    }
}
