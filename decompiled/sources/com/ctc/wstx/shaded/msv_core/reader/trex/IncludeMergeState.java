package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/IncludeMergeState.class */
public class IncludeMergeState extends SimpleState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        String href = this.startTag.getAttribute("href");
        if (href == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, Config.PROP_INCLUDE, "href");
        } else {
            try {
                TREXBaseReader reader = (TREXBaseReader) this.reader;
                reader.switchSource(this, href, reader.sfactory.includedGrammar());
            } catch (AbortException e) {
            }
        }
        super.endSelf();
    }
}
