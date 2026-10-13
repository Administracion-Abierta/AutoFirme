package com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaGrammar;
import com.ctc.wstx.shaded.msv_core.verifier.Acceptor;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.AttributeFeeder;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.CombinedChildContentExpCreator;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/xmlschema/XSREDocDecl.class */
public class XSREDocDecl extends REDocumentDeclaration {
    protected final XMLSchemaGrammar grammar;
    protected final AttributeExp xsiAttExp;
    public static final String ERR_NON_NILLABLE_ELEMENT = "XMLSchemaVerifier.NonNillableElement";
    public static final String ERR_NOT_SUBSTITUTABLE_TYPE = "XMLSchemaVerifier.NotSubstitutableType";
    public static final String ERR_UNDEFINED_TYPE = "XMLSchemaVerifier.UndefinedType";

    public XSREDocDecl(XMLSchemaGrammar grammar) {
        super(grammar);
        this.xsiAttExp = new AttributeExp(new NamespaceNameClass("http://www.w3.org/2001/XMLSchema-instance"), Expression.anyString);
        this.grammar = grammar;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration, com.ctc.wstx.shaded.msv_core.verifier.DocumentDeclaration
    public Acceptor createAcceptor() {
        return new XSAcceptor(this, this.topLevel, null, Expression.epsilon);
    }

    CombinedChildContentExpCreator getCCCEC() {
        return this.cccec;
    }

    AttributeFeeder getAttFeeder() {
        return this.attFeeder;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.REDocumentDeclaration
    public String localizeMessage(String propertyName, Object[] args) {
        try {
            String format = ResourceBundle.getBundle("com.ctc.wstx.shaded.msv_core.verifier.regexp.xmlschema.Messages").getString(propertyName);
            return MessageFormat.format(format, args);
        } catch (Exception e) {
            return super.localizeMessage(propertyName, args);
        }
    }
}
