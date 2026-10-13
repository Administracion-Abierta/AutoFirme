package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.standalone.crypto.NativeDataCipher;
import es.gob.afirma.standalone.plugins.DataCipher;
import es.gob.afirma.standalone.plugins.EncryptingException;
import es.gob.afirma.standalone.plugins.PluginControlledException;
import es.gob.afirma.standalone.plugins.SignDataProcessor;
import es.gob.afirma.standalone.plugins.SignOperation;
import es.gob.afirma.standalone.plugins.SignResult;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/NativeSignDataProcessor.class */
public class NativeSignDataProcessor extends SignDataProcessor {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final char RESULT_SEPARATOR = '|';
    private DataCipher cipher;

    public NativeSignDataProcessor(int i) {
        super(i);
        this.cipher = null;
    }

    @Override // es.gob.afirma.standalone.plugins.SignDataProcessor
    public void setCipherKey(byte[] bArr) {
        if (bArr != null) {
            this.cipher = new NativeDataCipher(bArr);
        }
    }

    @Override // es.gob.afirma.standalone.plugins.SignDataProcessor
    public List<SignOperation> preProcess(SignOperation signOperation) {
        return Arrays.asList(signOperation);
    }

    @Override // es.gob.afirma.standalone.plugins.SignDataProcessor
    public StringBuilder postProcess(List<SignResult> list, SignOperation signOperation) throws EncryptingException, PluginControlledException {
        SignResult signResult = list.get(0);
        byte[] signature = signResult.getSignature();
        byte[] certificate = signResult.getCertificate();
        Properties extraData = signResult.getExtraData();
        StringBuilder sb = new StringBuilder();
        if (this.cipher != null) {
            LOGGER.info("Se cifran los datos resultantes con la clave de cifrado proporcionada");
            try {
                sb.append(this.cipher.cipher(certificate));
                sb.append('|');
                sb.append(this.cipher.cipher(signature));
                if (extraData != null && !extraData.isEmpty() && getProtocolVersion() >= 3) {
                    sb.append('|');
                    sb.append(this.cipher.cipher(buildExtraDataResult(extraData)));
                }
            } catch (Exception e) {
                throw new EncryptingException("Error al cifrar los datos a enviar", e);
            }
        } else {
            LOGGER.fine("Se omite el cifrado de los datos resultantes por no haberse proporcionado una clave de cifrado");
            sb.append(Base64.encode(certificate, true));
            sb.append('|');
            sb.append(Base64.encode(signature, true));
            if (extraData != null && !extraData.isEmpty() && getProtocolVersion() >= 3) {
                sb.append('|');
                sb.append(Base64.encode(buildExtraDataResult(extraData), true));
            }
        }
        return sb;
    }

    private static byte[] buildExtraDataResult(Properties properties) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String[] strArr = (String[]) properties.keySet().toArray(new String[0]);
        for (int i = 0; i < strArr.length; i++) {
            sb.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).append(strArr[i]).append("\": \"").append(properties.get(strArr[i])).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
            if (i < strArr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override // es.gob.afirma.standalone.plugins.SignDataProcessor
    public boolean checkTrigger(SignOperation signOperation) {
        return true;
    }

    @Override // es.gob.afirma.standalone.plugins.SignDataProcessor
    public boolean isErrorsAllowed() {
        return false;
    }
}
