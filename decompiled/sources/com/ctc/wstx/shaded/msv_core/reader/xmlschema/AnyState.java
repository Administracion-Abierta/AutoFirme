package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.ChoiceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.DifferenceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NamespaceNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.NotNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.LaxDefaultNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.XMLSchemaSchema;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.reader.GrammarReader;
import java.util.Iterator;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/AnyState.class */
public abstract class AnyState extends ExpressionWithoutChildState {
    protected abstract Expression createExpression(String str, String str2);

    protected abstract NameClass getNameClassFrom(ReferenceExp referenceExp);

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected final Expression makeExpression() {
        return createExpression(this.startTag.getDefaultedAttribute("namespace", "##any"), this.startTag.getDefaultedAttribute("processContents", "strict"));
    }

    protected NameClass getNameClass(String namespace, XMLSchemaSchema currentSchema) {
        NameClass nc;
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        String namespace2 = namespace.trim();
        if (namespace2.equals("##any")) {
            return NameClass.ALL;
        }
        if (namespace2.equals("##other")) {
            return new NotNameClass(new ChoiceNameClass(new NamespaceNameClass(currentSchema.targetNamespace), new NamespaceNameClass("")));
        }
        NameClass choices = null;
        StringTokenizer tokens = new StringTokenizer(namespace2);
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.equals("##targetNamespace")) {
                nc = new NamespaceNameClass(currentSchema.targetNamespace);
            } else if (token.equals("##local")) {
                nc = new NamespaceNameClass("");
            } else {
                nc = new NamespaceNameClass(token);
            }
            choices = choices == null ? nc : new ChoiceNameClass(choices, nc);
        }
        if (choices == null) {
            reader.reportError(GrammarReader.ERR_BAD_ATTRIBUTE_VALUE, "namespace", namespace2);
            return NameClass.ALL;
        }
        return choices;
    }

    protected NameClass createLaxNameClass(NameClass allowedNc, XMLSchemaReader.RefResolver res) {
        XMLSchemaReader reader = (XMLSchemaReader) this.reader;
        LaxDefaultNameClass laxNc = new LaxDefaultNameClass(allowedNc);
        Iterator itr = reader.grammar.iterateSchemas();
        while (itr.hasNext()) {
            XMLSchemaSchema schema = (XMLSchemaSchema) itr.next();
            if (allowedNc.accepts(schema.targetNamespace, "*")) {
                ReferenceExp[] refs = res.get(schema).getAll();
                for (ReferenceExp referenceExp : refs) {
                    NameClass name = getNameClassFrom(referenceExp);
                    if (!(name instanceof SimpleNameClass)) {
                        throw new Error();
                    }
                    SimpleNameClass snc = (SimpleNameClass) name;
                    laxNc.addName(snc.namespaceURI, snc.localName);
                }
            }
        }
        return new DifferenceNameClass(laxNc, new NotNameClass(allowedNc));
    }
}
