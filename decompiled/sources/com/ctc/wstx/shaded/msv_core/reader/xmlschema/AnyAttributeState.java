package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/AnyAttributeState.class */
public class AnyAttributeState extends AnyState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyState
    protected Expression createExpression(String namespace, String process) {
        int mode;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        XMLSchemaSchema currentSchema = reader.currentSchema;
        if (process.equals("skip")) {
            mode = 0;
        } else if (process.equals("lax")) {
            mode = 1;
        } else if (process.equals("strict")) {
            mode = 2;
        } else {
            reader.reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "processContents", process);
            mode = 0;
        }
        ((AnyAttributeOwner) this.parentState).setAttributeWildcard(new AttributeWildcard(getNameClass(namespace, currentSchema), mode));
        return Expression.epsilon;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.AnyState
    protected NameClass getNameClassFrom(ReferenceExp exp) {
        return ((AttributeDeclExp) exp).self.nameClass;
    }
}
