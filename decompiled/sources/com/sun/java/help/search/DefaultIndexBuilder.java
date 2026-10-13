package com.sun.java.help.search;

import com.sun.jna.platform.win32.WinError;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.help.search.IndexBuilder;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DefaultIndexBuilder.class */
public class DefaultIndexBuilder extends IndexBuilder {
    private static int InitSize = 4096;
    private FullBtreeDict dict;
    protected Hashtable cache;
    private DocumentCompressor compr;
    private int free;
    private int size;
    private ConceptLocation[] locations;
    private int freeID;
    private int currentDocID;
    private Schema _schema;
    private BtreeDictParameters _tmapParams;
    private int _title;
    private Hashtable tmpstopWords;
    private boolean debug;

    public DefaultIndexBuilder(String str) throws Exception {
        super(str);
        this.cache = new Hashtable(WinError.ERROR_EVT_INVALID_CHANNEL_PATH);
        this.free = 0;
        this.size = InitSize;
        this.locations = new ConceptLocation[this.size];
        this.currentDocID = 0;
        this._title = 0;
        this.tmpstopWords = new Hashtable();
        this.debug = false;
        removeExistingFiles(str);
        this._schema = new Schema(null, str, true);
        this._tmapParams = new BtreeDictParameters(this._schema, "TMAP");
        if (!this._tmapParams.readState()) {
            this._tmapParams.setBlockSize(2048);
            this._tmapParams.setRoot(0);
            this._tmapParams.setFreeID(1);
        }
        URL url = new URL("file", "", str);
        this.dict = new FullBtreeDict(this._tmapParams, true);
        this.freeID = this._tmapParams.getFreeID();
        this.compr = new DocumentCompressor(url);
    }

    @Override // javax.help.search.IndexBuilder
    public void storeStopWords(Enumeration enumeration) {
        while (enumeration.hasMoreElements()) {
            String str = (String) enumeration.nextElement();
            this.tmpstopWords.put(str, str);
        }
    }

    @Override // javax.help.search.IndexBuilder
    public Enumeration getStopWords() {
        return null;
    }

    private boolean isStopWord(String str) {
        return this.tmpstopWords.get(str) != null;
    }

    @Override // javax.help.search.IndexBuilder
    public void close() throws Exception {
        this.dict.close(this.freeID);
        this._tmapParams.setFreeID(this.freeID);
        this._tmapParams.updateSchema();
        debug("compacting...");
        BtreeDictCompactor btreeDictCompactor = new BtreeDictCompactor(this._tmapParams, false);
        URL url = new URL("file", "", new StringBuffer().append(this.indexDir).append("compacted").toString());
        BtreeDictParameters btreeDictParameters = new BtreeDictParameters(url, this._tmapParams.getBlockSize(), 0, this.freeID);
        btreeDictCompactor.compact(btreeDictParameters);
        File file = new File(Utilities.URLDecoder(new URL("file", "", new StringBuffer().append(this.indexDir).append("TMAP").toString()).getFile()));
        file.delete();
        new File(Utilities.URLDecoder(url.getFile())).renameTo(file);
        this._tmapParams.setRoot(btreeDictParameters.getRootPosition());
        this._tmapParams.updateSchema();
        debug(new StringBuffer().append("freeID is ").append(this.freeID).toString());
        this.compr.close(new StringBuffer().append(this.indexDir).append("OFFSETS").toString());
        debug("inverting index");
        DocumentLists.invert(this.indexDir);
        this._schema.save();
    }

    @Override // javax.help.search.IndexBuilder
    public void openDocument(String str) throws Exception {
        if (this.currentDocID != 0) {
            throw new Exception("document already open");
        }
        this.currentDocID = intern(str);
    }

    @Override // javax.help.search.IndexBuilder
    public void closeDocument() throws Exception {
        if (this.currentDocID == 0) {
            throw new Exception("no document open");
        }
        this.compr.compress(this.currentDocID, this._title, this.locations, this.free, null, 0);
        this.free = 0;
        this.currentDocID = 0;
        this._title = 0;
    }

    @Override // javax.help.search.IndexBuilder
    public void storeLocation(String str, int i) throws Exception {
        if (isStopWord(str)) {
            return;
        }
        if (this.free == this.size) {
            int i2 = this.size * 2;
            this.size = i2;
            ConceptLocation[] conceptLocationArr = new ConceptLocation[i2];
            System.arraycopy(this.locations, 0, conceptLocationArr, 0, this.free);
            this.locations = conceptLocationArr;
        }
        ConceptLocation[] conceptLocationArr2 = this.locations;
        int i3 = this.free;
        this.free = i3 + 1;
        conceptLocationArr2[i3] = new ConceptLocation(intern(str), i, i + str.length());
    }

    @Override // javax.help.search.IndexBuilder
    public void storeTitle(String str) throws Exception {
        this._title = intern(str);
    }

    private int intern(String str) throws Exception {
        Integer num = (Integer) this.cache.get(str);
        if (num != null) {
            return num.intValue();
        }
        int iFetch = this.dict.fetch(str);
        if (iFetch == 0) {
            FullBtreeDict fullBtreeDict = this.dict;
            int i = this.freeID;
            this.freeID = i + 1;
            iFetch = i;
            fullBtreeDict.store(str, i);
        }
        this.cache.put(str, new Integer(iFetch));
        return iFetch;
    }

    private void removeExistingFiles(String str) {
        File file = new File(str);
        try {
            if (file.exists()) {
                try {
                    new File(file, "TMAP").delete();
                } catch (NullPointerException e) {
                }
                try {
                    new File(file, "DOCS").delete();
                } catch (NullPointerException e2) {
                }
                try {
                    new File(file, "DOCS.TAB").delete();
                } catch (NullPointerException e3) {
                }
                try {
                    new File(file, "OFFSETS").delete();
                } catch (NullPointerException e4) {
                }
                try {
                    new File(file, "POSITIONS").delete();
                } catch (NullPointerException e5) {
                }
                try {
                    new File(file, "SCHEMA").delete();
                } catch (NullPointerException e6) {
                }
            }
        } catch (SecurityException e7) {
        }
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("DefaultIndexBuilder: ").append(str).toString());
        }
    }
}
