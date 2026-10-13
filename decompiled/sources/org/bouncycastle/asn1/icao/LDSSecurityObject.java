package org.bouncycastle.asn1.icao;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/icao/LDSSecurityObject.class */
public class LDSSecurityObject extends ASN1Object implements ICAOObjectIdentifiers {
    public static final int ub_DataGroups = 16;
    private ASN1Integer version;
    private AlgorithmIdentifier digestAlgorithmIdentifier;
    private DataGroupHash[] datagroupHash;
    private LDSVersionInfo versionInfo;

    public static LDSSecurityObject getInstance(Object obj) {
        if (obj instanceof LDSSecurityObject) {
            return (LDSSecurityObject) obj;
        }
        if (obj != null) {
            return new LDSSecurityObject(ASN1Sequence.getInstance(obj));
        }
        return null;
    }

    private LDSSecurityObject(ASN1Sequence aSN1Sequence) {
        this.version = new ASN1Integer(0L);
        if (aSN1Sequence == null || aSN1Sequence.size() == 0) {
            throw new IllegalArgumentException("null or empty sequence passed.");
        }
        Enumeration objects = aSN1Sequence.getObjects();
        this.version = ASN1Integer.getInstance(objects.nextElement());
        this.digestAlgorithmIdentifier = AlgorithmIdentifier.getInstance(objects.nextElement());
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(objects.nextElement());
        if (this.version.hasValue(1)) {
            this.versionInfo = LDSVersionInfo.getInstance(objects.nextElement());
        }
        checkDatagroupHashSeqSize(aSN1Sequence2.size());
        this.datagroupHash = new DataGroupHash[aSN1Sequence2.size()];
        for (int i = 0; i < aSN1Sequence2.size(); i++) {
            this.datagroupHash[i] = DataGroupHash.getInstance(aSN1Sequence2.getObjectAt(i));
        }
    }

    public LDSSecurityObject(AlgorithmIdentifier algorithmIdentifier, DataGroupHash[] dataGroupHashArr) {
        this.version = new ASN1Integer(0L);
        this.version = new ASN1Integer(0L);
        this.digestAlgorithmIdentifier = algorithmIdentifier;
        this.datagroupHash = copy(dataGroupHashArr);
        checkDatagroupHashSeqSize(dataGroupHashArr.length);
    }

    public LDSSecurityObject(AlgorithmIdentifier algorithmIdentifier, DataGroupHash[] dataGroupHashArr, LDSVersionInfo lDSVersionInfo) {
        this.version = new ASN1Integer(0L);
        this.version = new ASN1Integer(1L);
        this.digestAlgorithmIdentifier = algorithmIdentifier;
        this.datagroupHash = copy(dataGroupHashArr);
        this.versionInfo = lDSVersionInfo;
        checkDatagroupHashSeqSize(dataGroupHashArr.length);
    }

    private void checkDatagroupHashSeqSize(int i) {
        if (i < 2 || i > 16) {
            throw new IllegalArgumentException("wrong size in DataGroupHashValues : not in (2..16)");
        }
    }

    public int getVersion() {
        return this.version.intValueExact();
    }

    public AlgorithmIdentifier getDigestAlgorithmIdentifier() {
        return this.digestAlgorithmIdentifier;
    }

    public DataGroupHash[] getDatagroupHash() {
        return copy(this.datagroupHash);
    }

    public LDSVersionInfo getVersionInfo() {
        return this.versionInfo;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(4);
        aSN1EncodableVector.add(this.version);
        aSN1EncodableVector.add(this.digestAlgorithmIdentifier);
        aSN1EncodableVector.add(new DERSequence(this.datagroupHash));
        if (this.versionInfo != null) {
            aSN1EncodableVector.add(this.versionInfo);
        }
        return new DERSequence(aSN1EncodableVector);
    }

    private DataGroupHash[] copy(DataGroupHash[] dataGroupHashArr) {
        DataGroupHash[] dataGroupHashArr2 = new DataGroupHash[dataGroupHashArr.length];
        System.arraycopy(dataGroupHashArr, 0, dataGroupHashArr2, 0, dataGroupHashArr2.length);
        return dataGroupHashArr2;
    }
}
