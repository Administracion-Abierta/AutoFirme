package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/TotalDigitsFacet.class */
public class TotalDigitsFacet extends DataTypeWithLexicalConstraintFacet {
    public final int precision;
    private static final long serialVersionUID = 1;

    public TotalDigitsFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _precision, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_TOTALDIGITS, _isFixed);
        this.precision = _precision;
        DataTypeWithFacet o = baseType.getFacetObject(XSDatatype.FACET_TOTALDIGITS);
        if (o != null && ((TotalDigitsFacet) o).precision < this.precision) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_LOOSENED_FACET, XSDatatype.FACET_TOTALDIGITS, o.displayName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithLexicalConstraintFacet
    protected boolean checkLexicalConstraint(String content) {
        return countPrecision(content) <= this.precision;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        int cnt = countPrecision(content);
        if (cnt > this.precision) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_TOO_MUCH_PRECISION, new Integer(cnt), new Integer(this.precision)));
        }
    }

    protected static int countPrecision(String literal) {
        int len = literal.length();
        boolean skipMode = true;
        boolean seenDot = false;
        int count = 0;
        int trailingZero = 0;
        for (int i = 0; i < len; i++) {
            char ch = literal.charAt(i);
            if (ch == '.') {
                skipMode = false;
                seenDot = true;
            }
            if (skipMode) {
                if ('1' <= ch && ch <= '9') {
                    count++;
                    skipMode = false;
                }
            } else {
                trailingZero = (seenDot && ch == '0') ? trailingZero + 1 : 0;
                if ('0' <= ch && ch <= '9') {
                    count++;
                }
            }
        }
        return count - trailingZero;
    }
}
