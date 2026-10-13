package org.spongycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXCertPathChecker;
import java.security.cert.PKIXParameters;
import java.security.cert.PolicyNode;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CRLEntry;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1Enumerated;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERIA5String;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.x509.AccessDescription;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AuthorityInformationAccess;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.BasicConstraints;
import org.spongycastle.asn1.x509.CRLDistPoint;
import org.spongycastle.asn1.x509.DistributionPoint;
import org.spongycastle.asn1.x509.DistributionPointName;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.GeneralSubtree;
import org.spongycastle.asn1.x509.IssuingDistributionPoint;
import org.spongycastle.asn1.x509.NameConstraints;
import org.spongycastle.asn1.x509.PolicyInformation;
import org.spongycastle.asn1.x509.qualified.MonetaryValue;
import org.spongycastle.asn1.x509.qualified.QCStatement;
import org.spongycastle.i18n.ErrorBundle;
import org.spongycastle.i18n.LocaleString;
import org.spongycastle.i18n.filter.TrustedInput;
import org.spongycastle.i18n.filter.UntrustedInput;
import org.spongycastle.i18n.filter.UntrustedUrlInput;
import org.spongycastle.jce.provider.AnnotatedException;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.provider.PKIXNameConstraintValidator;
import org.spongycastle.jce.provider.PKIXNameConstraintValidatorException;
import org.spongycastle.jce.provider.PKIXPolicyNode;
import org.spongycastle.jce.provider.RFC3280CertPathUtilities;
import org.spongycastle.util.Integers;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/PKIXCertPathReviewer.class */
public class PKIXCertPathReviewer extends CertPathValidatorUtilities {
    private static final String QC_STATEMENT = Extension.qCStatements.getId();
    private static final String CRL_DIST_POINTS = Extension.cRLDistributionPoints.getId();
    private static final String AUTH_INFO_ACCESS = Extension.authorityInfoAccess.getId();
    private static final String RESOURCE_NAME = "org.spongycastle.x509.CertPathReviewerMessages";
    protected CertPath certPath;
    protected PKIXParameters pkixParams;
    protected Date validDate;
    protected List certs;
    protected int n;
    protected List[] notifications;
    protected List[] errors;
    protected TrustAnchor trustAnchor;
    protected PublicKey subjectPublicKey;
    protected PolicyNode policyTree;
    private boolean initialized;

