package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.reader.ChildlessState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/FacetState.class */
public class FacetState extends ChildlessState {
    public static final Set facetNames = initFacetNames();

    private static Set initFacetNames() {
        Set s = new HashSet();
        s.add(XSDatatype.FACET_LENGTH);
        s.add(XSDatatype.FACET_MINLENGTH);
        s.add(XSDatatype.FACET_MAXLENGTH);
        s.add(XSDatatype.FACET_PATTERN);
        s.add(XSDatatype.FACET_ENUMERATION);
        s.add(XSDatatype.FACET_MAXINCLUSIVE);
        s.add(XSDatatype.FACET_MININCLUSIVE);
        s.add(XSDatatype.FACET_MAXEXCLUSIVE);
        s.add(XSDatatype.FACET_MINEXCLUSIVE);
        s.add(XSDatatype.FACET_WHITESPACE);
        s.add(XSDatatype.FACET_FRACTIONDIGITS);
        s.add(XSDatatype.FACET_TOTALDIGITS);
        return s;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        String value = this.startTag.getAttribute("value");
        if (value == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, this.startTag.localName, "value");
            return;
        }
        try {
            ((FacetStateParent) this.parentState).getIncubator().addFacet(this.startTag.localName, value, "true".equals(this.startTag.getAttribute("fixed")), this.reader);
        } catch (DatatypeException e) {
            this.reader.reportError(e, GrammarReader.ERR_BAD_TYPE, e.getMessage());
        }
    }
}
