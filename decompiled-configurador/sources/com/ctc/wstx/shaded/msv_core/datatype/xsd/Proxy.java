package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/Proxy.class */
public class Proxy extends XSDatatypeImpl {
    public final XSDatatypeImpl baseType;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return this.baseType;
    }

    public Proxy(String nsUri, String newTypeName, XSDatatypeImpl baseType) {
        super(nsUri, newTypeName, baseType.whiteSpace);
        this.baseType = baseType;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public boolean isContextDependent() {
        return this.baseType.isContextDependent();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public int getIdType() {
        return this.baseType.getIdType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public boolean isFinal(int derivationType) {
        return this.baseType.isFinal(derivationType);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public ConcreteType getConcreteType() {
        return this.baseType.getConcreteType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String displayName() {
        return this.baseType.displayName();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public int getVariety() {
        return this.baseType.getVariety();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public int isFacetApplicable(String facetName) {
        return this.baseType.isFacetApplicable(facetName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public boolean checkFormat(String content, ValidationContext context) {
        return this.baseType.checkFormat(content, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        return this.baseType._createValue(content, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public DataTypeWithFacet getFacetObject(String facetName) {
        return this.baseType.getFacetObject(facetName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return this.baseType.getJavaObjectType();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createJavaObject(String literal, ValidationContext context) {
        return this.baseType._createJavaObject(literal, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public String serializeJavaObject(Object value, SerializationContext context) {
        return this.baseType.serializeJavaObject(value, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        return this.baseType.convertToLexicalValue(value, context);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public void _checkValid(String content, ValidationContext context) throws DatatypeException {
        this.baseType._checkValid(content, context);
    }
}
