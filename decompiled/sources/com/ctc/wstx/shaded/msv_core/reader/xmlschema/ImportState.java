package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/ImportState.class */
public class ImportState extends ChildlessState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String namespace = this.startTag.getAttribute("namespace");
        if (namespace == null) {
            namespace = "";
        }
        if (namespace.equals(reader.currentSchema.targetNamespace)) {
            reader.reportError(XMLSchemaReader.ERR_IMPORTING_SAME_NAMESPACE);
        } else {
            if (reader.isSchemaDefined(reader.getOrCreateSchema(namespace))) {
                return;
            }
            try {
                reader.switchSource(this, new RootIncludedSchemaState(reader.sfactory.schemaHead(namespace)));
            } catch (AbortException e) {
            }
        }
    }
}
