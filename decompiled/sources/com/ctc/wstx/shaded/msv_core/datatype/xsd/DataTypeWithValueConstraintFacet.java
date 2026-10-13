package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DataTypeWithValueConstraintFacet.class */
abstract class DataTypeWithValueConstraintFacet extends DataTypeWithFacet {
    private static final long serialVersionUID = 2497055158497151572L;

    DataTypeWithValueConstraintFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, facetName, _isFixed);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean needValueCheck() {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String literal, ValidationContext context) {
        return _createValue(literal, context) != null;
    }
}
