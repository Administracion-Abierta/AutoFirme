package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/IntegerType.class */
public class IntegerType extends IntegerDerivedType {
    public static final IntegerType theInstance;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ String convertToLexicalValue(Object x0, SerializationContext x1) {
        return super.convertToLexicalValue(x0, x1);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.IntegerDerivedType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public /* bridge */ /* synthetic */ DataTypeWithFacet getFacetObject(String x0) {
        return super.getFacetObject(x0);
    }

    static {
        try {
            theInstance = new IntegerType("integer", new FractionDigitsFacet(null, null, NumberType.theInstance, 0, true));
        } catch (DatatypeException e) {
            throw new InternalError();
        }
    }

    protected IntegerType(String typeName, XSDatatypeImpl baseFacets) {
        super(typeName, baseFacets);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public XSDatatype getBaseType() {
        return NumberType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return IntegerValueType.create(lexicalValue);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createJavaObject(String literal, ValidationContext context) {
        IntegerValueType o = (IntegerValueType) _createValue(literal, context);
        if (o == null) {
            return null;
        }
        return new BigInteger(o.toString());
    }

    public static BigInteger load(String s) {
        IntegerValueType o = IntegerValueType.create(s);
        if (o == null) {
            return null;
        }
        return new BigInteger(o.toString());
    }

    public static String save(BigInteger v) {
        return v.toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return BigInteger.class;
    }
}
