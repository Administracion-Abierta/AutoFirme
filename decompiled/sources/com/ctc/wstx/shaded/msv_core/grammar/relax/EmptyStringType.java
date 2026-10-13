package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/EmptyStringType.class */
public class EmptyStringType extends BuiltinAtomicType {
    public static final EmptyStringType theInstance = new EmptyStringType();
    private static final long serialVersionUID = 1;

    private EmptyStringType() {
        super("emptyString");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public int isFacetApplicable(String facetName) {
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public boolean checkFormat(String literal, ValidationContext context) {
        return literal.equals("");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String lexicalValue, ValidationContext context) {
        if (lexicalValue.equals("")) {
            return lexicalValue;
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object o, SerializationContext context) {
        if (o.equals("")) {
            return "";
        }
        throw new IllegalArgumentException();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return String.class;
    }
}
