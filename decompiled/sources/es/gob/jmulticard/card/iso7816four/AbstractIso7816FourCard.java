package es.gob.jmulticard.card.iso7816four;

import es.gob.jmulticard.JmcLogger;
import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.apdu.ResponseApdu;
import es.gob.jmulticard.apdu.StatusWord;
import es.gob.jmulticard.apdu.iso7816four.GetChallengeApduCommand;
import es.gob.jmulticard.apdu.iso7816four.ReadBinaryApduCommand;
import es.gob.jmulticard.apdu.iso7816four.ReadRecordApduCommand;
import es.gob.jmulticard.apdu.iso7816four.SelectDfByNameApduCommand;
import es.gob.jmulticard.apdu.iso7816four.SelectFileApduResponse;
import es.gob.jmulticard.apdu.iso7816four.SelectFileByIdApduCommand;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.card.AbstractSmartCard;
import es.gob.jmulticard.card.Location;
import es.gob.jmulticard.card.PinException;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.connection.ApduConnectionException;
import es.gob.jmulticard.connection.cwa14890.SecureChannelException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/iso7816four/AbstractIso7816FourCard.class */
public abstract class AbstractIso7816FourCard extends AbstractSmartCard {
    private static final StatusWord SW_UNSATISFIED_SECURITY_STATE = new StatusWord((byte) 105, (byte) -126);
    private static final StatusWord SW_FILE_NOT_FOUND = new StatusWord((byte) 106, (byte) -126);
    private static final StatusWord SW_EOF_REACHED = new StatusWord((byte) 98, (byte) -126);
    private static final StatusWord SW_OFFSET_OUTSIDE_EF = new StatusWord((byte) 107, (byte) 0);
    private static final int MAX_READ_CHUNK = 222;

    protected abstract void selectMasterFile() throws Iso7816FourCardException, ApduConnectionException;

    public abstract void verifyPin(PasswordCallback passwordCallback) throws PinException, ApduConnectionException;

    protected AbstractIso7816FourCard(byte c, ApduConnection conn) {
        super(c, conn);
    }

    private ResponseApdu readBinary(byte msbOffset, byte lsbOffset, byte readLength) throws OffsetOutsideEfException, RequiredSecurityStateNotSatisfiedException, ApduConnectionException {
        CommandApdu apdu = new ReadBinaryApduCommand(getCla(), msbOffset, lsbOffset, readLength);
        ResponseApdu res = getConnection().transmit(apdu);
        if (res.isOk()) {
            return res;
        }
        if (SW_OFFSET_OUTSIDE_EF.equals(res.getStatusWord())) {
            throw new OffsetOutsideEfException(SW_OFFSET_OUTSIDE_EF, apdu);
        }
        if (SW_UNSATISFIED_SECURITY_STATE.equals(res.getStatusWord())) {
            throw new RequiredSecurityStateNotSatisfiedException(res.getStatusWord());
        }
        if (SW_EOF_REACHED.equals(res.getStatusWord())) {
            JmcLogger.warning("Se ha alcanzado el final de fichero antes de poder leer los octetos indicados");
            return res;
        }
        throw new ApduConnectionException("Respuesta invalida en la lectura de binario con el codigo: " + res.getStatusWord());
    }

    public List<byte[]> readAllRecords() throws Iso7816FourCardException, ApduConnectionException {
        StatusWord readedResponseSw;
        List<byte[]> ret = new ArrayList<>();
        CommandApdu readRecordApduCommand = new ReadRecordApduCommand(getCla());
        do {
            ResponseApdu readedResponse = sendArbitraryApdu(readRecordApduCommand);
            readedResponseSw = readedResponse.getStatusWord();
            if (!readedResponse.isOk() && !ReadRecordApduCommand.RECORD_NOT_FOUND.equals(readedResponseSw)) {
                throw new Iso7816FourCardException("Error en la lectura de registro", readedResponseSw);
            }
            ret.add(readedResponse.getData());
        } while (!ReadRecordApduCommand.RECORD_NOT_FOUND.equals(readedResponseSw));
        return ret;
    }

