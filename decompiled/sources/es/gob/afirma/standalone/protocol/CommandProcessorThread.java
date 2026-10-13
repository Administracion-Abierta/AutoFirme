package es.gob.afirma.standalone.protocol;

import es.gob.afirma.core.misc.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Timer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/CommandProcessorThread.class */
class CommandProcessorThread extends Thread {
    private static final int READ_BUFFER_SIZE = 2048;
    private static final int BUFFERED_SECURITY_RANGE = 36;
    private static final int MAX_READING_BUFFER_TRIES = 10;
    private static final String MEMORY_ERROR = "MEMORY_ERROR";
    private static final String LOCALHOST = "localhost";
    private static final String LOOP_DIR = "127.0.0.1";
    private static final String LOOP_DIR_2 = "0:0:0:0:0:0:0:1";
    private static final String RESET = "-";
    private static final String SEPARADOR = "@";
    private static final String EOF = "@EOF";
    private static final String IDSESSION = "idsession";
    private static final String MORE_DATA_NEED = "MORE_DATA_NEED";
    private static final String OK = "OK";
    private static final String SAVE_OK = "SAVE_OK";
    private static final String CANCEL = "CANCEL";
    private static final int RESPONSE_MAX_SIZE = 1000000;
    private static final String CMD = "cmd=";
    private static final String ECHO = "echo=";
    private static final String FRAGMENT = "fragment=";
    private static final String SEND = "send=";
    private static final String SIGN = "firm=";
    private static final String AFIRMA = "afirma://";
    private static final String AFIRMA2 = "afirma://service?";
    private static final String AFIRMA3 = "afirma://service/?";
    private static final String SAVE = "afirma://save?";
    private static final String SAVE2 = "afirma://save/?";
    private final Socket localSocket;
    private final Timer timer;
    private final String idSession;
    private final int protocolVersion;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final List<String> request = new ArrayList();
    private static final List<String> toSend = new ArrayList();
    private static int parts = 0;

    public CommandProcessorThread(Socket socket, Timer timer, String str, int i) {
        this.localSocket = socket;
        this.timer = timer;
        this.idSession = str;
        this.protocolVersion = i;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        LOGGER.info("Detectada conexion entrante");
        InetSocketAddress inetSocketAddress = (InetSocketAddress) this.localSocket.getRemoteSocketAddress();
        if (!isLocalAddress(inetSocketAddress)) {
            LOGGER.severe("Se ha detectado un acceso no autorizado desde " + inetSocketAddress.getHostString() + ". Se cerrara el socket por seguridad.");
            closeSocket(this.localSocket);
            return;
        }
        try {
            String str = read(this.localSocket.getInputStream());
            try {
                if (str == null) {
                    LOGGER.warning("Se ha recibido una peticion vacia");
                } else {
                    LOGGER.fine("Peticion HTTP recibida:\n" + str);
                    processCommand(str, this.localSocket);
                }
            } catch (IOException e) {
                LOGGER.severe("Error en el envio a traves del socket: " + e);
                sendError("SAF_11", this.localSocket, "Envio del resultado a la aplicacion");
            } catch (IllegalArgumentException e2) {
                LOGGER.severe("Los parametros recibidos a traves del socket no son validos: " + e2);
                sendError("SAF_03", this.localSocket, "Parametros incorrectos");
            } catch (Exception e3) {
                LOGGER.severe("Error al procesar el comando enviado: " + e3);
                sendError("SAF_09", this.localSocket, "Error al procesar el comando");
            } catch (OutOfMemoryError e4) {
                LOGGER.severe("Se ha producido un error por falta memoria de la maquina virtual: " + e4);
                sendMemoryError(this.localSocket);
            }
            closeSocket(this.localSocket);
        } catch (IllegalArgumentException e5) {
            LOGGER.severe("Se proporciono un ID de sesion erroneo. Se rechaza la conexion: " + e5);
            sendError("SAF_03", this.localSocket, "ID de sesion erroneo");
            closeSocket(this.localSocket);
        } catch (Exception e6) {
            LOGGER.severe("Error en la lectura de datos del socket: " + e6);
            sendError("SAF_03", this.localSocket, "No se pudieron leer los datos del socket");
            closeSocket(this.localSocket);
        }
    }

