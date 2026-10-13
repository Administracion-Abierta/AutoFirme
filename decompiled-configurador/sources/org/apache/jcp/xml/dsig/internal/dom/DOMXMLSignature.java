package org.apache.jcp.xml.dsig.internal.dom;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.Manifest;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLObject;
import javax.xml.crypto.dsig.XMLSignContext;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLValidateContext;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXMLSignature.class */
public final class DOMXMLSignature extends DOMStructure implements XMLSignature {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) DOMXMLSignature.class);
    private final String id;
    private final XMLSignature.SignatureValue sv;
    private KeyInfo ki;
    private List<XMLObject> objects;
    private final SignedInfo si;
    private Document ownerDoc;
    private Element localSigElem;
    private Element sigElem;
    private boolean validationStatus;
    private boolean validated;
    private KeySelectorResult ksr;
    private Map<String, XMLStructure> signatureIdMap;

    static {
        Init.init();
    }

    public DOMXMLSignature(SignedInfo si, KeyInfo ki, List<? extends XMLObject> objs, String id, String signatureValueId) {
        this.ownerDoc = null;
        this.localSigElem = null;
        this.sigElem = null;
        this.validated = false;
        if (si == null) {
            throw new NullPointerException("signedInfo cannot be null");
        }
        this.si = si;
        this.id = id;
        this.sv = new DOMSignatureValue(signatureValueId);
        if (objs == null) {
            this.objects = Collections.emptyList();
        } else {
            this.objects = Collections.unmodifiableList(new ArrayList(objs));
            int size = this.objects.size();
            for (int i = 0; i < size; i++) {
                if (!(this.objects.get(i) instanceof XMLObject)) {
                    throw new ClassCastException("objs[" + i + "] is not an XMLObject");
                }
            }
        }
        this.ki = ki;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public DOMXMLSignature(Element sigElem, XMLCryptoContext context, Provider provider) throws MarshalException {
        this.ownerDoc = null;
        this.localSigElem = null;
        this.sigElem = null;
        this.validated = false;
        this.localSigElem = sigElem;
        this.ownerDoc = this.localSigElem.getOwnerDocument();
        this.id = DOMUtils.getAttributeValue(this.localSigElem, "Id");
        Element siElem = DOMUtils.getFirstChildElement(this.localSigElem, "SignedInfo", "http://www.w3.org/2000/09/xmldsig#");
        this.si = new DOMSignedInfo(siElem, context, provider);
        Element sigValElem = DOMUtils.getNextSiblingElement(siElem, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#");
        this.sv = new DOMSignatureValue(sigValElem);
        Element nextSibling = DOMUtils.getNextSiblingElement(sigValElem);
        if (nextSibling != null && Constants._TAG_KEYINFO.equals(nextSibling.getLocalName()) && "http://www.w3.org/2000/09/xmldsig#".equals(nextSibling.getNamespaceURI())) {
            this.ki = new DOMKeyInfo(nextSibling, context, provider);
            nextSibling = DOMUtils.getNextSiblingElement(nextSibling);
        }
        if (nextSibling == null) {
            this.objects = Collections.emptyList();
            return;
        }
        List<XMLObject> tempObjects = new ArrayList<>();
        while (nextSibling != null) {
            String name = nextSibling.getLocalName();
            String namespace = nextSibling.getNamespaceURI();
            if (!Constants._TAG_OBJECT.equals(name) || !"http://www.w3.org/2000/09/xmldsig#".equals(namespace)) {
                throw new MarshalException("Invalid element name: " + namespace + ":" + name + ", expected KeyInfo or Object");
            }
            tempObjects.add(new DOMXMLObject(nextSibling, context, provider));
            nextSibling = DOMUtils.getNextSiblingElement(nextSibling);
        }
        this.objects = Collections.unmodifiableList(tempObjects);
    }

    public String getId() {
        return this.id;
    }

    public KeyInfo getKeyInfo() {
        return this.ki;
    }

    public SignedInfo getSignedInfo() {
        return this.si;
    }

    public List<XMLObject> getObjects() {
        return this.objects;
    }

    public XMLSignature.SignatureValue getSignatureValue() {
        return this.sv;
    }

    public KeySelectorResult getKeySelectorResult() {
        return this.ksr;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
    public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        marshal(parent, null, dsPrefix, context);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    public void marshal(Node parent, Node nextSibling, String dsPrefix, DOMCryptoContext context) throws MarshalException {
        this.ownerDoc = DOMUtils.getOwnerDocument(parent);
        this.sigElem = DOMUtils.createElement(this.ownerDoc, "Signature", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
        if (dsPrefix == null || dsPrefix.length() == 0) {
            this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", CanonicalizerBase.XMLNS, "http://www.w3.org/2000/09/xmldsig#");
        } else {
            this.sigElem.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + dsPrefix, "http://www.w3.org/2000/09/xmldsig#");
        }
        ((DOMSignedInfo) this.si).marshal(this.sigElem, dsPrefix, context);
        ((DOMSignatureValue) this.sv).marshal(this.sigElem, dsPrefix, context);
        if (this.ki != null) {
            ((DOMKeyInfo) this.ki).marshal(this.sigElem, null, dsPrefix, context);
        }
        for (XMLObject object : this.objects) {
            ((DOMXMLObject) object).marshal(this.sigElem, dsPrefix, context);
        }
        DOMUtils.setAttributeID(this.sigElem, "Id", this.id);
        parent.insertBefore(this.sigElem, nextSibling);
    }

    public boolean validate(XMLValidateContext vc) throws XMLSignatureException {
        if (vc == null) {
            throw new NullPointerException("validateContext is null");
        }
        if (!(vc instanceof DOMValidateContext)) {
            throw new ClassCastException("validateContext must be of type DOMValidateContext");
        }
        if (this.validated) {
            return this.validationStatus;
        }
        boolean sigValidity = this.sv.validate(vc);
        if (!sigValidity) {
            this.validationStatus = false;
            this.validated = true;
            return this.validationStatus;
        }
        List<Reference> refs = this.si.getReferences();
        boolean validateRefs = true;
        int size = refs.size();
        for (int i = 0; validateRefs && i < size; i++) {
            Reference ref = refs.get(i);
            boolean refValid = ref.validate(vc);
            LOG.debug("Reference [{}] is valid: {}", ref.getURI(), Boolean.valueOf(refValid));
            validateRefs &= refValid;
        }
        if (!validateRefs) {
            LOG.debug("Couldn't validate the References");
            this.validationStatus = false;
            this.validated = true;
            return this.validationStatus;
        }
        boolean validateMans = true;
        if (Boolean.TRUE.equals(vc.getProperty("org.jcp.xml.dsig.validateManifests"))) {
            int size2 = this.objects.size();
            for (int i2 = 0; validateMans && i2 < size2; i2++) {
                XMLObject xo = this.objects.get(i2);
                List<XMLStructure> content = xo.getContent();
                int csize = content.size();
                for (int j = 0; validateMans && j < csize; j++) {
                    XMLStructure xMLStructure = content.get(j);
                    if (xMLStructure instanceof Manifest) {
                        LOG.debug("validating manifest");
                        Manifest man = (Manifest) xMLStructure;
                        List<Reference> manRefs = man.getReferences();
                        int rsize = manRefs.size();
                        for (int k = 0; validateMans && k < rsize; k++) {
                            Reference ref2 = manRefs.get(k);
                            boolean refValid2 = ref2.validate(vc);
                            LOG.debug("Manifest ref [{}] is valid: {}", ref2.getURI(), Boolean.valueOf(refValid2));
                            validateMans &= refValid2;
                        }
                    }
                }
            }
        }
        this.validationStatus = validateMans;
        this.validated = true;
        return this.validationStatus;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.MarshalException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    public void sign(XMLSignContext signContext) throws MarshalException, XMLSignatureException {
        if (signContext == null) {
            throw new NullPointerException("signContext cannot be null");
        }
        DOMSignContext context = (DOMSignContext) signContext;
        marshal(context.getParent(), context.getNextSibling(), DOMUtils.getSignaturePrefix(context), context);
        List<Reference> allReferences = new ArrayList<>();
        this.signatureIdMap = new HashMap();
        this.signatureIdMap.put(this.id, this);
        this.signatureIdMap.put(this.si.getId(), this.si);
        List<Reference> refs = this.si.getReferences();
        for (Reference reference : refs) {
            this.signatureIdMap.put(reference.getId(), reference);
        }
        Iterator<XMLObject> it = this.objects.iterator();
        while (it.hasNext()) {
            XMLStructure xMLStructure = (XMLObject) it.next();
            this.signatureIdMap.put(xMLStructure.getId(), xMLStructure);
            List<XMLStructure> content = xMLStructure.getContent();
            for (XMLStructure xs : content) {
                if (xs instanceof Manifest) {
                    XMLStructure xMLStructure2 = (Manifest) xs;
                    this.signatureIdMap.put(xMLStructure2.getId(), xMLStructure2);
                    List<Reference> manRefs = xMLStructure2.getReferences();
                    for (Reference reference2 : manRefs) {
                        allReferences.add(reference2);
                        this.signatureIdMap.put(reference2.getId(), reference2);
                    }
                }
            }
        }
        allReferences.addAll(refs);
        Iterator<Reference> it2 = allReferences.iterator();
        while (it2.hasNext()) {
            digestReference((DOMReference) it2.next(), signContext);
        }
        for (Reference ref : allReferences) {
            if (!((DOMReference) ref).isDigested()) {
                ((DOMReference) ref).digest(signContext);
            }
        }
        try {
            KeySelectorResult keySelectorResult = signContext.getKeySelector().select(this.ki, KeySelector.Purpose.SIGN, this.si.getSignatureMethod(), signContext);
            Key signingKey = keySelectorResult.getKey();
            if (signingKey == null) {
                throw new XMLSignatureException("the keySelector did not find a signing key");
            }
            this.ksr = keySelectorResult;
            try {
                byte[] val = ((AbstractDOMSignatureMethod) this.si.getSignatureMethod()).sign(signingKey, this.si, signContext);
                ((DOMSignatureValue) this.sv).setValue(val);
                this.localSigElem = this.sigElem;
            } catch (InvalidKeyException ike) {
                throw new XMLSignatureException(ike);
            }
        } catch (KeySelectorException kse) {
            throw new XMLSignatureException("cannot find signing key", kse);
        }
    }

    public boolean equals(Object o) {
        boolean zEquals;
        boolean zEquals2;
        if (this == o) {
            return true;
        }
        if (!(o instanceof XMLSignature)) {
            return false;
        }
        XMLSignature osig = (XMLSignature) o;
        if (this.id == null) {
            zEquals = osig.getId() == null;
        } else {
            zEquals = this.id.equals(osig.getId());
        }
        boolean idEqual = zEquals;
        if (this.ki == null) {
            zEquals2 = osig.getKeyInfo() == null;
        } else {
            zEquals2 = this.ki.equals(osig.getKeyInfo());
        }
        boolean keyInfoEqual = zEquals2;
        return idEqual && keyInfoEqual && this.sv.equals(osig.getSignatureValue()) && this.si.equals(osig.getSignedInfo()) && this.objects.equals(osig.getObjects());
    }

    public int hashCode() {
        int result = 17;
        if (this.id != null) {
            result = (31 * 17) + this.id.hashCode();
        }
        if (this.ki != null) {
            result = (31 * result) + this.ki.hashCode();
        }
        return (31 * ((31 * ((31 * result) + this.sv.hashCode())) + this.si.hashCode())) + this.objects.hashCode();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
    private void digestReference(DOMReference ref, XMLSignContext signContext) throws XMLSignatureException {
        if (ref.isDigested()) {
            return;
        }
        String uri = ref.getURI();
        if (Utils.sameDocumentURI(uri)) {
            String parsedId = Utils.parseIdFromSameDocumentURI(uri);
            if (parsedId != null && this.signatureIdMap.containsKey(parsedId)) {
                Manifest manifest = (XMLStructure) this.signatureIdMap.get(parsedId);
                if (manifest instanceof DOMReference) {
                    digestReference((DOMReference) manifest, signContext);
                } else if (manifest instanceof Manifest) {
                    Manifest man = manifest;
                    List<Reference> manRefs = DOMManifest.getManifestReferences(man);
                    for (Reference manRef : manRefs) {
                        digestReference((DOMReference) manRef, signContext);
                    }
                }
            }
            if (uri.length() == 0) {
                List<Transform> transforms = ref.getTransforms();
                for (Transform transform : transforms) {
                    String transformAlg = transform.getAlgorithm();
                    if (transformAlg.equals(Transforms.TRANSFORM_XPATH) || transformAlg.equals("http://www.w3.org/2002/06/xmldsig-filter2")) {
                        return;
                    }
                }
            }
        }
        ref.digest(signContext);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/jcp/xml/dsig/internal/dom/DOMXMLSignature$DOMSignatureValue.class */
    public class DOMSignatureValue extends DOMStructure implements XMLSignature.SignatureValue {
        private String id;
        private byte[] value;
        private String valueBase64;
        private Element sigValueElem;
        private boolean validated = false;
        private boolean validationStatus;

        DOMSignatureValue(String id) {
            this.id = id;
        }

        DOMSignatureValue(Element sigValueElem) throws MarshalException {
            String content = XMLUtils.getFullTextChildrenFromNode(sigValueElem);
            this.value = XMLUtils.decode(content);
            Attr attr = sigValueElem.getAttributeNodeNS(null, "Id");
            if (attr != null) {
                this.id = attr.getValue();
                sigValueElem.setIdAttributeNode(attr, true);
            } else {
                this.id = null;
            }
            this.sigValueElem = sigValueElem;
        }

        public String getId() {
            return this.id;
        }

        public byte[] getValue() {
            if (this.value == null) {
                return null;
            }
            return (byte[]) this.value.clone();
        }

        public String getEncodedValue() {
            return this.valueBase64;
        }

        /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.crypto.dsig.XMLSignatureException */
        public boolean validate(XMLValidateContext validateContext) throws XMLSignatureException {
            if (validateContext == null) {
                throw new NullPointerException("context cannot be null");
            }
            if (!this.validated) {
                SignatureMethod sm = DOMXMLSignature.this.si.getSignatureMethod();
                Key validationKey = null;
                KeySelectorResult ksResult = null;
                try {
                    KeySelector keySelector = validateContext.getKeySelector();
                    if (keySelector != null) {
                        ksResult = keySelector.select(DOMXMLSignature.this.ki, KeySelector.Purpose.VERIFY, sm, validateContext);
                        if (ksResult != null) {
                            validationKey = ksResult.getKey();
                        }
                    }
                    if (validationKey == null) {
                        throw new XMLSignatureException("the keyselector did not find a validation key");
                    }
                    try {
                        this.validationStatus = ((AbstractDOMSignatureMethod) sm).verify(validationKey, DOMXMLSignature.this.si, this.value, validateContext);
                        this.validated = true;
                        DOMXMLSignature.this.ksr = ksResult;
                        return this.validationStatus;
                    } catch (Exception e) {
                        throw new XMLSignatureException(e);
                    }
                } catch (KeySelectorException kse) {
                    throw new XMLSignatureException("cannot find validation key", kse);
                }
            }
            return this.validationStatus;
        }

        public boolean equals(Object o) {
            boolean zEquals;
            if (this == o) {
                return true;
            }
            if (!(o instanceof XMLSignature.SignatureValue)) {
                return false;
            }
            XMLSignature.SignatureValue osv = (XMLSignature.SignatureValue) o;
            if (this.id == null) {
                zEquals = osv.getId() == null;
            } else {
                zEquals = this.id.equals(osv.getId());
            }
            boolean idEqual = zEquals;
            return idEqual;
        }

        public int hashCode() {
            int result = 17;
            if (this.id != null) {
                result = (31 * 17) + this.id.hashCode();
            }
            return result;
        }

        @Override // org.apache.jcp.xml.dsig.internal.dom.DOMStructure
        public void marshal(Node parent, String dsPrefix, DOMCryptoContext context) throws MarshalException {
            this.sigValueElem = DOMUtils.createElement(DOMXMLSignature.this.ownerDoc, "SignatureValue", "http://www.w3.org/2000/09/xmldsig#", dsPrefix);
            if (this.valueBase64 != null) {
                this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
            }
            DOMUtils.setAttributeID(this.sigValueElem, "Id", this.id);
            parent.appendChild(this.sigValueElem);
        }

        void setValue(byte[] value) {
            this.value = value;
            this.valueBase64 = XMLUtils.encodeToString(value);
            this.sigValueElem.appendChild(DOMXMLSignature.this.ownerDoc.createTextNode(this.valueBase64));
        }
    }
}
