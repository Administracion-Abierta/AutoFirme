package org.apache.pdfbox.pdfparser;

import com.aowagie.text.pdf.ColumnText;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNull;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSObjectKey;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.DecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.PublicKeyDecryptionMaterial;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;
import org.apache.pdfbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/COSParser.class */
public class COSParser extends BaseParser {
    private static final String PDF_HEADER = "%PDF-";
    private static final String FDF_HEADER = "%FDF-";
    private static final String PDF_DEFAULT_VERSION = "1.4";
    private static final String FDF_DEFAULT_VERSION = "1.0";
    private static final long MINIMUM_SEARCH_OFFSET = 6;
    private static final int X = 120;
    private static final int STRMBUFLEN = 2048;
    private final byte[] strmBuf;
    protected final RandomAccessRead source;
    private AccessPermission accessPermission;
    private InputStream keyStoreInputStream;
    private String password;
    private String keyAlias;
    public static final String SYSPROP_PARSEMINIMAL = "org.apache.pdfbox.pdfparser.nonSequentialPDFParser.parseMinimal";
    public static final String SYSPROP_EOFLOOKUPRANGE = "org.apache.pdfbox.pdfparser.nonSequentialPDFParser.eofLookupRange";
    private static final int DEFAULT_TRAIL_BYTECOUNT = 2048;
    private long trailerOffset;
    protected long fileLen;
    private boolean isLenient;
    protected boolean initialParseDone;
    private boolean trailerWasRebuild;
    private Map<COSObjectKey, Long> bfSearchCOSObjectKeyOffsets;
    private Long lastEOFMarker;
    private List<Long> bfSearchXRefTablesOffsets;
    private List<Long> bfSearchXRefStreamsOffsets;
    private PDEncryption encryption;
    protected SecurityHandler securityHandler;
    private int readTrailBytes;
    protected XrefTrailerResolver xrefTrailerResolver;
    public static final String TMP_FILE_PREFIX = "tmpPDF";
    private static final int STREAMCOPYBUFLEN = 8192;
    private final byte[] streamCopyBuf;
    private static final char[] XREF_TABLE = {'x', 'r', 'e', 'f'};
    private static final char[] XREF_STREAM = {'/', 'X', 'R', 'e', 'f'};
    private static final char[] STARTXREF = {'s', 't', 'a', 'r', 't', 'x', 'r', 'e', 'f'};
    private static final byte[] ENDSTREAM = {101, 110, 100, 115, 116, 114, 101, 97, 109};
    private static final byte[] ENDOBJ = {101, 110, 100, 111, 98, 106};
    protected static final char[] EOF_MARKER = {'%', '%', 'E', 'O', 'F'};
    protected static final char[] OBJ_MARKER = {'o', 'b', 'j'};
    private static final char[] TRAILER_MARKER = {'t', 'r', 'a', 'i', 'l', 'e', 'r'};
    private static final char[] OBJ_STREAM = {'/', 'O', 'b', 'j', 'S', 't', 'm'};
    private static final Log LOG = LogFactory.getLog(COSParser.class);

    public COSParser(RandomAccessRead source) {
        super(new RandomAccessSource(source));
        this.strmBuf = new byte[2048];
        this.keyStoreInputStream = null;
        this.password = "";
        this.keyAlias = null;
        this.isLenient = true;
        this.initialParseDone = false;
        this.trailerWasRebuild = false;
        this.bfSearchCOSObjectKeyOffsets = null;
        this.lastEOFMarker = null;
        this.bfSearchXRefTablesOffsets = null;
        this.bfSearchXRefStreamsOffsets = null;
        this.encryption = null;
        this.securityHandler = null;
        this.readTrailBytes = 2048;
        this.xrefTrailerResolver = new XrefTrailerResolver();
        this.streamCopyBuf = new byte[8192];
        this.source = source;
    }

    public COSParser(RandomAccessRead source, String password, InputStream keyStore, String keyAlias) {
        super(new RandomAccessSource(source));
        this.strmBuf = new byte[2048];
        this.keyStoreInputStream = null;
        this.password = "";
        this.keyAlias = null;
        this.isLenient = true;
        this.initialParseDone = false;
        this.trailerWasRebuild = false;
        this.bfSearchCOSObjectKeyOffsets = null;
        this.lastEOFMarker = null;
        this.bfSearchXRefTablesOffsets = null;
        this.bfSearchXRefStreamsOffsets = null;
        this.encryption = null;
        this.securityHandler = null;
        this.readTrailBytes = 2048;
        this.xrefTrailerResolver = new XrefTrailerResolver();
        this.streamCopyBuf = new byte[8192];
        this.source = source;
        this.password = password;
        this.keyAlias = keyAlias;
        this.keyStoreInputStream = keyStore;
    }

    public void setEOFLookupRange(int byteCount) {
        if (byteCount > 15) {
            this.readTrailBytes = byteCount;
        }
    }

    protected COSDictionary retrieveTrailer() throws IOException {
        COSDictionary trailer = null;
        boolean rebuildTrailer = false;
        try {
            long startXRefOffset = getStartxrefOffset();
            if (startXRefOffset > -1) {
                trailer = parseXref(startXRefOffset);
            } else {
                rebuildTrailer = isLenient();
            }
        } catch (IOException exception) {
            if (isLenient()) {
                rebuildTrailer = true;
            } else {
                throw exception;
            }
        }
        if (trailer != null && trailer.getItem(COSName.ROOT) == null) {
            rebuildTrailer = isLenient();
        }
        if (rebuildTrailer) {
            trailer = rebuildTrailer();
        } else {
            prepareDecryption();
            if (this.bfSearchCOSObjectKeyOffsets != null && !this.bfSearchCOSObjectKeyOffsets.isEmpty()) {
                bfSearchForObjStreams();
            }
        }
        return trailer;
    }

    protected COSDictionary parseXref(long startXRefOffset) throws IOException {
        COSDictionary trailer;
        this.source.seek(startXRefOffset);
        long startXrefOffset = Math.max(0L, parseStartXref());
        long fixedOffset = checkXRefOffset(startXrefOffset);
        if (fixedOffset > -1) {
            startXrefOffset = fixedOffset;
        }
        this.document.setStartXref(startXrefOffset);
        long prev = startXrefOffset;
        Set<Long> prevSet = new HashSet<>();
        while (prev > 0) {
            this.source.seek(prev);
            skipSpaces();
            prevSet.add(Long.valueOf(this.source.getPosition()));
            if (this.source.peek() == 120) {
                if (!parseXrefTable(prev) || !parseTrailer()) {
                    throw new IOException("Expected trailer object at offset " + this.source.getPosition());
                }
                trailer = this.xrefTrailerResolver.getCurrentTrailer();
                if (trailer.containsKey(COSName.XREF_STM)) {
                    int streamOffset = trailer.getInt(COSName.XREF_STM);
                    long fixedOffset2 = checkXRefOffset(streamOffset);
                    if (fixedOffset2 > -1 && fixedOffset2 != streamOffset) {
                        LOG.warn("/XRefStm offset " + streamOffset + " is incorrect, corrected to " + fixedOffset2);
                        streamOffset = (int) fixedOffset2;
                        trailer.setInt(COSName.XREF_STM, streamOffset);
                    }
                    if (streamOffset > 0) {
                        this.source.seek(streamOffset);
                        skipSpaces();
                        try {
                            parseXrefObjStream(prev, false);
                        } catch (IOException ex) {
                            if (this.isLenient) {
                                LOG.error("Failed to parse /XRefStm at offset " + streamOffset, ex);
                            } else {
                                throw ex;
                            }
                        }
                    } else if (this.isLenient) {
                        LOG.error("Skipped XRef stream due to a corrupt offset:" + streamOffset);
                    } else {
                        throw new IOException("Skipped XRef stream due to a corrupt offset:" + streamOffset);
                    }
                }
                prev = trailer.getLong(COSName.PREV);
            } else {
                prev = parseXrefObjStream(prev, true);
                trailer = this.xrefTrailerResolver.getCurrentTrailer();
            }
            if (prev > 0) {
                long fixedOffset3 = checkXRefOffset(prev);
                if (fixedOffset3 > -1 && fixedOffset3 != prev) {
                    prev = fixedOffset3;
                    trailer.setLong(COSName.PREV, prev);
                }
            }
            if (prevSet.contains(Long.valueOf(prev))) {
                throw new IOException("/Prev loop at offset " + prev);
            }
        }
        this.xrefTrailerResolver.setStartxref(startXrefOffset);
        COSDictionary trailer2 = this.xrefTrailerResolver.getTrailer();
        this.document.setTrailer(trailer2);
        this.document.setIsXRefStream(XrefTrailerResolver.XRefType.STREAM == this.xrefTrailerResolver.getXrefType());
        checkXrefOffsets();
        this.document.addXRefTable(this.xrefTrailerResolver.getXrefTable());
        return trailer2;
    }

