package org.spongycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.cms.SignedData;
import org.spongycastle.asn1.cms.SignerInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSSignedDataGenerator.class */
public class CMSSignedDataGenerator extends CMSSignedGenerator {
    private List signerInfs = new ArrayList();

    public CMSSignedData generate(CMSTypedData content) throws CMSException {
        return generate(content, false);
    }

    public CMSSignedData generate(CMSTypedData content, boolean encapsulate) throws CMSException {
        if (!this.signerInfs.isEmpty()) {
            throw new IllegalStateException("this method can only be used with SignerInfoGenerator");
        }
        ASN1EncodableVector digestAlgs = new ASN1EncodableVector();
        ASN1EncodableVector signerInfos = new ASN1EncodableVector();
        this.digests.clear();
        for (SignerInformation signer : this._signers) {
            digestAlgs.add(CMSSignedHelper.INSTANCE.fixAlgID(signer.getDigestAlgorithmID()));
            signerInfos.add(signer.toASN1Structure());
        }
        ASN1ObjectIdentifier contentTypeOID = content.getContentType();
        ASN1OctetString octs = null;
        if (content.getContent() != null) {
            ByteArrayOutputStream bOut = null;
            if (encapsulate) {
                bOut = new ByteArrayOutputStream();
            }
            OutputStream cOut = CMSUtils.getSafeOutputStream(CMSUtils.attachSignersToOutputStream(this.signerGens, bOut));
            try {
                content.write(cOut);
                cOut.close();
                if (encapsulate) {
                    octs = new BEROctetString(bOut.toByteArray());
                }
            } catch (IOException e) {
                throw new CMSException("data processing exception: " + e.getMessage(), e);
            }
        }
        for (SignerInfoGenerator sGen : this.signerGens) {
            SignerInfo inf = sGen.generate(contentTypeOID);
            digestAlgs.add(inf.getDigestAlgorithm());
            signerInfos.add(inf);
            byte[] calcDigest = sGen.getCalculatedDigest();
            if (calcDigest != null) {
                this.digests.put(inf.getDigestAlgorithm().getAlgorithm().getId(), calcDigest);
            }
        }
        ASN1Set certificates = null;
        if (this.certs.size() != 0) {
            certificates = CMSUtils.createBerSetFromList(this.certs);
        }
        ASN1Set certrevlist = null;
        if (this.crls.size() != 0) {
            certrevlist = CMSUtils.createBerSetFromList(this.crls);
        }
        ContentInfo encInfo = new ContentInfo(contentTypeOID, octs);
        SignedData sd = new SignedData(new DERSet(digestAlgs), encInfo, certificates, certrevlist, new DERSet(signerInfos));
        ContentInfo contentInfo = new ContentInfo(CMSObjectIdentifiers.signedData, sd);
        return new CMSSignedData(content, contentInfo);
    }

    public SignerInformationStore generateCounterSigners(SignerInformation signer) throws CMSException {
        return generate(new CMSProcessableByteArray(null, signer.getSignature()), false).getSignerInfos();
    }
}
