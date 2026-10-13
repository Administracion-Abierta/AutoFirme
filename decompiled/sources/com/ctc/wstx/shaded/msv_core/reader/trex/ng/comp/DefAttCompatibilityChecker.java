package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.util.RefExpRemover;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.ResidualCalculator;
import com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/DefAttCompatibilityChecker.class */
class DefAttCompatibilityChecker extends CompatibilityChecker {
    private final Map defaultedAttributes;
    private final RefExpRemover refRemover;
    private ExpressionWalker contextDependentTypeChecker;
    public static final String CERR_DEFVALUE_NAME_IS_NOT_SIMPLE = "RELAXNGReader.Compatibility.DefaultValue.NameIsNotSimple";
    public static final String CERR_DEFVALUE_INVALID = "RELAXNGReader.Compatibility.DefaultValue.Invalid";
    public static final String CERR_DEFVALUE_NOT_OPTIONAL = "RELAXNGReader.Compatibility.DefaultValue.NotOptional";
    public static final String CERR_DEFVALUE_REPEATABLE = "RELAXNGReader.Compatibility.DefaultValue.Repeatable";
    public static final String CERR_DEFVALUE_COMPLEX_ELEMENTNAME = "RELAXNGReader.Compatibility.DefaultValue.ComplexElementName";
    public static final String CERR_DEFVALUE_DIFFERENT_VALUES = "RELAXNGReader.Compatibility.DefaultValue.DifferentValues";
    public static final String CERR_DEFVALUE_CONTEXT_DEPENDENT_TYPE = "RELAXNGReader.Compatibility.DefaultValue.ContextDependentType";
    public static final String CERR_DEFVALUE_COMPETING_ELEMENTS = "RELAXNGReader.Compatibility.DefaultValue.CompetingElements";

