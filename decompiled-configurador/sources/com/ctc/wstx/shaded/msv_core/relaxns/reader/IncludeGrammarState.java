package com.ctc.wstx.shaded.msv_core.relaxns.reader;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/IncludeGrammarState.class */
public class IncludeGrammarState extends ChildlessState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String href = this.startTag.getAttribute("grammarLocation");
        if (href == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "include", "grammarLocation");
        } else {
            try {
                this.reader.switchSource(this, href, new RootGrammarMergeState());
            } catch (AbortException e) {
            }
        }
    }
}
