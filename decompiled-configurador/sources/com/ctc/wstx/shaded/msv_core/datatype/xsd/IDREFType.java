package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/IDREFType.class */
public class IDREFType extends NcnameType {
    public static final IDREFType theInstance = new IDREFType();
    private static final long serialVersionUID = 1;

    protected IDREFType() {
        super(DTDParser.TYPE_IDREF);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public int getIdType() {
        return 2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType
    protected Object readResolve() {
        return theInstance;
    }
}
