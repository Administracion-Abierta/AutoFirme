package org.spongycastle.cert.dane;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.util.Strings;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/dane/DANEEntrySelectorFactory.class */
public class DANEEntrySelectorFactory {
    private final DigestCalculator digestCalculator;

    public DANEEntrySelectorFactory(DigestCalculator digestCalculator) {
        this.digestCalculator = digestCalculator;
    }

    public DANEEntrySelector createSelector(String emailAddress) throws DANEException {
        byte[] enc = Strings.toUTF8ByteArray(emailAddress.substring(0, emailAddress.indexOf(64)));
        try {
            OutputStream cOut = this.digestCalculator.getOutputStream();
            cOut.write(enc);
            cOut.close();
            byte[] hash = this.digestCalculator.getDigest();
            String domainName = Strings.fromByteArray(Hex.encode(hash)) + "._smimecert." + emailAddress.substring(emailAddress.indexOf(64) + 1);
            return new DANEEntrySelector(domainName);
        } catch (IOException e) {
            throw new DANEException("Unable to calculate digest string: " + e.getMessage(), e);
        }
    }
}
