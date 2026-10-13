package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.SimpleTypeExp;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import com.ctc.wstx.shaded.msv_core.reader.SimpleState;
import com.ctc.wstx.shaded.msv_core.reader.State;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSDatatypeExp;
import com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;
import org.ini4j.Config;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/GlobalDeclState.class */
public class GlobalDeclState extends SimpleState implements ExpressionOwner, XSTypeOwner {
    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        if (tag.localName.equals(Config.PROP_INCLUDE)) {
            return reader.sfactory.include(this, tag);
        }
        if (tag.localName.equals("import")) {
            return reader.sfactory.import_(this, tag);
        }
        if (tag.localName.equals("redefine")) {
            return reader.sfactory.redefine(this, tag);
        }
        if (tag.localName.equals("simpleType")) {
            return reader.sfactory.simpleType(this, tag);
        }
        if (tag.localName.equals("complexType")) {
            return reader.sfactory.complexTypeDecl(this, tag);
        }
        if (tag.localName.equals("group")) {
            return reader.sfactory.group(this, tag);
        }
        if (tag.localName.equals("attributeGroup")) {
            return reader.sfactory.attributeGroup(this, tag);
        }
        if (tag.localName.equals("element")) {
            return reader.sfactory.elementDecl(this, tag);
        }
        if (tag.localName.equals("attribute")) {
            return reader.sfactory.attribute(this, tag);
        }
        if (tag.localName.equals("notation")) {
            return reader.sfactory.notation(this, tag);
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public String getTargetNamespaceUri() {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        return reader.currentSchema.targetNamespace;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public void onEndChild(Expression exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.datatype.xsd.XSTypeOwner
    public void onEndChild(XSDatatypeExp type) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String typeName = reader.getCurrentState().getStartTag().getAttribute("name");
        if (typeName == null) {
            reader.reportError(GrammarReader.ERR_MISSING_ATTRIBUTE, "simpleType", "name");
            return;
        }
        SimpleTypeExp exp = reader.currentSchema.simpleTypes.getOrCreate(typeName);
        if (exp.getType() != null) {
            reader.reportError(GrammarReader.ERR_DATATYPE_ALREADY_DEFINED, typeName);
        } else {
            exp.set(type);
            reader.setDeclaredLocationOf(exp);
        }
    }
}
