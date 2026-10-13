package com.ctc.wstx.shaded.msv_core.datatype.xsd;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/FinalComponent.class */
public final class FinalComponent extends Proxy {
    private final int finalValue;
    private static final long serialVersionUID = 1;

    public FinalComponent(XSDatatypeImpl baseType, int finalValue) {
        this(baseType.getNamespaceUri(), baseType.getName(), baseType, finalValue);
    }

    public FinalComponent(String nsUri, String newTypeName, XSDatatypeImpl baseType, int finalValue) {
        super(nsUri, newTypeName, baseType);
        this.finalValue = finalValue;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Proxy, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public boolean isFinal(int derivationType) {
        if ((this.finalValue & derivationType) != 0) {
            return true;
        }
        return super.isFinal(derivationType);
    }
}
