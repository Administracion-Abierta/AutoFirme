package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ComplexAcceptor.class */
public final class ComplexAcceptor extends ComplexAcceptorBaseImpl {
    public final ElementExp[] owners;

    private static Expression[] createDefaultContentModels(ElementExp[] owners, ExpressionPool pool) {
        Expression[] r = new Expression[owners.length];
        for (int i = 0; i < owners.length; i++) {
            r[i] = owners[i].contentModel.getExpandedExp(pool);
        }
        return r;
    }

    public ComplexAcceptor(REDocumentDeclaration docDecl, Expression combined, ElementExp[] primitives) {
        this(docDecl, combined, createDefaultContentModels(primitives, docDecl.pool), primitives);
    }

    public ComplexAcceptor(REDocumentDeclaration docDecl, Expression combined, Expression[] contentModels, ElementExp[] owners) {
        super(docDecl, combined, contentModels, owners[0].ignoreUndeclaredAttributes);
        this.owners = owners;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Acceptor createClone() {
        Expression[] models = new Expression[this.contents.length];
        System.arraycopy(this.contents, 0, models, 0, this.contents.length);
        return new ComplexAcceptor(this.docDecl, getExpression(), models, this.owners);
    }

    public final ElementExp[] getSatisfiedOwners() {
        int cnt = 0;
        for (int i = 0; i < this.contents.length; i++) {
            if (this.contents[i].isEpsilonReducible()) {
                cnt++;
            }
        }
        if (cnt == 0) {
            return new ElementExp[0];
        }
        ElementExp[] satisfied = new ElementExp[cnt];
        int cnt2 = 0;
        for (int i2 = 0; i2 < this.contents.length; i2++) {
            if (this.contents[i2].isEpsilonReducible()) {
                int i3 = cnt2;
                cnt2++;
                satisfied[i3] = this.owners[i2];
            }
        }
        return satisfied;
    }
}
