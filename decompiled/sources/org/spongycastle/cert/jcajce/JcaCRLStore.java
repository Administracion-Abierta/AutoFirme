package org.spongycastle.cert.jcajce;

import java.io.IOException;
import java.security.cert.CRLException;
import java.security.cert.X509CRL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.util.CollectionStore;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/jcajce/JcaCRLStore.class */
public class JcaCRLStore extends CollectionStore {
    public JcaCRLStore(Collection collection) throws CRLException {
        super(convertCRLs(collection));
    }

    private static Collection convertCRLs(Collection collection) throws CRLException {
        List list = new ArrayList(collection.size());
        for (Object crl : collection) {
            if (crl instanceof X509CRL) {
                try {
                    list.add(new X509CRLHolder(((X509CRL) crl).getEncoded()));
                } catch (IOException e) {
                    throw new CRLException("cannot read encoding: " + e.getMessage());
                }
            } else {
                list.add((X509CRLHolder) crl);
            }
        }
        return list;
    }
}
