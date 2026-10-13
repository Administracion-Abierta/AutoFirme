package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/EntityType.class */
public class EntityType extends BuiltinAtomicType {
    public static final EntityType theInstance = new EntityType();
    private static final long serialVersionUID = 1;

    private EntityType() {
        super(DTDParser.TYPE_ENTITY);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return NcnameType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype
    public boolean isContextDependent() {
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_LENGTH) || facetName.equals(XSDatatype.FACET_MINLENGTH) || facetName.equals(XSDatatype.FACET_MAXLENGTH) || facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_ENUMERATION)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String content, ValidationContext context) {
        return context.isUnparsedEntity(content);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        if (context.isUnparsedEntity(content)) {
            return content;
        }
        return null;
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
}
