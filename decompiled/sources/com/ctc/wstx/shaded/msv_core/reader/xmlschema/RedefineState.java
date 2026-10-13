package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.AbortException;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/RedefineState.class */
public class RedefineState extends GlobalDeclState {
    private SimpleTypeExp oldSimpleTypeExp;
    private boolean prevDuplicateCheck;

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        if (tag.localName.equals("simpleType")) {
            XMLSchemaReader reader = (XMLSchemaReader) this.reader;
            String name = tag.getAttribute("name");
            SimpleTypeExp sexp = reader.currentSchema.simpleTypes.get(name);
            if (sexp == null) {
                reader.reportError(XMLSchemaReader.ERR_REDEFINE_UNDEFINED, name);
                sexp = reader.currentSchema.simpleTypes.getOrCreate(name);
            }
            reader.currentSchema.simpleTypes.redefine(name, sexp.getClone());
            this.oldSimpleTypeExp = sexp;
        }
        return super.createChildState(tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        super.startSelf();
        try {
            reader.switchSource(this, new RootIncludedSchemaState(reader.sfactory.schemaIncluded(this, reader.currentSchema.targetNamespace)));
        } catch (AbortException e) {
        }
        this.prevDuplicateCheck = reader.doDuplicateDefinitionCheck;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        reader.doDuplicateDefinitionCheck = this.prevDuplicateCheck;
        super.endSelf();
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.GlobalDeclState, com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp type) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String typeName = type.name;
        if (typeName == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name");
            return;
        }
        this.oldSimpleTypeExp.set(type);
        reader.setDeclaredLocationOf(this.oldSimpleTypeExp);
        reader.currentSchema.simpleTypes.redefine(this.oldSimpleTypeExp.name, this.oldSimpleTypeExp);
        this.oldSimpleTypeExp = null;
    }
}
