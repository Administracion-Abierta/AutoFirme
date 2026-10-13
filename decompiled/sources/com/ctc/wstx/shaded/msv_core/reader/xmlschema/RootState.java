package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.State;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/RootState.class */
public class RootState extends RootIncludedSchemaState {
    protected RootState(State topLevelState) {
        super(topLevelState);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        reader.wrapUp();
        super.endSelf();
    }
}
