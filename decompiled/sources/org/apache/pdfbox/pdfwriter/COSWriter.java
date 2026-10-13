package org.apache.pdfbox.pdfwriter;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.cos.COSUpdateInfo;
import org.apache.pdfbox.cos.ICOSVisitor;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFXRefStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.COSFilterInputStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.util.Charsets;
import org.apache.pdfbox.util.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfwriter/COSWriter.class */
public class COSWriter implements ICOSVisitor, Closeable {
    private static final Log LOG = LogFactory.getLog(COSWriter.class);
    public static final byte[] DICT_OPEN = "<<".getBytes(Charsets.US_ASCII);
    public static final byte[] DICT_CLOSE = ">>".getBytes(Charsets.US_ASCII);
    public static final byte[] SPACE = {32};
    public static final byte[] COMMENT = {37};
    public static final byte[] VERSION = "PDF-1.4".getBytes(Charsets.US_ASCII);
    public static final byte[] GARBAGE = {-10, -28, -4, -33};
    public static final byte[] EOF = "%%EOF".getBytes(Charsets.US_ASCII);
    public static final byte[] REFERENCE = "R".getBytes(Charsets.US_ASCII);
    public static final byte[] XREF = "xref".getBytes(Charsets.US_ASCII);
    public static final byte[] XREF_FREE = OperatorName.FILL_NON_ZERO.getBytes(Charsets.US_ASCII);
    public static final byte[] XREF_USED = OperatorName.ENDPATH.getBytes(Charsets.US_ASCII);
    public static final byte[] TRAILER = "trailer".getBytes(Charsets.US_ASCII);
    public static final byte[] STARTXREF = "startxref".getBytes(Charsets.US_ASCII);
    public static final byte[] OBJ = "obj".getBytes(Charsets.US_ASCII);
    public static final byte[] ENDOBJ = "endobj".getBytes(Charsets.US_ASCII);
    public static final byte[] ARRAY_OPEN = "[".getBytes(Charsets.US_ASCII);
    public static final byte[] ARRAY_CLOSE = "]".getBytes(Charsets.US_ASCII);
    public static final byte[] STREAM = "stream".getBytes(Charsets.US_ASCII);
    public static final byte[] ENDSTREAM = "endstream".getBytes(Charsets.US_ASCII);
    private final NumberFormat formatXrefOffset;
    private final NumberFormat formatXrefGeneration;
    private OutputStream output;
    private COSStandardOutputStream standardOutput;
    private long startxref;
    private long number;
    private final Map<COSBase, COSObjectKey> objectKeys;
    private final Map<COSObjectKey, COSBase> keyObject;
    private final List<COSWriterXRefEntry> xRefEntries;
    private final Set<COSBase> objectsToWriteSet;
    private final Deque<COSBase> objectsToWrite;
    private final Set<COSBase> writtenObjects;
    private final Set<COSBase> actualsAdded;
    private COSObjectKey currentObjectKey;
    private PDDocument pdDocument;
    private FDFDocument fdfDocument;
    private boolean willEncrypt;
    private boolean incrementalUpdate;
    private boolean reachedSignature;
    private long signatureOffset;
    private long signatureLength;
    private long byteRangeOffset;
    private long byteRangeLength;
    private RandomAccessRead incrementalInput;
    private OutputStream incrementalOutput;
    private SignatureInterface signatureInterface;
    private byte[] incrementPart;
    private COSArray byteRangeArray;

    public COSWriter(OutputStream outputStream) {
        this.formatXrefOffset = new DecimalFormat("0000000000", DecimalFormatSymbols.getInstance(Locale.US));
        this.formatXrefGeneration = new DecimalFormat("00000", DecimalFormatSymbols.getInstance(Locale.US));
        this.startxref = 0L;
        this.number = 0L;
        this.objectKeys = new Hashtable();
        this.keyObject = new HashMap();
        this.xRefEntries = new ArrayList();
        this.objectsToWriteSet = new HashSet();
        this.objectsToWrite = new LinkedList();
        this.writtenObjects = new HashSet();
        this.actualsAdded = new HashSet();
        this.currentObjectKey = null;
        this.pdDocument = null;
        this.fdfDocument = null;
        this.willEncrypt = false;
        this.incrementalUpdate = false;
        this.reachedSignature = false;
        setOutput(outputStream);
        setStandardOutput(new COSStandardOutputStream(this.output));
    }

