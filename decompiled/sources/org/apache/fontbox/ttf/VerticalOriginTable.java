package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/VerticalOriginTable.class */
public class VerticalOriginTable extends TTFTable {
    public static final String TAG = "VORG";
    private float version;
    private int defaultVertOriginY;
    private Map<Integer, Integer> origins;

    VerticalOriginTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        this.version = data.read32Fixed();
        this.defaultVertOriginY = data.readSignedShort();
        int numVertOriginYMetrics = data.readUnsignedShort();
        this.origins = new ConcurrentHashMap(numVertOriginYMetrics);
        for (int i = 0; i < numVertOriginYMetrics; i++) {
            int g = data.readUnsignedShort();
            int y = data.readSignedShort();
            this.origins.put(Integer.valueOf(g), Integer.valueOf(y));
        }
        this.initialized = true;
    }

    public float getVersion() {
        return this.version;
    }

    public int getOriginY(int gid) {
        if (this.origins.containsKey(Integer.valueOf(gid))) {
            return this.origins.get(Integer.valueOf(gid)).intValue();
        }
        return this.defaultVertOriginY;
    }
}
