package org.spongycastle.cert.crmf;

import java.io.IOException;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERUTF8String;
import org.spongycastle.asn1.crmf.AttributeTypeAndValue;
import org.spongycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.spongycastle.asn1.crmf.CertReqMsg;
import org.spongycastle.asn1.crmf.CertTemplate;
import org.spongycastle.asn1.crmf.Controls;
import org.spongycastle.asn1.crmf.PKIArchiveOptions;
import org.spongycastle.asn1.crmf.PKMACValue;
import org.spongycastle.asn1.crmf.POPOSigningKey;
import org.spongycastle.asn1.crmf.ProofOfPossession;
import org.spongycastle.cert.CertIOException;
import org.spongycastle.operator.ContentVerifier;
import org.spongycastle.operator.ContentVerifierProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/CertificateRequestMessage.class */
public class CertificateRequestMessage implements Encodable {
    public static final int popRaVerified = 0;
    public static final int popSigningKey = 1;
    public static final int popKeyEncipherment = 2;
    public static final int popKeyAgreement = 3;
    private final CertReqMsg certReqMsg;
    private final Controls controls;

    private static CertReqMsg parseBytes(byte[] encoding) throws IOException {
        try {
            return CertReqMsg.getInstance(ASN1Primitive.fromByteArray(encoding));
        } catch (ClassCastException e) {
            throw new CertIOException("malformed data: " + e.getMessage(), e);
        } catch (IllegalArgumentException e2) {
            throw new CertIOException("malformed data: " + e2.getMessage(), e2);
        }
    }

    public CertificateRequestMessage(byte[] certReqMsg) throws IOException {
        this(parseBytes(certReqMsg));
    }

    public CertificateRequestMessage(CertReqMsg certReqMsg) {
        this.certReqMsg = certReqMsg;
        this.controls = certReqMsg.getCertReq().getControls();
    }

    public CertReqMsg toASN1Structure() {
        return this.certReqMsg;
    }

    public CertTemplate getCertTemplate() {
        return this.certReqMsg.getCertReq().getCertTemplate();
    }

    public boolean hasControls() {
        return this.controls != null;
    }

    public boolean hasControl(ASN1ObjectIdentifier type) {
        return findControl(type) != null;
    }

    public Control getControl(ASN1ObjectIdentifier type) {
        AttributeTypeAndValue found = findControl(type);
        if (found != null) {
            if (found.getType().equals(CRMFObjectIdentifiers.id_regCtrl_pkiArchiveOptions)) {
                return new PKIArchiveControl(PKIArchiveOptions.getInstance(found.getValue()));
            }
            if (found.getType().equals(CRMFObjectIdentifiers.id_regCtrl_regToken)) {
                return new RegTokenControl(DERUTF8String.getInstance(found.getValue()));
            }
            if (found.getType().equals(CRMFObjectIdentifiers.id_regCtrl_authenticator)) {
                return new AuthenticatorControl(DERUTF8String.getInstance(found.getValue()));
            }
            return null;
        }
        return null;
    }

    private AttributeTypeAndValue findControl(ASN1ObjectIdentifier type) {
        if (this.controls == null) {
            return null;
        }
        AttributeTypeAndValue[] tAndVs = this.controls.toAttributeTypeAndValueArray();
        AttributeTypeAndValue found = null;
        for (int i = 0; i != tAndVs.length; i++) {
            if (tAndVs[i].getType().equals(type)) {
                found = tAndVs[i];
                break;
            }
        }
        return found;
    }

    public boolean hasProofOfPossession() {
        return this.certReqMsg.getPopo() != null;
    }

    public int getProofOfPossessionType() {
        return this.certReqMsg.getPopo().getType();
    }

    public boolean hasSigningKeyProofOfPossessionWithPKMAC() {
        ProofOfPossession pop = this.certReqMsg.getPopo();
        if (pop.getType() == 1) {
            POPOSigningKey popoSign = POPOSigningKey.getInstance(pop.getObject());
            return popoSign.getPoposkInput().getPublicKeyMAC() != null;
        }
        return false;
    }

    public boolean isValidSigningKeyPOP(ContentVerifierProvider verifierProvider) throws IllegalStateException, CRMFException {
        ProofOfPossession pop = this.certReqMsg.getPopo();
        if (pop.getType() == 1) {
            POPOSigningKey popoSign = POPOSigningKey.getInstance(pop.getObject());
            if (popoSign.getPoposkInput() != null && popoSign.getPoposkInput().getPublicKeyMAC() != null) {
                throw new IllegalStateException("verification requires password check");
            }
            return verifySignature(verifierProvider, popoSign);
        }
        throw new IllegalStateException("not Signing Key type of proof of possession");
    }

    public boolean isValidSigningKeyPOP(ContentVerifierProvider verifierProvider, PKMACBuilder macBuilder, char[] password) throws IllegalStateException, CRMFException {
        ProofOfPossession pop = this.certReqMsg.getPopo();
        if (pop.getType() == 1) {
            POPOSigningKey popoSign = POPOSigningKey.getInstance(pop.getObject());
            if (popoSign.getPoposkInput() == null || popoSign.getPoposkInput().getSender() != null) {
                throw new IllegalStateException("no PKMAC present in proof of possession");
            }
            PKMACValue pkMAC = popoSign.getPoposkInput().getPublicKeyMAC();
            PKMACValueVerifier macVerifier = new PKMACValueVerifier(macBuilder);
            if (macVerifier.isValid(pkMAC, password, getCertTemplate().getPublicKey())) {
                return verifySignature(verifierProvider, popoSign);
            }
            return false;
        }
        throw new IllegalStateException("not Signing Key type of proof of possession");
    }

    private boolean verifySignature(ContentVerifierProvider verifierProvider, POPOSigningKey popoSign) throws CRMFException {
        try {
            ContentVerifier verifier = verifierProvider.get(popoSign.getAlgorithmIdentifier());
            if (popoSign.getPoposkInput() != null) {
                CRMFUtil.derEncodeToStream(popoSign.getPoposkInput(), verifier.getOutputStream());
            } else {
                CRMFUtil.derEncodeToStream(this.certReqMsg.getCertReq(), verifier.getOutputStream());
            }
            return verifier.verify(popoSign.getSignature().getOctets());
        } catch (OperatorCreationException e) {
            throw new CRMFException("unable to create verifier: " + e.getMessage(), e);
        }
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.certReqMsg.getEncoded();
    }
}
