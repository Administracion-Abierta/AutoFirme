package org.spongycastle.cert.selector;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.cert.AttributeCertificateHolder;
import org.spongycastle.cert.AttributeCertificateIssuer;
import org.spongycastle.cert.X509AttributeCertificateHolder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/selector/X509AttributeCertificateHolderSelectorBuilder.class */
public class X509AttributeCertificateHolderSelectorBuilder {
    private AttributeCertificateHolder holder;
    private AttributeCertificateIssuer issuer;
    private BigInteger serialNumber;
    private Date attributeCertificateValid;
    private X509AttributeCertificateHolder attributeCert;
    private Collection targetNames = new HashSet();
    private Collection targetGroups = new HashSet();

    public void setAttributeCert(X509AttributeCertificateHolder attributeCert) {
        this.attributeCert = attributeCert;
    }

    public void setAttributeCertificateValid(Date attributeCertificateValid) {
        if (attributeCertificateValid != null) {
            this.attributeCertificateValid = new Date(attributeCertificateValid.getTime());
        } else {
            this.attributeCertificateValid = null;
        }
    }

    public void setHolder(AttributeCertificateHolder holder) {
        this.holder = holder;
    }

    public void setIssuer(AttributeCertificateIssuer issuer) {
        this.issuer = issuer;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void addTargetName(GeneralName name) {
        this.targetNames.add(name);
    }

    public void setTargetNames(Collection names) throws IOException {
        this.targetNames = extractGeneralNames(names);
    }

    public void addTargetGroup(GeneralName group) {
        this.targetGroups.add(group);
    }

    public void setTargetGroups(Collection names) throws IOException {
        this.targetGroups = extractGeneralNames(names);
    }

    private Set extractGeneralNames(Collection names) throws IOException {
        if (names == null || names.isEmpty()) {
            return new HashSet();
        }
        Set temp = new HashSet();
        Iterator it = names.iterator();
        while (it.hasNext()) {
            temp.add(GeneralName.getInstance(it.next()));
        }
        return temp;
    }

    public X509AttributeCertificateHolderSelector build() {
        X509AttributeCertificateHolderSelector sel = new X509AttributeCertificateHolderSelector(this.holder, this.issuer, this.serialNumber, this.attributeCertificateValid, this.attributeCert, Collections.unmodifiableCollection(new HashSet(this.targetNames)), Collections.unmodifiableCollection(new HashSet(this.targetGroups)));
        return sel;
    }
}
