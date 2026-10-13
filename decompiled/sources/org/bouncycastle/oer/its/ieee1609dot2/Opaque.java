package org.bouncycastle.oer.its.ieee1609dot2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.OERInputStream;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/oer/its/ieee1609dot2/Opaque.class */
public class Opaque extends ASN1Object {
    private final byte[] content;

    public Opaque(byte[] bArr) {
        this.content = Arrays.clone(bArr);
    }

    private Opaque(ASN1OctetString aSN1OctetString) {
        this(aSN1OctetString.getOctets());
    }

    public static Opaque getInstance(Object obj) {
        if (obj instanceof Opaque) {
            return (Opaque) obj;
        }
        if (obj != null) {
            return new Opaque(ASN1OctetString.getInstance(obj));
        }
        return null;
    }

    @Override // org.bouncycastle.asn1.ASN1Object, org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        return new DEROctetString(this.content);
    }

    public byte[] getContent() {
        return this.content;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.content);
    }

    public static <T> T getValue(final Class<T> cls, final Element element, Opaque opaque) {
        return (T) AccessController.doPrivileged(new PrivilegedAction<T>() { // from class: org.bouncycastle.oer.its.ieee1609dot2.Opaque.1
            @Override // java.security.PrivilegedAction
            public T run() {
                try {
                    return (T) cls.cast(cls.getMethod("getInstance", Object.class).invoke(null, OERInputStream.parse(Opaque.this.content, element)));
                } catch (Exception e) {
                    throw new IllegalStateException("could not invoke getInstance on type " + e.getMessage(), e);
                }
            }
        });
    }
}