    private static boolean isLocalAddress(InetSocketAddress inetSocketAddress) {
        String hostString = inetSocketAddress.getHostString();
        if (LOOP_DIR_2.equals(hostString) || LOOP_DIR.equals(hostString) || LOCALHOST.equals(hostString)) {
            return true;
        }
        return false;
    }

    private void processCommand(String str, Socket socket) throws IOException, IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException("Los datos recibidos por HTTP son nulos");
        }
        String uriTypeFromRequest = getUriTypeFromRequest(str);
        LOGGER.info("Recibido comando de tipo: " + uriTypeFromRequest);
        try {
            switch (uriTypeFromRequest) {
                case "echo=":
                    doEchoPetition(str.substring(str.indexOf(ECHO) + ECHO.length()), socket);
                    break;
                case "cmd=":
                    doCmdPetition(str.substring(str.indexOf(CMD) + CMD.length()), socket);
                    break;
                case "fragment=":
                    doFragmentPetition(str.substring(str.indexOf(FRAGMENT) + FRAGMENT.length()), socket);
                    break;
                case "firm=":
                    doFragmentedProcess(socket);
                    break;
                case "send=":
                    doSendPetition(str.substring(str.indexOf(SEND) + SEND.length()), socket);
                    break;
                default:
                    throw new IllegalStateException("Comando no permitido: " + uriTypeFromRequest);
            }
        } catch (IOException e) {
            throw e;
        } catch (IllegalStateException e2) {
            throw new IllegalArgumentException("Error al procesar el comando de tipo '" + uriTypeFromRequest + "': " + e2, e2);
        } catch (Exception e3) {
            throw new IOException("Error durante la operacion por socket", e3);
        }
    }

    private static String getUriTypeFromRequest(String str) {
        String[] strArr = {CMD, ECHO, FRAGMENT, SIGN, SEND};
        String str2 = null;
        for (int i = 0; str2 == null && i < strArr.length; i++) {
            if (str.indexOf(strArr[i]) != -1) {
                str2 = strArr[i];
            }
        }
        if (str2 == null) {
            throw new IllegalArgumentException("Los datos recibidos por HTTP no contienen comando reconocido: " + str);
        }
        return str2;
    }

    private void doEchoPetition(String str, Socket socket) throws IOException {
        this.timer.stop();
        if (str.contains(RESET)) {
            reset();
        }
        LOGGER.info("Comando URI recibido por HTTP: echo=");
        sendData(createHttpResponse(true, OK), socket, ECHO);
    }

    private void doFragmentedProcess(Socket socket) throws IOException {
        boolean z = false;
        this.timer.stop();
        LOGGER.info("Comando URI recibido por HTTP (Fragmento)");
        if (toSend.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<String> it = request.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
            }
            if (sb.toString().startsWith(SAVE) || sb.toString().startsWith(SAVE2)) {
                z = true;
                String strLaunch = ProtocolInvocationLauncher.launch(sb.toString(), this.protocolVersion, true);
                if (strLaunch.equals(OK)) {
                    sendData(createHttpResponse(true, SAVE_OK), socket, "Operacion save realizada con exito");
                } else if (strLaunch.equals("CANCEL")) {
                    sendData(createHttpResponse(true, "CANCEL"), socket, "Cancelado por el usuario");
                } else {
                    throw new IllegalArgumentException("Error al realizar la operacion save");
                }
            } else {
                calculateNumberPartsResponse(ProtocolInvocationLauncher.launch(sb.toString(), this.protocolVersion, true));
            }
        } else {
            LOGGER.info("Se habia calculado el numero de partes anteriormente");
        }
        if (!z) {
            sendData(createHttpResponse(true, Integer.toString(parts)), socket, "Se mandaran " + parts + " partes");
        }
    }

    private void doCmdPetition(String str, Socket socket) throws IOException {
        String str2 = new String(Base64.decode(str.trim(), true));
        if (str2.startsWith(AFIRMA) && !str2.startsWith(AFIRMA2) && !str2.startsWith(AFIRMA3)) {
            this.timer.stop();
            LOGGER.info("Comando URI recibido por HTTP: " + str2);
            if (str2.startsWith(SAVE) || str2.startsWith(SAVE2)) {
                String strLaunch = ProtocolInvocationLauncher.launch(str2.toString(), this.protocolVersion, true);
                if (strLaunch.equals(OK)) {
                    sendData(createHttpResponse(true, SAVE_OK), socket, "Guardar datos");
                    return;
                } else {
                    if (strLaunch.equals("CANCEL")) {
                        sendData(createHttpResponse(true, "CANCEL"), socket, "Cancelado por el usuario");
                        return;
                    }
                    throw new IllegalArgumentException("Error al realizar la operacion 'save'");
                }
            }
            if (toSend.isEmpty()) {
                calculateNumberPartsResponse(ProtocolInvocationLauncher.launch(str2.toString(), this.protocolVersion, true));
            }
            sendData(createHttpResponse(true, Integer.toString(parts)), socket, "Se mandaran " + parts + " partes");
            return;
        }
        throw new IllegalArgumentException("Los datos recibidos en el parametro 'cmd' por HTTP no son una URI del tipo 'afirma://': " + str2);
    }

    private void doFragmentPetition(String str, Socket socket) throws IOException {
        this.timer.stop();
        LOGGER.info("Comando URI recibido por HTTP (fragmento)");
        String[] strArrSplit = str.split("@");
        int i = Integer.parseInt(strArrSplit[1]);
        int i2 = Integer.parseInt(strArrSplit[2]);
        String str2 = new String(Base64.decode(strArrSplit[3].trim(), true));
        if (request.size() == i) {
            LOGGER.info("sustituimos la parte " + i);
            request.set(i - 1, str2);
        } else {
            LOGGER.info("insertamos la parte " + i);
            request.add(i - 1, str2);
        }
        if (i == i2) {
            sendData(createHttpResponse(true, OK), socket, "Mandada la ultima parte " + i + "de " + i2);
        } else {
            sendData(createHttpResponse(true, MORE_DATA_NEED), socket, "Mandar resto de datos de la firma, parte " + i + "de " + i2);
        }
    }

    private void doSendPetition(String str, Socket socket) throws IOException {
        this.timer.stop();
        LOGGER.info("Comando URI recibido por HTTP: " + str);
        String[] strArrSplit = str.split("@");
        int i = Integer.parseInt(strArrSplit[1]);
        int i2 = Integer.parseInt(strArrSplit[2]);
        if (i < 1 || i > i2) {
            throw new IllegalArgumentException("Se ha solicitado enviar un fragmento invalido: " + i + "de " + i2);
        }
        sendData(createHttpResponse(true, toSend.get(i - 1)), socket, "Mandada la parte " + i + " de " + i2);
    }

    private static void calculateNumberPartsResponse(String str) {
        parts = (int) Math.ceil(((double) str.length()) / 1000000.0d);
        LOGGER.info("Se mandaran " + parts + "partes");
        LOGGER.info("tam total=" + str.length());
        for (int i = 0; i < parts; i++) {
            int i2 = RESPONSE_MAX_SIZE * i;
            toSend.add(str.substring(i2, Math.min(i2 + RESPONSE_MAX_SIZE, str.length())));
            LOGGER.info("Tamano de la parte " + (i + 1) + " =" + toSend.get(i).length());
        }
    }

    private static void reset() {
        request.clear();
        toSend.clear();
        parts = 0;
    }

    private void sendData(byte[] bArr, Socket socket, String str) throws IOException {
        socket.getOutputStream().write(bArr);
        socket.getOutputStream().flush();
        LOGGER.info("Mandando respuesta a la aplicacion: " + str);
        if (!this.timer.isRunning()) {
            this.timer.restart();
        }
    }

    private void sendError(String str, Socket socket, String str2) {
        try {
            sendData(createHttpResponse(true, ProtocolInvocationLauncherErrorManager.getErrorMessage(str)), this.localSocket, "ID de sesion erroneo");
        } catch (IOException e) {
            LOGGER.warning("No se ha podido informar a la aplicacion del error producido: " + e);
        }
    }

    private void sendMemoryError(Socket socket) {
        try {
            sendData(createHttpResponse(true, MEMORY_ERROR), this.localSocket, "Error de memoria");
        } catch (IOException e) {
            LOGGER.warning("No se ha podido informar a la aplicacion del error de memoria: " + e);
        }
    }

    private static byte[] createHttpResponse(boolean z, String str) {
        StringBuilder sb = new StringBuilder();
        if (z) {
            sb.append("HTTP/1.1 200 OK\n");
        } else {
            sb.append("HTTP/1.1 500 Internal Server Error");
        }
        sb.append("Connection: close\n");
        sb.append("Pragma: no-cache\n");
        sb.append("Server: Cliente @firma\n");
        sb.append("Content-Type: text/html; charset=utf-8\n");
        sb.append("Access-Control-Allow-Origin: *\n");
        sb.append('\n');
        if (str != null) {
            sb.append(Base64.encode(str.getBytes(StandardCharsets.UTF_8), true));
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String read(InputStream inputStream) throws IOException, IllegalArgumentException {
        String str;
        boolean z;
        int iIndexOf;
        int iIndexOf2;
        StringBuilder sb = new StringBuilder();
        String strSubstring = "";
        byte[] bArr = new byte[2048];
        boolean z2 = true;
        while (z2) {
            int i = 0;
            do {
                i++;
                str = new String(bArr, 0, inputStream.read(bArr), StandardCharsets.UTF_8);
                if (!str.trim().isEmpty()) {
                    break;
                }
            } while (i <= 10);
            if (i > 10) {
                return null;
            }
            strSubstring = strSubstring + str.substring(0, Math.min(36, str.length()));
            if (strSubstring.indexOf(EOF) != -1 || str.indexOf(EOF) != -1) {
                z2 = false;
                String strSubstring2 = null;
                if (strSubstring.indexOf(EOF) != -1) {
                    z = true;
                    iIndexOf = strSubstring.indexOf(EOF);
                    iIndexOf2 = strSubstring.indexOf(IDSESSION);
                    if (iIndexOf2 != -1) {
                        strSubstring2 = strSubstring.substring(iIndexOf2 + IDSESSION.length() + 1, iIndexOf);
                    }
                } else {
                    z = false;
                    iIndexOf = str.indexOf(EOF);
                    iIndexOf2 = str.indexOf(IDSESSION);
                    if (iIndexOf2 != -1) {
                        strSubstring2 = str.substring(iIndexOf2 + IDSESSION.length() + 1, iIndexOf);
                    }
                }
                checkIdSession(strSubstring2);
                if (z) {
                    if (iIndexOf2 != -1 && iIndexOf2 < 36) {
                        sb.replace(sb.length() - (36 - iIndexOf2), sb.length(), "");
                    } else if (iIndexOf < 36) {
                        sb.replace(sb.length() - (36 - iIndexOf), sb.length(), "");
                    } else if (iIndexOf2 > 0 || iIndexOf > 0) {
                        sb.append(strSubstring.substring(0, iIndexOf2 > -1 ? iIndexOf2 : iIndexOf));
                    }
                } else {
                    sb.append(str.substring(0, iIndexOf2 > -1 ? iIndexOf2 : iIndexOf));
                }
            } else {
                sb.append(str);
                strSubstring = str.substring(Math.max(0, str.length() - 36));
            }
        }
        return sb.toString();
    }

    private void checkIdSession(String str) throws IllegalArgumentException {
        if (this.idSession != null && !this.idSession.equals(str)) {
            throw new IllegalArgumentException("No se ha recibido el idSession esperado.");
        }
    }

    private static void closeSocket(Socket socket) {
        try {
            socket.close();
        } catch (Exception e) {
            LOGGER.warning("Error al cerrar el socket: " + e);
        }
    }
}
