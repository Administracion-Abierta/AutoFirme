package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/VersionUtil.class */
public class VersionUtil {
    public static VerRsrc.VS_FIXEDFILEINFO getFileVersionInfo(String filePath) {
        IntByReference dwDummy = new IntByReference();
        int versionLength = Version.INSTANCE.GetFileVersionInfoSize(filePath, dwDummy);
        if (versionLength == 0) {
            throw new Win32Exception(Native.getLastError());
        }
        Pointer lpData = new Memory(versionLength);
        PointerByReference lplpBuffer = new PointerByReference();
        if (!Version.INSTANCE.GetFileVersionInfo(filePath, 0, versionLength, lpData)) {
            throw new Win32Exception(Native.getLastError());
        }
        IntByReference puLen = new IntByReference();
        if (!Version.INSTANCE.VerQueryValue(lpData, "\\", lplpBuffer, puLen)) {
            throw new UnsupportedOperationException("Unable to extract version info from the file: \"" + filePath + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }
        VerRsrc.VS_FIXEDFILEINFO fileInfo = new VerRsrc.VS_FIXEDFILEINFO(lplpBuffer.getValue());
        fileInfo.read();
        return fileInfo;
    }
}
