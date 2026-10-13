package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/BinaryBaseType.class */
abstract class BinaryBaseType extends BuiltinAtomicType implements Discrete {
    private static final long serialVersionUID = -6355125980881791215L;

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public abstract String serializeJavaObject(Object obj, SerializationContext serializationContext);

    BinaryBaseType(String typeName) {
        super(typeName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_LENGTH) || facetName.equals(XSDatatype.FACET_MAXLENGTH) || facetName.equals(XSDatatype.FACET_MINLENGTH) || facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_ENUMERATION)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Discrete
    public final int countLength(Object value) {
        return ((BinaryValueType) value).rawData.length;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createJavaObject(String literal, ValidationContext context) {
        BinaryValueType v = (BinaryValueType) createValue(literal, context);
        if (v == null) {
            return null;
        }
        return v.rawData;
    }

    public Class getJavaObjectType() {
        return byte[].class;
    }
}
