package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DataTypeWithLexicalConstraintFacet.class */
abstract class DataTypeWithLexicalConstraintFacet extends DataTypeWithFacet {
    private static final long serialVersionUID = 6093401348890059498L;

    protected abstract boolean checkLexicalConstraint(String str);

    DataTypeWithLexicalConstraintFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, facetName, _isFixed);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String literal, ValidationContext context) {
        if (this.baseType.checkFormat(literal, context)) {
            return checkLexicalConstraint(literal);
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || checkLexicalConstraint(literal)) {
            return o;
        }
        return null;
    }
}
