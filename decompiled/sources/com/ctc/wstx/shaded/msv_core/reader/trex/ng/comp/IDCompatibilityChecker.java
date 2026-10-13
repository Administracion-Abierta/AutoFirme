package com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.NameClass;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker;
import com.ctc.wstx.shaded.msv_core.grammar.util.RefExpRemover;
import com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDParser;
import com.ctc.wstx.shaded.msv_core.util.StringPair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.xml.sax.Locator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/IDCompatibilityChecker.class */
class IDCompatibilityChecker extends CompatibilityChecker {
    private static final String CERR_MALPLACED_ID_TYPE = "RELAXNGReader.Compatibility.ID.MalplacedIDType";
    private static final String CERR_ID_TYPE_WITH_NON_SIMPLE_ATTNAME = "RELAXNGReader.Compatibility.ID.IDTypeWithNonSimpleAttName";
    private static final String CERR_ID_TYPE_WITH_NON_SIMPLE_ELEMENTNAME = "RELAXNGReader.Compatibility.ID.IDTypeWithNonSimpleElementName";
    private static final String CERR_COMPETING = "RELAXNGReader.Compatibility.ID.Competing";
    private static final String CERR_COMPETING2 = "RELAXNGReader.Compatibility.ID.Competing2";

    IDCompatibilityChecker(RELAXNGCompReader reader) {
        super(reader);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.CompatibilityChecker
    protected void setCompatibility(boolean val) {
        this.grammar.isIDcompatible = val;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/reader/trex/ng/comp/IDCompatibilityChecker$IDAttMap.class */
    private static class IDAttMap {
        final ElementExp sampleDecl;
        final Map idatts = new HashMap();

        IDAttMap(ElementExp e) {
            this.sampleDecl = e;
        }
    }

    public void test() {
        this.grammar.isIDcompatible = true;
        final Map name2value = new HashMap();
        final Set<ElementExp> elements = new HashSet();
        final RefExpRemover remover = new RefExpRemover(this.reader.pool, false);
        this.reader.getGrammar().visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.IDCompatibilityChecker.1
            private StringPair elementName = null;
            private ElementExp curElm = null;
            private IDAttMap curAtts = null;

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onElement(ElementExp exp) {
                if (!elements.add(exp)) {
                    return;
                }
                StringPair _en = this.elementName;
                IDAttMap _curAtts = this.curAtts;
                ElementExp _curElm = this.curElm;
                NameClass nc = exp.getNameClass();
                if (nc instanceof SimpleNameClass) {
                    this.elementName = new StringPair((SimpleNameClass) nc);
                    this.curAtts = (IDAttMap) name2value.get(this.elementName);
                } else {
                    this.elementName = null;
                }
                this.curElm = exp;
                exp.contentModel.visit(remover).visit(this);
                if (this.elementName != null && this.curAtts != null) {
                    name2value.put(this.elementName, this.curAtts);
                }
                this.elementName = _en;
                this.curAtts = _curAtts;
                this.curElm = _curElm;
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onAttribute(AttributeExp exp) {
                if (!(exp.exp instanceof DataOrValueExp)) {
                    exp.exp.visit(this);
                    return;
                }
                DataOrValueExp texp = (DataOrValueExp) exp.exp;
                if (texp.getType().getIdType() == 0) {
                    return;
                }
                if (!(exp.nameClass instanceof SimpleNameClass)) {
                    IDCompatibilityChecker.this.reportCompError(new Locator[]{IDCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, IDCompatibilityChecker.CERR_ID_TYPE_WITH_NON_SIMPLE_ATTNAME, new Object[]{texp.getName().localName, IDCompatibilityChecker.getSemanticsStr(texp.getType().getIdType())});
                    return;
                }
                StringPair attName = new StringPair((SimpleNameClass) exp.nameClass);
                if (this.elementName == null) {
                    IDCompatibilityChecker.this.reportCompError(new Locator[]{IDCompatibilityChecker.this.reader.getDeclaredLocationOf(exp), IDCompatibilityChecker.this.reader.getDeclaredLocationOf(this.curElm)}, IDCompatibilityChecker.CERR_ID_TYPE_WITH_NON_SIMPLE_ELEMENTNAME, new Object[]{texp.getName().localName, IDCompatibilityChecker.getSemanticsStr(texp.getType().getIdType())});
                    return;
                }
                if (this.curAtts == null) {
                    this.curAtts = new IDAttMap(this.curElm);
                }
                this.curAtts.idatts.put(attName, texp.getName());
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onData(DataExp exp) {
                checkIdType(exp);
            }

            @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
            public void onValue(ValueExp exp) {
                checkIdType(exp);
            }

            private void checkIdType(DataOrValueExp exp) {
                if (exp.getType().getIdType() != 0) {
                    IDCompatibilityChecker.this.reportCompError(new Locator[]{IDCompatibilityChecker.this.reader.getDeclaredLocationOf(exp)}, IDCompatibilityChecker.CERR_MALPLACED_ID_TYPE, new Object[]{exp.getName().localName, IDCompatibilityChecker.getSemanticsStr(exp.getType().getIdType())});
                }
            }
        });
        if (!this.grammar.isIDcompatible) {
            return;
        }
        final Vector vec = new Vector();
        for (final ElementExp eexp : elements) {
            vec.clear();
            for (Map.Entry e : name2value.entrySet()) {
                if (eexp.getNameClass().accepts((StringPair) e.getKey())) {
                    vec.add(e.getValue());
                }
            }
            if (vec.size() != 0) {
                eexp.contentModel.visit(remover).visit(new ExpressionWalker() { // from class: com.ctc.wstx.shaded.msv_core.reader.trex.ng.comp.IDCompatibilityChecker.2
                    @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                    public void onElement(ElementExp exp) {
                    }

                    @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                    public void onAttribute(AttributeExp exp) {
                        if (exp.exp instanceof DataOrValueExp) {
                            DataOrValueExp texp = (DataOrValueExp) exp.exp;
                            if (texp.getType().getIdType() != 0) {
                                IDCompatibilityChecker._assert(vec.size() == 1);
                                SimpleNameClass attName = (SimpleNameClass) exp.nameClass;
                                IDAttMap iam = (IDAttMap) vec.get(0);
                                if (!texp.getName().equals(iam.idatts.get(new StringPair(attName)))) {
                                    IDCompatibilityChecker.this.reportCompError(new Locator[]{IDCompatibilityChecker.this.reader.getDeclaredLocationOf(exp), IDCompatibilityChecker.this.reader.getDeclaredLocationOf(iam.sampleDecl)}, IDCompatibilityChecker.CERR_COMPETING, new Object[]{texp.getName().localName, IDCompatibilityChecker.getSemanticsStr(texp.getType().getIdType())});
                                    return;
                                }
                                return;
                            }
                        }
                        for (int i = vec.size() - 1; i >= 0; i--) {
                            IDAttMap iam2 = (IDAttMap) vec.get(i);
                            for (Map.Entry e2 : iam2.idatts.entrySet()) {
                                if (exp.nameClass.accepts((StringPair) e2.getKey())) {
                                    IDCompatibilityChecker.this.reportCompError(new Locator[]{IDCompatibilityChecker.this.reader.getDeclaredLocationOf(exp), IDCompatibilityChecker.this.reader.getDeclaredLocationOf(eexp), IDCompatibilityChecker.this.reader.getDeclaredLocationOf(iam2.sampleDecl)}, IDCompatibilityChecker.CERR_COMPETING2, new Object[]{((StringPair) e2.getKey()).localName, ((StringPair) e2.getValue()).localName});
                                    return;
                                }
                            }
                        }
                    }

                    @Override // com.ctc.wstx.shaded.msv_core.grammar.util.ExpressionWalker, com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
                    public void onList(ListExp exp) {
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getSemanticsStr(int type) {
        switch (type) {
            case 1:
                return "ID";
            case 2:
                return DTDParser.TYPE_IDREF;
            case 3:
                return DTDParser.TYPE_IDREFS;
            default:
                throw new Error();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void _assert(boolean b) {
        if (!b) {
            throw new Error("assertion failed");
        }
    }
}
