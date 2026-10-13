package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/LongType.class */
public class LongType extends IntegerDerivedType {
    public static final LongType theInstance = new LongType();
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ String convertToLexicalValue(Object x0, SerializationContext x1) {
        return super.convertToLexicalValue(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ DataTypeWithFacet getFacetObject(String x0) {
        return super.getFacetObject(x0);
    }

    private LongType() {
        super("long", createRangeFacet(IntegerType.theInstance, new Long(Long.MIN_VALUE), new Long(Long.MAX_VALUE)));
    }

    protected LongType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return IntegerType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return load(lexicalValue);
    }

    public static Long load(String s) {
        try {
            return new Long(removeOptionalPlus(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Long v) {
        return v.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Long.class;
    }
}
