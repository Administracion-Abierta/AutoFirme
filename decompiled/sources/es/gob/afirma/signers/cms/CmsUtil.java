package es.gob.afirma.signers.cms;

import es.gob.afirma.core.AOException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Set;
import org.spongycastle.asn1.BEROctetString;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.cms.CMSProcessableByteArray;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/cms/CmsUtil.class */
final class CmsUtil {
    private CmsUtil() {
    }

    static ASN1OctetString firma(String str, PrivateKey privateKey, ASN1Set aSN1Set) throws AOException {
        try {
            Signature signature = Signature.getInstance(str);
            try {
                byte[] encoded = aSN1Set.getEncoded("DER");
                try {
                    signature.initSign(privateKey);
                    try {
                        signature.update(encoded);
                        try {
                            return new DEROctetString(signature.sign());
                        } catch (Exception e) {
                            throw new AOException("Error durante el proceso de firma", e);
                        }
                    } catch (SignatureException e2) {
                        throw new AOException("Error al configurar la informacion de firma", e2);
                    }
                } catch (Exception e3) {
                    throw new AOException("Error al inicializar la firma con la clave privada", e3);
                }
            } catch (IOException e4) {
                throw new AOException("Error obteniendo los atributos firmados", e4);
            }
        } catch (Exception e5) {
            throw new AOException("Error obteniendo la clase de firma para el algoritmo " + str, e5);
        }
    }

    static ContentInfo getContentInfo(byte[] bArr, boolean z, String str) throws IOException {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(str);
        if (!z) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                new CMSProcessableByteArray(bArr).write(byteArrayOutputStream);
                return new ContentInfo(aSN1ObjectIdentifier, new BEROctetString(byteArrayOutputStream.toByteArray()));
            } catch (Exception e) {
                throw new IOException("Error en la escritura del procesable CMS: " + e, e);
            }
        }
        return new ContentInfo(aSN1ObjectIdentifier, null);
    }
}
