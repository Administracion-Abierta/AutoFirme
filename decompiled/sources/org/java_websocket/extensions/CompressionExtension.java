package org.java_websocket.extensions;

import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.exceptions.InvalidFrameException;
import org.java_websocket.framing.ControlFrame;
import org.java_websocket.framing.DataFrame;
import org.java_websocket.framing.Framedata;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/extensions/CompressionExtension.class */
public abstract class CompressionExtension extends DefaultExtension {
    @Override // org.java_websocket.extensions.DefaultExtension, org.java_websocket.extensions.IExtension
    public void isFrameValid(Framedata inputFrame) throws InvalidDataException {
        if ((inputFrame instanceof DataFrame) && (inputFrame.isRSV2() || inputFrame.isRSV3())) {
            throw new InvalidFrameException("bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3());
        }
        if (inputFrame instanceof ControlFrame) {
            if (inputFrame.isRSV1() || inputFrame.isRSV2() || inputFrame.isRSV3()) {
                throw new InvalidFrameException("bad rsv RSV1: " + inputFrame.isRSV1() + " RSV2: " + inputFrame.isRSV2() + " RSV3: " + inputFrame.isRSV3());
            }
        }
    }
}
