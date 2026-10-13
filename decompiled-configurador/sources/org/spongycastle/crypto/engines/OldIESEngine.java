package org.spongycastle.crypto.engines;

import org.spongycastle.crypto.BasicAgreement;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.Mac;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/engines/OldIESEngine.class */
public class OldIESEngine extends IESEngine {
    public OldIESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac) {
        super(agree, kdf, mac);
    }

    public OldIESEngine(BasicAgreement agree, DerivationFunction kdf, Mac mac, BufferedBlockCipher cipher) {
        super(agree, kdf, mac, cipher);
    }

    @Override // org.spongycastle.crypto.engines.IESEngine
    protected byte[] getLengthTag(byte[] p2) {
        byte[] L2 = new byte[4];
        if (p2 != null) {
            Pack.intToBigEndian(p2.length * 8, L2, 0);
        }
        return L2;
    }
}