    public byte[] readBinaryComplete(int len) throws IOException {
        ResponseApdu readedResponse;
        int off = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while (off < len) {
            byte msbOffset = (byte) (off >> 8);
            byte lsbOffset = (byte) (off & 255);
            int left = len - off;
            if (left < 222) {
                try {
                    readedResponse = readBinary(msbOffset, lsbOffset, (byte) left);
                } catch (OffsetOutsideEfException e) {
                    JmcLogger.warning("Se ha intentado una lectura fuera de los limites del fichero, se devolvera lo leido hasta ahora: " + e);
                    return out.toByteArray();
                } catch (RequiredSecurityStateNotSatisfiedException e2) {
                    throw new IOException("Condicion de seguridad no satisfecha", e2);
                }
            } else {
                readedResponse = readBinary(msbOffset, lsbOffset, (byte) -34);
            }
            boolean eofReached = SW_EOF_REACHED.equals(readedResponse.getStatusWord());
            if (!readedResponse.isOk() && !eofReached) {
                throw new IOException("Error leyendo el binario (" + readedResponse.getStatusWord() + ")");
            }
            out.write(readedResponse.getData());
            off += 222;
            if (eofReached) {
                break;
            }
        }
        return out.toByteArray();
    }

    public int selectFileByName(String name) throws Iso7816FourCardException, ApduConnectionException {
        return selectFileByName(name.getBytes());
    }

    public int selectFileByName(byte[] name) throws Iso7816FourCardException, ApduConnectionException {
        CommandApdu selectCommand = new SelectDfByNameApduCommand(getCla(), name);
        ResponseApdu response = sendArbitraryApdu(selectCommand);
        if (response.isOk()) {
            return new SelectFileApduResponse(response).getFileLength();
        }
        StatusWord sw = response.getStatusWord();
        if (SW_FILE_NOT_FOUND.equals(sw)) {
            throw new FileNotFoundException(name);
        }
        throw new Iso7816FourCardException(sw, selectCommand);
    }

    public int selectFileById(byte[] id) throws Iso7816FourCardException, ApduConnectionException {
        CommandApdu selectCommand = new SelectFileByIdApduCommand(getCla(), id);
        ResponseApdu res = getConnection().transmit(selectCommand);
        if (SW_FILE_NOT_FOUND.equals(res.getStatusWord())) {
            throw new FileNotFoundException(id);
        }
        SelectFileApduResponse response = new SelectFileApduResponse(res);
        if (response.isOk()) {
            return response.getFileLength();
        }
        StatusWord sw = response.getStatusWord();
        if (SW_FILE_NOT_FOUND.equals(sw)) {
            throw new FileNotFoundException(id);
        }
        if (SW_UNSATISFIED_SECURITY_STATE.equals(sw)) {
            throw new RequiredSecurityStateNotSatisfiedException(response.getStatusWord());
        }
        throw new Iso7816FourCardException(sw, selectCommand);
    }

    public byte[] selectFileByIdAndRead(byte[] id) throws Iso7816FourCardException, IOException {
        int fileLength = selectFileById(id);
        return readBinaryComplete(fileLength);
    }

    private int selectFileByLocation(Location location) throws Iso7816FourCardException, ApduConnectionException {
        int fileLength = 0;
        selectMasterFile();
        for (Location loc = location; loc != null; loc = loc.getChild()) {
            byte[] id = loc.getFile();
            fileLength = selectFileById(id);
        }
        return fileLength;
    }

    public byte[] selectFileByLocationAndRead(Location location) throws Iso7816FourCardException, IOException {
        int fileLenght = selectFileByLocation(location);
        return readBinaryComplete(fileLenght);
    }

    public void setPublicKeyToVerification(byte[] refPublicKey) throws ApduConnectionException {
        ResponseApdu res = sendArbitraryApdu(new CommandApdu((byte) 0, (byte) 34, (byte) -127, (byte) -74, new Tlv((byte) -125, refPublicKey).getBytes(), null));
        if (!res.isOk()) {
            throw new SecureChannelException("Error estableciendo la clave publica para verificacion, con respuesta: " + res.getStatusWord());
        }
    }

    public byte[] getChallenge() throws ApduConnectionException {
        ResponseApdu res = getConnection().transmit(new GetChallengeApduCommand((byte) 0));
        if (res.isOk()) {
            return res.getData();
        }
        throw new ApduConnectionException("Respuesta invalida en la obtencion de desafio con el codigo: " + res.getStatusWord());
    }
}
