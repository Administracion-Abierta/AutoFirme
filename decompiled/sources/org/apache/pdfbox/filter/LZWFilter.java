package org.apache.pdfbox.filter;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/filter/LZWFilter.class */
public class LZWFilter extends Filter {
    private static final Log LOG = LogFactory.getLog(LZWFilter.class);
    public static final long CLEAR_TABLE = 256;
    public static final long EOD = 257;

    @Override // org.apache.pdfbox.filter.Filter
    public DecodeResult decode(InputStream encoded, OutputStream decoded, COSDictionary parameters, int index) throws IOException {
        COSDictionary decodeParams = getDecodeParams(parameters, index);
        int earlyChange = decodeParams.getInt(COSName.EARLY_CHANGE, 1);
        if (earlyChange != 0 && earlyChange != 1) {
            earlyChange = 1;
        }
        doLZWDecode(encoded, Predictor.wrapPredictor(decoded, decodeParams), earlyChange);
        return new DecodeResult(parameters);
    }

    private void doLZWDecode(InputStream encoded, OutputStream decoded, int earlyChange) throws IOException {
        List<byte[]> codeTable = new ArrayList();
        int chunk = 9;
        MemoryCacheImageInputStream in = new MemoryCacheImageInputStream(encoded);
        long prevCommand = -1;
        while (true) {
            try {
                long nextCommand = in.readBits(chunk);
                if (nextCommand == 257) {
                    break;
                }
                if (nextCommand == 256) {
                    chunk = 9;
                    codeTable = createCodeTable();
                    prevCommand = -1;
                } else {
                    if (nextCommand < codeTable.size()) {
                        byte[] data = codeTable.get((int) nextCommand);
                        byte firstByte = data[0];
                        decoded.write(data);
                        if (prevCommand != -1) {
                            checkIndexBounds(codeTable, prevCommand, in);
                            byte[] data2 = codeTable.get((int) prevCommand);
                            byte[] newData = Arrays.copyOf(data2, data2.length + 1);
                            newData[data2.length] = firstByte;
                            codeTable.add(newData);
                        }
                    } else {
                        checkIndexBounds(codeTable, prevCommand, in);
                        byte[] data3 = codeTable.get((int) prevCommand);
                        byte[] newData2 = Arrays.copyOf(data3, data3.length + 1);
                        newData2[data3.length] = data3[0];
                        decoded.write(newData2);
                        codeTable.add(newData2);
                    }
                    chunk = calculateChunk(codeTable.size(), earlyChange);
                    prevCommand = nextCommand;
                }
            } catch (EOFException e) {
                LOG.warn("Premature EOF in LZW stream, EOD code missing");
            }
        }
        decoded.flush();
    }

    private void checkIndexBounds(List<byte[]> codeTable, long index, MemoryCacheImageInputStream in) throws IOException {
        if (index < 0) {
            throw new IOException("negative array index: " + index + " near offset " + in.getStreamPosition());
        }
        if (index >= codeTable.size()) {
            throw new IOException("array index overflow: " + index + " >= " + codeTable.size() + " near offset " + in.getStreamPosition());
        }
    }

    @Override // org.apache.pdfbox.filter.Filter
    protected void encode(InputStream rawData, OutputStream encoded, COSDictionary parameters) throws IOException {
        int foundCode;
        List<byte[]> codeTable = createCodeTable();
        byte[] inputPattern = null;
        MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(encoded);
        out.writeBits(256L, 9);
        int i = -1;
        while (true) {
            foundCode = i;
            int r = rawData.read();
            if (r == -1) {
                break;
            }
            byte by = (byte) r;
            if (inputPattern == null) {
                inputPattern = new byte[]{by};
                i = by & 255;
            } else {
                inputPattern = Arrays.copyOf(inputPattern, inputPattern.length + 1);
                inputPattern[inputPattern.length - 1] = by;
                int newFoundCode = findPatternCode(codeTable, inputPattern);
                if (newFoundCode == -1) {
                    int chunk = calculateChunk(codeTable.size() - 1, 1);
                    out.writeBits(foundCode, chunk);
                    codeTable.add(inputPattern);
                    if (codeTable.size() == 4096) {
                        out.writeBits(256L, chunk);
                        codeTable = createCodeTable();
                    }
                    inputPattern = new byte[]{by};
                    i = by & 255;
                } else {
                    i = newFoundCode;
                }
            }
        }
        if (foundCode != -1) {
            int chunk2 = calculateChunk(codeTable.size() - 1, 1);
            out.writeBits(foundCode, chunk2);
        }
        int chunk3 = calculateChunk(codeTable.size(), 1);
        out.writeBits(257L, chunk3);
        out.writeBits(0L, 7);
        out.flush();
        out.close();
    }

    private int findPatternCode(List<byte[]> codeTable, byte[] pattern) {
        int foundCode = -1;
        int foundLen = 0;
        for (int i = codeTable.size() - 1; i >= 0; i--) {
            if (i <= 257) {
                if (foundCode != -1) {
                    return foundCode;
                }
                if (pattern.length > 1) {
                    return -1;
                }
            }
            byte[] tryPattern = codeTable.get(i);
            if ((foundCode != -1 || tryPattern.length > foundLen) && Arrays.equals(tryPattern, pattern)) {
                foundCode = i;
                foundLen = tryPattern.length;
            }
        }
        return foundCode;
    }

    private List<byte[]> createCodeTable() {
        List<byte[]> codeTable = new ArrayList<>(4096);
        for (int i = 0; i < 256; i++) {
            codeTable.add(new byte[]{(byte) (i & 255)});
        }
        codeTable.add(null);
        codeTable.add(null);
        return codeTable;
    }

    private int calculateChunk(int tabSize, int earlyChange) {
        if (tabSize >= 2048 - earlyChange) {
            return 12;
        }
        if (tabSize >= 1024 - earlyChange) {
            return 11;
        }
        if (tabSize >= 512 - earlyChange) {
            return 10;
        }
        return 9;
    }
}
