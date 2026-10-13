package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/EnumerationFacet.class */
public class EnumerationFacet extends DataTypeWithValueConstraintFacet {
    public final Set values;
    private static final long serialVersionUID = 1;

    protected EnumerationFacet(String nsUri, String typeName, XSDatatypeImpl baseType, Collection _values, boolean _isFixed) throws DatatypeException {
        super(nsUri, typeName, baseType, XSDatatype.FACET_ENUMERATION, _isFixed);
        this.values = new HashSet(_values);
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl
    public Object _createValue(String literal, ValidationContext context) {
        Object o = this.baseType._createValue(literal, context);
        if (o == null || !this.values.contains(o)) {
            return null;
        }
        return o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet
    protected void diagnoseByFacet(String content, ValidationContext context) throws DatatypeException {
        if (_createValue(content, context) != null) {
            return;
        }
        if (this.values.size() <= 4) {
            Object[] members = this.values.toArray();
            if ((members[0] instanceof String) || (members[0] instanceof Number)) {
                String r = "" + OperatorName.SHOW_TEXT_LINE_AND_SPACE + members[0].toString() + OperatorName.SHOW_TEXT_LINE_AND_SPACE;
                for (int i = 1; i < members.length; i++) {
                    r = r + "/\"" + members[i].toString() + OperatorName.SHOW_TEXT_LINE_AND_SPACE;
                }
                throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_ENUMERATION_WITH_ARG, "(" + r + ")"));
            }
        }
        throw new DatatypeException(-1, localize(XSDatatypeImpl.ERR_ENUMERATION));
    }
}
