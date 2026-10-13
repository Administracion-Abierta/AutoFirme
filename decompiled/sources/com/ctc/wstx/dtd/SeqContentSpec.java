package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;
import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/SeqContentSpec.class */
public class SeqContentSpec extends ContentSpec {
    final boolean mNsAware;
    final ContentSpec[] mContentSpecs;

    public SeqContentSpec(boolean nsAware, char arity, ContentSpec[] subSpecs) {
        super(arity);
        this.mNsAware = nsAware;
        this.mContentSpecs = subSpecs;
    }

    public static SeqContentSpec construct(boolean nsAware, char arity, Collection<ContentSpec> subSpecs) {
        ContentSpec[] specs = new ContentSpec[subSpecs.size()];
        subSpecs.toArray(specs);
        return new SeqContentSpec(nsAware, arity, specs);
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public StructValidator getSimpleValidator() {
        ContentSpec[] specs = this.mContentSpecs;
        int i = 0;
        int len = specs.length;
        while (i < len && specs[i].isLeaf()) {
            i++;
        }
        if (i == len) {
            PrefixedName[] set = new PrefixedName[len];
            for (int i2 = 0; i2 < len; i2++) {
                TokenContentSpec ss = (TokenContentSpec) specs[i2];
                set[i2] = ss.getName();
            }
            return new Validator(this.mArity, set);
        }
        return null;
    }

    @Override // com.ctc.wstx.dtd.ContentSpec
    public ModelNode rewrite() {
        ModelNode model = rewrite(this.mContentSpecs, 0, this.mContentSpecs.length);
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

    private ModelNode rewrite(ContentSpec[] specs, int first, int last) {
        int count = last - first;
        if (count > 3) {
            int mid = ((last + first) + 1) >> 1;
            return new ConcatModel(rewrite(specs, first, mid), rewrite(specs, mid, last));
        }
        ConcatModel model = new ConcatModel(specs[first].rewrite(), specs[first + 1].rewrite());
        if (count == 3) {
            model = new ConcatModel(model, specs[first + 2].rewrite());
        }
        return model;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(');
        for (int i = 0; i < this.mContentSpecs.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mContentSpecs[i].toString());
        }
        sb.append(')');
        if (this.mArity != ' ') {
            sb.append(this.mArity);
        }
        return sb.toString();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/SeqContentSpec$Validator.class */
    static final class Validator extends StructValidator {
        final char mArity;
        final PrefixedName[] mNames;
        int mRounds = 0;
        int mStep = 0;

        public Validator(char arity, PrefixedName[] names) {
            this.mArity = arity;
            this.mNames = names;
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public StructValidator newInstance() {
            return new Validator(this.mArity, this.mNames);
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public String tryToValidate(PrefixedName elemName) {
            if (this.mStep == 0 && this.mRounds == 1 && (this.mArity == '?' || this.mArity == ' ')) {
                return "was not expecting any more elements in the sequence (" + concatNames(this.mNames) + ")";
            }
            PrefixedName next = this.mNames[this.mStep];
            if (!elemName.equals(next)) {
                return expElem(this.mStep);
            }
            int i = this.mStep + 1;
            this.mStep = i;
            if (i == this.mNames.length) {
                this.mRounds++;
                this.mStep = 0;
                return null;
            }
            return null;
        }

        @Override // com.ctc.wstx.dtd.StructValidator
        public String fullyValid() {
            if (this.mStep != 0) {
                return expElem(this.mStep) + "; got end element";
            }
            switch (this.mArity) {
                case ' ':
                case '+':
                    if (this.mRounds > 0) {
                        return null;
                    }
                    return "Expected sequence (" + concatNames(this.mNames) + "); got end element";
                case '*':
                case '?':
                    return null;
                default:
                    throw new IllegalStateException("Internal error");
            }
        }

        private String expElem(int step) {
            return "expected element <" + this.mNames[step] + "> in sequence (" + concatNames(this.mNames) + ")";
        }

        static final String concatNames(PrefixedName[] names) {
            StringBuilder sb = new StringBuilder();
            int len = names.length;
            for (int i = 0; i < len; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(names[i].toString());
            }
            return sb.toString();
        }
    }
}
