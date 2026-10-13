package org.apache.xml.security.stax.ext;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.QName;
import org.apache.xml.security.utils.EncryptionConstants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/SecurePart.class */
public class SecurePart {
    private QName name;
    private boolean generateXPointer;
    private Modifier modifier;
    private String idToSecure;
    private String externalReference;
    private String[] transforms;
    private String digestMethod;
    private boolean required;
    private boolean secureEntireRequest;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/stax/ext/SecurePart$Modifier.class */
    public enum Modifier {
        Element(EncryptionConstants.TYPE_ELEMENT),
        Content(EncryptionConstants.TYPE_CONTENT);

        private final String modifier;
        private static final Map<String, Modifier> modifierMap = new HashMap();

        static {
            for (Modifier modifier : EnumSet.allOf(Modifier.class)) {
                modifierMap.put(modifier.getModifier(), modifier);
            }
        }

        Modifier(String modifier) {
            this.modifier = modifier;
        }

        public String getModifier() {
            return this.modifier;
        }

        public static Modifier getModifier(String modifier) {
            return modifierMap.get(modifier);
        }
    }

    public SecurePart(Modifier modifier) {
        this((QName) null, false, modifier);
    }

    public SecurePart(QName name, Modifier modifier) {
        this(name, false, modifier);
    }

    public SecurePart(QName name, Modifier modifier, String[] transforms, String digestMethod) {
        this(name, false, modifier, transforms, digestMethod);
    }

    public SecurePart(QName name, boolean generateXPointer, Modifier modifier) {
        this.required = true;
        this.name = name;
        this.generateXPointer = generateXPointer;
        this.modifier = modifier;
    }

    public SecurePart(QName name, boolean generateXPointer, Modifier modifier, String[] transforms, String digestMethod) {
        this.required = true;
        this.name = name;
        this.generateXPointer = generateXPointer;
        this.modifier = modifier;
        this.transforms = transforms;
        this.digestMethod = digestMethod;
    }

    public SecurePart(String externalReference) {
        this.required = true;
        this.externalReference = externalReference;
    }

    public SecurePart(String externalReference, Modifier modifier) {
        this.required = true;
        this.externalReference = externalReference;
        this.modifier = modifier;
    }

    public SecurePart(String externalReference, String[] transforms, String digestMethod) {
        this.required = true;
        this.externalReference = externalReference;
        this.transforms = transforms;
        this.digestMethod = digestMethod;
    }

    public QName getName() {
        return this.name;
    }

    public void setName(QName name) {
        this.name = name;
    }

    public Modifier getModifier() {
        return this.modifier;
    }

    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }

    public String getIdToSecure() {
        return this.idToSecure;
    }

    public void setIdToSecure(String idToSecure) {
        this.idToSecure = idToSecure;
    }

    @Deprecated
    public String getIdToSign() {
        return getIdToSecure();
    }

    @Deprecated
    public void setIdToSign(String idToSign) {
        setIdToSecure(idToSign);
    }

    public boolean isGenerateXPointer() {
        return this.generateXPointer;
    }

    public void setGenerateXPointer(boolean generateXPointer) {
        this.generateXPointer = generateXPointer;
    }

    public String getExternalReference() {
        return this.externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String[] getTransforms() {
        return this.transforms;
    }

    public void setTransforms(String[] transforms) {
        this.transforms = transforms;
    }

    public String getDigestMethod() {
        return this.digestMethod;
    }

    public void setDigestMethod(String digestMethod) {
        this.digestMethod = digestMethod;
    }

    public boolean isRequired() {
        return this.required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isSecureEntireRequest() {
        return this.secureEntireRequest;
    }

    public void setSecureEntireRequest(boolean secureEntireRequest) {
        this.secureEntireRequest = secureEntireRequest;
    }
}
