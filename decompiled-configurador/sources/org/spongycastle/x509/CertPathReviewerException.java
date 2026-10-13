package org.spongycastle.x509;

import java.security.cert.CertPath;
import org.spongycastle.i18n.ErrorBundle;
import org.spongycastle.i18n.LocalizedException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/CertPathReviewerException.class */
public class CertPathReviewerException extends LocalizedException {
    private int index;
    private CertPath certPath;

    public CertPathReviewerException(ErrorBundle errorMessage, Throwable throwable) {
        super(errorMessage, throwable);
        this.index = -1;
        this.certPath = null;
    }

    public CertPathReviewerException(ErrorBundle errorMessage) {
        super(errorMessage);
        this.index = -1;
        this.certPath = null;
    }

    public CertPathReviewerException(ErrorBundle errorMessage, Throwable throwable, CertPath certPath, int index) {
        super(errorMessage, throwable);
        this.index = -1;
        this.certPath = null;
        if (certPath == null || index == -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 || (certPath != null && index >= certPath.getCertificates().size())) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = index;
    }

    public CertPathReviewerException(ErrorBundle errorMessage, CertPath certPath, int index) {
        super(errorMessage);
        this.index = -1;
        this.certPath = null;
        if (certPath == null || index == -1) {
            throw new IllegalArgumentException();
        }
        if (index < -1 || (certPath != null && index >= certPath.getCertificates().size())) {
            throw new IndexOutOfBoundsException();
        }
        this.certPath = certPath;
        this.index = index;
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getIndex() {
        return this.index;
    }
}
