package org.spongycastle.pkcs;

import java.io.IOException;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.asn1.pkcs.AuthenticatedSafe;
import org.spongycastle.asn1.pkcs.ContentInfo;
import org.spongycastle.asn1.pkcs.MacData;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.pkcs.Pfx;
import org.spongycastle.cms.CMSEncryptedDataGenerator;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pkcs/PKCS12PfxPduBuilder.class */
public class PKCS12PfxPduBuilder {
    private ASN1EncodableVector dataVector = new ASN1EncodableVector();

    public PKCS12PfxPduBuilder addData(PKCS12SafeBag data) throws IOException {
        this.dataVector.add(new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(new DLSequence(data.toASN1Structure()).getEncoded())));
        return this;
    }

    public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor dataEncryptor, PKCS12SafeBag data) throws IOException {
        return addEncryptedData(dataEncryptor, new DERSequence(data.toASN1Structure()));
    }

    public PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor dataEncryptor, PKCS12SafeBag[] data) throws IOException {
        ASN1EncodableVector v = new ASN1EncodableVector();
        for (int i = 0; i != data.length; i++) {
            v.add(data[i].toASN1Structure());
        }
        return addEncryptedData(dataEncryptor, new DLSequence(v));
    }

    private PKCS12PfxPduBuilder addEncryptedData(OutputEncryptor dataEncryptor, ASN1Sequence data) throws IOException {
        CMSEncryptedDataGenerator envGen = new CMSEncryptedDataGenerator();
        try {
            this.dataVector.add(envGen.generate(new CMSProcessableByteArray(data.getEncoded()), dataEncryptor).toASN1Structure());
            return this;
        } catch (CMSException e) {
            throw new PKCSIOException(e.getMessage(), e.getCause());
        }
    }

    public PKCS12PfxPdu build(PKCS12MacCalculatorBuilder macCalcBuilder, char[] password) throws PKCSException {
        AuthenticatedSafe auth = AuthenticatedSafe.getInstance(new DLSequence(this.dataVector));
        try {
            byte[] encAuth = auth.getEncoded();
            ContentInfo mainInfo = new ContentInfo(PKCSObjectIdentifiers.data, new DEROctetString(encAuth));
            MacData mData = null;
            if (macCalcBuilder != null) {
                MacDataGenerator mdGen = new MacDataGenerator(macCalcBuilder);
                mData = mdGen.build(password, encAuth);
            }
            Pfx pfx = new Pfx(mainInfo, mData);
            return new PKCS12PfxPdu(pfx);
        } catch (IOException e) {
            throw new PKCSException("unable to encode AuthenticatedSafe: " + e.getMessage(), e);
        }
    }
}
