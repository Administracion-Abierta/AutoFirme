package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.BERSequenceGenerator;
import org.spongycastle.asn1.cms.CMSObjectIdentifiers;
import org.spongycastle.operator.OutputCompressor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSCompressedDataStreamGenerator.class */
public class CMSCompressedDataStreamGenerator {
    public static final String ZLIB = "1.2.840.113549.1.9.16.3.8";
    private int _bufferSize;

    public void setBufferSize(int bufferSize) {
        this._bufferSize = bufferSize;
    }

    public OutputStream open(OutputStream out, OutputCompressor compressor) throws IOException {
        return open(CMSObjectIdentifiers.data, out, compressor);
    }

    public OutputStream open(ASN1ObjectIdentifier contentOID, OutputStream out, OutputCompressor compressor) throws IOException {
        BERSequenceGenerator sGen = new BERSequenceGenerator(out);
        sGen.addObject(CMSObjectIdentifiers.compressedData);
        BERSequenceGenerator cGen = new BERSequenceGenerator(sGen.getRawOutputStream(), 0, true);
        cGen.addObject(new ASN1Integer(0L));
        cGen.addObject(compressor.getAlgorithmIdentifier());
        BERSequenceGenerator eiGen = new BERSequenceGenerator(cGen.getRawOutputStream());
        eiGen.addObject(contentOID);
        OutputStream octetStream = CMSUtils.createBEROctetOutputStream(eiGen.getRawOutputStream(), 0, true, this._bufferSize);
        return new CmsCompressedOutputStream(compressor.getOutputStream(octetStream), sGen, cGen, eiGen);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/CMSCompressedDataStreamGenerator$CmsCompressedOutputStream.class */
    private class CmsCompressedOutputStream extends OutputStream {
        private OutputStream _out;
        private BERSequenceGenerator _sGen;
        private BERSequenceGenerator _cGen;
        private BERSequenceGenerator _eiGen;

        CmsCompressedOutputStream(OutputStream out, BERSequenceGenerator sGen, BERSequenceGenerator cGen, BERSequenceGenerator eiGen) {
            this._out = out;
            this._sGen = sGen;
            this._cGen = cGen;
            this._eiGen = eiGen;
        }

        @Override // java.io.OutputStream
        public void write(int b) throws IOException {
            this._out.write(b);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes, int off, int len) throws IOException {
            this._out.write(bytes, off, len);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bytes) throws IOException {
            this._out.write(bytes);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this._out.close();
            this._eiGen.close();
            this._cGen.close();
            this._sGen.close();
        }
    }
}
