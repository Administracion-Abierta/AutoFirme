package org.bouncycastle.jcajce.spec;

import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/CompositeAlgorithmSpec.class */
public class CompositeAlgorithmSpec implements AlgorithmParameterSpec {
    private final List<String> algorithmNames;
    private final List<AlgorithmParameterSpec> parameterSpecs;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/spec/CompositeAlgorithmSpec$Builder.class */
    public static class Builder {
        private List<String> algorithmNames = new ArrayList();
        private List<AlgorithmParameterSpec> parameterSpecs = new ArrayList();

        public Builder add(String str) {
            return add(str, null);
        }

        public Builder add(String str, AlgorithmParameterSpec algorithmParameterSpec) {
            if (this.algorithmNames.contains(str)) {
                throw new IllegalStateException("cannot build with the same algorithm name added");
            }
            this.algorithmNames.add(str);
            this.parameterSpecs.add(algorithmParameterSpec);
            return this;
        }

        public CompositeAlgorithmSpec build() {
            if (this.algorithmNames.isEmpty()) {
                throw new IllegalStateException("cannot call build with no algorithm names added");
            }
            return new CompositeAlgorithmSpec(this);
        }
    }

    public CompositeAlgorithmSpec(Builder builder) {
        this.algorithmNames = Collections.unmodifiableList(new ArrayList(builder.algorithmNames));
        this.parameterSpecs = Collections.unmodifiableList(new ArrayList(builder.parameterSpecs));
    }

    public List<String> getAlgorithmNames() {
        return this.algorithmNames;
    }

    public List<AlgorithmParameterSpec> getParameterSpecs() {
        return this.parameterSpecs;
    }
}
