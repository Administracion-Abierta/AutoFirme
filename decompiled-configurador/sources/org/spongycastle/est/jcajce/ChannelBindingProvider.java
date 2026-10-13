package org.spongycastle.est.jcajce;

import java.net.Socket;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/jcajce/ChannelBindingProvider.class */
public interface ChannelBindingProvider {
    boolean canAccessChannelBinding(Socket socket);

    byte[] getChannelBinding(Socket socket, String str);
}