    private long parseXrefObjStream(long objByteOffset, boolean isStandalone) throws IOException {
        long objectNumber = readObjectNumber();
        long currentHighestXRefObjectNumber = this.document.getHighestXRefObjectNumber();
        this.document.setHighestXRefObjectNumber(Math.max(currentHighestXRefObjectNumber, objectNumber));
        readGenerationNumber();
        readExpectedString(OBJ_MARKER, true);
        COSDictionary dict = parseCOSDictionary();
        COSStream xrefStream = parseCOSStream(dict);
        parseXrefStream(xrefStream, objByteOffset, isStandalone);
        xrefStream.close();
        return dict.getLong(COSName.PREV);
    }

    protected final long getStartxrefOffset() throws IOException {
        try {
            int trailByteCount = this.fileLen < ((long) this.readTrailBytes) ? (int) this.fileLen : this.readTrailBytes;
            byte[] buf = new byte[trailByteCount];
            long skipBytes = this.fileLen - ((long) trailByteCount);
            this.source.seek(skipBytes);
            int off = 0;
            while (off < trailByteCount) {
                int readBytes = this.source.read(buf, off, trailByteCount - off);
                if (readBytes < 1) {
                    throw new IOException("No more bytes to read for trailing buffer, but expected: " + (trailByteCount - off));
                }
                off += readBytes;
            }
            this.source.seek(0L);
            int bufOff = lastIndexOf(EOF_MARKER, buf, buf.length);
            if (bufOff < 0) {
                if (this.isLenient) {
                    bufOff = buf.length;
                    LOG.debug("Missing end of file marker '" + new String(EOF_MARKER) + OperatorName.SHOW_TEXT_LINE);
                } else {
                    throw new IOException("Missing end of file marker '" + new String(EOF_MARKER) + OperatorName.SHOW_TEXT_LINE);
                }
            }
            int bufOff2 = lastIndexOf(STARTXREF, buf, bufOff);
            if (bufOff2 < 0) {
                throw new IOException("Missing 'startxref' marker.");
            }
            return skipBytes + ((long) bufOff2);
        } catch (Throwable th) {
            this.source.seek(0L);
            throw th;
        }
    }

    protected int lastIndexOf(char[] pattern, byte[] buf, int endOff) {
        int lastPatternChOff = pattern.length - 1;
        int bufOff = endOff;
        int patOff = lastPatternChOff;
        char lookupCh = pattern[patOff];
        while (true) {
            bufOff--;
            if (bufOff >= 0) {
                if (buf[bufOff] == lookupCh) {
                    patOff--;
                    if (patOff < 0) {
                        return bufOff;
                    }
                    lookupCh = pattern[patOff];
                } else if (patOff < lastPatternChOff) {
                    patOff = lastPatternChOff;
                    lookupCh = pattern[patOff];
                }
            } else {
                return -1;
            }
        }
    }

    public boolean isLenient() {
        return this.isLenient;
    }

    public void setLenient(boolean lenient) {
        if (this.initialParseDone) {
            throw new IllegalArgumentException("Cannot change leniency after parsing");
        }
        this.isLenient = lenient;
    }

    private long getObjectId(COSObject obj) {
        return (obj.getObjectNumber() << 32) | ((long) obj.getGenerationNumber());
    }

    private void addNewToList(Queue<COSBase> toBeParsedList, Collection<COSBase> newObjects, Set<Long> addedObjects) {
        for (COSBase newObject : newObjects) {
            addNewToList(toBeParsedList, newObject, addedObjects);
        }
    }

    private void addNewToList(Queue<COSBase> toBeParsedList, COSBase newObject, Set<Long> addedObjects) {
        if (newObject instanceof COSObject) {
            long objId = getObjectId((COSObject) newObject);
            if (!addedObjects.add(Long.valueOf(objId))) {
                return;
            }
            toBeParsedList.add(newObject);
            return;
        }
        if ((newObject instanceof COSDictionary) || (newObject instanceof COSArray)) {
            toBeParsedList.add(newObject);
        }
    }

    protected void parseDictObjects(COSDictionary dict, COSName... excludeObjects) throws IOException {
        Queue<COSBase> toBeParsedList = new LinkedList<>();
        TreeMap<Long, List<COSObject>> objToBeParsed = new TreeMap<>();
        Set<Long> parsedObjects = new HashSet<>();
        Set<Long> addedObjects = new HashSet<>();
        addExcludedToList(excludeObjects, dict, parsedObjects);
        addNewToList(toBeParsedList, dict.getValues(), addedObjects);
        while (true) {
            if (!toBeParsedList.isEmpty() || !objToBeParsed.isEmpty()) {
                while (true) {
                    COSBase baseObj = toBeParsedList.poll();
                    if (baseObj != null) {
                        if (baseObj instanceof COSDictionary) {
                            addNewToList(toBeParsedList, ((COSDictionary) baseObj).getValues(), addedObjects);
                        } else if (baseObj instanceof COSArray) {
                            for (COSBase cosBase : (COSArray) baseObj) {
                                addNewToList(toBeParsedList, cosBase, addedObjects);
                            }
                        } else if (baseObj instanceof COSObject) {
                            COSObject obj = (COSObject) baseObj;
                            long objId = getObjectId(obj);
                            COSObjectKey objKey = new COSObjectKey(obj.getObjectNumber(), obj.getGenerationNumber());
                            if (parsedObjects.contains(Long.valueOf(objId))) {
                                continue;
                            } else {
                                Long fileOffset = this.document.getXrefTable().get(objKey);
                                if (fileOffset == null && this.isLenient && this.bfSearchCOSObjectKeyOffsets != null) {
                                    fileOffset = this.bfSearchCOSObjectKeyOffsets.get(objKey);
                                    if (fileOffset != null) {
                                        LOG.debug("Set missing " + fileOffset + " for object " + objKey);
                                        this.document.getXrefTable().put(objKey, fileOffset);
                                    }
                                }
                                if (fileOffset == null || fileOffset.longValue() == 0) {
                                    COSObject pdfObject = this.document.getObjectFromPool(objKey);
                                    pdfObject.setObject(COSNull.NULL);
                                } else if (fileOffset.longValue() > 0) {
                                    objToBeParsed.put(fileOffset, Collections.singletonList(obj));
                                } else {
                                    COSObjectKey key = new COSObjectKey((int) (-fileOffset.longValue()), 0);
                                    Long fileOffset2 = this.document.getXrefTable().get(key);
                                    if (fileOffset2 == null || fileOffset2.longValue() <= 0) {
                                        if (this.isLenient && this.bfSearchCOSObjectKeyOffsets != null) {
                                            fileOffset2 = this.bfSearchCOSObjectKeyOffsets.get(key);
                                            if (fileOffset2 != null) {
                                                LOG.debug("Set missing " + fileOffset2 + " for object " + key);
                                                this.document.getXrefTable().put(key, fileOffset2);
                                            } else {
                                                LOG.warn("Invalid object stream xref object reference for key '" + objKey + "': " + fileOffset2);
                                            }
                                        } else {
                                            String msg = "Invalid object stream xref object reference for key '" + objKey + "': " + fileOffset2;
                                            if (this.isLenient && fileOffset2 == null) {
                                                LOG.warn(msg);
                                            } else {
                                                throw new IOException(msg);
                                            }
                                        }
                                    }
                                    List<COSObject> stmObjects = objToBeParsed.get(fileOffset2);
                                    if (stmObjects == null) {
                                        stmObjects = new ArrayList<>();
                                        objToBeParsed.put(fileOffset2, stmObjects);
                                    } else if (!(stmObjects instanceof ArrayList)) {
                                        throw new IOException(obj + " cannot be assigned to offset " + fileOffset2 + ", this belongs to " + stmObjects.get(0));
                                    }
                                    stmObjects.add(obj);
                                }
                            }
                        } else {
                            continue;
                        }
                    }
                }
                if (!objToBeParsed.isEmpty()) {
                    for (COSObject obj2 : objToBeParsed.remove(objToBeParsed.firstKey())) {
                        COSBase parsedObj = parseObjectDynamically(obj2, false);
                        if (parsedObj != null) {
                            obj2.setObject(parsedObj);
                            addNewToList(toBeParsedList, parsedObj, addedObjects);
                            parsedObjects.add(Long.valueOf(getObjectId(obj2)));
                        }
                    }
                } else {
                    return;
                }
            } else {
                return;
            }
        }
    }

    private void addExcludedToList(COSName[] excludeObjects, COSDictionary dict, Set<Long> parsedObjects) {
        if (excludeObjects != null) {
            for (COSName objName : excludeObjects) {
                COSBase baseObj = dict.getItem(objName);
                if (baseObj instanceof COSObject) {
                    parsedObjects.add(Long.valueOf(getObjectId((COSObject) baseObj)));
                }
            }
        }
    }

    protected final COSBase parseObjectDynamically(COSObject obj, boolean requireExistingNotCompressedObj) throws IOException {
        return parseObjectDynamically(obj.getObjectNumber(), obj.getGenerationNumber(), requireExistingNotCompressedObj);
    }

