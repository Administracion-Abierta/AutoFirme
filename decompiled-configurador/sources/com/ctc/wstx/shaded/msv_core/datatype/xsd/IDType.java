package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/IDType.class */
public class IDType extends NcnameType {
    public static final IDType theInstance = new IDType();
    private static final long serialVersionUID = 1;

    protected IDType() {
        super(DTDParser.TYPE_ID);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType
    protected Object readResolve() {
        return theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public int getIdType() {
        return 1;
    }
}
