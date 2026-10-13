package org.apache.pdfbox.cos;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdfparser.PDFObjectStreamParser;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSDocument.class */
public class COSDocument extends COSBase implements Closeable {
    private static final Log LOG = LogFactory.getLog(COSDocument.class);
    private float version;
    private final Map<COSObjectKey, COSObject> objectPool;
    private final Map<COSObjectKey, Long> xrefTable;
    private final List<COSStream> streams;
    private COSDictionary trailer;
    private boolean warnMissingClose;
    private boolean isDecrypted;
    private long startXref;
    private boolean closed;
    private boolean isXRefStream;
    private ScratchFile scratchFile;
    private long highestXRefObjectNumber;

    public COSDocument() {
        this(ScratchFile.getMainMemoryOnlyInstance());
    }

    public COSDocument(ScratchFile scratchFile) {
        this.version = 1.4f;
        this.objectPool = new HashMap();
        this.xrefTable = new HashMap();
        this.streams = new ArrayList();
        this.warnMissingClose = true;
        this.isDecrypted = false;
        this.closed = false;
        this.scratchFile = scratchFile;
    }

    public COSStream createCOSStream() {
        COSStream stream = new COSStream(this.scratchFile);
        this.streams.add(stream);
        return stream;
    }

    public COSStream createCOSStream(COSDictionary dictionary) {
        COSStream stream = new COSStream(this.scratchFile);
        for (Map.Entry<COSName, COSBase> entry : dictionary.entrySet()) {
            stream.setItem(entry.getKey(), entry.getValue());
        }
        return stream;
    }

    public COSObject getObjectByType(COSName type) throws IOException {
        for (COSObject object : this.objectPool.values()) {
            COSBase realObject = object.getObject();
            if (realObject instanceof COSDictionary) {
                try {
                    COSDictionary dic = (COSDictionary) realObject;
                    COSBase typeItem = dic.getItem(COSName.TYPE);
                    if (typeItem instanceof COSName) {
                        COSName objectType = (COSName) typeItem;
                        if (objectType.equals(type)) {
                            return object;
                        }
                    } else if (typeItem != null) {
                        LOG.debug("Expected a /Name object after /Type, got '" + typeItem + "' instead");
                    }
                } catch (ClassCastException e) {
                    LOG.warn(e, e);
                }
            }
        }
        return null;
    }

    public List<COSObject> getObjectsByType(String type) throws IOException {
        return getObjectsByType(COSName.getPDFName(type));
    }

    public List<COSObject> getObjectsByType(COSName type) throws IOException {
        List<COSObject> retval = new ArrayList<>();
        for (COSObject object : this.objectPool.values()) {
            COSBase realObject = object.getObject();
            if (realObject instanceof COSDictionary) {
                try {
                    COSDictionary dic = (COSDictionary) realObject;
                    COSBase typeItem = dic.getItem(COSName.TYPE);
                    if (typeItem instanceof COSName) {
                        COSName objectType = (COSName) typeItem;
                        if (objectType.equals(type)) {
                            retval.add(object);
                        }
                    } else if (typeItem != null) {
                        LOG.debug("Expected a /Name object after /Type, got '" + typeItem + "' instead");
                    }
                } catch (ClassCastException e) {
                    LOG.warn(e, e);
                }
            }
        }
        return retval;
    }

