package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSInputStreamData.class */
public class ERSInputStreamData extends ERSCachingData {
    private final File contentFile;
    private final byte[] contentBytes;

    public ERSInputStreamData(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("directory not allowed");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file + " not found");
        }
        this.contentBytes = null;
        this.contentFile = file;
    }

    public ERSInputStreamData(InputStream inputStream) {
        try {
            this.contentBytes = Streams.readAll(inputStream);
            this.contentFile = null;
        } catch (IOException e) {
            throw ExpUtil.createIllegalState("unable to open content: " + e.getMessage(), e);
        }
    }

    @Override // org.bouncycastle.tsp.ers.ERSCachingData
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] bArr) {
        byte[] bArrCalculateDigest;
        if (this.contentBytes != null) {
            bArrCalculateDigest = ERSUtil.calculateDigest(digestCalculator, this.contentBytes);
        } else {
            try {
                FileInputStream fileInputStream = new FileInputStream(this.contentFile);
                bArrCalculateDigest = ERSUtil.calculateDigest(digestCalculator, fileInputStream);
                fileInputStream.close();
            } catch (IOException e) {
                throw ExpUtil.createIllegalState("unable to open content: " + e.getMessage(), e);
            }
        }
        return bArr != null ? ERSUtil.concatPreviousHashes(digestCalculator, bArr, bArrCalculateDigest) : bArrCalculateDigest;
    }
}
