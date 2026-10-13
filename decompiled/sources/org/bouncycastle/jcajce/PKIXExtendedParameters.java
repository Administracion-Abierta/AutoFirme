package org.bouncycastle.jcajce;

import java.security.cert.CertPathParameters;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PKIXExtendedParameters.class */
public class PKIXExtendedParameters implements CertPathParameters {
    public static final int PKIX_VALIDITY_MODEL = 0;
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private final PKIXParameters baseParameters;
    private final PKIXCertStoreSelector targetConstraints;
    private final Date validityDate;
    private final Date date;
    private final List<PKIXCertStore> extraCertStores;
    private final Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
    private final List<PKIXCRLStore> extraCRLStores;
    private final Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
    private final boolean revocationEnabled;
    private final boolean useDeltas;
    private final int validityModel;
    private final Set<TrustAnchor> trustAnchors;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/PKIXExtendedParameters$Builder.class */
    public static class Builder {
        private final PKIXParameters baseParameters;
        private final Date validityDate;
        private final Date date;
        private PKIXCertStoreSelector targetConstraints;
        private List<PKIXCertStore> extraCertStores;
        private Map<GeneralName, PKIXCertStore> namedCertificateStoreMap;
        private List<PKIXCRLStore> extraCRLStores;
        private Map<GeneralName, PKIXCRLStore> namedCRLStoreMap;
        private boolean revocationEnabled;
        private int validityModel;
        private boolean useDeltas;
        private Set<TrustAnchor> trustAnchors;

        public Builder(PKIXParameters pKIXParameters) {
            this.extraCertStores = new ArrayList();
            this.namedCertificateStoreMap = new HashMap();
            this.extraCRLStores = new ArrayList();
            this.namedCRLStoreMap = new HashMap();
            this.validityModel = 0;
            this.useDeltas = false;
            this.baseParameters = (PKIXParameters) pKIXParameters.clone();
            CertSelector targetCertConstraints = pKIXParameters.getTargetCertConstraints();
            if (targetCertConstraints != null) {
                this.targetConstraints = new PKIXCertStoreSelector.Builder(targetCertConstraints).build();
            }
            this.validityDate = pKIXParameters.getDate();
            this.date = this.validityDate == null ? new Date() : this.validityDate;
            this.revocationEnabled = pKIXParameters.isRevocationEnabled();
            this.trustAnchors = pKIXParameters.getTrustAnchors();
        }

        public Builder(PKIXExtendedParameters pKIXExtendedParameters) {
            this.extraCertStores = new ArrayList();
            this.namedCertificateStoreMap = new HashMap();
            this.extraCRLStores = new ArrayList();
            this.namedCRLStoreMap = new HashMap();
            this.validityModel = 0;
            this.useDeltas = false;
            this.baseParameters = pKIXExtendedParameters.baseParameters;
            this.validityDate = pKIXExtendedParameters.validityDate;
            this.date = pKIXExtendedParameters.date;
            this.targetConstraints = pKIXExtendedParameters.targetConstraints;
            this.extraCertStores = new ArrayList(pKIXExtendedParameters.extraCertStores);
            this.namedCertificateStoreMap = new HashMap(pKIXExtendedParameters.namedCertificateStoreMap);
            this.extraCRLStores = new ArrayList(pKIXExtendedParameters.extraCRLStores);
            this.namedCRLStoreMap = new HashMap(pKIXExtendedParameters.namedCRLStoreMap);
            this.useDeltas = pKIXExtendedParameters.useDeltas;
            this.validityModel = pKIXExtendedParameters.validityModel;
            this.revocationEnabled = pKIXExtendedParameters.isRevocationEnabled();
            this.trustAnchors = pKIXExtendedParameters.getTrustAnchors();
        }

        public Builder addCertificateStore(PKIXCertStore pKIXCertStore) {
            this.extraCertStores.add(pKIXCertStore);
            return this;
        }

        public Builder addNamedCertificateStore(GeneralName generalName, PKIXCertStore pKIXCertStore) {
            this.namedCertificateStoreMap.put(generalName, pKIXCertStore);
            return this;
        }

