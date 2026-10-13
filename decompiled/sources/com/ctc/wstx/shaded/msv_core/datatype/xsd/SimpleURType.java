package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/SimpleURType.class */
public class SimpleURType extends BuiltinAtomicType {
    public static final SimpleURType theInstance = new SimpleURType();
    private static final long serialVersionUID = 1;

    protected SimpleURType() {
        super("anySimpleType", WhiteSpaceProcessor.thePreserve);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String content, ValidationContext context) {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return lexicalValue;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return String.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalArgumentException();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        return 0;
    }
}
