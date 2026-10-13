package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/AttributeWildcard.class */
public class AttributeWildcard {
    private NameClass name;
    private int processMode;
    public static final int SKIP = 0;
    public static final int LAX = 1;
    public static final int STRICT = 2;
    private static final long serialVersionUID = 1;

    public AttributeWildcard(NameClass name, int processMode) {
        this.name = name;
        this.processMode = processMode;
    }

    public NameClass getName() {
        return this.name;
    }

    public int getProcessMode() {
        return this.processMode;
    }

    public AttributeWildcard copy() {
        return new AttributeWildcard(this.name, this.processMode);
    }

    public Expression createExpression(XMLSchemaGrammar grammar) {
        ExpressionPool pool = grammar.pool;
        switch (this.processMode) {
            case 0:
                return pool.createZeroOrMore(pool.createAttribute(this.name));
            case 1:
            case 2:
                Expression exp = Expression.epsilon;
                LaxDefaultNameClass laxNc = new LaxDefaultNameClass(this.name);
                Iterator itr = grammar.iterateSchemas();
                while (itr.hasNext()) {
                    XMLSchemaSchema schema = (XMLSchemaSchema) itr.next();
                    if (this.name.accepts(schema.targetNamespace, "*")) {
                        ReferenceExp[] atts = schema.attributeDecls.getAll();
                        for (int i = 0; i < atts.length; i++) {
                            exp = pool.createSequence(pool.createOptional(atts[i]), exp);
                            laxNc.addName(schema.targetNamespace, atts[i].name);
                        }
                    }
                }
                if (this.processMode == 2) {
                    return exp;
                }
                return pool.createSequence(pool.createZeroOrMore(pool.createAttribute(laxNc)), exp);
            default:
                throw new Error("undefined process mode:" + this.processMode);
        }
    }
}
