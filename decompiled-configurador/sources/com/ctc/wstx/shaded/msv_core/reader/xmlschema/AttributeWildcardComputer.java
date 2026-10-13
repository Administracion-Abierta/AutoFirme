package com.ctc.wstx.shaded.msv_core.reader.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttWildcardExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeGroupExp;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.AttributeWildcard;
import com.ctc.wstx.shaded.msv_core.grammar.xmlschema.ComplexTypeExp;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/xmlschema/AttributeWildcardComputer.class */
public class AttributeWildcardComputer extends ExpressionWalker {
    private final XMLSchemaReader reader;
    private final Set visitedExps = new HashSet();
    private final Stack unprocessedElementExps = new Stack();
    private Set wildcards = null;

    public static void compute(XMLSchemaReader reader, Expression topLevel) {
        new AttributeWildcardComputer(reader).compute(topLevel);
    }

    private void compute(Expression topLevel) {
        topLevel.visit(this);
        while (!this.unprocessedElementExps.isEmpty()) {
            ((ElementExp) this.unprocessedElementExps.pop()).contentModel.visit(this);
        }
    }

    protected AttributeWildcardComputer(XMLSchemaReader _reader) {
        this.reader = _reader;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onElement(ElementExp exp) {
        if (!this.visitedExps.add(exp)) {
            return;
        }
        this.unprocessedElementExps.add(exp);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onRef(ReferenceExp referenceExp) {
        AttributeWildcard w;
        if (this.visitedExps.add(referenceExp)) {
            if (referenceExp instanceof AttributeGroupExp) {
                AttributeGroupExp aexp = (AttributeGroupExp) referenceExp;
                Set o = this.wildcards;
                this.wildcards = new HashSet();
                referenceExp.exp.visit(this);
                aexp.wildcard = calcCompleteWildcard(aexp.wildcard, this.wildcards);
                this.wildcards = o;
            } else if (referenceExp instanceof ComplexTypeExp) {
                ComplexTypeExp cexp = (ComplexTypeExp) referenceExp;
                Set o2 = this.wildcards;
                this.wildcards = new HashSet();
                referenceExp.exp.visit(this);
                cexp.wildcard = calcCompleteWildcard(cexp.wildcard, this.wildcards);
                if (cexp.complexBaseType != null) {
                    cexp.complexBaseType.visit(this);
                    if (cexp.derivationMethod == 2) {
                        cexp.wildcard = calcComplexTypeWildcard(cexp.wildcard, cexp.complexBaseType.wildcard);
                    }
                    propagateAttributes(cexp);
                }
                if (cexp.wildcard != null) {
                    cexp.attWildcard.exp = cexp.wildcard.createExpression(this.reader.grammar);
                }
                this.wildcards = o2;
            } else {
                super.onRef(referenceExp);
            }
        }
        if (this.wildcards == null || !(referenceExp instanceof AttWildcardExp) || (w = ((AttWildcardExp) referenceExp).getAttributeWildcard()) == null) {
            return;
        }
        this.wildcards.add(w);
    }

    private AttributeWildcard calcCompleteWildcard(AttributeWildcard local, Set s) {
        AttributeWildcard[] children = (AttributeWildcard[]) s.toArray(new AttributeWildcard[s.size()]);
        if (children.length == 0) {
            return local;
        }
        NameClass target = children[0].getName();
        for (int i = 1; i < children.length; i++) {
            target = NameClass.intersection(target, children[i].getName());
        }
        if (local != null) {
            return new AttributeWildcard(NameClass.intersection(local.getName(), target), local.getProcessMode());
        }
        return new AttributeWildcard(target, children[0].getProcessMode());
    }

    private AttributeWildcard calcComplexTypeWildcard(AttributeWildcard complete, AttributeWildcard base) {
        if (base != null) {
            if (complete == null) {
                return base;
            }
            return new AttributeWildcard(NameClass.union(complete.getName(), base.getName()), complete.getProcessMode());
        }
        return complete;
    }

    private void propagateAttributes(final ComplexTypeExp cexp) {
        if (cexp.derivationMethod != 1 || cexp.complexBaseType == null || cexp.complexBaseType == this.reader.complexUrType) {
            return;
        }
        final Set explicitAtts = new HashSet();
        cexp.body.visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.AttributeWildcardComputer.1
            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onElement(ElementExp exp) {
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onAttribute(AttributeExp exp) {
                if (!(exp.nameClass instanceof SimpleNameClass)) {
                    throw new Error(exp.nameClass.toString());
                }
                explicitAtts.add(((SimpleNameClass) exp.nameClass).toStringPair());
            }
        });
        cexp.complexBaseType.body.visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.xmlschema.AttributeWildcardComputer.2
            private boolean isOptional = false;

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onChoice(ChoiceExp exp) {
                boolean b = this.isOptional;
                this.isOptional = true;
                super.onChoice(exp);
                this.isOptional = b;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onElement(ElementExp exp) {
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onAttribute(AttributeExp exp) {
                if (!(exp.nameClass instanceof SimpleNameClass)) {
                    throw new Error();
                }
                SimpleNameClass snc = (SimpleNameClass) exp.nameClass;
                if (!explicitAtts.contains(snc.toStringPair())) {
                    cexp.body.exp = AttributeWildcardComputer.this.reader.pool.createSequence(cexp.body.exp, this.isOptional ? AttributeWildcardComputer.this.reader.pool.createOptional(exp) : exp);
                }
            }
        });
    }
}
