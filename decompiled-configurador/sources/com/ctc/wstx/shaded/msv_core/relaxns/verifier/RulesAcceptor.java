package com.ctc.wstx.shaded.msv_core.relaxns.verifier;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.ElementDecl;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.DeclImpl;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ComplexAcceptorBaseImpl;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/relaxns/verifier/RulesAcceptor.class */
public class RulesAcceptor extends ComplexAcceptorBaseImpl {
    protected final DeclImpl[] owners;

    private static Expression createCombined(ExpressionPool pool, DeclImpl[] rules) {
        Expression exp = Expression.nullSet;
        for (DeclImpl declImpl : rules) {
            exp = pool.createChoice(exp, declImpl.exp);
        }
        return exp;
    }

    private static Expression[] getContents(DeclImpl[] rules) {
        Expression[] r = new Expression[rules.length];
        for (int i = 0; i < rules.length; i++) {
            r[i] = rules[i].exp;
        }
        return r;
    }

    public RulesAcceptor(REDocumentDeclaration docDecl, DeclImpl[] rules) {
        this(docDecl, createCombined(docDecl.pool, rules), getContents(rules), rules);
    }

    private RulesAcceptor(REDocumentDeclaration docDecl, Expression combined, Expression[] contentModels, DeclImpl[] owners) {
        super(docDecl, combined, contentModels, false);
        this.owners = owners;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Acceptor createClone() {
        Expression[] models = new Expression[this.contents.length];
        System.arraycopy(this.contents, 0, models, 0, this.contents.length);
        return new RulesAcceptor(this.docDecl, getExpression(), models, this.owners);
    }

    ElementDecl[] getSatisfiedElementDecls() {
        int cnt = 0;
        for (int i = 0; i < this.owners.length; i++) {
            if (this.contents[i].isEpsilonReducible()) {
                cnt++;
            }
        }
        ElementDecl[] r = new DeclImpl[cnt];
        int cnt2 = 0;
        for (int i2 = 0; i2 < this.owners.length; i2++) {
            if (this.contents[i2].isEpsilonReducible()) {
                int i3 = cnt2;
                cnt2++;
                r[i3] = this.owners[i2];
            }
        }
        return r;
    }
}
