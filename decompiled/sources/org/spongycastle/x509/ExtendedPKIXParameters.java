package org.spongycastle.x509;

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
import org.spongycastle.util.Selector;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/ExtendedPKIXParameters.class */
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

    public ExtendedPKIXParameters(Set trustAnchors) throws InvalidAlgorithmParameterException {
        super((Set<TrustAnchor>) trustAnchors);
        this.validityModel = 0;
        this.useDeltas = false;
        this.stores = new ArrayList();
        this.additionalStores = new ArrayList();
        this.trustedACIssuers = new HashSet();
        this.necessaryACAttributes = new HashSet();
        this.prohibitedACAttributes = new HashSet();
        this.attrCertCheckers = new HashSet();
    }

    public static ExtendedPKIXParameters getInstance(PKIXParameters pkixParams) {
        try {
            ExtendedPKIXParameters params = new ExtendedPKIXParameters(pkixParams.getTrustAnchors());
            params.setParams(pkixParams);
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    protected void setParams(PKIXParameters params) {
        setDate(params.getDate());
        setCertPathCheckers(params.getCertPathCheckers());
        setCertStores(params.getCertStores());
        setAnyPolicyInhibited(params.isAnyPolicyInhibited());
        setExplicitPolicyRequired(params.isExplicitPolicyRequired());
        setPolicyMappingInhibited(params.isPolicyMappingInhibited());
        setRevocationEnabled(params.isRevocationEnabled());
        setInitialPolicies(params.getInitialPolicies());
        setPolicyQualifiersRejected(params.getPolicyQualifiersRejected());
        setSigProvider(params.getSigProvider());
        setTargetCertConstraints(params.getTargetCertConstraints());
        try {
            setTrustAnchors(params.getTrustAnchors());
            if (params instanceof ExtendedPKIXParameters) {
                ExtendedPKIXParameters _params = (ExtendedPKIXParameters) params;
                this.validityModel = _params.validityModel;
                this.useDeltas = _params.useDeltas;
                this.additionalLocationsEnabled = _params.additionalLocationsEnabled;
                this.selector = _params.selector == null ? null : (Selector) _params.selector.clone();
                this.stores = new ArrayList(_params.stores);
                this.additionalStores = new ArrayList(_params.additionalStores);
                this.trustedACIssuers = new HashSet(_params.trustedACIssuers);
                this.prohibitedACAttributes = new HashSet(_params.prohibitedACAttributes);
                this.necessaryACAttributes = new HashSet(_params.necessaryACAttributes);
                this.attrCertCheckers = new HashSet(_params.attrCertCheckers);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isUseDeltasEnabled() {
        return this.useDeltas;
    }

    public void setUseDeltasEnabled(boolean useDeltas) {
        this.useDeltas = useDeltas;
    }

    public int getValidityModel() {
        return this.validityModel;
    }

    @Override // java.security.cert.PKIXParameters
    public void setCertStores(List stores) {
        if (stores != null) {
            Iterator it = stores.iterator();
            while (it.hasNext()) {
                addCertStore((CertStore) it.next());
            }
        }
    }

    public void setStores(List stores) {
        if (stores == null) {
            this.stores = new ArrayList();
            return;
        }
        Iterator i = stores.iterator();
        while (i.hasNext()) {
            if (!(i.next() instanceof Store)) {
                throw new ClassCastException("All elements of list must be of type org.spongycastle.util.Store.");
            }
        }
        this.stores = new ArrayList(stores);
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

    public void setValidityModel(int validityModel) {
        this.validityModel = validityModel;
    }

    @Override // java.security.cert.PKIXParameters, java.security.cert.CertPathParameters
    public Object clone() {
        try {
            ExtendedPKIXParameters params = new ExtendedPKIXParameters(getTrustAnchors());
            params.setParams(this);
            return params;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public boolean isAdditionalLocationsEnabled() {
        return this.additionalLocationsEnabled;
    }

    public void setAdditionalLocationsEnabled(boolean enabled) {
        this.additionalLocationsEnabled = enabled;
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
    public void setTargetCertConstraints(CertSelector selector) {
        super.setTargetCertConstraints(selector);
        if (selector != null) {
            this.selector = X509CertStoreSelector.getInstance((X509CertSelector) selector);
        } else {
            this.selector = null;
        }
    }

    public Set getTrustedACIssuers() {
        return Collections.unmodifiableSet(this.trustedACIssuers);
    }

    public void setTrustedACIssuers(Set trustedACIssuers) {
        if (trustedACIssuers == null) {
            this.trustedACIssuers.clear();
            return;
        }
        Iterator it = trustedACIssuers.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof TrustAnchor)) {
                throw new ClassCastException("All elements of set must be of type " + TrustAnchor.class.getName() + ".");
            }
        }
        this.trustedACIssuers.clear();
        this.trustedACIssuers.addAll(trustedACIssuers);
    }

    public Set getNecessaryACAttributes() {
        return Collections.unmodifiableSet(this.necessaryACAttributes);
    }

    public void setNecessaryACAttributes(Set necessaryACAttributes) {
        if (necessaryACAttributes == null) {
            this.necessaryACAttributes.clear();
            return;
        }
        Iterator it = necessaryACAttributes.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof String)) {
                throw new ClassCastException("All elements of set must be of type String.");
            }
        }
        this.necessaryACAttributes.clear();
        this.necessaryACAttributes.addAll(necessaryACAttributes);
    }

    public Set getProhibitedACAttributes() {
        return Collections.unmodifiableSet(this.prohibitedACAttributes);
    }

    public void setProhibitedACAttributes(Set prohibitedACAttributes) {
        if (prohibitedACAttributes == null) {
            this.prohibitedACAttributes.clear();
            return;
        }
        Iterator it = prohibitedACAttributes.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof String)) {
                throw new ClassCastException("All elements of set must be of type String.");
            }
        }
        this.prohibitedACAttributes.clear();
        this.prohibitedACAttributes.addAll(prohibitedACAttributes);
    }

    public Set getAttrCertCheckers() {
        return Collections.unmodifiableSet(this.attrCertCheckers);
    }

    public void setAttrCertCheckers(Set attrCertCheckers) {
        if (attrCertCheckers == null) {
            this.attrCertCheckers.clear();
            return;
        }
        Iterator it = attrCertCheckers.iterator();
        while (it.hasNext()) {
            if (!(it.next() instanceof PKIXAttrCertChecker)) {
                throw new ClassCastException("All elements of set must be of type " + PKIXAttrCertChecker.class.getName() + ".");
            }
        }
        this.attrCertCheckers.clear();
        this.attrCertCheckers.addAll(attrCertCheckers);
    }
}
