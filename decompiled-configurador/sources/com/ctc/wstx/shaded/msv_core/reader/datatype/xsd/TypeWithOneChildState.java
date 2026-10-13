package com.ctc.wstx.shaded.msv_core.reader.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/datatype/xsd/TypeWithOneChildState.class */
abstract class TypeWithOneChildState extends TypeState implements XSTypeOwner {
    protected XSDatatypeExp type;

    TypeWithOneChildState() {
    }

    public void onEndChild(XSDatatypeExp child) {
        if (this.type != null) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_TYPE);
        } else {
            this.type = child;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.TypeState
    protected final XSDatatypeExp makeType() throws DatatypeException {
        if (this.type == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_CHILD_TYPE);
            return new XSDatatypeExp(StringType.theInstance, this.reader.pool);
        }
        return annealType(this.type);
    }

    protected XSDatatypeExp annealType(XSDatatypeExp dt) throws DatatypeException {
        return dt;
    }
}