    public void init(CertPath certPath, PKIXParameters params) throws CertPathReviewerException {
        if (this.initialized) {
            throw new IllegalStateException("object is already initialized!");
        }
        this.initialized = true;
        if (certPath == null) {
            throw new NullPointerException("certPath was null");
        }
        this.certPath = certPath;
        this.certs = certPath.getCertificates();
        this.n = this.certs.size();
        if (this.certs.isEmpty()) {
            throw new CertPathReviewerException(new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.emptyCertPath"));
        }
        this.pkixParams = (PKIXParameters) params.clone();
        this.validDate = getValidDate(this.pkixParams);
        this.notifications = null;
        this.errors = null;
        this.trustAnchor = null;
        this.subjectPublicKey = null;
        this.policyTree = null;
    }

    public PKIXCertPathReviewer(CertPath certPath, PKIXParameters params) throws CertPathReviewerException {
        init(certPath, params);
    }

    public PKIXCertPathReviewer() {
    }

    public CertPath getCertPath() {
        return this.certPath;
    }

    public int getCertPathSize() {
        return this.n;
    }

    public List[] getErrors() {
        doChecks();
        return this.errors;
    }

    public List getErrors(int index) {
        doChecks();
        return this.errors[index + 1];
    }

    public List[] getNotifications() {
        doChecks();
        return this.notifications;
    }

    public List getNotifications(int index) {
        doChecks();
        return this.notifications[index + 1];
    }

    public PolicyNode getPolicyTree() {
        doChecks();
        return this.policyTree;
    }

    public PublicKey getSubjectPublicKey() {
        doChecks();
        return this.subjectPublicKey;
    }

    public TrustAnchor getTrustAnchor() {
        doChecks();
        return this.trustAnchor;
    }

    public boolean isValidCertPath() {
        doChecks();
        boolean valid = true;
        for (int i = 0; i < this.errors.length; i++) {
            if (!this.errors[i].isEmpty()) {
                valid = false;
                break;
            }
        }
        return valid;
    }

    protected void addNotification(ErrorBundle msg) {
        this.notifications[0].add(msg);
    }

    protected void addNotification(ErrorBundle msg, int index) {
        if (index < -1 || index >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.notifications[index + 1].add(msg);
    }

    protected void addError(ErrorBundle msg) {
        this.errors[0].add(msg);
    }

    protected void addError(ErrorBundle msg, int index) {
        if (index < -1 || index >= this.n) {
            throw new IndexOutOfBoundsException();
        }
        this.errors[index + 1].add(msg);
    }

    protected void doChecks() {
        if (!this.initialized) {
            throw new IllegalStateException("Object not initialized. Call init() first.");
        }
        if (this.notifications == null) {
            this.notifications = new List[this.n + 1];
            this.errors = new List[this.n + 1];
            for (int i = 0; i < this.notifications.length; i++) {
                this.notifications[i] = new ArrayList();
                this.errors[i] = new ArrayList();
            }
            checkSignatures();
            checkNameConstraints();
            checkPathLength();
            checkPolicy();
            checkCriticalExtensions();
        }
    }

    private void checkNameConstraints() {
        PKIXNameConstraintValidator nameConstraintValidator = new PKIXNameConstraintValidator();
        try {
            for (int index = this.certs.size() - 1; index > 0; index--) {
                int i = this.n - index;
                X509Certificate cert = (X509Certificate) this.certs.get(index);
                if (!isSelfIssued(cert)) {
                    X500Principal principal = getSubjectPrincipal(cert);
                    ASN1InputStream aIn = new ASN1InputStream(new ByteArrayInputStream(principal.getEncoded()));
                    try {
                        ASN1Sequence dns = (ASN1Sequence) aIn.readObject();
                        try {
                            nameConstraintValidator.checkPermittedDN(dns);
                            try {
                                nameConstraintValidator.checkExcludedDN(dns);
                                try {
                                    ASN1Sequence altName = (ASN1Sequence) getExtensionValue(cert, SUBJECT_ALTERNATIVE_NAME);
                                    if (altName != null) {
                                        for (int j = 0; j < altName.size(); j++) {
                                            GeneralName name = GeneralName.getInstance(altName.getObjectAt(j));
                                            try {
                                                nameConstraintValidator.checkPermitted(name);
                                                nameConstraintValidator.checkExcluded(name);
                                            } catch (PKIXNameConstraintValidatorException cpve) {
                                                ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notPermittedEmail", new Object[]{new UntrustedInput(name)});
                                                throw new CertPathReviewerException(msg, cpve, this.certPath, index);
                                            }
                                        }
                                    }
                                } catch (AnnotatedException ae) {
                                    ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.subjAltNameExtError");
                                    throw new CertPathReviewerException(msg2, ae, this.certPath, index);
                                }
                            } catch (PKIXNameConstraintValidatorException cpve2) {
                                ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.excludedDN", new Object[]{new UntrustedInput(principal.getName())});
                                throw new CertPathReviewerException(msg3, cpve2, this.certPath, index);
                            }
                        } catch (PKIXNameConstraintValidatorException cpve3) {
                            ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notPermittedDN", new Object[]{new UntrustedInput(principal.getName())});
                            throw new CertPathReviewerException(msg4, cpve3, this.certPath, index);
                        }
                    } catch (IOException e) {
                        ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ncSubjectNameError", new Object[]{new UntrustedInput(principal)});
                        throw new CertPathReviewerException(msg5, e, this.certPath, index);
                    }
                }
                try {
                    ASN1Sequence ncSeq = (ASN1Sequence) getExtensionValue(cert, NAME_CONSTRAINTS);
                    if (ncSeq != null) {
                        NameConstraints nc = NameConstraints.getInstance(ncSeq);
                        GeneralSubtree[] permitted = nc.getPermittedSubtrees();
                        if (permitted != null) {
                            nameConstraintValidator.intersectPermittedSubtree(permitted);
                        }
                        GeneralSubtree[] excluded = nc.getExcludedSubtrees();
                        if (excluded != null) {
                            for (int c = 0; c != excluded.length; c++) {
                                nameConstraintValidator.addExcludedSubtree(excluded[c]);
                            }
                        }
                    }
                } catch (AnnotatedException ae2) {
                    ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ncExtError");
                    throw new CertPathReviewerException(msg6, ae2, this.certPath, index);
                }
            }
        } catch (CertPathReviewerException cpre) {
            addError(cpre.getErrorMessage(), cpre.getIndex());
        }
    }

    private void checkPathLength() {
        BasicConstraints bc;
        BigInteger _pathLengthConstraint;
        int _plc;
        int maxPathLength = this.n;
        int totalPathLength = 0;
        for (int index = this.certs.size() - 1; index > 0; index--) {
            int i = this.n - index;
            X509Certificate cert = (X509Certificate) this.certs.get(index);
            if (!isSelfIssued(cert)) {
                if (maxPathLength <= 0) {
                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.pathLengthExtended");
                    addError(msg);
                }
                maxPathLength--;
                totalPathLength++;
            }
            try {
                bc = BasicConstraints.getInstance(getExtensionValue(cert, BASIC_CONSTRAINTS));
            } catch (AnnotatedException e) {
                ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.processLengthConstError");
                addError(msg2, index);
                bc = null;
            }
            if (bc != null && (_pathLengthConstraint = bc.getPathLenConstraint()) != null && (_plc = _pathLengthConstraint.intValue()) < maxPathLength) {
                maxPathLength = _plc;
            }
        }
        ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.totalPathLength", new Object[]{Integers.valueOf(totalPathLength)});
        addNotification(msg3);
    }

    private void checkSignatures() {
        AuthorityKeyIdentifier aki;
        GeneralNames issuerNames;
        boolean[] ku;
        PublicKey trustPublicKey;
        TrustAnchor trust = null;
        X500Principal trustPrincipal = null;
        ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certPathValidDate", new Object[]{new TrustedInput(this.validDate), new TrustedInput(new Date())});
        addNotification(msg);
        try {
            X509Certificate cert = (X509Certificate) this.certs.get(this.certs.size() - 1);
            Collection trustColl = getTrustAnchors(cert, this.pkixParams.getTrustAnchors());
            if (trustColl.size() > 1) {
                ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.conflictingTrustAnchors", new Object[]{Integers.valueOf(trustColl.size()), new UntrustedInput(cert.getIssuerX500Principal())});
                addError(msg2);
            } else if (trustColl.isEmpty()) {
                ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noTrustAnchorFound", new Object[]{new UntrustedInput(cert.getIssuerX500Principal()), Integers.valueOf(this.pkixParams.getTrustAnchors().size())});
                addError(msg3);
            } else {
                trust = (TrustAnchor) trustColl.iterator().next();
                if (trust.getTrustedCert() != null) {
                    trustPublicKey = trust.getTrustedCert().getPublicKey();
                } else {
                    trustPublicKey = trust.getCAPublicKey();
                }
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(cert, trustPublicKey, this.pkixParams.getSigProvider());
                } catch (SignatureException e) {
                    ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustButInvalidCert");
                    addError(msg4);
                } catch (Exception e2) {
                }
            }
        } catch (CertPathReviewerException cpre) {
            addError(cpre.getErrorMessage());
        } catch (Throwable t) {
            ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.unknown", new Object[]{new UntrustedInput(t.getMessage()), new UntrustedInput(t)});
            addError(msg5);
        }
        if (trust != null) {
            X509Certificate sign = trust.getTrustedCert();
            try {
                if (sign != null) {
                    trustPrincipal = getSubjectPrincipal(sign);
                } else {
                    trustPrincipal = new X500Principal(trust.getCAName());
                }
            } catch (IllegalArgumentException e3) {
                ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustDNInvalid", new Object[]{new UntrustedInput(trust.getCAName())});
                addError(msg6);
            }
            if (sign != null && (ku = sign.getKeyUsage()) != null && !ku[5]) {
                ErrorBundle msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustKeyUsage");
                addNotification(msg7);
            }
        }
        PublicKey workingPublicKey = null;
        X500Principal workingIssuerName = trustPrincipal;
        X509Certificate sign2 = null;
        if (trust != null) {
            sign2 = trust.getTrustedCert();
            if (sign2 != null) {
                workingPublicKey = sign2.getPublicKey();
            } else {
                workingPublicKey = trust.getCAPublicKey();
            }
            try {
                AlgorithmIdentifier workingAlgId = getAlgorithmIdentifier(workingPublicKey);
                workingAlgId.getAlgorithm();
                workingAlgId.getParameters();
            } catch (CertPathValidatorException e4) {
                ErrorBundle msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustPubKeyError");
                addError(msg8);
            }
        }
        for (int index = this.certs.size() - 1; index >= 0; index--) {
            int i = this.n - index;
            X509Certificate cert2 = (X509Certificate) this.certs.get(index);
            if (workingPublicKey != null) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(cert2, workingPublicKey, this.pkixParams.getSigProvider());
                } catch (GeneralSecurityException ex) {
                    ErrorBundle msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.signatureNotVerified", new Object[]{ex.getMessage(), ex, ex.getClass().getName()});
                    addError(msg9, index);
                }
            } else if (isSelfIssued(cert2)) {
                try {
                    CertPathValidatorUtilities.verifyX509Certificate(cert2, cert2.getPublicKey(), this.pkixParams.getSigProvider());
                    ErrorBundle msg10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.rootKeyIsValidButNotATrustAnchor");
                    addError(msg10, index);
                } catch (GeneralSecurityException ex2) {
                    ErrorBundle msg11 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.signatureNotVerified", new Object[]{ex2.getMessage(), ex2, ex2.getClass().getName()});
                    addError(msg11, index);
                }
            } else {
                ErrorBundle msg12 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.NoIssuerPublicKey");
                byte[] akiBytes = cert2.getExtensionValue(Extension.authorityKeyIdentifier.getId());
                if (akiBytes != null && (issuerNames = (aki = AuthorityKeyIdentifier.getInstance(DEROctetString.getInstance(akiBytes).getOctets())).getAuthorityCertIssuer()) != null) {
                    GeneralName name = issuerNames.getNames()[0];
                    BigInteger serial = aki.getAuthorityCertSerialNumber();
                    if (serial != null) {
                        Object[] extraArgs = {new LocaleString(RESOURCE_NAME, "missingIssuer"), " \"", name, "\" ", new LocaleString(RESOURCE_NAME, "missingSerial"), " ", serial};
                        msg12.setExtraArguments(extraArgs);
                    }
                }
                addError(msg12, index);
            }
            try {
                cert2.checkValidity(this.validDate);
            } catch (CertificateExpiredException e5) {
                ErrorBundle msg13 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certificateExpired", new Object[]{new TrustedInput(cert2.getNotAfter())});
                addError(msg13, index);
            } catch (CertificateNotYetValidException e6) {
                ErrorBundle msg14 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certificateNotYetValid", new Object[]{new TrustedInput(cert2.getNotBefore())});
                addError(msg14, index);
            }
            if (this.pkixParams.isRevocationEnabled()) {
                CRLDistPoint crlDistPoints = null;
                try {
                    ASN1Primitive crl_dp = getExtensionValue(cert2, CRL_DIST_POINTS);
                    if (crl_dp != null) {
                        crlDistPoints = CRLDistPoint.getInstance(crl_dp);
                    }
                } catch (AnnotatedException e7) {
                    ErrorBundle msg15 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlDistPtExtError");
                    addError(msg15, index);
                }
                AuthorityInformationAccess authInfoAcc = null;
                try {
                    ASN1Primitive auth_info_acc = getExtensionValue(cert2, AUTH_INFO_ACCESS);
                    if (auth_info_acc != null) {
                        authInfoAcc = AuthorityInformationAccess.getInstance(auth_info_acc);
                    }
                } catch (AnnotatedException e8) {
                    ErrorBundle msg16 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlAuthInfoAccError");
                    addError(msg16, index);
                }
                Vector crlDistPointUrls = getCRLDistUrls(crlDistPoints);
                Vector ocspUrls = getOCSPUrls(authInfoAcc);
                Iterator urlIt = crlDistPointUrls.iterator();
                while (urlIt.hasNext()) {
                    ErrorBundle msg17 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlDistPoint", new Object[]{new UntrustedUrlInput(urlIt.next())});
                    addNotification(msg17, index);
                }
                Iterator urlIt2 = ocspUrls.iterator();
                while (urlIt2.hasNext()) {
                    ErrorBundle msg18 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.ocspLocation", new Object[]{new UntrustedUrlInput(urlIt2.next())});
                    addNotification(msg18, index);
                }
                try {
                    checkRevocation(this.pkixParams, cert2, this.validDate, sign2, workingPublicKey, crlDistPointUrls, ocspUrls, index);
                } catch (CertPathReviewerException cpre2) {
                    addError(cpre2.getErrorMessage(), index);
                }
            }
            if (workingIssuerName != null && !cert2.getIssuerX500Principal().equals(workingIssuerName)) {
                ErrorBundle msg19 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certWrongIssuer", new Object[]{workingIssuerName.getName(), cert2.getIssuerX500Principal().getName()});
                addError(msg19, index);
            }
            if (i != this.n) {
                if (cert2 != null && cert2.getVersion() == 1) {
                    ErrorBundle msg20 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCACert");
                    addError(msg20, index);
                }
                try {
                    BasicConstraints bc = BasicConstraints.getInstance(getExtensionValue(cert2, BASIC_CONSTRAINTS));
                    if (bc != null) {
                        if (!bc.isCA()) {
                            ErrorBundle msg21 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCACert");
                            addError(msg21, index);
                        }
                    } else {
                        ErrorBundle msg22 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noBasicConstraints");
                        addError(msg22, index);
                    }
                } catch (AnnotatedException e9) {
                    ErrorBundle msg23 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.errorProcesingBC");
                    addError(msg23, index);
                }
                boolean[] _usage = cert2.getKeyUsage();
                if (_usage != null && !_usage[5]) {
                    ErrorBundle msg24 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCertSign");
                    addError(msg24, index);
                }
            }
            sign2 = cert2;
            workingIssuerName = cert2.getSubjectX500Principal();
            try {
                workingPublicKey = getNextWorkingKey(this.certs, index);
                AlgorithmIdentifier workingAlgId2 = getAlgorithmIdentifier(workingPublicKey);
                workingAlgId2.getAlgorithm();
                workingAlgId2.getParameters();
            } catch (CertPathValidatorException e10) {
                ErrorBundle msg25 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.pubKeyError");
                addError(msg25, index);
            }
        }
        this.trustAnchor = trust;
        this.subjectPublicKey = workingPublicKey;
    }

    private void checkPolicy() {
        int explicitPolicy;
        int inhibitAnyPolicy;
        int policyMapping;
        PKIXPolicyNode intersection;
        int _inhibitAnyPolicy;
        String _policy;
        Set<String> initialPolicies = this.pkixParams.getInitialPolicies();
        List[] policyNodes = new ArrayList[this.n + 1];
        for (int j = 0; j < policyNodes.length; j++) {
            policyNodes[j] = new ArrayList();
        }
        Set policySet = new HashSet();
        policySet.add(RFC3280CertPathUtilities.ANY_POLICY);
        PKIXPolicyNode validPolicyTree = new PKIXPolicyNode(new ArrayList(), 0, policySet, null, new HashSet(), RFC3280CertPathUtilities.ANY_POLICY, false);
        policyNodes[0].add(validPolicyTree);
        if (this.pkixParams.isExplicitPolicyRequired()) {
            explicitPolicy = 0;
        } else {
            explicitPolicy = this.n + 1;
        }
        if (this.pkixParams.isAnyPolicyInhibited()) {
            inhibitAnyPolicy = 0;
        } else {
            inhibitAnyPolicy = this.n + 1;
        }
        if (this.pkixParams.isPolicyMappingInhibited()) {
            policyMapping = 0;
        } else {
            policyMapping = this.n + 1;
        }
        Set acceptablePolicies = null;
        X509Certificate cert = null;
        try {
            int index = this.certs.size() - 1;
            while (index >= 0) {
                int i = this.n - index;
                cert = (X509Certificate) this.certs.get(index);
                try {
                    ASN1Sequence certPolicies = (ASN1Sequence) getExtensionValue(cert, CERTIFICATE_POLICIES);
                    if (certPolicies != null && validPolicyTree != null) {
                        Enumeration e = certPolicies.getObjects();
                        Set pols = new HashSet();
                        while (e.hasMoreElements()) {
                            PolicyInformation pInfo = PolicyInformation.getInstance(e.nextElement());
                            ASN1ObjectIdentifier pOid = pInfo.getPolicyIdentifier();
                            pols.add(pOid.getId());
                            if (!RFC3280CertPathUtilities.ANY_POLICY.equals(pOid.getId())) {
                                try {
                                    Set pq = getQualifierSet(pInfo.getPolicyQualifiers());
                                    boolean match = processCertD1i(i, policyNodes, pOid, pq);
                                    if (!match) {
                                        processCertD1ii(i, policyNodes, pOid, pq);
                                    }
                                } catch (CertPathValidatorException cpve) {
                                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                    throw new CertPathReviewerException(msg, cpve, this.certPath, index);
                                }
                            }
                        }
                        if (acceptablePolicies == null || acceptablePolicies.contains(RFC3280CertPathUtilities.ANY_POLICY)) {
                            acceptablePolicies = pols;
                        } else {
                            Set t1 = new HashSet();
                            for (Object o : acceptablePolicies) {
                                if (pols.contains(o)) {
                                    t1.add(o);
                                }
                            }
                            acceptablePolicies = t1;
                        }
                        if (inhibitAnyPolicy > 0 || (i < this.n && isSelfIssued(cert))) {
                            Enumeration e2 = certPolicies.getObjects();
                            while (e2.hasMoreElements()) {
                                PolicyInformation pInfo2 = PolicyInformation.getInstance(e2.nextElement());
                                if (RFC3280CertPathUtilities.ANY_POLICY.equals(pInfo2.getPolicyIdentifier().getId())) {
                                    try {
                                        Set _apq = getQualifierSet(pInfo2.getPolicyQualifiers());
                                        List _nodes = policyNodes[i - 1];
                                        for (int k = 0; k < _nodes.size(); k++) {
                                            PKIXPolicyNode _node = (PKIXPolicyNode) _nodes.get(k);
                                            for (Object _tmp : _node.getExpectedPolicies()) {
                                                if (_tmp instanceof String) {
                                                    _policy = (String) _tmp;
                                                } else if (_tmp instanceof ASN1ObjectIdentifier) {
                                                    _policy = ((ASN1ObjectIdentifier) _tmp).getId();
                                                }
                                                boolean _found = false;
                                                Iterator _childrenIter = _node.getChildren();
                                                while (_childrenIter.hasNext()) {
                                                    PKIXPolicyNode _child = (PKIXPolicyNode) _childrenIter.next();
                                                    if (_policy.equals(_child.getValidPolicy())) {
                                                        _found = true;
                                                    }
                                                }
                                                if (!_found) {
                                                    Set _newChildExpectedPolicies = new HashSet();
                                                    _newChildExpectedPolicies.add(_policy);
                                                    PKIXPolicyNode _newChild = new PKIXPolicyNode(new ArrayList(), i, _newChildExpectedPolicies, _node, _apq, _policy, false);
                                                    _node.addChild(_newChild);
                                                    policyNodes[i].add(_newChild);
                                                }
                                            }
                                        }
                                    } catch (CertPathValidatorException cpve2) {
                                        ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                        throw new CertPathReviewerException(msg2, cpve2, this.certPath, index);
                                    }
                                }
                            }
                        }
                        for (int j2 = i - 1; j2 >= 0; j2--) {
                            List nodes = policyNodes[j2];
                            for (int k2 = 0; k2 < nodes.size(); k2++) {
                                PKIXPolicyNode node = (PKIXPolicyNode) nodes.get(k2);
                                if (!node.hasChildren()) {
                                    validPolicyTree = removePolicyNode(validPolicyTree, policyNodes, node);
                                    if (validPolicyTree == null) {
                                        break;
                                    }
                                }
                            }
                        }
                        Set<String> criticalExtensionOIDs = cert.getCriticalExtensionOIDs();
                        if (criticalExtensionOIDs != null) {
                            boolean critical = criticalExtensionOIDs.contains(CERTIFICATE_POLICIES);
                            List nodes2 = policyNodes[i];
                            for (int j3 = 0; j3 < nodes2.size(); j3++) {
                                ((PKIXPolicyNode) nodes2.get(j3)).setCritical(critical);
                            }
                        }
                    }
                    if (certPolicies == null) {
                        validPolicyTree = null;
                    }
                    if (explicitPolicy <= 0 && validPolicyTree == null) {
                        ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noValidPolicyTree");
                        throw new CertPathReviewerException(msg3);
                    }
                    if (i != this.n) {
                        try {
                            ASN1Primitive pm = getExtensionValue(cert, POLICY_MAPPINGS);
                            if (pm != null) {
                                ASN1Sequence mappings = (ASN1Sequence) pm;
                                for (int j4 = 0; j4 < mappings.size(); j4++) {
                                    ASN1Sequence mapping = (ASN1Sequence) mappings.getObjectAt(j4);
                                    ASN1ObjectIdentifier ip_id = (ASN1ObjectIdentifier) mapping.getObjectAt(0);
                                    ASN1ObjectIdentifier sp_id = (ASN1ObjectIdentifier) mapping.getObjectAt(1);
                                    if (RFC3280CertPathUtilities.ANY_POLICY.equals(ip_id.getId())) {
                                        ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicyMapping");
                                        throw new CertPathReviewerException(msg4, this.certPath, index);
                                    }
                                    if (RFC3280CertPathUtilities.ANY_POLICY.equals(sp_id.getId())) {
                                        ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicyMapping");
                                        throw new CertPathReviewerException(msg5, this.certPath, index);
                                    }
                                }
                            }
                            if (pm != null) {
                                ASN1Sequence mappings2 = (ASN1Sequence) pm;
                                Map m_idp = new HashMap();
                                Set<String> s_idp = new HashSet();
                                for (int j5 = 0; j5 < mappings2.size(); j5++) {
                                    ASN1Sequence mapping2 = (ASN1Sequence) mappings2.getObjectAt(j5);
                                    String id_p = ((ASN1ObjectIdentifier) mapping2.getObjectAt(0)).getId();
                                    String sd_p = ((ASN1ObjectIdentifier) mapping2.getObjectAt(1)).getId();
                                    if (!m_idp.containsKey(id_p)) {
                                        HashSet hashSet = new HashSet();
                                        hashSet.add(sd_p);
                                        m_idp.put(id_p, hashSet);
                                        s_idp.add(id_p);
                                    } else {
                                        Set tmp = (Set) m_idp.get(id_p);
                                        tmp.add(sd_p);
                                    }
                                }
                                for (String id_p2 : s_idp) {
                                    if (policyMapping > 0) {
                                        try {
                                            prepareNextCertB1(i, policyNodes, id_p2, m_idp, cert);
                                        } catch (CertPathValidatorException cpve3) {
                                            ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyQualifierError");
                                            throw new CertPathReviewerException(msg6, cpve3, this.certPath, index);
                                        } catch (AnnotatedException ae) {
                                            ErrorBundle msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyExtError");
                                            throw new CertPathReviewerException(msg7, ae, this.certPath, index);
                                        }
                                    } else if (policyMapping <= 0) {
                                        validPolicyTree = prepareNextCertB2(i, policyNodes, id_p2, validPolicyTree);
                                    }
                                }
                            }
                            if (!isSelfIssued(cert)) {
                                if (explicitPolicy != 0) {
                                    explicitPolicy--;
                                }
                                if (policyMapping != 0) {
                                    policyMapping--;
                                }
                                if (inhibitAnyPolicy != 0) {
                                    inhibitAnyPolicy--;
                                }
                            }
                            try {
                                ASN1Sequence pc = (ASN1Sequence) getExtensionValue(cert, POLICY_CONSTRAINTS);
                                if (pc != null) {
                                    Enumeration policyConstraints = pc.getObjects();
                                    while (policyConstraints.hasMoreElements()) {
                                        ASN1TaggedObject constraint = (ASN1TaggedObject) policyConstraints.nextElement();
                                        switch (constraint.getTagNo()) {
                                            case 0:
                                                int tmpInt = ASN1Integer.getInstance(constraint, false).getValue().intValue();
                                                if (tmpInt < explicitPolicy) {
                                                    explicitPolicy = tmpInt;
                                                }
                                                break;
                                            case 1:
                                                int tmpInt2 = ASN1Integer.getInstance(constraint, false).getValue().intValue();
                                                if (tmpInt2 < policyMapping) {
                                                    policyMapping = tmpInt2;
                                                }
                                                break;
                                        }
                                    }
                                }
                                try {
                                    ASN1Integer iap = (ASN1Integer) getExtensionValue(cert, INHIBIT_ANY_POLICY);
                                    if (iap != null && (_inhibitAnyPolicy = iap.getValue().intValue()) < inhibitAnyPolicy) {
                                        inhibitAnyPolicy = _inhibitAnyPolicy;
                                    }
                                } catch (AnnotatedException e3) {
                                    ErrorBundle msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyInhibitExtError");
                                    throw new CertPathReviewerException(msg8, this.certPath, index);
                                }
                            } catch (AnnotatedException e4) {
                                ErrorBundle msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyConstExtError");
                                throw new CertPathReviewerException(msg9, this.certPath, index);
                            }
                        } catch (AnnotatedException ae2) {
                            ErrorBundle msg10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyMapExtError");
                            throw new CertPathReviewerException(msg10, ae2, this.certPath, index);
                        }
                    }
                    index--;
                } catch (AnnotatedException ae3) {
                    ErrorBundle msg11 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyExtError");
                    throw new CertPathReviewerException(msg11, ae3, this.certPath, index);
                }
            }
            if (!isSelfIssued(cert) && explicitPolicy > 0) {
                explicitPolicy--;
            }
            try {
                ASN1Sequence pc2 = (ASN1Sequence) getExtensionValue(cert, POLICY_CONSTRAINTS);
                if (pc2 != null) {
                    Enumeration policyConstraints2 = pc2.getObjects();
                    while (policyConstraints2.hasMoreElements()) {
                        ASN1TaggedObject constraint2 = (ASN1TaggedObject) policyConstraints2.nextElement();
                        switch (constraint2.getTagNo()) {
                            case 0:
                                if (ASN1Integer.getInstance(constraint2, false).getValue().intValue() == 0) {
                                    explicitPolicy = 0;
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
                if (validPolicyTree == null) {
                    if (this.pkixParams.isExplicitPolicyRequired()) {
                        ErrorBundle msg12 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.explicitPolicy");
                        throw new CertPathReviewerException(msg12, this.certPath, index);
                    }
                    intersection = null;
                } else if (isAnyPolicy(initialPolicies)) {
                    if (this.pkixParams.isExplicitPolicyRequired()) {
                        if (acceptablePolicies.isEmpty()) {
                            ErrorBundle msg13 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.explicitPolicy");
                            throw new CertPathReviewerException(msg13, this.certPath, index);
                        }
                        Set _validPolicyNodeSet = new HashSet();
                        for (List _nodeDepth : policyNodes) {
                            for (int k3 = 0; k3 < _nodeDepth.size(); k3++) {
                                PKIXPolicyNode _node2 = (PKIXPolicyNode) _nodeDepth.get(k3);
                                if (RFC3280CertPathUtilities.ANY_POLICY.equals(_node2.getValidPolicy())) {
                                    Iterator _iter = _node2.getChildren();
                                    while (_iter.hasNext()) {
                                        _validPolicyNodeSet.add(_iter.next());
                                    }
                                }
                            }
                        }
                        Iterator _vpnsIter = _validPolicyNodeSet.iterator();
                        while (_vpnsIter.hasNext()) {
                            String _validPolicy = ((PKIXPolicyNode) _vpnsIter.next()).getValidPolicy();
                            if (!acceptablePolicies.contains(_validPolicy)) {
                            }
                        }
                        if (validPolicyTree != null) {
                            for (int j6 = this.n - 1; j6 >= 0; j6--) {
                                List nodes3 = policyNodes[j6];
                                for (int k4 = 0; k4 < nodes3.size(); k4++) {
                                    PKIXPolicyNode node2 = (PKIXPolicyNode) nodes3.get(k4);
                                    if (!node2.hasChildren()) {
                                        validPolicyTree = removePolicyNode(validPolicyTree, policyNodes, node2);
                                    }
                                }
                            }
                        }
                    }
                    intersection = validPolicyTree;
                } else {
                    Set<PKIXPolicyNode> _validPolicyNodeSet2 = new HashSet();
                    for (List _nodeDepth2 : policyNodes) {
                        for (int k5 = 0; k5 < _nodeDepth2.size(); k5++) {
                            PKIXPolicyNode _node3 = (PKIXPolicyNode) _nodeDepth2.get(k5);
                            if (RFC3280CertPathUtilities.ANY_POLICY.equals(_node3.getValidPolicy())) {
                                Iterator _iter2 = _node3.getChildren();
                                while (_iter2.hasNext()) {
                                    PKIXPolicyNode _c_node = (PKIXPolicyNode) _iter2.next();
                                    if (!RFC3280CertPathUtilities.ANY_POLICY.equals(_c_node.getValidPolicy())) {
                                        _validPolicyNodeSet2.add(_c_node);
                                    }
                                }
                            }
                        }
                    }
                    for (PKIXPolicyNode _node4 : _validPolicyNodeSet2) {
                        String _validPolicy2 = _node4.getValidPolicy();
                        if (!initialPolicies.contains(_validPolicy2)) {
                            validPolicyTree = removePolicyNode(validPolicyTree, policyNodes, _node4);
                        }
                    }
                    if (validPolicyTree != null) {
                        for (int j7 = this.n - 1; j7 >= 0; j7--) {
                            List nodes4 = policyNodes[j7];
                            for (int k6 = 0; k6 < nodes4.size(); k6++) {
                                PKIXPolicyNode node3 = (PKIXPolicyNode) nodes4.get(k6);
                                if (!node3.hasChildren()) {
                                    validPolicyTree = removePolicyNode(validPolicyTree, policyNodes, node3);
                                }
                            }
                        }
                    }
                    intersection = validPolicyTree;
                }
                if (explicitPolicy <= 0 && intersection == null) {
                    ErrorBundle msg14 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.invalidPolicy");
                    throw new CertPathReviewerException(msg14);
                }
            } catch (AnnotatedException e5) {
                ErrorBundle msg15 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.policyConstExtError");
                throw new CertPathReviewerException(msg15, this.certPath, index);
            }
        } catch (CertPathReviewerException cpre) {
            addError(cpre.getErrorMessage(), cpre.getIndex());
        }
    }

    private void checkCriticalExtensions() {
        List<PKIXCertPathChecker> certPathCheckers = this.pkixParams.getCertPathCheckers();
        Iterator<PKIXCertPathChecker> it = certPathCheckers.iterator();
        while (it.hasNext()) {
            try {
                try {
                    it.next().init(false);
                } catch (CertPathValidatorException cpve) {
                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certPathCheckerError", new Object[]{cpve.getMessage(), cpve, cpve.getClass().getName()});
                    throw new CertPathReviewerException(msg, cpve);
                }
            } catch (CertPathReviewerException cpre) {
                addError(cpre.getErrorMessage(), cpre.getIndex());
                return;
            }
        }
        for (int index = this.certs.size() - 1; index >= 0; index--) {
            X509Certificate cert = (X509Certificate) this.certs.get(index);
            Set<String> criticalExtensionOIDs = cert.getCriticalExtensionOIDs();
            if (criticalExtensionOIDs != null && !criticalExtensionOIDs.isEmpty()) {
                criticalExtensionOIDs.remove(KEY_USAGE);
                criticalExtensionOIDs.remove(CERTIFICATE_POLICIES);
                criticalExtensionOIDs.remove(POLICY_MAPPINGS);
                criticalExtensionOIDs.remove(INHIBIT_ANY_POLICY);
                criticalExtensionOIDs.remove(ISSUING_DISTRIBUTION_POINT);
                criticalExtensionOIDs.remove(DELTA_CRL_INDICATOR);
                criticalExtensionOIDs.remove(POLICY_CONSTRAINTS);
                criticalExtensionOIDs.remove(BASIC_CONSTRAINTS);
                criticalExtensionOIDs.remove(SUBJECT_ALTERNATIVE_NAME);
                criticalExtensionOIDs.remove(NAME_CONSTRAINTS);
                if (criticalExtensionOIDs.contains(QC_STATEMENT) && processQcStatements(cert, index)) {
                    criticalExtensionOIDs.remove(QC_STATEMENT);
                }
                Iterator<PKIXCertPathChecker> it2 = certPathCheckers.iterator();
                while (it2.hasNext()) {
                    try {
                        it2.next().check(cert, criticalExtensionOIDs);
                    } catch (CertPathValidatorException e) {
                        ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.criticalExtensionError", new Object[]{e.getMessage(), e, e.getClass().getName()});
                        throw new CertPathReviewerException(msg2, e.getCause(), this.certPath, index);
                    }
                }
                if (!criticalExtensionOIDs.isEmpty()) {
                    Iterator<String> it3 = criticalExtensionOIDs.iterator();
                    while (it3.hasNext()) {
                        ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.unknownCriticalExt", new Object[]{new ASN1ObjectIdentifier(it3.next())});
                        addError(msg3, index);
                    }
                }
            }
        }
    }

    private boolean processQcStatements(X509Certificate cert, int index) {
        ErrorBundle msg;
        try {
            boolean unknownStatement = false;
            ASN1Sequence qcSt = (ASN1Sequence) getExtensionValue(cert, QC_STATEMENT);
            for (int j = 0; j < qcSt.size(); j++) {
                QCStatement stmt = QCStatement.getInstance(qcSt.getObjectAt(j));
                if (QCStatement.id_etsi_qcs_QcCompliance.equals(stmt.getStatementId())) {
                    ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcEuCompliance");
                    addNotification(msg2, index);
                } else if (!QCStatement.id_qcs_pkixQCSyntax_v1.equals(stmt.getStatementId())) {
                    if (QCStatement.id_etsi_qcs_QcSSCD.equals(stmt.getStatementId())) {
                        ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcSSCD");
                        addNotification(msg3, index);
                    } else if (QCStatement.id_etsi_qcs_LimiteValue.equals(stmt.getStatementId())) {
                        MonetaryValue limit = MonetaryValue.getInstance(stmt.getStatementInfo());
                        limit.getCurrency();
                        double value = limit.getAmount().doubleValue() * Math.pow(10.0d, limit.getExponent().doubleValue());
                        if (limit.getCurrency().isAlphabetic()) {
                            msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcLimitValueAlpha", new Object[]{limit.getCurrency().getAlphabetic(), new TrustedInput(new Double(value)), limit});
                        } else {
                            msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcLimitValueNum", new Object[]{Integers.valueOf(limit.getCurrency().getNumeric()), new TrustedInput(new Double(value)), limit});
                        }
                        addNotification(msg, index);
                    } else {
                        ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcUnknownStatement", new Object[]{stmt.getStatementId(), new UntrustedInput(stmt)});
                        addNotification(msg4, index);
                        unknownStatement = true;
                    }
                }
            }
            return !unknownStatement;
        } catch (AnnotatedException e) {
            ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.QcStatementExtError");
            addError(msg5, index);
            return false;
        }
    }

    private String IPtoString(byte[] ip) {
        String result;
        try {
            result = InetAddress.getByAddress(ip).getHostAddress();
        } catch (Exception e) {
            StringBuffer b = new StringBuffer();
            for (int i = 0; i != ip.length; i++) {
                b.append(Integer.toHexString(ip[i] & 255));
                b.append(' ');
            }
            result = b.toString();
        }
        return result;
    }

    protected void checkRevocation(PKIXParameters paramsPKIX, X509Certificate cert, Date validDate, X509Certificate sign, PublicKey workingPublicKey, Vector crlDistPointUrls, Vector ocspUrls, int index) throws CertPathReviewerException {
        checkCRLs(paramsPKIX, cert, validDate, sign, workingPublicKey, crlDistPointUrls, index);
    }

    protected void checkCRLs(PKIXParameters paramsPKIX, X509Certificate cert, Date validDate, X509Certificate sign, PublicKey workingPublicKey, Vector crlDistPointUrls, int index) throws CertPathReviewerException {
        Iterator crl_iter;
        boolean[] keyusage;
        X509CRLStoreSelector crlselect = new X509CRLStoreSelector();
        try {
            crlselect.addIssuerName(getEncodedIssuerPrincipal(cert).getEncoded());
            crlselect.setCertificateChecking(cert);
            try {
                Collection crl_coll = CRL_UTIL.findCRLs(crlselect, paramsPKIX);
                crl_iter = crl_coll.iterator();
                if (crl_coll.isEmpty()) {
                    Iterator it = CRL_UTIL.findCRLs(new X509CRLStoreSelector(), paramsPKIX).iterator();
                    List nonMatchingCrlNames = new ArrayList();
                    while (it.hasNext()) {
                        nonMatchingCrlNames.add(((X509CRL) it.next()).getIssuerX500Principal());
                    }
                    int numbOfCrls = nonMatchingCrlNames.size();
                    ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCrlInCertstore", new Object[]{new UntrustedInput(crlselect.getIssuerNames()), new UntrustedInput(nonMatchingCrlNames), Integers.valueOf(numbOfCrls)});
                    addNotification(msg, index);
                }
            } catch (AnnotatedException ae) {
                ErrorBundle msg2 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlExtractionError", new Object[]{ae.getCause().getMessage(), ae.getCause(), ae.getCause().getClass().getName()});
                addError(msg2, index);
                crl_iter = new ArrayList().iterator();
            }
            boolean validCrlFound = false;
            X509CRL crl = null;
            while (crl_iter.hasNext()) {
                crl = (X509CRL) crl_iter.next();
                if (crl.getNextUpdate() == null || paramsPKIX.getDate().before(crl.getNextUpdate())) {
                    validCrlFound = true;
                    ErrorBundle msg3 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.localValidCRL", new Object[]{new TrustedInput(crl.getThisUpdate()), new TrustedInput(crl.getNextUpdate())});
                    addNotification(msg3, index);
                    break;
                }
                ErrorBundle msg4 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.localInvalidCRL", new Object[]{new TrustedInput(crl.getThisUpdate()), new TrustedInput(crl.getNextUpdate())});
                addNotification(msg4, index);
            }
            if (!validCrlFound) {
                Iterator urlIt = crlDistPointUrls.iterator();
                while (urlIt.hasNext()) {
                    try {
                        String location = (String) urlIt.next();
                        X509CRL onlineCRL = getCRL(location);
                        if (onlineCRL != null) {
                            if (!cert.getIssuerX500Principal().equals(onlineCRL.getIssuerX500Principal())) {
                                ErrorBundle msg5 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineCRLWrongCA", new Object[]{new UntrustedInput(onlineCRL.getIssuerX500Principal().getName()), new UntrustedInput(cert.getIssuerX500Principal().getName()), new UntrustedUrlInput(location)});
                                addNotification(msg5, index);
                            } else {
                                if (onlineCRL.getNextUpdate() == null || this.pkixParams.getDate().before(onlineCRL.getNextUpdate())) {
                                    validCrlFound = true;
                                    ErrorBundle msg6 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineValidCRL", new Object[]{new TrustedInput(onlineCRL.getThisUpdate()), new TrustedInput(onlineCRL.getNextUpdate()), new UntrustedUrlInput(location)});
                                    addNotification(msg6, index);
                                    crl = onlineCRL;
                                    break;
                                }
                                ErrorBundle msg7 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.onlineInvalidCRL", new Object[]{new TrustedInput(onlineCRL.getThisUpdate()), new TrustedInput(onlineCRL.getNextUpdate()), new UntrustedUrlInput(location)});
                                addNotification(msg7, index);
                            }
                        }
                    } catch (CertPathReviewerException cpre) {
                        addNotification(cpre.getErrorMessage(), index);
                    }
                }
            }
            if (crl != null) {
                if (sign != null && (keyusage = sign.getKeyUsage()) != null && (keyusage.length < 7 || !keyusage[6])) {
                    ErrorBundle msg8 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noCrlSigningPermited");
                    throw new CertPathReviewerException(msg8);
                }
                if (workingPublicKey != null) {
                    try {
                        crl.verify(workingPublicKey, BouncyCastleProvider.PROVIDER_NAME);
                        X509CRLEntry crl_entry = crl.getRevokedCertificate(cert.getSerialNumber());
                        if (crl_entry != null) {
                            String reason = null;
                            if (crl_entry.hasExtensions()) {
                                try {
                                    ASN1Enumerated reasonCode = ASN1Enumerated.getInstance(getExtensionValue(crl_entry, Extension.reasonCode.getId()));
                                    if (reasonCode != null) {
                                        reason = crlReasons[reasonCode.getValue().intValue()];
                                    }
                                } catch (AnnotatedException ae2) {
                                    ErrorBundle msg9 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlReasonExtError");
                                    throw new CertPathReviewerException(msg9, ae2);
                                }
                            }
                            if (reason == null) {
                                reason = crlReasons[7];
                            }
                            LocaleString ls = new LocaleString(RESOURCE_NAME, reason);
                            if (!validDate.before(crl_entry.getRevocationDate())) {
                                ErrorBundle msg10 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.certRevoked", new Object[]{new TrustedInput(crl_entry.getRevocationDate()), ls});
                                throw new CertPathReviewerException(msg10);
                            }
                            ErrorBundle msg11 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.revokedAfterValidation", new Object[]{new TrustedInput(crl_entry.getRevocationDate()), ls});
                            addNotification(msg11, index);
                        } else {
                            ErrorBundle msg12 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.notRevoked");
                            addNotification(msg12, index);
                        }
                        if (crl.getNextUpdate() != null && crl.getNextUpdate().before(this.pkixParams.getDate())) {
                            ErrorBundle msg13 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlUpdateAvailable", new Object[]{new TrustedInput(crl.getNextUpdate())});
                            addNotification(msg13, index);
                        }
                        try {
                            ASN1Primitive idp = getExtensionValue(crl, ISSUING_DISTRIBUTION_POINT);
                            try {
                                ASN1Primitive dci = getExtensionValue(crl, DELTA_CRL_INDICATOR);
                                if (dci != null) {
                                    X509CRLStoreSelector baseSelect = new X509CRLStoreSelector();
                                    try {
                                        baseSelect.addIssuerName(getIssuerPrincipal(crl).getEncoded());
                                        baseSelect.setMinCRLNumber(((ASN1Integer) dci).getPositiveValue());
                                        try {
                                            baseSelect.setMaxCRLNumber(((ASN1Integer) getExtensionValue(crl, CRL_NUMBER)).getPositiveValue().subtract(BigInteger.valueOf(1L)));
                                            boolean foundBase = false;
                                            try {
                                                for (X509CRL base : CRL_UTIL.findCRLs(baseSelect, paramsPKIX)) {
                                                    try {
                                                        ASN1Primitive baseIdp = getExtensionValue(base, ISSUING_DISTRIBUTION_POINT);
                                                        if (idp == null) {
                                                            if (baseIdp == null) {
                                                                foundBase = true;
                                                                break;
                                                            }
                                                        } else if (idp.equals(baseIdp)) {
                                                            foundBase = true;
                                                            break;
                                                        }
                                                    } catch (AnnotatedException ae3) {
                                                        ErrorBundle msg14 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.distrPtExtError");
                                                        throw new CertPathReviewerException(msg14, ae3);
                                                    }
                                                }
                                                if (!foundBase) {
                                                    ErrorBundle msg15 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noBaseCRL");
                                                    throw new CertPathReviewerException(msg15);
                                                }
                                            } catch (AnnotatedException ae4) {
                                                ErrorBundle msg16 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlExtractionError");
                                                throw new CertPathReviewerException(msg16, ae4);
                                            }
                                        } catch (AnnotatedException ae5) {
                                            ErrorBundle msg17 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlNbrExtError");
                                            throw new CertPathReviewerException(msg17, ae5);
                                        }
                                    } catch (IOException e) {
                                        ErrorBundle msg18 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlIssuerException");
                                        throw new CertPathReviewerException(msg18, e);
                                    }
                                }
                                if (idp != null) {
                                    IssuingDistributionPoint p = IssuingDistributionPoint.getInstance(idp);
                                    try {
                                        BasicConstraints bc = BasicConstraints.getInstance(getExtensionValue(cert, BASIC_CONSTRAINTS));
                                        if (p.onlyContainsUserCerts() && bc != null && bc.isCA()) {
                                            ErrorBundle msg19 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyUserCert");
                                            throw new CertPathReviewerException(msg19);
                                        }
                                        if (p.onlyContainsCACerts() && (bc == null || !bc.isCA())) {
                                            ErrorBundle msg20 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyCaCert");
                                            throw new CertPathReviewerException(msg20);
                                        }
                                        if (p.onlyContainsAttributeCerts()) {
                                            ErrorBundle msg21 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlOnlyAttrCert");
                                            throw new CertPathReviewerException(msg21);
                                        }
                                    } catch (AnnotatedException ae6) {
                                        ErrorBundle msg22 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlBCExtError");
                                        throw new CertPathReviewerException(msg22, ae6);
                                    }
                                }
                            } catch (AnnotatedException e2) {
                                ErrorBundle msg23 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.deltaCrlExtError");
                                throw new CertPathReviewerException(msg23);
                            }
                        } catch (AnnotatedException e3) {
                            ErrorBundle msg24 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.distrPtExtError");
                            throw new CertPathReviewerException(msg24);
                        }
                    } catch (Exception e4) {
                        ErrorBundle msg25 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlVerifyFailed");
                        throw new CertPathReviewerException(msg25, e4);
                    }
                } else {
                    ErrorBundle msg26 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlNoIssuerPublicKey");
                    throw new CertPathReviewerException(msg26);
                }
            }
            if (!validCrlFound) {
                ErrorBundle msg27 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.noValidCrlFound");
                throw new CertPathReviewerException(msg27);
            }
        } catch (IOException e5) {
            ErrorBundle msg28 = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.crlIssuerException");
            throw new CertPathReviewerException(msg28, e5);
        }
    }

    protected Vector getCRLDistUrls(CRLDistPoint crlDistPoints) {
        Vector urls = new Vector();
        if (crlDistPoints != null) {
            DistributionPoint[] distPoints = crlDistPoints.getDistributionPoints();
            for (DistributionPoint distributionPoint : distPoints) {
                DistributionPointName dp_name = distributionPoint.getDistributionPoint();
                if (dp_name.getType() == 0) {
                    GeneralName[] generalNames = GeneralNames.getInstance(dp_name.getName()).getNames();
                    for (int j = 0; j < generalNames.length; j++) {
                        if (generalNames[j].getTagNo() == 6) {
                            String url = ((DERIA5String) generalNames[j].getName()).getString();
                            urls.add(url);
                        }
                    }
                }
            }
        }
        return urls;
    }

    protected Vector getOCSPUrls(AuthorityInformationAccess authInfoAccess) {
        Vector urls = new Vector();
        if (authInfoAccess != null) {
            AccessDescription[] ads = authInfoAccess.getAccessDescriptions();
            for (int i = 0; i < ads.length; i++) {
                if (ads[i].getAccessMethod().equals(AccessDescription.id_ad_ocsp)) {
                    GeneralName name = ads[i].getAccessLocation();
                    if (name.getTagNo() == 6) {
                        String url = ((DERIA5String) name.getName()).getString();
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    private X509CRL getCRL(String location) throws CertPathReviewerException {
        X509CRL result = null;
        try {
            URL url = new URL(location);
            if (url.getProtocol().equals("http") || url.getProtocol().equals("https")) {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    CertificateFactory cf = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
                    result = (X509CRL) cf.generateCRL(conn.getInputStream());
                } else {
                    throw new Exception(conn.getResponseMessage());
                }
            }
            return result;
        } catch (Exception e) {
            ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.loadCrlDistPointError", new Object[]{new UntrustedInput(location), e.getMessage(), e, e.getClass().getName()});
            throw new CertPathReviewerException(msg);
        }
    }

    protected Collection getTrustAnchors(X509Certificate cert, Set trustanchors) throws CertPathReviewerException {
        Collection trustColl = new ArrayList();
        Iterator it = trustanchors.iterator();
        X509CertSelector certSelectX509 = new X509CertSelector();
        try {
            certSelectX509.setSubject(getEncodedIssuerPrincipal(cert).getEncoded());
            byte[] ext = cert.getExtensionValue(Extension.authorityKeyIdentifier.getId());
            if (ext != null) {
                ASN1OctetString oct = (ASN1OctetString) ASN1Primitive.fromByteArray(ext);
                AuthorityKeyIdentifier authID = AuthorityKeyIdentifier.getInstance(ASN1Primitive.fromByteArray(oct.getOctets()));
                certSelectX509.setSerialNumber(authID.getAuthorityCertSerialNumber());
                byte[] keyID = authID.getKeyIdentifier();
                if (keyID != null) {
                    certSelectX509.setSubjectKeyIdentifier(new DEROctetString(keyID).getEncoded());
                }
            }
            while (it.hasNext()) {
                TrustAnchor trust = (TrustAnchor) it.next();
                if (trust.getTrustedCert() != null) {
                    if (certSelectX509.match(trust.getTrustedCert())) {
                        trustColl.add(trust);
                    }
                } else if (trust.getCAName() != null && trust.getCAPublicKey() != null) {
                    X500Principal certIssuer = getEncodedIssuerPrincipal(cert);
                    X500Principal caName = new X500Principal(trust.getCAName());
                    if (certIssuer.equals(caName)) {
                        trustColl.add(trust);
                    }
                }
            }
            return trustColl;
        } catch (IOException e) {
            ErrorBundle msg = new ErrorBundle(RESOURCE_NAME, "CertPathReviewer.trustAnchorIssuerError");
            throw new CertPathReviewerException(msg);
        }
    }
}
