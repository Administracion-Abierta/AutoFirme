package es.gob.jmulticard.connection;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.iso7816four.GetResponseApduCommand;
import java.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/AbstractApduConnectionIso7816.class */
public abstract class AbstractApduConnectionIso7816 implements ApduConnection {
    private static final byte TAG_RESPONSE_PENDING = 97;
    private static final byte TAG_RESPONSE_INVALID_LENGTH = 108;

    public abstract int getMaxApduSize();

    protected abstract ResponseApdu internalTransmit(byte[] bArr) throws ApduConnectionException;

    @Override // es.gob.jmulticard.connection.ApduConnection
    public final ResponseApdu transmit(CommandApdu command) throws ApduConnectionException {
        byte[] sendApdu;
        if (command == null) {
            throw new IllegalArgumentException("No se puede transmitir una APDU nula");
        }
        if (command.getBytes().length > getMaxApduSize()) {
            JmcLogger.debug(AbstractApduConnectionIso7816.class.getName(), "transmit", "La APDU supera el tamano maximo, se envolvera en fragmentos mas pequenos");
            int sentLength = 0;
            int totalLength = command.getBytes().length;
            int contentSizeEnvelope = getMaxApduSize() - 5;
            while (totalLength - sentLength > contentSizeEnvelope) {
                byte[] apduChunk = Arrays.copyOfRange(command.getBytes(), sentLength, sentLength + contentSizeEnvelope);
                ResponseApdu response = internalTransmit(HexUtils.concatenateByteArrays(new byte[]{-112, -62, 0, 0, (byte) apduChunk.length}, apduChunk));
                if (!response.isOk()) {
                    return response;
                }
                sentLength += contentSizeEnvelope;
            }
            byte[] apduChunk2 = Arrays.copyOfRange(command.getBytes(), sentLength, totalLength);
            sendApdu = HexUtils.concatenateByteArrays(new byte[]{-112, -62, 0, 0, (byte) apduChunk2.length}, apduChunk2);
        } else {
            sendApdu = command.getBytes();
        }
        ResponseApdu response2 = internalTransmit(sendApdu);
        if (response2.getStatusWord().getMsb() == 97) {
            if (response2.getData().length > 0) {
                byte[] data = response2.getData();
                byte[] additionalData = transmit(new GetResponseApduCommand((byte) 0, response2.getStatusWord().getLsb())).getBytes();
                byte[] fullResponse = new byte[data.length + additionalData.length];
                System.arraycopy(data, 0, fullResponse, 0, data.length);
                System.arraycopy(additionalData, 0, fullResponse, data.length, additionalData.length);
                return new ResponseApdu(fullResponse);
            }
            return transmit(new GetResponseApduCommand((byte) 0, response2.getStatusWord().getLsb()));
        }
        if (response2.getStatusWord().getMsb() == 108 && command.getCla() == 0) {
            command.setLe(response2.getStatusWord().getLsb());
            return transmit(command);
        }
        return response2;
    }
}
