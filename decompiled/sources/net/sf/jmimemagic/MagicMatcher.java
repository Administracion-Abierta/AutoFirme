package net.sf.jmimemagic;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;

/* JADX INFO: loaded from: autofirma.jar:net/sf/jmimemagic/MagicMatcher.class */
public final class MagicMatcher implements Cloneable, Serializable {
    private static final long serialVersionUID = -1109707428218614961L;
    private final ArrayList<MagicMatcher> subMatchers = new ArrayList<>(0);
    private MagicMatch match = null;

    static {
        if (System.getProperty("os.name").contains("inux") && "Dalvik".equals(System.getProperty("java.vm.name"))) {
            System.setProperty("org.xml.sax.driver", "org.xmlpull.v1.sax2.Driver");
        }
    }

    public void setMatch(MagicMatch match) {
        this.match = match;
    }

    public MagicMatch getMatch() {
        return this.match;
    }

    public boolean isValid() {
        if (this.match == null || this.match.getTest() == null) {
            return false;
        }
        String type = new String(this.match.getTest().array()).trim();
        char comparator = this.match.getComparator();
        String description = this.match.getDescription();
        String test = new String(this.match.getTest().array()).trim();
        if (!type.equals("") && comparator != 0) {
            if ((comparator == '=' || comparator == '!' || comparator == '>' || comparator == '<') && description != null && !description.equals("") && !test.equals("")) {
                return true;
            }
            return false;
        }
        return false;
    }

    public void addSubMatcher(MagicMatcher m) {
        this.subMatchers.add(m);
    }

    public void setSubMatchers(Collection<MagicMatcher> a) {
        this.subMatchers.clear();
        this.subMatchers.addAll(a);
    }

    public Collection<MagicMatcher> getSubMatchers() {
        return this.subMatchers;
    }

