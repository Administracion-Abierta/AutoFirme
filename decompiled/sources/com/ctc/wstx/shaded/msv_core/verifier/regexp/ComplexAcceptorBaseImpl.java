package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ComplexAcceptorBaseImpl.class */
public abstract class ComplexAcceptorBaseImpl extends ContentModelAcceptor {
    protected final Expression[] contents;

    public ComplexAcceptorBaseImpl(REDocumentDeclaration docDecl, Expression combined, Expression[] contents, boolean ignoreUndeclaredAttributes) {
        super(docDecl, combined, ignoreUndeclaredAttributes);
        this.contents = contents;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor, com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public final boolean onText2(String literal, IDContextProvider2 context, StringRef refErr, DatatypeRef refType) {
        if (!super.onText2(literal, context, refErr, refType)) {
            return false;
        }
        StringToken token = new StringToken(this.docDecl, literal, context);
        ResidualCalculator res = this.docDecl.resCalc;
        for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = res.calcResidual(this.contents[i], token);
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ContentModelAcceptor, com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public final boolean stepForward(Acceptor child, StringRef errRef) {
        Token token;
        if (!super.stepForward(child, errRef)) {
            return false;
        }
        ResidualCalculator res = this.docDecl.resCalc;
        if (child instanceof SimpleAcceptor) {
            ElementExp cowner = ((SimpleAcceptor) child).owner;
            if (cowner == null) {
                token = AnyElementToken.theInstance;
            } else {
                token = new ElementToken(new ElementExp[]{cowner});
            }
        } else if (errRef != null) {
            token = new ElementToken(((ComplexAcceptor) child).owners);
        } else {
            token = new ElementToken(((ComplexAcceptor) child).getSatisfiedOwners());
        }
        for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = res.calcResidual(this.contents[i], token);
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor
    protected boolean onAttribute(AttributeToken token, StringRef refErr) {
        if (!super.onAttribute(token, refErr)) {
            return false;
        }
        for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = this.docDecl.attFeeder.feed(this.contents[i], token, this.ignoreUndeclaredAttributes);
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor, com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public boolean onEndAttributes(StartTagInfo sti, StringRef refErr) {
        if (!super.onEndAttributes(sti, refErr)) {
            return false;
        }
        for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = this.docDecl.attPruner.prune(this.contents[i]);
        }
        return true;
    }
}
