package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/ElementRefState.class */
public class ElementRefState extends ExpressionWithoutChildState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (!this.startTag.containsAttribute("ref")) {
            throw new Error();
        }
        Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.ElementRefState.1
            @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.RefResolver
            public ReferenceContainer get(XMLSchemaSchema g) {
                return g.elementDecls;
            }
        });
        return exp == null ? Expression.epsilon : exp;
    }
}
