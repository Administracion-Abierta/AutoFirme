package org.apache.pdfbox.util.filetypedetector;

import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.pdfbox.util.Charsets;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/filetypedetector/FileTypeDetector.class */
public final class FileTypeDetector {
    private static final ByteTrie<FileType> root = new ByteTrie<>();

    static {
        root.setDefaultValue(FileType.UNKNOWN);
        root.addPath(FileType.JPEG, new byte[]{-1, -40});
        root.addPath(FileType.TIFF, "II".getBytes(Charsets.ISO_8859_1), new byte[]{42, 0});
        root.addPath(FileType.TIFF, "MM".getBytes(Charsets.ISO_8859_1), new byte[]{0, 42});
        root.addPath(FileType.PSD, "8BPS".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.PNG, new byte[]{-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13, 73, 72, 68, 82});
        root.addPath(FileType.BMP, "BM".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.GIF, "GIF87a".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.GIF, "GIF89a".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.ICO, new byte[]{0, 0, 1, 0});
        root.addPath(FileType.PCX, new byte[]{10, 0, 1});
        root.addPath(FileType.PCX, new byte[]{10, 2, 1});
        root.addPath(FileType.PCX, new byte[]{10, 3, 1});
        root.addPath(FileType.PCX, new byte[]{10, 5, 1});
        root.addPath(FileType.RIFF, "RIFF".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.CRW, "II".getBytes(Charsets.ISO_8859_1), new byte[]{26, 0, 0, 0}, "HEAPCCDR".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.CR2, "II".getBytes(Charsets.ISO_8859_1), new byte[]{42, 0, 16, 0, 0, 0, 67, 82});
        root.addPath(FileType.NEF, "MM".getBytes(Charsets.ISO_8859_1), new byte[]{0, 42, 0, 0, 0, -128, 0});
        root.addPath(FileType.ORF, "IIRO".getBytes(Charsets.ISO_8859_1), new byte[]{8, 0});
        root.addPath(FileType.ORF, "IIRS".getBytes(Charsets.ISO_8859_1), new byte[]{8, 0});
        root.addPath(FileType.RAF, "FUJIFILMCCD-RAW".getBytes(Charsets.ISO_8859_1));
        root.addPath(FileType.RW2, "II".getBytes(Charsets.ISO_8859_1), new byte[]{85, 0});
    }

    private FileTypeDetector() {
    }

    public static FileType detectFileType(BufferedInputStream inputStream) throws IOException {
        if (!inputStream.markSupported()) {
            throw new IOException("Stream must support mark/reset");
        }
        int maxByteCount = root.getMaxDepth();
        inputStream.mark(maxByteCount);
        byte[] bytes = new byte[maxByteCount];
        int bytesRead = inputStream.read(bytes);
        if (bytesRead == -1) {
            throw new IOException("Stream ended before file's magic number could be determined.");
        }
        inputStream.reset();
        return root.find(bytes);
    }

    public static FileType detectFileType(byte[] fileBytes) throws IOException {
        return root.find(fileBytes);
    }
}
