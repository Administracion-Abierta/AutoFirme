package org.spongycastle.jce.interfaces;

import org.spongycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/interfaces/GOST3410Params.class */
public interface GOST3410Params {
    String getPublicKeyParamSetOID();

    String getDigestParamSetOID();

    String getEncryptionParamSetOID();

    GOST3410PublicKeyParameterSetSpec getPublicKeyParameters();
}
