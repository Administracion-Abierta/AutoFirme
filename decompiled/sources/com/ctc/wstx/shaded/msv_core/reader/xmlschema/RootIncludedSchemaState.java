package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/RootIncludedSchemaState.class */
public class RootIncludedSchemaState extends SimpleState {
    protected State topLevelState;

    public RootIncludedSchemaState(State topLevelState) {
        this.topLevelState = topLevelState;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("schema")) {
            return this.topLevelState;
        }
        return null;
    }
}