    public COSWriter(OutputStream outputStream, RandomAccessRead inputData) throws IOException {
        this.formatXrefOffset = new DecimalFormat("0000000000", DecimalFormatSymbols.getInstance(Locale.US));
        this.formatXrefGeneration = new DecimalFormat("00000", DecimalFormatSymbols.getInstance(Locale.US));
        this.startxref = 0L;
        this.number = 0L;
        this.objectKeys = new Hashtable();
        this.keyObject = new HashMap();
        this.xRefEntries = new ArrayList();
        this.objectsToWriteSet = new HashSet();
        this.objectsToWrite = new LinkedList();
        this.writtenObjects = new HashSet();
        this.actualsAdded = new HashSet();
        this.currentObjectKey = null;
        this.pdDocument = null;
        this.fdfDocument = null;
        this.willEncrypt = false;
        this.incrementalUpdate = false;
        this.reachedSignature = false;
        setOutput(new ByteArrayOutputStream());
        setStandardOutput(new COSStandardOutputStream(this.output, inputData.length()));
        this.incrementalInput = inputData;
        this.incrementalOutput = outputStream;
        this.incrementalUpdate = true;
    }

    public COSWriter(OutputStream outputStream, RandomAccessRead inputData, Set<COSDictionary> objectsToWrite) throws IOException {
        this(outputStream, inputData);
        this.objectsToWrite.addAll(objectsToWrite);
    }

    private void prepareIncrement(PDDocument doc) {
        if (doc != null) {
            try {
                COSDocument cosDoc = doc.getDocument();
                Map<COSObjectKey, Long> xrefTable = cosDoc.getXrefTable();
                Set<COSObjectKey> keySet = xrefTable.keySet();
                long highestNumber = doc.getDocument().getHighestXRefObjectNumber();
                for (COSObjectKey cosObjectKey : keySet) {
                    COSBase object = cosDoc.getObjectFromPool(cosObjectKey).getObject();
                    if (object != null && cosObjectKey != null && !(object instanceof COSNumber)) {
                        this.objectKeys.put(object, cosObjectKey);
                        this.keyObject.put(cosObjectKey, object);
                    }
                    if (cosObjectKey != null) {
                        long num = cosObjectKey.getNumber();
                        if (num > highestNumber) {
                            highestNumber = num;
                        }
                    }
                }
                setNumber(highestNumber);
            } catch (IOException e) {
                LOG.error(e, e);
            }
        }
    }

    protected void addXRefEntry(COSWriterXRefEntry entry) {
        getXRefEntries().add(entry);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (getStandardOutput() != null) {
            getStandardOutput().close();
        }
        if (this.incrementalOutput != null) {
            this.incrementalOutput.close();
        }
    }

    protected long getNumber() {
        return this.number;
    }

    public Map<COSBase, COSObjectKey> getObjectKeys() {
        return this.objectKeys;
    }

    protected OutputStream getOutput() {
        return this.output;
    }

    protected COSStandardOutputStream getStandardOutput() {
        return this.standardOutput;
    }

    protected long getStartxref() {
        return this.startxref;
    }

    protected List<COSWriterXRefEntry> getXRefEntries() {
        return this.xRefEntries;
    }

    protected void setNumber(long newNumber) {
        this.number = newNumber;
    }

    private void setOutput(OutputStream newOutput) {
        this.output = newOutput;
    }

    private void setStandardOutput(COSStandardOutputStream newStandardOutput) {
        this.standardOutput = newStandardOutput;
    }

    protected void setStartxref(long newStartxref) {
        this.startxref = newStartxref;
    }

    protected void doWriteBody(COSDocument doc) throws IOException {
        COSDictionary trailer = doc.getTrailer();
        COSDictionary root = trailer.getCOSDictionary(COSName.ROOT);
        COSDictionary info = trailer.getCOSDictionary(COSName.INFO);
        COSDictionary encrypt = trailer.getCOSDictionary(COSName.ENCRYPT);
        if (root != null) {
            addObjectToWrite(root);
        }
        if (info != null) {
            addObjectToWrite(info);
        }
        doWriteObjects();
        this.willEncrypt = false;
        if (encrypt != null) {
            addObjectToWrite(encrypt);
        }
        doWriteObjects();
    }

    private void doWriteObjects() throws IOException {
        while (this.objectsToWrite.size() > 0) {
            COSBase nextObject = this.objectsToWrite.removeFirst();
            this.objectsToWriteSet.remove(nextObject);
            doWriteObject(nextObject);
        }
    }

