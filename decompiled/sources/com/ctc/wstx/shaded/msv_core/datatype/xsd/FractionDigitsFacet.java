package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/FractionDigitsFacet.class */
public class FractionDigitsFacet extends DataTypeWithLexicalConstraintFacet {
    public final int scale;
    private static final long serialVersionUID = 1;

    public FractionDigitsFacet(String nsUri, String typeName, XSDatatypeImpl baseType, int _scale, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_FRACTIONDIGITS, _isFixed);
        this.scale = _scale;
        DataTypeWithFacet o = baseType.getFacetObject(XSDatatype.FACET_FRACTIONDIGITS);
        if (o != null && ((FractionDigitsFacet) o).scale < this.scale) {
            throw new DatatypeException(localize(XSDatatypeImpl.ERR_LOOSENED_FACET, XSDatatype.FACET_FRACTIONDIGITS, o.displayName()));
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithLexicalConstraintFacet
    protected boolean checkLexicalConstraint(String content) {
        return countScale(content) <= this.scale;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        int cnt = countScale(content);
        if (cnt > this.scale) {
            throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_TOO_MUCH_SCALE, new Integer(cnt), new Integer(this.scale)));
        }
    }

    protected static final int countScale(String literal) {
        int len = literal.length();
        boolean skipMode = true;
        int count = 0;
        int trailingZero = 0;
        for (int i = 0; i < len; i++) {
            char ch = literal.charAt(i);
            if (skipMode) {
                if (ch == '.') {
                    skipMode = false;
                }
            } else {
                trailingZero = ch == '0' ? trailingZero + 1 : 0;
                if ('0' <= ch && ch <= '9') {
                    count++;
                }
            }
        }
        return count - trailingZero;
    }
}
