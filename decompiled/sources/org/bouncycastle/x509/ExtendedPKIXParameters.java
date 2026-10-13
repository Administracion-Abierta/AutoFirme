package org.bouncycastle.x509;

import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CertSelector;
import java.security.cert.CertStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/ExtendedPKIXParameters.class */
public class ExtendedPKIXParameters extends PKIXParameters {
    private List stores;
    private Selector selector;
    private boolean additionalLocationsEnabled;
    private List additionalStores;
    private Set trustedACIssuers;
    private Set necessaryACAttributes;
    private Set prohibitedACAttributes;
    private Set attrCertCheckers;
    public static final int PKIX_VALIDITY_MODEL = 0;
    public static final int CHAIN_VALIDITY_MODEL = 1;
    private int validityModel;
    private boolean useDeltas;

    public ExtendedPKIXParameters(Set set) throws InvalidAlgorithmParameterException {
        super((Set<TrustAnchor>) set);
        this.validityModel = 0;
        this.useDeltas = false;
        this.stores = new ArrayList();
        this.additionalStores = new ArrayList();
        this.trustedACIssuers = new HashSet();
        this.necessaryACAttributes = new HashSet();
        this.prohibitedACAttributes = new HashSet();
        this.attrCertCheckers = new HashSet();
    }

