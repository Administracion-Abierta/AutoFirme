package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/LengthFacet.class */
public class LengthFacet extends DataTypeWithValueConstraintFacet {
    public final int length;
    private static final long serialVersionUID = 1;

    protected LengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, TypeIncubator facets) throws DatatypeException {
        this(nsUri, typeName, baseType, facets.getNonNegativeInteger(XSDatatype.FACET_LENGTH), facets.isFixed(XSDatatype.FACET_LENGTH));
    }

    protected LengthFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _length, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_LENGTH, _isFixed);
        this.length = _length;
        DataTypeWithFacet o = baseType.getFacetObject(XSDatatype.FACET_LENGTH);
        if (o != null && ((LengthFacet) o).length != this.length) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_LOOSENED_FACET, XSDatatype.FACET_LENGTH, o.displayName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        Object o = this.baseType._createValue(content, context);
        if (o == null || ((Discrete) this.concreteType).countLength(o) != this.length) {
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
        if (cnt != this.length) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_LENGTH, new Integer(cnt), new Integer(this.length)));
        }
    }
}
