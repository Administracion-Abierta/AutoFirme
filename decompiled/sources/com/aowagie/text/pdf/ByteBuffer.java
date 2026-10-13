package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import com.ctc.wstx.api.ReaderConfig;
import com.sun.jna.platform.win32.WinError;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/ByteBuffer.class */
public class ByteBuffer extends OutputStream {
    private int count;
    private byte[] buf;
    private static final byte ZERO = 48;
    private static int byteCacheSize = 0;
    private static byte[][] byteCache = new byte[byteCacheSize][];
    private static final char[] chars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final byte[] bytes = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102};
    private static boolean HIGH_PRECISION = false;
    private static final DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);

    public ByteBuffer() {
        this(128);
    }

    public ByteBuffer(int size) {
        this.buf = new byte[size < 1 ? 128 : size];
    }

    private static byte[] convertToBytes(int i) {
        int size = (int) Math.floor(Math.log(i) / Math.log(10.0d));
        if (i % 100 != 0) {
            size += 2;
        }
        if (i % 10 != 0) {
            size++;
        }
        if (i < 100) {
            size++;
            if (i < 10) {
                size++;
            }
        }
        int size2 = size - 1;
        byte[] cache = new byte[size2];
        int size3 = size2 - 1;
        if (i < 100) {
            cache[0] = 48;
        }
        if (i % 10 != 0) {
            size3--;
            cache[size3] = bytes[i % 10];
        }
        if (i % 100 != 0) {
            int i2 = size3;
            int size4 = size3 - 1;
            cache[i2] = bytes[(i / 10) % 10];
            int i3 = size4 - 1;
            cache[size4] = 46;
        }
        int size5 = ((int) Math.floor(Math.log(i) / Math.log(10.0d))) - 1;
        for (int add = 0; add < size5; add++) {
            cache[add] = bytes[(i / ((int) Math.pow(10.0d, (size5 - add) + 1))) % 10];
        }
        return cache;
    }

    public ByteBuffer append_i(int b) {
        int newcount = this.count + 1;
        if (newcount > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        this.buf[this.count] = (byte) b;
        this.count = newcount;
        return this;
    }

    ByteBuffer append(byte[] b, int off, int len) {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0 || len == 0) {
            return this;
        }
        int newcount = this.count + len;
        if (newcount > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count = newcount;
        return this;
    }

    ByteBuffer append(byte[] b) {
        return append(b, 0, b.length);
    }

    ByteBuffer append(String str) {
        if (str != null) {
            return append(DocWriter.getISOBytes(str));
        }
        return this;
    }

    ByteBuffer append(char c) {
        return append_i(c);
    }

    ByteBuffer append(ByteBuffer buf) {
        return append(buf.buf, 0, buf.count);
    }

    ByteBuffer append(int i) {
        return append(i);
    }

    public ByteBuffer append(byte b) {
        return append_i(b);
    }

    ByteBuffer appendHex(byte b) {
        append(bytes[(b >> 4) & 15]);
        return append(bytes[b & 15]);
    }

    ByteBuffer append(float i) {
        return append(i);
    }

    private ByteBuffer append(double d) {
        append(formatDouble(d, this));
        return this;
    }

    static String formatDouble(double d) {
        return formatDouble(d, null);
    }

    private static String formatDouble(double d, ByteBuffer buf) {
        if (HIGH_PRECISION) {
            DecimalFormat dn = new DecimalFormat("0.######", dfs);
            String sform = dn.format(d);
            if (buf == null) {
                return sform;
            }
            buf.append(sform);
            return null;
        }
        boolean negative = false;
        if (Math.abs(d) < 1.5E-5d) {
            if (buf != null) {
                buf.append((byte) 48);
                return null;
            }
            return "0";
        }
        if (d < 0.0d) {
            negative = true;
            d = -d;
        }
        if (d < 1.0d) {
            double d2 = d + 5.0E-6d;
            if (d2 >= 1.0d) {
                if (negative) {
                    if (buf != null) {
                        buf.append((byte) 45);
                        buf.append((byte) 49);
                        return null;
                    }
                    return "-1";
                }
                if (buf != null) {
                    buf.append((byte) 49);
                    return null;
                }
                return "1";
            }
            if (buf != null) {
                int v = (int) (d2 * 100000.0d);
                if (negative) {
                    buf.append((byte) 45);
                }
                buf.append((byte) 48);
                buf.append((byte) 46);
                buf.append((byte) ((v / WinError.WSABASEERR) + 48));
                if (v % WinError.WSABASEERR != 0) {
                    buf.append((byte) (((v / 1000) % 10) + 48));
                    if (v % 1000 != 0) {
                        buf.append((byte) (((v / 100) % 10) + 48));
                        if (v % 100 != 0) {
                            buf.append((byte) (((v / 10) % 10) + 48));
                            if (v % 10 != 0) {
                                buf.append((byte) ((v % 10) + 48));
                                return null;
                            }
                            return null;
                        }
                        return null;
                    }
                    return null;
                }
                return null;
            }
            int v2 = (int) (d2 * ((double) ReaderConfig.DEFAULT_MAX_ENTITY_COUNT));
            StringBuffer res = new StringBuffer();
            if (negative) {
                res.append('-');
            }
            res.append("0.");
            for (int x = 100000; v2 < x / 10; x /= 10) {
                res.append('0');
            }
            res.append(v2);
            int cut = res.length() - 1;
            while (res.charAt(cut) == '0') {
                cut--;
            }
            res.setLength(cut + 1);
            return res.toString();
        }
        if (d <= 32767.0d) {
            int v3 = (int) ((d + 0.005d) * 100.0d);
            if (v3 < byteCacheSize && byteCache[v3] != null) {
                if (buf != null) {
                    if (negative) {
                        buf.append((byte) 45);
                    }
                    buf.append(byteCache[v3]);
                    return null;
                }
                String tmp = PdfEncodings.convertToString(byteCache[v3], null);
                if (negative) {
                    tmp = "-" + tmp;
                }
                return tmp;
            }
            if (buf != null) {
                if (v3 < byteCacheSize) {
                    int size = 0;
                    if (v3 >= 1000000) {
                        size = 0 + 5;
                    } else if (v3 >= 100000) {
                        size = 0 + 4;
                    } else if (v3 >= 10000) {
                        size = 0 + 3;
                    } else if (v3 >= 1000) {
                        size = 0 + 2;
                    } else if (v3 >= 100) {
                        size = 0 + 1;
                    }
                    if (v3 % 100 != 0) {
                        size += 2;
                    }
                    if (v3 % 10 != 0) {
                        size++;
                    }
                    byte[] cache = new byte[size];
                    int add = 0;
                    if (v3 >= 1000000) {
                        add = 0 + 1;
                        cache[0] = bytes[v3 / 1000000];
                    }
                    if (v3 >= 100000) {
                        int i = add;
                        add++;
                        cache[i] = bytes[(v3 / ReaderConfig.DEFAULT_MAX_ENTITY_COUNT) % 10];
                    }
                    if (v3 >= 10000) {
                        int i2 = add;
                        add++;
                        cache[i2] = bytes[(v3 / WinError.WSABASEERR) % 10];
                    }
                    if (v3 >= 1000) {
                        int i3 = add;
                        add++;
                        cache[i3] = bytes[(v3 / 1000) % 10];
                    }
                    if (v3 >= 100) {
                        int i4 = add;
                        add++;
                        cache[i4] = bytes[(v3 / 100) % 10];
                    }
                    if (v3 % 100 != 0) {
                        int i5 = add;
                        int add2 = add + 1;
                        cache[i5] = 46;
                        int add3 = add2 + 1;
                        cache[add2] = bytes[(v3 / 10) % 10];
                        if (v3 % 10 != 0) {
                            int i6 = add3 + 1;
                            cache[add3] = bytes[v3 % 10];
                        }
                    }
                    byteCache[v3] = cache;
                }
                if (negative) {
                    buf.append((byte) 45);
                }
                if (v3 >= 1000000) {
                    buf.append(bytes[v3 / 1000000]);
                }
                if (v3 >= 100000) {
                    buf.append(bytes[(v3 / ReaderConfig.DEFAULT_MAX_ENTITY_COUNT) % 10]);
                }
                if (v3 >= 10000) {
                    buf.append(bytes[(v3 / WinError.WSABASEERR) % 10]);
                }
                if (v3 >= 1000) {
                    buf.append(bytes[(v3 / 1000) % 10]);
                }
                if (v3 >= 100) {
                    buf.append(bytes[(v3 / 100) % 10]);
                }
                if (v3 % 100 != 0) {
                    buf.append((byte) 46);
                    buf.append(bytes[(v3 / 10) % 10]);
                    if (v3 % 10 != 0) {
                        buf.append(bytes[v3 % 10]);
                        return null;
                    }
                    return null;
                }
                return null;
            }
            StringBuffer res2 = new StringBuffer();
            if (negative) {
                res2.append('-');
            }
            if (v3 >= 1000000) {
                res2.append(chars[v3 / 1000000]);
            }
            if (v3 >= 100000) {
                res2.append(chars[(v3 / ReaderConfig.DEFAULT_MAX_ENTITY_COUNT) % 10]);
            }
            if (v3 >= 10000) {
                res2.append(chars[(v3 / WinError.WSABASEERR) % 10]);
            }
            if (v3 >= 1000) {
                res2.append(chars[(v3 / 1000) % 10]);
            }
            if (v3 >= 100) {
                res2.append(chars[(v3 / 100) % 10]);
            }
            if (v3 % 100 != 0) {
                res2.append('.');
                res2.append(chars[(v3 / 10) % 10]);
                if (v3 % 10 != 0) {
                    res2.append(chars[v3 % 10]);
                }
            }
            return res2.toString();
        }
        StringBuffer res3 = new StringBuffer();
        if (negative) {
            res3.append('-');
        }
        return res3.append((long) (d + 0.5d)).toString();
    }

    void reset() {
        this.count = 0;
    }

    public byte[] toByteArray() {
        byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }

    int size() {
        return this.count;
    }

    public void setSize(int size) {
        if (size > this.count || size < 0) {
            throw new IndexOutOfBoundsException("The new size must be positive and <= of the current size");
        }
        this.count = size;
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }

    @Override // java.io.OutputStream
    public void write(int b) throws IOException {
        append((byte) b);
    }

    @Override // java.io.OutputStream
    public void write(byte[] b, int off, int len) {
        append(b, off, len);
    }

    public byte[] getBuffer() {
        return this.buf;
    }
}
