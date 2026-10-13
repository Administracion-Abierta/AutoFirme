package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/MaxLengthFacet.class */
public class MaxLengthFacet extends DataTypeWithValueConstraintFacet {
    public final int maxLength;
    private static final long serialVersionUID = 1;

    protected MaxLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, facets.getNonNegativeInteger(XSDatatype.FACET_MAXLENGTH), facets.isFixed(XSDatatype.FACET_MAXLENGTH));
    }

    protected MaxLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _maxLength, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_MAXLENGTH, _isFixed);
        this.maxLength = _maxLength;
        DataTypeWithFacet o = baseType.getFacetObject(XSDatatype.FACET_MAXLENGTH);
        if (o != null && ((MaxLengthFacet) o).maxLength < this.maxLength) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_LOOSENED_FACET, XSDatatype.FACET_MAXLENGTH, o.displayName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || ((Discrete) this.concreteType).countLength(o) > this.maxLength) {
            return null;
        }
        return o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        Object o = this.concreteType._createValue(content, context);
        if (o == null) {
            throw new IllegalStateException();
        }
        int cnt = ((Discrete) this.concreteType).countLength(o);
        if (cnt > this.maxLength) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_MAXLENGTH, new Integer(cnt), new Integer(this.maxLength)));
        }
    }
}
