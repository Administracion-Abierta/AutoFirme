package org.apache.pdfbox.filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/CryptFilter.class */
final class CryptFilter extends Filter {
    CryptFilter() {
    }

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        COSName encryptionName = (COSName) parameters.getDictionaryObject(COSName.NAME);
        if (encryptionName == null || encryptionName.equals(COSName.IDENTITY)) {
            Filter identityFilter = new IdentityFilter();
            identityFilter.decode(encoded, decoded, parameters, index);
            return new DecodeResult(parameters);
        }
        throw new IOException("Unsupported crypt filter " + encryptionName.getName());
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream input, OutputStream encoded, COSDictionary parameters) throws IOException {
        COSName encryptionName = (COSName) parameters.getDictionaryObject(COSName.NAME);
        if (encryptionName == null || encryptionName.equals(COSName.IDENTITY)) {
            Filter identityFilter = new IdentityFilter();
            identityFilter.encode(input, encoded, parameters);
            return;
        }
        throw new IOException("Unsupported crypt filter " + encryptionName.getName());
    }
}
