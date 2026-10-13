package org.spongycastle.cert;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.Holder;
import org.spongycastle.asn1.x509.IssuerSerial;
import org.spongycastle.asn1.x509.ObjectDigestInfo;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Selector;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/AttributeCertificateHolder.class */
public class AttributeCertificateHolder implements Selector {
    private static DigestCalculatorProvider digestCalculatorProvider;
    final Holder holder;

    AttributeCertificateHolder(ASN1Sequence seq) {
        this.holder = Holder.getInstance(seq);
    }

    public AttributeCertificateHolder(X500Name issuerName, BigInteger serialNumber) {
        this.holder = new Holder(new IssuerSerial(generateGeneralNames(issuerName), new ASN1Integer(serialNumber)));
    }

    public AttributeCertificateHolder(X509CertificateHolder cert) {
        this.holder = new Holder(new IssuerSerial(generateGeneralNames(cert.getIssuer()), new ASN1Integer(cert.getSerialNumber())));
    }

    public AttributeCertificateHolder(X500Name principal) {
        this.holder = new Holder(generateGeneralNames(principal));
    }

    public AttributeCertificateHolder(int digestedObjectType, ASN1ObjectIdentifier digestAlgorithm, ASN1ObjectIdentifier otherObjectTypeID, byte[] objectDigest) {
        this.holder = new Holder(new ObjectDigestInfo(digestedObjectType, otherObjectTypeID, new AlgorithmIdentifier(digestAlgorithm), Arrays.clone(objectDigest)));
    }

    public int getDigestedObjectType() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getDigestedObjectType().getValue().intValue();
        }
        return -1;
    }

    public AlgorithmIdentifier getDigestAlgorithm() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getDigestAlgorithm();
        }
        return null;
    }

    public byte[] getObjectDigest() {
        if (this.holder.getObjectDigestInfo() != null) {
            return this.holder.getObjectDigestInfo().getObjectDigest().getBytes();
        }
        return null;
    }

    public ASN1ObjectIdentifier getOtherObjectTypeID() {
        if (this.holder.getObjectDigestInfo() != null) {
            new ASN1ObjectIdentifier(this.holder.getObjectDigestInfo().getOtherObjectTypeID().getId());
            return null;
        }
        return null;
    }

    private GeneralNames generateGeneralNames(X500Name principal) {
        return new GeneralNames(new GeneralName(principal));
    }

    private boolean matchesDN(X500Name subject, GeneralNames targets) {
        GeneralName[] names = targets.getNames();
        for (int i = 0; i != names.length; i++) {
            GeneralName gn = names[i];
            if (gn.getTagNo() == 4 && X500Name.getInstance(gn.getName()).equals(subject)) {
                return true;
            }
        }
        return false;
    }

    private X500Name[] getPrincipals(GeneralName[] names) {
        List l = new ArrayList(names.length);
        for (int i = 0; i != names.length; i++) {
            if (names[i].getTagNo() == 4) {
                l.add(X500Name.getInstance(names[i].getName()));
            }
        }
        return (X500Name[]) l.toArray(new X500Name[l.size()]);
    }

    public X500Name[] getEntityNames() {
        if (this.holder.getEntityName() != null) {
            return getPrincipals(this.holder.getEntityName().getNames());
        }
        return null;
    }

    public X500Name[] getIssuer() {
        if (this.holder.getBaseCertificateID() != null) {
            return getPrincipals(this.holder.getBaseCertificateID().getIssuer().getNames());
        }
        return null;
    }

    public BigInteger getSerialNumber() {
        if (this.holder.getBaseCertificateID() != null) {
            return this.holder.getBaseCertificateID().getSerial().getValue();
        }
        return null;
    }

    @Override // org.spongycastle.util.Selector
    public Object clone() {
        return new AttributeCertificateHolder((ASN1Sequence) this.holder.toASN1Primitive());
    }

    @Override // org.spongycastle.util.Selector
    public boolean match(Object obj) {
        if (!(obj instanceof X509CertificateHolder)) {
            return false;
        }
        X509CertificateHolder x509Cert = (X509CertificateHolder) obj;
        if (this.holder.getBaseCertificateID() != null) {
            return this.holder.getBaseCertificateID().getSerial().getValue().equals(x509Cert.getSerialNumber()) && matchesDN(x509Cert.getIssuer(), this.holder.getBaseCertificateID().getIssuer());
        }
        if (this.holder.getEntityName() != null && matchesDN(x509Cert.getSubject(), this.holder.getEntityName())) {
            return true;
        }
        if (this.holder.getObjectDigestInfo() != null) {
            try {
                DigestCalculator digCalc = digestCalculatorProvider.get(this.holder.getObjectDigestInfo().getDigestAlgorithm());
                OutputStream digOut = digCalc.getOutputStream();
                switch (getDigestedObjectType()) {
                    case 0:
                        digOut.write(x509Cert.getSubjectPublicKeyInfo().getEncoded());
                        break;
                    case 1:
                        digOut.write(x509Cert.getEncoded());
                        break;
                }
                digOut.close();
                if (!Arrays.areEqual(digCalc.getDigest(), getObjectDigest())) {
                    return false;
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AttributeCertificateHolder)) {
            return false;
        }
        AttributeCertificateHolder other = (AttributeCertificateHolder) obj;
        return this.holder.equals(other.holder);
    }

    public int hashCode() {
        return this.holder.hashCode();
    }

    public static void setDigestCalculatorProvider(DigestCalculatorProvider digCalcProvider) {
        digestCalculatorProvider = digCalcProvider;
    }
}
