package org.bouncycastle.tsp.ers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSFileData.class */
public class ERSFileData extends ERSCachingData {
    private final File content;

    public ERSFileData(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            throw new IllegalArgumentException("directory not allowed as ERSFileData");
        }
        if (!file.exists()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " does not exist");
        }
        if (!file.canRead()) {
            throw new FileNotFoundException(file.getAbsolutePath() + " is not readable");
        }
        this.content = file;
    }

    @Override // org.bouncycastle.tsp.ers.ERSCachingData
    protected byte[] calculateHash(DigestCalculator digestCalculator, byte[] bArr) {
        try {
            FileInputStream fileInputStream = new FileInputStream(this.content);
            byte[] bArrCalculateDigest = ERSUtil.calculateDigest(digestCalculator, fileInputStream);
            fileInputStream.close();
            return bArr != null ? ERSUtil.concatPreviousHashes(digestCalculator, bArr, bArrCalculateDigest) : bArrCalculateDigest;
        } catch (IOException e) {
            throw new IllegalStateException("unable to process " + this.content.getAbsolutePath());
        }
    }
}
