package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/ValueState.class */
public class ValueState extends ExpressionWithoutChildState {
    protected final StringBuffer text = new StringBuffer();

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void characters(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        Datatype type;
        StringPair typeFullName;
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        String typeName = this.startTag.getCollapsedAttribute("type");
        if (typeName == null) {
            try {
                type = reader.resolveDataTypeLibrary("").createDatatype("token");
                typeFullName = new StringPair("", "token");
            } catch (DatatypeException e) {
                e.printStackTrace();
                throw new InternalError();
            }
        } else {
            type = reader.resolveDataType(typeName);
            typeFullName = new StringPair(reader.datatypeLibURI, typeName);
        }
        Object value = type.createValue(this.text.toString(), reader);
        if (value == null) {
            reader.reportError(RELAXNGReader.ERR_BAD_DATA_VALUE, typeName, this.text.toString().trim());
            return Expression.nullSet;
        }
        return reader.pool.createValue(type, typeFullName, value);
    }
}
