package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.sun.jna.platform.win32.WinNT;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/IntType.class */
public class IntType extends IntegerDerivedType {
    public static final IntType theInstance = new IntType("int", createRangeFacet(LongType.theInstance, new Integer(Integer.MIN_VALUE), new Integer(WinNT.MAXLONG)));
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ String convertToLexicalValue(Object x0, SerializationContext x1) {
        return super.convertToLexicalValue(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ DataTypeWithFacet getFacetObject(String x0) {
        return super.getFacetObject(x0);
    }

    protected IntType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return LongType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return load(lexicalValue);
    }

    public static Integer load(String s) {
        try {
            return new Integer(removeOptionalPlus(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Integer v) {
        return v.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Integer.class;
    }
}
