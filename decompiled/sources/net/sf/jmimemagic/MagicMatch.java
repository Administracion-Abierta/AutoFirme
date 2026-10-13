package net.sf.jmimemagic;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/MagicMatch.class */
public final class MagicMatch implements Cloneable, Serializable {
    private static final long serialVersionUID = -1070505530849518849L;
    private String mimeType = null;
    private String extension = null;
    private String description = null;
    private byte[] test = null;
    private int offset = 0;
    private int length = 0;
    private String type = "";
    private long bitmask = 4294967295L;
    private char comparator = 0;
    private final ArrayList<MagicMatch> subMatches = new ArrayList<>(0);
    private Map<String, String> properties;

    void setMimeType(String value) {
        this.mimeType = value;
    }

    public String getMimeType() {
        return this.mimeType;
    }

    void setExtension(String value) {
        this.extension = value;
    }

    public String getExtension() {
        return this.extension;
    }

    void setDescription(String value) {
        this.description = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setTest(ByteBuffer value) {
        this.test = value.array();
    }

    public ByteBuffer getTest() {
        return ByteBuffer.wrap(this.test);
    }

    public void setOffset(int value) {
        this.offset = value;
    }

    public int getOffset() {
        return this.offset;
    }

    public void setLength(int value) {
        this.length = value;
    }

    public int getLength() {
        return this.length;
    }

    public void setType(String value) {
        this.type = value;
    }

    public String getType() {
        return this.type;
    }

    public void setBitmask(String value) {
        if (value != null) {
            this.bitmask = Long.decode(value).intValue();
        }
    }

    public long getBitmask() {
        return this.bitmask;
    }

    public void setComparator(String value) {
        this.comparator = value.charAt(0);
    }

    public char getComparator() {
        return this.comparator;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }

    public void addSubMatch(MagicMatch m) {
        this.subMatches.add(m);
    }

    public void setSubMatches(Collection<MagicMatch> a) {
        this.subMatches.clear();
        this.subMatches.addAll(a);
    }

    public Collection<MagicMatch> getSubMatches() {
        return this.subMatches;
    }

    public boolean descriptionMatches(String desc) {
        if (this.description != null && this.description.equals(desc)) {
            return true;
        }
        Collection<MagicMatch> submatches = getSubMatches();
        for (MagicMatch m : submatches) {
            if (m.descriptionMatches(desc)) {
                return true;
            }
        }
        return false;
    }

    public boolean mimeTypeMatches(String desc) {
        if (this.mimeType != null && this.mimeType.equals(desc)) {
            return true;
        }
        Collection<MagicMatch> submatches = getSubMatches();
        for (MagicMatch m : submatches) {
            if (m.mimeTypeMatches(desc)) {
                return true;
            }
        }
        return false;
    }

    protected Object clone() {
        MagicMatch clone = new MagicMatch();
        clone.setBitmask(Long.toString(this.bitmask, 8));
        clone.setComparator("" + this.comparator);
        clone.setDescription(this.description);
        clone.setExtension(this.extension);
        clone.setLength(this.length);
        clone.setMimeType(this.mimeType);
        clone.setOffset(this.offset);
        HashMap<String, String> m = new HashMap<>();
        m.putAll(this.properties);
        clone.setProperties(m);
        ArrayList<MagicMatch> a = new ArrayList<>();
        for (MagicMatch mm : this.subMatches) {
            a.add(mm);
        }
        clone.setSubMatches(a);
        clone.setTest(ByteBuffer.wrap(this.test));
        clone.setType(this.type);
        return clone;
    }
}
