package es.gob.jmulticard.apdu.iso7816four;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/MseSetAuthenticationTemplateApduCommand.class */
public abstract class MseSetAuthenticationTemplateApduCommand extends MseSetApduCommand {
    protected MseSetAuthenticationTemplateApduCommand(byte cla, byte[] data) {
        super(cla, (byte) -63, (byte) -92, data);
    }
}
