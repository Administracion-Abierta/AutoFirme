package org.bouncycastle.cert.selector;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.cert.AttributeCertificateHolder;
import org.bouncycastle.cert.AttributeCertificateIssuer;
import org.bouncycastle.cert.X509AttributeCertificateHolder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/selector/X509AttributeCertificateHolderSelectorBuilder.class */
public class X509AttributeCertificateHolderSelectorBuilder {
    private AttributeCertificateHolder holder;
    private AttributeCertificateIssuer issuer;
    private BigInteger serialNumber;
    private Date attributeCertificateValid;
    private X509AttributeCertificateHolder attributeCert;
    private Collection targetNames = new HashSet();
    private Collection targetGroups = new HashSet();

    public void setAttributeCert(X509AttributeCertificateHolder x509AttributeCertificateHolder) {
        this.attributeCert = x509AttributeCertificateHolder;
    }

    public void setAttributeCertificateValid(Date date) {
        if (date != null) {
            this.attributeCertificateValid = new Date(date.getTime());
        } else {
            this.attributeCertificateValid = null;
        }
    }

    public void setHolder(AttributeCertificateHolder attributeCertificateHolder) {
        this.holder = attributeCertificateHolder;
    }

    public void setIssuer(AttributeCertificateIssuer attributeCertificateIssuer) {
        this.issuer = attributeCertificateIssuer;
    }

    public void setSerialNumber(BigInteger bigInteger) {
        this.serialNumber = bigInteger;
    }

    public void addTargetName(GeneralName generalName) {
        this.targetNames.add(generalName);
    }

    public void setTargetNames(Collection collection) throws IOException {
        this.targetNames = extractGeneralNames(collection);
    }

    public void addTargetGroup(GeneralName generalName) {
        this.targetGroups.add(generalName);
    }

    public void setTargetGroups(Collection collection) throws IOException {
        this.targetGroups = extractGeneralNames(collection);
    }

    private Set extractGeneralNames(Collection collection) throws IOException {
        if (collection == null || collection.isEmpty()) {
            return new HashSet();
        }
        HashSet hashSet = new HashSet();
        Iterator it = collection.iterator();
        while (it.hasNext()) {
            hashSet.add(GeneralName.getInstance(it.next()));
        }
        return hashSet;
    }

    public X509AttributeCertificateHolderSelector build() {
        return new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, Collections.unmodifiableCollection(new HashSet(this.targetNames)), Collections.unmodifiableCollection(new HashSet(this.targetGroups)));
    }
}
