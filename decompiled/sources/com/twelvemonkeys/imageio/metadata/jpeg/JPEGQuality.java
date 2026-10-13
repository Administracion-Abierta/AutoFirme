package com.twelvemonkeys.imageio.metadata.jpeg;

import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import com.twelvemonkeys.lang.Validate;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.plugins.jpeg.JPEGQTable;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/metadata/jpeg/JPEGQuality.class */
public final class JPEGQuality {
    static final int NUM_QUANT_TABLES = 4;
    static final int DCT_SIZE_2 = 64;

    public static float getJPEGQuality(List<JPEGSegment> list) throws IOException {
        int jPEGQuality = getJPEGQuality(getQuantizationTables(list));
        return jPEGQuality >= 0 ? jPEGQuality / 100.0f : jPEGQuality;
    }

    public static float getJPEGQuality(ImageInputStream imageInputStream) throws IOException {
        return getJPEGQuality(JPEGSegmentUtil.readSegments(imageInputStream, JPEG.DQT, null));
    }

    private static int getJPEGQuality(int[][] iArr) throws IOException {
        int[] iArr2;
        int[] iArr3;
        int i;
        int i2 = 0;
        for (int i3 = 0; i3 < 4; i3++) {
            if (iArr[i3] != null) {
                for (int i4 = 0; i4 < 64; i4++) {
                    i2 += iArr[i3][i4];
                }
            }
        }
        if (iArr[0] != null && iArr[1] != null) {
            iArr2 = new int[]{WinError.ERROR_KEY_HAS_CHILDREN, 1015, 932, 848, WinError.ERROR_DISK_REPAIR_DISABLED, WinError.ERROR_ABANDONED_WAIT_0, WinError.ERROR_SEGMENT_NOTIFICATION, WinError.ERROR_MEDIA_CHECK, 660, 645, 632, 623, 613, 607, 600, WinError.ERROR_DRIVER_CANCEL_TIMEOUT, WinError.ERROR_CANNOT_LOAD_REGISTRY_FILE, WinError.ERROR_BIOS_FAILED_TO_CONNECT_INTERRUPT, WinError.ERROR_DOMAIN_CTRLR_CONFIG_ERROR, WinError.ERROR_IO_PRIVILEGE_FAILED, WinError.ERROR_CANT_TERMINATE_SELF, 542, TIFF.TAG_YCBCR_COEFFICIENTS, TIFF.TAG_JPEG_INTERCHANGE_FORMAT_LENGTH, 494, 474, 457, 439, 424, 410, 397, 386, 373, 364, WinError.ERROR_FAIL_SHUTDOWN, 341, TIFF.TAG_NUMBER_OF_INKS, TIFF.TAG_TILE_OFFSETS, 317, 309, WinError.ERROR_PARTIAL_COPY, 294, TIFF.TAG_Y_POSITION, TIFF.TAG_STRIP_BYTE_COUNTS, 274, WinError.ERROR_DIRECTORY, TIFF.TAG_PHOTOMETRIC_INTERPRETATION, 257, 251, 247, 243, 237, WinError.ERROR_NO_DATA, 227, WinError.ERROR_BAD_FILE_TYPE, WinError.ERROR_EXE_CANNOT_MODIFY_SIGNED_BINARY, 213, WinError.ERROR_RING2_STACK_IN_USE, 202, 198, 192, 188, 183, 177, 173, 168, 163, 157, 153, 148, 143, 139, 132, 128, 125, 119, 115, 108, 104, 99, 94, 90, 84, 79, 74, 70, 64, 59, 55, 49, 45, 40, 34, 30, 25, 20, 15, 11, 6, 4, 0};
            iArr3 = new int[]{32640, 32635, 32266, 31495, 30665, 29804, 29146, 28599, 28104, 27670, 27225, 26725, 26210, 25716, 25240, 24789, 24373, 23946, 23572, 22846, 21801, 20842, 19949, 19121, 18386, 17651, 16998, 16349, 15800, 15247, 14783, 14321, WinError.ERROR_IPSEC_IKE_OUT_OF_MEMORY, 13535, 13081, 12702, 12423, 12056, 11779, 11513, 11135, 10955, 10676, 10392, 10208, 9928, 9747, WinError.DNS_ERROR_CANNOT_FIND_ROOT_HINTS, 9369, 9193, WinError.DNS_ERROR_RCODE_BADKEY, 8822, 8639, WinError.ERROR_DS_DRA_NAME_COLLISION, 8270, 8084, 7896, 7710, 7527, 7347, 7156, 6977, 6788, WinError.ERROR_LOG_RESTART_INVALID, 6422, 6236, 6054, 5867, 5684, 5495, 5305, 5128, 4945, 4751, 4638, 4442, 4248, 4065, 3888, 3698, 3509, 3326, 3139, 2957, 2775, 2586, LMErr.NERR_LocalDrive, LMErr.NERR_LogonDomainExists, 2037, 1846, 1666, 1483, WinError.ERROR_INCOMPATIBLE_SERVICE_PRIVILEGE, WinError.ERROR_UNABLE_TO_UNLOAD_MEDIA, 927, WinError.ERROR_ABANDONED_WAIT_0, 554, 375, 201, 128, 0};
            i = iArr[0][2] + iArr[0][53] + iArr[1][0] + iArr[1][63];
        } else if (iArr[0] != null) {
            iArr2 = new int[]{510, 505, 422, 380, 355, TIFF.TAG_EXTRA_SAMPLES, 326, 318, 311, TIFF.TAG_SOFTWARE, 300, TIFF.TAG_PAGE_NUMBER, TIFF.TAG_GROUP4OPTIONS, 291, 288, TIFF.TAG_X_POSITION, TIFF.TAG_PLANAR_CONFIGURATION, TIFF.TAG_Y_RESOLUTION, 281, 280, TIFF.TAG_STRIP_BYTE_COUNTS, 278, 277, TIFF.TAG_STRIP_OFFSETS, TIFF.TAG_PHOTOMETRIC_INTERPRETATION, 251, 243, WinError.ERROR_PIPE_NOT_CONNECTED, WinError.ERROR_VIRUS_INFECTED, WinError.ERROR_EXE_CANNOT_MODIFY_STRONG_SIGNED_BINARY, 211, 205, 198, 193, 186, 181, 177, 172, 168, 164, 158, 156, 152, 148, 145, 142, 139, 136, 133, 131, 129, 126, 123, 120, 118, 115, 113, 110, 107, 105, 102, 100, 97, 94, 92, 89, 87, 83, 81, 79, 76, 74, 70, 68, 66, 63, 61, 57, 55, 52, 50, 48, 44, 42, 39, 37, 34, 31, 29, 26, 24, 21, 18, 16, 13, 11, 8, 6, 3, 2, 0};
            iArr3 = new int[]{16320, 16315, 15946, 15277, 14655, WinError.ERROR_SXS_XML_E_MISSINGEQUALS, 13623, 13230, 12859, 12560, 12240, 11861, 11456, 11081, 10714, 10360, 10027, 9679, 9368, 9056, 8680, WinError.ERROR_DS_CANT_MIX_MASTER_AND_REPS, 7995, 7668, 7376, 7084, WinError.ERROR_FILE_IDENTITY_NOT_PERSISTENT, 6562, 6345, 6125, 5939, 5756, 5571, 5421, 5240, WinError.ERROR_QUORUM_DISK_NOT_FOUND, 4976, 4829, 4719, 4616, 4463, WinError.ERROR_REPARSE_TAG_INVALID, 4280, 4166, 4092, 3980, 3909, 3835, 3755, 3688, 3621, 3541, 3467, 3396, 3323, 3247, 3170, 3096, 3021, 2952, 2874, 2804, 2727, 2657, 2583, LMErr.NERR_TooManyImageParams, 2437, LMErr.NERR_TooManyEntries, 2290, LMErr.NERR_LogonNoUserPath, LMErr.NERR_NetworkError, 2068, 1996, WinError.RPC_S_INVALID_ASYNC_CALL, 1858, WinError.RPC_X_SS_CHAR_TRANS_OPEN_FAIL, 1692, WinError.ERROR_INSTALL_PACKAGE_INVALID, WinError.ERROR_THREAD_ALREADY_IN_TASK, 1477, WinError.ERROR_TIME_SKEW, WinError.ERROR_LOGON_FAILURE, WinError.ERROR_ONLY_IF_CONNECTED, WinError.ERROR_JOURNAL_NOT_ACTIVE, WinError.ERROR_UNABLE_TO_UNLOAD_MEDIA, 1031, 961, 884, 814, WinError.ERROR_ABANDONED_WAIT_63, 667, 592, 518, 441, 369, TIFF.TAG_GROUP3OPTIONS, WinError.ERROR_CHECKOUT_REQUIRED, 151, 86, 64, 0};
            i = iArr[0][2] + iArr[0][53];
        } else {
            return -1;
        }
        for (int i5 = 0; i5 < 100; i5++) {
            if (i >= iArr2[i5] || i2 >= iArr3[i5]) {
                if ((i <= iArr2[i5] && i2 <= iArr3[i5]) || i5 >= 50) {
                    return i5 + 1;
                }
                return -1;
            }
        }
        return -1;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    public static JPEGQTable[] getQTables(List<JPEGSegment> list) throws IOException, IIOException {
        int[][] quantizationTables = getQuantizationTables(list);
        ArrayList arrayList = new ArrayList();
        for (int[] iArr : quantizationTables) {
            if (iArr != null) {
                arrayList.add(new JPEGQTable(iArr));
            }
        }
        return (JPEGQTable[]) arrayList.toArray(new JPEGQTable[arrayList.size()]);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.imageio.IIOException */
    private static int[][] getQuantizationTables(List<JPEGSegment> list) throws IOException, IIOException {
        Validate.notNull(list, "segments");
        int[][] iArr = new int[4][];
        for (JPEGSegment jPEGSegment : list) {
            if (jPEGSegment.marker() == 65499) {
                DataInputStream dataInputStream = new DataInputStream(jPEGSegment.data());
                int length = 0;
                while (length < jPEGSegment.length()) {
                    int i = dataInputStream.read();
                    int i2 = length + 1;
                    int i3 = i & 15;
                    int i4 = i >> 4;
                    if (i3 >= 4) {
                        throw new IIOException("Bad DQT table index: " + i3);
                    }
                    if (iArr[i3] != null) {
                        throw new IIOException("Duplicate DQT table index: " + i3);
                    }
                    if (i4 < 0 || i4 > 1) {
                        throw new IIOException("Bad DQT bit info: " + i4);
                    }
                    byte[] bArr = new byte[64 * (i4 + 1)];
                    dataInputStream.readFully(bArr);
                    length = i2 + bArr.length;
                    iArr[i3] = new int[64];
                    switch (i4) {
                        case 0:
                            int length2 = bArr.length;
                            for (int i5 = 0; i5 < length2; i5++) {
                                iArr[i3][i5] = (short) (bArr[i5] & 255);
                            }
                            break;
                        case 1:
                            int length3 = bArr.length;
                            for (int i6 = 0; i6 < length3; i6 += 2) {
                                iArr[i3][i6 / 2] = (short) (((bArr[i6] & 255) << 8) | (bArr[i6 + 1] & 255));
                            }
                            break;
                    }
                }
            }
        }
        return iArr;
    }

    public static void main(String[] strArr) throws IOException {
        for (String str : strArr) {
            float jPEGQuality = getJPEGQuality(ImageIO.createImageInputStream(new File(str)));
            System.err.println(str + " quality: " + jPEGQuality + "/" + ((int) (jPEGQuality * 100.0f)));
        }
    }
}
