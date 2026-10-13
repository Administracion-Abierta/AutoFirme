package org.java_websocket.extensions.permessage_deflate;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.java_websocket.enums.Opcode;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.extensions.CompressionExtension;
import org.java_websocket.extensions.ExtensionRequestData;
import org.java_websocket.extensions.IExtension;
import org.java_websocket.framing.ContinuousFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.FramedataImpl1;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/extensions/permessage_deflate/PerMessageDeflateExtension.class */
public class PerMessageDeflateExtension extends CompressionExtension {
    private static final String EXTENSION_REGISTERED_NAME = "permessage-deflate";
    private static final String SERVER_NO_CONTEXT_TAKEOVER = "server_no_context_takeover";
    private static final String CLIENT_NO_CONTEXT_TAKEOVER = "client_no_context_takeover";
    private static final String SERVER_MAX_WINDOW_BITS = "server_max_window_bits";
    private static final String CLIENT_MAX_WINDOW_BITS = "client_max_window_bits";
    private static final int serverMaxWindowBits = 32768;
    private static final int clientMaxWindowBits = 32768;
    private static final byte[] TAIL_BYTES = {0, 0, -1, -1};
    private static final int BUFFER_SIZE = 1024;
    private int threshold;
    private boolean serverNoContextTakeover;
    private boolean clientNoContextTakeover;
    private Map<String, String> requestedParameters;
    private final int compressionLevel;
    private final Inflater inflater;
    private final Deflater deflater;

    public PerMessageDeflateExtension() {
        this(-1);
    }

    public PerMessageDeflateExtension(int compressionLevel) {
        this.threshold = 1024;
        this.serverNoContextTakeover = true;
        this.clientNoContextTakeover = false;
        this.requestedParameters = new LinkedHashMap();
        this.compressionLevel = compressionLevel;
        this.deflater = new Deflater(this.compressionLevel, true);
        this.inflater = new Inflater(true);
    }

