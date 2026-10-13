package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/ByteType.class */
public class ByteType extends IntegerDerivedType {
    public static final ByteType theInstance = new ByteType();
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ String convertToLexicalValue(Object x0, SerializationContext x1) {
        return super.convertToLexicalValue(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ DataTypeWithFacet getFacetObject(String x0) {
        return super.getFacetObject(x0);
    }

    private ByteType() {
        super("byte", createRangeFacet(ShortType.theInstance, new Byte((byte) -128), new Byte((byte) 127)));
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return ShortType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        return load(content);
    }

    public static Byte load(String s) {
        try {
            return new Byte(removeOptionalPlus(s));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Byte v) {
        return v.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Byte.class;
    }
}
