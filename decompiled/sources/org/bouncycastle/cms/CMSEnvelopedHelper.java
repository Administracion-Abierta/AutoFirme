package org.bouncycastle.cms;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEKRecipientInfo;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientInfo;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.OtherRecipientInfo;
import org.bouncycastle.asn1.cms.PasswordRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEnvelopedHelper.class */
class CMSEnvelopedHelper {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEnvelopedHelper$CMSAuthEnveSecureReadable.class */
    static class CMSAuthEnveSecureReadable extends CMSDefaultSecureReadable {
        private AlgorithmIdentifier algorithm;

        CMSAuthEnveSecureReadable(AlgorithmIdentifier algorithmIdentifier, ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSReadable cMSReadable) {
            super(aSN1ObjectIdentifier, cMSReadable);
            this.algorithm = algorithmIdentifier;
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public InputStream getInputStream() throws CMSException, IOException {
            return this.readable.getInputStream();
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public boolean hasAdditionalData() {
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEnvelopedHelper$CMSDefaultSecureReadable.class */
    static abstract class CMSDefaultSecureReadable implements CMSSecureReadable {
        protected final ASN1ObjectIdentifier contentType;
        protected CMSReadable readable;
        protected ASN1Set authAttrSet;

        CMSDefaultSecureReadable(ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSReadable cMSReadable) {
            this.contentType = aSN1ObjectIdentifier;
            this.readable = cMSReadable;
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public ASN1ObjectIdentifier getContentType() {
            return this.contentType;
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public ASN1Set getAuthAttrSet() {
            return this.authAttrSet;
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public void setAuthAttrSet(ASN1Set aSN1Set) {
            this.authAttrSet = aSN1Set;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSEnvelopedHelper$CMSDigestAuthenticatedSecureReadable.class */
    static class CMSDigestAuthenticatedSecureReadable extends CMSDefaultSecureReadable {
        private DigestCalculator digestCalculator;

        public CMSDigestAuthenticatedSecureReadable(DigestCalculator digestCalculator, ASN1ObjectIdentifier aSN1ObjectIdentifier, CMSReadable cMSReadable) {
            super(aSN1ObjectIdentifier, cMSReadable);
            this.digestCalculator = digestCalculator;
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public InputStream getInputStream() throws CMSException, IOException {
            return new FilterInputStream(this.readable.getInputStream()) { // from class: org.bouncycastle.cms.CMSEnvelopedHelper.CMSDigestAuthenticatedSecureReadable.1
                @Override // java.io.FilterInputStream, java.io.InputStream
                public int read() throws IOException {
                    int i = this.in.read();
                    if (i >= 0) {
                        CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(i);
                    }
                    return i;
                }

                @Override // java.io.FilterInputStream, java.io.InputStream
                public int read(byte[] bArr, int i, int i2) throws IOException {
                    int i3 = this.in.read(bArr, i, i2);
                    if (i3 >= 0) {
                        CMSDigestAuthenticatedSecureReadable.this.digestCalculator.getOutputStream().write(bArr, i, i3);
                    }
                    return i3;
                }
            };
        }

        public byte[] getDigest() {
            return this.digestCalculator.getDigest();
        }

        @Override // org.bouncycastle.cms.CMSSecureReadable
        public boolean hasAdditionalData() {
            return true;
        }
    }

    CMSEnvelopedHelper() {
    }

    static RecipientInformationStore buildRecipientInformationStore(ASN1Set aSN1Set, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i != aSN1Set.size(); i++) {
            readRecipientInfo(arrayList, RecipientInfo.getInstance(aSN1Set.getObjectAt(i)), algorithmIdentifier, cMSSecureReadable);
        }
        return new RecipientInformationStore(arrayList);
    }

    private static void readRecipientInfo(List list, RecipientInfo recipientInfo, AlgorithmIdentifier algorithmIdentifier, CMSSecureReadable cMSSecureReadable) {
        ASN1Encodable info = recipientInfo.getInfo();
        if (info instanceof KeyTransRecipientInfo) {
            list.add(new KeyTransRecipientInformation((KeyTransRecipientInfo) info, algorithmIdentifier, cMSSecureReadable));
            return;
        }
        if (info instanceof OtherRecipientInfo) {
            OtherRecipientInfo otherRecipientInfo = OtherRecipientInfo.getInstance(info);
            if (CMSObjectIdentifiers.id_ori_kem.equals((ASN1Primitive) otherRecipientInfo.getType())) {
                list.add(new KEMRecipientInformation(KEMRecipientInfo.getInstance(otherRecipientInfo.getValue()), algorithmIdentifier, cMSSecureReadable));
                return;
            }
            return;
        }
        if (info instanceof KEKRecipientInfo) {
            list.add(new KEKRecipientInformation((KEKRecipientInfo) info, algorithmIdentifier, cMSSecureReadable));
        } else if (info instanceof KeyAgreeRecipientInfo) {
            KeyAgreeRecipientInformation.readRecipientInfo(list, (KeyAgreeRecipientInfo) info, algorithmIdentifier, cMSSecureReadable);
        } else if (info instanceof PasswordRecipientInfo) {
            list.add(new PasswordRecipientInformation((PasswordRecipientInfo) info, algorithmIdentifier, cMSSecureReadable));
        }
    }
}
