package com.ctc.wstx.shaded.msv_core.reader.trex;

import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/NameClassWithoutChildState.class */
public abstract class NameClassWithoutChildState extends NameClassState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected final State createChildState(StartTagInfo tag) {
        return null;
    }
}
