package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceContainer;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.GroupDeclExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/GroupState.class */
public class GroupState extends RedefinableDeclState {
    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState, com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return reader.createModelGroupState(this, tag);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.RedefinableDeclState
    protected ReferenceContainer getContainer() {
        return ((XMLSchemaReader) this.reader).currentSchema.groupDecls;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (this.startTag.containsAttribute("ref")) {
            Expression exp = reader.resolveQNameRef(this.startTag, "ref", new XMLSchemaReader.RefResolver() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.GroupState.1
                @Override // com.ctc.wstx.shaded.msv_core.reader.xmlschema.XMLSchemaReader.RefResolver
                public ReferenceContainer get(XMLSchemaSchema g) {
                    return g.groupDecls;
                }
            });
            return exp == null ? Expression.epsilon : exp;
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression halfCastedExpression, Expression newChildExpression) {
        if (halfCastedExpression != null) {
            this.reader.reportError(GrammarReader.ERR_MORE_THAN_ONE_CHILD_EXPRESSION);
            return halfCastedExpression;
        }
        return newChildExpression;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression annealExpression(Expression contentType) {
        GroupDeclExp decl;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (!isGlobal()) {
            return contentType;
        }
        String name = this.startTag.getAttribute("name");
        if (name == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "group", "name");
            return Expression.nullSet;
        }
        if (contentType == null) {
            reader.reportError(GrammarReader.ERR_MISSING_CHILD_EXPRESSION);
            return Expression.nullSet;
        }
        if (isRedefine()) {
            decl = (GroupDeclExp) this.oldDecl;
        } else {
            decl = reader.currentSchema.groupDecls.getOrCreate(name);
            if (decl.exp != null) {
                reader.reportError(new Locator[]{this.location, reader.getDeclaredLocationOf(decl)}, XMLSchemaReader.ERR_DUPLICATE_GROUP_DEFINITION, new Object[]{name});
            }
        }
        reader.setDeclaredLocationOf(decl);
        decl.exp = contentType;
        return decl;
    }
}