        public Builder addCRLStore(PKIXCRLStore pKIXCRLStore) {
            this.extraCRLStores.add(pKIXCRLStore);
            return this;
        }

        public Builder addNamedCRLStore(GeneralName generalName, PKIXCRLStore pKIXCRLStore) {
            this.namedCRLStoreMap.put(generalName, pKIXCRLStore);
            return this;
        }

        public Builder setTargetConstraints(PKIXCertStoreSelector pKIXCertStoreSelector) {
            this.targetConstraints = pKIXCertStoreSelector;
            return this;
        }

        public Builder setUseDeltasEnabled(boolean z) {
            this.useDeltas = z;
            return this;
        }

        public Builder setValidityModel(int i) {
            this.validityModel = i;
            return this;
        }

        public Builder setTrustAnchor(TrustAnchor trustAnchor) {
            this.trustAnchors = Collections.singleton(trustAnchor);
            return this;
        }

        public Builder setTrustAnchors(Set<TrustAnchor> set) {
            this.trustAnchors = set;
            return this;
        }

        public void setRevocationEnabled(boolean z) {
            this.revocationEnabled = z;
        }

        public PKIXExtendedParameters build() {
            return new PKIXExtendedParameters(this);
        }
    }

    private PKIXExtendedParameters(Builder builder) {
        this.baseParameters = builder.baseParameters;
        this.validityDate = builder.validityDate;
        this.date = builder.date;
        this.extraCertStores = Collections.unmodifiableList(builder.extraCertStores);
        this.namedCertificateStoreMap = Collections.unmodifiableMap(new HashMap(builder.namedCertificateStoreMap));
        this.extraCRLStores = Collections.unmodifiableList(builder.extraCRLStores);
        this.namedCRLStoreMap = Collections.unmodifiableMap(new HashMap(builder.namedCRLStoreMap));
        this.targetConstraints = builder.targetConstraints;
        this.revocationEnabled = builder.revocationEnabled;
        this.useDeltas = builder.useDeltas;
        this.validityModel = builder.validityModel;
        this.trustAnchors = Collections.unmodifiableSet(builder.trustAnchors);
    }

    public List<PKIXCertStore> getCertificateStores() {
        return this.extraCertStores;
    }

    public Map<GeneralName, PKIXCertStore> getNamedCertificateStoreMap() {
        return this.namedCertificateStoreMap;
    }

    public List<PKIXCRLStore> getCRLStores() {
        return this.extraCRLStores;
    }

    public Map<GeneralName, PKIXCRLStore> getNamedCRLStoreMap() {
        return this.namedCRLStoreMap;
    }

    public Date getValidityDate() {
        if (null == this.validityDate) {
            return null;
        }
        return new Date(this.validityDate.getTime());
    }

    public Date getDate() {
        return new Date(this.date.getTime());
    }

    public boolean isUseDeltasEnabled() {
        return this.useDeltas;
    }

    public int getValidityModel() {
        return this.validityModel;
    }

    @Override // java.security.cert.CertPathParameters
    public Object clone() {
        return this;
    }

    public PKIXCertStoreSelector getTargetConstraints() {
        return this.targetConstraints;
    }

    public Set getTrustAnchors() {
        return this.trustAnchors;
    }

    public Set getInitialPolicies() {
        return this.baseParameters.getInitialPolicies();
    }

    public String getSigProvider() {
        return this.baseParameters.getSigProvider();
    }

    public boolean isExplicitPolicyRequired() {
        return this.baseParameters.isExplicitPolicyRequired();
    }

    public boolean isAnyPolicyInhibited() {
        return this.baseParameters.isAnyPolicyInhibited();
    }

    public boolean isPolicyMappingInhibited() {
        return this.baseParameters.isPolicyMappingInhibited();
    }

    public List getCertPathCheckers() {
        return this.baseParameters.getCertPathCheckers();
    }

    public List<CertStore> getCertStores() {
        return this.baseParameters.getCertStores();
    }

    public boolean isRevocationEnabled() {
        return this.revocationEnabled;
    }

    public boolean getPolicyQualifiersRejected() {
        return this.baseParameters.getPolicyQualifiersRejected();
    }
}
