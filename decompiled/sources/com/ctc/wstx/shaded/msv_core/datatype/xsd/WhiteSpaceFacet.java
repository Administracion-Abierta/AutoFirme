package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/WhiteSpaceFacet.class */
public class WhiteSpaceFacet extends DataTypeWithFacet {
    private static final long serialVersionUID = 1;

    WhiteSpaceFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, WhiteSpaceProcessor.get((String) facets.getFacet(XSDatatype.FACET_WHITESPACE)), facets.isFixed(XSDatatype.FACET_WHITESPACE));
    }

    WhiteSpaceFacet(String nsUri, String typeName, XSDatatypeImpl baseType, WhiteSpaceProcessor proc, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_WHITESPACE, _isFixed, proc);
        if (baseType.whiteSpace.tightness() > this.whiteSpace.tightness()) {
            XSDatatype d = baseType.getFacetObject(XSDatatype.FACET_WHITESPACE);
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_LOOSENED_FACET, XSDatatype.FACET_WHITESPACE, (d == null ? getConcreteType() : d).displayName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String content, ValidationContext context) {
        return this.baseType.checkFormat(content, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        return this.baseType._createValue(content, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) {
    }
}