    DefAttCompatibilityChecker(RELAXNGCompReader _reader, Map _defaultedAttributes) {
        super(_reader);
        this.refRemover = new RefExpRemover(this.reader.pool, false);
        this.contextDependentTypeChecker = new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.DefAttCompatibilityChecker.2
            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onData(DataExp exp) {
                check(exp.dt, exp.name);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onValue(ValueExp exp) {
                check(exp.dt, exp.name);
            }

            private void check(Datatype dt, StringPair name) {
                if (dt.isContextDependent()) {
                    DefAttCompatibilityChecker.this.reportCompError(null, DefAttCompatibilityChecker.CERR_DEFVALUE_CONTEXT_DEPENDENT_TYPE, new Object[]{name.localName});
                    throw new Abort();
                }
            }
        };
        this.defaultedAttributes = _defaultedAttributes;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.CompatibilityChecker
    protected void setCompatibility(boolean val) {
        this.grammar.isDefaultAttributeValueCompatible = val;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/DefAttCompatibilityChecker$Abort.class */
    private static final class Abort extends RuntimeException {
        private Abort() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/DefAttCompatibilityChecker$DefAttMap.class */
    private static final class DefAttMap {
        final Map defaultAttributes;
        final ElementExp sampleDecl;

        DefAttMap(ElementExp sample, Map atts) {
            this.sampleDecl = sample;
            this.defaultAttributes = atts;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isEpsilon(Expression exp) {
        return exp == Expression.epsilon || exp.visit(this.refRemover) == Expression.epsilon;
    }

    public void test() {
        this.grammar.isDefaultAttributeValueCompatible = true;
        if (this.defaultedAttributes.size() == 0) {
            return;
        }
        ResidualCalculator resCalc = new ResidualCalculator(this.reader.pool);
        for (Map.Entry item : this.defaultedAttributes.entrySet()) {
            AttributeExp exp = (AttributeExp) item.getKey();
            String value = (String) item.getValue();
            if (!(exp.nameClass instanceof SimpleNameClass)) {
                reportCompError(new Locator[]{this.reader.getDeclaredLocationOf(exp)}, CERR_DEFVALUE_NAME_IS_NOT_SIMPLE);
            }
            try {
                exp.exp.visit(this.contextDependentTypeChecker);
                StringToken token = new StringToken(resCalc, value, (IDContextProvider2) null, (DatatypeRef) null);
                if (!resCalc.calcResidual(exp.exp, token).isEpsilonReducible()) {
                    reportCompError(new Locator[]{this.reader.getDeclaredLocationOf(exp)}, CERR_DEFVALUE_INVALID, new Object[]{value});
                }
            } catch (Abort e) {
            }
        }
        if (!this.grammar.isDefaultAttributeValueCompatible) {
            return;
        }
        final Map name2value = new HashMap();
        final Set<ElementExp> elements = new HashSet();
        this.grammar.visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.DefAttCompatibilityChecker.1
            private boolean inOneOrMore = false;
            private boolean inChoice = false;
            private boolean inOptionalChoice = false;
            private boolean inSimpleElement = false;
            private Map currentAttributes = null;
            private SimpleNameClass currentElementName = null;

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onElement(ElementExp exp2) {
                if (!elements.add(exp2)) {
                    return;
                }
                boolean oldSE = this.inSimpleElement;
                boolean oldOC = this.inOptionalChoice;
                boolean oldC = this.inChoice;
                boolean oldOOM = this.inOneOrMore;
                SimpleNameClass prevElemName = this.currentElementName;
                Map oldCA = this.currentAttributes;
                this.inSimpleElement = exp2.getNameClass() instanceof SimpleNameClass;
                this.inOptionalChoice = true;
                this.inChoice = false;
                this.inOneOrMore = false;
                StringPair en = null;
                if (this.inSimpleElement) {
                    this.currentElementName = (SimpleNameClass) exp2.getNameClass();
                    en = new StringPair(this.currentElementName);
                    this.currentAttributes = new HashMap();
                } else {
                    this.currentElementName = null;
                }
                exp2.contentModel.visit(this);
                if (en != null) {
                    DefAttMap m = (DefAttMap) name2value.get(en);
                    if (m == null) {
                        name2value.put(en, new DefAttMap(exp2, this.currentAttributes));
                    } else if (!m.defaultAttributes.equals(this.currentAttributes)) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(m.sampleDecl), DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp2)}, DefAttCompatibilityChecker.CERR_DEFVALUE_COMPETING_ELEMENTS, new Object[]{((SimpleNameClass) m.sampleDecl.getNameClass()).localName});
                        name2value.remove(en);
                    }
                }
                this.inSimpleElement = oldSE;
                this.inOptionalChoice = oldOC;
                this.inChoice = oldC;
                this.inOneOrMore = oldOOM;
                this.currentElementName = prevElemName;
                this.currentAttributes = oldCA;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onOneOrMore(OneOrMoreExp exp2) {
                boolean oldOOM = this.inOneOrMore;
                this.inOneOrMore = true;
                exp2.exp.visit(this);
                this.inOneOrMore = oldOOM;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onChoice(ChoiceExp exp2) {
                boolean oldOC = this.inOptionalChoice;
                boolean oldC = this.inChoice;
                this.inChoice = true;
                if (!DefAttCompatibilityChecker.this.isEpsilon(exp2.exp1) && !DefAttCompatibilityChecker.this.isEpsilon(exp2.exp2)) {
                    this.inOptionalChoice = false;
                }
                super.onChoice(exp2);
                this.inOptionalChoice = oldOC;
                this.inChoice = oldC;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onAttribute(AttributeExp exp2) {
                if (DefAttCompatibilityChecker.this.defaultedAttributes.containsKey(exp2)) {
                    if (!this.inOptionalChoice || !this.inChoice) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp2)}, DefAttCompatibilityChecker.CERR_DEFVALUE_NOT_OPTIONAL);
                        return;
                    }
                    if (this.inOneOrMore) {
                        DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp2)}, DefAttCompatibilityChecker.CERR_DEFVALUE_REPEATABLE);
                        return;
                    }
                    if (this.inSimpleElement) {
                        String value2 = (String) DefAttCompatibilityChecker.this.defaultedAttributes.get(exp2);
                        String v = (String) this.currentAttributes.put(new StringPair((SimpleNameClass) exp2.nameClass), value2);
                        if (v != null && !v.equals(value2)) {
                            DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp2)}, DefAttCompatibilityChecker.CERR_DEFVALUE_DIFFERENT_VALUES, new Object[]{v, value2, this.currentElementName.localName, ((SimpleNameClass) exp2.nameClass).localName});
                            return;
                        }
                        return;
                    }
                    DefAttCompatibilityChecker.this.reportCompError(new Locator[]{DefAttCompatibilityChecker.this.reader.getDeclaredLocationOf(exp2)}, DefAttCompatibilityChecker.CERR_DEFVALUE_COMPLEX_ELEMENTNAME);
                }
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onList(ListExp exp2) {
            }
        });
        for (ElementExp eexp : elements) {
            NameClass nc = eexp.getNameClass();
            if (!(nc instanceof SimpleNameClass)) {
                for (Map.Entry e2 : name2value.entrySet()) {
                    if (nc.accepts((StringPair) e2.getKey())) {
                        DefAttMap defAtts = (DefAttMap) e2.getValue();
                        if (defAtts.defaultAttributes.size() > 0) {
                            reportCompError(new Locator[]{this.reader.getDeclaredLocationOf(defAtts.sampleDecl), this.reader.getDeclaredLocationOf(eexp)}, CERR_DEFVALUE_COMPETING_ELEMENTS, new Object[]{((SimpleNameClass) defAtts.sampleDecl.getNameClass()).localName});
                            return;
                        }
                    }
                }
            }
        }
    }
}
