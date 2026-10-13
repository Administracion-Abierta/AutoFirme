package org.bouncycastle.cert.path;

import java.util.Collections;
import java.util.Set;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/path/CertPathValidationResult.class */
public class CertPathValidationResult {
    private final boolean isValid;
    private final CertPathValidationException cause;
    private final Set unhandledCriticalExtensionOIDs;
    private final int certIndex;
    private final int ruleIndex;
    private CertPathValidationException[] causes;
    private int[] certIndexes;
    private int[] ruleIndexes;

    public CertPathValidationResult(CertPathValidationContext certPathValidationContext) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(certPathValidationContext.getUnhandledCriticalExtensionOIDs());
        this.isValid = this.unhandledCriticalExtensionOIDs.isEmpty();
        this.certIndex = -1;
        this.ruleIndex = -1;
        this.cause = null;
    }

    public CertPathValidationResult(CertPathValidationContext certPathValidationContext, int i, int i2, CertPathValidationException certPathValidationException) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(certPathValidationContext.getUnhandledCriticalExtensionOIDs());
        this.isValid = false;
        this.certIndex = i;
        this.ruleIndex = i2;
        this.cause = certPathValidationException;
    }

    public CertPathValidationResult(CertPathValidationContext certPathValidationContext, int[] iArr, int[] iArr2, CertPathValidationException[] certPathValidationExceptionArr) {
        this.unhandledCriticalExtensionOIDs = Collections.unmodifiableSet(certPathValidationContext.getUnhandledCriticalExtensionOIDs());
        this.isValid = false;
        this.cause = certPathValidationExceptionArr[0];
        this.certIndex = iArr[0];
        this.ruleIndex = iArr2[0];
        this.causes = certPathValidationExceptionArr;
        this.certIndexes = iArr;
        this.ruleIndexes = iArr2;
    }

    public boolean isValid() {
        return this.isValid;
    }

    public CertPathValidationException getCause() {
        if (this.cause != null) {
            return this.cause;
        }
        if (this.unhandledCriticalExtensionOIDs.isEmpty()) {
            return null;
        }
        return new CertPathValidationException("Unhandled Critical Extensions");
    }

    public int getFailingCertIndex() {
        return this.certIndex;
    }

    public int getFailingRuleIndex() {
        return this.ruleIndex;
    }

    public Set getUnhandledCriticalExtensionOIDs() {
        return this.unhandledCriticalExtensionOIDs;
    }

    public boolean isDetailed() {
        return this.certIndexes != null;
    }

    public CertPathValidationException[] getCauses() {
        if (this.causes != null) {
            CertPathValidationException[] certPathValidationExceptionArr = new CertPathValidationException[this.causes.length];
            System.arraycopy(this.causes, 0, certPathValidationExceptionArr, 0, this.causes.length);
            return certPathValidationExceptionArr;
        }
        if (this.unhandledCriticalExtensionOIDs.isEmpty()) {
            return null;
        }
        return new CertPathValidationException[]{new CertPathValidationException("Unhandled Critical Extensions")};
    }

    public int[] getFailingCertIndexes() {
        return Arrays.clone(this.certIndexes);
    }

    public int[] getFailingRuleIndexes() {
        return Arrays.clone(this.ruleIndexes);
    }
}
