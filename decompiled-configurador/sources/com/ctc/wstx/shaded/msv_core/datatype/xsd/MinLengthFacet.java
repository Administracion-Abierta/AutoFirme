package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/MinLengthFacet.class */
public class MinLengthFacet extends DataTypeWithValueConstraintFacet {
    public final int minLength;
    private static final long serialVersionUID = 1;

    protected MinLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, facets.getNonNegativeInteger(XSDatatype.FACET_MINLENGTH), facets.isFixed(XSDatatype.FACET_MINLENGTH));
    }

    protected MinLengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _minLength, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_MINLENGTH, _isFixed);
        this.minLength = _minLength;
        DataTypeWithFacet o = baseType.getFacetObject(XSDatatype.FACET_MINLENGTH);
        if (o != null && ((MinLengthFacet) o).minLength > this.minLength) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_LOOSENED_FACET, XSDatatype.FACET_MINLENGTH, o.displayName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || ((Discrete) this.concreteType).countLength(o) < this.minLength) {
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
        if (cnt < this.minLength) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_MINLENGTH, new Integer(cnt), new Integer(this.minLength)));
        }
    }
}
