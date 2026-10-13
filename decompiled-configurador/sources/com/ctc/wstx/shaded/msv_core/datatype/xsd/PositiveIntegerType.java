package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import es.gob.afirma.signers.tsp.pkcs7.TsaParams;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/PositiveIntegerType.class */
public class PositiveIntegerType extends IntegerType {
    public static final PositiveIntegerType theInstance = new PositiveIntegerType();
    private static final long serialVersionUID = 1;

    private PositiveIntegerType() {
        super("positiveInteger", createRangeFacet(NonNegativeIntegerType.theInstance, IntegerValueType.create(TsaParams.TS_SIGN), null));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return NonNegativeIntegerType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        Object o = super._createValue(lexicalValue, context);
        if (o == null) {
            return null;
        }
        IntegerValueType v = (IntegerValueType) o;
        if (v.isPositive()) {
            return v;
        }
        return null;
    }
}