    protected COSBase parseObjectDynamically(long objNr, int objGenNr, boolean requireExistingNotCompressedObj) throws IOException {
        COSObjectKey objKey = new COSObjectKey(objNr, objGenNr);
        COSObject pdfObject = this.document.getObjectFromPool(objKey);
        if (pdfObject.getObject() == null) {
            Long offsetOrObjstmObNr = this.document.getXrefTable().get(objKey);
            if (offsetOrObjstmObNr == null && this.isLenient && this.bfSearchCOSObjectKeyOffsets != null) {
                offsetOrObjstmObNr = this.bfSearchCOSObjectKeyOffsets.get(objKey);
                if (offsetOrObjstmObNr != null) {
                    LOG.debug("Set missing offset " + offsetOrObjstmObNr + " for object " + objKey);
                    this.document.getXrefTable().put(objKey, offsetOrObjstmObNr);
                }
            }
            if (requireExistingNotCompressedObj && (offsetOrObjstmObNr == null || offsetOrObjstmObNr.longValue() <= 0)) {
                throw new IOException("Object must be defined and must not be compressed object: " + objKey.getNumber() + ":" + objKey.getGeneration());
            }
            if (offsetOrObjstmObNr == null && this.isLenient && this.bfSearchCOSObjectKeyOffsets == null) {
                bfSearchForObjects();
                if (this.bfSearchCOSObjectKeyOffsets != null && !this.bfSearchCOSObjectKeyOffsets.isEmpty()) {
                    LOG.debug("Add all new read objects from brute force search to the xref table");
                    Map<COSObjectKey, Long> xrefOffset = this.document.getXrefTable();
                    Set<Map.Entry<COSObjectKey, Long>> entries = this.bfSearchCOSObjectKeyOffsets.entrySet();
                    for (Map.Entry<COSObjectKey, Long> entry : entries) {
                        COSObjectKey key = entry.getKey();
                        if (!xrefOffset.containsKey(key)) {
                            xrefOffset.put(key, entry.getValue());
                        }
                    }
                    offsetOrObjstmObNr = xrefOffset.get(objKey);
                }
            }
            if (offsetOrObjstmObNr == null) {
                pdfObject.setObject(COSNull.NULL);
            } else if (offsetOrObjstmObNr.longValue() > 0) {
                parseFileObject(offsetOrObjstmObNr, objKey, pdfObject);
            } else {
                parseObjectStream((int) (-offsetOrObjstmObNr.longValue()));
            }
        }
        return pdfObject.getObject();
    }

    private void parseFileObject(Long offsetOrObjstmObNr, COSObjectKey objKey, COSObject pdfObject) throws IOException {
        this.source.seek(offsetOrObjstmObNr.longValue());
        long readObjNr = readObjectNumber();
        int readObjGen = readGenerationNumber();
        readExpectedString(OBJ_MARKER, true);
        if (readObjNr != objKey.getNumber() || readObjGen != objKey.getGeneration()) {
            throw new IOException("XREF for " + objKey.getNumber() + ":" + objKey.getGeneration() + " points to wrong object: " + readObjNr + ":" + readObjGen + " at offset " + offsetOrObjstmObNr);
        }
        skipSpaces();
        COSBase pb = parseDirObject();
        String endObjectKey = readString();
        if (endObjectKey.equals("stream")) {
            this.source.rewind(endObjectKey.getBytes(Charsets.ISO_8859_1).length);
            if (pb instanceof COSDictionary) {
                COSStream stream = parseCOSStream((COSDictionary) pb);
                if (this.securityHandler != null) {
                    this.securityHandler.decryptStream(stream, objKey.getNumber(), objKey.getGeneration());
                }
                pb = stream;
                skipSpaces();
                endObjectKey = readLine();
                if (!endObjectKey.startsWith("endobj") && endObjectKey.startsWith("endstream")) {
                    endObjectKey = endObjectKey.substring(9).trim();
                    if (endObjectKey.length() == 0) {
                        endObjectKey = readLine();
                    }
                }
            } else {
                throw new IOException("Stream not preceded by dictionary (offset: " + offsetOrObjstmObNr + ").");
            }
        } else if (this.securityHandler != null) {
            this.securityHandler.decrypt(pb, objKey.getNumber(), objKey.getGeneration());
        }
        pdfObject.setObject(pb);
        if (!endObjectKey.startsWith("endobj")) {
            if (this.isLenient) {
                LOG.warn("Object (" + readObjNr + ":" + readObjGen + ") at offset " + offsetOrObjstmObNr + " does not end with 'endobj' but with '" + endObjectKey + OperatorName.SHOW_TEXT_LINE);
                return;
            }
            throw new IOException("Object (" + readObjNr + ":" + readObjGen + ") at offset " + offsetOrObjstmObNr + " does not end with 'endobj' but with '" + endObjectKey + OperatorName.SHOW_TEXT_LINE);
        }
    }

    private void parseObjectStream(int objstmObjNr) throws IOException {
        COSBase objstmBaseObj = parseObjectDynamically(objstmObjNr, 0, true);
        if (objstmBaseObj instanceof COSStream) {
            try {
                PDFObjectStreamParser parser = new PDFObjectStreamParser((COSStream) objstmBaseObj, this.document);
                try {
                    parser.parse();
                    for (COSObject next : parser.getObjects()) {
                        COSObjectKey stmObjKey = new COSObjectKey(next);
                        Long offset = this.xrefTrailerResolver.getXrefTable().get(stmObjKey);
                        if (offset != null && offset.longValue() == (-objstmObjNr)) {
                            COSObject stmObj = this.document.getObjectFromPool(stmObjKey);
                            stmObj.setObject(next.getObject());
                        }
                    }
                } catch (IOException exception) {
                    if (this.isLenient) {
                        LOG.debug("Stop reading object stream " + objstmObjNr + " due to an exception", exception);
                        return;
                    }
                    throw exception;
                }
            } catch (IOException ex) {
                if (this.isLenient) {
                    LOG.error("object stream " + objstmObjNr + " could not be parsed due to an exception", ex);
                    return;
                }
                throw ex;
            }
        }
    }

    private COSNumber getLength(COSBase lengthBaseObj, COSName streamType) throws IOException {
        COSNumber retVal;
        if (lengthBaseObj == null) {
            return null;
        }
        if (lengthBaseObj instanceof COSNumber) {
            retVal = (COSNumber) lengthBaseObj;
        } else if (lengthBaseObj instanceof COSObject) {
            COSObject lengthObj = (COSObject) lengthBaseObj;
            COSBase length = lengthObj.getObject();
            if (length == null) {
                long curFileOffset = this.source.getPosition();
                boolean isObjectStream = COSName.OBJ_STM.equals(streamType);
                parseObjectDynamically(lengthObj, isObjectStream);
                this.source.seek(curFileOffset);
                length = lengthObj.getObject();
            }
            if (length == null) {
                throw new IOException("Length object content was not read.");
            }
            if (COSNull.NULL == length) {
                LOG.warn("Length object (" + lengthObj.getObjectNumber() + " " + lengthObj.getGenerationNumber() + ") not found");
                return null;
            }
            if (!(length instanceof COSNumber)) {
                throw new IOException("Wrong type of referenced length object " + lengthObj + ": " + length.getClass().getSimpleName());
            }
            retVal = (COSNumber) length;
        } else {
            throw new IOException("Wrong type of length object: " + lengthBaseObj.getClass().getSimpleName());
        }
        return retVal;
    }

    protected COSStream parseCOSStream(COSDictionary dic) throws IOException {
        COSStream stream = this.document.createCOSStream(dic);
        readString();
        skipWhiteSpaces();
        COSNumber streamLengthObj = getLength(dic.getItem(COSName.LENGTH), dic.getCOSName(COSName.TYPE));
        if (streamLengthObj == null) {
            if (this.isLenient) {
                LOG.warn("The stream doesn't provide any stream length, using fallback readUntilEnd, at offset " + this.source.getPosition());
            } else {
                throw new IOException("Missing length for stream.");
            }
        }
        if (streamLengthObj != null && validateStreamLength(streamLengthObj.longValue())) {
            OutputStream out = stream.createRawOutputStream();
            try {
                readValidStream(out, streamLengthObj);
                out.close();
                stream.setItem(COSName.LENGTH, (COSBase) streamLengthObj);
            } catch (Throwable th) {
                out.close();
                stream.setItem(COSName.LENGTH, (COSBase) streamLengthObj);
                throw th;
            }
        } else {
            OutputStream out2 = stream.createRawOutputStream();
            try {
                readUntilEndStream(new EndstreamOutputStream(out2));
                out2.close();
                if (streamLengthObj != null) {
                    stream.setItem(COSName.LENGTH, (COSBase) streamLengthObj);
                }
            } catch (Throwable th2) {
                out2.close();
                if (streamLengthObj != null) {
                    stream.setItem(COSName.LENGTH, (COSBase) streamLengthObj);
                }
                throw th2;
            }
        }
        String endStream = readString();
        if (endStream.equals("endobj") && this.isLenient) {
            LOG.warn("stream ends with 'endobj' instead of 'endstream' at offset " + this.source.getPosition());
            this.source.rewind(ENDOBJ.length);
        } else if (endStream.length() > 9 && this.isLenient && endStream.startsWith("endstream")) {
            LOG.warn("stream ends with '" + endStream + "' instead of 'endstream' at offset " + this.source.getPosition());
            this.source.rewind(endStream.substring(9).getBytes(Charsets.ISO_8859_1).length);
        } else if (!endStream.equals("endstream")) {
            throw new IOException("Error reading stream, expected='endstream' actual='" + endStream + "' at offset " + this.source.getPosition());
        }
        return stream;
    }

