package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/ShortType.class */
public class ShortType extends IntegerDerivedType {
    public static final ShortType theInstance = new ShortType("short", createRangeFacet(IntType.theInstance, new Short(Short.MIN_VALUE), new Short(Short.MAX_VALUE)));
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ String convertToLexicalValue(Object x0, SerializationContext x1) {
        return super.convertToLexicalValue(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ DataTypeWithFacet getFacetObject(String x0) {
        return super.getFacetObject(x0);
    }

    protected ShortType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return IntType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return load(lexicalValue);
    }

    public static Short load(String s) {
        try {
            return new Short(removeOptionalPlus(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Short v) {
        return v.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Short.class;
    }
}