    public COSObjectKey getKey(COSBase object) {
        for (Map.Entry<COSObjectKey, COSObject> entry : this.objectPool.entrySet()) {
            if (entry.getValue().getObject() == object) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void print() {
        for (COSObject object : this.objectPool.values()) {
            System.out.println(object);
        }
    }

    public void setVersion(float versionValue) {
        this.version = versionValue;
    }

    public float getVersion() {
        return this.version;
    }

    public void setDecrypted() {
        this.isDecrypted = true;
    }

    public boolean isDecrypted() {
        return this.isDecrypted;
    }

    public boolean isEncrypted() {
        boolean encrypted = false;
        if (this.trailer != null) {
            encrypted = this.trailer.getDictionaryObject(COSName.ENCRYPT) instanceof COSDictionary;
        }
        return encrypted;
    }

    public COSDictionary getEncryptionDictionary() {
        return this.trailer.getCOSDictionary(COSName.ENCRYPT);
    }

    public void setEncryptionDictionary(COSDictionary encDictionary) {
        this.trailer.setItem(COSName.ENCRYPT, (COSBase) encDictionary);
    }

    public COSArray getDocumentID() {
        return getTrailer().getCOSArray(COSName.ID);
    }

    public void setDocumentID(COSArray id) {
        getTrailer().setItem(COSName.ID, (COSBase) id);
    }

    public COSObject getCatalog() throws IOException {
        COSObject catalog = getObjectByType(COSName.CATALOG);
        if (catalog == null) {
            throw new IOException("Catalog cannot be found");
        }
        return catalog;
    }

    public List<COSObject> getObjects() {
        return new ArrayList(this.objectPool.values());
    }

    public COSDictionary getTrailer() {
        return this.trailer;
    }

    public void setTrailer(COSDictionary newTrailer) {
        this.trailer = newTrailer;
    }

    public long getHighestXRefObjectNumber() {
        return this.highestXRefObjectNumber;
    }

    public void setHighestXRefObjectNumber(long highestXRefObjectNumber) {
        this.highestXRefObjectNumber = highestXRefObjectNumber;
    }

    @Override // org.apache.pdfbox.cos.COSBase
    public Object accept(ICOSVisitor visitor) throws IOException {
        return visitor.visitFromDocument(this);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.closed) {
            return;
        }
        IOException firstException = null;
        for (COSObject object : getObjects()) {
            COSBase cosObject = object.getObject();
            if (cosObject instanceof COSStream) {
                firstException = IOUtils.closeAndLogException((COSStream) cosObject, LOG, "COSStream", firstException);
            }
        }
        for (COSStream stream : this.streams) {
            firstException = IOUtils.closeAndLogException(stream, LOG, "COSStream", firstException);
        }
        if (this.scratchFile != null) {
            firstException = IOUtils.closeAndLogException(this.scratchFile, LOG, "ScratchFile", firstException);
        }
        this.closed = true;
        if (firstException != null) {
            throw firstException;
        }
    }

    public boolean isClosed() {
        return this.closed;
    }

    protected void finalize() throws IOException {
        if (!this.closed) {
            if (this.warnMissingClose) {
                LOG.warn("Warning: You did not close a PDF Document");
            }
            close();
        }
    }

    public void setWarnMissingClose(boolean warn) {
        this.warnMissingClose = warn;
    }

    public void dereferenceObjectStreams() throws IOException {
        for (COSObject objStream : getObjectsByType(COSName.OBJ_STM)) {
            COSStream stream = (COSStream) objStream.getObject();
            PDFObjectStreamParser parser = new PDFObjectStreamParser(stream, this);
            parser.parse();
            for (COSObject next : parser.getObjects()) {
                COSObjectKey key = new COSObjectKey(next);
                if (this.objectPool.get(key) == null || this.objectPool.get(key).getObject() == null || (this.xrefTable.containsKey(key) && this.xrefTable.get(key).longValue() == (-objStream.getObjectNumber()))) {
                    COSObject obj = getObjectFromPool(key);
                    obj.setObject(next.getObject());
                }
            }
        }
    }

    public COSObject getObjectFromPool(COSObjectKey key) throws IOException {
        COSObject obj = null;
        if (key != null) {
            obj = this.objectPool.get(key);
        }
        if (obj == null) {
            obj = new COSObject(null);
            if (key != null) {
                obj.setObjectNumber(key.getNumber());
                obj.setGenerationNumber(key.getGeneration());
                this.objectPool.put(key, obj);
            }
        }
        return obj;
    }

    public COSObject removeObject(COSObjectKey key) {
        return this.objectPool.remove(key);
    }

    public void addXRefTable(Map<COSObjectKey, Long> xrefTableValues) {
        this.xrefTable.putAll(xrefTableValues);
    }

    public Map<COSObjectKey, Long> getXrefTable() {
        return this.xrefTable;
    }

    public void setStartXref(long startXrefValue) {
        this.startXref = startXrefValue;
    }

    public long getStartXref() {
        return this.startXref;
    }

    public boolean isXRefStream() {
        return this.isXRefStream;
    }

    public void setIsXRefStream(boolean isXRefStreamValue) {
        this.isXRefStream = isXRefStreamValue;
    }
}
