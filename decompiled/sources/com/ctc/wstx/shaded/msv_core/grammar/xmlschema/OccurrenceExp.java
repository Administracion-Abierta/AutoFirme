package com.ctc.wstx.shaded.msv_core.grammar.xmlschema;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/xmlschema/OccurrenceExp.class */
public class OccurrenceExp extends OtherExp {
    public final int maxOccurs;
    public final int minOccurs;
    public final Expression itemExp;
    private static final long serialVersionUID = 1;

    public OccurrenceExp(Expression preciseExp, int maxOccurs, int minOccurs, Expression itemExp) {
        super(preciseExp);
        this.maxOccurs = maxOccurs;
        this.minOccurs = minOccurs;
        this.itemExp = itemExp;
    }

    public String toString() {
        return this.itemExp.toString() + "[" + this.minOccurs + "," + (this.maxOccurs == -1 ? "inf" : String.valueOf(this.maxOccurs)) + "]";
    }
}