    public MagicMatch test(File f, boolean onlyMimeMatch) throws UnsupportedTypeException, IOException {
        int length;
        int offset = this.match.getOffset();
        this.match.getDescription();
        String type = this.match.getType();
        this.match.getMimeType();
        RandomAccessFile file = new RandomAccessFile(f, PDPageLabelRange.STYLE_ROMAN_LOWER);
        Throwable th = null;
        try {
            if (type.equals("byte")) {
                length = 1;
            } else if (type.equals("short") || type.equals("leshort") || type.equals("beshort")) {
                length = 4;
            } else if (type.equals("long") || type.equals("lelong") || type.equals("belong")) {
                length = 8;
            } else if (type.equals("string")) {
                length = this.match.getTest().capacity();
            } else if (type.equals("regex")) {
                length = ((int) file.length()) - offset;
                if (length < 0) {
                    length = 0;
                }
            } else if (type.equals("detector")) {
                length = ((int) file.length()) - offset;
                if (length < 0) {
                    length = 0;
                }
            } else {
                throw new UnsupportedTypeException("unsupported test type '" + type + OperatorName.SHOW_TEXT_LINE);
            }
            if (length <= file.length() - ((long) offset)) {
                byte[] buf = new byte[length];
                file.seek(offset);
                int bytesRead = 0;
                boolean done = false;
                while (!done) {
                    int size = file.read(buf, 0, length - bytesRead);
                    if (size == -1) {
                        throw new IOException("reached end of file before all bytes were read");
                    }
                    bytesRead += size;
                    if (bytesRead == length) {
                        done = true;
                    }
                }
                MagicMatch match1 = null;
                if (testInternal(buf)) {
                    match1 = getMatch();
                    if (!onlyMimeMatch && this.subMatchers != null && this.subMatchers.size() > 0) {
                        for (int i = 0; i < this.subMatchers.size(); i++) {
                            MagicMatcher m = this.subMatchers.get(i);
                            MagicMatch submatch = m.test(f, false);
                            if (submatch != null) {
                                match1.addSubMatch(submatch);
                            }
                        }
                    }
                }
                MagicMatch magicMatch = match1;
                if (file != null) {
                    if (0 != 0) {
                        try {
                            file.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        file.close();
                    }
                }
                return magicMatch;
            }
            if (file != null) {
                if (0 != 0) {
                    try {
                        file.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                } else {
                    file.close();
                }
            }
            return null;
        } catch (Throwable th4) {
            if (file != null) {
                if (0 != 0) {
                    try {
                        file.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    file.close();
                }
            }
            throw th4;
        }
    }

    public MagicMatch test(byte[] data, boolean onlyMimeMatch) throws UnsupportedTypeException, IOException {
        int length;
        int offset = this.match.getOffset();
        this.match.getDescription();
        String type = this.match.getType();
        this.match.getMimeType();
        if (type.equals("byte")) {
            length = 1;
        } else if (type.equals("short") || type.equals("leshort") || type.equals("beshort")) {
            length = 4;
        } else if (type.equals("long") || type.equals("lelong") || type.equals("belong")) {
            length = 8;
        } else if (type.equals("string")) {
            length = this.match.getTest().capacity();
        } else if (type.equals("regex")) {
            length = (data.length - offset) - 1;
            if (length < 0) {
                length = 0;
            }
        } else if (type.equals("detector")) {
            length = (data.length - offset) - 1;
            if (length < 0) {
                length = 0;
            }
        } else {
            throw new UnsupportedTypeException("unsupported test type " + type);
        }
        byte[] buf = new byte[length];
        if (offset + length < data.length) {
            System.arraycopy(data, offset, buf, 0, length);
            MagicMatch match1 = null;
            if (testInternal(buf)) {
                match1 = getMatch();
                if (!onlyMimeMatch && this.subMatchers != null && this.subMatchers.size() > 0) {
                    for (int i = 0; i < this.subMatchers.size(); i++) {
                        MagicMatcher m = this.subMatchers.get(i);
                        MagicMatch submatch = m.test(data, false);
                        if (submatch != null) {
                            match1.addSubMatch(submatch);
                        }
                    }
                }
            }
            return match1;
        }
        return null;
    }

    private boolean testInternal(byte[] data) {
        if (data.length == 0) {
            return false;
        }
        String type = this.match.getType();
        String test = new String(this.match.getTest().array()).trim();
        this.match.getMimeType();
        this.match.getDescription();
        ByteBuffer buffer = ByteBuffer.allocate(data.length);
        if (type != null && test.length() > 0) {
            if (type.equals("string")) {
                return testString(buffer.put(data));
            }
            if (type.equals("byte")) {
                return testByte(buffer.put(data));
            }
            if (type.equals("short")) {
                return testShort(buffer.put(data));
            }
            if (type.equals("leshort")) {
                ByteBuffer buffer2 = buffer.put(data);
                buffer2.order(ByteOrder.LITTLE_ENDIAN);
                return testShort(buffer2);
            }
            if (type.equals("beshort")) {
                ByteBuffer buffer3 = buffer.put(data);
                buffer3.order(ByteOrder.BIG_ENDIAN);
                return testShort(buffer3);
            }
            if (type.equals("long")) {
                return testLong(buffer.put(data));
            }
            if (type.equals("lelong")) {
                ByteBuffer buffer4 = buffer.put(data);
                buffer4.order(ByteOrder.LITTLE_ENDIAN);
                return testLong(buffer4);
            }
            if (type.equals("belong")) {
                ByteBuffer buffer5 = buffer.put(data);
                buffer5.order(ByteOrder.BIG_ENDIAN);
                return testLong(buffer5);
            }
            if (type.equals("regex")) {
                return testRegex(new String(data));
            }
            if (type.equals("detector")) {
                return testDetector(buffer.put(data));
            }
            return false;
        }
        return false;
    }

    private boolean testByte(ByteBuffer data) {
        String test = new String(this.match.getTest().array()).trim();
        char comparator = this.match.getComparator();
        long bitmask = this.match.getBitmask();
        byte b = (byte) (((long) data.get(0)) & bitmask);
        int tst = Integer.decode(test).byteValue();
        byte t = (byte) (tst & 255);
        switch (comparator) {
            case '!':
                return t != b;
            case '<':
                return t < b;
            case '=':
                return t == b;
            case '>':
                return t > b;
            default:
                return false;
        }
    }

    private boolean testString(ByteBuffer data) {
        ByteBuffer test = this.match.getTest();
        char comparator = this.match.getComparator();
        byte[] b = data.array();
        byte[] t = test.array();
        boolean diff = false;
        int i = 0;
        while (i < t.length) {
            if (t[i] == b[i]) {
                i++;
            } else {
                diff = true;
                break;
            }
        }
        switch (comparator) {
            case '!':
                return diff;
            case '<':
                return t[i] < b[i];
            case '=':
                return !diff;
            case '>':
                return t[i] > b[i];
            default:
                return false;
        }
    }

    private boolean testShort(ByteBuffer data) {
        String test = new String(this.match.getTest().array()).trim();
        char comparator = this.match.getComparator();
        long bitmask = this.match.getBitmask();
        short val = (short) (byteArrayToShort(data) & ((short) bitmask));
        try {
            short tst = Integer.decode(test).shortValue();
            switch (comparator) {
                case '!':
                    return val != tst;
                case '<':
                    return val < tst;
                case '=':
                    return val == tst;
                case '>':
                    return val > tst;
                default:
                    return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean testLong(ByteBuffer data) {
        String test = new String(this.match.getTest().array()).trim();
        char comparator = this.match.getComparator();
        long bitmask = this.match.getBitmask();
        long val = byteArrayToLong(data) & bitmask;
        long tst = Long.decode(test).longValue();
        switch (comparator) {
            case '!':
                return val != tst;
            case '<':
                return val < tst;
            case '=':
                return val == tst;
            case '>':
                return val > tst;
            default:
                return false;
        }
    }

    private boolean testRegex(String text) {
        String test = new String(this.match.getTest().array()).trim();
        char comparator = this.match.getComparator();
        Perl5Util utility = new Perl5Util();
        if (comparator == '=') {
            if (utility.match(test, text)) {
                return true;
            }
            return false;
        }
        if (comparator != '!' || utility.match(test, text)) {
            return false;
        }
        return true;
    }

    private boolean testDetector(ByteBuffer data) {
        String detectorClassName = new String(this.match.getTest().array()).trim();
        try {
            Class<?> detectorClass = classForName(detectorClassName);
            Object detectorObject = detectorClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Method processMethod = detectorClass.getDeclaredMethod("process", byte[].class, Integer.TYPE, Integer.TYPE, Long.TYPE, Character.TYPE, String.class, Map.class);
            String[] types = (String[]) processMethod.invoke(detectorObject, data.array(), Integer.valueOf(this.match.getOffset()), Integer.valueOf(this.match.getLength()), Long.valueOf(this.match.getBitmask()), Character.valueOf(this.match.getComparator()), this.match.getMimeType(), this.match.getProperties());
            if (types != null && types.length > 0) {
                this.match.setMimeType(types[0]);
                return true;
            }
            return false;
        } catch (Throwable e) {
            Logger.getLogger("es.gob.afirma").warning(e.toString());
            return false;
        }
    }

    public String[] getDetectorExtensions() {
        String detectorClassName = new String(this.match.getTest().array()).trim();
        try {
            Class<?> detectorClass = classForName(detectorClassName);
            Object detectorObject = detectorClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Method getHandledTypesMethod = detectorClass.getDeclaredMethod("getHandledTypes", new Class[0]);
            return (String[]) getHandledTypesMethod.invoke(detectorObject, new Object[0]);
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("Error cargando e instanciando la clase " + detectorClassName + ": " + e);
            e.printStackTrace();
            return new String[0];
        }
    }

    private static short byteArrayToShort(ByteBuffer data) {
        return data.getShort(0);
    }

    private static long byteArrayToLong(ByteBuffer data) {
        return data.getInt(0);
    }

    protected Object clone() throws CloneNotSupportedException {
        MagicMatcher clone = new MagicMatcher();
        clone.setMatch((MagicMatch) this.match.clone());
        ArrayList<MagicMatcher> sub = new ArrayList<>();
        for (MagicMatcher m : this.subMatchers) {
            sub.add((MagicMatcher) m.clone());
        }
        clone.setSubMatchers(sub);
        return clone;
    }

    static Class<?> classForName(String className) throws ClassNotFoundException {
        return getCleanClassLoader().loadClass(className);
    }

    private static ClassLoader getCleanClassLoader() {
        ClassLoader classLoader = MagicMatcher.class.getClassLoader();
        if ((classLoader instanceof URLClassLoader) && !classLoader.getClass().toString().contains("sun.plugin2.applet.JNLP2ClassLoader")) {
            List<URL> urls = new ArrayList<>();
            for (URL url : ((URLClassLoader) classLoader).getURLs()) {
                if (url.toString().endsWith(".jar")) {
                    urls.add(url);
                }
            }
            classLoader = new URLClassLoader((URL[]) urls.toArray(new URL[0]));
        }
        return classLoader;
    }
}
