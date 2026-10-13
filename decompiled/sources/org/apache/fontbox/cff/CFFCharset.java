package org.apache.fontbox.cff;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFCharset.class */
public abstract class CFFCharset {
    private final boolean isCIDFont;
    private final Map<Integer, Integer> sidOrCidToGid = new HashMap(250);
    private final Map<Integer, Integer> gidToSid = new HashMap(250);
    private final Map<String, Integer> nameToSid = new HashMap(250);
    private final Map<Integer, Integer> gidToCid = new HashMap();
    private final Map<Integer, String> gidToName = new HashMap(250);

    CFFCharset(boolean isCIDFont) {
        this.isCIDFont = isCIDFont;
    }

    public boolean isCIDFont() {
        return this.isCIDFont;
    }

    public void addSID(int gid, int sid, String name) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        this.sidOrCidToGid.put(Integer.valueOf(sid), Integer.valueOf(gid));
        this.gidToSid.put(Integer.valueOf(gid), Integer.valueOf(sid));
        this.nameToSid.put(name, Integer.valueOf(sid));
        this.gidToName.put(Integer.valueOf(gid), name);
    }

    public void addCID(int gid, int cid) {
        if (!this.isCIDFont) {
            throw new IllegalStateException("Not a CIDFont");
        }
        this.sidOrCidToGid.put(Integer.valueOf(cid), Integer.valueOf(gid));
        this.gidToCid.put(Integer.valueOf(gid), Integer.valueOf(cid));
    }

    int getSIDForGID(int sid) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        Integer gid = this.gidToSid.get(Integer.valueOf(sid));
        if (gid == null) {
            return 0;
        }
        return gid.intValue();
    }

    int getGIDForSID(int sid) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        Integer gid = this.sidOrCidToGid.get(Integer.valueOf(sid));
        if (gid == null) {
            return 0;
        }
        return gid.intValue();
    }

    public int getGIDForCID(int cid) {
        if (!this.isCIDFont) {
            throw new IllegalStateException("Not a CIDFont");
        }
        Integer gid = this.sidOrCidToGid.get(Integer.valueOf(cid));
        if (gid == null) {
            return 0;
        }
        return gid.intValue();
    }

    int getSID(String name) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        Integer sid = this.nameToSid.get(name);
        if (sid == null) {
            return 0;
        }
        return sid.intValue();
    }

    public String getNameForGID(int gid) {
        if (this.isCIDFont) {
            throw new IllegalStateException("Not a Type 1-equivalent font");
        }
        return this.gidToName.get(Integer.valueOf(gid));
    }

    public int getCIDForGID(int gid) {
        if (!this.isCIDFont) {
            throw new IllegalStateException("Not a CIDFont");
        }
        Integer cid = this.gidToCid.get(Integer.valueOf(gid));
        if (cid != null) {
            return cid.intValue();
        }
        return 0;
    }
}
