package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertPath;
import org.bouncycastle.pkix.util.ErrorBundle;
import org.bouncycastle.pkix.util.LocalizedException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/jcajce/CertPathReviewerException.class */
public class CertPathReviewerException extends LocalizedException {
    private int index;
    private CertPath certPath;

    public CertPathReviewerException(ErrorBundle errorBundle, Throwable th) {
        super(errorBundle, th);
        this.index = -1;
        this.certPath = null;
    }

    public CertPathReviewerException(ErrorBundle errorBundle) {
        super(errorBundle);
        this.index = -1;
        this.certPath = null;
    }

    public CertPathReviewerException(ErrorBundle errorBundle, Throwable th, CertPath certPath, int i) {
        super(errorBundle, th);
        this.index = -1;
        this.certPath = null;
        if (certPath == null || i == -1) {
            throw new IllegalArgumentException();
        }
        if (i < -1 || i >= certPath.getCertificates().size()) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = i;
    }

    public CertPathReviewerException(ErrorBundle errorBundle, CertPath certPath, int i) {
        super(errorBundle);
        this.index = -1;
        this.certPath = null;
        if (certPath == null || i == -1) {
            throw new IllegalArgumentException();
        }
        if (i < -1 || i >= certPath.getCertificates().size()) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = i;
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getIndex() {
        return this.index;
    }
}
