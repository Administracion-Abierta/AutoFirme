package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/MaxInclusiveFacet.class */
public class MaxInclusiveFacet extends RangeFacet {
    private static final long serialVersionUID = 1;

    protected MaxInclusiveFacet(String nsUri, String typeName, XSDatatypeImpl baseType, Object limit, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_MAXINCLUSIVE, limit, _isFixed);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.RangeFacet
    protected final boolean rangeCheck(int r) {
        return r == 1 || r == 0;
    }
}
