package com.ctc.wstx.shaded.msv_core.reader.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/relax/ElementRefState.class */
public class ElementRefState extends LabelRefState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.relax.LabelRefState
    protected final Expression resolve(String namespace, String label) {
        return ((RELAXReader) this.reader).resolveElementRef(namespace, label);
    }
}
