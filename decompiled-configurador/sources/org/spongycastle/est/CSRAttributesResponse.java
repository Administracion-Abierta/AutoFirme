package org.spongycastle.est;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.est.AttrOrOID;
import org.spongycastle.asn1.est.CsrAttrs;
import org.spongycastle.util.Encodable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/CSRAttributesResponse.class */
public class CSRAttributesResponse implements Encodable {
    private final CsrAttrs csrAttrs;
    private final HashMap<ASN1ObjectIdentifier, AttrOrOID> index;

    public CSRAttributesResponse(byte[] responseEncoding) throws ESTException {
        this(parseBytes(responseEncoding));
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

    private static CsrAttrs parseBytes(byte[] responseEncoding) throws ESTException {
        try {
            return CsrAttrs.getInstance(ASN1Primitive.fromByteArray(responseEncoding));
        } catch (Exception e) {
            throw new ESTException("malformed data: " + e.getMessage(), e);
        }
    }

    public boolean hasRequirement(ASN1ObjectIdentifier requirementOid) {
        return this.index.containsKey(requirementOid);
    }

    public boolean isAttribute(ASN1ObjectIdentifier requirementOid) {
        return this.index.containsKey(requirementOid) && !this.index.get(requirementOid).isOid();
    }

    public boolean isEmpty() {
        return this.csrAttrs.size() == 0;
    }

    public Collection<ASN1ObjectIdentifier> getRequirements() {
        return this.index.keySet();
    }

    @Override // org.spongycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        return this.csrAttrs.getEncoded();
    }
}
