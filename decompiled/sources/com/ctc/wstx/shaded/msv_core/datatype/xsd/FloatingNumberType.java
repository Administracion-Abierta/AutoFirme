package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/FloatingNumberType.class */
abstract class FloatingNumberType extends BuiltinAtomicType implements Comparator {
    private static final long serialVersionUID = -224134863141700384L;

    protected FloatingNumberType(String typeName) {
        super(typeName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String lexicalValue, ValidationContext context) {
        return _createValue(lexicalValue, context) != null;
    }

    protected static boolean isDigitOrPeriodOrSign(char ch) {
        return ('0' <= ch && ch <= '9') || ch == '+' || ch == '-' || ch == '.';
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public final int compare(Object lhs, Object rhs) {
        int r = ((Comparable) lhs).compareTo(rhs);
        if (r < 0) {
            return -1;
        }
        return r > 0 ? 1 : 0;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_MAXINCLUSIVE) || facetName.equals(XSDatatype.FACET_MININCLUSIVE) || facetName.equals(XSDatatype.FACET_MAXEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MINEXCLUSIVE)) {
            return 0;
        }
        return -2;
    }
}
