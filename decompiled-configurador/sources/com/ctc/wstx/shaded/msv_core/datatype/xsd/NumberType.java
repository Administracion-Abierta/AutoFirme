package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import java.math.BigDecimal;
import java.math.BigInteger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/NumberType.class */
public class NumberType extends BuiltinAtomicType implements Comparator {
    public static final NumberType theInstance = new NumberType();
    private static final BigInteger the10 = new BigInteger("10");
    private static final long serialVersionUID = 1;

    private NumberType() {
        super("decimal");
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    protected boolean checkFormat(String content, ValidationContext context) {
        int len = content.length();
        int i = 0;
        boolean atLeastOneDigit = false;
        if (len == 0) {
            return false;
        }
        char ch = content.charAt(0);
        if (ch == '-' || ch == '+') {
            i = 0 + 1;
        }
        while (i < len) {
            int i2 = i;
            i++;
            char ch2 = content.charAt(i2);
            if ('0' <= ch2 && ch2 <= '9') {
                atLeastOneDigit = true;
            } else {
                if (ch2 == '.') {
                    break;
                }
                return false;
            }
        }
        while (i < len) {
            int i3 = i;
            i++;
            char ch3 = content.charAt(i3);
            if ('0' <= ch3 && ch3 <= '9') {
                atLeastOneDigit = true;
            } else {
                return false;
            }
        }
        return atLeastOneDigit;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String content, ValidationContext context) {
        if (checkFormat(content, context)) {
            return load(content);
        }
        return null;
    }

    public static BigDecimal load(String content) {
        try {
            if (content.length() == 0) {
                return null;
            }
            if (content.charAt(0) == '+') {
                content = content.substring(1);
            }
            BigDecimal r = new BigDecimal(content);
            while (r.scale() > 0) {
                BigInteger[] q_r = r.unscaledValue().divideAndRemainder(the10);
                if (!q_r[1].equals(BigInteger.ZERO)) {
                    break;
                }
                r = new BigDecimal(q_r[0], r.scale() - 1);
            }
            return r;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Object o) {
        return ((BigDecimal) o).toString();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.DatabindableDatatype
    public Class getJavaObjectType() {
        return BigDecimal.class;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public String convertToLexicalValue(Object o, SerializationContext context) {
        if (o instanceof BigDecimal) {
            return o.toString();
        }
        throw new IllegalArgumentException();
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype
    public final int isFacetApplicable(String facetName) {
        if (facetName.equals(XSDatatype.FACET_TOTALDIGITS) || facetName.equals(XSDatatype.FACET_FRACTIONDIGITS) || facetName.equals(XSDatatype.FACET_PATTERN) || facetName.equals(XSDatatype.FACET_ENUMERATION) || facetName.equals(XSDatatype.FACET_WHITESPACE) || facetName.equals(XSDatatype.FACET_MAXINCLUSIVE) || facetName.equals(XSDatatype.FACET_MININCLUSIVE) || facetName.equals(XSDatatype.FACET_MAXEXCLUSIVE) || facetName.equals(XSDatatype.FACET_MINEXCLUSIVE)) {
            return 0;
        }
        return -2;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator
    public final int compare(Object o1, Object o2) {
        int r = ((Comparable) o1).compareTo(o2);
        if (r < 0) {
            return -1;
        }
        return r > 0 ? 1 : 0;
    }
}
