package org.spongycastle.jce.interfaces;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/interfaces/IESKey.class */
public interface IESKey extends Key {
    PublicKey getPublic();

    PrivateKey getPrivate();
}