    private void readUntilEndStream(OutputStream out) throws IOException {
        int i;
        byte ch;
        int charMatchCount = 0;
        byte[] keyw = ENDSTREAM;
        while (true) {
            int bufSize = this.source.read(this.strmBuf, charMatchCount, 2048 - charMatchCount);
            if (bufSize <= 0) {
                break;
            }
            int bufSize2 = bufSize + charMatchCount;
            int bIdx = charMatchCount;
            int maxQuicktestIdx = bufSize2 - 5;
            while (bIdx < bufSize2) {
                int quickTestIdx = bIdx + 5;
                if (charMatchCount == 0 && quickTestIdx < maxQuicktestIdx && ((ch = this.strmBuf[quickTestIdx]) > 116 || ch < 97)) {
                    bIdx = quickTestIdx;
                } else {
                    byte ch2 = this.strmBuf[bIdx];
                    if (ch2 == keyw[charMatchCount]) {
                        charMatchCount++;
                        if (charMatchCount == keyw.length) {
                            bIdx++;
                            break;
                        }
                    } else if (charMatchCount == 3 && ch2 == ENDOBJ[charMatchCount]) {
                        keyw = ENDOBJ;
                        charMatchCount++;
                    } else {
                        if (ch2 == 101) {
                            i = 1;
                        } else {
                            i = (ch2 == 110 && charMatchCount == 7) ? 2 : 0;
                        }
                        charMatchCount = i;
                        keyw = ENDSTREAM;
                    }
                }
                bIdx++;
            }
            int contentBytes = Math.max(0, bIdx - charMatchCount);
            if (contentBytes > 0) {
                out.write(this.strmBuf, 0, contentBytes);
            }
            if (charMatchCount == keyw.length) {
                this.source.rewind(bufSize2 - contentBytes);
                break;
            }
            System.arraycopy(keyw, 0, this.strmBuf, 0, charMatchCount);
        }
        out.flush();
    }

    private void readValidStream(OutputStream out, COSNumber streamLengthObj) throws IOException {
        long jLongValue = streamLengthObj.longValue();
        while (true) {
            long remainBytes = jLongValue;
            if (remainBytes > 0) {
                int chunk = remainBytes > 8192 ? 8192 : (int) remainBytes;
                int readBytes = this.source.read(this.streamCopyBuf, 0, chunk);
                if (readBytes <= 0) {
                    throw new IOException("read error at offset " + this.source.getPosition() + ": expected " + chunk + " bytes, but read() returns " + readBytes);
                }
                out.write(this.streamCopyBuf, 0, readBytes);
                jLongValue = remainBytes - ((long) readBytes);
            } else {
                return;
            }
        }
    }

    private boolean validateStreamLength(long streamLength) throws IOException {
        boolean streamLengthIsValid = true;
        long originOffset = this.source.getPosition();
        long expectedEndOfStream = originOffset + streamLength;
        if (expectedEndOfStream > this.fileLen) {
            streamLengthIsValid = false;
            LOG.warn("The end of the stream is out of range, using workaround to read the stream, stream start position: " + originOffset + ", length: " + streamLength + ", expected end position: " + expectedEndOfStream);
        } else {
            this.source.seek(expectedEndOfStream);
            skipSpaces();
            if (!isString(ENDSTREAM)) {
                streamLengthIsValid = false;
                LOG.warn("The end of the stream doesn't point to the correct offset, using workaround to read the stream, stream start position: " + originOffset + ", length: " + streamLength + ", expected end position: " + expectedEndOfStream);
            }
            this.source.seek(originOffset);
        }
        return streamLengthIsValid;
    }

    private long checkXRefOffset(long startXRefOffset) throws IOException {
        if (!this.isLenient) {
            return startXRefOffset;
        }
        this.source.seek(startXRefOffset);
        skipSpaces();
        if (this.source.peek() == 120 && isString(XREF_TABLE)) {
            return startXRefOffset;
        }
        if (startXRefOffset > 0) {
            if (checkXRefStreamOffset(startXRefOffset)) {
                return startXRefOffset;
            }
            return calculateXRefFixedOffset(startXRefOffset, false);
        }
        return -1L;
    }

