package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/ChildlessState.class */
public class ChildlessState extends SimpleState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected final State createChildState(StartTagInfo tag) {
        return null;
    }
}
