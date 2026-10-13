package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/BooleanType.class */
public class BooleanType extends BuiltinAtomicType {
    public static final BooleanType theInstance = new BooleanType();
    private static final long serialVersionUID = 1;

    private BooleanType() {
        super("boolean");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String content, ValidationContext context) {
        return "true".equals(content) || "false".equals(content) || "0".equals(content) || "1".equals(content);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        return load(lexicalValue);
    }

    public static Boolean load(String s) {
        if (!s.equals("true") && !s.equals("1")) {
            if (s.equals("0") || s.equals("false")) {
                return Boolean.FALSE;
            }
            return null;
        }
        return Boolean.TRUE;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (value instanceof Boolean) {
            return save((Boolean) value);
        }
        throw new IllegalArgumentException();
    }

    public static String save(Boolean b) {
        return b.booleanValue() ? "true" : "false";
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_WHITESPACE)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return Boolean.class;
    }
}
