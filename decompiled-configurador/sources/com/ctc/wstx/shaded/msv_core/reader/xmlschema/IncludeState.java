package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/IncludeState.class */
public class IncludeState extends ChildlessState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        super.startSelf();
        try {
            reader.switchSource(this, new RootIncludedSchemaState(reader.sfactory.schemaIncluded(this, reader.currentSchema.targetNamespace)));
        } catch (AbortException e) {
        }
    }
}
