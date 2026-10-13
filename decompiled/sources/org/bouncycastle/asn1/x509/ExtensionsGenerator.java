package org.bouncycastle.asn1.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1ParsingException;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/x509/ExtensionsGenerator.class */
public class ExtensionsGenerator {
    private Hashtable extensions = new Hashtable();
    private Vector extOrdering = new Vector();
    private static final Set dupsAllowed;

    public void reset() {
        this.extensions = new Hashtable();
        this.extOrdering = new Vector();
    }

    public void addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, ASN1Encodable aSN1Encodable) throws IOException {
        Extension extension = (Extension) this.extensions.get(aSN1ObjectIdentifier);
        if (extension != null) {
            implAddExtensionDup(extension, z, aSN1Encodable.toASN1Primitive().getEncoded("DER"));
        } else {
            implAddExtension(new Extension(aSN1ObjectIdentifier, z, new DEROctetString(aSN1Encodable)));
        }
    }

    public void addExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, byte[] bArr) {
        Extension extension = (Extension) this.extensions.get(aSN1ObjectIdentifier);
        if (extension != null) {
            implAddExtensionDup(extension, z, bArr);
        } else {
            implAddExtension(new Extension(aSN1ObjectIdentifier, z, bArr));
        }
    }

    public void addExtension(Extension extension) {
        if (hasExtension(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " already added");
        }
        implAddExtension(extension);
    }

    public void addExtension(Extensions extensions) {
        addExtensions(extensions);
    }

    public void addExtensions(Extensions extensions) {
        ASN1ObjectIdentifier[] extensionOIDs = extensions.getExtensionOIDs();
        for (int i = 0; i != extensionOIDs.length; i++) {
            ASN1ObjectIdentifier aSN1ObjectIdentifier = extensionOIDs[i];
            Extension extension = extensions.getExtension(aSN1ObjectIdentifier);
            addExtension(ASN1ObjectIdentifier.getInstance(aSN1ObjectIdentifier), extension.isCritical(), extension.getExtnValue().getOctets());
        }
    }

    public void replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, ASN1Encodable aSN1Encodable) throws IOException {
        replaceExtension(new Extension(aSN1ObjectIdentifier, z, new DEROctetString(aSN1Encodable)));
    }

    public void replaceExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier, boolean z, byte[] bArr) {
        replaceExtension(new Extension(aSN1ObjectIdentifier, z, bArr));
    }

    public void replaceExtension(Extension extension) {
        if (!hasExtension(extension.getExtnId())) {
            throw new IllegalArgumentException("extension " + extension.getExtnId() + " not present");
        }
        this.extensions.put(extension.getExtnId(), extension);
    }

    public void removeExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (!hasExtension(aSN1ObjectIdentifier)) {
            throw new IllegalArgumentException("extension " + aSN1ObjectIdentifier + " not present");
        }
        this.extOrdering.removeElement(aSN1ObjectIdentifier);
        this.extensions.remove(aSN1ObjectIdentifier);
    }

    public boolean hasExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.extensions.containsKey(aSN1ObjectIdentifier);
    }

    public Extension getExtension(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (Extension) this.extensions.get(aSN1ObjectIdentifier);
    }

    public boolean isEmpty() {
        return this.extOrdering.isEmpty();
    }

    public Extensions generate() {
        Extension[] extensionArr = new Extension[this.extOrdering.size()];
        for (int i = 0; i != this.extOrdering.size(); i++) {
            extensionArr[i] = (Extension) this.extensions.get(this.extOrdering.elementAt(i));
        }
        return new Extensions(extensionArr);
    }

    private void implAddExtension(Extension extension) {
        this.extOrdering.addElement(extension.getExtnId());
        this.extensions.put(extension.getExtnId(), extension);
    }

    private void implAddExtensionDup(Extension extension, boolean z, byte[] bArr) {
        ASN1ObjectIdentifier extnId = extension.getExtnId();
        if (!dupsAllowed.contains(extnId)) {
            throw new IllegalArgumentException("extension " + extnId + " already added");
        }
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(DEROctetString.getInstance(extension.getExtnValue()).getOctets());
        ASN1Sequence aSN1Sequence2 = ASN1Sequence.getInstance(bArr);
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector(aSN1Sequence.size() + aSN1Sequence2.size());
        Enumeration objects = aSN1Sequence.getObjects();
        while (objects.hasMoreElements()) {
            aSN1EncodableVector.add((ASN1Encodable) objects.nextElement());
        }
        Enumeration objects2 = aSN1Sequence2.getObjects();
        while (objects2.hasMoreElements()) {
            aSN1EncodableVector.add((ASN1Encodable) objects2.nextElement());
        }
        try {
            this.extensions.put(extnId, new Extension(extnId, z, new DEROctetString(new DERSequence(aSN1EncodableVector))));
        } catch (IOException e) {
            throw new ASN1ParsingException(e.getMessage(), e);
        }
    }

    static {
        HashSet hashSet = new HashSet();
        hashSet.add(Extension.subjectAlternativeName);
        hashSet.add(Extension.issuerAlternativeName);
        hashSet.add(Extension.subjectDirectoryAttributes);
        hashSet.add(Extension.certificateIssuer);
        dupsAllowed = Collections.unmodifiableSet(hashSet);
    }
}
