package org.spongycastle.cert.path;

import java.util.HashSet;
import java.util.Set;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.util.Memoable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/path/CertPathValidationContext.class */
public class CertPathValidationContext implements Memoable {
    private Set criticalExtensions;
    private Set handledExtensions = new HashSet();
    private boolean endEntity;
    private int index;

    public CertPathValidationContext(Set criticalExtensionsOIDs) {
        this.criticalExtensions = criticalExtensionsOIDs;
    }

    public void addHandledExtension(ASN1ObjectIdentifier extensionIdentifier) {
        this.handledExtensions.add(extensionIdentifier);
    }

    public void setIsEndEntity(boolean isEndEntity) {
        this.endEntity = isEndEntity;
    }

    public Set getUnhandledCriticalExtensionOIDs() {
        Set rv = new HashSet(this.criticalExtensions);
        rv.removeAll(this.handledExtensions);
        return rv;
    }

    public boolean isEndEntity() {
        return this.endEntity;
    }

    @Override // org.spongycastle.util.Memoable
    public Memoable copy() {
        return null;
    }

    @Override // org.spongycastle.util.Memoable
    public void reset(Memoable other) {
    }
}
