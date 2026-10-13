package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/AttributeRecoveryToken.class */
final class AttributeRecoveryToken extends AttributeToken {
    private Expression failedExp;

    AttributeRecoveryToken(REDocumentDeclaration docDecl, String namespaceURI, String localName, String qName, StringToken value) {
        super(docDecl, namespaceURI, localName, qName, value);
        this.failedExp = Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeToken, com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(AttributeExp exp) {
        if (!exp.nameClass.accepts(this.namespaceURI, this.localName)) {
            return false;
        }
        if (!this.docDecl.resCalc.calcResidual(exp.exp, this.value).isEpsilonReducible()) {
            this.failedExp = this.docDecl.pool.createChoice(this.failedExp, exp.exp);
            return true;
        }
        return true;
    }

    Expression getFailedExp() {
        return this.failedExp;
    }
}
