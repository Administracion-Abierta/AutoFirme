package es.gob.afirma.core.misc.protocol;

import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/protocol/ProtocolVersion.class */
public enum ProtocolVersion {
    VERSION_0(0),
    VERSION_1(1),
    VERSION_2(2),
    VERSION_3(3),
    VERSION_4(4);

    private int version;

    ProtocolVersion(int i) {
        this.version = i;
    }

    public int getVersion() {
        return this.version;
    }

    public boolean support(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Integer) {
            return this.version >= ((Integer) obj).intValue();
        }
        if (obj instanceof ProtocolVersion) {
            return this.version >= ((ProtocolVersion) obj).getVersion();
        }
        try {
            return this.version >= Integer.parseInt(obj.toString());
        } catch (Exception e) {
            Logger.getLogger("es.gob.afirma").warning("Cadena de protocolo en formato deconocido ('" + obj + "'): " + e);
            return false;
        }
    }

    public boolean support(int i) {
        return this.version >= i;
    }

    @Override // java.lang.Enum
    public String toString() {
        return Integer.toString(this.version);
    }
}
