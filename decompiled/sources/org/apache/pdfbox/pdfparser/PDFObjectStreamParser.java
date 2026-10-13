package org.apache.pdfbox.pdfparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdfparser/PDFObjectStreamParser.class */
public class PDFObjectStreamParser extends BaseParser {
    private static final Log LOG = LogFactory.getLog(PDFObjectStreamParser.class);
    private List<COSObject> streamObjects;
    private final int numberOfObjects;
    private final int firstObject;

    public PDFObjectStreamParser(COSStream stream, COSDocument document) throws IOException {
        super(new InputStreamSource(stream.createInputStream()));
        this.streamObjects = null;
        this.document = document;
        this.numberOfObjects = stream.getInt(COSName.N);
        if (this.numberOfObjects == -1) {
            throw new IOException("/N entry missing in object stream");
        }
        if (this.numberOfObjects < 0) {
            throw new IOException("Illegal /N entry in object stream: " + this.numberOfObjects);
        }
        this.firstObject = stream.getInt(COSName.FIRST);
        if (this.firstObject == -1) {
            throw new IOException("/First entry missing in object stream");
        }
        if (this.firstObject < 0) {
            throw new IOException("Illegal /First entry in object stream: " + this.firstObject);
        }
    }

    public void parse() throws IOException {
        try {
            Map<Integer, Long> offsets = readOffsets();
            this.streamObjects = new ArrayList(offsets.size());
            for (Map.Entry<Integer, Long> offset : offsets.entrySet()) {
                COSBase cosObject = parseObject(offset.getKey().intValue());
                COSObject object = new COSObject(cosObject);
                object.setGenerationNumber(0);
                object.setObjectNumber(offset.getValue().longValue());
                this.streamObjects.add(object);
                if (LOG.isDebugEnabled()) {
                    LOG.debug("parsed=" + object);
                }
            }
            this.seqSource.close();
        } catch (Throwable th) {
            this.seqSource.close();
            throw th;
        }
    }

    public List<COSObject> getObjects() {
        return this.streamObjects;
    }

    private Map<Integer, Long> readOffsets() throws IOException {
        Map<Integer, Long> objectNumbers = new TreeMap<>();
        long firstObjectPosition = (this.seqSource.getPosition() + ((long) this.firstObject)) - 1;
        for (int i = 0; i < this.numberOfObjects && this.seqSource.getPosition() < firstObjectPosition; i++) {
            long objectNumber = readObjectNumber();
            int offset = (int) readLong();
            objectNumbers.put(Integer.valueOf(offset), Long.valueOf(objectNumber));
        }
        return objectNumbers;
    }

    private COSBase parseObject(int offset) throws IOException {
        long currentPosition = this.seqSource.getPosition();
        int finalPosition = this.firstObject + offset;
        if (finalPosition > 0 && currentPosition < finalPosition) {
            this.seqSource.readFully(finalPosition - ((int) currentPosition));
        }
        return parseDirObject();
    }
}
