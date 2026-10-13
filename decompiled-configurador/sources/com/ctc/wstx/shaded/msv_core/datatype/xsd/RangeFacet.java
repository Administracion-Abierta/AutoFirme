package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/RangeFacet.class */
public abstract class RangeFacet extends DataTypeWithValueConstraintFacet {
    public final Object limitValue;
    private static final long serialVersionUID = 1;

    protected abstract boolean rangeCheck(int i);

    protected RangeFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, Object limit, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, facetName, _isFixed);
        this.limitValue = limit;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null) {
            return null;
        }
        int r = ((Comparator) this.concreteType).compare(this.limitValue, o);
        if (rangeCheck(r)) {
            return o;
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if (_createValue(content, context) == null) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_OUT_OF_RANGE, this.facetName, this.limitValue));
        }
    }
}
