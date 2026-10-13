package org.apache.pdfbox.rendering;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/ImageType.class */
public enum ImageType {
    BINARY { // from class: org.apache.pdfbox.rendering.ImageType.1
        @Override // org.apache.pdfbox.rendering.ImageType
        int toBufferedImageType() {
            return 12;
        }
    },
    GRAY { // from class: org.apache.pdfbox.rendering.ImageType.2
        @Override // org.apache.pdfbox.rendering.ImageType
        int toBufferedImageType() {
            return 10;
        }
    },
    RGB { // from class: org.apache.pdfbox.rendering.ImageType.3
        @Override // org.apache.pdfbox.rendering.ImageType
        int toBufferedImageType() {
            return 1;
        }
    },
    ARGB { // from class: org.apache.pdfbox.rendering.ImageType.4
        @Override // org.apache.pdfbox.rendering.ImageType
        int toBufferedImageType() {
            return 2;
        }
    },
    BGR { // from class: org.apache.pdfbox.rendering.ImageType.5
        @Override // org.apache.pdfbox.rendering.ImageType
        int toBufferedImageType() {
            return 5;
        }
    };

    abstract int toBufferedImageType();
}
