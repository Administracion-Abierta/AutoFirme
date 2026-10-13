package org.bouncycastle.cert.selector;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.Target;
import org.bouncycastle.asn1.x509.TargetInformation;
import org.bouncycastle.asn1.x509.Targets;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;
import org.bouncycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/X509AttributeCertificateHolderSelector.class */
public class X509AttributeCertificateHolderSelector implements Selector {
    private final AttributeCertificateHolder holder;
    private final AttributeCertificateIssuer issuer;
    private final BigInteger serialNumber;
    private final Date attributeCertificateValid;
    private final X509AttributeCertificateHolder attributeCert;
    private final Collection targetNames;
    private final Collection targetGroups;

    X509AttributeCertificateHolderSelector(AttributeCertificateHolder attributeCertificateHolder, AttributeCertificateIssuer attributeCertificateIssuer, BigInteger bigInteger, Date date, X509AttributeCertificateHolder x509AttributeCertificateHolder, Collection collection, Collection collection2) {
        this.holder = attributeCertificateHolder;
        this.issuer = attributeCertificateIssuer;
        this.serialNumber = bigInteger;
        this.attributeCertificateValid = date;
        this.attributeCert = x509AttributeCertificateHolder;
        this.targetNames = collection;
        this.targetGroups = collection2;
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(Object obj) {
        Extension extension;
        if (!(obj instanceof X509AttributeCertificateHolder)) {
            return false;
        }
        X509AttributeCertificateHolder x509AttributeCertificateHolder = (X509AttributeCertificateHolder) obj;
        if (this.attributeCert != null && !this.attributeCert.equals(x509AttributeCertificateHolder)) {
            return false;
        }
        if (this.serialNumber != null && !x509AttributeCertificateHolder.getSerialNumber().equals(this.serialNumber)) {
            return false;
        }
        if (this.holder != null && !x509AttributeCertificateHolder.getHolder().equals(this.holder)) {
            return false;
        }
        if (this.issuer != null && !x509AttributeCertificateHolder.getIssuer().equals(this.issuer)) {
            return false;
        }
        if (this.attributeCertificateValid != null && !x509AttributeCertificateHolder.isValidOn(this.attributeCertificateValid)) {
            return false;
        }
        if ((this.targetNames.isEmpty() && this.targetGroups.isEmpty()) || (extension = x509AttributeCertificateHolder.getExtension(Extension.targetInformation)) == null) {
            return true;
        }
        try {
            Targets[] targetsObjects = TargetInformation.getInstance(extension.getParsedValue()).getTargetsObjects();
            if (!this.targetNames.isEmpty()) {
                boolean z = false;
                for (Targets targets : targetsObjects) {
                    for (Target target : targets.getTargets()) {
                        if (this.targetNames.contains(GeneralName.getInstance(target.getTargetName()))) {
                            z = true;
                            break;
                        }
                    }
                }
                if (!z) {
                    return false;
                }
            }
            if (this.targetGroups.isEmpty()) {
                return true;
            }
            boolean z2 = false;
            for (Targets targets2 : targetsObjects) {
                for (Target target2 : targets2.getTargets()) {
                    if (this.targetGroups.contains(GeneralName.getInstance(target2.getTargetGroup()))) {
                        z2 = true;
                        break;
                    }
                }
            }
            return z2;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override // org.bouncycastle.util.Selector
    public Object clone() {
        return new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, this.targetNames, this.targetGroups);
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
