package org.bouncycastle.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.cms.SignedData;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSSignedDataGenerator.class */
public class CMSSignedDataGenerator extends CMSSignedGenerator {
    private boolean isDefiniteLength;

    public CMSSignedDataGenerator() {
        this.isDefiniteLength = false;
    }

    public CMSSignedDataGenerator(DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder) {
        super(digestAlgorithmIdentifierFinder);
        this.isDefiniteLength = false;
    }

    public void setDefiniteLengthEncoding(boolean z) {
        this.isDefiniteLength = z;
    }

    public CMSSignedData generate(CMSTypedData cMSTypedData) throws CMSException {
        return generate(cMSTypedData, false);
    }

    public CMSSignedData generate(CMSTypedData cMSTypedData, boolean z) throws CMSException {
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        this.digests.clear();
        for (SignerInformation signerInformation : this._signers) {
            CMSUtils.addDigestAlgs(linkedHashSet, signerInformation, this.digestAlgIdFinder);
            aSN1EncodableVector.add(signerInformation.toASN1Structure());
        }
        ASN1ObjectIdentifier contentType = cMSTypedData.getContentType();
        ASN1Encodable dEROctetString = null;
        if (cMSTypedData.getContent() != null) {
            if (z) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                writeContentViaSignerGens(cMSTypedData, byteArrayOutputStream);
                dEROctetString = this.isDefiniteLength ? new DEROctetString(byteArrayOutputStream.toByteArray()) : new BEROctetString(byteArrayOutputStream.toByteArray());
            } else {
                writeContentViaSignerGens(cMSTypedData, null);
            }
        }
        Iterator it = this.signerGens.iterator();
        while (it.hasNext()) {
            SignerInfo signerInfoGenerateSignerInfo = generateSignerInfo((SignerInfoGenerator) it.next(), contentType);
            linkedHashSet.add(signerInfoGenerateSignerInfo.getDigestAlgorithm());
            aSN1EncodableVector.add(signerInfoGenerateSignerInfo);
        }
        return new CMSSignedData(cMSTypedData, new ContentInfo(CMSObjectIdentifiers.signedData, new SignedData(CMSUtils.convertToDlSet(linkedHashSet), new ContentInfo(contentType, dEROctetString), createSetFromList(this.certs, this.isDefiniteLength), createSetFromList(this.crls, this.isDefiniteLength), new DERSet(aSN1EncodableVector))));
    }

    public SignerInformationStore generateCounterSigners(SignerInformation signerInformation) throws CMSException {
        this.digests.clear();
        CMSProcessableByteArray cMSProcessableByteArray = new CMSProcessableByteArray(null, signerInformation.getSignature());
        ArrayList arrayList = new ArrayList();
        Iterator it = this._signers.iterator();
        while (it.hasNext()) {
            arrayList.add(new SignerInformation(((SignerInformation) it.next()).toASN1Structure(), null, cMSProcessableByteArray, null));
        }
        writeContentViaSignerGens(cMSProcessableByteArray, null);
        Iterator it2 = this.signerGens.iterator();
        while (it2.hasNext()) {
            arrayList.add(new SignerInformation(generateSignerInfo((SignerInfoGenerator) it2.next(), null), null, cMSProcessableByteArray, null));
        }
        return new SignerInformationStore(arrayList);
    }

    private SignerInfo generateSignerInfo(SignerInfoGenerator signerInfoGenerator, ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        SignerInfo signerInfoGenerate = signerInfoGenerator.generate(aSN1ObjectIdentifier);
        byte[] calculatedDigest = signerInfoGenerator.getCalculatedDigest();
        if (calculatedDigest != null) {
            this.digests.put(signerInfoGenerate.getDigestAlgorithm().getAlgorithm().getId(), calculatedDigest);
        }
        return signerInfoGenerate;
    }

    private void writeContentViaSignerGens(CMSTypedData cMSTypedData, OutputStream outputStream) throws CMSException {
        OutputStream safeOutputStream = CMSUtils.getSafeOutputStream(CMSUtils.attachSignersToOutputStream(this.signerGens, outputStream));
        try {
            cMSTypedData.write(safeOutputStream);
            safeOutputStream.close();
        } catch (IOException e) {
            throw new CMSException("data processing exception: " + e.getMessage(), e);
        }
    }

    private static ASN1Set createSetFromList(List list, boolean z) {
        if (list.size() < 1) {
            return null;
        }
        return z ? CMSUtils.createDlSetFromList(list) : CMSUtils.createBerSetFromList(list);
    }
}
