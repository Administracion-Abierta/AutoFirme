package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/RootGrammarMergeState.class */
class RootGrammarMergeState extends SimpleState {
    RootGrammarMergeState() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("grammar")) {
            return new GrammarState();
        }
        return null;
    }
}
