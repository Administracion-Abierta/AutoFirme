package org.bouncycastle.crypto.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.util.Encodable;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/JournaledAlgorithm.class */
public class JournaledAlgorithm implements Encodable, Serializable {
    private transient JournalingSecureRandom journaling;
    private transient AlgorithmIdentifier algID;

    public JournaledAlgorithm(AlgorithmIdentifier algorithmIdentifier, JournalingSecureRandom journalingSecureRandom) {
        if (algorithmIdentifier == null) {
            throw new NullPointerException("AlgorithmIdentifier passed to JournaledAlgorithm is null");
        }
        if (journalingSecureRandom == null) {
            throw new NullPointerException("JournalingSecureRandom passed to JournaledAlgorithm is null");
        }
        this.journaling = journalingSecureRandom;
        this.algID = algorithmIdentifier;
    }

    public JournaledAlgorithm(byte[] bArr) {
        this(bArr, CryptoServicesRegistrar.getSecureRandom());
    }

    public JournaledAlgorithm(byte[] bArr, SecureRandom secureRandom) {
        if (bArr == null) {
            throw new NullPointerException("encoding passed to JournaledAlgorithm is null");
        }
        if (secureRandom == null) {
            throw new NullPointerException("random passed to JournaledAlgorithm is null");
        }
        initFromEncoding(bArr, secureRandom);
    }

    private void initFromEncoding(byte[] bArr, SecureRandom secureRandom) {
        ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(bArr);
        this.algID = AlgorithmIdentifier.getInstance(aSN1Sequence.getObjectAt(0));
        this.journaling = new JournalingSecureRandom(ASN1OctetString.getInstance(aSN1Sequence.getObjectAt(1)).getOctets(), secureRandom);
    }

    public JournalingSecureRandom getJournalingSecureRandom() {
        return this.journaling;
    }

    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return this.algID;
    }

    public void storeState(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("file for storage is null in JournaledAlgorithm");
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            storeState(fileOutputStream);
        } finally {
            fileOutputStream.close();
        }
    }

    public void storeState(OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new NullPointerException("output stream for storage is null in JournaledAlgorithm");
        }
        outputStream.write(getEncoded());
    }

    public static JournaledAlgorithm getState(InputStream inputStream, SecureRandom secureRandom) throws IOException, ClassNotFoundException {
        if (inputStream == null) {
            throw new NullPointerException("stream for loading is null in JournaledAlgorithm");
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        try {
            return new JournaledAlgorithm(Streams.readAll(bufferedInputStream), secureRandom);
        } finally {
            bufferedInputStream.close();
        }
    }

    public static JournaledAlgorithm getState(File file, SecureRandom secureRandom) throws IOException, ClassNotFoundException {
        if (file == null) {
            throw new NullPointerException("File for loading is null in JournaledAlgorithm");
        }
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        try {
            return new JournaledAlgorithm(Streams.readAll(bufferedInputStream), secureRandom);
        } finally {
            bufferedInputStream.close();
        }
    }

    @Override // org.bouncycastle.util.Encodable
    public byte[] getEncoded() throws IOException {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        aSN1EncodableVector.add(this.algID);
        aSN1EncodableVector.add(new DEROctetString(this.journaling.getFullTranscript()));
        return new DERSequence(aSN1EncodableVector).getEncoded();
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        initFromEncoding((byte[]) objectInputStream.readObject(), CryptoServicesRegistrar.getSecureRandom());
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }
}
