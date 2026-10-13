package org.spongycastle.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.OtherRevocationInfoFormat;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.spongycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.cert.X509AttributeCertificateHolder;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Store;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSSignedGenerator.class */
public class CMSSignedGenerator {
    public static final String DATA = CMSObjectIdentifiers.data.getId();
    public static final String DIGEST_SHA1 = OIWObjectIdentifiers.idSHA1.getId();
    public static final String DIGEST_SHA224 = NISTObjectIdentifiers.id_sha224.getId();
    public static final String DIGEST_SHA256 = NISTObjectIdentifiers.id_sha256.getId();
    public static final String DIGEST_SHA384 = NISTObjectIdentifiers.id_sha384.getId();
    public static final String DIGEST_SHA512 = NISTObjectIdentifiers.id_sha512.getId();
    public static final String DIGEST_MD5 = PKCSObjectIdentifiers.md5.getId();
    public static final String DIGEST_GOST3411 = CryptoProObjectIdentifiers.gostR3411.getId();
    public static final String DIGEST_RIPEMD128 = TeleTrusTObjectIdentifiers.ripemd128.getId();
    public static final String DIGEST_RIPEMD160 = TeleTrusTObjectIdentifiers.ripemd160.getId();
    public static final String DIGEST_RIPEMD256 = TeleTrusTObjectIdentifiers.ripemd256.getId();
    public static final String ENCRYPTION_RSA = PKCSObjectIdentifiers.rsaEncryption.getId();
    public static final String ENCRYPTION_DSA = X9ObjectIdentifiers.id_dsa_with_sha1.getId();
    public static final String ENCRYPTION_ECDSA = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
    public static final String ENCRYPTION_RSA_PSS = PKCSObjectIdentifiers.id_RSASSA_PSS.getId();
    public static final String ENCRYPTION_GOST3410 = CryptoProObjectIdentifiers.gostR3410_94.getId();
    public static final String ENCRYPTION_ECGOST3410 = CryptoProObjectIdentifiers.gostR3410_2001.getId();
    public static final String ENCRYPTION_ECGOST3410_2012_256 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256.getId();
    public static final String ENCRYPTION_ECGOST3410_2012_512 = RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512.getId();
    private static final String ENCRYPTION_ECDSA_WITH_SHA1 = X9ObjectIdentifiers.ecdsa_with_SHA1.getId();
    private static final String ENCRYPTION_ECDSA_WITH_SHA224 = X9ObjectIdentifiers.ecdsa_with_SHA224.getId();
    private static final String ENCRYPTION_ECDSA_WITH_SHA256 = X9ObjectIdentifiers.ecdsa_with_SHA256.getId();
    private static final String ENCRYPTION_ECDSA_WITH_SHA384 = X9ObjectIdentifiers.ecdsa_with_SHA384.getId();
    private static final String ENCRYPTION_ECDSA_WITH_SHA512 = X9ObjectIdentifiers.ecdsa_with_SHA512.getId();
    private static final Set NO_PARAMS = new HashSet();
    private static final Map EC_ALGORITHMS = new HashMap();
    protected List certs = new ArrayList();
    protected List crls = new ArrayList();
    protected List _signers = new ArrayList();
    protected List signerGens = new ArrayList();
    protected Map digests = new HashMap();

    static {
        NO_PARAMS.add(ENCRYPTION_DSA);
        NO_PARAMS.add(ENCRYPTION_ECDSA);
        NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA1);
        NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA224);
        NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA256);
        NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA384);
        NO_PARAMS.add(ENCRYPTION_ECDSA_WITH_SHA512);
        EC_ALGORITHMS.put(DIGEST_SHA1, ENCRYPTION_ECDSA_WITH_SHA1);
        EC_ALGORITHMS.put(DIGEST_SHA224, ENCRYPTION_ECDSA_WITH_SHA224);
        EC_ALGORITHMS.put(DIGEST_SHA256, ENCRYPTION_ECDSA_WITH_SHA256);
        EC_ALGORITHMS.put(DIGEST_SHA384, ENCRYPTION_ECDSA_WITH_SHA384);
        EC_ALGORITHMS.put(DIGEST_SHA512, ENCRYPTION_ECDSA_WITH_SHA512);
    }

    protected CMSSignedGenerator() {
    }

    protected Map getBaseParameters(ASN1ObjectIdentifier contentType, AlgorithmIdentifier digAlgId, byte[] hash) {
        Map param = new HashMap();
        param.put("contentType", contentType);
        param.put("digestAlgID", digAlgId);
        param.put("digest", Arrays.clone(hash));
        return param;
    }

    public void addCertificate(X509CertificateHolder certificate) throws CMSException {
        this.certs.add(certificate.toASN1Structure());
    }

    public void addCertificates(Store certStore) throws CMSException {
        this.certs.addAll(CMSUtils.getCertificatesFromStore(certStore));
    }

    public void addCRL(X509CRLHolder crl) {
        this.crls.add(crl.toASN1Structure());
    }

    public void addCRLs(Store crlStore) throws CMSException {
        this.crls.addAll(CMSUtils.getCRLsFromStore(crlStore));
    }

    public void addAttributeCertificate(X509AttributeCertificateHolder attrCert) throws CMSException {
        this.certs.add(new DERTaggedObject(false, 2, attrCert.toASN1Structure()));
    }

    public void addAttributeCertificates(Store attrStore) throws CMSException {
        this.certs.addAll(CMSUtils.getAttributeCertificatesFromStore(attrStore));
    }

    public void addOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat, ASN1Encodable otherRevocationInfo) {
        this.crls.add(new DERTaggedObject(false, 1, new OtherRevocationInfoFormat(otherRevocationInfoFormat, otherRevocationInfo)));
    }

    public void addOtherRevocationInfo(ASN1ObjectIdentifier otherRevocationInfoFormat, Store otherRevocationInfos) {
        this.crls.addAll(CMSUtils.getOthersFromStore(otherRevocationInfoFormat, otherRevocationInfos));
    }

    public void addSigners(SignerInformationStore signerStore) {
        Iterator<SignerInformation> it = signerStore.getSigners().iterator();
        while (it.hasNext()) {
            this._signers.add(it.next());
        }
    }

    public void addSignerInfoGenerator(SignerInfoGenerator infoGen) {
        this.signerGens.add(infoGen);
    }

    public Map getGeneratedDigests() {
        return new HashMap(this.digests);
    }
}
