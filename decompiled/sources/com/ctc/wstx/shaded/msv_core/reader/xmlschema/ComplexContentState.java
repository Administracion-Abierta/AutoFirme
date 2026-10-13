package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/ComplexContentState.class */
public class ComplexContentState extends ExpressionWithChildState {
    protected ComplexTypeExp parentDecl;

    protected ComplexContentState(ComplexTypeExp decl) {
        this.parentDecl = decl;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (this.exp != null) {
            return null;
        }
        if (tag.localName.equals("restriction")) {
            return reader.sfactory.complexRst(this, tag, this.parentDecl);
        }
        return tag.localName.equals("extension") ? reader.sfactory.complexExt(this, tag, this.parentDecl) : super.createChildState(tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            throw new Error();
        }
        return newChildExpression;
    }
}
