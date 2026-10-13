package es.gob.afirma.core.misc.http;

import es.gob.afirma.core.misc.LoggerUtil;
import java.io.IOException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/http/HttpError.class */
public final class HttpError extends IOException {
    private static final long serialVersionUID = 8997766820804553378L;
    private final int responseCode;
    private final String responseDescription;
    private final byte[] errorStreamBytes;

    HttpError(int i) {
        super("Error en conexion HTTP con codigo de respuesta " + i);
        this.responseCode = i;
        this.responseDescription = null;
        this.errorStreamBytes = null;
    }

    public HttpError(int i, String str, String str2) {
        this(i, str, null, str2);
    }

    public HttpError(int i, String str, byte[] bArr, String str2) {
        super("Error HTTP con codigo  " + i + ((bArr == null || bArr.length <= 0) ? "" : ", cuerpo '" + LoggerUtil.getTrimBytes(bArr) + "'") + " y descripcion '" + str + "' para la direccion: " + LoggerUtil.getTrimStr(str2));
        this.responseCode = i;
        this.responseDescription = str;
        this.errorStreamBytes = bArr != null ? (byte[]) bArr.clone() : null;
    }

    public byte[] getErrorStreamBytes() {
        if (this.errorStreamBytes != null) {
            return (byte[]) this.errorStreamBytes.clone();
        }
        return null;
    }

    public int getResponseCode() {
        return this.responseCode;
    }

    public String getResponseDescription() {
        return this.responseDescription;
    }
}
