package org.apache.fontbox.ttf;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/KerningTable.class */
public class KerningTable extends TTFTable {
    private static final Log LOG = LogFactory.getLog(KerningTable.class);
    public static final String TAG = "kern";
    private KerningSubtable[] subtables;

    KerningTable(TrueTypeFont font) {
        super(font);
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        int version = data.readUnsignedShort();
        if (version != 0) {
            version = (version << 16) | data.readUnsignedShort();
        }
        int numSubtables = 0;
        if (version == 0) {
            numSubtables = data.readUnsignedShort();
        } else if (version == 1) {
            numSubtables = (int) data.readUnsignedInt();
        } else {
            LOG.debug("Skipped kerning table due to an unsupported kerning table version: " + version);
        }
        if (numSubtables > 0) {
            this.subtables = new KerningSubtable[numSubtables];
            for (int i = 0; i < numSubtables; i++) {
                KerningSubtable subtable = new KerningSubtable();
                subtable.read(data, version);
                this.subtables[i] = subtable;
            }
        }
        this.initialized = true;
    }

    public KerningSubtable getHorizontalKerningSubtable() {
        return getHorizontalKerningSubtable(false);
    }

    public KerningSubtable getHorizontalKerningSubtable(boolean cross) {
        if (this.subtables != null) {
            for (KerningSubtable s : this.subtables) {
                if (s.isHorizontalKerning(cross)) {
                    return s;
                }
            }
            return null;
        }
        return null;
    }
}
