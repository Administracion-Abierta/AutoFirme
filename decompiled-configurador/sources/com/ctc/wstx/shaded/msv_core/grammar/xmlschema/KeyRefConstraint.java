package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/KeyRefConstraint.class */
public class KeyRefConstraint extends IdentityConstraint {
    public KeyConstraint key;
    private static final long serialVersionUID = 1;

    public KeyRefConstraint(String namespaceURI, String localName, XPath[] selector, Field[] fields) {
        super(namespaceURI, localName, selector, fields);
    }
}
