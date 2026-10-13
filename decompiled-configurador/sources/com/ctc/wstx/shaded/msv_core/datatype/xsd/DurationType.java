package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.BigTimeDurationValueType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.datetime.ITimeDurationValueType;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/DurationType.class */
public final class DurationType extends BuiltinAtomicType implements Comparator {
    public static final DurationType theInstance = new DurationType();
    private static final long serialVersionUID = 1;

    private DurationType() {
        super("duration");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String content, ValidationContext context) {
        try {
            new BigTimeDurationValueType(content);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        try {
            return new BigTimeDurationValueType(content);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return ITimeDurationValueType.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public int compare(Object lhs, Object rhs) {
        return ((ITimeDurationValueType) lhs).compare((ITimeDurationValueType) rhs);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_MAXINCLUSIVE) || facetName.equals(XSDatatype.FACET_MAXEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MININCLUSIVE) || facetName.equals(XSDatatype.FACET_MINEXCLUSIVE)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if (!(value instanceof ITimeDurationValueType)) {
            throw new IllegalArgumentException();
        }
        return value.toString();
    }
}
