package com.ctc.wstx.shaded.msv_core.reader.relax.core.checker;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDREFType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.IDType;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ChoiceExp;
import com.ctc.wstx.shaded.msv_core.grammar.ConcurExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.InterleaveExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.MixedExp;
import com.ctc.wstx.shaded.msv_core.grammar.OneOrMoreExp;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SequenceExp;
import com.ctc.wstx.shaded.msv_core.grammar.SimpleNameClass;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.grammar.relax.AttPoolClause;
import com.ctc.wstx.shaded.msv_core.grammar.relax.ElementRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.HedgeRules;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid;
import com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXModule;
import com.ctc.wstx.shaded.msv_core.grammar.relax.TagClause;
import com.ctc.wstx.shaded.msv_core.reader.relax.core.RELAXCoreReader;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/relax/core/checker/IdAbuseChecker.class */
public class IdAbuseChecker implements RELAXExpressionVisitorVoid {
    private final Set tagNames = new HashSet();
    private final Set overloadedNames = new HashSet();
    private final Set nonIdAttrNames = new HashSet();
    private final Set idAttributes = new HashSet();
    private final RELAXModule module;
    private final RELAXCoreReader reader;
    private String currentTagName;

    private IdAbuseChecker(RELAXCoreReader r, RELAXModule m) {
        this.reader = r;
        this.module = m;
    }

    public static void check(RELAXCoreReader reader, RELAXModule module) {
        new IdAbuseChecker(reader, module).run();
    }

    private void run() {
        for (TagClause tag : this.module.tags) {
            if (tag.nameClass instanceof SimpleNameClass) {
                SimpleNameClass snc = (SimpleNameClass) tag.nameClass;
                if (this.tagNames.contains(snc.localName)) {
                    this.overloadedNames.add(snc.localName);
                } else {
                    this.tagNames.add(snc.localName);
                }
            }
        }
        for (TagClause tag2 : this.module.tags) {
            if (tag2.nameClass instanceof SimpleNameClass) {
                this.currentTagName = ((SimpleNameClass) tag2.nameClass).localName;
            } else {
                this.currentTagName = null;
            }
            tag2.exp.visit((RELAXExpressionVisitorVoid) this);
        }
        for (AttributeExp atr : this.idAttributes) {
            if (atr.nameClass instanceof SimpleNameClass) {
                String name = ((SimpleNameClass) atr.nameClass).localName;
                if (this.nonIdAttrNames.contains(name)) {
                    this.reader.reportError(RELAXCoreReader.ERR_ID_ABUSE_1, name);
                }
            } else {
                this.reader.reportError(RELAXCoreReader.ERR_ID_ABUSE);
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAttribute(AttributeExp exp) {
        if (exp.nameClass instanceof SimpleNameClass) {
            Expression body = exp.exp.getExpandedExp(this.reader.pool);
            if (body instanceof DataExp) {
                SimpleNameClass snc = (SimpleNameClass) exp.nameClass;
                if (snc.namespaceURI.equals("")) {
                    Datatype dt = ((DataExp) body).dt;
                    if (dt == IDType.theInstance || dt == IDREFType.theInstance) {
                        if (this.currentTagName == null || this.overloadedNames.contains(this.currentTagName)) {
                            this.idAttributes.add(exp);
                            return;
                        }
                        return;
                    }
                    this.nonIdAttrNames.add(snc.localName);
                }
            }
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onChoice(ChoiceExp exp) {
        exp.exp1.visit((RELAXExpressionVisitorVoid) this);
        exp.exp2.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onElement(ElementExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOneOrMore(OneOrMoreExp exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onMixed(MixedExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onRef(ReferenceExp exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onOther(OtherExp exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onEpsilon() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onNullSet() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onAnyString() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onSequence(SequenceExp exp) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onData(DataExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onValue(ValueExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onAttPool(AttPoolClause exp) {
        exp.exp.visit((RELAXExpressionVisitorVoid) this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onTag(TagClause exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onElementRules(ElementRules exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.relax.RELAXExpressionVisitorVoid
    public void onHedgeRules(HedgeRules exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onInterleave(InterleaveExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onConcur(ConcurExp exp) {
        throw new Error();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid
    public void onList(ListExp exp) {
        throw new Error();
    }
}
