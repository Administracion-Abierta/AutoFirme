package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.driver.textui.Debug;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.util.StringRef;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ContentModelAcceptor.class */
public abstract class ContentModelAcceptor extends ExpressionAcceptor {
    protected ContentModelAcceptor(REDocumentDeclaration docDecl, Expression exp, boolean ignoreUndeclaredAttributes) {
        super(docDecl, exp, ignoreUndeclaredAttributes);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public boolean stepForward(Acceptor child, StringRef errRef) {
        if (child instanceof SimpleAcceptor) {
            SimpleAcceptor sa = (SimpleAcceptor) child;
            if (sa.continuation != null) {
                return stepForwardByContinuation(sa.continuation, errRef);
            }
            return stepForward(new ElementToken(new ElementExp[]{sa.owner}), errRef);
        }
        if (child instanceof ComplexAcceptor) {
            ComplexAcceptor ca = (ComplexAcceptor) child;
            return stepForward(new ElementToken(errRef != null ? ca.owners : ca.getSatisfiedOwners()), errRef);
        }
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ExpressionAcceptor
    protected Acceptor createAcceptor(Expression combined, Expression continuation, ElementExp[] primitives, int numPrimitives) {
        if (primitives == null || numPrimitives <= 1) {
            return new SimpleAcceptor(this.docDecl, combined, primitives == null ? null : primitives[0], continuation);
        }
        if (Debug.debug) {
            System.out.println("ComplexAcceptor is used");
        }
        ElementExp[] owners = new ElementExp[numPrimitives];
        System.arraycopy(primitives, 0, owners, 0, numPrimitives);
        return new ComplexAcceptor(this.docDecl, combined, owners);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.Acceptor
    public Object getOwnerType() {
        return null;
    }
}
