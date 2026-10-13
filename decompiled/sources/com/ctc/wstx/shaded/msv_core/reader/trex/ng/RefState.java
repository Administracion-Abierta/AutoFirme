package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/RefState.class */
public class RefState extends com.ctc.wstx.shaded.msv_core.reader.trex.RefState {
    public RefState(boolean parentRef) {
        super(parentRef);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.RefState
    protected void wrapUp(ReferenceExp r) {
        super.wrapUp(r);
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        if (reader.currentNamedPattern != null) {
            if (reader.directRefernce) {
                reader.currentNamedPattern.directRefs.add(r);
            } else {
                reader.currentNamedPattern.indirectRefs.add(r);
            }
        }
    }
}
