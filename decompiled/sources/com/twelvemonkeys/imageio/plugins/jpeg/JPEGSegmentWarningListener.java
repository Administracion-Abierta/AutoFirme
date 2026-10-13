package com.twelvemonkeys.imageio.plugins.jpeg;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/plugins/jpeg/JPEGSegmentWarningListener.class */
interface JPEGSegmentWarningListener {
    public static final JPEGSegmentWarningListener NULL_LISTENER = new JPEGSegmentWarningListener() { // from class: com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentWarningListener.1
        @Override // com.twelvemonkeys.imageio.plugins.jpeg.JPEGSegmentWarningListener
        public void warningOccurred(String str) {
        }
    };

    void warningOccurred(String str);
}
