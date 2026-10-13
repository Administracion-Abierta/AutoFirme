package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassNotState.class */
public class NameClassNotState extends NameClassWithChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassWithChildState
    protected NameClass castNameClass(NameClass halfCastedNameClass, NameClass child) {
        if (halfCastedNameClass != null) {
            this.reader.reportError(TREXBaseReader.ERR_MORE_THAN_ONE_NAMECLASS);
            return halfCastedNameClass;
        }
        return new NotNameClass(child);
    }
}
