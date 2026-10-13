package org.spongycastle.jcajce;

import java.security.InvalidParameterException;
import java.security.cert.CertPathParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/PKIXExtendedBuilderParameters.class */
public class PKIXExtendedBuilderParameters implements CertPathParameters {
    private final PKIXExtendedParameters baseParameters;
    private final Set<X509Certificate> excludedCerts;
    private final int maxPathLength;

    /* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/PKIXExtendedBuilderParameters$Builder.class */
    public static class Builder {
        private final PKIXExtendedParameters baseParameters;
        private int maxPathLength;
        private Set<X509Certificate> excludedCerts;

        public Builder(PKIXBuilderParameters baseParameters) {
            this.maxPathLength = 5;
            this.excludedCerts = new HashSet();
            this.baseParameters = new PKIXExtendedParameters.Builder(baseParameters).build();
            this.maxPathLength = baseParameters.getMaxPathLength();
        }

        public Builder(PKIXExtendedParameters baseParameters) {
            this.maxPathLength = 5;
            this.excludedCerts = new HashSet();
            this.baseParameters = baseParameters;
        }

        public Builder addExcludedCerts(Set<X509Certificate> excludedCerts) {
            this.excludedCerts.addAll(excludedCerts);
            return this;
        }

        public Builder setMaxPathLength(int maxPathLength) {
            if (maxPathLength < -1) {
                throw new InvalidParameterException("The maximum path length parameter can not be less than -1.");
            }
            this.maxPathLength = maxPathLength;
            return this;
        }

        public PKIXExtendedBuilderParameters build() {
            return new PKIXExtendedBuilderParameters(this);
        }
    }

    private PKIXExtendedBuilderParameters(Builder builder) {
        this.baseParameters = builder.baseParameters;
        this.excludedCerts = Collections.unmodifiableSet(builder.excludedCerts);
        this.maxPathLength = builder.maxPathLength;
    }

    public PKIXExtendedParameters getBaseParameters() {
        return this.baseParameters;
    }

    public Set getExcludedCerts() {
        return this.excludedCerts;
    }

    public int getMaxPathLength() {
        return this.maxPathLength;
    }

    @Override // java.security.cert.CertPathParameters
    public Object clone() {
        return this;
    }
}
