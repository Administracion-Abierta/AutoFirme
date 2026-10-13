package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/SimpleAcceptor.class */
public class SimpleAcceptor extends ContentModelAcceptor {
    public final Expression continuation;
    public final ElementExp owner;

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ContentModelAcceptor, com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public final Object getOwnerType() {
        return this.owner;
    }

    public SimpleAcceptor(REDocumentDeclaration docDecl, Expression combined, ElementExp owner, Expression continuation) {
        super(docDecl, combined, owner == null ? true : owner.ignoreUndeclaredAttributes);
        this.continuation = continuation;
        this.owner = owner;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Acceptor createClone() {
        return new SimpleAcceptor(this.docDecl, getExpression(), this.owner, this.continuation);
    }
}
