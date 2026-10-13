package com.ctc.wstx.shaded.msv_core.writer.relaxng;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ConcreteType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.DataTypeWithFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.EnumerationFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FinalComponent;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.FractionDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.LengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.ListType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MaxLengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.MinLengthFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.PatternFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.RangeFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TotalDigitsFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.UnionType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceFacet;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.BinaryExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.EmptyStringType;
import com.ctc.wstx.shaded.msv_core.grammar.relax.NoneType;
import com.ctc.wstx.shaded.msv_core.writer.XMLWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import org.spongycastle.i18n.TextBundle;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/writer/relaxng/PatternWriter.class */
public abstract class PatternWriter implements ExpressionVisitorVoid {
    protected final XMLWriter writer;
    protected final Context context;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public abstract void onOther(OtherExp otherExp);

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public abstract void onRef(ReferenceExp referenceExp);

    public PatternWriter(Context ctxt) {
        this.writer = ctxt.getWriter();
        this.context = ctxt;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onElement(ElementExp exp) {
        this.writer.start("element");
        this.context.writeNameClass(exp.getNameClass());
        visitUnary(exp.contentModel);
        this.writer.end("element");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onEpsilon() {
        this.writer.element("empty");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onNullSet() {
        this.writer.element("notAllowed");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAnyString() {
        this.writer.element(TextBundle.TEXT_ENTRY);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onInterleave(InterleaveExp exp) {
        visitBinExp("interleave", exp, InterleaveExp.class);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onConcur(ConcurExp exp) {
        throw new IllegalArgumentException("the grammar includes concur, which is not supported");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onList(ListExp exp) {
        this.writer.start("list");
        visitUnary(exp.exp);
        this.writer.end("list");
    }

    protected void onOptional(Expression exp) {
        if (exp instanceof OneOrMoreExp) {
            onZeroOrMore((OneOrMoreExp) exp);
            return;
        }
        this.writer.start("optional");
        visitUnary(exp);
        this.writer.end("optional");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onChoice(ChoiceExp exp) {
        if (exp.exp1 == Expression.epsilon) {
            onOptional(exp.exp2);
        } else if (exp.exp2 == Expression.epsilon) {
            onOptional(exp.exp1);
        } else {
            visitBinExp("choice", exp, ChoiceExp.class);
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onSequence(SequenceExp exp) {
        visitBinExp("group", exp, SequenceExp.class);
    }

    public void visitBinExp(String elementName, BinaryExp exp, Class type) {
        this.writer.start(elementName);
        Expression[] children = exp.getChildren();
        for (Expression expression : children) {
            expression.visit(this);
        }
        this.writer.end(elementName);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onMixed(MixedExp exp) {
        this.writer.start("mixed");
        visitUnary(exp.exp);
        this.writer.end("mixed");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOneOrMore(OneOrMoreExp exp) {
        this.writer.start("oneOrMore");
        visitUnary(exp.exp);
        this.writer.end("oneOrMore");
    }

    protected void onZeroOrMore(OneOrMoreExp exp) {
        this.writer.start("zeroOrMore");
        visitUnary(exp.exp);
        this.writer.end("zeroOrMore");
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAttribute(AttributeExp exp) {
        this.writer.start("attribute");
        this.context.writeNameClass(exp.nameClass);
        visitUnary(exp.exp);
        this.writer.end("attribute");
    }

    public void visitUnary(Expression exp) {
        if (exp instanceof SequenceExp) {
            SequenceExp seq = (SequenceExp) exp;
            visitUnary(seq.exp1);
            seq.exp2.visit(this);
            return;
        }
        exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onValue(ValueExp exp) {
        if (exp.dt instanceof XSDatatypeImpl) {
            XSDatatypeImpl base = (XSDatatypeImpl) exp.dt;
            final Vector ns = new Vector();
            String lex = base.convertToLexicalValue(exp.value, new SerializationContext() { // from class: com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter.1
                @Override // com.ctc.wstx.shaded.msv_core.datatype.SerializationContext
                public String getNamespacePrefix(String namespaceURI) {
                    int cnt = ns.size() / 2;
                    ns.add("xmlns:ns" + cnt);
                    ns.add(namespaceURI);
                    return "ns" + cnt;
                }
            });
            if (base != TokenType.theInstance) {
                ns.add("type");
                ns.add(base.getName());
            }
            this.writer.start("value", (String[]) ns.toArray(new String[0]));
            this.writer.characters(lex);
            this.writer.end("value");
            return;
        }
        throw new UnsupportedOperationException(exp.dt.getClass().getName());
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onData(DataExp exp) {
        Datatype dt = exp.dt;
        if (dt instanceof XSDatatypeImpl) {
            XSDatatypeImpl dti = (XSDatatypeImpl) dt;
            if (isPredefinedType(dt)) {
                this.writer.element("data", new String[]{"type", dti.getName()});
                return;
            } else {
                serializeDataType(dti);
                return;
            }
        }
        this.writer.element("data-unknown", new String[]{"class", dt.getClass().getName()});
    }

    protected void serializeDataType(XSDatatype dt) {
        if (dt instanceof UnionType) {
            serializeUnionType((UnionType) dt);
            return;
        }
        Set appliedFacets = new HashSet();
        Vector effectiveFacets = new Vector();
        XSDatatype baseType = dt;
        while (true) {
            XSDatatype x = baseType;
            if ((x instanceof DataTypeWithFacet) || (x instanceof FinalComponent)) {
                if (x instanceof FinalComponent) {
                    baseType = x.getBaseType();
                } else {
                    String facetName = ((DataTypeWithFacet) x).facetName;
                    if (facetName.equals(XSDatatype.FACET_ENUMERATION)) {
                        serializeEnumeration((XSDatatypeImpl) dt, (EnumerationFacet) x);
                        return;
                    } else if (facetName.equals(XSDatatype.FACET_WHITESPACE)) {
                        System.err.println("warning: unsupported whiteSpace facet is ignored");
                        baseType = x.getBaseType();
                    } else {
                        if (!appliedFacets.contains(facetName)) {
                            appliedFacets.add(facetName);
                            effectiveFacets.add(x);
                        }
                        baseType = ((DataTypeWithFacet) x).baseType;
                    }
                }
            } else {
                if (x instanceof ListType) {
                    serializeListType((XSDatatypeImpl) dt);
                    return;
                }
                if (!(x instanceof ConcreteType)) {
                    throw new Error(x.getClass().getName());
                }
                if (x instanceof EmptyStringType) {
                    this.writer.element("value");
                    return;
                }
                if (x instanceof NoneType) {
                    this.writer.element("notAllowed");
                    return;
                }
                this.writer.start("data", new String[]{"type", x.getName()});
                for (int i = effectiveFacets.size() - 1; i >= 0; i--) {
                    DataTypeWithFacet dtf = (DataTypeWithFacet) effectiveFacets.get(i);
                    if (dtf instanceof LengthFacet) {
                        param(XSDatatype.FACET_LENGTH, Long.toString(((LengthFacet) dtf).length));
                    } else if (dtf instanceof MinLengthFacet) {
                        param(XSDatatype.FACET_MINLENGTH, Long.toString(((MinLengthFacet) dtf).minLength));
                    } else if (dtf instanceof MaxLengthFacet) {
                        param(XSDatatype.FACET_MAXLENGTH, Long.toString(((MaxLengthFacet) dtf).maxLength));
                    } else if (dtf instanceof PatternFacet) {
                        String pattern = "";
                        PatternFacet pf = (PatternFacet) dtf;
                        for (int j = 0; j < pf.getRegExps().length; j++) {
                            if (pattern.length() != 0) {
                                pattern = pattern + "|";
                            }
                            pattern = pattern + pf.patterns[j];
                        }
                        param(XSDatatype.FACET_PATTERN, pattern);
                    } else if (dtf instanceof TotalDigitsFacet) {
                        param(XSDatatype.FACET_TOTALDIGITS, Long.toString(((TotalDigitsFacet) dtf).precision));
                    } else if (dtf instanceof FractionDigitsFacet) {
                        param(XSDatatype.FACET_FRACTIONDIGITS, Long.toString(((FractionDigitsFacet) dtf).scale));
                    } else if (dtf instanceof RangeFacet) {
                        param(dtf.facetName, dtf.convertToLexicalValue(((RangeFacet) dtf).limitValue, null));
                    } else if (!(dtf instanceof WhiteSpaceFacet)) {
                        throw new Error();
                    }
                }
                this.writer.end("data");
                return;
            }
        }
    }

    protected void param(String name, String value) {
        this.writer.start("param", new String[]{"name", name});
        this.writer.characters(value);
        this.writer.end("param");
    }

    protected boolean isPredefinedType(Datatype x) {
        return ((x instanceof DataTypeWithFacet) || (x instanceof UnionType) || (x instanceof ListType) || (x instanceof FinalComponent) || (x instanceof EmptyStringType) || (x instanceof NoneType)) ? false : true;
    }

    protected void serializeUnionType(UnionType dt) {
        this.writer.start("choice");
        for (int i = 0; i < dt.memberTypes.length; i++) {
            serializeDataType(dt.memberTypes[i]);
        }
        this.writer.end("choice");
    }

    protected void serializeListType(XSDatatypeImpl dt) {
        ListType base = (ListType) dt.getConcreteType();
        if (dt.getFacetObject(XSDatatype.FACET_LENGTH) != null) {
            int len = ((LengthFacet) dt.getFacetObject(XSDatatype.FACET_LENGTH)).length;
            this.writer.start("list");
            for (int i = 0; i < len; i++) {
                serializeDataType(base.itemType);
            }
            this.writer.end("list");
            return;
        }
        if (dt.getFacetObject(XSDatatype.FACET_MAXLENGTH) != null) {
            throw new UnsupportedOperationException("warning: maxLength facet to list type is not properly converted.");
        }
        MinLengthFacet minLength = (MinLengthFacet) dt.getFacetObject(XSDatatype.FACET_MINLENGTH);
        this.writer.start("list");
        if (minLength != null) {
            for (int i2 = 0; i2 < minLength.minLength; i2++) {
                serializeDataType(base.itemType);
            }
        }
        this.writer.start("zeroOrMore");
        serializeDataType(base.itemType);
        this.writer.end("zeroOrMore");
        this.writer.end("list");
    }

    protected void serializeEnumeration(XSDatatypeImpl dt, EnumerationFacet enums) {
        Object[] values = enums.values.toArray();
        if (values.length > 1) {
            this.writer.start("choice");
        }
        for (Object obj : values) {
            final Vector ns = new Vector();
            String lex = dt.convertToLexicalValue(obj, new SerializationContext() { // from class: com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter.2
                @Override // com.ctc.wstx.shaded.msv_core.datatype.SerializationContext
                public String getNamespacePrefix(String namespaceURI) {
                    int cnt = ns.size() / 2;
                    ns.add("xmlns:ns" + cnt);
                    ns.add(namespaceURI);
                    return "ns" + cnt;
                }
            });
            boolean allowed = dt.isValid(lex, new ValidationContext() { // from class: com.ctc.wstx.shaded.msv_core.writer.relaxng.PatternWriter.3
                @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
                public String resolveNamespacePrefix(String prefix) {
                    if (!prefix.startsWith("ns")) {
                        return null;
                    }
                    int i = Integer.parseInt(prefix.substring(2));
                    return (String) ns.get((i * 2) + 1);
                }

                @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
                public boolean isUnparsedEntity(String name) {
                    return true;
                }

                @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
                public boolean isNotation(String name) {
                    return true;
                }

                @Override // com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext
                public String getBaseUri() {
                    return null;
                }
            });
            ns.add("type");
            ns.add(dt.getConcreteType().getName());
            if (allowed) {
                this.writer.start("value", (String[]) ns.toArray(new String[0]));
                this.writer.characters(lex);
                this.writer.end("value");
            }
        }
        if (values.length > 1) {
            this.writer.end("choice");
        }
    }
}
