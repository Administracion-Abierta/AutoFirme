package org.spongycastle.dvcs;

import java.io.IOException;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignedDataGenerator;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/dvcs/SignedDVCSMessageGenerator.class */
public class SignedDVCSMessageGenerator {
    private final CMSSignedDataGenerator signedDataGen;

    public SignedDVCSMessageGenerator(CMSSignedDataGenerator signedDataGen) {
        this.signedDataGen = signedDataGen;
    }

    public CMSSignedData build(DVCSMessage message) throws DVCSException {
        try {
            byte[] encapsulatedData = message.getContent().toASN1Primitive().getEncoded("DER");
            return this.signedDataGen.generate(new CMSProcessableByteArray(message.getContentType(), encapsulatedData), true);
        } catch (IOException e) {
            throw new DVCSException("Could not encode DVCS request", e);
        } catch (CMSException e2) {
            throw new DVCSException("Could not sign DVCS request", e2);
        }
    }
}