    private boolean checkXRefStreamOffset(long startXRefOffset) throws IOException {
        if (!this.isLenient || startXRefOffset == 0) {
            return true;
        }
        this.source.seek(startXRefOffset - 1);
        int nextValue = this.source.read();
        if (isWhitespace(nextValue)) {
            skipSpaces();
            if (isDigit()) {
                try {
                    readObjectNumber();
                    readGenerationNumber();
                    readExpectedString(OBJ_MARKER, true);
                    COSDictionary dict = parseCOSDictionary();
                    this.source.seek(startXRefOffset);
                    if ("XRef".equals(dict.getNameAsString(COSName.TYPE))) {
                        return true;
                    }
                    return false;
                } catch (IOException e) {
                    this.source.seek(startXRefOffset);
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    private long calculateXRefFixedOffset(long objectOffset, boolean streamsOnly) throws IOException {
        if (objectOffset < 0) {
            LOG.error("Invalid object offset " + objectOffset + " when searching for a xref table/stream");
            return 0L;
        }
        long newOffset = bfSearchForXRef(objectOffset, streamsOnly);
        if (newOffset > -1) {
            LOG.debug("Fixed reference for xref table/stream " + objectOffset + " -> " + newOffset);
            return newOffset;
        }
        LOG.error("Can't find the object xref table/stream at offset " + objectOffset);
        return 0L;
    }

    private boolean validateXrefOffsets(Map<COSObjectKey, Long> map) throws IOException {
        if (map == null) {
            return true;
        }
        Map<COSObjectKey, COSObjectKey> correctedKeys = new HashMap<>();
        for (Map.Entry<COSObjectKey, Long> objectEntry : map.entrySet()) {
            COSObjectKey objectKey = objectEntry.getKey();
            Long objectOffset = objectEntry.getValue();
            if (objectOffset != null && objectOffset.longValue() >= 0) {
                COSObjectKey foundObjectKey = findObjectKey(objectKey, objectOffset.longValue());
                if (foundObjectKey == null) {
                    LOG.debug("Stop checking xref offsets as at least one (" + objectKey + ") couldn't be dereferenced");
                    return false;
                }
                if (foundObjectKey != objectKey) {
                    correctedKeys.put(objectKey, foundObjectKey);
                }
            }
        }
        for (Map.Entry<COSObjectKey, COSObjectKey> correctedKeyEntry : correctedKeys.entrySet()) {
            map.put(correctedKeyEntry.getValue(), map.remove(correctedKeyEntry.getKey()));
        }
        return true;
    }

    private void checkXrefOffsets() throws IOException {
        if (!this.isLenient) {
            return;
        }
        Map<COSObjectKey, Long> xrefOffset = this.xrefTrailerResolver.getXrefTable();
        if (!validateXrefOffsets(xrefOffset)) {
            bfSearchForObjects();
            if (this.bfSearchCOSObjectKeyOffsets != null && !this.bfSearchCOSObjectKeyOffsets.isEmpty()) {
                LOG.debug("Replaced read xref table with the results of a brute force search");
                xrefOffset.clear();
                xrefOffset.putAll(this.bfSearchCOSObjectKeyOffsets);
            }
        }
    }

    private COSObjectKey findObjectKey(COSObjectKey objectKey, long offset) throws IOException {
        if (offset < MINIMUM_SEARCH_OFFSET) {
            return null;
        }
        try {
            this.source.seek(offset);
            if (objectKey.getNumber() == readObjectNumber()) {
                int genNumber = readGenerationNumber();
                readExpectedString(OBJ_MARKER, true);
                if (genNumber == objectKey.getGeneration()) {
                    return objectKey;
                }
                if (this.isLenient && genNumber > objectKey.getGeneration()) {
                    return new COSObjectKey(objectKey.getNumber(), genNumber);
                }
                return null;
            }
            return null;
        } catch (IOException exception) {
            LOG.debug("No valid object at given location " + offset + " - ignoring", exception);
            return null;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v102, types: [org.apache.pdfbox.io.RandomAccessRead] */
    /* JADX WARN: Type inference failed for: r0v107, types: [org.apache.pdfbox.io.RandomAccessRead] */
    /* JADX WARN: Type inference failed for: r0v75, types: [long] */
    /* JADX WARN: Type inference failed for: r0v79, types: [long] */
    /* JADX WARN: Type inference failed for: r0v91, types: [long] */
    /* JADX WARN: Type inference failed for: r1v35, types: [long] */
    /* JADX WARN: Type inference failed for: r1v37, types: [long] */
    /* JADX WARN: Type inference failed for: r21v2 */
    /* JADX WARN: Type inference failed for: r21v3 */
    /* JADX WARN: Type inference failed for: r21v4 */
    /* JADX WARN: Type inference failed for: r21v5 */
    /* JADX WARN: Type inference failed for: r21v6 */
    /* JADX WARN: Type inference failed for: r21v7 */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void bfSearchForObjects() throws IOException {
        long j;
        ?? r21;
        boolean z;
        if (this.bfSearchCOSObjectKeyOffsets == null) {
            bfSearchForLastEOFMarker();
            this.bfSearchCOSObjectKeyOffsets = new HashMap();
            long position = this.source.getPosition();
            long length = 6;
            long j2 = Long.MIN_VALUE;
            int i = Integer.MIN_VALUE;
            long j3 = Long.MIN_VALUE;
            char[] charArray = "ndo".toCharArray();
            char[] charArray2 = "bj".toCharArray();
            boolean z2 = false;
            do {
                this.source.seek(length);
                int i2 = this.source.read();
                length++;
                if (isWhitespace(i2) && isString(OBJ_MARKER)) {
                    long j4 = length - 2;
                    this.source.seek(j4);
                    int iPeek = this.source.peek();
                    if (isDigit(iPeek)) {
                        int i3 = iPeek - 48;
                        j = j4 - 1;
                        this.source.seek(j);
                        if (isWhitespace()) {
                            while (true) {
                                r21 = j;
                                if (r21 <= MINIMUM_SEARCH_OFFSET || !isWhitespace()) {
                                    break;
                                }
                                ?? r0 = this.source;
                                long j5 = r21 - 1;
                                r21 = r0;
                                r0.seek(j5);
                            }
                            boolean z3 = false;
                            ?? r22 = r21;
                            while (true) {
                                z = z3;
                                if (r22 <= MINIMUM_SEARCH_OFFSET || !isDigit()) {
                                    break;
                                }
                                ?? r1 = this.source;
                                long j6 = r22 - 1;
                                r22 = r1;
                                r1.seek(j6);
                                z3 = true;
                            }
                            if (z) {
                                this.source.read();
                                long objectNumber = readObjectNumber();
                                if (j3 > 0) {
                                    this.bfSearchCOSObjectKeyOffsets.put(new COSObjectKey(j2, i), Long.valueOf(j3));
                                }
                                j2 = objectNumber;
                                i = i3;
                                j3 = r22 + 1;
                                length += (long) (OBJ_MARKER.length - 1);
                                z2 = false;
                            }
                        }
                    }
                } else if (i2 == 101 && isString(charArray)) {
                    length += (long) charArray.length;
                    this.source.seek(length);
                    if (this.source.isEOF()) {
                        z2 = true;
                    } else if (isString(charArray2)) {
                        length += (long) charArray2.length;
                        z2 = true;
                    }
                }
                r21 = j;
                if (length >= this.lastEOFMarker.longValue()) {
                    break;
                }
            } while (!this.source.isEOF());
            if ((this.lastEOFMarker.longValue() < Long.MAX_VALUE || z2) && j3 > 0) {
                this.bfSearchCOSObjectKeyOffsets.put(new COSObjectKey(j2, i), Long.valueOf(j3));
            }
            this.source.seek(position);
        }
    }

    private long bfSearchForXRef(long xrefOffset, boolean streamsOnly) throws IOException {
        long newOffset = -1;
        long newOffsetTable = -1;
        long newOffsetStream = -1;
        if (!streamsOnly) {
            bfSearchForXRefTables();
        }
        bfSearchForXRefStreams();
        if (!streamsOnly && this.bfSearchXRefTablesOffsets != null) {
            newOffsetTable = searchNearestValue(this.bfSearchXRefTablesOffsets, xrefOffset);
        }
        if (this.bfSearchXRefStreamsOffsets != null) {
            newOffsetStream = searchNearestValue(this.bfSearchXRefStreamsOffsets, xrefOffset);
        }
        if (newOffsetTable > -1 && newOffsetStream > -1) {
            long differenceTable = xrefOffset - newOffsetTable;
            long differenceStream = xrefOffset - newOffsetStream;
            if (Math.abs(differenceTable) > Math.abs(differenceStream)) {
                newOffset = newOffsetStream;
                this.bfSearchXRefStreamsOffsets.remove(Long.valueOf(newOffsetStream));
            } else {
                newOffset = newOffsetTable;
                this.bfSearchXRefTablesOffsets.remove(Long.valueOf(newOffsetTable));
            }
        } else if (newOffsetTable > -1) {
            newOffset = newOffsetTable;
            this.bfSearchXRefTablesOffsets.remove(Long.valueOf(newOffsetTable));
        } else if (newOffsetStream > -1) {
            newOffset = newOffsetStream;
            this.bfSearchXRefStreamsOffsets.remove(Long.valueOf(newOffsetStream));
        }
        return newOffset;
    }

    private long searchNearestValue(List<Long> values, long offset) {
        long newValue = -1;
        Long currentDifference = null;
        int currentOffsetIndex = -1;
        int numberOfOffsets = values.size();
        for (int i = 0; i < numberOfOffsets; i++) {
            long newDifference = offset - values.get(i).longValue();
            if (currentDifference == null || Math.abs(currentDifference.longValue()) > Math.abs(newDifference)) {
                currentDifference = Long.valueOf(newDifference);
                currentOffsetIndex = i;
            }
        }
        if (currentOffsetIndex > -1) {
            newValue = values.get(currentOffsetIndex).longValue();
        }
        return newValue;
    }

    private boolean bfSearchForTrailer(COSDictionary trailer) throws IOException {
        COSObject encObj;
        COSDictionary infoDict;
        COSDictionary rootDict;
        long originOffset = this.source.getPosition();
        this.source.seek(MINIMUM_SEARCH_OFFSET);
        while (!this.source.isEOF()) {
            if (isString(TRAILER_MARKER)) {
                this.source.seek(this.source.getPosition() + ((long) TRAILER_MARKER.length));
                try {
                    boolean rootFound = false;
                    boolean infoFound = false;
                    skipSpaces();
                    COSDictionary trailerDict = parseCOSDictionary();
                    COSObject rootObj = trailerDict.getCOSObject(COSName.ROOT);
                    if (rootObj != null && (rootDict = retrieveCOSDictionary(rootObj)) != null && isCatalog(rootDict)) {
                        rootFound = true;
                    }
                    COSObject infoObj = trailerDict.getCOSObject(COSName.INFO);
                    if (infoObj != null && (infoDict = retrieveCOSDictionary(infoObj)) != null && isInfo(infoDict)) {
                        infoFound = true;
                    }
                    if (rootFound && infoFound) {
                        trailer.setItem(COSName.ROOT, (COSBase) rootObj);
                        trailer.setItem(COSName.INFO, (COSBase) infoObj);
                        if (trailerDict.containsKey(COSName.ENCRYPT) && (encObj = trailerDict.getCOSObject(COSName.ENCRYPT)) != null) {
                            COSDictionary encDict = retrieveCOSDictionary(encObj);
                            if (encDict != null) {
                                trailer.setItem(COSName.ENCRYPT, (COSBase) encObj);
                            }
                        }
                        if (trailerDict.containsKey(COSName.ID)) {
                            COSBase idObj = trailerDict.getItem(COSName.ID);
                            if (idObj instanceof COSArray) {
                                trailer.setItem(COSName.ID, idObj);
                                return true;
                            }
                            return true;
                        }
                        return true;
                    }
                } catch (IOException e) {
                }
            }
            this.source.read();
        }
        this.source.seek(originOffset);
        return false;
    }

    private void bfSearchForLastEOFMarker() throws IOException {
        if (this.lastEOFMarker == null) {
            long originOffset = this.source.getPosition();
            this.source.seek(MINIMUM_SEARCH_OFFSET);
            while (!this.source.isEOF()) {
                if (isString(EOF_MARKER)) {
                    long tempMarker = this.source.getPosition();
                    this.source.seek(tempMarker + 5);
                    try {
                        skipSpaces();
                        if (!isString(XREF_TABLE)) {
                            readObjectNumber();
                            readGenerationNumber();
                        }
                    } catch (IOException e) {
                        this.lastEOFMarker = Long.valueOf(tempMarker);
                    }
                }
                this.source.read();
            }
            this.source.seek(originOffset);
            if (this.lastEOFMarker == null) {
                this.lastEOFMarker = Long.MAX_VALUE;
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v151, types: [org.apache.pdfbox.io.RandomAccessRead] */
    /* JADX WARN: Type inference failed for: r0v152, types: [long] */
    /* JADX WARN: Type inference failed for: r0v171, types: [org.apache.pdfbox.io.RandomAccessRead] */
    /* JADX WARN: Type inference failed for: r1v74, types: [long] */
    /* JADX WARN: Type inference failed for: r21v5 */
    /* JADX WARN: Type inference failed for: r21v6 */
    /* JADX WARN: Type inference failed for: r21v7 */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void bfSearchForObjStreams() throws IOException {
        HashMap<Long, COSObjectKey> bfSearchObjStreamsOffsets = new HashMap<>();
        long originOffset = this.source.getPosition();
        this.source.seek(MINIMUM_SEARCH_OFFSET);
        char[] string = " obj".toCharArray();
        while (!this.source.isEOF()) {
            if (isString(OBJ_STREAM)) {
                long currentPosition = this.source.getPosition();
                long newOffset = -1;
                boolean objFound = false;
                for (int i = 1; i < 40 && !objFound; i++) {
                    long currentOffset = currentPosition - ((long) (i * 10));
                    if (currentOffset > 0) {
                        this.source.seek(currentOffset);
                        for (int j = 0; j < 10; j++) {
                            if (isString(string)) {
                                long tempOffset = currentOffset - 1;
                                this.source.seek(tempOffset);
                                int genID = this.source.peek();
                                if (isDigit(genID)) {
                                    long tempOffset2 = tempOffset - 1;
                                    this.source.seek(tempOffset2);
                                    if (isSpace()) {
                                        int length = 0;
                                        ?? r0 = this.source;
                                        ?? r21 = r0;
                                        r0.seek(tempOffset2 - 1);
                                        while (r21 > MINIMUM_SEARCH_OFFSET && isDigit()) {
                                            ?? r1 = this.source;
                                            long j2 = r21 - 1;
                                            r21 = r1;
                                            r1.seek(j2);
                                            length++;
                                        }
                                        if (length > 0) {
                                            this.source.read();
                                            newOffset = this.source.getPosition();
                                            long objNumber = readObjectNumber();
                                            int genNumber = readGenerationNumber();
                                            COSObjectKey streamObjectKey = new COSObjectKey(objNumber, genNumber);
                                            bfSearchObjStreamsOffsets.put(Long.valueOf(newOffset), streamObjectKey);
                                        }
                                    }
                                }
                                LOG.debug("Dictionary start for object stream -> " + newOffset);
                                objFound = true;
                                break;
                            }
                            currentOffset++;
                            this.source.read();
                        }
                    }
                }
                this.source.seek(currentPosition + ((long) OBJ_STREAM.length));
            }
            this.source.read();
        }
        for (Long offset : bfSearchObjStreamsOffsets.keySet()) {
            Long bfOffset = this.bfSearchCOSObjectKeyOffsets.get(bfSearchObjStreamsOffsets.get(offset));
            if (bfOffset == null) {
                LOG.warn("Skipped incomplete object stream:" + bfSearchObjStreamsOffsets.get(offset) + " at " + offset);
            } else if (offset.equals(bfOffset)) {
                this.source.seek(offset.longValue());
                long stmObjNumber = readObjectNumber();
                int stmGenNumber = readGenerationNumber();
                readExpectedString(OBJ_MARKER, true);
                COSStream stream = null;
                try {
                    try {
                        COSDictionary dict = parseCOSDictionary();
                        int offsetFirstStream = dict.getInt(COSName.FIRST);
                        int nrOfObjects = dict.getInt(COSName.N);
                        if (offsetFirstStream != -1 && nrOfObjects != -1) {
                            COSStream stream2 = parseCOSStream(dict);
                            if (this.securityHandler != null) {
                                this.securityHandler.decryptStream(stream2, stmObjNumber, stmGenNumber);
                            }
                            PDFObjectStreamParser strmParser = new PDFObjectStreamParser(stream2, this.document);
                            List<Long> objectNumbers = new ArrayList<>(nrOfObjects);
                            for (int i2 = 0; i2 < nrOfObjects; i2++) {
                                objectNumbers.add(Long.valueOf(strmParser.readObjectNumber()));
                                strmParser.readLong();
                            }
                            if (stream2 != null) {
                                stream2.close();
                            }
                            if (objectNumbers.size() < nrOfObjects) {
                                LOG.debug("Skipped corrupt stream: (" + stmObjNumber + " 0 at offset " + offset);
                            } else {
                                Map<COSObjectKey, Long> xrefOffset = this.xrefTrailerResolver.getXrefTable();
                                for (Long objNumber2 : objectNumbers) {
                                    COSObjectKey objKey = new COSObjectKey(objNumber2.longValue(), 0);
                                    Long existingOffset = this.bfSearchCOSObjectKeyOffsets.get(objKey);
                                    if (existingOffset != null && existingOffset.longValue() < 0) {
                                        COSObjectKey objStmKey = new COSObjectKey(Math.abs(existingOffset.longValue()), 0);
                                        existingOffset = this.bfSearchCOSObjectKeyOffsets.get(objStmKey);
                                    }
                                    if (existingOffset == null || offset.longValue() > existingOffset.longValue()) {
                                        this.bfSearchCOSObjectKeyOffsets.put(objKey, Long.valueOf(-stmObjNumber));
                                        xrefOffset.put(objKey, Long.valueOf(-stmObjNumber));
                                    }
                                }
                            }
                        } else if (0 != 0) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        LOG.debug("Skipped corrupt stream: (" + stmObjNumber + " 0 at offset " + offset);
                        if (0 != 0) {
                            stream.close();
                        }
                    }
                } catch (Throwable th) {
                    if (0 != 0) {
                        stream.close();
                    }
                    throw th;
                }
            }
        }
        this.source.seek(originOffset);
    }

    private void bfSearchForXRefTables() throws IOException {
        if (this.bfSearchXRefTablesOffsets == null) {
            this.bfSearchXRefTablesOffsets = new ArrayList();
            long originOffset = this.source.getPosition();
            this.source.seek(MINIMUM_SEARCH_OFFSET);
            while (!this.source.isEOF()) {
                if (isString(XREF_TABLE)) {
                    long newOffset = this.source.getPosition();
                    this.source.seek(newOffset - 1);
                    if (isWhitespace()) {
                        this.bfSearchXRefTablesOffsets.add(Long.valueOf(newOffset));
                    }
                    this.source.seek(newOffset + 4);
                }
                this.source.read();
            }
            this.source.seek(originOffset);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v65, types: [org.apache.pdfbox.io.RandomAccessRead] */
    /* JADX WARN: Type inference failed for: r0v66, types: [long] */
    /* JADX WARN: Type inference failed for: r0v78, types: [org.apache.pdfbox.io.RandomAccessRead] */
    /* JADX WARN: Type inference failed for: r1v30, types: [long] */
    /* JADX WARN: Type inference failed for: r20v2 */
    /* JADX WARN: Type inference failed for: r20v3 */
    /* JADX WARN: Type inference failed for: r20v4 */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void bfSearchForXRefStreams() throws IOException {
        if (this.bfSearchXRefStreamsOffsets == null) {
            this.bfSearchXRefStreamsOffsets = new ArrayList();
            long originOffset = this.source.getPosition();
            this.source.seek(MINIMUM_SEARCH_OFFSET);
            char[] string = " obj".toCharArray();
            while (!this.source.isEOF()) {
                if (isString(XREF_STREAM)) {
                    long newOffset = -1;
                    long xrefOffset = this.source.getPosition();
                    boolean objFound = false;
                    for (int i = 1; i < 40 && !objFound; i++) {
                        long currentOffset = xrefOffset - ((long) (i * 10));
                        if (currentOffset > 0) {
                            this.source.seek(currentOffset);
                            for (int j = 0; j < 10; j++) {
                                if (isString(string)) {
                                    long tempOffset = currentOffset - 1;
                                    this.source.seek(tempOffset);
                                    int genID = this.source.peek();
                                    if (isDigit(genID)) {
                                        long tempOffset2 = tempOffset - 1;
                                        this.source.seek(tempOffset2);
                                        if (isSpace()) {
                                            int length = 0;
                                            ?? r0 = this.source;
                                            ?? r20 = r0;
                                            r0.seek(tempOffset2 - 1);
                                            while (r20 > MINIMUM_SEARCH_OFFSET && isDigit()) {
                                                ?? r1 = this.source;
                                                long j2 = r20 - 1;
                                                r20 = r1;
                                                r1.seek(j2);
                                                length++;
                                            }
                                            if (length > 0) {
                                                this.source.read();
                                                newOffset = this.source.getPosition();
                                            }
                                        }
                                    }
                                    LOG.debug("Fixed reference for xref stream " + xrefOffset + " -> " + newOffset);
                                    objFound = true;
                                    break;
                                }
                                currentOffset++;
                                this.source.read();
                            }
                        }
                    }
                    if (newOffset > -1) {
                        this.bfSearchXRefStreamsOffsets.add(Long.valueOf(newOffset));
                    }
                    this.source.seek(xrefOffset + 5);
                }
                this.source.read();
            }
            this.source.seek(originOffset);
        }
    }

    protected final COSDictionary rebuildTrailer() throws IOException {
        COSDictionary trailer = null;
        bfSearchForObjects();
        if (this.bfSearchCOSObjectKeyOffsets != null) {
            this.xrefTrailerResolver.reset();
            this.xrefTrailerResolver.nextXrefObj(0L, XrefTrailerResolver.XRefType.TABLE);
            for (Map.Entry<COSObjectKey, Long> entry : this.bfSearchCOSObjectKeyOffsets.entrySet()) {
                this.xrefTrailerResolver.setXRef(entry.getKey(), entry.getValue().longValue());
            }
            this.xrefTrailerResolver.setStartxref(0L);
            trailer = this.xrefTrailerResolver.getTrailer();
            getDocument().setTrailer(trailer);
            boolean searchForObjStreamsDone = false;
            if (!bfSearchForTrailer(trailer) && !searchForTrailerItems(trailer)) {
                bfSearchForObjStreams();
                searchForObjStreamsDone = true;
                searchForTrailerItems(trailer);
            }
            prepareDecryption();
            if (!searchForObjStreamsDone) {
                bfSearchForObjStreams();
            }
        }
        this.trailerWasRebuild = true;
        return trailer;
    }

    private boolean searchForTrailerItems(COSDictionary trailer) throws IOException {
        COSObject rootObject = null;
        Long rootOffset = null;
        COSObject infoObject = null;
        Long infoOffset = null;
        for (Map.Entry<COSObjectKey, Long> entry : this.bfSearchCOSObjectKeyOffsets.entrySet()) {
            COSDictionary dictionary = retrieveCOSDictionary(entry.getKey(), entry.getValue().longValue());
            if (dictionary != null) {
                if (isCatalog(dictionary)) {
                    COSObject cosObject = this.document.getObjectFromPool(entry.getKey());
                    rootObject = compareCOSObjects(cosObject, entry.getValue(), rootObject, rootOffset);
                    if (rootObject == cosObject) {
                        rootOffset = entry.getValue();
                    }
                } else if (isInfo(dictionary)) {
                    COSObject cosObject2 = this.document.getObjectFromPool(entry.getKey());
                    infoObject = compareCOSObjects(cosObject2, entry.getValue(), infoObject, infoOffset);
                    if (infoObject == cosObject2) {
                        infoOffset = entry.getValue();
                    }
                }
            }
        }
        if (rootObject != null) {
            trailer.setItem(COSName.ROOT, (COSBase) rootObject);
        }
        if (infoObject != null) {
            trailer.setItem(COSName.INFO, (COSBase) infoObject);
        }
        return rootObject != null;
    }

    private COSObject compareCOSObjects(COSObject newObject, Long newOffset, COSObject currentObject, Long currentOffset) {
        if (currentObject == null) {
            return newObject;
        }
        if (currentObject.getObjectNumber() == newObject.getObjectNumber()) {
            return currentObject.getGenerationNumber() < newObject.getGenerationNumber() ? newObject : currentObject;
        }
        return (currentOffset == null || newOffset.longValue() <= currentOffset.longValue()) ? currentObject : newObject;
    }

    private COSDictionary retrieveCOSDictionary(COSObject object) throws IOException {
        COSObjectKey key = new COSObjectKey(object);
        Long offset = this.bfSearchCOSObjectKeyOffsets.get(key);
        if (offset != null) {
            long currentPosition = this.source.getPosition();
            COSDictionary dictionary = retrieveCOSDictionary(key, offset.longValue());
            this.source.seek(currentPosition);
            return dictionary;
        }
        return null;
    }

    private COSDictionary retrieveCOSDictionary(COSObjectKey key, long offset) throws IOException {
        COSDictionary dictionary = null;
        if (offset < 0) {
            COSObject compressedObject = this.document.getObjectFromPool(key);
            if (compressedObject.getObject() == null) {
                parseObjectStream((int) (-offset));
            }
            COSBase baseObject = compressedObject.getObject();
            if (baseObject instanceof COSDictionary) {
                dictionary = (COSDictionary) baseObject;
            }
        } else {
            this.source.seek(offset);
            readObjectNumber();
            readGenerationNumber();
            readExpectedString(OBJ_MARKER, true);
            if (this.source.peek() != 60) {
                return null;
            }
            try {
                dictionary = parseCOSDictionary();
            } catch (IOException e) {
                LOG.debug("Skipped object " + key + ", either it's corrupt or not a dictionary");
            }
        }
        return dictionary;
    }

    protected void checkPages(COSDictionary root) {
        if (this.trailerWasRebuild && root != null) {
            COSBase pages = root.getDictionaryObject(COSName.PAGES);
            if (pages instanceof COSDictionary) {
                checkPagesDictionary((COSDictionary) pages, new HashSet());
            }
        }
    }

    private int checkPagesDictionary(COSDictionary pagesDict, Set<COSObject> set) {
        COSBase kids = pagesDict.getDictionaryObject(COSName.KIDS);
        int numberOfPages = 0;
        if (kids instanceof COSArray) {
            COSArray kidsArray = (COSArray) kids;
            List<? extends COSBase> kidsList = kidsArray.toList();
            for (COSBase kid : kidsList) {
                if (!(kid instanceof COSObject) || set.contains((COSObject) kid)) {
                    kidsArray.remove(kid);
                } else {
                    COSObject kidObject = (COSObject) kid;
                    COSBase kidBaseobject = kidObject.getObject();
                    if (kidBaseobject == null || kidBaseobject.equals(COSNull.NULL)) {
                        LOG.warn("Removed null object " + kid + " from pages dictionary");
                        kidsArray.remove(kid);
                    } else if (kidBaseobject instanceof COSDictionary) {
                        COSDictionary kidDictionary = (COSDictionary) kidBaseobject;
                        COSName type = kidDictionary.getCOSName(COSName.TYPE);
                        if (COSName.PAGES.equals(type)) {
                            set.add(kidObject);
                            numberOfPages += checkPagesDictionary(kidDictionary, set);
                        } else if (COSName.PAGE.equals(type)) {
                            numberOfPages++;
                        }
                    }
                }
            }
        }
        pagesDict.setInt(COSName.COUNT, numberOfPages);
        return numberOfPages;
    }

    protected boolean isCatalog(COSDictionary dictionary) {
        return COSName.CATALOG.equals(dictionary.getCOSName(COSName.TYPE));
    }

    private boolean isInfo(COSDictionary dictionary) {
        if (dictionary.containsKey(COSName.PARENT) || dictionary.containsKey(COSName.A) || dictionary.containsKey(COSName.DEST)) {
            return false;
        }
        if (!dictionary.containsKey(COSName.MOD_DATE) && !dictionary.containsKey(COSName.TITLE) && !dictionary.containsKey(COSName.AUTHOR) && !dictionary.containsKey(COSName.SUBJECT) && !dictionary.containsKey(COSName.KEYWORDS) && !dictionary.containsKey(COSName.CREATOR) && !dictionary.containsKey(COSName.PRODUCER) && !dictionary.containsKey(COSName.CREATION_DATE)) {
            return false;
        }
        return true;
    }

    private long parseStartXref() throws IOException {
        long startXref = -1;
        if (isString(STARTXREF)) {
            readString();
            skipSpaces();
            startXref = readLong();
        }
        return startXref;
    }

    private boolean isString(byte[] string) throws IOException {
        int numberOfBytes;
        int readMore;
        boolean bytesMatching = false;
        if (this.source.peek() == string[0]) {
            int length = string.length;
            byte[] bytesRead = new byte[length];
            int i = this.source.read(bytesRead, 0, length);
            while (true) {
                numberOfBytes = i;
                if (numberOfBytes >= length || (readMore = this.source.read(bytesRead, numberOfBytes, length - numberOfBytes)) < 0) {
                    break;
                }
                i = numberOfBytes + readMore;
            }
            bytesMatching = Arrays.equals(string, bytesRead);
            this.source.rewind(numberOfBytes);
        }
        return bytesMatching;
    }

    private boolean isString(char[] string) throws IOException {
        boolean bytesMatching = true;
        long originOffset = this.source.getPosition();
        for (char c : string) {
            if (this.source.read() != c) {
                bytesMatching = false;
                break;
            }
        }
        this.source.seek(originOffset);
        return bytesMatching;
    }

    private boolean parseTrailer() throws IOException {
        this.trailerOffset = this.source.getPosition();
        if (this.isLenient) {
            int iPeek = this.source.peek();
            while (true) {
                int nextCharacter = iPeek;
                if (nextCharacter == 116 || !isDigit(nextCharacter)) {
                    break;
                }
                if (this.source.getPosition() == this.trailerOffset) {
                    LOG.warn("Expected trailer object at offset " + this.trailerOffset + ", keep trying");
                }
                readLine();
                iPeek = this.source.peek();
            }
        }
        if (this.source.peek() != 116) {
            return false;
        }
        long currentOffset = this.source.getPosition();
        String nextLine = readLine();
        if (!nextLine.trim().equals("trailer")) {
            if (nextLine.startsWith("trailer")) {
                int len = "trailer".length();
                this.source.seek(currentOffset + ((long) len));
            } else {
                return false;
            }
        }
        skipSpaces();
        COSDictionary parsedTrailer = parseCOSDictionary();
        this.xrefTrailerResolver.setTrailer(parsedTrailer);
        skipSpaces();
        return true;
    }

    protected boolean parsePDFHeader() throws IOException {
        return parseHeader(PDF_HEADER, PDF_DEFAULT_VERSION);
    }

    protected boolean parseFDFHeader() throws IOException {
        return parseHeader(FDF_HEADER, "1.0");
    }

    private boolean parseHeader(String headerMarker, String defaultVersion) throws IOException {
        String header = readLine();
        if (!header.contains(headerMarker)) {
            String line = readLine();
            while (true) {
                header = line;
                if (header.contains(headerMarker) || (header.length() > 0 && Character.isDigit(header.charAt(0)))) {
                    break;
                }
                line = readLine();
            }
        }
        if (!header.contains(headerMarker)) {
            this.source.seek(0L);
            return false;
        }
        int headerStart = header.indexOf(headerMarker);
        if (headerStart > 0) {
            header = header.substring(headerStart);
        }
        if (header.startsWith(headerMarker) && !header.matches(headerMarker + "\\d.\\d")) {
            if (header.length() < headerMarker.length() + 3) {
                header = headerMarker + defaultVersion;
                LOG.debug("No version found, set to " + defaultVersion + " as default.");
            } else {
                String headerGarbage = header.substring(headerMarker.length() + 3, header.length()) + "\n";
                header = header.substring(0, headerMarker.length() + 3);
                this.source.rewind(headerGarbage.getBytes(Charsets.ISO_8859_1).length);
            }
        }
        float headerVersion = -1.0f;
        try {
            String[] headerParts = header.split("-");
            if (headerParts.length == 2) {
                headerVersion = Float.parseFloat(headerParts[1]);
            }
        } catch (NumberFormatException exception) {
            LOG.debug("Can't parse the header version.", exception);
        }
        if (headerVersion < ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            if (this.isLenient) {
                headerVersion = 1.7f;
            } else {
                throw new IOException("Error getting header version: " + header);
            }
        }
        this.document.setVersion(headerVersion);
        this.source.seek(0L);
        return true;
    }

    protected boolean parseXrefTable(long startByteOffset) throws IOException {
        if (this.source.peek() != 120) {
            return false;
        }
        String xref = readString();
        if (!xref.trim().equals("xref")) {
            return false;
        }
        String str = readString();
        byte[] b = str.getBytes(Charsets.ISO_8859_1);
        this.source.rewind(b.length);
        this.xrefTrailerResolver.nextXrefObj(startByteOffset, XrefTrailerResolver.XRefType.TABLE);
        if (str.startsWith("trailer")) {
            LOG.warn("skipping empty xref table");
            return false;
        }
        do {
            String currentLine = readLine();
            String[] splitString = currentLine.split("\\s");
            if (splitString.length != 2) {
                LOG.warn("Unexpected XRefTable Entry: " + currentLine);
                return false;
            }
            try {
                long currObjID = Long.parseLong(splitString[0]);
                try {
                    int count = Integer.parseInt(splitString[1]);
                    skipSpaces();
                    for (int i = 0; i < count && !this.source.isEOF() && !isEndOfName((char) this.source.peek()) && this.source.peek() != 116; i++) {
                        String currentLine2 = readLine();
                        String[] splitString2 = currentLine2.split("\\s");
                        if (splitString2.length < 3) {
                            LOG.warn("invalid xref line: " + currentLine2);
                            break;
                        }
                        if (splitString2[splitString2.length - 1].equals(OperatorName.ENDPATH)) {
                            try {
                                long currOffset = Long.parseLong(splitString2[0]);
                                if (currOffset > 0) {
                                    int currGenID = Integer.parseInt(splitString2[1]);
                                    COSObjectKey objKey = new COSObjectKey(currObjID, currGenID);
                                    this.xrefTrailerResolver.setXRef(objKey, currOffset);
                                }
                            } catch (NumberFormatException e) {
                                throw new IOException(e);
                            }
                        } else if (!splitString2[2].equals(OperatorName.FILL_NON_ZERO)) {
                            throw new IOException("Corrupt XRefTable Entry - ObjID:" + currObjID);
                        }
                        currObjID++;
                        skipSpaces();
                    }
                    skipSpaces();
                } catch (NumberFormatException e2) {
                    LOG.warn("XRefTable: invalid number of objects: " + currentLine);
                    return false;
                }
            } catch (NumberFormatException e3) {
                LOG.warn("XRefTable: invalid ID for the first object: " + currentLine);
                return false;
            }
        } while (isDigit());
        return true;
    }

    private void parseXrefStream(COSStream stream, long objByteOffset, boolean isStandalone) throws IOException {
        if (isStandalone) {
            this.xrefTrailerResolver.nextXrefObj(objByteOffset, XrefTrailerResolver.XRefType.STREAM);
            this.xrefTrailerResolver.setTrailer(stream);
        }
        PDFXrefStreamParser parser = new PDFXrefStreamParser(stream, this.document, this.xrefTrailerResolver);
        parser.parse();
    }

    public COSDocument getDocument() throws IOException {
        if (this.document == null) {
            throw new IOException("You must parse the document first before calling getDocument()");
        }
        return this.document;
    }

    public PDEncryption getEncryption() throws IOException {
        if (this.document == null) {
            throw new IOException("You must parse the document first before calling getEncryption()");
        }
        return this.encryption;
    }

    public AccessPermission getAccessPermission() throws IOException {
        if (this.document == null) {
            throw new IOException("You must parse the document first before calling getAccessPermission()");
        }
        return this.accessPermission;
    }

    protected COSBase parseTrailerValuesDynamically(COSDictionary trailer) throws IOException {
        for (COSBase trailerEntry : trailer.getValues()) {
            if (trailerEntry instanceof COSObject) {
                COSObject tmpObj = (COSObject) trailerEntry;
                parseObjectDynamically(tmpObj, false);
            }
        }
        COSObject root = trailer.getCOSObject(COSName.ROOT);
        if (root == null) {
            throw new IOException("Missing root object specification in trailer.");
        }
        return root.getObject();
    }

    private void prepareDecryption() throws IOException {
        COSBase trailerEncryptItem;
        DecryptionMaterial decryptionMaterial;
        if (this.encryption != null || (trailerEncryptItem = this.document.getTrailer().getItem(COSName.ENCRYPT)) == null || (trailerEncryptItem instanceof COSNull)) {
            return;
        }
        if (trailerEncryptItem instanceof COSObject) {
            COSObject trailerEncryptObj = (COSObject) trailerEncryptItem;
            parseDictionaryRecursive(trailerEncryptObj);
        }
        try {
            try {
                this.encryption = new PDEncryption(this.document.getEncryptionDictionary());
                if (this.keyStoreInputStream != null) {
                    KeyStore ks = KeyStore.getInstance("PKCS12");
                    ks.load(this.keyStoreInputStream, this.password.toCharArray());
                    decryptionMaterial = new PublicKeyDecryptionMaterial(ks, this.keyAlias, this.password);
                } else {
                    decryptionMaterial = new StandardDecryptionMaterial(this.password);
                }
                this.securityHandler = this.encryption.getSecurityHandler();
                this.securityHandler.prepareForDecryption(this.encryption, this.document.getDocumentID(), decryptionMaterial);
                this.accessPermission = this.securityHandler.getCurrentAccessPermission();
                if (this.keyStoreInputStream != null) {
                    IOUtils.closeQuietly(this.keyStoreInputStream);
                }
            } catch (IOException e) {
                throw e;
            } catch (Exception e2) {
                throw new IOException("Error (" + e2.getClass().getSimpleName() + ") while creating security handler for decryption", e2);
            }
        } catch (Throwable th) {
            if (this.keyStoreInputStream != null) {
                IOUtils.closeQuietly(this.keyStoreInputStream);
            }
            throw th;
        }
    }

    private void parseDictionaryRecursive(COSObject dictionaryObject) throws IOException {
        parseObjectDynamically(dictionaryObject, true);
        if (!(dictionaryObject.getObject() instanceof COSDictionary)) {
            throw new IOException("Dictionary object expected at offset " + this.source.getPosition());
        }
        COSDictionary dictionary = (COSDictionary) dictionaryObject.getObject();
        for (COSBase value : dictionary.getValues()) {
            if (value instanceof COSObject) {
                COSObject object = (COSObject) value;
                if (object.getObject() == null) {
                    parseDictionaryRecursive(object);
                }
            }
        }
    }
}