    public static ExtendedPKIXParameters getInstance(PKIXParameters pKIXParameters) {
        try {
            ExtendedPKIXParameters extendedPKIXParameters = new ExtendedPKIXParameters(pKIXParameters.getTrustAnchors());
            extendedPKIXParameters.setParams(pKIXParameters);
            return extendedPKIXParameters;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected void setParams(PKIXParameters pKIXParameters) {
        setDate(pKIXParameters.getDate());
        setCertPathCheckers(pKIXParameters.getCertPathCheckers());
        setCertStores(pKIXParameters.getCertStores());
        setAnyPolicyInhibited(pKIXParameters.isAnyPolicyInhibited());
        setExplicitPolicyRequired(pKIXParameters.isExplicitPolicyRequired());
        setPolicyMappingInhibited(pKIXParameters.isPolicyMappingInhibited());
        setRevocationEnabled(pKIXParameters.isRevocationEnabled());
        setInitialPolicies(pKIXParameters.getInitialPolicies());
        setPolicyQualifiersRejected(pKIXParameters.getPolicyQualifiersRejected());
        setSigProvider(pKIXParameters.getSigProvider());
        setTargetCertConstraints(pKIXParameters.getTargetCertConstraints());
        try {
            setTrustAnchors(pKIXParameters.getTrustAnchors());
            if (pKIXParameters instanceof ExtendedPKIXParameters) {
                ExtendedPKIXParameters extendedPKIXParameters = (ExtendedPKIXParameters) pKIXParameters;
                this.validityModel = extendedPKIXParameters.validityModel;
                this.useDeltas = extendedPKIXParameters.useDeltas;
                this.additionalLocationsEnabled = extendedPKIXParameters.additionalLocationsEnabled;
                this.selector = extendedPKIXParameters.selector == null ? null : (Selector) extendedPKIXParameters.selector.clone();
                this.stores = new ArrayList(extendedPKIXParameters.stores);
                this.additionalStores = new ArrayList(extendedPKIXParameters.additionalStores);
                this.trustedACIssuers = new HashSet(extendedPKIXParameters.trustedACIssuers);
                this.prohibitedACAttributes = new HashSet(extendedPKIXParameters.prohibitedACAttributes);
                this.necessaryACAttributes = new HashSet(extendedPKIXParameters.necessaryACAttributes);
                this.attrCertCheckers = new HashSet(extendedPKIXParameters.attrCertCheckers);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isUseDeltasEnabled() {
        return this.useDeltas;
    }

    public void setUseDeltasEnabled(boolean z) {
        this.useDeltas = z;
    }

    public int getValidityModel() {
        return this.validityModel;
    }

    @Override // java.security.cert.PKIXParameters
    public void setCertStores(List list) {
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                addCertStore((CertStore) it.next());
            }
        }
    }

    public void setStores(List list) {
        if (list == null) {
            this.stores = new ArrayList();
            return;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof Store)) {
                throw new ClassCastException("All elements of list must be of type org.bouncycastle.util.Store.");
            }
        }
        this.stores = new ArrayList(list);
    }

    public void addStore(Store store) {
        if (store != null) {
            this.stores.add(store);
        }
    }

    public void addAdditionalStore(Store store) {
        if (store != null) {
            this.additionalStores.add(store);
        }
    }

    public void addAddionalStore(Store store) {
        addAdditionalStore(store);
    }

    public List getAdditionalStores() {
        return Collections.unmodifiableList(this.additionalStores);
    }

    public List getStores() {
        return Collections.unmodifiableList(new ArrayList(this.stores));
    }

    public void setValidityModel(int i) {
        this.validityModel = i;
    }

    @Override // java.security.cert.PKIXParameters, java.security.cert.CertPathParameters
    public Object clone() {
        try {
            ExtendedPKIXParameters extendedPKIXParameters = new ExtendedPKIXParameters(getTrustAnchors());
            extendedPKIXParameters.setParams(this);
            return extendedPKIXParameters;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isAdditionalLocationsEnabled() {
        return this.additionalLocationsEnabled;
    }

    public void setAdditionalLocationsEnabled(boolean z) {
        this.additionalLocationsEnabled = z;
    }

    public Selector getTargetConstraints() {
        if (this.selector != null) {
            return (Selector) this.selector.clone();
        }
        return null;
    }

    public void setTargetConstraints(Selector selector) {
        if (selector != null) {
            this.selector = (Selector) selector.clone();
        } else {
            this.selector = null;
        }
    }

    @Override // java.security.cert.PKIXParameters
    public void setTargetCertConstraints(CertSelector certSelector) {
        super.setTargetCertConstraints(certSelector);
        if (certSelector != null) {
            this.selector = X509CertStoreSelector.getInstance((X509CertSelector) certSelector);
        } else {
            this.selector = null;
        }
    }

    public Set getTrustedACIssuers() {
        return Collections.unmodifiableSet(this.trustedACIssuers);
    }

    public void setTrustedACIssuers(Set set) {
        if (set == null) {
            this.trustedACIssuers.clear();
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof TrustAnchor)) {
                throw new ClassCastException("All elements of set must be of type " + TrustAnchor.class.getName() + ".");
            }
        }
        this.trustedACIssuers.clear();
        this.trustedACIssuers.addAll(set);
    }

    public Set getNecessaryACAttributes() {
        return Collections.unmodifiableSet(this.necessaryACAttributes);
    }

    public void setNecessaryACAttributes(Set set) {
        if (set == null) {
            this.necessaryACAttributes.clear();
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof String)) {
                throw new ClassCastException("All elements of set must be of type String.");
            }
        }
        this.necessaryACAttributes.clear();
        this.necessaryACAttributes.addAll(set);
    }

    public Set getProhibitedACAttributes() {
        return Collections.unmodifiableSet(this.prohibitedACAttributes);
    }

    public void setProhibitedACAttributes(Set set) {
        if (set == null) {
            this.prohibitedACAttributes.clear();
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof String)) {
                throw new ClassCastException("All elements of set must be of type String.");
            }
        }
        this.prohibitedACAttributes.clear();
        this.prohibitedACAttributes.addAll(set);
    }

    public Set getAttrCertCheckers() {
        return Collections.unmodifiableSet(this.attrCertCheckers);
    }

    public void setAttrCertCheckers(Set set) {
        if (set == null) {
            this.attrCertCheckers.clear();
            return;
        }
        Iterator it = set.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof PKIXAttrCertChecker)) {
                throw new ClassCastException("All elements of set must be of type " + PKIXAttrCertChecker.class.getName() + ".");
            }
        }
        this.attrCertCheckers.clear();
        this.attrCertCheckers.addAll(set);
    }
}
