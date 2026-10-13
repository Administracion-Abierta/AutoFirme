package org.apache.xml.security.encryption.params;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/params/KeyAgreementParameters.class */
public class KeyAgreementParameters implements AlgorithmParameterSpec {
    private final KeyDerivationParameters KeyDerivationParameter;
    private final ActorType actorType;
    private final String keyAgreementAlgorithm;
    private PublicKey originatorPublicKey;
    private PrivateKey originatorPrivateKey;
    private PublicKey recipientPublicKey;
    private PrivateKey recipientPrivateKey;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/params/KeyAgreementParameters$ActorType.class */
    public enum ActorType {
        ORIGINATOR,
        RECIPIENT
    }

    public KeyAgreementParameters(ActorType actorType, String keyAgreementAlgorithm, KeyDerivationParameters keyDerivationParameter) {
        this.actorType = actorType;
        this.KeyDerivationParameter = keyDerivationParameter;
        this.keyAgreementAlgorithm = keyAgreementAlgorithm;
    }

    public KeyDerivationParameters getKeyDerivationParameter() {
        return this.KeyDerivationParameter;
    }

    public String getKeyAgreementAlgorithm() {
        return this.keyAgreementAlgorithm;
    }

    public void setOriginatorKeyPair(KeyPair originatorKeyPair) {
        this.originatorPublicKey = originatorKeyPair.getPublic();
        this.originatorPrivateKey = originatorKeyPair.getPrivate();
    }

    public PublicKey getOriginatorPublicKey() {
        return this.originatorPublicKey;
    }

    public void setOriginatorPublicKey(PublicKey originatorPublicKey) {
        this.originatorPublicKey = originatorPublicKey;
    }

    public PrivateKey getOriginatorPrivateKey() {
        return this.originatorPrivateKey;
    }

    public void setOriginatorPrivateKey(PrivateKey originatorPrivateKey) {
        if (this.actorType != ActorType.ORIGINATOR) {
            throw new IllegalStateException("Cannot set originator private key when actor type is not ORIGINATOR");
        }
        this.originatorPrivateKey = originatorPrivateKey;
    }

    public PublicKey getRecipientPublicKey() {
        return this.recipientPublicKey;
    }

    public void setRecipientPublicKey(PublicKey recipientPublicKey) {
        if (this.actorType != ActorType.ORIGINATOR) {
            throw new IllegalStateException("Cannot set recipient public key when actor type is not ORIGINATOR");
        }
        this.recipientPublicKey = recipientPublicKey;
    }

    public PrivateKey getRecipientPrivateKey() {
        return this.recipientPrivateKey;
    }

    public void setRecipientPrivateKey(PrivateKey recipientPrivateKey) {
        if (this.actorType != ActorType.RECIPIENT) {
            throw new IllegalStateException("Cannot set recipient private key when actor type is not RECIPIENT");
        }
        this.recipientPrivateKey = recipientPrivateKey;
    }

    public ActorType getActorType() {
        return this.actorType;
    }

    public PublicKey getAgreementPublicKey() {
        return this.actorType == ActorType.ORIGINATOR ? this.recipientPublicKey : this.originatorPublicKey;
    }

    public PrivateKey getAgreementPrivateKey() {
        return this.actorType == ActorType.ORIGINATOR ? this.originatorPrivateKey : this.recipientPrivateKey;
    }
}
