package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.RedefinableExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/RedefinableDeclState.class */
public abstract class RedefinableDeclState extends ExpressionWithChildState {
    protected RedefinableExp oldDecl;

    protected abstract ReferenceContainer getContainer();

    protected boolean isGlobal() {
        return this.parentState instanceof GlobalDeclState;
    }

    protected boolean isRedefine() {
        return this.parentState instanceof RedefineState;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        if (isRedefine()) {
            XMLSchemaReader reader = (XMLSchemaReader) this.reader;
            String name = this.startTag.getAttribute("name");
            if (name == null) {
                return;
            }
            this.oldDecl = (RedefinableExp) getContainer()._get(name);
            if (this.oldDecl == null) {
                reader.reportError(XMLSchemaReader.ERR_REDEFINE_UNDEFINED, name);
                this.oldDecl = (RedefinableExp) getContainer()._getOrCreate(name);
            } else {
                getContainer().redefine(name, this.oldDecl.getClone());
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected void endSelf() {
        if (this.oldDecl != null) {
            getContainer().redefine(this.oldDecl.name, this.oldDecl);
        }
        super.endSelf();
    }
}
