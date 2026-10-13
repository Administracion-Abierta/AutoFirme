package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.IDContextProvider2;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/StringToken.class */
public class StringToken extends Token {
    public final String literal;
    public final IDContextProvider2 context;
    protected final ResidualCalculator resCalc;
    protected final boolean ignorable;
    public DatatypeRef refType;
    protected boolean saturated;
    private static final Datatype[] ignoredType = new Datatype[0];

    public StringToken(REDocumentDeclaration docDecl, String literal, IDContextProvider2 context) {
        this(docDecl.resCalc, literal, context, (DatatypeRef) null);
    }

    public StringToken(REDocumentDeclaration docDecl, String literal, IDContextProvider2 context, DatatypeRef refType) {
        this(docDecl.resCalc, literal, context, refType);
    }

    public StringToken(ResidualCalculator resCalc, String literal, IDContextProvider2 context, DatatypeRef refType) {
        this.saturated = false;
        this.resCalc = resCalc;
        this.literal = literal;
        this.context = context;
        this.refType = refType;
        this.ignorable = literal.trim().length() == 0;
        if (this.ignorable && refType != null) {
            refType.types = ignoredType;
        }
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(DataExp exp) {
        if (!exp.dt.isValid(this.literal, this.context)) {
            return false;
        }
        if (exp.except != Expression.nullSet && this.resCalc.calcResidual(exp.except, this).isEpsilonReducible()) {
            return false;
        }
        if (this.refType != null) {
            assignType(exp.dt);
        }
        if (exp.dt.getIdType() != 0 && this.context != null) {
            this.context.onID(exp.dt, this);
            return true;
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(ValueExp exp) {
        Object thisValue = exp.dt.createValue(this.literal, this.context);
        if (!exp.dt.sameValue(thisValue, exp.value)) {
            return false;
        }
        if (this.refType != null) {
            assignType(exp.dt);
        }
        if (exp.dt.getIdType() != 0 && this.context != null) {
            this.context.onID(exp.dt, this);
            return true;
        }
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(ListExp exp) {
        StringTokenizer tokens = new StringTokenizer(this.literal);
        Expression residual = exp.exp;
        DatatypeRef dtRef = null;
        Datatype[] childTypes = null;
        int cnt = 0;
        if (this.refType != null) {
            dtRef = new DatatypeRef();
            childTypes = new Datatype[tokens.countTokens()];
        }
        while (tokens.hasMoreTokens()) {
            StringToken child = createChildStringToken(tokens.nextToken(), dtRef);
            residual = this.resCalc.calcResidual(residual, child);
            if (residual == Expression.nullSet) {
                return false;
            }
            if (dtRef != null) {
                if (dtRef.types == null) {
                    this.saturated = true;
                    this.refType.types = null;
                    dtRef = null;
                } else {
                    if (dtRef.types.length != 1) {
                        throw new Error();
                    }
                    int i = cnt;
                    cnt++;
                    childTypes[i] = dtRef.types[0];
                }
            }
        }
        if (!residual.isEpsilonReducible()) {
            return false;
        }
        if (childTypes != null) {
            if (this.saturated) {
                this.refType.types = null;
            } else {
                this.refType.types = childTypes;
            }
            this.saturated = true;
            return true;
        }
        return true;
    }

    protected StringToken createChildStringToken(String literal, DatatypeRef dtRef) {
        return new StringToken(this.resCalc, literal, this.context, dtRef);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean matchAnyString() {
        if (this.refType != null) {
            assignType(StringType.theInstance);
            return true;
        }
        return true;
    }

    private void assignType(Datatype dt) {
        if (this.saturated) {
            if (this.refType.types != null) {
                if (this.refType.types[0] != dt || this.refType.types.length != 1) {
                    this.refType.types = null;
                    return;
                }
                return;
            }
            return;
        }
        this.refType.types = new Datatype[]{dt};
        this.saturated = true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    boolean isIgnorable() {
        return this.ignorable;
    }
}
