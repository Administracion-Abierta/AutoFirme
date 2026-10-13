package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/XMLSchemaTypeExp.class */
public abstract class XMLSchemaTypeExp extends RedefinableExp {
    public static final int RESTRICTION = 1;
    public static final int EXTENSION = 2;
    private static final long serialVersionUID = 1;

    public abstract int getBlock();

    XMLSchemaTypeExp(String typeLocalName) {
        super(typeLocalName);
    }
}
