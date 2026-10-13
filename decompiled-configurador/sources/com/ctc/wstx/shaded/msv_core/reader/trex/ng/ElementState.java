package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/ElementState.class */
public class ElementState extends com.ctc.wstx.shaded.msv_core.reader.trex.ElementState {
    private boolean previousDirectReference;

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassAndExpressionState, com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        this.previousDirectReference = ((RELAXNGReader) this.reader).directRefernce;
        ((RELAXNGReader) this.reader).directRefernce = false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.NameClassAndExpressionState, com.ctc.wstx.shaded.msv_core.reader.ExpressionState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        reader.directRefernce = this.previousDirectReference;
        super.endSelf();
        reader.restrictionChecker.checkNameClass(this.nameClass);
    }
}
