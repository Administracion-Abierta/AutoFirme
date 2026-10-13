package org.bouncycastle.est.jcajce;

import java.net.Socket;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/jcajce/ChannelBindingProvider.class */
public interface ChannelBindingProvider {
    boolean canAccessChannelBinding(Socket socket);

    byte[] getChannelBinding(Socket socket, String str);
}
