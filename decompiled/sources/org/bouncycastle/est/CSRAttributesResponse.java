package org.bouncycastle.est;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.est.AttrOrOID;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.util.Encodable;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/CSRAttributesResponse.class */
public class CSRAttributesResponse implements Encodable {
    private final CsrAttrs csrAttrs;
    private final HashMap<ASN1ObjectIdentifier, AttrOrOID> index;

    public CSRAttributesResponse(byte[] bArr) throws ESTException {
        this(parseBytes(bArr));
    }

    public CSRAttributesResponse(CsrAttrs csrAttrs) throws ESTException {
        this.csrAttrs = csrAttrs;
        this.index = new HashMap<>(csrAttrs.size());
        AttrOrOID[] attrOrOIDs = csrAttrs.getAttrOrOIDs();
        for (int i = 0; i != attrOrOIDs.length; i++) {
            AttrOrOID attrOrOID = attrOrOIDs[i];
            if (attrOrOID.isOid()) {
                this.index.put(attrOrOID.getOid(), attrOrOID);
            } else {
                this.index.put(attrOrOID.getAttribute().getAttrType(), attrOrOID);
            }
        }
    }

    private static CsrAttrs parseBytes(byte[] bArr) throws ESTException {
        try {
            return CsrAttrs.getInstance(ASN1Primitive.fromByteArray(bArr));
        } catch (Exception e) {
            throw new ESTException("malformed data: " + e.getMessage(), e);
        }
    }

    public boolean hasRequirement(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.index.containsKey(aSN1ObjectIdentifier);
    }

    public boolean isAttribute(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return this.index.containsKey(aSN1ObjectIdentifier) && !this.index.get(aSN1ObjectIdentifier).isOid();
    }

    public boolean isEmpty() {
        return this.csrAttrs.size() == 0;
    }

    public Collection<ASN1ObjectIdentifier> getRequirements() {
        return this.index.keySet();
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.csrAttrs.getEncoded();
    }
}
