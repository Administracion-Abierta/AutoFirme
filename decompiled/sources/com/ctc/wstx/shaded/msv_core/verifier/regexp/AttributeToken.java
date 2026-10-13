package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/AttributeToken.class */
public class AttributeToken extends Token {
    public String namespaceURI;
    public String localName;
    public String qName;
    public StringToken value;
    protected REDocumentDeclaration docDecl;
    public AttributeExp matchedExp;
    private boolean saturated;

    AttributeToken(REDocumentDeclaration docDecl) {
        this.matchedExp = null;
        this.saturated = false;
        this.docDecl = docDecl;
    }

    protected AttributeToken(REDocumentDeclaration docDecl, String namespaceURI, String localName, String qName, String value, IDContextProvider2 context) {
        this(docDecl, namespaceURI, localName, qName, new StringToken(docDecl, value, context, new DatatypeRef()));
    }

    protected AttributeToken(REDocumentDeclaration docDecl, String namespaceURI, String localName, String qName, StringToken value) {
        this(docDecl);
        reinit(namespaceURI, localName, qName, value);
    }

    void reinit(String namespaceURI, String localName, String qName, StringToken value) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.qName = qName;
        this.value = value;
        this.matchedExp = null;
        this.saturated = false;
    }

    final AttributeRecoveryToken createRecoveryAttToken() {
        return new AttributeRecoveryToken(this.docDecl, this.namespaceURI, this.localName, this.qName, this.value);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(AttributeExp exp) {
        if (!exp.nameClass.accepts(this.namespaceURI, this.localName)) {
            return false;
        }
        boolean satisfied = false;
        if ((this.value.literal.trim().length() == 0 && exp.exp.isEpsilonReducible()) || this.docDecl.resCalc.calcResidual(exp.exp, this.value).isEpsilonReducible()) {
            satisfied = true;
        }
        if (satisfied) {
            if (!this.saturated || exp == this.matchedExp) {
                this.matchedExp = exp;
            } else {
                this.matchedExp = null;
            }
            this.saturated = true;
            return true;
        }
        return false;
    }
}
