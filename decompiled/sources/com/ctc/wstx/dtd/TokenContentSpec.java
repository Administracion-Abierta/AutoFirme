package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.util.PrefixedName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/TokenContentSpec.class */
public class TokenContentSpec extends ContentSpec {
    static final TokenContentSpec sDummy = new TokenContentSpec(' ', new PrefixedName("*", "*"));
    final PrefixedName mElemName;

    public TokenContentSpec(char arity, PrefixedName elemName) {
        super(arity);
        this.mElemName = elemName;
    }

    public static TokenContentSpec construct(char arity, PrefixedName elemName) {
        return new TokenContentSpec(arity, elemName);
    }

    public static TokenContentSpec getDummySpec() {
        return sDummy;
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public boolean isLeaf() {
        return this.mArity == ' ';
    }

    public PrefixedName getName() {
        return this.mElemName;
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public StructValidator getSimpleValidator() {
        return new Validator(this.mArity, this.mElemName);
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public ModelNode rewrite() {
        TokenModel model = new TokenModel(this.mElemName);
        if (this.mArity == '*') {
            return new StarModel(model);
        }
        if (this.mArity == '?') {
            return new OptionalModel(model);
        }
        if (this.mArity == '+') {
            return new ConcatModel(model, new StarModel(new TokenModel(this.mElemName)));
        }
        return model;
    }

    public String toString() {
        return this.mArity == ' ' ? this.mElemName.toString() : this.mElemName.toString() + this.mArity;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/TokenContentSpec$Validator.class */
    static final class Validator extends StructValidator {
        final char mArity;
        final PrefixedName mElemName;
        int mCount = 0;

        public Validator(char arity, PrefixedName elemName) {
            this.mArity = arity;
            this.mElemName = elemName;
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public StructValidator newInstance() {
            return this.mArity == '*' ? this : new Validator(this.mArity, this.mElemName);
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public String tryToValidate(PrefixedName elemName) {
            if (!elemName.equals(this.mElemName)) {
                return "Expected element <" + this.mElemName + ">";
            }
            int i = this.mCount + 1;
            this.mCount = i;
            if (i <= 1) {
                return null;
            }
            if (this.mArity == '?' || this.mArity == ' ') {
                return "More than one instance of element <" + this.mElemName + ">";
            }
            return null;
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public String fullyValid() {
            switch (this.mArity) {
                case ' ':
                case '+':
                    if (this.mCount > 0) {
                        return null;
                    }
                    return "Expected " + (this.mArity == '+' ? "at least one" : "") + " element <" + this.mElemName + ">";
                case '*':
                case '?':
                    return null;
                default:
                    throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
            }
        }
    }
}
