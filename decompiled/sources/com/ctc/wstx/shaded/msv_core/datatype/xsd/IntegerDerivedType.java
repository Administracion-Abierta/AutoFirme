package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/IntegerDerivedType.class */
abstract class IntegerDerivedType extends BuiltinAtomicType implements Comparator {
    private final XSDatatypeImpl baseFacets;
    private static final long serialVersionUID = -7353993842821534786L;

    protected IntegerDerivedType(String typeName, XSDatatypeImpl _baseFacets) {
        super(typeName);
        this.baseFacets = _baseFacets;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_TOTALDIGITS) || facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_MAXINCLUSIVE) || facetName.equals(XSDatatype.FACET_MININCLUSIVE) || facetName.equals(XSDatatype.FACET_MAXEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MINEXCLUSIVE)) {
            return 0;
        }
        if (facetName.equals(XSDatatype.FACET_FRACTIONDIGITS)) {
            return -1;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl, com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public DataTypeWithFacet getFacetObject(String facetName) {
        return this.baseFacets.getFacetObject(facetName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected final boolean checkFormat(String content, ValidationContext context) {
        return _createValue(content, context) != null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object value, SerializationContext context) {
        if ((value instanceof Number) || (value instanceof IntegerValueType)) {
            return value.toString();
        }
        throw new IllegalArgumentException("invalid value type:" + value.getClass().toString());
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public final int compare(Object o1, Object o2) {
        int r = ((Comparable) o1).compareTo(o2);
        if (r < 0) {
            return -1;
        }
        return r > 0 ? 1 : 0;
    }

    protected static String removeOptionalPlus(String s) {
        if (s.length() <= 1 || s.charAt(0) != '+') {
            return s;
        }
        String s2 = s.substring(1);
        char ch = s2.charAt(0);
        if (('0' > ch || ch > '9') && '.' != ch) {
            throw new NumberFormatException();
        }
        return s2;
    }

    protected static XSDatatypeImpl createRangeFacet(XSDatatypeImpl baseType, Number min, Number max) {
        XSDatatypeImpl r = baseType;
        if (min != null) {
            try {
                r = new MinInclusiveFacet(null, null, r, min, false);
            } catch (DatatypeException e) {
                throw new InternalError();
            }
        }
        if (max != null) {
            r = new MaxInclusiveFacet(null, null, r, max, false);
        }
        return r;
    }
}
