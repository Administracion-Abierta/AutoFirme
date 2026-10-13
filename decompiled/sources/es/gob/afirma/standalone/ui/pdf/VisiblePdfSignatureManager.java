package es.gob.afirma.standalone.ui.pdf;

import es.gob.afirma.core.AOCancelledOperationException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.signers.pades.AOPDFSigner;
import es.gob.afirma.signers.pades.PdfUtil;
import es.gob.afirma.signers.pades.common.PdfExtraParams;
import es.gob.afirma.standalone.ui.SignOperationConfig;
import es.gob.afirma.standalone.ui.SignatureExecutor;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/VisiblePdfSignatureManager.class */
public final class VisiblePdfSignatureManager {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public static void getVisibleSignatureParams(List<SignOperationConfig> list, SignatureExecutor signatureExecutor, boolean z, boolean z2, Frame frame) throws AOCancelledOperationException, IOException {
        SignOperationConfig signOperationConfig = null;
        for (SignOperationConfig signOperationConfig2 : list) {
            if (signOperationConfig2.getSigner() instanceof AOPDFSigner) {
                signOperationConfig = signOperationConfig2;
                break;
            }
        }
        byte[] bArrLoadData = null;
        if (signOperationConfig != null) {
            bArrLoadData = loadData(signOperationConfig.getDataFile());
        }
        List<PdfUtil.SignatureField> pdfEmptySignatureFields = PdfUtil.getPdfEmptySignatureFields(bArrLoadData);
        if (!pdfEmptySignatureFields.isEmpty()) {
            selectEmptySignatureField(bArrLoadData, list, pdfEmptySignatureFields, signatureExecutor, z, z2, frame);
        } else if (z || z2) {
            showVisibleSignatureDialog(bArrLoadData, list, signatureExecutor, z, z2, frame);
        } else {
            LOGGER.info(" =========----- Iniciamos el proceso de firma visible del PDF, pero sin haber iniciado el dialogo");
            signatureExecutor.initSignTask(list);
        }
    }

    private static void selectEmptySignatureField(byte[] bArr, List<SignOperationConfig> list, List<PdfUtil.SignatureField> list2, SignatureExecutor signatureExecutor, boolean z, boolean z2, Frame frame) throws AOCancelledOperationException, IOException {
        PdfUtil.SignatureField signatureFieldSelectField = PdfEmptySignatureFieldsChooserDialog.selectField(list2);
        if (signatureFieldSelectField == null) {
            if (z || z2) {
                showVisibleSignatureDialog(bArr, list, signatureExecutor, z, z2, frame);
                return;
            } else {
                LOGGER.info(" =========----- Iniciamos el proceso de firma en un campo de firma prexistente");
                signatureExecutor.initSignTask(list);
                return;
            }
        }
        Iterator<SignOperationConfig> it = list.iterator();
        while (it.hasNext()) {
            it.next().addExtraParam(PdfExtraParams.SIGNATURE_FIELD, signatureFieldSelectField.getName());
        }
        if (z2) {
            showVisibleSignatureDialog(bArr, list, signatureExecutor, false, z2, frame);
        } else {
            signatureExecutor.initSignTask(list);
        }
    }

    private static void showVisibleSignatureDialog(byte[] bArr, List<SignOperationConfig> list, SignatureExecutor signatureExecutor, boolean z, boolean z2, Frame frame) throws IOException {
        try {
            SignPdfDialog.getVisibleSignatureExtraParams(list.get(0).getSigner().isSign(bArr), list.size() > 1, bArr, frame, z, true, z2, new SignPdfListener(list, signatureExecutor));
        } catch (Exception e) {
            LOGGER.severe("No se han podido cargar los parametros de la firma visible: " + e);
            throw new IOException("No se han podido cargar los parametros de la firma visible", e);
        }
    }

    private static byte[] loadData(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th = null;
        try {
            byte[] dataFromInputStream = AOUtil.getDataFromInputStream(fileInputStream);
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            return dataFromInputStream;
        } catch (Throwable th3) {
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th3;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/VisiblePdfSignatureManager$SignPdfListener.class */
    public static final class SignPdfListener implements SignPdfDialog.SignPdfDialogListener {
        private final List<SignOperationConfig> signConfigs;
        private final SignatureExecutor executor;

        public SignPdfListener(List<SignOperationConfig> list, SignatureExecutor signatureExecutor) {
            this.signConfigs = list;
            this.executor = signatureExecutor;
        }

        @Override // es.gob.afirma.standalone.ui.pdf.SignPdfDialog.SignPdfDialogListener
        public void propertiesCreated(Properties properties) {
            if (properties != null && !properties.isEmpty()) {
                for (SignOperationConfig signOperationConfig : this.signConfigs) {
                    if (signOperationConfig.getSigner() instanceof AOPDFSigner) {
                        signOperationConfig.addExtraParams(properties);
                    }
                }
            }
            VisiblePdfSignatureManager.LOGGER.info(" =========----- Iniciamos el proceso de firma visible PDF con los parametros ya establecidos");
            this.executor.initSignTask(this.signConfigs);
        }
    }
}
