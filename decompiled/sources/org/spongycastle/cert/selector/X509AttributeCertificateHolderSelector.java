package org.spongycastle.cert.selector;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.Target;
import org.spongycastle.asn1.x509.TargetInformation;
import org.spongycastle.asn1.x509.Targets;
import org.spongycastle.cert.AttributeCertificateHolder;
import org.spongycastle.cert.AttributeCertificateIssuer;
import org.spongycastle.cert.X509AttributeCertificateHolder;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/selector/X509AttributeCertificateHolderSelector.class */
public class X509AttributeCertificateHolderSelector implements Selector {
    private final AttributeCertificateHolder holder;
    private final AttributeCertificateIssuer issuer;
    private final BigInteger serialNumber;
    private final Date attributeCertificateValid;
    private final X509AttributeCertificateHolder attributeCert;
    private final Collection targetNames;
    private final Collection targetGroups;

    X509AttributeCertificateHolderSelector(AttributeCertificateHolder holder, AttributeCertificateIssuer issuer, BigInteger serialNumber, Date attributeCertificateValid, X509AttributeCertificateHolder attributeCert, Collection targetNames, Collection targetGroups) {
        this.holder = holder;
        this.issuer = issuer;
        this.serialNumber = serialNumber;
        this.attributeCertificateValid = attributeCertificateValid;
        this.attributeCert = attributeCert;
        this.targetNames = targetNames;
        this.targetGroups = targetGroups;
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        Extension targetInfoExt;
        if (!(obj instanceof X509AttributeCertificateHolder)) {
            return false;
        }
        X509AttributeCertificateHolder attrCert = (X509AttributeCertificateHolder) obj;
        if (this.attributeCert != null && !this.attributeCert.equals(attrCert)) {
            return false;
        }
        if (this.serialNumber != null && !attrCert.getSerialNumber().equals(this.serialNumber)) {
            return false;
        }
        if (this.holder != null && !attrCert.getHolder().equals(this.holder)) {
            return false;
        }
        if (this.issuer != null && !attrCert.getIssuer().equals(this.issuer)) {
            return false;
        }
        if (this.attributeCertificateValid != null && !attrCert.isValidOn(this.attributeCertificateValid)) {
            return false;
        }
        if ((!this.targetNames.isEmpty() || !this.targetGroups.isEmpty()) && (targetInfoExt = attrCert.getExtension(Extension.targetInformation)) != null) {
            try {
                TargetInformation targetinfo = TargetInformation.getInstance(targetInfoExt.getParsedValue());
                Targets[] targetss = targetinfo.getTargetsObjects();
                if (!this.targetNames.isEmpty()) {
                    boolean found = false;
                    for (Targets t : targetss) {
                        for (Target target : t.getTargets()) {
                            if (this.targetNames.contains(GeneralName.getInstance(target.getTargetName()))) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        return false;
                    }
                }
                if (!this.targetGroups.isEmpty()) {
                    boolean found2 = false;
                    for (Targets t2 : targetss) {
                        Target[] targets = t2.getTargets();
                        for (Target target2 : targets) {
                            if (this.targetGroups.contains(GeneralName.getInstance(target2.getTargetGroup()))) {
                                found2 = true;
                                break;
                            }
                        }
                    }
                    if (!found2) {
                        return false;
                    }
                    return true;
                }
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        X509AttributeCertificateHolderSelector sel = new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, this.targetNames, this.targetGroups);
        return sel;
    }

    public X509AttributeCertificateHolder getAttributeCert() {
        return this.attributeCert;
    }

    public Date getAttributeCertificateValid() {
        if (this.attributeCertificateValid != null) {
            return new Date(this.attributeCertificateValid.getTime());
        }
        return null;
    }

    public AttributeCertificateHolder getHolder() {
        return this.holder;
    }

    public AttributeCertificateIssuer getIssuer() {
        return this.issuer;
    }

    public BigInteger getSerialNumber() {
        return this.serialNumber;
    }

    public Collection getTargetNames() {
        return this.targetNames;
    }

    public Collection getTargetGroups() {
        return this.targetGroups;
    }
}