    public int getCompressionLevel() {
        return this.compressionLevel;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public boolean isServerNoContextTakeover() {
        return this.serverNoContextTakeover;
    }

    public void setServerNoContextTakeover(boolean serverNoContextTakeover) {
        this.serverNoContextTakeover = serverNoContextTakeover;
    }

    public boolean isClientNoContextTakeover() {
        return this.clientNoContextTakeover;
    }

    public void setClientNoContextTakeover(boolean clientNoContextTakeover) {
        this.clientNoContextTakeover = clientNoContextTakeover;
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public void decodeFrame(Framedata inputFrame) throws InvalidDataException {
        if (!(inputFrame instanceof DataFrame)) {
            return;
        }
        if (!inputFrame.isRSV1() && inputFrame.getOpcode() != Opcode.CONTINUOUS) {
            return;
        }
        if (inputFrame.getOpcode() == Opcode.CONTINUOUS && inputFrame.isRSV1()) {
            throw new InvalidDataException(1008, "RSV1 bit can only be set for the first frame.");
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            decompress(inputFrame.getPayloadData().array(), output);
            if (this.inflater.getRemaining() > 0) {
                this.inflater.reset();
                decompress(inputFrame.getPayloadData().array(), output);
            }
            if (inputFrame.isFin()) {
                decompress(TAIL_BYTES, output);
                if (this.clientNoContextTakeover) {
                    this.inflater.reset();
                }
            }
            ((FramedataImpl1) inputFrame).setPayload(ByteBuffer.wrap(output.toByteArray(), 0, output.size()));
        } catch (DataFormatException e) {
            throw new InvalidDataException(1008, e.getMessage());
        }
    }

    private void decompress(byte[] data, ByteArrayOutputStream outputBuffer) throws DataFormatException {
        this.inflater.setInput(data);
        byte[] buffer = new byte[1024];
        while (true) {
            int bytesInflated = this.inflater.inflate(buffer);
            if (bytesInflated > 0) {
                outputBuffer.write(buffer, 0, bytesInflated);
            } else {
                return;
            }
        }
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public void encodeFrame(Framedata inputFrame) {
        if (!(inputFrame instanceof DataFrame)) {
            return;
        }
        byte[] payloadData = inputFrame.getPayloadData().array();
        if (payloadData.length < this.threshold) {
            return;
        }
        if (!(inputFrame instanceof ContinuousFrame)) {
            ((DataFrame) inputFrame).setRSV1(true);
        }
        this.deflater.setInput(payloadData);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        while (true) {
            int bytesCompressed = this.deflater.deflate(buffer, 0, buffer.length, 2);
            if (bytesCompressed <= 0) {
                break;
            } else {
                output.write(buffer, 0, bytesCompressed);
            }
        }
        byte[] outputBytes = output.toByteArray();
        int outputLength = outputBytes.length;
        if (inputFrame.isFin()) {
            if (endsWithTail(outputBytes)) {
                outputLength -= TAIL_BYTES.length;
            }
            if (this.serverNoContextTakeover) {
                this.deflater.reset();
            }
        }
        ((FramedataImpl1) inputFrame).setPayload(ByteBuffer.wrap(outputBytes, 0, outputLength));
    }

    private static boolean endsWithTail(byte[] data) {
        if (data.length < 4) {
            return false;
        }
        int length = data.length;
        for (int i = 0; i < TAIL_BYTES.length; i++) {
            if (TAIL_BYTES[i] != data[(length - TAIL_BYTES.length) + i]) {
                return false;
            }
        }
        return true;
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public boolean acceptProvidedExtensionAsServer(String inputExtension) {
        String[] requestedExtensions = inputExtension.split(",");
        for (String extension : requestedExtensions) {
            ExtensionRequestData extensionData = ExtensionRequestData.parseExtensionRequest(extension);
            if (EXTENSION_REGISTERED_NAME.equalsIgnoreCase(extensionData.getExtensionName())) {
                Map<String, String> headers = extensionData.getExtensionParameters();
                this.requestedParameters.putAll(headers);
                if (this.requestedParameters.containsKey(CLIENT_NO_CONTEXT_TAKEOVER)) {
                    this.clientNoContextTakeover = true;
                    return true;
                }
                return true;
            }
        }
        return false;
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public boolean acceptProvidedExtensionAsClient(String inputExtension) {
        String[] requestedExtensions = inputExtension.split(",");
        for (String extension : requestedExtensions) {
            ExtensionRequestData extensionData = ExtensionRequestData.parseExtensionRequest(extension);
            if (EXTENSION_REGISTERED_NAME.equalsIgnoreCase(extensionData.getExtensionName())) {
                extensionData.getExtensionParameters();
                return true;
            }
        }
        return false;
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public String getProvidedExtensionAsClient() {
        this.requestedParameters.put(CLIENT_NO_CONTEXT_TAKEOVER, "");
        this.requestedParameters.put(SERVER_NO_CONTEXT_TAKEOVER, "");
        return "permessage-deflate; server_no_context_takeover; client_no_context_takeover";
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public String getProvidedExtensionAsServer() {
        return "permessage-deflate; server_no_context_takeover" + (this.clientNoContextTakeover ? "; client_no_context_takeover" : "");
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public IExtension copyInstance() {
        PerMessageDeflateExtension clone = new PerMessageDeflateExtension(getCompressionLevel());
        clone.setThreshold(getThreshold());
        clone.setClientNoContextTakeover(isClientNoContextTakeover());
        clone.setServerNoContextTakeover(isServerNoContextTakeover());
        return clone;
    }

    @Override // org.java_websocket.extensions.CompressionExtension, org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public void isFrameValid(Framedata inputFrame) throws InvalidDataException {
        if ((inputFrame instanceof ContinuousFrame) && (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3())) {
            throw new InvalidFrameException("bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3());
        }
        super.isFrameValid(inputFrame);
    }

    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public String toString() {
        return "PerMessageDeflateExtension";
    }
}
