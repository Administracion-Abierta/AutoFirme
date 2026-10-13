package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/SchemaState.class */
public class SchemaState extends SchemaIncludedState {
    private XMLSchemaSchema old;

    protected SchemaState(String expectedTargetNamespace) {
        super(expectedTargetNamespace);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaIncludedState
    protected void onTargetNamespaceResolved(String targetNs, boolean ignoreContents) {
        super.onTargetNamespaceResolved(targetNs, ignoreContents);
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        this.old = reader.currentSchema;
        reader.currentSchema = reader.getOrCreateSchema(targetNs);
        if (ignoreContents) {
            return;
        }
        if (reader.isSchemaDefined(reader.currentSchema)) {
            reader.reportError(XMLSchemaReader.ERR_DUPLICATE_SCHEMA_DEFINITION, targetNs);
            reader.currentSchema = new XMLSchemaSchema(targetNs, reader.grammar);
        }
        reader.markSchemaAsDefined(reader.currentSchema);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.SchemaIncludedState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        reader.currentSchema = this.old;
        super.endSelf();
    }
}
