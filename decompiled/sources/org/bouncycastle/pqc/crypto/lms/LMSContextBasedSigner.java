package org.bouncycastle.pqc.crypto.lms;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/lms/LMSContextBasedSigner.class */
public interface LMSContextBasedSigner {
    LMSContext generateLMSContext();

    byte[] generateSignature(LMSContext lMSContext);

    long getUsagesRemaining();
}