    private void addObjectToWrite(COSBase object) {
        COSObjectKey cosObjectKey;
        COSBase actual = object;
        if (actual instanceof COSObject) {
            actual = ((COSObject) actual).getObject();
        }
        if (this.writtenObjects.contains(object) || this.objectsToWriteSet.contains(object) || this.actualsAdded.contains(actual)) {
            return;
        }
        if (actual != null && (cosObjectKey = this.objectKeys.get(actual)) != null) {
            COSBase cosBase = this.keyObject.get(cosObjectKey);
            if (!isNeedToBeUpdated(object) && !isNeedToBeUpdated(cosBase)) {
                return;
            }
        }
        this.objectsToWrite.add(object);
        this.objectsToWriteSet.add(object);
        if (actual != null) {
            this.actualsAdded.add(actual);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private boolean isNeedToBeUpdated(COSBase cOSBase) {
        if (cOSBase instanceof COSUpdateInfo) {
            return ((COSUpdateInfo) cOSBase).isNeedToBeUpdated();
        }
        return false;
    }

    public void doWriteObject(COSBase obj) throws IOException {
        this.writtenObjects.add(obj);
        this.currentObjectKey = getObjectKey(obj);
        addXRefEntry(new COSWriterXRefEntry(getStandardOutput().getPos(), obj, this.currentObjectKey));
        getStandardOutput().write(String.valueOf(this.currentObjectKey.getNumber()).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(String.valueOf(this.currentObjectKey.getGeneration()).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(OBJ);
        getStandardOutput().writeEOL();
        obj.accept(this);
        getStandardOutput().writeEOL();
        getStandardOutput().write(ENDOBJ);
        getStandardOutput().writeEOL();
    }

    protected void doWriteHeader(COSDocument doc) throws IOException {
        String headerString;
        if (this.fdfDocument != null) {
            headerString = "%FDF-" + doc.getVersion();
        } else {
            headerString = "%PDF-" + doc.getVersion();
        }
        getStandardOutput().write(headerString.getBytes(Charsets.ISO_8859_1));
        getStandardOutput().writeEOL();
        getStandardOutput().write(COMMENT);
        getStandardOutput().write(GARBAGE);
        getStandardOutput().writeEOL();
    }

    protected void doWriteTrailer(COSDocument doc) throws IOException {
        getStandardOutput().write(TRAILER);
        getStandardOutput().writeEOL();
        COSDictionary trailer = doc.getTrailer();
        Collections.sort(getXRefEntries());
        COSWriterXRefEntry lastEntry = getXRefEntries().get(getXRefEntries().size() - 1);
        trailer.setLong(COSName.SIZE, lastEntry.getKey().getNumber() + 1);
        if (!this.incrementalUpdate) {
            trailer.removeItem(COSName.PREV);
        }
        if (!doc.isXRefStream()) {
            trailer.removeItem(COSName.XREF_STM);
        }
        trailer.removeItem(COSName.DOC_CHECKSUM);
        COSArray idArray = trailer.getCOSArray(COSName.ID);
        if (idArray != null) {
            idArray.setDirect(true);
        }
        trailer.accept(this);
    }

    private void doWriteXRefInc(COSDocument doc, long hybridPrev) throws IOException {
        if (doc.isXRefStream() || hybridPrev != -1) {
            PDFXRefStream pdfxRefStream = new PDFXRefStream(doc);
            List<COSWriterXRefEntry> xRefEntries2 = getXRefEntries();
            for (COSWriterXRefEntry cosWriterXRefEntry : xRefEntries2) {
                pdfxRefStream.addEntry(cosWriterXRefEntry);
            }
            COSDictionary trailer = doc.getTrailer();
            if (this.incrementalUpdate) {
                trailer.setLong(COSName.PREV, doc.getStartXref());
            } else {
                trailer.removeItem(COSName.PREV);
            }
            pdfxRefStream.addTrailerInfo(trailer);
            pdfxRefStream.setSize(getNumber() + 2);
            setStartxref(getStandardOutput().getPos());
            COSStream stream2 = pdfxRefStream.getStream();
            doWriteObject(stream2);
        }
        if (!doc.isXRefStream() || hybridPrev != -1) {
            COSDictionary trailer2 = doc.getTrailer();
            trailer2.setLong(COSName.PREV, doc.getStartXref());
            if (hybridPrev != -1) {
                COSName xrefStm = COSName.XREF_STM;
                trailer2.removeItem(xrefStm);
                trailer2.setLong(xrefStm, getStartxref());
            }
            doWriteXRefTable();
            doWriteTrailer(doc);
        }
    }

    private void doWriteXRefTable() throws IOException {
        addXRefEntry(COSWriterXRefEntry.getNullEntry());
        Collections.sort(getXRefEntries());
        setStartxref(getStandardOutput().getPos());
        getStandardOutput().write(XREF);
        getStandardOutput().writeEOL();
        Long[] xRefRanges = getXRefRanges(getXRefEntries());
        int xRefLength = xRefRanges.length;
        int j = 0;
        if (xRefLength % 2 == 0) {
            for (int x = 0; x < xRefLength; x += 2) {
                long xRefRangeX1 = xRefRanges[x + 1].longValue();
                writeXrefRange(xRefRanges[x].longValue(), xRefRangeX1);
                for (int i = 0; i < xRefRangeX1; i++) {
                    int i2 = j;
                    j++;
                    writeXrefEntry(this.xRefEntries.get(i2));
                }
            }
        }
    }

    private void doWriteIncrement() throws IOException {
        IOUtils.copy(new RandomAccessInputStream(this.incrementalInput), this.incrementalOutput);
        this.incrementalOutput.write(((ByteArrayOutputStream) this.output).toByteArray());
    }

    private void doWriteSignature() throws IOException {
        long inLength = this.incrementalInput.length();
        long beforeLength = this.signatureOffset;
        long afterOffset = this.signatureOffset + this.signatureLength;
        long afterLength = (getStandardOutput().getPos() - (inLength + this.signatureLength)) - (this.signatureOffset - inLength);
        String byteRange = "0 " + beforeLength + " " + afterOffset + " " + afterLength + "]";
        this.byteRangeArray.set(0, (COSBase) COSInteger.ZERO);
        this.byteRangeArray.set(1, (COSBase) COSInteger.get(beforeLength));
        this.byteRangeArray.set(2, (COSBase) COSInteger.get(afterOffset));
        this.byteRangeArray.set(3, (COSBase) COSInteger.get(afterLength));
        if (byteRange.length() > this.byteRangeLength) {
            throw new IOException("Can't write new byteRange '" + byteRange + "' not enough space: byteRange.length(): " + byteRange.length() + ", byteRangeLength: " + this.byteRangeLength);
        }
        ByteArrayOutputStream byteOut = (ByteArrayOutputStream) this.output;
        byteOut.flush();
        this.incrementPart = byteOut.toByteArray();
        byte[] byteRangeBytes = byteRange.getBytes(Charsets.ISO_8859_1);
        for (int i = 0; i < this.byteRangeLength; i++) {
            if (i >= byteRangeBytes.length) {
                this.incrementPart[(int) ((this.byteRangeOffset + ((long) i)) - inLength)] = 32;
            } else {
                this.incrementPart[(int) ((this.byteRangeOffset + ((long) i)) - inLength)] = byteRangeBytes[i];
            }
        }
        if (this.signatureInterface != null) {
            InputStream dataToSign = getDataToSign();
            byte[] signatureBytes = this.signatureInterface.sign(dataToSign);
            writeExternalSignature(signatureBytes);
        }
    }

    public InputStream getDataToSign() throws IOException {
        if (this.incrementPart == null || this.incrementalInput == null) {
            throw new IllegalStateException("PDF not prepared for signing");
        }
        int incPartSigOffset = (int) (this.signatureOffset - this.incrementalInput.length());
        int afterSigOffset = incPartSigOffset + ((int) this.signatureLength);
        int[] range = {0, incPartSigOffset, afterSigOffset, this.incrementPart.length - afterSigOffset};
        return new SequenceInputStream(new RandomAccessInputStream(this.incrementalInput), new COSFilterInputStream(this.incrementPart, range));
    }

    public void writeExternalSignature(byte[] cmsSignature) throws IOException {
        if (this.incrementPart == null || this.incrementalInput == null) {
            throw new IllegalStateException("PDF not prepared for setting signature");
        }
        byte[] signatureBytes = Hex.getBytes(cmsSignature);
        if (signatureBytes.length > this.signatureLength - 2) {
            throw new IOException("Can't write signature, not enough space");
        }
        int incPartSigOffset = (int) (this.signatureOffset - this.incrementalInput.length());
        System.arraycopy(signatureBytes, 0, this.incrementPart, incPartSigOffset + 1, signatureBytes.length);
        IOUtils.copy(new RandomAccessInputStream(this.incrementalInput), this.incrementalOutput);
        this.incrementalOutput.write(this.incrementPart);
        this.incrementPart = null;
    }

    private void writeXrefRange(long x, long y) throws IOException {
        getStandardOutput().write(String.valueOf(x).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(String.valueOf(y).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().writeEOL();
    }

    private void writeXrefEntry(COSWriterXRefEntry entry) throws IOException {
        String offset = this.formatXrefOffset.format(entry.getOffset());
        String generation = this.formatXrefGeneration.format(entry.getKey().getGeneration());
        getStandardOutput().write(offset.getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(generation.getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(entry.isFree() ? XREF_FREE : XREF_USED);
        getStandardOutput().writeCRLF();
    }

    protected Long[] getXRefRanges(List<COSWriterXRefEntry> xRefEntriesList) {
        long last = -2;
        long count = 1;
        List<Long> list = new ArrayList<>();
        for (COSWriterXRefEntry object : xRefEntriesList) {
            long nr = object.getKey().getNumber();
            if (nr == last + 1) {
                count++;
                last = nr;
            } else if (last == -2) {
                last = nr;
            } else {
                list.add(Long.valueOf((last - count) + 1));
                list.add(Long.valueOf(count));
                last = nr;
                count = 1;
            }
        }
        if (xRefEntriesList.size() > 0) {
            list.add(Long.valueOf((last - count) + 1));
            list.add(Long.valueOf(count));
        }
        return (Long[]) list.toArray(new Long[list.size()]);
    }

    private COSObjectKey getObjectKey(COSBase obj) {
        COSBase actual = obj;
        if (actual instanceof COSObject) {
            actual = ((COSObject) obj).getObject();
        }
        COSObjectKey key = this.objectKeys.get(obj);
        if (key == null && actual != null) {
            key = this.objectKeys.get(actual);
        }
        if (key == null) {
            setNumber(getNumber() + 1);
            key = new COSObjectKey(getNumber(), 0);
            this.objectKeys.put(obj, key);
            if (actual != null) {
                this.objectKeys.put(actual, key);
            }
        }
        return key;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromArray(COSArray obj) throws IOException {
        int count = 0;
        getStandardOutput().write(ARRAY_OPEN);
        Iterator<COSBase> i = obj.iterator();
        while (i.hasNext()) {
            COSBase current = i.next();
            if (current instanceof COSDictionary) {
                if (current.isDirect()) {
                    visitFromDictionary((COSDictionary) current);
                } else {
                    addObjectToWrite(current);
                    writeReference(current);
                }
            } else if (current instanceof COSObject) {
                COSBase subValue = ((COSObject) current).getObject();
                if (this.willEncrypt || this.incrementalUpdate || (subValue instanceof COSDictionary) || subValue == null) {
                    addObjectToWrite(current);
                    writeReference(current);
                } else {
                    subValue.accept(this);
                }
            } else if (current == null) {
                COSNull.NULL.accept(this);
            } else {
                current.accept(this);
            }
            count++;
            if (i.hasNext()) {
                if (count % 10 == 0) {
                    getStandardOutput().writeEOL();
                } else {
                    getStandardOutput().write(SPACE);
                }
            }
        }
        getStandardOutput().write(ARRAY_CLOSE);
        getStandardOutput().writeEOL();
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromBoolean(COSBoolean obj) throws IOException {
        obj.writePDF(getStandardOutput());
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromDictionary(COSDictionary obj) throws IOException {
        if (!this.reachedSignature) {
            COSBase itemType = obj.getItem(COSName.TYPE);
            if (COSName.SIG.equals(itemType) || COSName.DOC_TIME_STAMP.equals(itemType)) {
                this.reachedSignature = true;
            }
        }
        getStandardOutput().write(DICT_OPEN);
        getStandardOutput().writeEOL();
        for (Map.Entry<COSName, COSBase> entry : obj.entrySet()) {
            COSBase value = entry.getValue();
            if (value != null) {
                entry.getKey().accept(this);
                getStandardOutput().write(SPACE);
                if (value instanceof COSDictionary) {
                    COSDictionary dict = (COSDictionary) value;
                    if (!this.incrementalUpdate) {
                        COSBase item = dict.getItem(COSName.XOBJECT);
                        if (item != null && !COSName.XOBJECT.equals(entry.getKey())) {
                            item.setDirect(true);
                        }
                        COSBase item2 = dict.getItem(COSName.RESOURCES);
                        if (item2 != null && !COSName.RESOURCES.equals(entry.getKey())) {
                            item2.setDirect(true);
                        }
                    }
                    if (dict.isDirect()) {
                        visitFromDictionary(dict);
                    } else {
                        addObjectToWrite(dict);
                        writeReference(dict);
                    }
                } else if (value instanceof COSObject) {
                    COSBase subValue = ((COSObject) value).getObject();
                    if (this.willEncrypt || this.incrementalUpdate || (subValue instanceof COSDictionary) || subValue == null) {
                        addObjectToWrite(value);
                        writeReference(value);
                    } else {
                        subValue.accept(this);
                    }
                } else if (this.reachedSignature && COSName.CONTENTS.equals(entry.getKey())) {
                    this.signatureOffset = getStandardOutput().getPos();
                    value.accept(this);
                    this.signatureLength = getStandardOutput().getPos() - this.signatureOffset;
                } else if (this.reachedSignature && COSName.BYTERANGE.equals(entry.getKey())) {
                    this.byteRangeArray = (COSArray) entry.getValue();
                    this.byteRangeOffset = getStandardOutput().getPos() + 1;
                    value.accept(this);
                    this.byteRangeLength = (getStandardOutput().getPos() - 1) - this.byteRangeOffset;
                    this.reachedSignature = false;
                } else {
                    value.accept(this);
                }
                getStandardOutput().writeEOL();
            }
        }
        getStandardOutput().write(DICT_CLOSE);
        getStandardOutput().writeEOL();
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromDocument(COSDocument doc) throws IOException {
        if (!this.incrementalUpdate) {
            doWriteHeader(doc);
        } else {
            getStandardOutput().writeCRLF();
        }
        doWriteBody(doc);
        COSDictionary trailer = doc.getTrailer();
        long hybridPrev = -1;
        if (trailer != null) {
            hybridPrev = trailer.getLong(COSName.XREF_STM);
        }
        if (this.incrementalUpdate || doc.isXRefStream()) {
            doWriteXRefInc(doc, hybridPrev);
        } else {
            doWriteXRefTable();
            doWriteTrailer(doc);
        }
        getStandardOutput().write(STARTXREF);
        getStandardOutput().writeEOL();
        getStandardOutput().write(String.valueOf(getStartxref()).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().writeEOL();
        getStandardOutput().write(EOF);
        getStandardOutput().writeEOL();
        if (!this.incrementalUpdate) {
            return null;
        }
        if (this.signatureOffset == 0 || this.byteRangeOffset == 0) {
            doWriteIncrement();
            return null;
        }
        doWriteSignature();
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromFloat(COSFloat obj) throws IOException {
        obj.writePDF(getStandardOutput());
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromInt(COSInteger obj) throws IOException {
        obj.writePDF(getStandardOutput());
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromName(COSName obj) throws IOException {
        obj.writePDF(getStandardOutput());
        return null;
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromNull(COSNull obj) throws IOException {
        obj.writePDF(getStandardOutput());
        return null;
    }

    public void writeReference(COSBase obj) throws IOException {
        COSObjectKey key = getObjectKey(obj);
        getStandardOutput().write(String.valueOf(key.getNumber()).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(String.valueOf(key.getGeneration()).getBytes(Charsets.ISO_8859_1));
        getStandardOutput().write(SPACE);
        getStandardOutput().write(REFERENCE);
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromStream(COSStream obj) throws IOException {
        if (this.willEncrypt) {
            this.pdDocument.getEncryption().getSecurityHandler().encryptStream(obj, this.currentObjectKey.getNumber(), this.currentObjectKey.getGeneration());
        }
        InputStream input = null;
        try {
            visitFromDictionary(obj);
            getStandardOutput().write(STREAM);
            getStandardOutput().writeCRLF();
            InputStream input2 = obj.createRawInputStream();
            IOUtils.copy(input2, getStandardOutput());
            getStandardOutput().writeCRLF();
            getStandardOutput().write(ENDSTREAM);
            getStandardOutput().writeEOL();
            if (input2 != null) {
            }
            return null;
        } finally {
            if (input != null) {
                input.close();
            }
        }
    }

    @Override // org.apache.pdfbox.cos.ICOSVisitor
    public Object visitFromString(COSString obj) throws IOException {
        if (this.willEncrypt) {
            this.pdDocument.getEncryption().getSecurityHandler().encryptString(obj, this.currentObjectKey.getNumber(), this.currentObjectKey.getGeneration());
        }
        writeString(obj, getStandardOutput());
        return null;
    }

    public void write(COSDocument doc) throws IOException {
        PDDocument pdDoc = new PDDocument(doc);
        write(pdDoc);
    }

    public void write(PDDocument doc) throws IOException {
        write(doc, null);
    }

    public void write(PDDocument doc, SignatureInterface signInterface) throws IOException {
        Long idTime = Long.valueOf(doc.getDocumentId() == null ? System.currentTimeMillis() : doc.getDocumentId().longValue());
        this.pdDocument = doc;
        this.signatureInterface = signInterface;
        if (this.incrementalUpdate) {
            prepareIncrement(doc);
        }
        if (doc.isAllSecurityToBeRemoved()) {
            this.willEncrypt = false;
            doc.getDocument().getTrailer().removeItem(COSName.ENCRYPT);
        } else if (this.pdDocument.getEncryption() != null) {
            if (!this.incrementalUpdate) {
                SecurityHandler securityHandler = this.pdDocument.getEncryption().getSecurityHandler();
                if (!securityHandler.hasProtectionPolicy()) {
                    throw new IllegalStateException("PDF contains an encryption dictionary, please remove it with setAllSecurityToBeRemoved() or set a protection policy with protect()");
                }
                securityHandler.prepareDocumentForEncryption(this.pdDocument);
            }
            this.willEncrypt = true;
        } else {
            this.willEncrypt = false;
        }
        COSDocument cosDoc = this.pdDocument.getDocument();
        COSDictionary trailer = cosDoc.getTrailer();
        COSArray idArray = null;
        boolean missingID = true;
        COSBase base = trailer.getDictionaryObject(COSName.ID);
        if (base instanceof COSArray) {
            idArray = (COSArray) base;
            if (idArray.size() == 2) {
                missingID = false;
            }
        }
        if (idArray != null && idArray.size() == 2) {
            missingID = false;
        }
        if (missingID || this.incrementalUpdate) {
            try {
                MessageDigest md5 = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
                md5.update(Long.toString(idTime.longValue()).getBytes(Charsets.ISO_8859_1));
                COSDictionary info = trailer.getCOSDictionary(COSName.INFO);
                if (info != null) {
                    for (COSBase cosBase : info.getValues()) {
                        md5.update(cosBase.toString().getBytes(Charsets.ISO_8859_1));
                    }
                }
                COSString firstID = missingID ? new COSString(md5.digest()) : (COSString) idArray.get(0);
                COSString secondID = missingID ? firstID : new COSString(md5.digest());
                COSArray idArray2 = new COSArray();
                idArray2.add((COSBase) firstID);
                idArray2.add((COSBase) secondID);
                trailer.setItem(COSName.ID, (COSBase) idArray2);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        cosDoc.accept(this);
    }

    public void write(FDFDocument doc) throws IOException {
        this.fdfDocument = doc;
        this.willEncrypt = false;
        COSDocument cosDoc = this.fdfDocument.getDocument();
        cosDoc.accept(this);
    }

    public static void writeString(COSString string, OutputStream output) throws IOException {
        writeString(string.getBytes(), string.getForceHexForm(), output);
    }

    public static void writeString(byte[] bytes, OutputStream output) throws IOException {
        writeString(bytes, false, output);
    }

    private static void writeString(byte[] bytes, boolean forceHex, OutputStream output) throws IOException {
        boolean isASCII = true;
        if (!forceHex) {
            for (byte b : bytes) {
                if (b < 0) {
                    isASCII = false;
                    break;
                } else {
                    if (b == 13 || b == 10) {
                        isASCII = false;
                        break;
                    }
                }
            }
        }
        if (isASCII && !forceHex) {
            output.write(40);
            for (byte b2 : bytes) {
                switch (b2) {
                    case 40:
                    case 41:
                    case 92:
                        output.write(92);
                        output.write(b2);
                        break;
                    default:
                        output.write(b2);
                        break;
                }
            }
            output.write(41);
            return;
        }
        output.write(60);
        Hex.writeHexBytes(bytes, output);
        output.write(62);
    }
}
