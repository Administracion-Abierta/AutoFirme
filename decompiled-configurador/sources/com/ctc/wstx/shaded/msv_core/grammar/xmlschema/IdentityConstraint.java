package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import java.io.Serializable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/IdentityConstraint.class */
public class IdentityConstraint implements Serializable {
    public final XPath[] selectors;
    public final String namespaceURI;
    public final String localName;
    public final Field[] fields;
    private static final long serialVersionUID = 1;

    public IdentityConstraint(String namespaceURI, String localName, XPath[] selectors, Field[] fields) {
        this.namespaceURI = namespaceURI;
        this.localName = localName;
        this.selectors = selectors;
        this.fields = fields;
    }
}
