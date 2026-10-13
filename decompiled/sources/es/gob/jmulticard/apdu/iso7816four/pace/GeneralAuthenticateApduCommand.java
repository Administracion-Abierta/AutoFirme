package es.gob.jmulticard.apdu.iso7816four.pace;

import es.gob.jmulticard.apdu.CommandApdu;
import es.gob.jmulticard.asn1.Tlv;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/GeneralAuthenticateApduCommand.class */
public final class GeneralAuthenticateApduCommand extends CommandApdu {
    private static final byte INS_GENERAL_AUTHENTICATE = -122;
    private static final byte NO_INFORMATION_GIVEN = 0;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/GeneralAuthenticateApduCommand$GeneralAuthenticateData.class */
    public static abstract class GeneralAuthenticateData {
        protected static final byte TAG_DYNAMIC_AUTHENTICATION_DATA = 124;

        public abstract byte[] getBytes();
    }

    public GeneralAuthenticateApduCommand(byte cla, GeneralAuthenticateData data) {
        super(cla, (byte) -122, (byte) 0, (byte) 0, data.getBytes(), null);
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/GeneralAuthenticateApduCommand$DataEncryptedNonce.class */
    public static final class DataEncryptedNonce extends GeneralAuthenticateData {
        @Override // es.gob.jmulticard.apdu.iso7816four.pace.GeneralAuthenticateApduCommand.GeneralAuthenticateData
        public byte[] getBytes() {
            return new byte[]{124, 0};
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/GeneralAuthenticateApduCommand$DataMapNonce.class */
    public static final class DataMapNonce extends GeneralAuthenticateData {
        public static final byte TAG_GEN_AUTH_2 = -127;
        private final byte[] publicKeyIfdDh1;

        public DataMapNonce(byte[] puKIfdDh1UncompressedBytes) {
            this.publicKeyIfdDh1 = puKIfdDh1UncompressedBytes;
        }

        @Override // es.gob.jmulticard.apdu.iso7816four.pace.GeneralAuthenticateApduCommand.GeneralAuthenticateData
        public byte[] getBytes() {
            return new Tlv((byte) 124, new Tlv((byte) -127, this.publicKeyIfdDh1).getBytes()).getBytes();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/GeneralAuthenticateApduCommand$DataPerformKeyAgreement.class */
    public static final class DataPerformKeyAgreement extends GeneralAuthenticateData {
        public static final byte TAG_GEN_AUTH_3 = -125;
        private final byte[] publicKeyIfdDh2;

        public DataPerformKeyAgreement(byte[] puKIfdDh2UncompressedBytes) {
            this.publicKeyIfdDh2 = puKIfdDh2UncompressedBytes;
        }

        @Override // es.gob.jmulticard.apdu.iso7816four.pace.GeneralAuthenticateApduCommand.GeneralAuthenticateData
        public byte[] getBytes() {
            return new Tlv((byte) 124, new Tlv((byte) -125, this.publicKeyIfdDh2).getBytes()).getBytes();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/pace/GeneralAuthenticateApduCommand$DataMutualAuthentication.class */
    public static final class DataMutualAuthentication extends GeneralAuthenticateData {
        protected static final byte TAG_GEN_AUTH_4 = -123;
        private final byte[] messageAuthenticationCode;

        public DataMutualAuthentication(byte[] mac) {
            this.messageAuthenticationCode = mac;
        }

        @Override // es.gob.jmulticard.apdu.iso7816four.pace.GeneralAuthenticateApduCommand.GeneralAuthenticateData
        public byte[] getBytes() {
            return new Tlv((byte) 124, new Tlv((byte) -123, this.messageAuthenticationCode).getBytes()).getBytes();
        }
    }
}
