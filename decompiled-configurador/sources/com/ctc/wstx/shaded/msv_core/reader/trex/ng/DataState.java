package com.ctc.wstx.shaded.msv_core.reader.trex.ng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeBuilder;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.ErrorDatatypeLibrary;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/DataState.class */
public class DataState extends ExpressionState implements ExpressionOwner {
    protected DatatypeBuilder typeBuilder;
    protected StringPair baseTypeName;
    protected Expression except = null;

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        if (tag.localName.equals("except")) {
            return reader.getStateFactory().dataExcept(this, tag);
        }
        if (tag.localName.equals("param")) {
            return reader.getStateFactory().dataParam(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        super.startSelf();
        String localName = this.startTag.getCollapsedAttribute("type");
        if (localName == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "data", "type");
        } else {
            this.baseTypeName = new StringPair(reader.datatypeLibURI, localName);
            try {
                this.typeBuilder = reader.getCurrentDatatypeLibrary().createDatatypeBuilder(localName);
            } catch (DatatypeException dte) {
                reader.reportError(RELAXNGReader.ERR_UNDEFINED_DATATYPE_1, localName, dte.getMessage());
            }
        }
        if (this.typeBuilder == null) {
            this.typeBuilder = ErrorDatatypeLibrary.theInstance;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression child) {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        if (this.except != null) {
            reader.reportError(RELAXNGReader.ERR_MULTIPLE_EXCEPT);
        }
        this.except = child;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        RELAXNGReader reader = (RELAXNGReader) this.reader;
        try {
            if (this.except == null) {
                this.except = Expression.nullSet;
            }
            return reader.pool.createData(this.typeBuilder.createDatatype(), this.baseTypeName, this.except);
        } catch (DatatypeException dte) {
            reader.reportError(RELAXNGReader.ERR_INVALID_PARAMETERS, dte.getMessage());
            return Expression.nullSet;
        }
    }
}
