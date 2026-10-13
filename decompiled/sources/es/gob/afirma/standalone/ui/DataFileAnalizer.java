package es.gob.afirma.standalone.ui;

import com.aowagie.text.Chunk;
import es.gob.afirma.core.misc.MimeHelper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/DataFileAnalizer.class */
public class DataFileAnalizer {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final Set<String> EXECUTABLE_EXTENSIONS = new HashSet();

    static {
        EXECUTABLE_EXTENSIONS.add(Chunk.ACTION);
        EXECUTABLE_EXTENSIONS.add("APK");
        EXECUTABLE_EXTENSIONS.add("APP");
        EXECUTABLE_EXTENSIONS.add("BAT");
        EXECUTABLE_EXTENSIONS.add("BIN");
        EXECUTABLE_EXTENSIONS.add("CMD");
        EXECUTABLE_EXTENSIONS.add("COM");
        EXECUTABLE_EXTENSIONS.add("COMMAND");
        EXECUTABLE_EXTENSIONS.add("CPL");
        EXECUTABLE_EXTENSIONS.add("CSH");
        EXECUTABLE_EXTENSIONS.add("EXE");
        EXECUTABLE_EXTENSIONS.add("GADGET");
        EXECUTABLE_EXTENSIONS.add("INF1");
        EXECUTABLE_EXTENSIONS.add("INS");
        EXECUTABLE_EXTENSIONS.add("INX");
        EXECUTABLE_EXTENSIONS.add("IPA");
        EXECUTABLE_EXTENSIONS.add("ISU");
        EXECUTABLE_EXTENSIONS.add("JOB");
        EXECUTABLE_EXTENSIONS.add("JSE");
        EXECUTABLE_EXTENSIONS.add("KSH");
        EXECUTABLE_EXTENSIONS.add("LNK");
        EXECUTABLE_EXTENSIONS.add("MSC");
        EXECUTABLE_EXTENSIONS.add("MSI");
        EXECUTABLE_EXTENSIONS.add("MSP");
        EXECUTABLE_EXTENSIONS.add("MST");
        EXECUTABLE_EXTENSIONS.add("OSX");
        EXECUTABLE_EXTENSIONS.add("OUT");
        EXECUTABLE_EXTENSIONS.add("PAF");
        EXECUTABLE_EXTENSIONS.add("PIF");
        EXECUTABLE_EXTENSIONS.add("PRG");
        EXECUTABLE_EXTENSIONS.add("PS1");
        EXECUTABLE_EXTENSIONS.add("REG");
        EXECUTABLE_EXTENSIONS.add("RGS");
        EXECUTABLE_EXTENSIONS.add("RUN");
        EXECUTABLE_EXTENSIONS.add("SCR");
        EXECUTABLE_EXTENSIONS.add("SCT");
        EXECUTABLE_EXTENSIONS.add("SHB");
        EXECUTABLE_EXTENSIONS.add("SHS");
        EXECUTABLE_EXTENSIONS.add("U3P");
        EXECUTABLE_EXTENSIONS.add("VB");
        EXECUTABLE_EXTENSIONS.add("VBE");
        EXECUTABLE_EXTENSIONS.add("VBS");
        EXECUTABLE_EXTENSIONS.add("VBSCRIPT");
        EXECUTABLE_EXTENSIONS.add("WORKFLOW");
        EXECUTABLE_EXTENSIONS.add("WS");
        EXECUTABLE_EXTENSIONS.add("WSF");
        EXECUTABLE_EXTENSIONS.add("WSH");
    }

    static DataFileInfo analize(byte[] bArr) {
        MimeHelper mimeHelper = new MimeHelper(bArr);
        DataFileInfo dataFileInfo = new DataFileInfo();
        try {
            dataFileInfo.setExtension(mimeHelper.getExtension());
        } catch (IOException e) {
            LOGGER.warning("No se pudo analizar el tipo de datos para identificar la extension: " + e);
        }
        try {
            dataFileInfo.setDescription(mimeHelper.getDescription());
        } catch (IOException e2) {
            LOGGER.warning("No se pudo analizar el tipo de datos para identificar la descripcion: " + e2);
        }
        dataFileInfo.setSize(bArr.length);
        try {
            dataFileInfo.setIcon(FileIconProvider.getIcon(dataFileInfo.getExtension()));
        } catch (IOException e3) {
            LOGGER.warning("No se ha podido cargar el icono de la extension " + dataFileInfo.getExtension() + ": " + e3);
        }
        dataFileInfo.setData(bArr);
        dataFileInfo.setExecutable(isExecutableExtension(dataFileInfo.getExtension()));
        return dataFileInfo;
    }

    static boolean isExecutableExtension(String str) {
        return str != null && EXECUTABLE_EXTENSIONS.contains(str.toUpperCase());
    }
}
