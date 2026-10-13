package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/MaxExclusiveFacet.class */
public class MaxExclusiveFacet extends RangeFacet {
    private static final long serialVersionUID = 1;

    protected MaxExclusiveFacet(String nsUri, String typeName, XSDatatypeImpl baseType, Object limit, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_MAXEXCLUSIVE, limit, _isFixed);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.RangeFacet
    protected final boolean rangeCheck(int r) {
        return r == 1;
    }
}
