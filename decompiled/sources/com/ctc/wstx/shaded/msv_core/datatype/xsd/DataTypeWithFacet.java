package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DataTypeWithFacet.class */
public abstract class DataTypeWithFacet extends XSDatatypeImpl {
    public final XSDatatypeImpl baseType;
    protected final ConcreteType concreteType;
    public final String facetName;
    public final boolean isFacetFixed;
    private final boolean needValueCheckFlag;
    private static final long serialVersionUID = 1;

    protected abstract void diagnoseByFacet(String str, ValidationContext validationContext) throws DatatypeException;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return this.baseType;
    }

    DataTypeWithFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed) throws DatatypeException {
        this(nsUri, typeName, baseType, facetName, _isFixed, baseType.whiteSpace);
    }

    DataTypeWithFacet(String nsUri, String typeName, XSDatatypeImpl baseType, String facetName, boolean _isFixed, WhiteSpaceProcessor whiteSpace) throws DatatypeException {
        super(nsUri, typeName, whiteSpace);
        this.baseType = baseType;
        this.facetName = facetName;
        this.isFacetFixed = _isFixed;
        this.concreteType = baseType.getConcreteType();
        this.needValueCheckFlag = baseType.needValueCheck();
        int r = baseType.isFacetApplicable(facetName);
        switch (r) {
            case -2:
                throw new DatatypeException(localize(XSDatatypeImpl.ERR_NOT_APPLICABLE_FACET, facetName));
            case -1:
                throw new DatatypeException(localize(XSDatatypeImpl.ERR_OVERRIDING_FIXED_FACET, facetName));
            case 0:
                return;
            default:
                return;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public boolean isContextDependent() {
        return this.concreteType.isContextDependent();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public int getIdType() {
        return this.concreteType.getIdType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final String displayName() {
        return getName() != null ? getName() : this.concreteType.getName() + "-derived";
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (this.facetName.equals(facetName)) {
            return this.isFacetFixed ? -1 : 0;
        }
        return this.baseType.isFacetApplicable(facetName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean needValueCheck() {
        return this.needValueCheckFlag;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final DataTypeWithFacet getFacetObject(String facetName) {
        if (this.facetName.equals(facetName)) {
            return this;
        }
        return this.baseType.getFacetObject(facetName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final ConcreteType getConcreteType() {
        return this.concreteType;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int getVariety() {
        return this.concreteType.getVariety();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final boolean isFinal(int derivationType) {
        return this.baseType.isFinal(derivationType);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final String convertToLexicalValue(Object o, SerializationContext context) {
        return this.concreteType.convertToLexicalValue(o, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public final Class getJavaObjectType() {
        return this.concreteType.getJavaObjectType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public final Object _createJavaObject(String literal, ValidationContext context) {
        if (isValid(literal, context)) {
            return this.baseType.createJavaObject(literal, context);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public String serializeJavaObject(Object value, SerializationContext context) {
        return this.baseType.serializeJavaObject(value, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final void _checkValid(String content, ValidationContext context) throws DatatypeException {
        this.baseType._checkValid(content, context);
        diagnoseByFacet(content, context);
    }
}
