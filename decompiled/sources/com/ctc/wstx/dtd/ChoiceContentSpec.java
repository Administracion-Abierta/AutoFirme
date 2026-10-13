package com.ctc.wstx.dtd;

import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.PrefixedName;
import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/ChoiceContentSpec.class */
public class ChoiceContentSpec extends ContentSpec {
    final boolean mNsAware;
    final boolean mHasMixed;
    final ContentSpec[] mContentSpecs;

    private ChoiceContentSpec(boolean nsAware, char arity, boolean mixed, ContentSpec[] specs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mHasMixed = mixed;
        this.mContentSpecs = specs;
    }

    private ChoiceContentSpec(boolean nsAware, char arity, boolean mixed, Collection<ContentSpec> specs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mHasMixed = mixed;
        this.mContentSpecs = new ContentSpec[specs.size()];
        specs.toArray(this.mContentSpecs);
    }

    public static ChoiceContentSpec constructChoice(boolean nsAware, char arity, Collection<ContentSpec> specs) {
        return new ChoiceContentSpec(nsAware, arity, false, specs);
    }

    public static ChoiceContentSpec constructMixed(boolean nsAware, Collection<ContentSpec> specs) {
        return new ChoiceContentSpec(nsAware, '*', true, specs);
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public StructValidator getSimpleValidator() {
        int i;
        ContentSpec[] specs = this.mContentSpecs;
        int len = specs.length;
        if (this.mHasMixed) {
            i = len;
        } else {
            i = 0;
            while (i < len && specs[i].isLeaf()) {
                i++;
            }
        }
        if (i == len) {
            PrefixedNameSet keyset = namesetFromSpecs(this.mNsAware, specs);
            return new Validator(this.mArity, keyset);
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public ModelNode rewrite() {
        ContentSpec[] specs = this.mContentSpecs;
        int len = specs.length;
        ModelNode[] models = new ModelNode[len];
        for (int i = 0; i < len; i++) {
            models[i] = specs[i].rewrite();
        }
        ChoiceModel model = new ChoiceModel(models);
        if (this.mArity == '*') {
            return new StarModel(model);
        }
        if (this.mArity == '?') {
            return new OptionalModel(model);
        }
        if (this.mArity == '+') {
            return new ConcatModel(model, new StarModel(model.cloneModel()));
        }
        return model;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.mHasMixed) {
            sb.append("(#PCDATA | ");
        } else {
            sb.append('(');
        }
        for (int i = 0; i < this.mContentSpecs.length; i++) {
            if (i > 0) {
                sb.append(" | ");
            }
            sb.append(this.mContentSpecs[i].toString());
        }
        sb.append(')');
        if (this.mArity != ' ') {
            sb.append(this.mArity);
        }
        return sb.toString();
    }

    protected static PrefixedNameSet namesetFromSpecs(boolean nsAware, ContentSpec[] specs) {
        int len = specs.length;
        PrefixedName[] nameArray = new PrefixedName[len];
        for (int i = 0; i < len; i++) {
            nameArray[i] = ((TokenContentSpec) specs[i]).getName();
        }
        if (len < 5) {
            return new SmallPrefixedNameSet(nsAware, nameArray);
        }
        return new LargePrefixedNameSet(nsAware, nameArray);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/ChoiceContentSpec$Validator.class */
    static final class Validator extends StructValidator {
        final char mArity;
        final PrefixedNameSet mNames;
        int mCount = 0;

        public Validator(char arity, PrefixedNameSet names) {
            this.mArity = arity;
            this.mNames = names;
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public StructValidator newInstance() {
            return this.mArity == '*' ? this : new Validator(this.mArity, this.mNames);
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public String tryToValidate(PrefixedName elemName) {
            if (!this.mNames.contains(elemName)) {
                if (this.mNames.hasMultiple()) {
                    return "Expected one of (" + this.mNames.toString(" | ") + ")";
                }
                return "Expected <" + this.mNames.toString("") + ">";
            }
            int i = this.mCount + 1;
            this.mCount = i;
            if (i <= 1) {
                return null;
            }
            if (this.mArity == '?' || this.mArity == ' ') {
                if (this.mNames.hasMultiple()) {
                    return "Expected $END (already had one of [" + this.mNames.toString(" | ") + "]";
                }
                return "Expected $END (already had one <" + this.mNames.toString("") + ">]";
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
                    return "Expected " + (this.mArity == '+' ? "at least" : "") + " one of elements (" + this.mNames + ")";
                case '*':
                case '?':
                    return null;
                default:
                    ExceptionUtil.throwGenericInternal();
                    return null;
            }
        }
    }
}
